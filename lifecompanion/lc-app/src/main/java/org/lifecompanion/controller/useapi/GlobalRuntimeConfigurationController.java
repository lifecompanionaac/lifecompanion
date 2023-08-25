/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2024 CMRRF KERPAPE (Lorient, France)
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
package org.lifecompanion.controller.useapi;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.useapi.GlobalRuntimeConfigurationType;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum GlobalRuntimeConfigurationController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRuntimeConfigurationController.class);

    private Map<GlobalRuntimeConfiguration, List<String>> globalRuntimeConfigurations;

    GlobalRuntimeConfigurationController() {
    }

    public void init(List<String> argsCollection) {
        if (globalRuntimeConfigurations != null) {
            throw new IllegalStateException("Can't init global runtime configuration twice");
        }
        globalRuntimeConfigurations = new HashMap<>();
        detectJavaProperties();
        detectCommandLine(argsCollection);

        if (isPresent(GlobalRuntimeConfiguration.PROP_DEV_MODE)) {
            for (GlobalRuntimeConfigurationType type : GlobalRuntimeConfigurationType.values()) {
                LOGGER.info("{} documentation\n{}", type, GlobalRuntimeConfiguration.getMarkdownDocumentation(type));
            }
        }
    }

    private void detectJavaProperties() {
        // Properties
        LOGGER.info("Initialize properties configuration");
        for (GlobalRuntimeConfiguration globalRuntimeConfiguration : GlobalRuntimeConfiguration.getAll(GlobalRuntimeConfigurationType.JAVA_PROPERTY)) {
            String propValue = System.getProperty(globalRuntimeConfiguration.getName());
            if (propValue != null) {
                if (globalRuntimeConfiguration.getExpectedParameterCount() <= 0) {
                    LOGGER.info("Valid property {} detected (no parameter)", globalRuntimeConfiguration.getName());
                    this.globalRuntimeConfigurations.put(globalRuntimeConfiguration, List.of());
                } else if (StringUtils.isNotBlank(propValue)) {
                    LOGGER.info("Valid property {} detected (parameter : {})", globalRuntimeConfiguration.getName(), globalRuntimeConfiguration.isSecuredParameters() ? "*****" : propValue);
                    this.globalRuntimeConfigurations.put(globalRuntimeConfiguration, List.of(propValue));
                } else {
                    LOGGER.error("Invalid property {} detected, found it with not parameters !", globalRuntimeConfiguration.getName());
                }
            }
        }
    }

    private void detectCommandLine(List<String> argsCollection) {
        // TODO : ignore case on detected args

        // Command line arguments
        LOGGER.info("Initialize command line arguments");
        for (GlobalRuntimeConfiguration globalRuntimeConfiguration : GlobalRuntimeConfiguration.getAll(GlobalRuntimeConfigurationType.COMMAND_LINE)) {
            final int indexOfArg = argsCollection.indexOf("-" + globalRuntimeConfiguration.getName());
            if (indexOfArg >= 0) {
                // Remove arg
                argsCollection.remove(indexOfArg);
                // Try to get args if possible
                if (globalRuntimeConfiguration.getExpectedParameterCount() > 0) {
                    if (indexOfArg + (globalRuntimeConfiguration.getExpectedParameterCount() - 1) < argsCollection.size()) {
                        List<String> paramForCurrent = new ArrayList<>();
                        for (int i = 0; i < globalRuntimeConfiguration.getExpectedParameterCount(); i++) {
                            paramForCurrent.add(argsCollection.remove(indexOfArg));
                        }
                        if (this.globalRuntimeConfigurations.containsKey(globalRuntimeConfiguration)) {
                            LOGGER.error("Invalid arg {} detected, found it with parameters {} but it was already in the arg list (with parameters {})",
                                    globalRuntimeConfiguration.getName(),
                                    globalRuntimeConfiguration.isSecuredParameters() ? "*****" : paramForCurrent,
                                    globalRuntimeConfiguration.isSecuredParameters() ? "*****" : this.globalRuntimeConfigurations.get(globalRuntimeConfiguration)
                            );
                        } else {
                            LOGGER.info("Valid arg {} detected (parameter : {})", globalRuntimeConfiguration.getName(), globalRuntimeConfiguration.isSecuredParameters() ? "*****" : paramForCurrent);
                            this.globalRuntimeConfigurations.put(globalRuntimeConfiguration, paramForCurrent);
                        }
                    } else {
                        LOGGER.error("Invalid arg {} detected, expected {} parameters but didn't find them",
                                globalRuntimeConfiguration.getName(),
                                globalRuntimeConfiguration.getExpectedParameterCount()
                        );
                    }
                } else {
                    LOGGER.info("Valid arg {} detected (no parameter)", globalRuntimeConfiguration.getName());
                    this.globalRuntimeConfigurations.put(globalRuntimeConfiguration, new ArrayList<>());
                }
            }
        }
        // Check for parameters left that could match the arg format
        argsCollection.stream()
                .filter(s -> StringUtils.startWithIgnoreCase(s, "-"))
                .forEach(arg -> {
                    LOGGER.warn("Invalid arg {} detected, it doesn't match any of the available args, check the docs !", arg);
                });
    }

    public boolean isPresent(GlobalRuntimeConfiguration globalRuntimeConfiguration) {
        return globalRuntimeConfigurations.containsKey(globalRuntimeConfiguration);
    }

    public void removeConfiguration(GlobalRuntimeConfiguration globalRuntimeConfiguration) {
        this.globalRuntimeConfigurations.remove(globalRuntimeConfiguration);
    }

    public List<String> getParameters(GlobalRuntimeConfiguration globalRuntimeConfiguration) {
        return globalRuntimeConfigurations.get(globalRuntimeConfiguration);
    }

    public String getParameter(GlobalRuntimeConfiguration globalRuntimeConfiguration) {
        return globalRuntimeConfigurations.get(globalRuntimeConfiguration)
                .get(0);
    }
}
