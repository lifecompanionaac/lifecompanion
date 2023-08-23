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
package org.lifecompanion.model.impl.useapi;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.useapi.CommandLineArgumentI;

import java.util.stream.Stream;

public enum CommandLineArgumentEnum implements CommandLineArgumentI {
    DIRECT_LAUNCH_CONFIGURATION("directLaunchOn",
            "profileId configurationId",
            "Try to launch LifeCompanion directly in use mode on a given profile and configuration combination. Profile and configuration should have already been loaded in LifeCompanion on a previous launch.",
            "4aab2626-6b72-4e5e-8318-777c3684e8a3 9e94f3c0-e2de-4afb-8b65-8b07a994b3d4",
            2),

    DIRECT_IMPORT_AND_LAUNCH_CONFIGURATION("directImportAndLaunch",
            "configurationFilePath",
            "Try to import a configuration file and launch it directly in use mode. The given configuration will not be added to profile. This can be useful to run LifeCompanion as a \"configuration reader only\"",
            "C:\\lifecompanion\\my-configuration.lcc",
            1),
    DISABLE_SWITCH_TO_EDIT_MODE("disableSwitchToEditMode",
            null,
            "Disable the switch to edit mode when the use mode is launched. This will hide the edit mode button and disable keyboard shortcuts or any action that could cause a switch to edit mode. Note that this doesn't disable the edit mode itself : on the first launch, LifeCompanion can be used in edit mode.",
            null,
            0);

    private final String name, parameters, description, parametersExample;
    private final int expectedParameterCount;

    CommandLineArgumentEnum(String name, String parameters, String description, String parametersExample, int expectedParameterCount) {
        this.name = name;
        this.parameters = parameters;
        this.description = description;
        this.parametersExample = parametersExample;
        this.expectedParameterCount = expectedParameterCount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParameters() {
        return parameters;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getParametersExample() {
        return parametersExample;
    }

    @Override
    public int getExpectedParameterCount() {
        return expectedParameterCount;
    }

    @Override
    public String getMarkdownDocumentation() {
        return "|`-" + getName() + (StringUtils.isNotBlank(getParameters()) ? (" " + getParameters()) : "") + "`|" + (StringUtils.isNotBlank(getParametersExample()) ? ("`" + getParametersExample() + "`") : "*`NONE`*") + "|" + getDescription() + "|\n";
    }

    public static String getAllMarkdownDocumentation() {
        StringBuilder all = new StringBuilder();
        all.append("|Argument|Parameters|Description|\n|-|-|-|\n");
        Stream.of(values()).map(CommandLineArgumentEnum::getMarkdownDocumentation).forEach(all::append);
        return all.toString();
    }
}
