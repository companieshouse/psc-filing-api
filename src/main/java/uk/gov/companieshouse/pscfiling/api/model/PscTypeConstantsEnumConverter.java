package uk.gov.companieshouse.pscfiling.api.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class PscTypeConstantsEnumConverter implements Converter<String, PscTypeConstants> {
    @Override
    public PscTypeConstants convert(@Nullable final String source) {
        return PscTypeConstants.nameOf(source).orElseThrow(() -> new IllegalArgumentException(
                String.format("No such PSC Type: %s", source)));
    }
}
