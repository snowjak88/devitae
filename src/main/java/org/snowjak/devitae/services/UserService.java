package org.snowjak.devitae.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snowjak.devitae.data.entities.Scope;
import org.snowjak.devitae.data.entities.User;
import org.snowjak.devitae.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.argument.StructuredArguments.v;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    /**
     * Usernames must not match this regex.
     */
    public static final String INVALID_USERNAME_CHARACTERS_REGEX = "([^a-zA-Z0-9_\\-])";
    public static final Pattern INVALID_USERNAME = Pattern.compile(INVALID_USERNAME_CHARACTERS_REGEX);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScopeService scopeService;

    private void checkForInvalidUsernameCharacters(String username) throws UsernameContainsInvalidCharactersException {
        final Matcher invalidUsernameMatcher = INVALID_USERNAME.matcher(username);
        if(invalidUsernameMatcher.find()) {
            final String invalidCharacters = invalidUsernameMatcher.group(1).replaceAll(" ", "[space]");
            throw new UsernameContainsInvalidCharactersException(username, invalidCharacters);
        }
    }

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

    public User createUser(String username, String encryptedPassword) throws UsernameAlreadyExistsException, UsernameContainsInvalidCharactersException {

        checkForInvalidUsernameCharacters(username);

        if(userRepository.findByUsername(username) != null)
            throw new UsernameAlreadyExistsException(username);

        final User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setScopes(scopeService.getDefaultScopes());
        return userRepository.save(user);
    }

    public User updateUser(int userID, User updatedUser) throws UserNotFoundException, UsernameAlreadyExistsException, UsernameContainsInvalidCharactersException {

        if(updatedUser == null)
            throw new IllegalArgumentException("Cannot update using a null user!");

        final User toUpdate = userRepository.findById(userID).orElse(null);
        if(toUpdate == null)
            throw new UserNotFoundException(userID);

        //
        // Update the user's username, if necessary.
        if(!toUpdate.getUsername().equals(updatedUser.getUsername())) {
            checkForInvalidUsernameCharacters(updatedUser.getUsername());

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

    public void changeUserScopes(int toUpdateID, Collection<String> newScopeNames) throws UserNotFoundException {
        final Collection<Scope> newScopes = newScopeNames.stream()
                .map(scopeService::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final User toUpdate = userRepository.findById(toUpdateID).orElse(null);
        if(toUpdate == null)
            throw new UserNotFoundException(toUpdateID);

        final Set<Scope> adding = newScopes.stream().filter(s -> !toUpdate.getScopes().contains(s)).collect(Collectors.toSet());
        final Set<Scope> removing = toUpdate.getScopes().stream().filter(s -> !newScopes.contains(s)).collect(Collectors.toSet());

        if(!adding.isEmpty()) {
            adding.stream()
                    .peek(s -> LOG.debug("Adding scope {} to user #{}", v("scope", s.getName()), v("userID", toUpdateID)))
                    .peek(s -> toUpdate.getScopes().add(s));
        }

        if(!removing.isEmpty()) {
            removing.stream()
                    .peek(s -> LOG.debug("Removing scope {} to user #{}", v("scope", s.getName()), v("userID", toUpdateID)))
                    .peek(s -> toUpdate.getScopes().remove(s));
        }

        if(!adding.isEmpty() && !removing.isEmpty())
            userRepository.save(toUpdate);
    }

    public void deleteUser(int userID) throws UserNotFoundException {

        final User toDelete = userRepository.findById(userID).orElse(null);
        if(toDelete == null)
            throw new UserNotFoundException(userID);

        userRepository.delete(toDelete);
    }

    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Username contains invalid characters")
    public static class UsernameContainsInvalidCharactersException extends Exception {
        public UsernameContainsInvalidCharactersException(String username, String invalidCharacters) {
            super(String.format("Username '%s' contains invalid characters ('%s')", username, invalidCharacters));
        }
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
