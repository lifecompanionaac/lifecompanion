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
        this.order = 1;
        this.category = DefaultUseActionSubCategories.APPLICATION;
        this.nameID = "action.change.window.size.name";
        this.staticDescriptionID = "action.change.window.size.description";
        this.configIconPath = "miscellaneous/icon_exit_application_action.png";
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
        Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
        FXThreadUtils.runOnFXThread(() -> {
            Double x = stage.getX();
            Double y  = stage.getY();
            Double stageWidth = stage.getWidth();
            Double stageHeight = stage.getHeight();

            // Removing the fullscreen and maximized status
            stage.setFullScreen(false);
            stage.setMaximized(false);

            // Getting the maximum size available for the stage
            Rectangle2D stageBounds = new Rectangle2D(x, y, stageWidth, stageHeight);
            ObservableList<Screen> screensContainingStage = Screen.getScreensForRectangle(stageBounds);
            Double maxAvailableWidth = 0.;
            Double maxAvailableHeight = 0.;
            for (Screen screen : screensContainingStage) {
                Double screenWidth = screen.getBounds().getWidth();
                if (screenWidth > maxAvailableWidth) {
                    maxAvailableWidth = screenWidth;
                }
                Double screenHeight = screen.getBounds().getHeight();
                if (screenHeight > maxAvailableHeight) {
                    maxAvailableHeight = screenHeight;
                }
            }
            // Change window size
            Double newStageWidth = Math.min(stageWidth*this.changeRatio.get(), maxAvailableWidth);
            stage.setWidth(newStageWidth);
            Double newStageHeight = Math.min(stageHeight*this.changeRatio.get(), maxAvailableHeight);
            stage.setHeight(newStageHeight);
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