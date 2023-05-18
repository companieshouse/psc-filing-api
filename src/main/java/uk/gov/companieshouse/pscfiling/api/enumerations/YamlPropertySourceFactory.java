package uk.gov.companieshouse.pscfiling.api.enumerations;

import java.util.Objects;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        final var encoded = Objects.requireNonNull(encodedResource);
        final var resource = Objects.requireNonNull(encoded.getResource());
        final var factory = new YamlPropertiesFactoryBean();

        factory.setResources(resource);

        final var properties = Objects.requireNonNull(factory.getObject());
        final var filename = Objects.requireNonNull(resource.getFilename());

        return new PropertiesPropertySource(filename, properties);
    }
}