package uk.gov.companieshouse.pscfiling.api.model.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface PscCommunal {
    String getId();

    Address getAddress();

    Boolean getAddressSameAsRegisteredOfficeAddress();

    LocalDate getCeasedOn();

    Instant getCreatedAt();

    String getEtag();

    String getKind();

    Links getLinks();

    List<String> getNaturesOfControl();

    LocalDate getNotifiedOn();

    String getReferenceEtag();

    String getReferencePscId();

    String getReferencePscListEtag();

    LocalDate getRegisterEntryDate();

    Instant getUpdatedAt();
}
