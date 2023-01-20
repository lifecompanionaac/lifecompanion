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
package org.lifecompanion.plugin.email.event.view;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.plugin.email.event.OnUnreadEmailCountUpdatedEventGenerator;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OnUnreadEmailCountUpdatedConfigView extends VBox
        implements UseEventGeneratorConfigurationViewI<OnUnreadEmailCountUpdatedEventGenerator> {

    private ChoiceBox<OnUnreadEmailCountUpdatedEventGenerator.UnreadEventGenerateCondition> choiceGenerateCondition;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<OnUnreadEmailCountUpdatedEventGenerator> getConfiguredActionType() {
        return OnUnreadEmailCountUpdatedEventGenerator.class;
    }

    @Override
    public void initUI() {
        this.choiceGenerateCondition = new ChoiceBox<>(FXCollections.observableArrayList(OnUnreadEmailCountUpdatedEventGenerator.UnreadEventGenerateCondition.values()));
        this.choiceGenerateCondition.setConverter(new StringConverter<OnUnreadEmailCountUpdatedEventGenerator.UnreadEventGenerateCondition>() {
            @Override
            public String toString(OnUnreadEmailCountUpdatedEventGenerator.UnreadEventGenerateCondition value) {
                return value != null ? value.getText() : null;
            }

            @Override
            public OnUnreadEmailCountUpdatedEventGenerator.UnreadEventGenerateCondition fromString(String value) {
                for (OnUnreadEmailCountUpdatedEventGenerator.UnreadEventGenerateCondition cond : OnUnreadEmailCountUpdatedEventGenerator.UnreadEventGenerateCondition.values()) {
                    if (cond.getText().equals(value)) {
                        return cond;
                    }
                }
                return null;
            }
        });
        this.getChildren().addAll(new Label(Translation.getText("email.plugin.unread.count.condition.label")), this.choiceGenerateCondition);
    }

    @Override
    public void editEnds(final OnUnreadEmailCountUpdatedEventGenerator element) {
        element.conditionProperty().set(choiceGenerateCondition.getValue());
    }

    @Override
    public void editStarts(final OnUnreadEmailCountUpdatedEventGenerator element) {
        choiceGenerateCondition.setValue(element.conditionProperty().get());
    }
}
