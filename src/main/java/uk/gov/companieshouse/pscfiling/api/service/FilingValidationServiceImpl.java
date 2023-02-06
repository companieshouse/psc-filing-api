package uk.gov.companieshouse.pscfiling.api.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValid;
import uk.gov.companieshouse.pscfiling.api.validator.IndividualFilingValidationContext;
import uk.gov.companieshouse.pscfiling.api.validator.WithIdentificationFilingValidationContext;

@Service
public class FilingValidationServiceImpl implements FilingValidationService {
    private final Map<PscTypeConstants, ? extends FilingForPscTypeValid> filingValidByPscType;

    @Autowired
    FilingValidationServiceImpl(final List<? extends FilingForPscTypeValid> forPscTypeValids) {
        this.filingValidByPscType = forPscTypeValids.stream()
                .collect(Collectors.toMap(FilingForPscTypeValid::getPscType, Function.identity()));
    }

    @Override
    public void validate(final IndividualFilingValidationContext individualContext) {
        Optional.ofNullable(filingValidByPscType.get(individualContext.getPscType()))
                .map(FilingForPscTypeValid::getFirst)
                .ifPresentOrElse(v -> v.validate(individualContext), () -> {
                    throw new UnsupportedOperationException(
                            MessageFormat.format("Validation not defined for PSC type ''{0}''",
                                individualContext.getPscType()));
                });
    }

    @Override
    public void validate(final WithIdentificationFilingValidationContext withIdentificationContext) {
        Optional.ofNullable(filingValidByPscType.get(withIdentificationContext.getPscType()))
                .map(FilingForPscTypeValid::getFirst)
                .ifPresentOrElse(v -> v.validate(withIdentificationContext), () -> {
                    throw new UnsupportedOperationException(
                            MessageFormat.format("Validation not defined for PSC type ''{0}''",
                                withIdentificationContext.getPscType()));
                });

    }

}
