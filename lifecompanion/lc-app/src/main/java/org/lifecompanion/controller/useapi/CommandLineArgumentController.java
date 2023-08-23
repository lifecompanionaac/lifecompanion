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
import org.lifecompanion.model.impl.useapi.CommandLineArgumentEnum;
import org.lifecompanion.util.LangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CommandLineArgumentController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineArgumentController.class);

    private Map<CommandLineArgumentEnum, List<String>> args;

    CommandLineArgumentController() {
    }

    public void initArgs(List<String> argsCollection) {
        if (args != null) throw new IllegalStateException("Can't init command line args twice");
        args = new HashMap<>();

        // Detect and analyze
        for (CommandLineArgumentEnum useApiArg : CommandLineArgumentEnum.values()) {
            final int indexOfArg = argsCollection.indexOf("-" + useApiArg.getName());
            if (indexOfArg >= 0) {
                // Remove arg
                argsCollection.remove(indexOfArg);
                // Try to get args if possible
                if (useApiArg.getExpectedParameterCount() > 0) {
                    if (indexOfArg + (useApiArg.getExpectedParameterCount() - 1) < argsCollection.size()) {
                        List<String> paramForCurrent = new ArrayList<>();
                        for (int i = indexOfArg; i < useApiArg.getExpectedParameterCount(); i++) {
                            paramForCurrent.add(argsCollection.remove(indexOfArg));
                        }
                        if (args.containsKey(useApiArg)) {
                            LOGGER.error("Invalid arg {} detected, found it with parameters {} but it was already in the arg list (with parameters {})",
                                    useApiArg.getName(),
                                    paramForCurrent,
                                    args.get(useApiArg));
                        } else {
                            LOGGER.info("Valid arg {} detected (parameter : {})", useApiArg.getName(), paramForCurrent);
                            args.put(useApiArg, paramForCurrent);
                        }
                    } else {
                        LOGGER.error("Invalid arg {} detected, expected {} parameters but didn't find them", useApiArg.getName(), useApiArg.getExpectedParameterCount());
                    }
                } else {
                    LOGGER.info("Valid arg {} detected (no parameter)", useApiArg.getName());
                    args.put(useApiArg, new ArrayList<>());
                }
            }
        }

        // Check for parameters left that could match the arg format
        argsCollection.stream().filter(s -> StringUtils.startWithIgnoreCase(s, "-")).forEach(arg -> {
            LOGGER.warn("Invalid arg {} detected, it doesn't match any of the available args, check the docs !", arg);
        });

        if (LangUtils.safeParseBoolean(System.getProperty("org.lifecompanion.debug.dev.env"))) {
            System.out.println(CommandLineArgumentEnum.getAllMarkdownDocumentation());
        }
    }

    public boolean isPresent(CommandLineArgumentEnum arg) {
        return args.containsKey(arg);
    }

    public List<String> getFollowingArgs(CommandLineArgumentEnum arg) {
        return args.get(arg);
    }
}
