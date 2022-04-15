package org.snowjak.devitae.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;

@Component
public class CheckForAdminUserOnStartup implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(CheckForAdminUserOnStartup.class);

    @Value("${ADMIN_PASSWORD}")
    private String defaultAdminPassword = "admin";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        try {

            LOG.info("Checking for default admin user ...");

            final PreparedStatement checkForUserExistence = dataSource.getConnection()
                    .prepareStatement("SELECT COUNT(*) FROM `Users` WHERE `username` = 'admin'");
            final ResultSet checkResults = checkForUserExistence.executeQuery();
            if(checkResults.next() && checkResults.getInt(1) != 0) {
                LOG.info("Default admin user already exists. No further action required.");
                return;
            } else
                checkResults.close();

            LOG.info("Default admin user does not exist. Creating ...");

            LOG.debug("Creating default admin user record ...");
            final PreparedStatement createUserStatement = dataSource.getConnection()
                    .prepareStatement("INSERT INTO `Users` ( `version`, `username`, `password`, `created` ) VALUES (1, ?, ?, ?)");
            createUserStatement.setString(1, "admin");
            createUserStatement.setString(2, passwordEncoder.encode(defaultAdminPassword));
            createUserStatement.setTimestamp(3, java.sql.Timestamp.from(Instant.now()));
            createUserStatement.execute();

            LOG.debug("Adding all scopes to default admin user ...");
            final PreparedStatement addAllScopesToUserStatement = dataSource.getConnection()
                    .prepareStatement("INSERT INTO `User_Scopes` ( `userID`, `scopeID` ) SELECT u.`id`, s.`id` FROM `Users` u, `Scopes` s WHERE u.`username` = ?");
            addAllScopesToUserStatement.setString(1, "admin");
            addAllScopesToUserStatement.execute();

            LOG.info("Default admin user created.");

        } catch (Exception e) {
            LOG.error("Failed while checking for default admin user.", e);
            throw new RuntimeException(e);
        }

    }
}
