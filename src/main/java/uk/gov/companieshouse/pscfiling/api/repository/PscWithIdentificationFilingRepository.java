package uk.gov.companieshouse.pscfiling.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

/**
 * Psc with identification filing repository pointing towards psc_submissions database.
 */
public interface PscWithIdentificationFilingRepository
        extends MongoRepository<PscWithIdentificationFiling, String> {
}
