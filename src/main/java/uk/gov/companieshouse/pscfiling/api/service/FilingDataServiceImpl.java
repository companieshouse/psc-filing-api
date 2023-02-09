package uk.gov.companieshouse.pscfiling.api.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.FilingDataConfig;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.PscWithIdentificationMapper;
import uk.gov.companieshouse.pscfiling.api.model.FilingKind;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.pscfiling.api.utils.MapHelper;

/**
 * Produces Filing Data format for consumption as JSON by filing-resource-handler external service.
 */
@Service
public class FilingDataServiceImpl implements FilingDataService {

    private final PscFilingService pscFilingService;
    private final PscMapper pscMapper;
    private final PscWithIdentificationMapper pscWithIdentificationMapper;
    private final PscDetailsService pscDetailsService;
    private final FilingDataConfig filingDataConfig;
    private final Logger logger;

    public FilingDataServiceImpl(PscFilingService pscFilingService,
                                 PscMapper filingMapper,
                                 PscWithIdentificationMapper pscWithIdentificationMapper,
                                 PscDetailsService pscDetailsService,
                                 FilingDataConfig filingDataConfig,
                                 Logger logger) {
        this.pscFilingService = pscFilingService;
        this.pscMapper = filingMapper;
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

        return populateFilingData(filing, filingId, transaction, passthroughHeader);
    }

    private FilingApi populateFilingData(FilingApi filing, String filingId, Transaction transaction, String passthroughHeader) {

        final var transactionId = transaction.getId();
        final var pscFilingOpt = pscFilingService.get(filingId, transactionId);
        final var pscFiling = pscFilingOpt.orElseThrow(() -> new FilingResourceNotFoundException(
                String.format("Psc individual not found when generating filing for %s", filingId)));

        PscApi pscDetails = new PscApi();
        NameElements nameElements;
        PscFiling enhancedPscFiling;

        if (pscDetails.getKind().equals(PscTypeConstants.INDIVIDUAL.getValue())) {

            pscDetails =
                pscDetailsService.getPscDetails(transaction, pscFiling.getReferencePscId(), PscTypeConstants.INDIVIDUAL,
                    passthroughHeader);

            nameElements = NameElements.builder()
                .title(pscDetails.getNameElements().getTitle())
                .forename(pscDetails.getNameElements().getForename())
                .otherForenames(pscDetails.getNameElements().getMiddleName())
                .surname(pscDetails.getNameElements().getSurname())
                .build();

            enhancedPscFiling = PscIndividualFiling.builder((PscIndividualFiling) pscFiling)
                .nameElements(nameElements)
                .build();

        } else {

            enhancedPscFiling = PscWithIdentificationFiling.builder((PscWithIdentificationFiling) pscFiling)
                .build();
        }

        var filingData = pscMapper.map((enhancedPscFiling));
        var dataMap = MapHelper.convertObject(filingData);

        final var logMap = LogHelper.createLogMap(transactionId, filingId);

        logMap.put("Data to submit", dataMap);
        logger.debugContext(transactionId, filingId, logMap);

        filing.setData(dataMap);

        return filing;
    }

//    private FilingApi populateWithIdentificationFilingData(FilingApi filing, String filingId, Transaction transaction, String passthroughHeader) {
//
//        final var transactionId = transaction.getId();
//        final var pscFilingOpt = pscFilingService.get(filingId, transactionId);
//        final var pscFiling = pscFilingOpt.orElseThrow(() -> new FilingResourceNotFoundException(
//            String.format("Psc individual not found when generating filing for %s", filingId)));
//
//        final var pscDetails =
//            pscDetailsService.getPscDetails(transaction, pscFiling.getReferencePscId(), PscTypeConstants.CORPORATE_ENTITY,
//                passthroughHeader);
//        var enhancedPscFiling = PscWithIdentificationFiling.builder(pscFiling)
//            .build();
//        var filingData = pscMapper.mapFiling(enhancedPscFiling);
//        var dataMap = MapHelper.convertObject(filingData);
//
//        final var logMap = LogHelper.createLogMap(transactionId, filingId);
//
//        logMap.put("Data to submit", dataMap);
//        logger.debugContext(transactionId, filingId, logMap);
//
//        filing.setData(dataMap);
//
//        return filing;
//    }

}