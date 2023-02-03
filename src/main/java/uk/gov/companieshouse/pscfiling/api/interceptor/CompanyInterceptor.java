package uk.gov.companieshouse.pscfiling.api.interceptor;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.AttributeName;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.ConflictingFilingException;
import uk.gov.companieshouse.pscfiling.api.service.CompanyProfileService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Component
public class CompanyInterceptor implements HandlerInterceptor {

    public static final String COMPANY_HAS_SUPER_SECURE_PSCS_MESSAGE =
            "As a result of protection under section 790ZG of the Companies Act 2006 this form cannot be filed online" +
                    ". You can only file this form on paper, refer to the information pack that was sent on " +
                    "application of the protection.";
    private static final List<String> ALLOWED_COMPANY_TYPES =
            List.of("private-unlimited", "ltd", "plc", "old-public-company",
                    "private-limited-guarant-nsc-limited-exemption", "private-limited-guarant-nsc",
                    "private-unlimited-nsc", "private-limited-shares-section-30-exemption");
    public static final String COMPANY_TYPE_NOT_ALLLOWED_MESSSAGE = "PSC form cannot be filed for this company type: ";
    private static final List<String> COMPANY_STATUS_NOT_ALLOWED = List.of("dissolved", "converted-closed");
    public static final String COMPANY_STATUS_NOT_ALLOWED_MESSAGE = "Form cannot be filed for a company that is ";

    CompanyProfileService companyProfileService;

    public CompanyInterceptor(CompanyProfileService companyProfileService) {
        this.companyProfileService = companyProfileService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());
        final var passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        CompanyProfileApi companyProfile = companyProfileService.getCompanyProfile(transaction, passthroughHeader);

        if (companyProfile != null) {
            if (companyProfile.hasSuperSecurePscs()) {
                sendValidationError(COMPANY_HAS_SUPER_SECURE_PSCS_MESSAGE);
            }

            if (!ALLOWED_COMPANY_TYPES.contains(companyProfile.getType())) {
                sendValidationError(COMPANY_TYPE_NOT_ALLLOWED_MESSSAGE + companyProfile.getType());
            }

            if (COMPANY_STATUS_NOT_ALLOWED.contains(companyProfile.getCompanyStatus())) {
                sendValidationError(COMPANY_STATUS_NOT_ALLOWED_MESSAGE + companyProfile.getCompanyStatus());
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
