package uk.gov.companieshouse.pscfiling.api.model.dto;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.PastOrPresent;

public interface PscDtoCommunal {
    AddressDto getAddress();

    Boolean getAddressSameAsRegisteredOfficeAddress();

    @PastOrPresent
    LocalDate getCeasedOn();

    List<String> getNaturesOfControl();

    LocalDate getNotifiedOn();

    String getReferenceEtag();

    String getReferencePscId();

    @PastOrPresent
    LocalDate getRegisterEntryDate();
}
