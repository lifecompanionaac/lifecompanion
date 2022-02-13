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
package org.lifecompanion.ui.app.profileconfigselect;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.profile.ChangelogEntryI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.pane.specific.cell.ChangelogEntryListCell;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.DisableSelectionSelectionModel;
import org.lifecompanion.util.model.Triple;

import java.util.stream.Collectors;

/**
 * View to edit a configuration description (name, etc)
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationEditionView extends BorderPane implements ProfileConfigStepViewI, LCViewInitHelper {

    /**
     * Image view to display configuration image
     */
    private ImageView configurationPreview;

    /**
     * Configuration informations
     */
    private TextField fieldName, fieldAuthor;

    /**
     * Configuration description
     */
    private TextArea fieldDescription;

    private ToggleSwitch toggleSwitchLaunchInUseMode;

    /**
     * The current profile in create
     */
    private final ObjectProperty<LCConfigurationDescriptionI> editedConfiguration;

    private Button buttonValidate;

    private Node nodeExportAction, nodeRemoveAction, nodeDesktopShortcut;

    /**
     * Display changelog entries list
     */
    private ListView<ChangelogEntryI> listViewChangelogEntries;

    public ConfigurationEditionView() {
        this.editedConfiguration = new SimpleObjectProperty<>();
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        Triple<HBox, Label, Node> header = FXControlUtils.createHeader("configuration.edit.title", e -> closeCurrentEdit(false, true));

        this.configurationPreview = new ImageView();
        this.configurationPreview.setFitHeight(200);
        this.configurationPreview.setFitWidth(250);
        this.configurationPreview.setPreserveRatio(true);
        this.fieldName = new TextField();
        this.fieldAuthor = new TextField();
        this.fieldDescription = new TextArea();
        this.fieldDescription.setPrefRowCount(2);
        this.fieldDescription.setWrapText(true);
        Label labelName = new Label(Translation.getText("configuration.description.name"));
        GridPane.setHgrow(labelName, Priority.ALWAYS);
        Label labelDescription = new Label(Translation.getText("configuration.description.description"));
        Label labelAuthor = new Label(Translation.getText("configuration.description.author"));
        GridPane.setValignment(fieldDescription, VPos.TOP);

        toggleSwitchLaunchInUseMode = FXControlUtils.createToggleSwitch("configuration.selection.default.configuration.toggle", "configuration.selection.default.configuration.toggle.explain");
        Label labelExplainLaunchInUseMode = new Label(Translation.getText("configuration.selection.default.configuration.toggle.explain"));
        labelExplainLaunchInUseMode.getStyleClass().add("explain-text");
        labelExplainLaunchInUseMode.setWrapText(true);

        buttonValidate = FXControlUtils.createLeftTextButton(Translation.getText("profile.config.view.button.ok"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_PRIMARY), null);
        GridPane.setHalignment(buttonValidate, HPos.RIGHT);

        // Info fields
        GridPane gridPaneInfo = new GridPane();
        gridPaneInfo.setHgap(10.0);
        gridPaneInfo.setVgap(5.0);
        gridPaneInfo.add(configurationPreview, 0, 0, 1, 9);
        gridPaneInfo.add(labelName, 1, 0);
        gridPaneInfo.add(fieldName, 1, 1);
        gridPaneInfo.add(labelAuthor, 1, 2);
        gridPaneInfo.add(fieldAuthor, 1, 3);
        gridPaneInfo.add(toggleSwitchLaunchInUseMode, 1, 4);
        gridPaneInfo.add(labelExplainLaunchInUseMode, 1, 5);
        gridPaneInfo.add(labelDescription, 1, 6);
        gridPaneInfo.add(fieldDescription, 1, 7);
        gridPaneInfo.add(buttonValidate, 1, 8);

        // Actions
        this.nodeExportAction = FXControlUtils.createActionTableEntry("configuration.selection.export.configuration.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).size(30).color(LCGraphicStyle.MAIN_DARK),
                () -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.EditConfigurationAction(this.editedConfiguration.get(),
                        configDescription -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.ExportEditAction(nodeExportAction, configDescription)))));
        this.nodeRemoveAction = FXControlUtils.createActionTableEntry("configuration.selection.remove.configuration.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(30).color(LCGraphicStyle.SECOND_DARK),
                () -> ConfigActionController.INSTANCE
                        .executeAction(new LCConfigurationActions.RemoveConfigurationAction(nodeRemoveAction, ProfileController.INSTANCE.currentProfileProperty().get(), this.editedConfiguration.get(),
                                removedConfig -> ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_LIST, null, null))));
        if (SystemType.current() == SystemType.WINDOWS) {
            this.nodeDesktopShortcut = FXControlUtils.createActionTableEntry("configuration.selection.create.desktop.link.button",
                    GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.EXTERNAL_LINK).size(30).color(LCGraphicStyle.MAIN_DARK),
                    () -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.CreateDesktopShortcut(ProfileController.INSTANCE.currentProfileProperty().get(), this.editedConfiguration.get())));
        }
        VBox boxActionButtons = new VBox(5.0, nodeExportAction, nodeRemoveAction, nodeDesktopShortcut);

        // Changelog entries
        listViewChangelogEntries = new ListView<>();
        listViewChangelogEntries.setFixedCellSize(ChangelogEntryListCell.CELL_HEIGHT);
        listViewChangelogEntries.setCellFactory(lv -> new ChangelogEntryListCell());
        listViewChangelogEntries.setPrefHeight(70.0);
        listViewChangelogEntries.setSelectionModel(new DisableSelectionSelectionModel<>());

        // Total
        VBox boxCenter = new VBox(3.0,
                FXControlUtils.createTitleLabel("configuration.edition.general.information.title"), gridPaneInfo,
                FXControlUtils.createTitleLabel("configuration.edition.general.actions.title"), boxActionButtons,
                FXControlUtils.createTitleLabel("configuration.edition.general.changelog.entries"), listViewChangelogEntries
        );
        boxCenter.setPadding(new Insets(10.0));

        this.setTop(header.getLeft());
        ScrollPane scrollCenter = new ScrollPane(boxCenter);
        scrollCenter.setFitToWidth(true);
        this.setCenter(scrollCenter);
    }

    @Override
    public void initBinding() {
        this.editedConfiguration.addListener((obs, ov, nv) -> {
            if (ov != null) {
                this.configurationPreview.imageProperty().unbind();
                this.configurationPreview.imageProperty().set(null);
                this.fieldName.textProperty().unbindBidirectional(ov.configurationNameProperty());
                this.fieldAuthor.textProperty().unbindBidirectional(ov.configurationAuthorProperty());
                this.fieldDescription.textProperty().unbindBidirectional(ov.configurationDescriptionProperty());
                this.listViewChangelogEntries.setItems(null);
            }
            if (nv != null) {
                nv.requestImageLoad();
                this.configurationPreview.imageProperty().bind(nv.configurationImageProperty());
                this.fieldName.textProperty().bindBidirectional(nv.configurationNameProperty());
                this.fieldAuthor.textProperty().bindBidirectional(nv.configurationAuthorProperty());
                this.fieldDescription.textProperty().bindBidirectional(nv.configurationDescriptionProperty());
                this.listViewChangelogEntries.setItems(createChangelogList(nv));
            }
        });
        this.nodeExportAction.disableProperty().bind(ProfileConfigSelectionController.INSTANCE.currentStepProperty().isEqualTo(ProfileConfigStep.CONFIGURATION_CREATE));
        this.nodeRemoveAction.disableProperty().bind(ProfileConfigSelectionController.INSTANCE.currentStepProperty().isEqualTo(ProfileConfigStep.CONFIGURATION_CREATE));
        if (this.nodeDesktopShortcut != null) {
            this.nodeDesktopShortcut.disableProperty().bind(ProfileConfigSelectionController.INSTANCE.currentStepProperty().isEqualTo(ProfileConfigStep.CONFIGURATION_CREATE));
        }
        this.toggleSwitchLaunchInUseMode.disableProperty().bind(ProfileConfigSelectionController.INSTANCE.currentStepProperty().isEqualTo(ProfileConfigStep.CONFIGURATION_CREATE));
    }

    private ObservableList<ChangelogEntryI> createChangelogList(LCConfigurationDescriptionI configurationDescription) {
        return FXCollections.observableArrayList(configurationDescription.getChangelogEntries().stream().sorted((e1, e2) -> e2.getWhen().compareTo(e1.getWhen())).collect(Collectors.toList()));
    }

    @Override
    public void initListener() {
        buttonValidate.setOnAction(e -> closeCurrentEdit(false, false));
        this.toggleSwitchLaunchInUseMode.selectedProperty().addListener((obs, ov, nv) -> {
            if (nv != this.editedConfiguration.get().launchInUseModeProperty().get()) {
                ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.SetDefaultEditAction(this.editedConfiguration.get(), toggleSwitchLaunchInUseMode.isSelected()));
            }
        });
    }

    //========================================================================

    // Class part : "Step part"
    //========================================================================
    private void closeCurrentEdit(boolean closeRequest, boolean buttonBack) {
        // Should save current configuration information
        ProfileConfigStep currentStep = ProfileConfigSelectionController.INSTANCE.currentStepProperty().get();
        if (currentStep != null) {
            if (currentStep == ProfileConfigStep.CONFIGURATION_EDIT) {
                ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.EditConfigurationAction(this.editedConfiguration.get(), null));
            } else if (currentStep == ProfileConfigStep.CONFIGURATION_CREATE && !closeRequest) {
                if (!buttonBack) {
                    ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.AddNewEditAction(this, this.editedConfiguration.get()));
                    closeRequest = true;
                }
            }
        }
        if (!closeRequest) {
            if (ProfileConfigSelectionController.INSTANCE.getPreviousStep() != null) {
                ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_LIST, ProfileConfigSelectionController.INSTANCE.getPreviousStep(), null);
            } else {
                ProfileConfigSelectionController.INSTANCE.hideStage();
            }
        }
        this.editedConfiguration.set(null);
    }

    @Override
    public boolean cancelRequest() {
        closeCurrentEdit(true, false);
        return false;
    }

    @Override
    public void beforeShow() {
        this.editedConfiguration.set(ProfileConfigSelectionController.INSTANCE.getConfigurationOption());
        this.toggleSwitchLaunchInUseMode.setSelected(editedConfiguration.get().launchInUseModeProperty().get());
    }

    @Override
    public Node getView() {
        return this;
    }
    //========================================================================

}
