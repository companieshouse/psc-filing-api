package uk.gov.companieshouse.pscfiling.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

public interface PscWithIdentificationFilingRepository
        extends MongoRepository<PscWithIdentificationFiling, String> {
}
