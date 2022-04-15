package org.snowjak.devitae.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.devitae.data.entities.User;
import org.snowjak.devitae.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
public class UserManagementEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagementEndpoints.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Jwt getJwt() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof JwtAuthenticationToken))
            throw new RuntimeException("Authentication is not a JWT token!");
        final JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        if(!(token.getPrincipal() instanceof Jwt))
            throw new RuntimeException("Authentication's principal is not a JWT token!");
        return (Jwt) token.getPrincipal();
    }

    @PreAuthorize("isAuthenticated() && ( hasAuthority('SCOPE_user_viewDetails') || authentication.principal.claims['id'] == #userId )")
    @GetMapping(value = "/user/{userID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@PathVariable("userID") int userID) {

        final int viewerID = Integer.parseInt( (String) getJwt().getClaims().get("id") );
        LOG.debug("Viewing user.", kv("viewerID", viewerID), kv("viewedID", userID));

        final User result = userService.findByID(userID);

        if(result == null)
            LOG.debug("Trying to view user by ID, but that ID doesn't exist.", kv("viewerID", viewerID), kv("userID", userID));

        return result;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_create')")
    @PutMapping(value = "/user/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(String username, String password, @AuthenticationPrincipal JwtAuthenticationToken token) throws UserService.UsernameAlreadyExistsException, UserService.UsernameContainsInvalidCharactersException {

        final int creatorID = Integer.parseInt( (String) getJwt().getClaims().get("id") );

        LOG.info("Creating a new user.", kv("creatorID", creatorID), kv("username", username));
        final User newUser = userService.createUser(username, password);
        LOG.debug("Created new user.", kv("creatorID", creatorID), kv("username", newUser.getUsername()), kv("userID", newUser.getId()));
        return newUser;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_delete')")
    @DeleteMapping(value = "/user/{userID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpStatus deleteUser(@PathVariable("userID") int toDeleteID, @AuthenticationPrincipal JwtAuthenticationToken token) throws UserService.UserNotFoundException {

        final int deleterID = Integer.parseInt( (String) getJwt().getClaims().get("id") );

        if(toDeleteID == deleterID)
            throw new CannotDeleteSelfException();

        LOG.info("Deleting user.", kv("deleterID", deleterID), kv("toDeleteID", toDeleteID));
        userService.deleteUser(toDeleteID);
        LOG.debug("Deleted user.", kv("deleterID", deleterID), kv("toDeleteID", toDeleteID));
        return HttpStatus.OK;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_update') || authentication.principal.claims['id'] == #toUpdateID")
    @PostMapping(value="/user/{userID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@PathVariable("userID") int toUpdateID, @RequestBody User toUpdate, @AuthenticationPrincipal JwtAuthenticationToken token) throws UserService.UserNotFoundException, UserService.UsernameAlreadyExistsException, UserService.UsernameContainsInvalidCharactersException {

        final int updaterID = Integer.parseInt( (String) getJwt().getClaims().get("id") );

        LOG.info("Updating user.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID));
        final User result = userService.updateUser(toUpdateID, toUpdate);
        LOG.debug("Updated user.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID));
        return result;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_update') || authentication.principal.claims['id'] == #toUpdateID")
    @PostMapping(value="/user/{userID}/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpStatus changePassword(@PathVariable("userID") int toUpdateID, @RequestParam("password") String password, @AuthenticationPrincipal JwtAuthenticationToken token) throws UserService.UserNotFoundException {

        final int updaterID = Integer.parseInt( (String) getJwt().getClaims().get("id") );

        LOG.info("Changing password.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID));
        userService.changePassword(toUpdateID, passwordEncoder.encode(password));
        LOG.debug("Changed password.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID));
        return HttpStatus.OK;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_chmod')")
    @PostMapping(value="/user/{userID}/scopes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUserScopes(@PathVariable("userID") int toUpdateID, @RequestBody ScopeUpdates scopeUpdates, @AuthenticationPrincipal JwtAuthenticationToken token) throws UserService.UserNotFoundException {

        final int updaterID = Integer.parseInt( (String) getJwt().getClaims().get("id") );

        LOG.info("Modifying user scopes.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID), kv("scopeAdds", scopeUpdates.add), kv("scopeRemoves", scopeUpdates.remove));
        userService.changeUserScopes(toUpdateID, scopeUpdates.add, scopeUpdates.remove);
        LOG.debug("Modified user scopes.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID), kv("scopeAdds", scopeUpdates.add), kv("scopeRemoves", scopeUpdates.remove));
        return userService.findByID(toUpdateID);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class CannotDeleteSelfException extends RuntimeException {
        public CannotDeleteSelfException() {
            super("You cannot delete yourself.");
        }
    }

    public static class ScopeUpdates {
        public Collection<String> add = new ArrayList<>();
        public Collection<String> remove = new ArrayList<>();
    }
}
