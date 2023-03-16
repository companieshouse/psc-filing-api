package uk.gov.companieshouse.pscfiling.api.service;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.FilingDataConfig;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.FilingDataMapper;
import uk.gov.companieshouse.pscfiling.api.model.FilingKind;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.pscfiling.api.utils.MapHelper;

/**
 * Produces Filing Data format for consumption as JSON by filing-resource-handler external service.
 */
@Service
public class FilingDataServiceImpl implements FilingDataService {

    private final PscFilingService pscFilingService;
    private final FilingDataMapper dataMapper;
    private final PscDetailsService pscDetailsService;
    private final FilingDataConfig filingDataConfig;
    private final Logger logger;

    public FilingDataServiceImpl(final PscFilingService pscFilingService,
            final FilingDataMapper filingMapper, final PscDetailsService pscDetailsService,
            final FilingDataConfig filingDataConfig, final Logger logger) {
        this.pscFilingService = pscFilingService;
        this.dataMapper = filingMapper;
        this.pscDetailsService = pscDetailsService;
        this.filingDataConfig = filingDataConfig;
        this.logger = logger;
    }

    @Override
    public FilingApi generatePscFiling(final String filingId, final PscTypeConstants pscType,
            final Transaction transaction, final String passthroughHeader) {
        final var filing = new FilingApi();
        filing.setKind(
                FilingKind.PSC_CESSATION.getValue()); // TODO: handling other kinds to come later
        filing.setDescription(filingDataConfig.getPsc07Description());

        return populateFilingData(filing, filingId, pscType, transaction, passthroughHeader);
    }

    private FilingApi populateFilingData(final FilingApi filing, final String filingId,
            final PscTypeConstants pscType, final Transaction transaction,
            final String passthroughHeader) {

        final var transactionId = transaction.getId();
        final var pscFilingOpt = pscFilingService.get(filingId, transactionId);
        final var pscFiling = pscFilingOpt.orElseThrow(() -> new FilingResourceNotFoundException(
                String.format("Psc individual not found when generating filing for %s", filingId)));
        final PscApi pscDetails =
                pscDetailsService.getPscDetails(transaction, pscFiling.getReferencePscId(), pscType,
                        passthroughHeader);
        final PscCommunal enhancedPscFiling = dataMapper.enhance(pscFiling, pscDetails);

        final var filingData = dataMapper.map(enhancedPscFiling);
        final var dataMap =
                MapHelper.convertObject(filingData, PropertyNamingStrategies.SNAKE_CASE);

        final var logMap = LogHelper.createLogMap(transactionId, filingId);

        logMap.put("Data to submit", dataMap);
        logger.debugContext(transactionId, filingId, logMap);

        filing.setData(dataMap);

        return filing;
    }

}