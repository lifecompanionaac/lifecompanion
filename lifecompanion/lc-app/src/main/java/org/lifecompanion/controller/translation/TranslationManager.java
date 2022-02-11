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

package org.lifecompanion.controller.translation;

import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public enum TranslationManager {
    INSTANCE;

    private final static Logger LOGGER = LoggerFactory.getLogger(TranslationManager.class);

    public void loadLanguageResource(String code, String name) {
        String resourcePath = "/translation/" + code + name;
        try (InputStream inputStreamForPath = ResourceHelper.getInputStreamForPath(resourcePath)) {
            Translation.INSTANCE.load(resourcePath, inputStreamForPath);
            LOGGER.debug("Translation resource loaded : {} - {}", code, name);
        } catch (Exception e) {
            LOGGER.error("Couldn't not load translation resource : {} - {}", name, code, e);
        }
    }
}
