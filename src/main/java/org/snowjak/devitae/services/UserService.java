package org.snowjak.devitae.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.devitae.data.entities.User;
import org.snowjak.devitae.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;
import java.util.Objects;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.argument.StructuredArguments.v;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScopeService scopeService;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username);
        if(user == null)
            throw new UsernameNotFoundException("No user found with username '" + username + "'");

        return user;
    }

    public User findByID(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(String username, String encryptedPassword) throws UsernameAlreadyExistsException {

        if(userRepository.findByUsername(username) != null)
            throw new UsernameAlreadyExistsException(username);

        final User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setScopes(scopeService.getDefaultScopes());
        return userRepository.save(user);
    }

    public User updateUser(int userID, User updatedUser) throws UserNotFoundException, UsernameAlreadyExistsException {

        if(updatedUser == null)
            throw new IllegalArgumentException("Cannot update using a null user!");

        final User toUpdate = userRepository.findById(userID).orElse(null);
        if(toUpdate == null)
            throw new UserNotFoundException(userID);

        //
        // Update the user's username, if necessary.
        if(!toUpdate.getUsername().equals(updatedUser.getUsername())) {
            if (userRepository.findByUsername(updatedUser.getUsername()) != null)
                throw new UsernameAlreadyExistsException(updatedUser.getUsername());

            LOG.debug("Updating user #{}: {} -> {}", v("userID", userID), kv("username", toUpdate.getUsername()), v("newUsername", updatedUser.getUsername()));
            toUpdate.setUsername(updatedUser.getUsername());
        }

        // TODO: add other updates as we add updatable fields (not password or scopes, those get separate methods)

        return userRepository.save(toUpdate);
    }

    public void changePassword(int userID, String encodedNewPassword) throws UserNotFoundException {

        final User toUpdate = userRepository.findById(userID).orElse(null);
        if(toUpdate == null)
            throw new UserNotFoundException(userID);

        toUpdate.setPassword(encodedNewPassword);
        userRepository.save(toUpdate);
    }

    public void changeUserScopes(int toUpdateID, Collection<String> add, Collection<String> remove) throws UserNotFoundException {

        final User toUpdate = userRepository.findById(toUpdateID).orElse(null);
        if(toUpdate == null)
            throw new UserNotFoundException(toUpdateID);

        boolean modified = false;
        if(add != null) {
            LOG.info("Adding scopes to user", kv("userID", toUpdateID), kv("scopes", add));
            modified = add.stream()
                    .map(scopeService::findByName)
                    .filter(Objects::nonNull)
                    .filter(s -> !toUpdate.getScopes().contains(s))
                    .peek(s -> LOG.debug("Adding scope {} to user #{}", v("scope", s.getName()), v("userID", toUpdateID)))
                    .peek(s -> toUpdate.getScopes().add(s))
                    .findAny().isPresent();
        }

        if(remove != null) {
            LOG.info("Removing scopes from user", kv("userID", toUpdateID), kv("scopes", remove));
            modified |= remove.stream()
                    .map(scopeService::findByName)
                    .filter(Objects::nonNull)
                    .filter(s -> toUpdate.getScopes().contains(s))
                    .peek(s -> LOG.debug("Removing scope {} to user #{}", v("scope", s.getName()), v("userID", toUpdateID)))
                    .peek(s -> toUpdate.getScopes().remove(s))
                    .findAny().isPresent();
        }

        if(modified)
            userRepository.save(toUpdate);
    }

    public void deleteUser(int userID) throws UserNotFoundException {

        final User toDelete = userRepository.findById(userID).orElse(null);
        if(toDelete == null)
            throw new UserNotFoundException(userID);

        userRepository.delete(toDelete);
    }

    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Username already exists.")
    public static class UsernameAlreadyExistsException extends Exception {
        public UsernameAlreadyExistsException(String username) {
            super("Username '" + username + "' already exists!");
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found.")
    public static class UserNotFoundException extends Exception {
        public UserNotFoundException(int userID) {
            super("No user found with ID '" + userID + "'!");
        }
    }
}
