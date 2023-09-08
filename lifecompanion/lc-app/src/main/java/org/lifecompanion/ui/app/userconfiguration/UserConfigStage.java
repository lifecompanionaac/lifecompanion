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

package org.lifecompanion.ui.app.userconfiguration;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.util.javafx.StageUtils;

public class UserConfigStage extends Stage {

    public UserConfigStage(Window owner) {
        StageUtils.applyDefaultStageConfiguration(this);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.initOwner(owner);
        this.setWidth(LCGraphicStyle.DEFAULT_TOOL_STAGE_WIDTH);
        this.setHeight(LCGraphicStyle.DEFAULT_TOOL_STAGE_HEIGHT);
        this.setResizable(LCGraphicStyle.TOOL_STAGE_RESIZABLE);
        StageUtils.fixMaximizedVisualBounds(this);
    }
}
