package uk.gov.companieshouse.pscfiling.api.validator;

import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public interface FilingForPscTypeValid {
    PscTypeConstants pscType();

    FilingValid first();
}
