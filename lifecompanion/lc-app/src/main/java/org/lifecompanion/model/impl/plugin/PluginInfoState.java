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

package org.lifecompanion.model.impl.plugin;

import javafx.scene.paint.Color;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.framework.commons.translation.Translation;

public enum PluginInfoState {
    LOADED("plugin.info.state.loaded.text", Color.GRAY),
    ADDED("plugin.info.state.added.text", LCGraphicStyle.LC_WARNING_COLOR),
    ERROR("plugin.info.state.error.text", LCGraphicStyle.SECOND_DARK),
    REMOVED("plugin.info.state.removed.text", LCGraphicStyle.SECOND_DARK);

    private final String stateTextId;
    private final Color stateColor;

    PluginInfoState(String stateTextId, Color stateColor) {
        this.stateTextId = stateTextId;
        this.stateColor = stateColor;
    }

    public String getStateText() {
        return Translation.getText(stateTextId);
    }

    public Color getStateColor() {
        return stateColor;
    }
}
