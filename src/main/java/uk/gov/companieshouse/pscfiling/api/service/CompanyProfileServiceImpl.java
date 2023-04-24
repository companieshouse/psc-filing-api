package uk.gov.companieshouse.pscfiling.api.service;

import java.io.IOException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.sdk.ApiClientService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.CompanyProfileServiceException;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

/**
 * The company profile service layer.
 */
@Service
public class CompanyProfileServiceImpl implements CompanyProfileService {

    private final ApiClientService apiClientService;
    private final Logger logger;

    public CompanyProfileServiceImpl(ApiClientService apiClientService, Logger logger) {
        this.apiClientService = apiClientService;
        this.logger = logger;
    }

    @Override
    public CompanyProfileApi getCompanyProfile(final Transaction transaction, final String ericPassThroughHeader)
            throws CompanyProfileServiceException {
        final var logMap = LogHelper.createLogMap(transaction.getId());

        try {
            final String uri = "/company/" + transaction.getCompanyNumber();
            final CompanyProfileApi companyProfile = apiClientService.getApiClient(ericPassThroughHeader)
                            .company()
                            .get(uri)
                            .execute()
                            .getData();
            logMap.put("company_number", transaction.getCompanyNumber());
            logMap.put("company_name", transaction.getCompanyName());
            logger.debugContext(transaction.getId(), "Retrieved company profile details", logMap);
            return companyProfile;
        }
        catch (final URIValidationException | IOException e) {
            throw new CompanyProfileServiceException("Error Retrieving company profile " + transaction.getCompanyNumber(), e);
        }
    }
}
