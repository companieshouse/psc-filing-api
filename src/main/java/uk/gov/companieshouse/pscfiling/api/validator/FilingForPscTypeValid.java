package uk.gov.companieshouse.pscfiling.api.validator;

import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public interface FilingForPscTypeValid {
    PscTypeConstants getPscType();

    FilingValid getFirst();
}
