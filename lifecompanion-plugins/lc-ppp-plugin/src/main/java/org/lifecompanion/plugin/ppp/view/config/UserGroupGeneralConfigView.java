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

package org.lifecompanion.plugin.ppp.view.config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.ppp.model.UserGroup;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.view.commons.FormatterListCell;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.control.generic.OrderModifiableListView;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.ArrayList;
import java.util.Collections;


public class UserGroupGeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    public static final String STEP_NAME = "UserGroupGeneralConfigView";

    private TextField fieldGroupId;
    private TextField fieldGroupName;
    private UserGroup editedGroup;
    private ObservableList<UserProfile> editedUsers;
    private OrderModifiableListView<UserProfile> userListView;

    public UserGroupGeneralConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "user group";
    }

    @Override
    public String getStep() {
        return STEP_NAME;
    }

    @Override
    public String getPreviousStep() {
        return UserDatabaseGeneralConfigView.STEP_NAME;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() {

        // Group info
        Label labelGroupName = new Label(Translation.getText("ppp.plugin.view.config.group.name.field"));
        labelGroupName.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        this.fieldGroupName = new TextField();
        GridPane.setHgrow(this.fieldGroupName, Priority.ALWAYS);
        this.fieldGroupName.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHalignment(this.fieldGroupName, HPos.RIGHT);

        Label labelGroupId = new Label(Translation.getText("ppp.plugin.view.config.group.id.field"));
        labelGroupId.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        this.fieldGroupId = new TextField();
        fieldGroupId.setEditable(false);
        GridPane.setHgrow(this.fieldGroupId, Priority.ALWAYS);
        this.fieldGroupId.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHalignment(this.fieldGroupId, HPos.RIGHT);

        GridPane configLayout = new GridPane();
        configLayout.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        configLayout.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        configLayout.add(labelGroupName, 0, 0);
        configLayout.add(this.fieldGroupName, 1, 0);
        configLayout.add(labelGroupId, 0, 1);
        configLayout.add(this.fieldGroupId, 1, 1);

        // Group users
        userListView = new OrderModifiableListView<>(true);
        userListView.setCellFactory(listView -> new FormatterListCell<>(UserProfile::getUserName));
        userListView.getButtonModify().setVisible(true);

        VBox vboxTotal = new VBox(5.0,
                FXControlUtils.createTitleLabel(Translation.getText("ppp.plugin.view.config.group.detail.title")),
                configLayout,
                FXControlUtils.createTitleLabel(Translation.getText("ppp.plugin.view.config.group.users.title")),
                userListView
        );
        vboxTotal.setPadding(new Insets(5.0));
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        ScrollPane scrollPane = new ScrollPane(vboxTotal);
        scrollPane.setFitToWidth(true);
        this.setCenter(scrollPane);
    }

    @Override
    public void initBinding() {
    }

    @Override
    public void initListener() {
        this.userListView.getButtonAdd().setOnAction(e -> {
            UserProfile addedElement = new UserProfile();
            addedElement.setUserName(Translation.getText("ppp.plugin.view.config.default.user.name"));
            editedUsers.add(addedElement);
            this.userListView.select(addedElement);
            this.userListView.scrollTo(addedElement);
        });
        this.userListView.getButtonRemove().setOnAction(e -> {
            UserProfile selectedElement = this.userListView.getSelectedItem();
            if (selectedElement != null) {
                editedUsers.remove(selectedElement);
            }
        });
        this.userListView.getButtonUp().setOnAction((ae) -> {
            UserProfile selectedElement = this.userListView.getSelectedItem();
            if (selectedElement != null) {
                int index = editedUsers.indexOf(selectedElement);
                if (index > 0) {
                    Collections.swap(editedUsers, index, index - 1);
                }
                this.userListView.select(selectedElement);
                this.userListView.scrollTo(selectedElement);
            }
        });
        this.userListView.getButtonDown().setOnAction((ae) -> {
            UserProfile selectedElement = this.userListView.getSelectedItem();
            if (selectedElement != null) {
                int index = editedUsers.indexOf(selectedElement);
                if (index < editedUsers.size() - 1) {
                    Collections.swap(editedUsers, index, index + 1);
                }
                this.userListView.select(selectedElement);
                this.userListView.scrollTo(selectedElement);
            }
        });
        this.userListView.getButtonModify().setOnAction(e -> {
            UserProfile selectedElement = this.userListView.getSelectedItem();
            if (selectedElement != null) {
                GeneralConfigurationController.INSTANCE.showStep(UserProfileGeneralConfigView.STEP_NAME, selectedElement);
            }
        });
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        if (stepArgs.length > 0) {
            editedGroup = (UserGroup) stepArgs[0];
        }
        editedUsers = FXCollections.observableArrayList(editedGroup.getUsers());
        this.userListView.setItems(editedUsers);
        this.fieldGroupId.setText(editedGroup.getGroupId());
        this.fieldGroupName.setText(editedGroup.getGroupName());
    }

    @Override
    public void afterHide() {
        if (editedGroup != null) {
            editedGroup.setUsers(new ArrayList<>(editedUsers));
            editedGroup.setGroupId(fieldGroupId.getText());
            editedGroup.setGroupName(fieldGroupName.getText());
        }
    }

    @Override
    public void saveChanges() {
    }

    @Override
    public void cancelChanges() {
    }

    @Override
    public void bind(LCConfigurationI config) {
    }

    @Override
    public void unbind(LCConfigurationI config) {
        this.editedGroup = null;
        this.editedUsers = null;
        this.fieldGroupName.setText(null);
        this.fieldGroupId.setText(null);
        this.userListView.setItems(null);
    }
}
