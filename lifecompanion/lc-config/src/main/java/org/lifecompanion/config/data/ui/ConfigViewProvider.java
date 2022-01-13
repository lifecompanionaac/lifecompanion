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

package org.lifecompanion.config.data.ui;

import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.api.ui.ViewProviderType;
import org.lifecompanion.base.data.component.simple.*;
import org.lifecompanion.base.data.ui.BaseViewProvider;
import org.lifecompanion.config.view.component.simple.*;

import java.util.HashMap;
import java.util.Map;

/**
 * View provider for component displayed in configuration mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigViewProvider extends BaseViewProvider {

    // Class part : "Initialization"
    //========================================================================
    private static final Map<Class<? extends DisplayableComponentI>, Class<? extends ComponentViewI<?>>> VIEWS = new HashMap<>();

    static {
        ConfigViewProvider.VIEWS.put(LCConfigurationComponent.class, LCConfigurationViewConfig.class);
        ConfigViewProvider.VIEWS.put(GridPartGridComponent.class, GridPartGridViewConfig.class);
        ConfigViewProvider.VIEWS.put(GridPartKeyComponent.class, GridPartKeyViewConfig.class);
        ConfigViewProvider.VIEWS.put(StackComponent.class, StackViewConfig.class);
        ConfigViewProvider.VIEWS.put(TextEditorComponent.class, TextEditorViewConfig.class);
        ConfigViewProvider.VIEWS.put(GridPartStackComponent.class, GridPartStackViewConfig.class);
        ConfigViewProvider.VIEWS.put(GridPartTextEditorComponent.class, GridPartTextEditorViewConfig.class);
    }

    public ConfigViewProvider() {
        super(ViewProviderType.CONFIG, ConfigViewProvider.VIEWS);
    }
    //========================================================================

}
