package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Optional;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

public interface PscWithIdentificationFilingService {

    PscWithIdentificationFiling save(PscWithIdentificationFiling filing, String transactionId);

    Optional<PscWithIdentificationFiling> get(String pscFilingId, String transactionId);

}
