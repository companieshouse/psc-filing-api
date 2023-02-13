package uk.gov.companieshouse.pscfiling.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.util.security.EricConstants;
import uk.gov.companieshouse.api.util.security.Permission;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public class BaseControllerIT {

    protected static final String TRANS_ID = "4f56fdf78b357bfc";
    protected static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    protected static final String ETAG = "e7101610f832de81c8d2f27904d6b1de2be82ff6";
    protected static final PscTypeConstants PSC_TYPE = PscTypeConstants.INDIVIDUAL;
    protected static final String PASSTHROUGH_HEADER = "passthrough";
    protected static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";
    protected static final LocalDate CEASED_ON_DATE = LocalDate.of(2022, 9, 13);
    protected static final LocalDate REGISTER_ENTRY_DATE = LocalDate.of(2022, 9, 14);
    protected static final String URL_PSC_INDIVIDUAL =
            "/transactions/{id}/persons-with-significant-control/individual";
    protected static final String URL_VALIDATION_STATUS =
            "/transactions/{transactionId}/persons-with-significant-control/{filingResourceId"
                    + "}/validation_status";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String PSC07_FRAGMENT = "\"reference_etag\":\""
            + ETAG
            + "\","
            + "\"reference_psc_id\": \""
            + PSC_ID
            + "\","
            + "\"ceased_on\": \"2022-09-13\","
            + "\"register_entry_date\": \"2022-09-14\"";
    protected static final String EMPTY_QUOTED_JSON = "\"\"";
    protected static final String MALFORMED_JSON = "{";
    protected static final Instant FIRST_INSTANT = Instant.parse("2022-10-15T09:44:08.108Z");
    protected static final String COMPANY_NUMBER = "012345678";
    protected static final String CEASED_ON = "2022-10-05";
    protected static final String REGISTER_ENTRY = "2022-10-05";
    protected HttpHeaders httpHeaders;
    protected Transaction transaction;
    @MockBean
    protected TransactionInterceptor transactionInterceptor;
    @MockBean
    protected OpenTransactionInterceptor openTransactionInterceptor;

    void setUp() throws Exception {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("ERIC-Access-Token", PASSTHROUGH_HEADER);
        setupEricTokenPermissions();
        when(transactionInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        when(openTransactionInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        transaction = createTestTransaction();
    }

    protected void setupEricTokenPermissions() {
        httpHeaders.add(EricConstants.ERIC_AUTHORISED_TOKEN_PERMISSIONS,
                Permission.Key.COMPANY_PSCS + "=" + Permission.Value.DELETE);
    }

    protected Transaction createTestTransaction() {
        transaction = new Transaction();
        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber(COMPANY_NUMBER);
        return transaction;
    }
}
