package org.snowjak.devitae.data.migrations;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.time.Instant;

@Component
public class V1_1__CreateDefaultAdminMigration extends BaseJavaMigration {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void migrate(Context context) throws Exception {

        try {

            final PreparedStatement createUserStatement = context.getConnection()
                    .prepareStatement("INSERT INTO `Users` ( `version`, `username`, `password`, `created` ) VALUES (1, ?, ?, ?)");
            createUserStatement.setString(1, "admin");
            createUserStatement.setString(2, passwordEncoder.encode("admin"));
            createUserStatement.setTimestamp(3, java.sql.Timestamp.from(Instant.now()));
            createUserStatement.execute();

            final PreparedStatement addAllScopesToUserStatement = context.getConnection()
                    .prepareStatement("INSERT INTO `User_Scopes` ( `userID`, `scopeID` ) SELECT u.`id`, s.`id` FROM `Users` u, `Scopes` s WHERE u.`username` = ?");
            addAllScopesToUserStatement.setString(1, "admin");
            addAllScopesToUserStatement.execute();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
