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
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public class BaseControllerIT {
    protected static final String TRANS_ID = "4f56fdf78b357bfc";
    protected static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    protected static final PscTypeConstants PSC_TYPE = PscTypeConstants.INDIVIDUAL;
    protected static final String PASSTHROUGH_HEADER = "passthrough";
    protected static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";
    protected static final String PSC07_FRAGMENT = "\"reference_etag\": \"etag\"," +
            "\"reference_psc_id\": \"" + PSC_ID + "\"," +
            "\"ceased_on\": \"2022-09-13\"," +
            "\"register_entry_date\": \"2022-09-14\"";
    protected static final String EMPTY_QUOTED_JSON = "\"\"";
    protected static final String MALFORMED_JSON = "{";
    protected static final Instant FIRST_INSTANT = Instant.parse("2022-10-15T09:44:08.108Z");
    protected static final String COMPANY_NUMBER = "012345678";
    protected static final LocalDate CEASED_ON_DATE = LocalDate.of(2022, 9, 13);
    protected static final LocalDate REGISTER_ENTRY_DATE = LocalDate.of(2022, 9, 14);
    protected HttpHeaders httpHeaders;
    protected Transaction transaction;
    @MockBean
    protected TransactionInterceptor transactionInterceptor;
    @MockBean
    protected OpenTransactionInterceptor openTransactionInterceptor;

    void setUp() throws Exception {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("ERIC-Access-Token", PASSTHROUGH_HEADER);
        when(transactionInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        when(openTransactionInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        transaction = createTestTransaction();
    }

    protected Transaction createTestTransaction() {
        transaction = new Transaction();
        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber("012345678");
        return transaction;
    }
}
