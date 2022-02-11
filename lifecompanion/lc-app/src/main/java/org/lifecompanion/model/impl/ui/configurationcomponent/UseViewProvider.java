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
package org.lifecompanion.model.impl.ui.configurationcomponent;

import java.util.HashMap;
import java.util.Map;

import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderType;
import org.lifecompanion.model.impl.configurationcomponent.*;
import org.lifecompanion.ui.configurationcomponent.base.*;
import org.lifecompanion.ui.configurationcomponent.usemode.GridPartKeyViewUse;
import org.lifecompanion.ui.configurationcomponent.usemode.LCConfigurationViewUse;

/**
 * View provider for component displayed in use mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseViewProvider extends BaseViewProvider {

    // Class part : "Initialization"
    //========================================================================
    private static final Map<Class<? extends DisplayableComponentI>, Class<? extends ComponentViewI<?>>> VIEWS = new HashMap<>();

    static {
        UseViewProvider.VIEWS.put(LCConfigurationComponent.class, LCConfigurationViewUse.class);
        UseViewProvider.VIEWS.put(GridPartGridComponent.class, GridPartGridViewBase.class);
        UseViewProvider.VIEWS.put(GridPartKeyComponent.class, GridPartKeyViewUse.class);
        UseViewProvider.VIEWS.put(StackComponent.class, StackViewBase.class);
        UseViewProvider.VIEWS.put(TextEditorComponent.class, TextEditorViewBase.class);
        UseViewProvider.VIEWS.put(GridPartTextEditorComponent.class, GridPartTextEditorComponentViewBase.class);
        UseViewProvider.VIEWS.put(GridPartStackComponent.class, GridPartStackViewBase.class);
    }

    public UseViewProvider() {
        super(ViewProviderType.USE, UseViewProvider.VIEWS);
    }
    //========================================================================

}
