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

package org.lifecompanion.util;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.Set;

/**
 * Implementation note : all Destkop calls are delegated to a daemon Thread.<br>
 * <strong>This is mandatory for Unix implementation</strong> : if the caller is on main/FX Thread on Unix systems, Destkop open/browse calls will fail and block calling Thread...
 */
public class DesktopUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DesktopUtils.class);

    private static final Set<String> URI_SCHEMES = Set.of("http", "https", "ftp", "file");

    public static boolean openUrlInDefaultBrowser(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            final String validatedUri;
            if (URI_SCHEMES.stream().filter(scheme -> StringUtils.startWithIgnoreCase(url, scheme + "://")).findAny().isEmpty()) {
                LOGGER.warn("The given url to open \"{}\" didn't have any valid {} scheme, will automatically add it", url, URI_SCHEMES);
                validatedUri = "https://" + url;
            } else validatedUri = url;
            LCNamedThreadFactory.daemonThreadFactory("DesktopUtils").newThread(() -> {
                try {
                    desktop.browse(new URI(validatedUri));
                } catch (Exception e) {
                    LOGGER.warn("Couldn't open default browser to {}", validatedUri, e);
                }
            }).start();
            return true;
        }
        return false;
    }

    public static boolean openFile(File path) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (path != null && path.exists() && desktop != null && desktop.isSupported(Desktop.Action.OPEN)) {
            LCNamedThreadFactory.daemonThreadFactory("DesktopUtils").newThread(() -> {
                try {
                    desktop.open(path);
                } catch (Exception e) {
                    LOGGER.warn("Couldn't open file with system from {}", path, e);
                }
            }).start();
            return true;
        }
        return false;
    }
}
