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

package org.lifecompanion.ui.virtualmouse;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.javafx.StageUtils;

/**
 * Stage use to show the different mouse selection item.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ScanningMouseStage extends Stage {

    // Class part : "Singleton"
    //========================================================================
    private static ScanningMouseStage instance;

    /**
     * <strong>Should be called on the JavaFX Thread !</strong>
     *
     * @return the virtual mouse stage
     */
    public static ScanningMouseStage getInstance() {
        if (ScanningMouseStage.instance == null) {
            ScanningMouseStage.instance = new ScanningMouseStage();
        }
        return ScanningMouseStage.instance;
    }
    //========================================================================

    // Class part : "Stage"
    //========================================================================
    private ScanningMouseScene scanningMouseScene;

    @SuppressWarnings("deprecation")
    private ScanningMouseStage() {
        //Stage parameters
        this.initStyle(StageStyle.TRANSPARENT);
        // FIXME : which stage?
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
        this.setWidth(primaryScreenBounds.getWidth());
        this.setHeight(primaryScreenBounds.getHeight());
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        this.setAlwaysOnTop(true);
        this.setMaximized(true);
        this.setTitle(LCConstant.NAME + " - " + Translation.getText("virtual.mouse.stage.title.com"));
        this.setOnShown(e1 -> StageUtils.setFocusableInternalAPI(this, false));
        this.centerOnScreen();
        //Scene
        this.scanningMouseScene = new ScanningMouseScene(new Group());
        this.setScene(scanningMouseScene);
    }

    public ScanningMouseScene getScanningMouseScene() {
        return scanningMouseScene;
    }

    //========================================================================

}
