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

package org.lifecompanion.plugin.caaai.ui.useevent;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.caaai.CAAAIPlugin;
import org.lifecompanion.plugin.caaai.CAAAIPluginProperties;
import org.lifecompanion.plugin.caaai.model.useevent.CAAAIExampleUseEvent;

public class CAAAIExampleUseEventConfigView extends VBox implements UseEventGeneratorConfigurationViewI<CAAAIExampleUseEvent> {

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<CAAAIExampleUseEvent> getConfiguredActionType() {
        return CAAAIExampleUseEvent.class;
    }

    @Override
    public void initUI() {
        // TODO : create UI
        this.getChildren().add(new Label("DEMO"));
    }

    @Override
    public void editStarts(final CAAAIExampleUseEvent element) {
        // If needed ?
        final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();
        CAAAIPluginProperties CAAAIPluginProperties = configuration.getPluginConfigProperties(CAAAIPlugin.ID, CAAAIPluginProperties.class);

        // TODO : model to view
    }

    @Override
    public void editEnds(final CAAAIExampleUseEvent element) {
        // TODO : view to model
    }

    @Override
    public void editCancelled(CAAAIExampleUseEvent element) {
        // Clear if needed
    }
}
