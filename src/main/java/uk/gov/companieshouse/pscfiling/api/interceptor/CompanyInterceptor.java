package uk.gov.companieshouse.pscfiling.api.interceptor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.AttributeName;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.ConflictingFilingException;
import uk.gov.companieshouse.pscfiling.api.service.CompanyProfileService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Component
public class CompanyInterceptor implements HandlerInterceptor {

    private final Map<String, String> validation;
    private final Map<String, List<String>> company;
    private final Map<String, String> companyStatus;
    private final CompanyProfileService companyProfileService;
    private final Logger logger;

    public CompanyInterceptor(CompanyProfileService companyProfileService,
            @Qualifier(value = "validation") Map<String, String> validation,
            @Qualifier(value = "company") Map<String, List<String>> company,
            @Qualifier(value = "companyStatus") Map<String, String> companyStatus,
            Logger logger) {
        this.companyProfileService = companyProfileService;
        this.validation = validation;
        this.company = company;
        this.companyStatus = companyStatus;
        this.logger = logger;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        var transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());
        Objects.requireNonNull(transaction, "Transaction missing from request");
        final var passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        final var logMap = LogHelper.createLogMap(transaction.getId());
        logger.info("Company Interceptor", logMap);

        CompanyProfileApi companyProfile = companyProfileService.getCompanyProfile(transaction, passthroughHeader);

        if (companyProfile != null) {
            if (companyProfile.hasSuperSecurePscs()) {
                logger.info("Company has Super Secure PSCs");
                throw new ConflictingFilingException(createValidationError(validation.get("super-secure-company")));
            }

            if (!company.get("type-allowed").contains(companyProfile.getType())) {
                logMap.put("company_number", transaction.getCompanyNumber());
                logMap.put("company_type", companyProfile.getType());
                logger.info("Company Type not allowed", logMap);
                throw new ConflictingFilingException(createValidationError(
                        validation.get("company-type-not-allowed")));
            }

            if (company.get("status-not-allowed").contains(companyProfile.getCompanyStatus())) {
                logMap.put("company_number", transaction.getCompanyNumber());
                logMap.put("company_status", companyProfile.getCompanyStatus());
                logger.info("Company status not allowed", logMap);
                throw new ConflictingFilingException(createValidationError(
                        validation.get("company-status-not-allowed") +
                                companyStatus.get(companyProfile.getCompanyStatus())));
            }
        }
        return true;
    }

    private List<FieldError> createValidationError(String errorMessage) {
        final var error = new FieldError("ignored", "ignored", errorMessage);
        return List.of(error);
    }

}
