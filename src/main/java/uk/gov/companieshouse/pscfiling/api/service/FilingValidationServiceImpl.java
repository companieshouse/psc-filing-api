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
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValid;
import uk.gov.companieshouse.pscfiling.api.validator.FilingValidationContext;

@Service
public class FilingValidationServiceImpl implements FilingValidationService {
    private final Map<PscTypeConstants, ? extends FilingForPscTypeValid> filingValidByPscType;

    @Autowired
    FilingValidationServiceImpl(final List<? extends FilingForPscTypeValid> forPscTypeValids) {
        this.filingValidByPscType = forPscTypeValids.stream()
                .collect(Collectors.toMap(FilingForPscTypeValid::getPscType, Function.identity()));
    }


    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> context) {
        Optional.ofNullable(filingValidByPscType.get(context.getPscType()))
                .map(FilingForPscTypeValid::getFirst)
                .ifPresentOrElse(v -> v.validate(context), () -> {
                    throw new UnsupportedOperationException(
                            MessageFormat.format("Validation not defined for PSC type ''{0}''",
                                context.getPscType()));
                });
    }

}