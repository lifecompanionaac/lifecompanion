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

package org.lifecompanion.config.view.pane.general.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStep;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions;
import org.lifecompanion.config.data.component.profile.ProfileConfigSelectionController;
import org.lifecompanion.config.data.component.profile.ProfileConfigStep;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.compselector.ComponentSelectorControl;
import org.lifecompanion.config.view.reusable.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class GeneralInformationConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private Label labelName, labelAuthor;
    private Button buttonEditConfigurationInformation, buttonPrintGridsPdf;
    private ToggleSwitch toggleSecuredConfigurationMode;
    private ToggleSwitch toggleVirtualKeyboard;
    private ComponentSelectorControl<GridComponentI> firstPartSelector;
    private LCColorPicker pickerBackgroundColor;

    private LCConfigurationI model;

    public GeneralInformationConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.general.information.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.GENERAL_INFORMATION.name();
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
        labelAuthor = new Label();
        labelAuthor.setStyle("-fx-font-weight: bold;");
        labelName = new Label();
        labelName.setStyle("-fx-font-weight: bold;");
        labelName.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelName, Priority.ALWAYS);
        labelName.setAlignment(Pos.CENTER_RIGHT);
        labelAuthor.setAlignment(Pos.CENTER_RIGHT);
        labelAuthor.setMaxWidth(Double.MAX_VALUE);
        Label labelNameField = new Label(Translation.getText("general.configuration.info.label.name"));
        labelNameField.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        Label labelAuthorField = new Label(Translation.getText("general.configuration.info.label.author"));
        Label labelGeneralInfo = UIUtils.createTitleLabel(Translation.getText("general.configuration.info.info.description.title"));
        buttonEditConfigurationInformation = UIUtils.createSimpleTextButton(Translation.getText("general.configuration.info.button.edit.information"), null);
        GridPane.setHalignment(buttonEditConfigurationInformation, HPos.CENTER);

        Label labelPartDisplay = UIUtils.createTitleLabel(Translation.getText("general.configuration.info.general.configuration.style"));
        this.pickerBackgroundColor = new LCColorPicker();
        UIUtils.createAndAttachTooltip(pickerBackgroundColor, "tooltip.explain.configuration.style.background.color");
        Label labelBColor = new Label(Translation.getText("configuration.background.color"));

        Label labelGeneralConfiguration = UIUtils.createTitleLabel(Translation.getText("general.configuration.info.general.configuration"));
        this.firstPartSelector = new ComponentSelectorControl<>(GridComponentI.class);
        this.firstPartSelector.setTooltipText("tooltip.explain.use.param.first.part");
        firstPartSelector.setAlignment(Pos.CENTER_RIGHT);
        Label labelFirstPart = new Label(Translation.getText("general.configuration.info.label.first.part.start"));
        Label labelExplainFirstPart = new Label(Translation.getText("tooltip.explain.use.param.first.part"));
        labelExplainFirstPart.getStyleClass().add("explain-text");

        this.toggleVirtualKeyboard = ConfigUIUtils.createToggleSwitch("configuration.for.virtual.keyboard",
                "tooltip.explain.use.param.virtual.keyboard");
        Label labelExplainVirtualKeyboard = new Label(Translation.getText("tooltip.explain.use.param.virtual.keyboard"));
        labelExplainVirtualKeyboard.getStyleClass().add("explain-text");

        this.toggleSecuredConfigurationMode = ConfigUIUtils.createToggleSwitch("configuration.secured.config.mode",
                "tooltip.explain.use.param.secured.config.mode");
        Label labelExplainSecuredConfigMode = new Label(Translation.getText("tooltip.explain.use.param.secured.config.mode"));
        labelExplainSecuredConfigMode.getStyleClass().add("explain-text");

        GridPane gridPaneTotal = new GridPane();
        gridPaneTotal.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneTotal.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int gridRowIndex = 0;
        gridPaneTotal.add(labelGeneralInfo, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelNameField, 0, gridRowIndex);
        gridPaneTotal.add(labelName, 1, gridRowIndex++);
        gridPaneTotal.add(labelAuthorField, 0, gridRowIndex);
        gridPaneTotal.add(labelAuthor, 1, gridRowIndex++);
        gridPaneTotal.add(buttonEditConfigurationInformation, 0, gridRowIndex++, 2, 1);

        gridPaneTotal.add(labelPartDisplay, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelBColor, 0, gridRowIndex);
        GridPane.setHalignment(pickerBackgroundColor,HPos.RIGHT);
        gridPaneTotal.add(pickerBackgroundColor, 1, gridRowIndex++);

        gridPaneTotal.add(labelGeneralConfiguration, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelFirstPart, 0, gridRowIndex);
        gridPaneTotal.add(firstPartSelector, 1, gridRowIndex++);
        gridPaneTotal.add(labelExplainFirstPart, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(toggleSecuredConfigurationMode, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelExplainSecuredConfigMode, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(toggleVirtualKeyboard, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelExplainVirtualKeyboard, 0, gridRowIndex++, 2, 1);

        // Actions
        GridPane gridPaneActions = new GridPane();
        int gridActionsRowIndex = 0;
        gridPaneActions.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneActions.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        Label labelPartActions = UIUtils.createTitleLabel(Translation.getText("general.configuration.info.general.configuration.actions"));
        gridPaneActions.add(labelPartActions, 0, gridActionsRowIndex++, 2, 1);
        this.buttonPrintGridsPdf = ConfigUIUtils.createActionTableEntry(gridActionsRowIndex, "configuration.selection.print.grids.pdf.configuration.button",
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.FILE_PDF_ALT).size(30).color(LCGraphicStyle.SECOND_DARK), gridPaneActions);
        gridActionsRowIndex += 2;

        gridPaneTotal.add(gridPaneActions, 0, gridRowIndex, 2, gridActionsRowIndex);
        gridRowIndex += gridActionsRowIndex;

        gridPaneTotal.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        ScrollPane scrollPane = new ScrollPane(gridPaneTotal);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);
    }

    @Override
    public void initListener() {
        buttonEditConfigurationInformation.setOnAction(e -> {
            LCConfigurationDescriptionI configuration = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();
            if (configuration != null) {
                ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_EDIT, null, configuration);
            }
        });
        this.buttonPrintGridsPdf.setOnAction(e -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.ExportConfigGridsToPdfAction(buttonPrintGridsPdf)));
    }
    //========================================================================


    @Override
    public void saveChanges() {
        model.firstSelectionPartProperty().set(this.firstPartSelector.selectedComponentProperty().get());
        model.securedConfigurationModeProperty().set(this.toggleSecuredConfigurationMode.isSelected());
        model.virtualKeyboardProperty().set(this.toggleVirtualKeyboard.isSelected());
        model.backgroundColorProperty().set(this.pickerBackgroundColor.getValue());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.firstPartSelector.selectedComponentProperty().set(model.firstSelectionPartProperty().get());
        this.toggleSecuredConfigurationMode.setSelected(model.securedConfigurationModeProperty().get());
        this.toggleVirtualKeyboard.setSelected(model.virtualKeyboardProperty().get());
        this.pickerBackgroundColor.setValue(model.backgroundColorProperty().get());
        LCConfigurationDescriptionI configurationDescription = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();
        if (configurationDescription != null) {
            this.labelName.textProperty().bind(configurationDescription.configurationNameProperty());
            this.labelAuthor.textProperty().bind(configurationDescription.configurationAuthorProperty());
            this.buttonEditConfigurationInformation.setDisable(false);
        } else {
            this.labelName.setText(Translation.getText("general.configuration.info.label.no.information"));
            this.labelAuthor.setText(Translation.getText("general.configuration.info.label.no.information"));
            this.buttonEditConfigurationInformation.setDisable(true);
        }
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
        this.labelName.textProperty().unbind();
        this.labelAuthor.textProperty().unbind();
        this.firstPartSelector.clearSelection();
    }
}
