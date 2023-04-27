package uk.gov.companieshouse.pscfiling.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;

/**
 * Psc filing repository pointing towards psc_submissions database.
 */
public interface PscFilingRepository extends MongoRepository<PscCommunal, String> {
}
