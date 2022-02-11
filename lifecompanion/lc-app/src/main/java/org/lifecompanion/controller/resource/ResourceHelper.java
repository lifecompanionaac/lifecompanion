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

package org.lifecompanion.controller.resource;

import org.lifecompanion.controller.plugin.PluginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;


public class ResourceHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceHelper.class);

    // TODO : resource module name should be located in a constant file...
    private static final String MODULE_WITH_RESOURCES = "org.lifecompanion.app";

    public static InputStream getInputStreamForPath(String resourcePath) {
        Optional<Module> specificModule = ModuleLayer.boot().findModule(MODULE_WITH_RESOURCES);
        if (specificModule.isPresent()) {
            try {
                // Try to load resource from app module
                InputStream resourceAsStream = specificModule.get().getResourceAsStream(resourcePath);
                if (resourceAsStream != null) {
                    return resourceAsStream;
                }
                // Try to load resource from plugin (e.g. this load from traditional classpath)
                else {
                    resourceAsStream = PluginController.INSTANCE.getResourceFromPlugin(resourcePath);
                    if (resourceAsStream != null) {
                        return resourceAsStream;
                    }
                }
                throw new IllegalArgumentException("Didn't find the resource : " + resourcePath);
            } catch (IOException e) {
                LOGGER.error("Couldn't retrieve resource stream", e);
                throw new RuntimeException("Could not load resource from " + resourcePath, e);
            }
        } else {
            throw new IllegalStateException("Could not find the module " + MODULE_WITH_RESOURCES);
        }
    }

    private static void checkPath(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Resource path cannot be null");
        }
        if (path.contains("//")) {
            throw new IllegalArgumentException("Check your path as it contains a double / : \"" + path + "\"");
        }
    }
}


