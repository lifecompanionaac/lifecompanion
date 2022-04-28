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
package org.lifecompanion.model.impl.constant;

import javafx.scene.CacheHint;
import javafx.scene.paint.Color;

/**
 * Class that keep values to the same global style to application.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCGraphicStyle {
    // Class part : "Durations"
    //========================================================================
    public final static long BRIEF_NOTIFICATION_DURATION_MS = 1750;
    public final static long SHORT_NOTIFICATION_DURATION_MS = 3_000;
    public final static long MEDIUM_NOTIFICATION_DURATION_MS = 6_000L;
    public final static long LONG_NOTIFICATION_DURATION_MS = 10_000L;
    public final static long BLOCKING_TASK_NOTIFICATION_DURATION_THRESHOLD = 500;
    //========================================================================

    // SYSTEM VALUES
    //========================================================================
    public final static double STAGE_TITLE_BAR_HEIGHT = 50.0;
    //========================================================================

    // DEFAULT STAGE SIZE
    //========================================================================
    public static double DEFAULT_TOOL_STAGE_WIDTH = 1050, DEFAULT_TOOL_STAGE_HEIGHT = 720;
    public static boolean TOOL_STAGE_RESIZABLE = true;
    //========================================================================

    // Class part : "Option size"
    //========================================================================
    public final static double SELECTED_STROKE_GAP = 1;
    public final static double VIEWPORT_COMPONENT_SIZE = 4;
    public final static double MAX_TOOLTIP_WIDTH = 200.0;
    public final static double MAX_ZOOM_VALUE = 1.95, MIN_ZOOM_VALUE = 0.35;
    public final static double ZOOM_MODIFIER = 0.05;
    //========================================================================

    // Class part : "Colors"
    //========================================================================
    //Main color
    public final static Color MAIN_PRIMARY = Color.web("#0395f4", 1);
    public final static Color MAIN_LIGHT = Color.web("#03bdf4", 1);
    public final static Color MAIN_DARK = Color.web("#0277BD", 1);
    //Accent color
    public final static Color SECOND_PRIMARY = Color.web("#F44336", 1);
    public final static Color SECOND_LIGHT = Color.web("#EF9A9A", 1);
    public final static Color SECOND_DARK = Color.web("#C62828", 1);

    public final static Color THIRD_DARK = Color.web("#FF9800", 1);


    //Useful color
    public final static Color LC_WHITE = Color.web("#E6E6E6", 1);
    public final static Color LC_BLACK = Color.web("#2E2E2E", 1);
    public final static Color LC_WARNING_COLOR = Color.web("#FF7F50", 1);
    public final static Color LC_GRAY = Color.GRAY;
    public final static Color LC_VERY_LIGHT_GRAY = Color.web("#F6F6F6", 1);
    //========================================================================

    // Class part : "Performance configuration"
    //========================================================================
    public static final boolean ENABLE_NODE_CACHE_CONFIG_MODE = true;
    public static final CacheHint CACHE_HINT_CONFIG_MODE = CacheHint.SPEED;
    public static final boolean FORCE_INTEGER_RENDER_SCALE = true;
    //========================================================================

}
