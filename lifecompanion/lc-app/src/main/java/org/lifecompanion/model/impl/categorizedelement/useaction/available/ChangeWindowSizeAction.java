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
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.jdom2.Element;
import java.lang.Math;

/**
 * Action to change the size of the use window given a ratio.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>, Paul BREUIL <tykapl.breuil@gmail.com>
 */
public class ChangeWindowSizeAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private final DoubleProperty changeRatio;

    public ChangeWindowSizeAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.FRAME;
        this.nameID = "action.change.window.size.name";
        this.staticDescriptionID = "action.change.window.size.description";
        this.configIconPath = "configuration/icon_move_frame.png";
        this.parameterizableAction = true;
        this.changeRatio = new SimpleDoubleProperty(1);
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public DoubleProperty changeRatioProperty() {
        return changeRatio;
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        FXThreadUtils.runOnFXThread(() -> {
            final Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
            Double x = stage.getX();
            Double y  = stage.getY();
            Double stageWidth = stage.getWidth();
            Double stageHeight = stage.getHeight();
            Double changeRatio = this.changeRatio.get();

            // Handling the Fullscreen/Maximized status
            if (stage.isFullScreen() || stage.isMaximized()) {
                if (changeRatio < 1) {
                    stage.setFullScreen(false);
                    stage.setMaximized(false);
                }
                return;
            }

            // Getting the screen the config resides in
            Rectangle2D stageCenterPoint = new Rectangle2D(x + stageWidth/2, y + stageHeight/2, 1, 1);
            ObservableList<Screen> screensContainingStage = Screen.getScreensForRectangle(stageCenterPoint);
            Screen stageScreen;
            if (screensContainingStage.size() == 0) {
                // Handles when the central point of the stage isn't on any screen
                Rectangle2D stageBounds = new Rectangle2D(x, y, stageWidth, stageHeight);
                screensContainingStage = Screen.getScreensForRectangle(stageBounds);
            }
            if (screensContainingStage.size() == 0) {
                stageScreen = Screen.getPrimary();
            }
            else {
                stageScreen = screensContainingStage.get(0);
            }
            Rectangle2D stageScreenBounds = stageScreen.getBounds();
            Double maxAvailableWidth = stageScreenBounds.getWidth();
            Double maxAvailableHeight = stageScreenBounds.getHeight();

            // Change window size
            Double newStageWidth = Math.max(Math.min(stageWidth*changeRatio, maxAvailableWidth), maxAvailableWidth/10);
            Double newStageHeight = Math.max(Math.min(stageHeight*changeRatio, maxAvailableHeight), maxAvailableHeight/10);
            stage.setWidth(newStageWidth);
            stage.setHeight(newStageHeight);
            if (newStageWidth > maxAvailableWidth - 1 && newStageHeight > maxAvailableHeight - 1) {
                stage.setMaximized(true);
                return;
            }

            // Recenter window to keep the same center point
            Double xOffset = (stageWidth - newStageWidth)/2;
            Double yOffset = (stageHeight - newStageHeight)/2;
            stage.setX(stage.getX() + xOffset);
            stage.setY(stage.getY() + yOffset);
        });
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(ChangeWindowSizeAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(ChangeWindowSizeAction.class, this, nodeP);
    }
    //========================================================================
}