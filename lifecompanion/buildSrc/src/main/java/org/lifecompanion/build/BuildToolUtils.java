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

package org.lifecompanion.build;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BuildToolUtils {
    private static final Logger LOGGER = Logging.getLogger(BuildToolUtils.class);

    public static String getServerURL(Project project) {
        Properties envProperties = getEnvProperties(project);
        return envProperties.getProperty("lifecompanion.framework.server.url");
    }

    public static void loginOnServerOrFail(AppServerClient appServerClient, Project project) {
        Properties envProperties = getEnvProperties(project);
        boolean login = appServerClient.login(envProperties.getProperty("lifecompanion.framework.server.login"), envProperties.getProperty("lifecompanion.framework.server.password"));
        if (!login) {
            throw new IllegalStateException("Can't connect to update server");
        }
    }

    public static String getEnvValueLowerCase(Project project) {
        return project.hasProperty("env") ? String.valueOf(project.property("env")).toLowerCase() : "local";
    }

    public static String getEnvVarOrDefault(String varName, String defaultValue) {
        String val = System.getenv(varName);
        return val == null || val.isBlank() ? defaultValue : val;
    }

    public static String checkAndGetProperty(Project project, String propName) {
        if (!project.hasProperty(propName)) {
            throw new IllegalArgumentException("Gradle property \"" + propName + "\" not set, you should check documentation");
        }
        return String.valueOf(project.property(propName));
    }

    public static void injectEnvPropertiesToProject(Project project) {
        Properties envProperties = getEnvProperties(project);
        envProperties.forEach((key, value) -> project.getExtensions().getExtraProperties().set((String) key, value));
    }

    private static Properties getEnvProperties(Project project) {
        final String env = getEnvValueLowerCase(project);
        File envFile = new File(project.getRootDir() + "/../env/.env." + env);
        File exampleEnvFile = new File(project.getRootDir() + "/../env/.env.example");
        if (envFile.exists()) {
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(envFile)) {
                properties.load(fis);
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid example file : " + envFile.getPath() + ", check \".env.example\" as reference / " + e.getClass().getSimpleName() + " : " + e.getMessage());
            }
            return properties;
        } else {
            LOGGER.lifecycle("No env ({}) configuration file found (expecting file at {})", env, envFile.getAbsolutePath());
            if ("local".equals(env) && exampleEnvFile.exists()) {
                LOGGER.lifecycle("Will try to initialize local environment from example file located at {}", exampleEnvFile.getAbsolutePath());
                try {
                    IOUtils.copyFiles(exampleEnvFile, envFile);
                } catch (IOException ignored) {
                }
                return getEnvProperties(project);
            } else {
                throw new IllegalArgumentException("No build configuration file found ! Expecting " + envFile.getAbsolutePath() + " to exist");
            }
        }
    }
}
