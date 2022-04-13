package org.snowjak.devitae.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${devitae.auth.jwt.keystore.path}")
    private String keystorePath;

    @Value("${devitae.auth.jwt.keystore.password}")
    private String keystorePassword;

    @Value("${devitae.auth.jwt.keystore.alias}")
    private String keyAlias;

    @Value("${devitae.auth.jwt.keystore.privateKeyPassphrase}")
    private String keystorePrivateKeyPassphrase;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.GET, "/")
                .antMatchers(HttpMethod.GET, "/index.html", "/**/*.ico", "/**/*.js", "/**/*.css", "/**/*.json");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .cors()
                .and()
            .csrf()
                .disable()
            .authorizeRequests()
                .anyRequest().permitAll()
                .and()
            .httpBasic()
                .disable()
            .formLogin()
                .disable()
            .logout()
                .disable()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }

    @Bean
    public KeyStore keyStore() {
        try {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(getClass().getResourceAsStream(keystorePath), keystorePassword.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public RSAPublicKey jwtSigningKey(KeyStore keyStore) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        try {
            keyStore.getKey(keyAlias, keystorePrivateKeyPassphrase.toCharArray());
            final PublicKey publicKey = keyStore.getCertificate(keyAlias).getPublicKey();

            if(publicKey instanceof RSAPublicKey)
                return (RSAPublicKey) publicKey;

        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            LOG.error("Could not load private key from keystore!", e);
            throw new RuntimeException("Could not load private key from keystore!", e);
        }

        throw new RuntimeException("Could not load private key from keystore!");
    }

    @Bean
    public RSAPrivateKey jwtPrivateKey(KeyStore keyStore) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        try {
            final Key key = keyStore.getKey(keyAlias, keystorePrivateKeyPassphrase.toCharArray());

            if(key instanceof RSAPrivateKey)
                return (RSAPrivateKey) key;

        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            LOG.error("Could not load private key from keystore!", e);
            throw new RuntimeException("Could not load private key from keystore!", e);
        }

        throw new RuntimeException("Could not load private key from keystore!");
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}