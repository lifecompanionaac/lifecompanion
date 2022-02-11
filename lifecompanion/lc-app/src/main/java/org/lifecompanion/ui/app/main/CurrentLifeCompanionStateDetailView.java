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
package org.lifecompanion.ui.app.main;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.configurationcomponent.TreeDisplayableType;
import org.lifecompanion.ui.app.displayablecomponent.CommonComponentStage;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Bottom pane, that contains informations of current actions, configuration, etc...
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CurrentLifeCompanionStateDetailView extends HBox implements LCViewInitHelper {
    private static final double CONFIG_ACTION_LIST_WIDTH = 350, CONFIG_ACTION_LIST_HEIGHT = 100;

    private Label labelCurrentConfigurationName;
    private Label labelCurrentUnsavedModifications;
    private Label labelCurrentProfile;
    private Label labelConfigurationSize;
    private Label labelCurrentComponentName;
    private Label labelCurrentDetailName;
    private ListView<String> configActionListView;
    private BorderPane paneConfigAction;

    private final Map<TreeDisplayableType, ImageView> componentTypeImageViews;

    public CurrentLifeCompanionStateDetailView() {
        componentTypeImageViews = new HashMap<>();
        this.initAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initUI() {
        HBox boxLeft = new HBox();
        //Create label
        this.labelCurrentConfigurationName = new Label(Translation.getText("configuration.menu.no.name"));
        this.labelCurrentUnsavedModifications = new Label();
        this.labelConfigurationSize = new Label();
        this.labelCurrentComponentName = new Label();
        this.labelCurrentComponentName.setGraphicTextGap(10.0);
        this.labelCurrentComponentName.setFont(Font.font(this.labelCurrentComponentName.getFont().getFamily(), FontWeight.BOLD, 14));
        this.labelCurrentDetailName = new Label();
        this.labelCurrentProfile = new Label(Translation.getText("profile.name.no.loaded"));
        HBox.setMargin(this.labelCurrentConfigurationName, new Insets(5));
        HBox.setMargin(this.labelCurrentUnsavedModifications, new Insets(5));
        HBox.setMargin(this.labelCurrentProfile, new Insets(5));
        boxLeft.getChildren().addAll(this.labelCurrentProfile, new Separator(Orientation.VERTICAL), this.labelCurrentConfigurationName,
                new Separator(Orientation.VERTICAL), this.labelConfigurationSize, new Separator(Orientation.VERTICAL),
                this.labelCurrentUnsavedModifications);
        boxLeft.setAlignment(Pos.CENTER_LEFT);

        //Config action list
        this.configActionListView = new ListView<>(ConfigActionController.INSTANCE.getActionStringList());
        //Pane with list and title
        this.paneConfigAction = new BorderPane(this.configActionListView);
        Label labelTitleActions = new Label(Translation.getText("config.action.currently.done"));
        labelTitleActions.getStyleClass().add("menu-part-title");
        BorderPane.setMargin(this.configActionListView, new Insets(10));
        BorderPane.setMargin(labelTitleActions, new Insets(0, 5, 0, 5));
        labelTitleActions.setMaxWidth(Double.MAX_VALUE);
        this.configActionListView.setPrefWidth(CurrentLifeCompanionStateDetailView.CONFIG_ACTION_LIST_WIDTH);
        this.configActionListView.setPrefHeight(CurrentLifeCompanionStateDetailView.CONFIG_ACTION_LIST_HEIGHT);
        this.paneConfigAction.setTop(labelTitleActions);

        // Box right : current component name
        HBox boxRight = new HBox(this.labelCurrentComponentName, this.labelCurrentDetailName);
        boxRight.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(boxRight, Priority.ALWAYS);
        HBox.setMargin(boxRight, new Insets(0.0, 5, 0.0, 0.0));
        boxRight.setMaxHeight(30.0);

        // Init component icons
        for (TreeDisplayableType compTypes : TreeDisplayableType.values()) {
            componentTypeImageViews.put(compTypes, compTypes.isIconValid() ? new ImageView(IconHelper.get(compTypes.getIconPath())) : null);
        }

        //Add
        this.getChildren().addAll(boxLeft, boxRight);
    }

    @Override
    public void initListener() {
        this.labelCurrentProfile.setOnMouseClicked(me -> {
            LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
            if (profile != null) {
                ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_EDIT, null, profile);
            }
        });
        this.labelConfigurationSize.setOnMouseClicked(me -> GeneralConfigurationController.INSTANCE.showStep(GeneralConfigurationStep.STAGE_SETTINGS));
        this.labelCurrentConfigurationName.setOnMouseClicked(me -> {
            LCConfigurationDescriptionI configuration = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();
            if (configuration != null) {
                ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_EDIT, null, configuration);
            }
        });
        EventHandler<? super MouseEvent> showCurrentComponentPart = me -> {
            if (SelectionController.INSTANCE.selectedComponentBothProperty().get() != null) {
                CommonComponentStage.getInstance().show();
            }
        };
        this.labelCurrentComponentName.setOnMouseClicked(showCurrentComponentPart);
        this.labelCurrentDetailName.setOnMouseClicked(showCurrentComponentPart);
    }


    @Override
    public void initBinding() {
        //Bind message
        AppModeController.INSTANCE.getEditModeContext().configurationProperty()
                .addListener((observableP, oldValueP, newValueP) -> {
                    if (oldValueP != null) {
                        LCUtils.unbindAndSet(labelCurrentUnsavedModifications.textProperty(), "");
                        LCUtils.unbindAndSet(labelConfigurationSize.textProperty(), "");
                    }
                    if (newValueP != null) {
                        // Specific binding as we want it as String
                        this.labelConfigurationSize.textProperty().bind(Bindings.createStringBinding(() -> (int) newValueP.computedWidthProperty().get() + "x" + (int) newValueP.computedHeightProperty().get(), newValueP.computedWidthProperty(), newValueP.computedHeightProperty()));
                        this.labelCurrentUnsavedModifications.textProperty().bind(TranslationFX.getTextBinding("status.bar.label.current.unsaved.modifications", newValueP.unsavedActionProperty()));
                    }
                });
        //Bind configuration description name
        this.labelCurrentConfigurationName.textProperty().bind(EasyBind.select(AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty())
                .selectObject(LCConfigurationDescriptionI::configurationNameProperty).orElse(Translation.getText("configuration.label.no.current")));
        //Bind component name
        this.labelCurrentComponentName.textProperty().bind(EasyBind.select(SelectionController.INSTANCE.selectedComponentBothProperty())
                .selectObject(DisplayableComponentI::nameProperty).orElse(Translation.getText("no.component.selected.simple")));

        this.labelCurrentUnsavedModifications.textProperty().bind(TranslationFX.getTextBinding("status.bar.label.current.unsaved.modifications", AppModeController.INSTANCE.getEditModeContext().configurationUnsavedActionProperty()));

        //Bind parent name + graphics
        SelectionController.INSTANCE.selectedComponentBothProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                LCUtils.unbindAndSetNull(this.labelCurrentDetailName.textProperty());
                labelCurrentComponentName.setGraphic(null);
            }
            if (nv != null) {
                this.labelCurrentDetailName.textProperty().bind(Bindings.createStringBinding(() -> !StringUtils.isBlank(nv.detailNameProperty().get()) ? " (" + nv.detailNameProperty().get() + ")" : "", nv.detailNameProperty()));
                labelCurrentComponentName.setGraphic(componentTypeImageViews.get(nv.getNodeType()));
            }
        });
        //Bind profile name
        ProfileController.INSTANCE.currentProfileProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                this.labelCurrentProfile.textProperty().unbind();
            }
            if (nv != null) {
                this.labelCurrentProfile.textProperty().bind(nv.nameProperty());
            }
        });
    }
}
