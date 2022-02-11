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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.ui.common.pane.specific.cell.ConfigurationDescriptionAdvancedListCell;
import org.lifecompanion.util.javafx.DisableSelectionSelectionModel;
import org.lifecompanion.util.model.Triple;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.ui.common.pane.specific.ProfileIconView;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ProfileConfigSelectionController;
import org.lifecompanion.controller.editmode.ProfileConfigStep;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.util.ConfigUIUtils;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

/**
 * View to select a configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationSelectionView extends BorderPane implements ProfileConfigStepViewI, LCViewInitHelper {
    private Button buttonAddConfiguration;

    /**
     * Filter to search for configurations
     */
    private TextField fieldSearchFilter;

    /**
     * Configuration list
     */
    private final ObservableList<LCConfigurationDescriptionI> configurationList;

    /**
     * List of configuration
     */
    private final FilteredList<LCConfigurationDescriptionI> filteredConfigurationList;

    private Label labelProfileName;
    private Button buttonChangeProfile;
    private ProfileIconView profileIconView;

    /**
     * Previous list binding
     */
    private Subscription previousListBinding;

    public ConfigurationSelectionView() {
        this.configurationList = FXCollections.observableArrayList();
        this.filteredConfigurationList = new FilteredList<>(this.configurationList);
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        Triple<HBox, Label, Node> header = ConfigUIUtils.createHeader("configuration.selection.view.title", e -> ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_LIST, null, null));
        this.setTop(header.getLeft());

        //Search filter
        this.fieldSearchFilter = TextFields.createClearableTextField();
        this.fieldSearchFilter.setPromptText(Translation.getText("configuration.list.search.tips"));

        // Current profile information
        this.labelProfileName = new Label();
        this.labelProfileName.getStyleClass().add("current-profile-in-config-list");
        buttonChangeProfile = UIUtils.createRightTextButton(Translation.getText("configuration.list.profile.change.action"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.REFRESH).size(14).color(LCGraphicStyle.MAIN_PRIMARY), null);
        buttonChangeProfile.getStyleClass().add("button-in-current-profil-config-list");
        this.profileIconView = new ProfileIconView();
        this.profileIconView.setIconSizeFactor(0.6);
        GridPane gridPaneCurrentProfile = new GridPane();
        gridPaneCurrentProfile.setHgap(10.0);
        gridPaneCurrentProfile.add(profileIconView, 0, 0, 1, 2);
        gridPaneCurrentProfile.add(labelProfileName, 1, 0);
        gridPaneCurrentProfile.add(buttonChangeProfile, 1, 1);

        //Center : list
        ListView<LCConfigurationDescriptionI> configurationListView = new ListView<>(this.filteredConfigurationList);
        configurationListView.setPlaceholder(new Label(Translation.getText("configuration.list.empty.placeholder")));
        configurationListView.setFixedCellSize(150.0);
        configurationListView.setCellFactory((lv) -> new ConfigurationDescriptionAdvancedListCell(this::configurationSelected));
        configurationListView.setSelectionModel(new DisableSelectionSelectionModel<>());
        VBox.setVgrow(configurationListView, Priority.ALWAYS);

        VBox boxCenter = new VBox(10.0, gridPaneCurrentProfile, fieldSearchFilter, configurationListView);
        boxCenter.setPadding(new Insets(10.0));

        buttonAddConfiguration = UIUtils.createRightTextButton(Translation.getText("configuration.selection.add.configuration.button"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(22).color(LCGraphicStyle.MAIN_PRIMARY), "configuration.selection.add.configuration.button.tooltip");
        buttonAddConfiguration.getStyleClass().add("button-icon-text-bigger");
        HBox bottomButtons = new HBox(10.0, buttonAddConfiguration);
        bottomButtons.setAlignment(Pos.CENTER);
        BorderPane.setMargin(bottomButtons, new Insets(0, 10.0, 10.0, 10.0));

        //Total
        this.setBottom(bottomButtons);
        this.setCenter(boxCenter);
    }

    @Override
    public void initListener() {
        this.fieldSearchFilter.textProperty().addListener((obs, ov, nv) -> {
            if (StringUtils.isBlank(nv)) {
                this.filteredConfigurationList.setPredicate(null);
            } else {
                this.filteredConfigurationList.setPredicate((desc) ->
                        StringUtils.startWithIgnoreCase(desc.configurationNameProperty().get(), nv)
                                || StringUtils.containsIgnoreCase(desc.configurationNameProperty().get(), nv)
                                || StringUtils.containsIgnoreCase(desc.configurationDescriptionProperty().get(), nv));
            }
        });
        this.buttonAddConfiguration.setOnAction(e -> ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_ADD, ProfileConfigStep.CONFIGURATION_LIST, null));
        this.buttonChangeProfile.setOnAction((ea) -> ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_LIST, null, null));
    }

    @Override
    public void initBinding() {
        this.labelProfileName.textProperty().bind(EasyBind.select(ProfileController.INSTANCE.currentProfileProperty()).selectObject(LCProfileI::nameProperty));
        this.profileIconView.profileProperty().bind(ProfileController.INSTANCE.currentProfileProperty());
    }
    //========================================================================

    // Class part : "Step part"
    //========================================================================
    private void configurationSelected(final LCConfigurationDescriptionI configurationDescription) {
        ProfileConfigSelectionController.INSTANCE.hideStage();
        if (configurationDescription != null) {
            ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.OpenConfigurationAction(this, configurationDescription));
        }
    }

    @Override
    public boolean cancelRequest() {
        return false;
    }

    @Override
    public void beforeShow() {
        if (this.previousListBinding != null) {
            this.previousListBinding.unsubscribe();
        }
        this.previousListBinding = EasyBind.listBind(this.configurationList, ProfileController.INSTANCE.currentProfileProperty().get().getConfiguration());
    }

    @Override
    public Node getView() {
        return this;
    }
    //========================================================================

}
