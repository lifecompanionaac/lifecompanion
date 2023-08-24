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
package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.lifecycle.AppModeController;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.collections.ObservableList;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Math;

/**
 * Action to change the size of the use window given a ratio.
 *
 * @author Paul BREUIL <tykapl.breuil@gmail.com>
 */
public class ChangeStageSizeAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeStageSizeAction.class);


    private final DoubleProperty changeRatioPercent;

    public ChangeStageSizeAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.FRAME;
        this.nameID = "action.change.stage.size.name";
        this.staticDescriptionID = "action.change.stage.size.description";
        this.configIconPath = "configuration/icon_change_stage_ratio.png";
        this.parameterizableAction = true;
        this.changeRatioPercent = new SimpleDoubleProperty(100);
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public DoubleProperty changeRatioProperty() {
        return changeRatioPercent;
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.FORCE_WINDOW_SIZE) && !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.FORCE_WINDOW_LOCATION)) {
            FXThreadUtils.runOnFXThread(() -> {
                final Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
                double x = stage.getX();
                double y = stage.getY();
                double stageWidth = stage.getWidth();
                double stageHeight = stage.getHeight();
                double changeRatio = this.changeRatioPercent.get() / 100;

                // Handling the Fullscreen/Maximized status
                if (stage.isFullScreen() || stage.isMaximized()) {
                    if (changeRatio < 1) {
                        stage.setFullScreen(false);
                        stage.setMaximized(false);
                    } else {
                        return;
                    }
                } else {
                    // Getting the screen the config resides in
                    Rectangle2D stageCenterPoint = new Rectangle2D(x + stageWidth / 2, y + stageHeight / 2, 1, 1);

                    ObservableList<Screen> screensContainingStage = Screen.getScreensForRectangle(stageCenterPoint);
                    Screen stageScreen;
                    if (screensContainingStage.size() == 0) {
                        // Handles when the central point of the stage isn't on any screen
                        Rectangle2D stageBounds = new Rectangle2D(x, y, stageWidth, stageHeight);
                        screensContainingStage = Screen.getScreensForRectangle(stageBounds);
                    }
                    if (screensContainingStage.size() == 0) {
                        stageScreen = Screen.getPrimary();
                    } else {
                        stageScreen = screensContainingStage.get(0);
                    }
                    Rectangle2D stageScreenBounds = stageScreen.getBounds();
                    double maxAvailableWidth = stageScreenBounds.getWidth();
                    double maxAvailableHeight = stageScreenBounds.getHeight();

                    // Change window size
                    double newStageWidth = Math.max(Math.min(stageWidth * changeRatio, maxAvailableWidth), maxAvailableWidth / 10);
                    double newStageHeight = Math.max(Math.min(stageHeight * changeRatio, maxAvailableHeight), maxAvailableHeight / 10);
                    stage.setWidth(newStageWidth);
                    stage.setHeight(newStageHeight);
                    if (newStageWidth > maxAvailableWidth - 1 && newStageHeight > maxAvailableHeight - 1) {
                        stage.setMaximized(true);
                    }
                }
                VirtualMouseController.INSTANCE.centerMouseOnStage();
            });
        } else {
            LOGGER.info("ChangeStageSizeAction action ignored because {} or {} are enabled", GlobalRuntimeConfiguration.FORCE_WINDOW_SIZE, GlobalRuntimeConfiguration.FORCE_WINDOW_LOCATION);
        }
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(ChangeStageSizeAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(ChangeStageSizeAction.class, this, nodeP);
    }
    //========================================================================
}