package uk.gov.companieshouse.pscfiling.api.model.dto;

import java.time.LocalDate;
import java.util.List;

public interface PscDtoCommunal {
    AddressDto getAddress();

    Boolean getAddressSameAsRegisteredOfficeAddress();

    LocalDate getCeasedOn();

    String getName();

    List<String> getNaturesOfControl();

    LocalDate getNotifiedOn();

    String getReferenceEtag();

    String getReferencePscId();

    String getReferencePscListEtag();

    LocalDate getRegisterEntryDate();
}
