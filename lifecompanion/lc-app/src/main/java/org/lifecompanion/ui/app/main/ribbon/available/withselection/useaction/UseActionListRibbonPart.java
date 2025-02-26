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
package org.lifecompanion.ui.app.main.ribbon.available.withselection.useaction;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.ui.app.categorizedelement.useaction.UseActionListManageView;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Part to display and manage action on a selected component for a given event type.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseActionListRibbonPart extends RibbonBasePart<UseActionTriggerComponentI> implements LCViewInitHelper {
    /**
     * List view to manage actions
     */
    private UseActionListManageView useActionListManageView;

    private final UseActionEvent eventType;
    private final boolean alwaysDisplay;
    private final String title;

    /**
     * Create a component to manager action
     *
     * @param eventTypeP     the event type of the action to manage
     * @param alwaysDisplayP if we need to display this component when the action list is empty
     */
    public UseActionListRibbonPart(final UseActionEvent eventTypeP, final boolean alwaysDisplayP) {
        this(eventTypeP, Translation.getText("action.list.title", Translation.getText(eventTypeP.getEventLabelId())), alwaysDisplayP);
    }

    public UseActionListRibbonPart(final UseActionEvent eventTypeP, final String title, final boolean alwaysDisplayP) {
        this.eventType = eventTypeP;
        this.alwaysDisplay = alwaysDisplayP;
        this.title = title;
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.useActionListManageView = new UseActionListManageView(this.eventType, this.alwaysDisplay, null);
        this.setContent(this.useActionListManageView);
        this.setTitle(this.title);
    }

    @Override
    public void initBinding() {
        //Bind on current grid component
        SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().addListener((obs, ov, nv) -> {
            if (nv instanceof UseActionTriggerComponentI) {
                this.model.set((UseActionTriggerComponentI) nv);
            } else {
                this.model.set(null);
            }
        });
    }
    //========================================================================

    @Override
    public void bind(final UseActionTriggerComponentI model) {
        this.useActionListManageView.modelProperty().set(model);
    }

    @Override
    public void unbind(final UseActionTriggerComponentI model) {
        this.useActionListManageView.modelProperty().set(null);
    }
}
