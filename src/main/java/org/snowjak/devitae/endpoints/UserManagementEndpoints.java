package org.snowjak.devitae.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.devitae.data.entities.User;
import org.snowjak.devitae.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController("/user")
public class UserManagementEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagementEndpoints.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("isAuthenticated() && ( hasAuthority('user_viewDetails') || authentication.details.id == #userId )")
    @GetMapping(value = "/{userID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@RequestParam("userID") int userID, @AuthenticationPrincipal User user) {

        LOG.debug("Viewing user.", kv("viewerID", user.getId()), kv("viewedID", userID));

        final User result = userService.findByID(userID);

        if(result == null)
            LOG.debug("Trying to view user by ID, but that ID doesn't exist.", kv("userID", userID));

        return result;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('user_create')")
    @PutMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(String username, String password, @AuthenticationPrincipal User creator) throws UserService.UsernameAlreadyExistsException {

        LOG.info("Creating a new user.", kv("creatorID", creator.getId()), kv("username", username));
        final User newUser = userService.createUser(username, password);
        LOG.debug("Created new user.", kv("creatorID", creator.getId()), kv("username", newUser.getUsername()), kv("userID", newUser.getId()));
        return newUser;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('user_delete')")
    @DeleteMapping(value = "/{userID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpStatus deleteUser(@RequestParam("userID") int toDeleteID, @AuthenticationPrincipal User deleter) throws UserService.UserNotFoundException {

        if(toDeleteID == deleter.getId())
            throw new CannotDeleteSelfException();

        LOG.info("Deleting user.", kv("deleterID", deleter.getId()), kv("toDeleteID", toDeleteID));
        userService.deleteUser(toDeleteID);
        LOG.debug("Deleted user.", kv("deleterID", deleter.getId()), kv("toDeleteID", toDeleteID));
        return HttpStatus.OK;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('user_update') || authentication.details.id == #toUpdateID")
    @PostMapping(value="/{userID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@RequestParam("userID") int toUpdateID, @RequestBody User toUpdate, @AuthenticationPrincipal User updater) throws UserService.UserNotFoundException, UserService.UsernameAlreadyExistsException {

        LOG.info("Updating user.", kv("updaterID", updater.getId()), kv("toUpdateID", toUpdateID));
        final User result = userService.updateUser(toUpdateID, toUpdate);
        LOG.debug("Updated user.", kv("updaterID", updater.getId()), kv("toUpdateID", toUpdateID));
        return result;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('user_update') || authentication.details.id == #toUpdateID")
    @PostMapping(value="/{userID}/password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpStatus changePassword(@RequestParam("userID") int toUpdateID, @RequestParam("password") String password, @AuthenticationPrincipal User updater) throws UserService.UserNotFoundException {

        LOG.info("Changing password.", kv("updaterID", updater.getId()), kv("toUpdateID", toUpdateID));
        userService.changePassword(toUpdateID, passwordEncoder.encode(password));
        LOG.debug("Changed password.", kv("updaterID", updater.getId()), kv("toUpdateID", toUpdateID));
        return HttpStatus.OK;
    }

    @PreAuthorize("isAuthenticated() && hasAuthority('user_chmod')")
    @PostMapping(value="/{userID}/scopes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUserScopes(@RequestParam("userID") int toUpdateID, @RequestBody ScopeUpdates scopeUpdates, @AuthenticationPrincipal User updater) throws UserService.UserNotFoundException {

        LOG.info("Modifying user scopes.", kv("updaterID", updater.getId()), kv("toUpdateID", toUpdateID), kv("scopeAdds", scopeUpdates.add), kv("scopeRemoves", scopeUpdates.remove));
        userService.changeUserScopes(toUpdateID, scopeUpdates.add, scopeUpdates.remove);
        LOG.debug("Modified user scopes.", kv("updaterID", updater.getId()), kv("toUpdateID", toUpdateID), kv("scopeAdds", scopeUpdates.add), kv("scopeRemoves", scopeUpdates.remove));
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
