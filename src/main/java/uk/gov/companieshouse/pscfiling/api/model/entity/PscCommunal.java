package uk.gov.companieshouse.pscfiling.api.model.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "psc_submissions")
public interface PscCommunal {

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

    LocalDate getRegisterEntryDate();

    Instant getUpdatedAt();
}
