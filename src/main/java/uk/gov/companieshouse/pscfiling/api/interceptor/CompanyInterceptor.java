package uk.gov.companieshouse.pscfiling.api.interceptor;

import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    private List<String> allowedCompanyTypes;
    private List<String> companyStatusNotAllowed;
    private String companyHasSuperSecurePscsMessage;
    private String companyTypeNotAlllowedMesssage;
    private String companyStatusNotAllowedMessage;
    CompanyProfileService companyProfileService;
    private final Logger logger;

    public CompanyInterceptor(CompanyProfileService companyProfileService, Logger logger) {
        this.companyProfileService = companyProfileService;
        this.logger = logger;
    }

    @Autowired
    public void setCompanyHasSuperSecurePscsMessage(@Value("${super.secure.message:not-defined}") String companyHasSuperSecurePscsMessage) {
        this.companyHasSuperSecurePscsMessage = companyHasSuperSecurePscsMessage;
    }
    @Autowired
    public void setCompanyTypeNotAlllowedMesssage(@Value("${company.type.not.allowed.message}") String companyTypeNotAlllowedMesssage) {
        this.companyTypeNotAlllowedMesssage = companyTypeNotAlllowedMesssage;
    }
    @Autowired
    public void setCompanyStatusNotAllowedMessage(@Value("${company.status.not.allowed.message}") String companyStatusNotAllowedMessage) {
        this.companyStatusNotAllowedMessage = companyStatusNotAllowedMessage;
    }
    @Autowired
    public void setAllowedCompanyTypes(@Value("#{${allowed.company.types}}") List<String> allowedCompanyTypes) {
        this.allowedCompanyTypes = allowedCompanyTypes;
    }
    @Autowired
    public void setCompanyStatusNotAllowed(@Value("#{${company.status.not.allowed}}") List<String> companyStatusNotAllowed) {
        this.companyStatusNotAllowed = companyStatusNotAllowed;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());
        Objects.requireNonNull(transaction, "Transaction missing from request");
        final var passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        final var logMap = LogHelper.createLogMap(transaction.getId());
        logger.info("Company Interceptor", logMap);

        CompanyProfileApi companyProfile = companyProfileService.getCompanyProfile(transaction, passthroughHeader);

        if (companyProfile != null) {
            if (companyProfile.hasSuperSecurePscs()) {
                logger.info("Company has Super Secure PSCs");
                sendValidationError(companyHasSuperSecurePscsMessage);
            }

            if (!allowedCompanyTypes.contains(companyProfile.getType())) {
                logMap.put("company_number", transaction.getCompanyNumber());
                logMap.put("company_type", companyProfile.getType());
                logger.info("Company Type not allowed", logMap);
                sendValidationError(companyTypeNotAlllowedMesssage + companyProfile.getType());
            }

            if (companyStatusNotAllowed.contains(companyProfile.getCompanyStatus())) {
                logMap.put("company_number", transaction.getCompanyNumber());
                logMap.put("company_status", companyProfile.getCompanyStatus());
                logger.info("Company status not allowed", logMap);
                sendValidationError(companyStatusNotAllowedMessage + companyProfile.getCompanyStatus());
            }
        }
        return true;
    }

    private void sendValidationError(String errorMessage) {
        final var error = new FieldError("object", "reference_psc_id",
                null, false, new String[]{null, "reference_psc_id"},
                null, errorMessage);
        List<FieldError> errors = List.of(error);
        throw new ConflictingFilingException(errors);
    }

}
