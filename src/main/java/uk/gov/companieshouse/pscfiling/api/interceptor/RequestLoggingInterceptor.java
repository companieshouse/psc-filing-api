package uk.gov.companieshouse.pscfiling.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.RequestLogger;
import uk.gov.companieshouse.pscfiling.api.PscFilingApiApplication;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor, RequestLogger {

    private final Logger logger;

    @Autowired
    public RequestLoggingInterceptor() {
        logger = LoggerFactory.getLogger(PscFilingApiApplication.APP_NAMESPACE);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logStartRequestProcessing(request, logger);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        logEndRequestProcessing(request, response, logger);
    }
}
