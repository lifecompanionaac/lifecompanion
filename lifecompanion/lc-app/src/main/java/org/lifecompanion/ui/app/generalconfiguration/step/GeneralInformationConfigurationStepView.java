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

import javafx.beans.InvalidationListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.ui.common.control.specific.selector.ComponentSelectorControl;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

public class GeneralInformationConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private Label labelName, labelAuthor;
    private Hyperlink linkWebsiteUrl;
    private Button buttonEditConfigurationInformation;
    private ToggleSwitch toggleVirtualKeyboard;
    private ComponentSelectorControl<GridComponentI> firstPartSelector;
    private LCColorPicker pickerBackgroundColor;

    private LCConfigurationI model;
    private boolean dirty;

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
        labelAuthor = createInfoLabel();
        labelName = createInfoLabel();
        linkWebsiteUrl = new Hyperlink();
        linkWebsiteUrl.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHalignment(linkWebsiteUrl,HPos.RIGHT);
        Label labelNameField = new Label(Translation.getText("general.configuration.info.label.name"));
        labelNameField.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        Label labelAuthorField = new Label(Translation.getText("general.configuration.info.label.author"));
        Label labelWebsiteField = new Label(Translation.getText("configuration.description.websiteurl"));
        Label labelGeneralInfo = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.info.info.description.title"));
        buttonEditConfigurationInformation = FXControlUtils.createSimpleTextButton(Translation.getText("general.configuration.info.button.edit.information"), null);
        GridPane.setHalignment(buttonEditConfigurationInformation, HPos.CENTER);

        Label labelPartDisplay = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.info.general.configuration.style"));
        this.pickerBackgroundColor = new LCColorPicker();
        FXControlUtils.createAndAttachTooltip(pickerBackgroundColor, "tooltip.explain.configuration.style.background.color");
        Label labelBColor = new Label(Translation.getText("configuration.background.color"));

        Label labelGeneralConfiguration = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.info.general.configuration"));
        this.firstPartSelector = new ComponentSelectorControl<>(GridComponentI.class);
        this.firstPartSelector.setTooltipText("tooltip.explain.use.param.first.part");
        firstPartSelector.setAlignment(Pos.CENTER_RIGHT);
        Label labelFirstPart = new Label(Translation.getText("general.configuration.info.label.first.part.start"));
        Label labelExplainFirstPart = new Label(Translation.getText("tooltip.explain.use.param.first.part"));
        labelExplainFirstPart.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-wrap-enabled");

        this.toggleVirtualKeyboard = FXControlUtils.createToggleSwitch("configuration.for.virtual.keyboard",
                "tooltip.explain.use.param.virtual.keyboard");
        Label labelExplainVirtualKeyboard = new Label(Translation.getText("tooltip.explain.use.param.virtual.keyboard"));
        labelExplainVirtualKeyboard.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-wrap-enabled");

        GridPane gridPaneTotal = new GridPane();
        gridPaneTotal.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneTotal.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int gridRowIndex = 0;
        gridPaneTotal.add(labelGeneralInfo, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelNameField, 0, gridRowIndex);
        gridPaneTotal.add(labelName, 1, gridRowIndex++);
        gridPaneTotal.add(labelAuthorField, 0, gridRowIndex);
        gridPaneTotal.add(labelAuthor, 1, gridRowIndex++);
        gridPaneTotal.add(labelWebsiteField, 0, gridRowIndex);
        gridPaneTotal.add(linkWebsiteUrl, 1, gridRowIndex++);
        gridPaneTotal.add(buttonEditConfigurationInformation, 0, gridRowIndex++, 2, 1);

        gridPaneTotal.add(labelPartDisplay, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelBColor, 0, gridRowIndex);
        GridPane.setHalignment(pickerBackgroundColor, HPos.RIGHT);
        gridPaneTotal.add(pickerBackgroundColor, 1, gridRowIndex++);

        gridPaneTotal.add(labelGeneralConfiguration, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelFirstPart, 0, gridRowIndex);
        gridPaneTotal.add(firstPartSelector, 1, gridRowIndex++);
        gridPaneTotal.add(labelExplainFirstPart, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(toggleVirtualKeyboard, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelExplainVirtualKeyboard, 0, gridRowIndex++, 2, 1);

        // Actions
        Label labelPartActions = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.info.general.configuration.actions"));
        final Node pdfActionNode = FXControlUtils.createActionTableEntry("configuration.selection.print.grids.pdf.configuration.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.FILE_PDF_ALT).size(30).color(LCGraphicStyle.SECOND_DARK),
                () -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.ExportEditGridsToPdfAction(gridPaneTotal)));
        VBox boxActions = new VBox(GeneralConfigurationStepViewI.GRID_V_GAP, labelPartActions, pdfActionNode);

        gridPaneTotal.add(boxActions, 0, gridRowIndex, 2, 2);

        gridPaneTotal.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        ScrollPane scrollPane = new ScrollPane(gridPaneTotal);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);
    }

    private Label createInfoLabel() {
        return addInfoStyle(new Label());
    }

    private static <T extends Labeled> T addInfoStyle(T label) {
        label.getStyleClass().add("text-weight-bold");
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(label, Priority.ALWAYS);
        return label;
    }

    @Override
    public void initListener() {
        buttonEditConfigurationInformation.setOnAction(e -> {
            LCConfigurationDescriptionI configuration = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();
            if (configuration != null) {
                ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_EDIT, null, configuration);
            }
        });
        linkWebsiteUrl.setOnAction(e -> {
            LCConfigurationDescriptionI configuration = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();
            if (configuration != null) {
                DesktopUtils.openUrlInDefaultBrowser(configuration.configurationWebsiteUrlProperty().get());
            }
        });
    }

    @Override
    public void initBinding() {
        InvalidationListener invalidationListener = inv -> dirty = true;
        pickerBackgroundColor.valueProperty().addListener(invalidationListener);
        firstPartSelector.selectedComponentProperty().addListener(invalidationListener);
        toggleVirtualKeyboard.selectedProperty().addListener(invalidationListener);
    }
    //========================================================================


    @Override
    public void saveChanges() {
        model.firstSelectionPartIdProperty().set(this.firstPartSelector.getSelectedComponentID());
        model.virtualKeyboardProperty().set(this.toggleVirtualKeyboard.isSelected());
        model.backgroundColorProperty().set(this.pickerBackgroundColor.getValue());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.firstPartSelector.selectedComponentProperty().set(model.firstSelectionPartProperty().get());
        this.toggleVirtualKeyboard.setSelected(model.virtualKeyboardProperty().get());
        this.pickerBackgroundColor.setValue(model.backgroundColorProperty().get());
        LCConfigurationDescriptionI configurationDescription = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();
        if (configurationDescription != null) {
            this.labelName.textProperty().bind(configurationDescription.configurationNameProperty());
            this.labelAuthor.textProperty().bind(configurationDescription.configurationAuthorProperty());
            this.linkWebsiteUrl.textProperty().bind(configurationDescription.configurationWebsiteUrlProperty());
            this.buttonEditConfigurationInformation.setDisable(false);
        } else {
            this.labelName.setText(Translation.getText("general.configuration.info.label.no.information"));
            this.labelAuthor.setText(Translation.getText("general.configuration.info.label.no.information"));
            this.linkWebsiteUrl.setText(Translation.getText("general.configuration.info.label.no.information"));
            this.buttonEditConfigurationInformation.setDisable(true);
        }
        this.dirty = false;
    }


    @Override
    public boolean shouldCancelBeConfirmed() {
        return dirty;
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
        this.labelName.textProperty().unbind();
        this.labelAuthor.textProperty().unbind();
        this.linkWebsiteUrl.textProperty().unbind();
        this.firstPartSelector.clearSelection();
    }
}
