package uk.gov.companieshouse.pscfiling.api.service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        filing.setKind(MessageFormat.format("{0}#{1}", FilingKind.PSC_CESSATION.getValue(), pscType.getValue())); // TODO: handling other kinds to come later
        populateFilingData(filing, filingId, pscType, transaction, passthroughHeader);
        setFilingDescription(filing, pscType);
        return filing;
    }

    private FilingApi populateFilingData(final FilingApi filing, final String filingId,
            final PscTypeConstants pscType, final Transaction transaction,
            final String passthroughHeader) {

        final var transactionId = transaction.getId();
        final var pscFilingOpt = pscFilingService.get(filingId, transactionId);
        final var pscFiling = pscFilingOpt.orElseThrow(() -> new FilingResourceNotFoundException(
                String.format("PSC filing not found when generating filing for %s", filingId)));
        final PscApi pscDetails =
                pscDetailsService.getPscDetails(transaction, pscFiling.getReferencePscId(), pscType,
                        passthroughHeader);
        final PscCommunal enhancedPscFiling = dataMapper.enhance(pscFiling, pscType, pscDetails);

        final var filingData = dataMapper.map(enhancedPscFiling, pscType);
        final var dataMap = MapHelper.convertObject(filingData);

        final var logMap = LogHelper.createLogMap(transactionId, filingId);

        logMap.put("Data to submit", dataMap);
        logger.debugContext(transactionId, filingId, logMap);

        filing.setData(dataMap);

        return filing;
    }

    private void setFilingDescription(FilingApi filing, final PscTypeConstants pscType) {
        var name = "";
        switch (pscType) {
            case INDIVIDUAL:
                var names = new String[]{(String) filing.getData().get("title"),
                        (String) filing.getData().get("first_name"),
                        (String) filing.getData().get("other_forenames"),
                        (String) filing.getData().get("last_name")};
                var sb = new StringBuilder();
                for (String str : names)
                    if (str != null) {
                        sb.append(str).append(" ");
                    }
                var result = sb.toString();
                name = result.trim();
                break;
            case CORPORATE_ENTITY:
                // fall through
            case LEGAL_PERSON:
                name = (String) filing.getData().get("name");
                break;

        }

        var date = LocalDate.parse(filing.getData().get("ceased_on").toString());
        var fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String strDate = date.format(fmt);
        filing.setDescription(MessageFormat.format(filingDataConfig.getPsc07Description(), name, strDate));
    }
}