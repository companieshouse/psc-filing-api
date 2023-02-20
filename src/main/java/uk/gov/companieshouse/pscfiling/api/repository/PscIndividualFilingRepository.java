package uk.gov.companieshouse.pscfiling.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

public interface PscIndividualFilingRepository extends MongoRepository<PscIndividualFiling, String> {

}
