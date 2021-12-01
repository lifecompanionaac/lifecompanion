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

package org.lifecompanion.config.view.pane.config;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.LCGraphicStyle;

public class UserConfigStage extends Stage {

    public UserConfigStage(Window owner) {
        this.setTitle(LCConstant.NAME);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.initOwner(owner);
        this.setWidth(LCGraphicStyle.DEFAULT_TOOL_STAGE_WIDTH);
        this.setHeight(LCGraphicStyle.DEFAULT_TOOL_STAGE_HEIGHT);
        this.setResizable(LCGraphicStyle.TOOL_STAGE_RESIZABLE);
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.getIcons().add(IconManager.get(LCConstant.LC_ICON_PATH));
    }
}
