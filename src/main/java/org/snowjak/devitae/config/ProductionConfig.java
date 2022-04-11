package org.snowjak.devitae.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

/**
 * Configuration active when the application is running in production mode.
 */
@Configuration
@Profile("prod")
public class ProductionConfig {

    @Bean
    public PasswordEncoder passwordEncoder(@Value("${PASSWORD_SALT}") String secretSalt) {
        return new Pbkdf2PasswordEncoder(secretSalt, 50000, 256);
    }
}
