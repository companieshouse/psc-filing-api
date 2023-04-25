package uk.gov.companieshouse.pscfiling.api.service.impl;

import static java.util.function.Predicate.not;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import uk.gov.companieshouse.patch.model.ValidationResult;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualPatchValidator;

@Component
public class PscIndividualPatchValidatorImpl implements PscIndividualPatchValidator {
    private final SmartValidator validator;
    private final PscMapper mapper;

    @Autowired
    public PscIndividualPatchValidatorImpl(final SmartValidator validator, final PscMapper mapper) {
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public ValidationResult validate(final PscIndividualFiling patchedFiling) {
        return Optional.ofNullable(patchedFiling)
                .map(mapper::map)
                .map(d -> {
                    final var e = new BeanPropertyBindingResult(d, "patched");
                    validator.validate(d, e);

                    return e;
                })
                .map(AbstractBindingResult::getFieldErrors)
                .filter(not(List::isEmpty))
                .map(ValidationResult::new)
                .orElseGet(ValidationResult::new);
    }

}