package uk.gov.companieshouse.pscfiling.api.model.dto;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

public interface PscDtoCommunal {
    AddressDto getAddress();

    Boolean getAddressSameAsRegisteredOfficeAddress();

    @NotNull
    @PastOrPresent
    LocalDate getCeasedOn();

    List<String> getNaturesOfControl();

    LocalDate getNotifiedOn();

    @NotBlank
    String getReferenceEtag();

    @NotBlank
    String getReferencePscId();

    String getReferencePscListEtag();

    @NotNull
    @PastOrPresent
    LocalDate getRegisterEntryDate();
}
