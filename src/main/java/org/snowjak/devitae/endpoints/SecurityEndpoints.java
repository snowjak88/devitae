package org.snowjak.devitae.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping( path = "/auth", produces = { MediaType.APPLICATION_JSON_VALUE } )
    public AuthDetails getAuthenticated() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return new AuthDetails(auth);
    }

    @PostMapping( path = "/login", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE } )
    public JwtToken login(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {

        LOG.info("Received login request for user '{}'", username);
        final User user;
        try {
            user = userService.loadUserByUsername(username);
        } catch(UsernameNotFoundException e) {
            LOG.debug("User '{}' not found", username);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Unknown username.");
        }

        if(!passwordEncoder.matches(password, user.getPassword())) {
            LOG.debug("Password for user '{}' does not match", username);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid password.");
        }

        final Map<String,String> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        final String claimedAuthorities = user.getScopes().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        claims.put("scope", claimedAuthorities);

        LOG.debug("Issuing JWT for user '{}' with authorities '{}'", username, claimedAuthorities);

        return new JwtToken(jwtHelper.createJwtForClaims(user.getUsername(), claims));

    }

    public static class JwtToken {

        public final String jwt;

        JwtToken(String jwt) {
            this.jwt = jwt;
        }
    }

    public static class AuthDetails {

        public final boolean authenticated;
        public final String username;
        public final Collection<String> scopes;

        public AuthDetails(Authentication auth) {
            if(auth == null || auth instanceof AnonymousAuthenticationToken) {
                this.authenticated = false;
                this.username = null;
                this.scopes = Collections.emptyList();
            }
            else {
                this.authenticated = auth.isAuthenticated();
                this.username = auth.getName();
                this.scopes = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            }
        }

        public AuthDetails(boolean authenticated, String username, Collection<String> scopes) {
            this.authenticated = authenticated;
            this.username = username;
            this.scopes = Collections.unmodifiableCollection(scopes);
        }
    }
}
