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
package org.lifecompanion.ui.common.control.specific.selector;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.ui.common.pane.specific.cell.ConfigurationSimpleListCell;
import org.lifecompanion.ui.common.control.generic.searchcombobox.SearchComboBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

/**
 * Component to select a configuration
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationSelectorControl extends VBox implements LCViewInitHelper {
    /**
     * Search combobox
     */
    private SearchComboBox<LCConfigurationDescriptionI> comboBoxConfigurations;

    /**
     * Text in label (default for constructor)
     */
    private final String labelText;

    public ConfigurationSelectorControl(final String labelTextP) {
        this.labelText = labelTextP;
        this.initAll();
    }

    @Override
    public void initUI() {
        comboBoxConfigurations = new SearchComboBox<>(
                lv -> new ConfigurationSimpleListCell(),
                searchText ->
                        StringUtils.isBlank(searchText) ? null : desc -> StringUtils.startWithIgnoreCase(desc.configurationNameProperty().get(), searchText)
                                || StringUtils.containsIgnoreCase(desc.configurationNameProperty().get(), searchText)
                                || StringUtils.containsIgnoreCase(desc.configurationDescriptionProperty().get(), searchText)
                , desc -> desc != null ?
                desc.configurationNameProperty().get() + " (" + desc.configurationAuthorProperty().get() + ")" : Translation.getText("configuration.selector.control.no.selection"));
        comboBoxConfigurations.setFixedCellSize(140.0);
        this.setPadding(new Insets(5.0));
        this.setSpacing(5.0);
        this.getChildren().addAll(new Label(this.labelText), comboBoxConfigurations);
    }

    public ObjectProperty<LCConfigurationDescriptionI> valueProperty() {
        return comboBoxConfigurations.valueProperty();
    }

    @Override
    public void initBinding() {
        ChangeListener<LCProfileI> profileChangeListener = (obs, ov, nv) -> {
            if (nv != null) {
                comboBoxConfigurations.setItems(nv.getConfiguration());
            } else {
                comboBoxConfigurations.setItems(null);
            }
        };
        profileChangeListener.changed(null, null, ProfileController.INSTANCE.currentProfileProperty().get());
        ProfileController.INSTANCE.currentProfileProperty().addListener(profileChangeListener);
    }
}
