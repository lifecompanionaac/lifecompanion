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

import org.lifecompanion.model.api.useapi.UseApiArgI;

import java.util.stream.Stream;

public enum UseApiArgEnum implements UseApiArgI {
    DIRECT_LAUNCH_CONFIGURATION("directLaunchOn",
            "profileId configurationId",
            "Try to launch LifeCompanion directly in use mode on a given profile and configuration combination. Profile and configuration should have already been loaded in LifeCompanion on a previous launch.",
            "4aab2626-6b72-4e5e-8318-777c3684e8a3 9e94f3c0-e2de-4afb-8b65-8b07a994b3d4",
            2),

    DIRECT_IMPORT_AND_LAUNCH_CONFIGURATION("directImportAndLaunch",
            "configurationFilePath",
            "Try to import a configuration file and launch it directly in use mode. The given configuration will not be added to profile. This can be useful to run LifeCompanion as a \"configuration reader only\"",
            "C:\\lifecompanion\\my-configuration.lcc",
            1);

    private final String name, argType, markdownDescription, argExample;
    private final int expectedArgCount;

    UseApiArgEnum(String name, String argType, String markdownDescription, String argExample, int expectedArgCount) {
        this.name = name;
        this.argType = argType;
        this.markdownDescription = markdownDescription;
        this.argExample = argExample;
        this.expectedArgCount = expectedArgCount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getArgType() {
        return argType;
    }

    @Override
    public String getMarkdownDescription() {
        return markdownDescription;
    }

    @Override
    public String getArgExample() {
        return argExample;
    }

    @Override
    public int getExpectedArgCount() {
        return expectedArgCount;
    }

    @Override
    public String getMarkdownDocumentation() {
        return "|`-" + getName() + " " + getArgType() + "`|`" + getArgExample() + "`|" + getMarkdownDescription() + "|\n";
    }

    public static String getAllMarkdownDocumentation() {
        StringBuilder all = new StringBuilder();
        all.append("|Argument|Parameters|Description|\n|-|-|-|\n");
        Stream.of(values()).map(UseApiArgEnum::getMarkdownDocumentation).forEach(all::append);
        return all.toString();
    }
}
