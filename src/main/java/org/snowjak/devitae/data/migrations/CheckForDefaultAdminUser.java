package org.snowjak.devitae.data.migrations;

import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.h2.command.Prepared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;

@Component
public class CheckForDefaultAdminUser extends BaseCallback {

    private static final Logger LOG = LoggerFactory.getLogger(CheckForDefaultAdminUser.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${ADMIN_PASSWORD}")
    private String defaultAdminPassword = "admin";

    @Override
    public void handle(Event event, Context context) {

        if(event == Event.AFTER_MIGRATE)
            try {

                LOG.info("Checking for default admin user ...");

                final PreparedStatement checkForUserExistence = context.getConnection()
                        .prepareStatement("SELECT COUNT(*) FROM `Users` WHERE `username` = 'admin'");
                final ResultSet checkResults = checkForUserExistence.executeQuery();
                if(checkResults.next() && checkResults.getInt(1) != 0) {
                    LOG.info("Default admin user already exists. No further action required.");
                    return;
                } else
                    checkResults.close();

                LOG.info("Default admin user does not exist. Creating ...");

                LOG.debug("Creating default admin user record ...");
                final PreparedStatement createUserStatement = context.getConnection()
                        .prepareStatement("INSERT INTO `Users` ( `version`, `username`, `password`, `created` ) VALUES (1, ?, ?, ?)");
                createUserStatement.setString(1, "admin");
                createUserStatement.setString(2, passwordEncoder.encode(defaultAdminPassword));
                createUserStatement.setTimestamp(3, java.sql.Timestamp.from(Instant.now()));
                createUserStatement.execute();

                LOG.debug("Adding all scopes to default admin user ...");
                final PreparedStatement addAllScopesToUserStatement = context.getConnection()
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
