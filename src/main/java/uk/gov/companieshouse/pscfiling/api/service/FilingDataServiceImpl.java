package uk.gov.companieshouse.pscfiling.api.service;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.pscfiling.api.utils.MapHelper;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

/**
 * Produces Filing Data format for consumption as JSON by filing-resource-handler external service.
 */
@Service
public class FilingDataServiceImpl implements FilingDataService {

    private final PscFilingService pscFilingService;
    private final PscIndividualMapper pscIndividualMapper;
    private final PscDetailsService pscDetailsService;
    private final Logger logger;

    public FilingDataServiceImpl(PscFilingService pscFilingService,
                                 PscIndividualMapper filingMapper,
                                 PscDetailsService pscDetailsService,
                                 Logger logger) {
        this.pscFilingService = pscFilingService;
        this.pscIndividualMapper = filingMapper;
        this.pscDetailsService = pscDetailsService;
        this.logger = logger;
    }

    @Override
    public FilingApi generatePscFiling(String filingId, HttpServletRequest request, Transaction transaction, String passthroughHeader) {
        var filing = new FilingApi();
        filing.setKind("psc-filing#termination"); // TODO: handling other kinds to come later

        setFilingApiData(filing, filingId, request, transaction, passthroughHeader);
        return filing;
    }

    private void setFilingApiData(FilingApi filing, String filingId, HttpServletRequest request,
                                  Transaction transaction, String passthroughHeader) {

        var transactionId = transaction.getId();
        var pscFilingOpt = pscFilingService.get(filingId, transactionId);
        var pscFiling = pscFilingOpt.orElseThrow(() -> new FilingResourceNotFoundException(
                String.format("Psc individual not found when generating filing for %s", filingId)));
        // TODO this is dummy data until we get the details from company-appointments API

        final var pscDetails =
            pscDetailsService.getPscDetails(transaction, pscFiling.getReferencePscId(), PscTypeConstants.INDIVIDUAL,
                passthroughHeader);
        var nameElements = NameElements.builder().forename(pscDetails.getNameElements().getForename())
            .surname(pscDetails.getNameElements().getSurname()).build();
        var enhancedPscFiling = PscIndividualFiling.builder(pscFiling)
                .dateOfBirth(new Date3Tuple(20, 10, 2000))
            .nameElements(nameElements)
                .build();
        var filingData = pscIndividualMapper.map(enhancedPscFiling);
        var dataMap = MapHelper.convertObject(filingData);

        final var logMap = LogHelper.createLogMap(transactionId, filingId);

        logMap.put("Data to submit", dataMap);
        logger.debugContext(transactionId, filingId, logMap);

        filing.setData(dataMap);
    }

}
