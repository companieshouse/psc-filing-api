package uk.gov.companieshouse.pscfiling.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.ResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.pscfiling.api.utils.MapHelper;

/**
 * Produces Filing Data format for consumption as JSON by filing-resource-handler external service.
 */
@Service
public class FilingDataServiceImpl implements FilingDataService {

    private final PscFilingService pscFilingService;
    private final PscIndividualMapper pscIndividualMapper;
    private final Logger logger;

    public FilingDataServiceImpl(PscFilingService pscFilingService,
            PscIndividualMapper filingMapper, Logger logger) {
        this.pscFilingService = pscFilingService;
        this.pscIndividualMapper = filingMapper;
        this.logger = logger;
    }

    /**
     * Generate FilingApi data enriched by names and date of birth from company-appointments API.
     *
     * @param transactionId the Transaction ID
     * @param filingId      the Psc Filing ID
     * @return the FilingApi data for JSON response
     */
    @Override
    public FilingApi generatePscFiling(String transactionId, String filingId) {
        var filing = new FilingApi();
        filing.setKind("psc-filing#termination"); // TODO: handling other kinds to come later

        setFilingApiData(filing, transactionId, filingId);
        return filing;
    }

    private void setFilingApiData(FilingApi filing, String transactionId, String filingId) {
        var pscFilingOpt = pscFilingService.get(filingId, transactionId);
        var pscFiling = pscFilingOpt.orElseThrow(() -> new ResourceNotFoundException(
                String.format("Psc not found when generating filing for %s", filingId)));
        // TODO this is dummy data until we get the details from company-appointments API
        var enhancedPscFiling = PscIndividualFiling.builder(pscFiling)
                .dateOfBirth(new Date3Tuple(20, 10, 2000))
//                .firstName("JOE")
//                .lastName("BLOGGS")
                .build();
        var filingData = pscIndividualMapper.map(enhancedPscFiling);
        var dataMap = MapHelper.convertObject(filingData);

        final var logMap = LogHelper.createLogMap(transactionId, filingId);

        logMap.put("Data to submit", dataMap);
        logger.debugContext(transactionId, filingId, logMap);

        filing.setData(dataMap);
    }

}
