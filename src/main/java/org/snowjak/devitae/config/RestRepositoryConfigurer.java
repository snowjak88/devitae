package org.snowjak.devitae.config;

import org.snowjak.devitae.data.entities.Scope;
import org.snowjak.devitae.data.entities.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Component
public class RestRepositoryConfigurer implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        final ExposureConfiguration exposure = config.getExposureConfiguration();
        exposure
                .disablePatchOnItemResources()
                .forDomainType(Scope.class).disablePutForCreation();
    }
}
