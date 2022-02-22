/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lifecompanion.framework.server.data.dao;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.tech.DatabaseMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public enum DataSource {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);

    private Sql2o sql2o;

    private static final String[] MIGRATIONS_SCRIPT_NAMES = {
            "0_CREATE.sql",
            "2_PLUGINS.sql",
            "3_SOFTWARE_STATS.sql",
            "4_SOFTWARE_STATS_TRY_COUNT.sql",
            "5_PLUGIN_UPDATE_APP_VERSION.sql",
            "6_APPLICATION_UPDATE_API_VERSION.sql"
    };

    DataSource() {
        try {
            if (!org.postgresql.Driver.isRegistered()) {
                org.postgresql.Driver.register();
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't find or register DB driver", e);
        }
        String databaseUrl;
        String databaseUrlFromProp = System.getProperty("org.lifecompanion.framework.server.dev.database.url");
        if (StringUtils.isNotBlank(databaseUrlFromProp)) {
            LOGGER.info("Database URL got from system properties");
            databaseUrl = databaseUrlFromProp;
        } else {
            LOGGER.info("No database URL found in \"org.lifecompanion.framework.server.dev.database.url\" property, will check \"DATABASE_URL\" env var");
            databaseUrl = System.getenv("DATABASE_URL");
        }
        URI databaseUri = URI.create(databaseUrl);
        String[] userInfo = databaseUri.getUserInfo().split(":");

        sql2o = new Sql2o("jdbc:postgresql://" + databaseUri.getHost() + ':' + databaseUri.getPort() + databaseUri.getPath(), userInfo[0], userInfo[1]);
        LOGGER.info("Datasource initialized");
    }

    public Sql2o getSql2o() {
        return sql2o;
    }

    public void checkDatabaseMigrations() {
        LOGGER.info("Checking for database migration...");
        List<DatabaseMigration> previousMigration;
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            try {
                previousMigration = connection.createQuery("SELECT * FROM database_migration")//
                        .executeAndFetch(DatabaseMigration.class);
            } catch (Sql2oException e) {
                LOGGER.warn("Couldn't find previous database migration, will use empty previous migration", e);
                previousMigration = new ArrayList<>();
            }
        }
        LOGGER.info("Found {} previous successful migration", previousMigration.size());

        // Create the set of already successful scripts
        Set<String> alreadySuccessfulScripts = previousMigration.stream().map(DatabaseMigration::getScriptName).collect(Collectors.toSet());

        // Get the migrations scripts
        List<String> scriptToMigrateNames = Arrays.stream(MIGRATIONS_SCRIPT_NAMES).filter(name -> !alreadySuccessfulScripts.contains(name)).sorted(Comparator.comparingInt(this::getScriptIndex)).collect(Collectors.toList());
        LOGGER.info("Found {} scripts to be executed for migration", scriptToMigrateNames.size());

        // Run all the migration script in a transaction
        try (Connection connection = sql2o.beginTransaction()) {
            for (String migrationScriptName : scriptToMigrateNames) {
                LOGGER.info("Try to execute the following migration script : {}", migrationScriptName);
                ScriptRunner scriptRunner = new ScriptRunner(connection.getJdbcConnection());
                scriptRunner.setStopOnError(true);
                scriptRunner.setAutoCommit(false);
                try (Reader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/sql/migrations/" + migrationScriptName)))) {
                    scriptRunner.runScript(reader);
                }
                // Script executed, insert to DB
                connection.createQuery(
                        "INSERT INTO database_migration(id,script_name,script_date)"//
                                + " VALUES "//
                                + "(:id,:scriptName,:scriptDate)")//
                        .bind(new DatabaseMigration(UUID.randomUUID().toString(), migrationScriptName, new Date()))//
                        .executeUpdate();
            }
            connection.commit();
        } catch (Throwable t) {
            LOGGER.error("Error in migration script, end JVM", t);
            System.exit(-1);
        }
    }

    private int getScriptIndex(String name) {
        return Integer.parseInt(name.substring(0, name.indexOf("_")));
    }
}
