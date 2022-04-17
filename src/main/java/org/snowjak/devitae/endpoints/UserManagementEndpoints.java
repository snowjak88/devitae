package org.snowjak.devitae.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.devitae.data.entities.Scope;
import org.snowjak.devitae.data.entities.User;
import org.snowjak.devitae.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpMethod;
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
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RepositoryRestController
//@BasePathAwareController
@ResponseBody
public class UserManagementEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagementEndpoints.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Jwt getJwt() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken))
            throw new RuntimeException("Authentication is not a JWT token!");
        final JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        if (!(token.getPrincipal() instanceof Jwt))
            throw new RuntimeException("Authentication's principal is not a JWT token!");
        return (Jwt) token.getPrincipal();
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_create')")
    @RequestMapping(method = PUT, path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RestResource(description = @Description("Create a new user."))
    public User createUser(String username, String password,
                           //@AuthenticationPrincipal JwtAuthenticationToken token,
                           @Value("${authentication.principal.claims['id']}") int creatorID) throws UserService.UsernameAlreadyExistsException, UserService.UsernameContainsInvalidCharactersException {

        //final int creatorID = Integer.parseInt( (String) getJwt().getClaims().get("id") );

        LOG.info("Creating a new user.", kv("creatorID", creatorID), kv("username", username));
        final User newUser = userService.createUser(username, password);
        LOG.debug("Created new user.", kv("creatorID", creatorID), kv("username", newUser.getUsername()), kv("userID", newUser.getId()));
        return newUser;
    }

//    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_delete')")
//    @RequestMapping(method = DELETE, value = "/users/{userID}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @RestResource(description = @Description("Deletes this user."))
//    public HttpStatus deleteUser(@PathVariable("userID") int toDeleteID, @AuthenticationPrincipal JwtAuthenticationToken token) throws UserService.UserNotFoundException {
//
//        final int deleterID = Integer.parseInt( (String) getJwt().getClaims().get("id") );
//
//        if(toDeleteID == deleterID)
//            throw new CannotDeleteSelfException();
//
//        LOG.info("Deleting user.", kv("deleterID", deleterID), kv("toDeleteID", toDeleteID));
//        userService.deleteUser(toDeleteID);
//        LOG.debug("Deleted user.", kv("deleterID", deleterID), kv("toDeleteID", toDeleteID));
//        return HttpStatus.OK;
//    }

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_update') || authentication.principal.claims['id'] == #toUpdateID")
    @RequestMapping(method = PUT, value="/users/{userID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RestResource(description = @Description("Update this user's details (excluding password and scopes)."))
    public User updateUser(@PathVariable("userID") int toUpdateID, @RequestBody User toUpdate,
                           //@AuthenticationPrincipal JwtAuthenticationToken token,
                           @Value("${authentication.principal.claims['id']}") int updaterID) throws UserService.UserNotFoundException, UserService.UsernameAlreadyExistsException, UserService.UsernameContainsInvalidCharactersException {

        //final int updaterID = Integer.parseInt( (String) getJwt().getClaims().get("id") );

        LOG.info("Updating user.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID));
        final User result = userService.updateUser(toUpdateID, toUpdate);
        LOG.debug("Updated user.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID));
        return result;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_update') || authentication.principal.claims['id'] == #toUpdateID")
    @RequestMapping(method = PATCH, value="/users/{userID}/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RestResource(description = @Description("Update this user's password."))
    public HttpStatus changePassword(@PathVariable("userID") int toUpdateID, @RequestParam("password") String password,
                                     //@AuthenticationPrincipal JwtAuthenticationToken token,
                                     @Value("${authentication.principal.claims['id']}") int updaterID) throws UserService.UserNotFoundException {

        //final int updaterID = Integer.parseInt( (String) getJwt().getClaims().get("id") );

        LOG.info("Changing password.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID));
        userService.changePassword(toUpdateID, passwordEncoder.encode(password));
        LOG.debug("Changed password.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID));
        return HttpStatus.OK;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_chmod')")
    @RequestMapping(method = PATCH, value="/users/{userID}/scopes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RestResource(description = @Description("Change the set of scopes associated with this user."))
    public User updateUserScopes(@PathVariable("userID") int toUpdateID, @RequestBody Collection<String> scopes,
                                 //@AuthenticationPrincipal JwtAuthenticationToken token,
                                 @Value("${authentication.principal.claims['id']}") int updaterID) throws UserService.UserNotFoundException {

        //final int updaterID = Integer.parseInt( (String) getJwt().getClaims().get("id") );

        final String newScopes = scopes.stream().collect(Collectors.joining(","));
        LOG.info("Modifying user scopes.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID), kv("newScopes", newScopes));
        userService.changeUserScopes(toUpdateID, scopes);
        LOG.debug("Modified user scopes.", kv("updaterID", updaterID), kv("toUpdateID", toUpdateID), kv("newScopes", newScopes));
        return userService.findByID(toUpdateID);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class CannotDeleteSelfException extends RuntimeException {
        public CannotDeleteSelfException() {
            super("You cannot delete yourself.");
        }
    }

    public static class BriefUser {
        public final int id;
        public final String username;

        public BriefUser(int id, String username) {
            this.id = id;
            this.username = username;
        }
        public BriefUser(User user) {
            this(user.getId(), user.getUsername());
        }
    }
}
