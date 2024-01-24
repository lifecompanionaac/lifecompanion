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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.ppp.model.UserDatabase;
import org.lifecompanion.plugin.ppp.model.UserGroup;
import org.lifecompanion.plugin.ppp.tasks.LoadUserDatabaseTask;
import org.lifecompanion.plugin.ppp.tasks.SaveUserDatabaseTask;
import org.lifecompanion.plugin.ppp.view.commons.FormatterListCell;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.control.generic.OrderModifiableListView;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;


public class UserDatabaseGeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    public static final String STEP_NAME = "UserDatabaseGeneralConfigView";

    private LCConfigurationI editedConfiguration;
    private UserDatabase editedUserDatabase;
    private ObservableList<UserGroup> editedGroups;
    private OrderModifiableListView<UserGroup> groupListView;

    public UserDatabaseGeneralConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "ppp.plugin.view.config.title";
    }

    @Override
    public String getStep() {
        return STEP_NAME;
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() {

        groupListView = new OrderModifiableListView<>(true);
        groupListView.setCellFactory(listView -> new FormatterListCell<>(UserGroup::getGroupName));
        groupListView.getButtonModify().setVisible(true);

        VBox vboxTotal = new VBox(5.0,
                FXControlUtils.createTitleLabel(Translation.getText("ppp.plugin.view.config.groups.title")),
                groupListView
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
        this.groupListView.getButtonAdd().setOnAction(e -> {
            UserGroup addedElement = new UserGroup();
            addedElement.setGroupName(Translation.getText("ppp.plugin.view.config.default.group.name"));
            editedGroups.add(addedElement);
            this.groupListView.select(addedElement);
            this.groupListView.scrollTo(addedElement);
        });
        this.groupListView.getButtonRemove().setOnAction(e -> {
            UserGroup selectedElement = this.groupListView.getSelectedItem();
            if (selectedElement != null) {
                editedGroups.remove(selectedElement);
            }
        });
        this.groupListView.getButtonUp().setOnAction((ae) -> {
            UserGroup selectedElement = this.groupListView.getSelectedItem();
            if (selectedElement != null) {
                int index = editedGroups.indexOf(selectedElement);
                if (index > 0) {
                    Collections.swap(editedGroups, index, index - 1);
                }
                this.groupListView.select(selectedElement);
                this.groupListView.scrollTo(selectedElement);
            }
        });
        this.groupListView.getButtonDown().setOnAction((ae) -> {
            UserGroup selectedElement = this.groupListView.getSelectedItem();
            if (selectedElement != null) {
                int index = editedGroups.indexOf(selectedElement);
                if (index < editedGroups.size() - 1) {
                    Collections.swap(editedGroups, index, index + 1);
                }
                this.groupListView.select(selectedElement);
                this.groupListView.scrollTo(selectedElement);
            }
        });
        this.groupListView.getButtonModify().setOnAction(e -> {
            UserGroup selectedElement = this.groupListView.getSelectedItem();
            if (selectedElement != null) {
                GeneralConfigurationController.INSTANCE.showStep(UserGroupGeneralConfigView.STEP_NAME, selectedElement);
            }
        });
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
    }

    @Override
    public void afterHide() {
    }

    @Override
    public void saveChanges() {
        // TODO : compare group with "before" groups
        editedUserDatabase.setGroups(new ArrayList<>(editedGroups));
        editedUserDatabase.setModifiedAt(ZonedDateTime.now());
        // TODO : detect changes before save, save only if needed
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, new SaveUserDatabaseTask(editedConfiguration, editedUserDatabase));
    }

    @Override
    public void cancelChanges() {
    }

    @Override
    public void bind(LCConfigurationI config) {
        this.editedConfiguration = config;
        LoadUserDatabaseTask loadTask = new LoadUserDatabaseTask(editedConfiguration);
        loadTask.setOnSucceeded(e -> {
            this.editedUserDatabase = loadTask.getValue();
            this.editedGroups = FXCollections.observableArrayList(editedUserDatabase.getGroups());
            this.groupListView.setItems(editedGroups);
        });
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, loadTask);
    }

    @Override
    public void unbind(LCConfigurationI config) {
        this.editedConfiguration = null;
        this.editedUserDatabase = null;
        this.editedGroups = null;
        this.groupListView.setItems(null);
    }
}
