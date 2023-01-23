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
package org.lifecompanion.plugin.homeassistant.event.view;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.plugin.homeassistant.event.OnItemStateChangedEventGenerator;
import org.lifecompanion.plugin.homeassistant.view.HAEntitySelector;

public class OnItemStateChangedEventGeneratorConfigView extends GridPane implements UseEventGeneratorConfigurationViewI<OnItemStateChangedEventGenerator> {

    private HAEntitySelector entitySelector;
    private TextField fieldStateFilter;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<OnItemStateChangedEventGenerator> getConfiguredActionType() {
        return OnItemStateChangedEventGenerator.class;
    }

    @Override
    public void initUI() {
        entitySelector = new HAEntitySelector();
        GridPane.setHalignment(entitySelector, HPos.LEFT);
        fieldStateFilter = new TextField();

        Label labelEntity = new Label(Translation.getText("ha.plugin.field.entity.id"));
        Label labelFilter = new Label(Translation.getText("ha.plugin.field.filter.state"));
        Label labelFilterExplain = new Label(Translation.getText("ha.plugin.field.filter.state.explain"));
        labelFilterExplain.setFont(Font.font(10));

        int rowIndex = 0;
        this.add(labelEntity, 0, rowIndex);
        this.add(entitySelector, 1, rowIndex++);
        this.add(labelFilter, 0, rowIndex);
        this.add(fieldStateFilter, 1, rowIndex++);
        this.add(labelFilterExplain, 0, rowIndex++, 2, 1);
        this.setHgap(10);
        this.setVgap(5);
    }


    @Override
    public void editStarts(final OnItemStateChangedEventGenerator element) {
        entitySelector.refresh(element.entityIdProperty().get(), null);
        fieldStateFilter.setText(element.valueFilterProperty().get());
    }

    @Override
    public void editEnds(final OnItemStateChangedEventGenerator element) {
        element.entityIdProperty().set(entitySelector.valueProperty().get());
        element.valueFilterProperty().set(fieldStateFilter.getText());
    }


}

