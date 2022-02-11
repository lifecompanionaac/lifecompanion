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
package org.lifecompanion.config.view.pane.profile;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.util.Triple;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.profile.LCProfile;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.view.pane.profile.ProfileIconView;
import org.lifecompanion.config.data.action.impl.LCProfileActions;
import org.lifecompanion.config.data.component.profile.ProfileConfigSelectionController;
import org.lifecompanion.config.data.component.profile.ProfileConfigStep;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.configuration.DefaultConfigurationListPane;
import org.lifecompanion.config.view.pane.profilconfig.ProfileConfigStepViewI;
import org.lifecompanion.config.view.reusable.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.profile.LCProfileI;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * View to create a new profile.<br>
 * Allow user to enter profile informations.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileEditionView extends BorderPane implements LCViewInitHelper, ProfileConfigStepViewI {

    /**
     * Field to enter the profile name
     */
    private TextField fieldName;

    /**
     * Field to select profile color
     */
    private LCColorPicker fieldColor;

    /**
     * The current profile in create
     */
    private final ObjectProperty<LCProfileI> editedProfile;

    /**
     * View to display the icon of the created profile
     */
    private ProfileIconView profileIconView;

    private Button buttonRemove, buttonExport, buttonValidate;
    private List<Node> editionActionNodes;

    private DefaultConfigurationListPane defaultConfigurationListPane;


    /**
     * Create a new profile creation view
     */
    public ProfileEditionView() {
        this.editedProfile = new SimpleObjectProperty<>();
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        Triple<HBox, Label, Node> header = ConfigUIUtils.createHeader("profile.edition.view.title.config", node -> closeCurrentEdit(false, true));

        //Profile name
        Label labelName = new Label(Translation.getText("profile.label.name"));
        GridPane.setHgrow(labelName, Priority.ALWAYS);
        this.fieldName = new TextField();
        GridPane.setFillWidth(fieldName, false);
        fieldName.setPrefWidth(300.0);

        //Profile color
        Label labelColor = new Label(Translation.getText("profile.label.color"));
        this.fieldColor = new LCColorPicker();
        fieldColor.setMaxWidth(300.0);

        //Box for profile icon
        this.profileIconView = new ProfileIconView();
        this.profileIconView.setIconSizeFactor(2.0);
        GridPane.setValignment(profileIconView, VPos.CENTER);

        buttonValidate = UIUtils.createLeftTextButton(Translation.getText("profile.config.view.button.ok"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_PRIMARY), null);
        GridPane.setHalignment(buttonValidate, HPos.LEFT);

        // Information
        GridPane gridPaneIconInfo = new GridPane();
        gridPaneIconInfo.setVgap(5.0);
        gridPaneIconInfo.setHgap(20.0);
        gridPaneIconInfo.add(labelName, 1, 0);
        gridPaneIconInfo.add(fieldName, 1, 1);
        gridPaneIconInfo.add(labelColor, 1, 2);
        gridPaneIconInfo.add(fieldColor, 1, 3);
        gridPaneIconInfo.add(profileIconView, 0, 0, 1, 4);
        gridPaneIconInfo.add(buttonValidate, 1, 4, 1, 1);
        gridPaneIconInfo.setMinHeight(150.0);

        // Action buttons
        GridPane gridPaneActions = new GridPane();
        this.buttonExport = ConfigUIUtils.createActionTableEntry(0, "profile.selection.export.profile.button",
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).size(30).color(LCGraphicStyle.MAIN_DARK), gridPaneActions);
        this.buttonRemove = ConfigUIUtils.createActionTableEntry(2, "profile.selection.remove.profile.button",
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(30).color(LCGraphicStyle.SECOND_DARK), gridPaneActions);
        Label labelActions = UIUtils.createTitleLabel("profile.edition.general.actions.title");
        editionActionNodes = Arrays.asList(labelActions, gridPaneActions);

        // Default configuration to add on profile
        defaultConfigurationListPane = new DefaultConfigurationListPane(true);

        //Add
        VBox boxCenter = new VBox(10.0, UIUtils.createTitleLabel("profile.edition.general.information.title"), gridPaneIconInfo, labelActions, gridPaneActions, defaultConfigurationListPane);
        boxCenter.setPadding(new Insets(10.0));
        this.setTop(header.getLeft());
        this.setCenter(boxCenter);
    }


    @Override
    public void initListener() {
        buttonValidate.setOnAction(e -> closeCurrentEdit(false, false));
        this.buttonRemove.setOnAction((ea) -> {
            if (this.editedProfile.get() != null) {
                ConfigActionController.INSTANCE.executeAction(new LCProfileActions.RemoveProfileAction(buttonExport, this.editedProfile.get()));
            }
        });
        this.buttonExport.setOnAction((ea) -> {
            if (this.editedProfile.get() != null) {
                ConfigActionController.INSTANCE.executeAction(
                        new LCProfileActions.EditProfileAction(this.editedProfile.get(),
                                profile -> ConfigActionController.INSTANCE.executeAction(new LCProfileActions.ProfileExportAction(buttonExport, profile))));
            }
        });
    }


    @Override
    public void initBinding() {
        BooleanBinding profileCreateBooleanBinding = ProfileConfigSelectionController.INSTANCE.currentStepProperty().isEqualTo(ProfileConfigStep.PROFILE_CREATE).not();
        for (Node editionActionNode : editionActionNodes) {
            editionActionNode.managedProperty().bind(editionActionNode.visibleProperty());
            editionActionNode.visibleProperty().bind(profileCreateBooleanBinding);
        }
        defaultConfigurationListPane.managedProperty().bind(defaultConfigurationListPane.visibleProperty());
        defaultConfigurationListPane.visibleProperty().bind(profileCreateBooleanBinding.not());
        this.profileIconView.profileProperty().bind(this.editedProfile);
        //On created profile change, bind field
        this.editedProfile.addListener((obs, ov, nv) -> {
            if (ov != null) {
                this.fieldName.textProperty().unbindBidirectional(ov.nameProperty());
                this.fieldColor.valueProperty().unbindBidirectional(ov.colorProperty());
            }
            if (nv != null) {
                this.fieldName.textProperty().bindBidirectional(nv.nameProperty());
                this.fieldColor.valueProperty().bindBidirectional(nv.colorProperty());
            }
        });
    }
    //========================================================================

    // STEP
    //========================================================================
    private void closeCurrentEdit(boolean closeRequest, boolean previous) {
        // Add/save profile
        ProfileConfigStep currentStep = ProfileConfigSelectionController.INSTANCE.currentStepProperty().get();
        if (currentStep == ProfileConfigStep.PROFILE_CREATE && !previous) {
            List<Pair<LCConfigurationDescriptionI, File>> defaultConfigurationToAdd = defaultConfigurationListPane.getSelectedDefaultConfigurations();
            ConfigActionController.INSTANCE.executeAction(new LCProfileActions.AddProfileAction(this, this.editedProfile.get(), defaultConfigurationToAdd));
        } else if (currentStep == ProfileConfigStep.PROFILE_EDIT) {
            ConfigActionController.INSTANCE.executeAction(new LCProfileActions.EditProfileAction(this.editedProfile.get(), null));
        }
        // Go back if not closing
        if (!closeRequest) {
            if (ProfileConfigSelectionController.INSTANCE.getPreviousStep() != null) {
                ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigSelectionController.INSTANCE.getPreviousStep(), null, null);
            } else {
                ProfileConfigSelectionController.INSTANCE.hideStage();
            }
        }
        this.editedProfile.set(null);
    }

    @Override
    public boolean cancelRequest() {
        if (ProfileConfigSelectionController.INSTANCE.showNoProfileWarning(this)) return true;
        this.closeCurrentEdit(true, false);
        return false;
    }

    @Override
    public void beforeShow() {
        if (ProfileConfigSelectionController.INSTANCE.currentStepProperty().get() == ProfileConfigStep.PROFILE_EDIT) {
            this.editedProfile.set(ProfileConfigSelectionController.INSTANCE.getProfileOption());
        } else {
            defaultConfigurationListPane.initDefaultConfigurations();
            this.editedProfile.set(new LCProfile());
        }
    }

    @Override
    public Node getView() {
        return this;
    }
    //========================================================================

}
