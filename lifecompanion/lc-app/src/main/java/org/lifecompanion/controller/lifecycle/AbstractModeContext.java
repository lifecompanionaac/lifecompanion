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

package org.lifecompanion.controller.lifecycle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;

public abstract class AbstractModeContext {
    protected final ObjectProperty<LCConfigurationI> configuration;
    protected final ObjectProperty<LCConfigurationDescriptionI> configurationDescription;
    protected final ObjectProperty<Stage> stage;

    protected AbstractModeContext() {
        this.configuration = new SimpleObjectProperty<>();
        this.configurationDescription = new SimpleObjectProperty<>();
        this.stage = new SimpleObjectProperty<>();
        initBindings();
    }

    // PROPS
    //========================================================================

    /**
     * The configuration for this context. This is filled by the system using {@link AppModeController} methods.<br>
     * This can change even if the current mode doesn't change (for example, in edit mode, it is possible to switch from current configuration).<br>
     * This can also be null if the mode is launched but with no current configuration (configuration can be closed)
     *
     * @return the read only property containing current configuration
     */
    public final ReadOnlyObjectProperty<LCConfigurationI> configurationProperty() {
        return configuration;
    }

    public LCConfigurationI getConfiguration() {
        return configuration.get();
    }


    /**
     * The configuration description associated with the current configuration.<br>
     * This can be null even if the configuration is filled : if the configuration was never saved yet, or in some special cases.
     *
     * @return the read only property containing the current configuration description
     */
    public final ReadOnlyObjectProperty<LCConfigurationDescriptionI> configurationDescriptionProperty() {
        return configurationDescription;
    }

    public LCConfigurationDescriptionI getConfigurationDescription() {
        return configurationDescription.get();
    }

    /**
     * The mode associated stage.<br>
     * Be aware that this Stage can be "under" another stage.<br>
     * Depending of the mode, a stage can be created on each launch (in use mode) or recycled on every launch (in edit mode)
     *
     * @return the read only property containing the current stage
     */
    public final ReadOnlyObjectProperty<Stage> stageProperty() {
        return stage;
    }

    public final Stage getStage() {
        return stage.get();
    }

    final void initStage(Stage stage) {
        this.stage.set(stage);
    }
    //========================================================================


    // BASE BEHAVIOR
    //========================================================================
    protected abstract void cleanAfterStop();

    private void initBindings() {
        this.configuration.addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.dispatchDisplayedProperty(false);
                ov.dispatchRemovedPropertyValue(true);
            }
            if (nv != null) {
                nv.dispatchRemovedPropertyValue(false);
                nv.dispatchDisplayedProperty(true);
            }
        });
        this.configurationDescription.addListener((obs, ov, nv) -> {
            if (ov != null) ov.loadedConfigurationProperty().set(null);
        });
    }

    void switchTo(final LCConfigurationI configuration, final LCConfigurationDescriptionI configurationDescription) {
        this.configuration.set(configuration);
        this.configurationDescription.set(configurationDescription);
        if (configurationDescription != null) {
            configurationDescription.loadedConfigurationProperty().set(configuration);
        }
    }
    //========================================================================


}
