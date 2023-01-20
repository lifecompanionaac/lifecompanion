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

package org.lifecompanion.ui.app.generalconfiguration.step;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.app.categorizedelement.useaction.UseActionListManageView;
import org.lifecompanion.ui.app.categorizedelement.useevent.UseEventListManageView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class UseEventListMainConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private LCConfigurationI model;

    private UseActionListManageView useActionListManageView;
    private UseEventListManageView useEventListManageView;

    public UseEventListMainConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.use.event.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.EVENTS.name();
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        // Top Intro
        Label labelExplainEvents = new Label(Translation.getText("general.configuration.view.step.use.event.explain"));
        labelExplainEvents.getStyleClass().addAll("text-wrap-enabled", "text-font-italic", "text-fill-gray");
        labelExplainEvents.setMaxWidth(Double.MAX_VALUE);
        labelExplainEvents.setMinHeight(90.0);

        // first part : IF
        Label labelEvent = FXControlUtils.createTitleLabel(Translation.getText("useevent.part.event.title"));
        this.useEventListManageView = new UseEventListManageView(true);
        this.useEventListManageView.setElementListViewPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        // second part : THEN
        Label labelAction = FXControlUtils.createTitleLabel(Translation.getText("useevent.part.action.title"));
        this.useActionListManageView = new UseActionListManageView(UseActionEvent.EVENT, true, this.useEventListManageView.selectedItemProperty());
        this.useActionListManageView.setElementListViewPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        VBox boxTotal = new VBox(10.0, labelExplainEvents, labelEvent, useEventListManageView, labelAction, useActionListManageView);
        boxTotal.setMaxHeight(Double.MAX_VALUE);
        boxTotal.setAlignment(Pos.CENTER);

        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(boxTotal);
    }


    @Override
    public void initBinding() {
        this.useActionListManageView.modelProperty().bind(this.useEventListManageView.selectedItemProperty());
        this.useActionListManageView.visibleProperty().bind(this.useEventListManageView.selectedItemProperty().isNotNull());
    }
    //========================================================================


    @Override
    public void saveChanges() {
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.useEventListManageView.modelProperty().set(model);
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.useEventListManageView.modelProperty().set(null);
        this.model = null;
    }
}
