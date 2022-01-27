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
package org.lifecompanion.config.view.pane.compselector;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.control.refacto.ProfileController;
import org.lifecompanion.config.view.pane.profile.ProfileSimpleListCell;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class ProfileSelectorControl extends VBox implements LCViewInitHelper {
    /**
     * Search combobox
     */
    private ComboBox<LCProfileI> comboBoxProfile;

    /**
     * Label for this control
     */
    private Label label;

    /**
     * Text in label (default for constructor)
     */
    private final String labelText;

    public ProfileSelectorControl(final String labelTextP) {
        this.labelText = labelTextP;
        this.initAll();
    }

    @Override
    public void initUI() {
        this.comboBoxProfile = new ComboBox<>(ProfileController.INSTANCE.getProfiles());
        this.comboBoxProfile.setCellFactory(lv -> new ProfileSimpleListCell());
        this.comboBoxProfile.setButtonCell(new ProfileSimpleListCell());
        UIUtils.setFixedWidth(comboBoxProfile, 250.0);
        this.label = new Label(this.labelText);
        this.setPadding(new Insets(5.0));
        this.setSpacing(5.0);
        this.getChildren().addAll(label, comboBoxProfile);
    }

    public ObjectProperty<LCProfileI> valueProperty() {
        return comboBoxProfile.valueProperty();
    }
}
