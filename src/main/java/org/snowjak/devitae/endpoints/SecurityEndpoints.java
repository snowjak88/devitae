package org.snowjak.devitae.endpoints;

import static net.logstash.logback.argument.StructuredArguments.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.devitae.data.entities.Scope;
import org.snowjak.devitae.data.entities.User;
import org.snowjak.devitae.security.JwtHelper;
import org.snowjak.devitae.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class SecurityEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityEndpoints.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping( path = "/login", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE } )
    public LoginResponse login(@RequestBody UsernamePassword usernamePassword) {

        LOG.info("Received login request for user: {}", kv("username", usernamePassword.username));
        final User user;
        try {
            user = userService.loadUserByUsername(usernamePassword.username);
        } catch(UsernameNotFoundException e) {
            LOG.debug("User not found: {}", kv("username", usernamePassword.username));
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Unknown username.");
        }

        if(!passwordEncoder.matches(usernamePassword.password, user.getPassword())) {
            LOG.debug("Password for user does not match: ", kv("username", usernamePassword.username));
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid password.");
        }

        final Map<String,String> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        final String claimedAuthorities = user.getScopes().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        claims.put("scope", claimedAuthorities);

        LOG.debug("Issuing JWT for user with authorities", kv("username", usernamePassword.username), kv("claimedAuthorities",  claimedAuthorities));

        return new LoginResponse(jwtHelper.createJwtForClaims(user.getUsername(), claims), user);

    }

    public static class UsernamePassword {
        public String username;
        public String password;
    }

    public static class LoginResponse {
        public final boolean authenticated;
        public final String jwt;
        public final String username;
        public final Collection<String> scopes;

        public LoginResponse(String jwt, User user) {
            this(jwt, user.getUsername(), user.getScopes().stream().map(Scope::getName).collect(Collectors.toList()), true);
        }

        public LoginResponse(String jwt, String username, Collection<String> scopes, boolean authenticated) {
            this.jwt = jwt;
            this.username = username;
            this.scopes = scopes;
            this.authenticated = authenticated;
        }
    }
}
