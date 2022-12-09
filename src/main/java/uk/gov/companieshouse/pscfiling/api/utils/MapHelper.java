package uk.gov.companieshouse.pscfiling.api.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Map;

public final class MapHelper {

    private MapHelper() {
        // intentionally blank
    }

    private static ObjectMapper mapper = null;

    /**
     * Convert an Object into a Key/Value property map.
     *
     * @param obj the Object
     * @return a Map of property values
     */
    public static Map<String, Object> convertObject(Object obj) {
        if (mapper == null) {
            mapper = new ObjectMapper().setPropertyNamingStrategy(
                    PropertyNamingStrategies.SNAKE_CASE);
        }

        mapper.registerModule(new JavaTimeModule());

        return mapper.convertValue(obj, new TypeReference<>() {
        });
    }

}