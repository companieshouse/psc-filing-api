package uk.gov.companieshouse.pscfiling.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.FilingDataConfig;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.PscWithIdentificationMapper;
import uk.gov.companieshouse.pscfiling.api.model.FilingKind;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.pscfiling.api.utils.MapHelper;

/**
 * Produces Filing Data format for consumption as JSON by filing-resource-handler external service.
 */
@Service
public class FilingDataServiceImpl implements FilingDataService {

    private final PscIndividualFilingService pscIndividualFilingService;
    private final PscWithIdentificationFilingService pscWithIdentificationFilingService;
    private final PscIndividualMapper pscIndividualMapper;
    private final PscWithIdentificationMapper pscWithIdentificationMapper;
    private final PscDetailsService pscDetailsService;
    private final FilingDataConfig filingDataConfig;
    private final Logger logger;

    public FilingDataServiceImpl(PscIndividualFilingService pscIndividualFilingService,
                                 PscWithIdentificationFilingService pscWithIdentificationFilingService,
                                 PscIndividualMapper filingMapper,
                                 PscWithIdentificationMapper pscWithIdentificationMapper,
                                 PscDetailsService pscDetailsService,
                                 FilingDataConfig filingDataConfig,
                                 Logger logger) {
        this.pscIndividualFilingService = pscIndividualFilingService;
        this.pscWithIdentificationFilingService = pscWithIdentificationFilingService;
        this.pscIndividualMapper = filingMapper;
        this.pscWithIdentificationMapper = pscWithIdentificationMapper;
        this.pscDetailsService = pscDetailsService;
        this.filingDataConfig = filingDataConfig;
        this.logger = logger;
    }

    @Override
    public FilingApi generatePscFiling(String filingId, Transaction transaction, String passthroughHeader) {
        var filing = new FilingApi();
        filing.setKind(FilingKind.PSC_CESSATION.getValue()); // TODO: handling other kinds to come later
        filing.setDescription(filingDataConfig.getPsc07Description());

        return populateIndividualFilingData(filing, filingId, transaction, passthroughHeader);
    }

    private FilingApi populateIndividualFilingData(FilingApi filing, String filingId, Transaction transaction, String passthroughHeader) {

        final var transactionId = transaction.getId();
        final var pscFilingOpt = pscIndividualFilingService.get(filingId, transactionId);
        final var pscFiling = pscFilingOpt.orElseThrow(() -> new FilingResourceNotFoundException(
                String.format("Psc individual not found when generating filing for %s", filingId)));

        final var pscDetails =
            pscDetailsService.getPscDetails(transaction, pscFiling.getReferencePscId(), PscTypeConstants.INDIVIDUAL,
                passthroughHeader);
        var nameElements = NameElements.builder()
                .title(pscDetails.getNameElements().getTitle())
                .forename(pscDetails.getNameElements().getForename())
                .otherForenames(pscDetails.getNameElements().getMiddleName())
                .surname(pscDetails.getNameElements().getSurname())
                .build();
        var enhancedPscFiling = PscIndividualFiling.builder(pscFiling)
                .nameElements(nameElements)
                .build();
        var filingData = pscIndividualMapper.mapFiling(enhancedPscFiling);
        var dataMap = MapHelper.convertObject(filingData);

        final var logMap = LogHelper.createLogMap(transactionId, filingId);

        logMap.put("Data to submit", dataMap);
        logger.debugContext(transactionId, filingId, logMap);

        filing.setData(dataMap);

        return filing;
    }

    private FilingApi populateWithIdentificationFilingData(FilingApi filing, String filingId, Transaction transaction, String passthroughHeader) {

        final var transactionId = transaction.getId();
        final var pscFilingOpt = pscWithIdentificationFilingService.get(filingId, transactionId);
        final var pscFiling = pscFilingOpt.orElseThrow(() -> new FilingResourceNotFoundException(
            String.format("Psc individual not found when generating filing for %s", filingId)));

        final var pscDetails =
            pscDetailsService.getPscDetails(transaction, pscFiling.getReferencePscId(), PscTypeConstants.CORPORATE_ENTITY,
                passthroughHeader);
        var enhancedPscFiling = PscWithIdentificationFiling.builder(pscFiling)
            .build();
        var filingData = pscWithIdentificationMapper.mapFiling(enhancedPscFiling);
        var dataMap = MapHelper.convertObject(filingData);

        final var logMap = LogHelper.createLogMap(transactionId, filingId);

        logMap.put("Data to submit", dataMap);
        logger.debugContext(transactionId, filingId, logMap);

        filing.setData(dataMap);

        return filing;
    }

}