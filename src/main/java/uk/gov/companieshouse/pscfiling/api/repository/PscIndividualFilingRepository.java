package uk.gov.companieshouse.pscfiling.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

/**
 * Psc individual filing repository pointing towards psc_submissions database.
 */
public interface PscIndividualFilingRepository extends MongoRepository<PscIndividualFiling, String> {

}
