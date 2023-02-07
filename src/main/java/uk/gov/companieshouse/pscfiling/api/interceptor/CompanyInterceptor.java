package uk.gov.companieshouse.pscfiling.api.interceptor;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${super.secure.message}")
    public String companyHasSuperSecurePscsMessage;
    private static final List<String> ALLOWED_COMPANY_TYPES =
            List.of("private-unlimited", "ltd", "plc", "old-public-company",
                    "private-limited-guarant-nsc-limited-exemption", "private-limited-guarant-nsc",
                    "private-unlimited-nsc", "private-limited-shares-section-30-exemption");
    @Value("${company.type.not.allowed.message}")
    public String companyTypeNotAlllowedMesssage;
    private static final List<String> COMPANY_STATUS_NOT_ALLOWED = List.of("dissolved", "converted-closed");
    @Value("${company.status.not.allowed.message}")
    public String companyStatusNotAllowedMessage;

    CompanyProfileService companyProfileService;
    private final Logger logger;

    public CompanyInterceptor(CompanyProfileService companyProfileService, Logger logger) {
        this.companyProfileService = companyProfileService;
        this.logger = logger;
    }

    public void setCompanyHasSuperSecurePscsMessage(String companyHasSuperSecurePscsMessage) {
        this.companyHasSuperSecurePscsMessage = companyHasSuperSecurePscsMessage;
    }

    public void setCompanyTypeNotAlllowedMesssage(String companyTypeNotAlllowedMesssage) {
        this.companyTypeNotAlllowedMesssage = companyTypeNotAlllowedMesssage;
    }

    public void setCompanyStatusNotAllowedMessage(String companyStatusNotAllowedMessage) {
        this.companyStatusNotAllowedMessage = companyStatusNotAllowedMessage;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());
        final var passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        final var logMap = LogHelper.createLogMap(transaction.getId());
        logger.info("Company Interceptor", logMap);

        CompanyProfileApi companyProfile = companyProfileService.getCompanyProfile(transaction, passthroughHeader);

        if (companyProfile != null) {
            if (companyProfile.hasSuperSecurePscs()) {
                logger.info("Company has Super Secure PSCs");
                sendValidationError(companyHasSuperSecurePscsMessage);
            }

            if (!ALLOWED_COMPANY_TYPES.contains(companyProfile.getType())) {
                logMap.put("company_number", transaction.getCompanyNumber());
                logMap.put("company_type", companyProfile.getType());
                logger.info("Company Type not allowed", logMap);
                sendValidationError(companyTypeNotAlllowedMesssage + companyProfile.getType());
            }

            if (COMPANY_STATUS_NOT_ALLOWED.contains(companyProfile.getCompanyStatus())) {
                logMap.put("company_number", transaction.getCompanyNumber());
                logMap.put("company_status", companyProfile.getCompanyStatus());
                logger.info("Company status not allowed", logMap);
                sendValidationError(companyStatusNotAllowedMessage + companyProfile.getCompanyStatus());
            }
        }
        return true;
    }

    private static void sendValidationError(String errorMessage) {
        final var error = new FieldError("object", "reference_psc_id",
                null, false, new String[]{null, "reference_psc_id"},
                null, errorMessage);
        List<FieldError> errors = List.of(error);
        throw new ConflictingFilingException(errors);
    }

}
