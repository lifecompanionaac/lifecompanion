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
package org.lifecompanion.config.view.pane.tabs.useaction.part.multi;

import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Part to display and manage action on a selected component list for a given event type.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MultiUseActionListRibbonPart extends RibbonBasePart<LCConfigurationI> implements LCViewInitHelper {
    /**
     * List view to manage actions
     */
    private MultiUseActionListManageView useActionListManageView;

    private final UseActionEvent eventType;
    private final boolean alwaysDisplay;

    /**
     * Create a component to manager action
     *
     * @param eventTypeP     the event type of the action to manage
     * @param alwaysDisplayP if we need to display this component when the action list is empty
     */
    public MultiUseActionListRibbonPart(final UseActionEvent eventTypeP, final boolean alwaysDisplayP) {
        this.eventType = eventTypeP;
        this.alwaysDisplay = alwaysDisplayP;
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.useActionListManageView = new MultiUseActionListManageView(this.eventType, this.alwaysDisplay, null);
        this.setContent(this.useActionListManageView);
        //Base for this tab (title with event type)
        this.setTitle(Translation.getText("action.list.title", Translation.getText(this.eventType.getEventLabelId())));
    }

    @Override
    public void initBinding() {
        this.model.bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty());
        this.useActionListManageView.modelProperty().bind(model);
    }
    //========================================================================

    @Override
    public void bind(final LCConfigurationI model) {
    }

    @Override
    public void unbind(final LCConfigurationI model) {
    }
}
