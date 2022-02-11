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

package org.lifecompanion.config.view.useevent.impl.control.clic;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.impl.categorizedelement.useevent.available.MouseButtonReleasedEventGenerator;
import org.lifecompanion.config.view.reusable.MouseButtonSelectorControl;
import org.lifecompanion.framework.commons.translation.Translation;

public class MouseButtonReleasedConfigView extends HBox implements UseEventGeneratorConfigurationViewI<MouseButtonReleasedEventGenerator> {

    private MouseButtonSelectorControl mouseButtonSelectorControl;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<MouseButtonReleasedEventGenerator> getConfiguredActionType() {
        return MouseButtonReleasedEventGenerator.class;
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.mouseButtonSelectorControl = new MouseButtonSelectorControl();
        Label label = new Label(Translation.getText("wanted.mouse.button.label.text"));
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);
        this.getChildren().addAll(label, this.mouseButtonSelectorControl);
    }

    @Override
    public void editEnds(final MouseButtonReleasedEventGenerator element) {
        element.wantedButtonProperty().set(this.mouseButtonSelectorControl.valueProperty().get());
    }

    @Override
    public void editStarts(final MouseButtonReleasedEventGenerator element) {
        this.mouseButtonSelectorControl.valueProperty().set(element.wantedButtonProperty().get());
    }
}
