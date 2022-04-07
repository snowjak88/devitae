package org.snowjak.devitae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
public class DevitaeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevitaeApplication.class, args);
    }
}
