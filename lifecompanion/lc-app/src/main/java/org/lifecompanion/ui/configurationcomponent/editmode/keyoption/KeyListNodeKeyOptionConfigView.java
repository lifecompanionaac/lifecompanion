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

package org.lifecompanion.ui.configurationcomponent.editmode.keyoption;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey.KeyListNodeKeyOption;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.controller.editaction.KeyOptionActions;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.javafx.FXControlUtils;


/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyListNodeKeyOptionConfigView extends BaseKeyOptionConfigView<KeyListNodeKeyOption> {

    private Spinner<Integer> spinnerSelectedLevel;
    private ToggleSwitch toggleSwitchSpecificLevel, toggleDisplayLevelBellow;

    private ChangeListener<Number> changeListenerSelectedLevel;
    private ChangeListener<Boolean> changeListenerSpecificLevel, changeListenerDisplayLevelBellow;

    private Button buttonConfigureKeyList;

    @Override
    public Class<KeyListNodeKeyOption> getConfiguredKeyOptionType() {
        return KeyListNodeKeyOption.class;
    }

    @Override
    public void initUI() {
        super.initUI();
        final Label labelLevelSelectionField = new Label(Translation.getText("keylist.key.option.field.level.value.spinner"));
        GridPane.setHgrow(labelLevelSelectionField, Priority.ALWAYS);
        labelLevelSelectionField.setMaxWidth(Double.MAX_VALUE);
        spinnerSelectedLevel = FXControlUtils.createIntSpinner(1, 999, 1, 1, 60.0);
        labelLevelSelectionField.disableProperty().bind(spinnerSelectedLevel.disabledProperty());

        toggleSwitchSpecificLevel = FXControlUtils.createToggleSwitch("keylist.key.option.field.specific.level.toggle", null);
        toggleDisplayLevelBellow = FXControlUtils.createToggleSwitch("keylist.key.option.field.display.level.bellow.toggle", null);

        buttonConfigureKeyList = FXControlUtils.createLeftTextButton(Translation.getText("keylist.key.option.button.configuration.key"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GEARS).size(14).color(LCGraphicStyle.MAIN_DARK), null);

        GridPane gridPaneConfig = new GridPane();
        gridPaneConfig.setVgap(5.0);
        gridPaneConfig.setPrefWidth(225.0);
        int rowIndex = 0;
        gridPaneConfig.add(buttonConfigureKeyList, 0, rowIndex++, 2, 1);
//        gridPaneConfig.add(toggleSwitchSpecificLevel, 0, rowIndex++, 2, 1);
//        gridPaneConfig.add(labelLevelSelectionField, 0, rowIndex);
//        gridPaneConfig.add(spinnerSelectedLevel, 1, rowIndex++);
//        gridPaneConfig.add(toggleDisplayLevelBellow, 0, rowIndex++, 2, 1);
        this.getChildren().addAll(gridPaneConfig);
    }

    @Override
    public void initListener() {
        super.initListener();
//        changeListenerSelectedLevel = EditActionUtils.createIntegerSpinnerBinding(this.spinnerSelectedLevel, this.model,
//                KeyListNodeKeyOption::selectedLevelProperty, KeyOptionActions.ChangeKeyListOptionSelectedLevelAction::new);
//        changeListenerDisplayLevelBellow = EditActionUtils.createSimpleBinding(this.toggleDisplayLevelBellow.selectedProperty(), this.model,
//                m -> m.displayLevelBellowProperty().get(), KeyOptionActions.ChangeKeyListOptionDisplayLevelBellowAction::new);
//        changeListenerSpecificLevel = EditActionUtils.createSimpleBinding(this.toggleSwitchSpecificLevel.selectedProperty(), this.model,
//                m -> m.specificLevelProperty().get(), KeyOptionActions.ChangeKeyListOptionSpecificLevelAction::new);
        this.buttonConfigureKeyList.setOnAction(e -> KeyListController.INSTANCE.openKeyListConfigFor(model.get()));
    }

    @Override
    public void initBinding() {
        super.initBinding();
        this.spinnerSelectedLevel.disableProperty().bind(toggleSwitchSpecificLevel.selectedProperty().not());
        this.toggleDisplayLevelBellow.disableProperty().bind(toggleSwitchSpecificLevel.selectedProperty().not());
    }

    @Override
    public void bind(final KeyListNodeKeyOption model) {
//        toggleDisplayLevelBellow.setSelected(model.displayLevelBellowProperty().get());
//        toggleSwitchSpecificLevel.setSelected(model.specificLevelProperty().get());
//        spinnerSelectedLevel.getValueFactory().setValue(model.selectedLevelProperty().get());
//        model.selectedLevelProperty().addListener(changeListenerSelectedLevel);
//        model.displayLevelBellowProperty().addListener(changeListenerDisplayLevelBellow);
//        model.specificLevelProperty().addListener(changeListenerSpecificLevel);
    }

    @Override
    public void unbind(final KeyListNodeKeyOption model) {
//        model.selectedLevelProperty().removeListener(changeListenerSelectedLevel);
//        model.displayLevelBellowProperty().removeListener(changeListenerDisplayLevelBellow);
//        model.specificLevelProperty().removeListener(changeListenerSpecificLevel);
    }

}
