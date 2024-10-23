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
package org.lifecompanion.controller.configurationcomponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.WhiteboardKeyOption;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UseModeWhiteboardController implements ModeListenerI {
    INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(UseModeWhiteboardController.class);

    private List<WhiteboardKeyOption> whiteboardKeyOptionList;

    private final ObjectProperty<Color> drawingColor;
    private final ObjectProperty<WhiteboardTool> currentTool;
    private final SimpleDoubleProperty pencilSize;
    private final SimpleDoubleProperty eraserSize;

    UseModeWhiteboardController() {
        this.drawingColor = new SimpleObjectProperty<>(Color.DARKGRAY);
        this.currentTool = new SimpleObjectProperty<>(WhiteboardTool.PENCIL);
        this.pencilSize = new SimpleDoubleProperty(10);
        this.eraserSize = new SimpleDoubleProperty(30.0);
    }

    public ObjectProperty<Color> drawingColorProperty() {
        return drawingColor;
    }

    public ObjectProperty<WhiteboardTool> currentToolProperty() {
        return currentTool;
    }

    public SimpleDoubleProperty pencilSizeProperty() {
        return pencilSize;
    }

    public SimpleDoubleProperty eraserSizeProperty() {
        return eraserSize;
    }

    public void clearWhiteboards() {
        if (this.whiteboardKeyOptionList != null) {
            this.whiteboardKeyOptionList.forEach(WhiteboardKeyOption::clearWhiteboard);
        }
    }

    public WhiteboardKeyOption getFirstWhiteboardKeyOption() {
        return !CollectionUtils.isEmpty(whiteboardKeyOptionList) ? whiteboardKeyOptionList.getFirst() : null;
    }

    public enum WhiteboardTool {
        PENCIL, ERASER;
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        Map<GridComponentI, List<WhiteboardKeyOption>> whiteboardKeyOptionsMap = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(WhiteboardKeyOption.class, configuration, whiteboardKeyOptionsMap, null);
        this.whiteboardKeyOptionList = whiteboardKeyOptionsMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.whiteboardKeyOptionList = null;
    }
    //========================================================================
}
