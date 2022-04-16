package org.snowjak.devitae.config;

import org.snowjak.devitae.data.entities.Scope;
import org.snowjak.devitae.data.repositories.ScopeRepository;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableJpaRepositories(basePackages = "org.snowjak.devitae.data.repositories")
@EnableJpaAuditing
public class DataConfig {

}
