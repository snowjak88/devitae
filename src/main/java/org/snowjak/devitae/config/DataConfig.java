package org.snowjak.devitae.config;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import org.snowjak.devitae.data.entities.Scope;
import org.snowjak.devitae.data.entities.User;
import org.snowjak.devitae.data.repositories.ScopeRepository;
import org.snowjak.devitae.data.repositories.UserRepository;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableJpaRepositories(basePackages = "org.snowjak.devitae.data.repositories")
@EnableJpaAuditing
public class DataConfig {

}
