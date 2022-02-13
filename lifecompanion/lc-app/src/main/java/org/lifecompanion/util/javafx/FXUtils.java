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

package org.lifecompanion.util.javafx;

import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;

public class FXUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FXUtils.class);

    public static Bounds getTextBounds(final String text, final Font font) {
        Text t = new Text(text);
        t.setFont(font);
        return t.getLayoutBounds();
    }

    public static void setFixedWidth(Region region, double width) {
        region.setMinWidth(width);
        region.setPrefWidth(width);
        region.setMaxWidth(width);
    }

    public static void setFixedHeight(Region region, double height) {
        region.setMinHeight(height);
        region.setPrefHeight(height);
        region.setMaxHeight(height);
    }

    public static void setFixedSize(Region region, double width, double height) {
        setFixedWidth(region, width);
        setFixedHeight(region, height);
    }

    public static Window getSourceWindow(Node source) {
        if (source != null) {
            if (source.getScene() != null) {
                Window window = source.getScene().getWindow();
                // FIXME : should test if visible and not null and fall back to known windows...
                return window;
            }
        }
        return null;
    }

    public static Node getSourceFromEvent(Event event) {
        if (event != null) {
            if (event.getSource() instanceof Node) {
                return (Node) event.getSource();
            } else if (event.getTarget() instanceof Node) {
                return (Node) event.getTarget();
            }
        }
        return AppModeController.INSTANCE.getEditModeContext().getStage().getScene().getRoot();
    }

    public static void applyPerformanceConfiguration(final Node node) {
        node.setCache(LCGraphicStyle.ENABLE_NODE_CACHE_CONFIG_MODE);
        node.setCacheHint(LCGraphicStyle.CACHE_HINT_CONFIG_MODE);
    }

}
