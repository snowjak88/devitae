package org.snowjak.devitae.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.devitae.data.entities.Scope;
import org.snowjak.devitae.data.entities.User;
import org.snowjak.devitae.security.JwtHelper;
import org.snowjak.devitae.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
public class AuthorizationEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationEndpoints.class);

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
        claims.put("id", Integer.toString(user.getId()));
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
        public final int id;

        public LoginResponse(String jwt, User user) {
            this(jwt, user.getId(), true);
        }

        public LoginResponse(String jwt, int id, boolean authenticated) {
            this.jwt = jwt;
            this.id = id;
            this.authenticated = authenticated;
        }
    }
}
