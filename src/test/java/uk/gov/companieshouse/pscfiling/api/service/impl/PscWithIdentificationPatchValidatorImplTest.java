package uk.gov.companieshouse.pscfiling.api.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscWithIdentificationPatchValidator;

@ExtendWith(MockitoExtension.class)
class PscWithIdentificationPatchValidatorImplTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2023, 11, 5);

    private PscWithIdentificationPatchValidator testValidator;
    @Mock
    private SmartValidator validator;
    @Mock
    private PscMapper mapper;

    @BeforeEach
    void setUp() {
        testValidator = new PscWithIdentificationPatchValidatorImpl(validator, mapper);
    }

    @Test
    void validateWhenValid() {
        final var dummyFiling = PscWithIdentificationFiling.builder()
                .build();
        final var dummyDto = PscWithIdentificationDto.builder()
                .build();

        when(mapper.map(dummyFiling)).thenReturn(dummyDto);

        final var result = testValidator.validate(dummyFiling);

        assertThat(result.isSuccess(), is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    void validateWhenNotValid() {
        final var dummyFiling = PscWithIdentificationFiling.builder()
                .build();
        final var dummyDto = PscWithIdentificationDto.builder()
                .build();
        final var expectedError = new FieldError("patched", "ceasedOn", null, false, new String[]{
                "future.date.patched.ceasedOn",
                "future.date.ceasedOn",
                "future.date.java.time.LocalDate",
                "future.date"
        }, new Object[]{TEST_DATE}, "bad date");

        when(mapper.map(dummyFiling)).thenReturn(dummyDto);
        doAnswer(i -> {
            final Errors errors = i.getArgument(1);
            errors.rejectValue("ceasedOn", "future.date", new Object[]{TEST_DATE}, "bad date");
            return null;
        }).when(validator)
                .validate(any(), any());

        final var result = testValidator.validate(dummyFiling);
        final var error = ((List<FieldError>) result.getErrors()).get(0);

        assertThat(result.isSuccess(), is(false));
        assertThat(error, is(equalTo(expectedError)));
    }
}