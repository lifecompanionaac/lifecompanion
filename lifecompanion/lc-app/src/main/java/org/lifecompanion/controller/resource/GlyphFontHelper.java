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

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Class for glyph font.<br>
 * This is done to avoid ControlFX usage in base api.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GlyphFontHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(GlyphFontHelper.class);

    // Class part : "Icon font"
    //========================================================================
    public static GlyphFont FONT_AWESOME;
    public static GlyphFont FONT_MATERIAL;

    /**
     * Load the web font.
     */
    public static void loadFont() {
        //Override the default ControlFX FontAwesome : local font is an up-to-date offline version
        try {
            try (InputStream fais = ResourceHelper.getInputStreamForPath("/font/fontawesome-webfont.ttf")) {
                GlyphFontRegistry.register("FontAwesome", fais, 14);
                GlyphFontHelper.FONT_AWESOME = GlyphFontRegistry.font("FontAwesome");
                GlyphFontHelper.FONT_AWESOME.registerAll(Arrays.asList(FontAwesome.Glyph.values()));
            }
            //Allow Enum usage
            try (InputStream miis = ResourceHelper.getInputStreamForPath("/font/MaterialIcons-Regular.ttf")) {
                GlyphFontRegistry.register("Material Icons", miis, 14);
                GlyphFontHelper.FONT_MATERIAL = GlyphFontRegistry.font("Material Icons");
            }
            GlyphFontHelper.LOGGER.info("Icon fonts loaded");
        } catch (IOException e) {
            LOGGER.error("Error when loading icon fonts", e);
        }
    }
    //========================================================================
}
