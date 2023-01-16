package uk.gov.companieshouse.pscfiling.api.interceptor;

import static uk.gov.companieshouse.pscfiling.api.utils.Constants.TRANSACTION_ID_KEY;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;
import uk.gov.companieshouse.api.sdk.ApiClientService;
import uk.gov.companieshouse.pscfiling.api.exception.PscServiceException;
import uk.gov.companieshouse.pscfiling.api.exception.TransactionServiceException;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Component
@Order(1)
public class TestTransactionInterceptor implements HandlerInterceptor {

    private final TransactionService transactionService;

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    public TestTransactionInterceptor(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final var transactionId = pathVariables.get(TRANSACTION_ID_KEY);
        String passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY,transactionId);
        try {
            final var transaction = transactionService.getTransaction(transactionId, passthroughHeader);
            if (transaction.getStatus() != TransactionStatus.OPEN) {
                throw new TransactionServiceException("The Transaction is not Open and cannot be updated",
                        new Exception("The transaction is: " + transaction.getStatus()));
            }
            request.setAttribute("transaction", transaction);
            return true;
        } catch (PscServiceException ex) {
            response.setStatus(500);
            return false;
        }
    }
}
