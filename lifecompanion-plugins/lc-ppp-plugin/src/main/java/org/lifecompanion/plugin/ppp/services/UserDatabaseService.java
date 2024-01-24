package org.lifecompanion.plugin.ppp.services;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.ppp.keyoption.UserGroupCellKeyOption;
import org.lifecompanion.plugin.ppp.keyoption.UserProfileCellKeyOption;
import org.lifecompanion.plugin.ppp.model.Action;
import org.lifecompanion.plugin.ppp.model.UserDatabase;
import org.lifecompanion.plugin.ppp.model.UserGroup;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.tasks.SyncDatabasesTask;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public enum UserDatabaseService implements ModeListenerI {
    INSTANCE;

    private LCConfigurationI configuration;
    private UserDatabase currentUserDatabase;
    private final List<UserGroupCellKeyOption> groupKeyOptions;
    private final List<UserProfileCellKeyOption> userKeyOptions;
    private final ObjectProperty<UserGroup> selectedGroup;
    private final ObjectProperty<UserProfile> selectedProfile;

    UserDatabaseService() {
        this.userKeyOptions = FXCollections.observableArrayList();
        this.groupKeyOptions = FXCollections.observableArrayList();
        this.selectedGroup = new SimpleObjectProperty<>();
        this.selectedProfile = new SimpleObjectProperty<>();

        this.selectedGroup.addListener((obs, ov, nv) -> {
            this.updateUserList();
        });
    }

    public UserProfile getSelectedProfile() {
        return selectedProfile.get();
    }

    public ReadOnlyObjectProperty<UserProfile> selectedProfileProperty() {
        return selectedProfile;
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.configuration = configuration;
        // Find cells
        Map<GridComponentI, List<UserGroupCellKeyOption>> groupKeysMap = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(UserGroupCellKeyOption.class, configuration, groupKeysMap, null);
        groupKeysMap.values().stream().flatMap(List::stream).distinct().forEach(groupKeyOptions::add);
        Map<GridComponentI, List<UserProfileCellKeyOption>> userKeysMap = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(UserProfileCellKeyOption.class, configuration, userKeysMap, null);
        userKeysMap.values().stream().flatMap(List::stream).distinct().forEach(userKeyOptions::add);

        loadCurrentUserDatabase();
    }

    private void loadCurrentUserDatabase() {
        currentUserDatabase = loadDatabase(configuration);
        this.updateGroupList();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        selectedGroup.set(null);
        selectedProfile.set(null);
        groupKeyOptions.clear();
        userKeyOptions.clear();
        currentUserDatabase = null;
        this.configuration = null;
    }

    public void selectGroup(UserGroup userGroup) {
        selectedGroup.set(userGroup);
    }

    public void selectUser(UserProfile user) {
        selectedProfile.set(user);
    }

    private void updateGroupList() {
        updateCells(0, currentUserDatabase.getGroups(), groupKeyOptions, (cell, group) -> cell.groupProperty().set(group));
    }

    private void updateUserList() {
        UserGroup userGroup = this.selectedGroup.get();
        if (userGroup != null) {
            updateCells(0, userGroup.getUsers(), userKeyOptions, (cell, group) -> cell.userProperty().set(group));
        } else {
            updateCells(0, new ArrayList<>(), userKeyOptions, (cell, user) -> cell.userProperty().set(null));
        }

    }

    private static <T, E> void updateCells(int currentPageIndex, List<T> sourceList, List<E> cellList, BiConsumer<E, T> setter) {
        int cellListSize = cellList.size();
        for (int cellIndex = 0; cellIndex < cellListSize; cellIndex++) {
            int itemIndex = currentPageIndex * cellListSize + cellIndex;
            E cell = cellList.get(cellIndex);
            T item = itemIndex < sourceList.size() ? sourceList.get(itemIndex) : null;
            FXThreadUtils.runOnFXThread(() -> setter.accept(cell, item));
        }
    }


    public void saveDatabase(LCConfigurationI config, UserDatabase databaseService) {
        FilesService.INSTANCE.jsonSave(databaseService, this.getDatabaseFilePath(FilesService.INSTANCE.getPluginDirectoryPath(config)));
    }

    public UserDatabase loadDatabase(String dataDirectory) {
        UserDatabase profile = FilesService.INSTANCE.jsonLoadOne(UserDatabase.class,
                this.getDatabaseFilePath(dataDirectory));
        return profile == null ? new UserDatabase() : profile;
    }

    public UserDatabase loadDatabase(LCConfigurationI config) {
        return this.loadDatabase(FilesService.INSTANCE.getPluginDirectoryPath(config));
    }

    public String getDatabaseFilePath(String dataDirectory) {
        return dataDirectory + File.separator + "users.json";
    }


    public void updateActionsForCurrentProfile(LCConfigurationI config, ObservableList<Action> actions) {
        UserProfile userProfile = selectedProfile.get();
        if (userProfile != null) {
            userProfile.setActions(new ArrayList<>(actions));
            saveDatabase(config, this.currentUserDatabase);
        }
    }

    public void syncDatabases() {
        SyncDatabasesTask task = new SyncDatabasesTask(FilesService.INSTANCE.getPluginDirectoryPath(AppModeController.INSTANCE.getUseModeContext().getConfiguration()));
        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                loadCurrentUserDatabase();
            }
        });
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, task);
    }
}
