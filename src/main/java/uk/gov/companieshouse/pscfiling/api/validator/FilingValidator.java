package uk.gov.companieshouse.pscfiling.api.validator;

import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.error.ApiErrors;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

public interface FilingValidator {

    ApiErrors validate(PscIndividualDto dto, ApiErrors errors, Transaction transaction,
                       PscTypeConstants pscType, String passthroughHeader);

    void setNext(FilingValidator filingValidator);
}
