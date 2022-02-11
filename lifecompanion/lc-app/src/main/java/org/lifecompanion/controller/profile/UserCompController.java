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
package org.lifecompanion.controller.profile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.io.IOManager;
import org.lifecompanion.controller.io.task.MultiUserCompDescriptionLoadingTask;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Manage all available user comp for the current profile.<br>
 * User comp are loaded on profile change.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UserCompController implements LCStateListener {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCompController.class);

    private final ObservableList<UserCompDescriptionI> userComponents;

    UserCompController() {
        this.userComponents = FXCollections.observableArrayList();
    }

    public ObservableList<UserCompDescriptionI> getUserComponents() {
        return this.userComponents;
    }

    public UserCompDescriptionI getCompBy(final String id) {
        for (UserCompDescriptionI userComp : this.userComponents) {
            if (StringUtils.isEquals(id, userComp.getSavedComponentId())) {
                return userComp;
            }
        }
        return null;
    }

    public void replace(final UserCompDescriptionI comp, final UserCompDescriptionI newComp) {
        int indexOf = this.userComponents.indexOf(comp);
        if (indexOf >= 0) {
            this.userComponents.set(indexOf, newComp);
        }
    }

    private void addCompAfterSave(final UserCompDescriptionI comp) {
        this.userComponents.add(comp);
    }

    public Predicate<UserCompDescriptionI> getPredicateFor(final String terms) {
        String[] termArray = terms != null ? terms.split(" ") : new String[]{};
        if (terms.length() > 0) {
            List<String> termList = Arrays.stream(termArray).filter(s -> s.length() > 2).collect(Collectors.toList());
            return (userComp) -> {
                return StringUtils.startWithIgnoreCase(userComp.nameProperty().get(), terms)
                        || StringUtils.countContainsIgnoreCase(userComp.nameProperty().get(), termList) > 0;
            };
        } else {
            return (userComp) -> true;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void lcStart() {
        ProfileController.INSTANCE.currentProfileProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                MultiUserCompDescriptionLoadingTask loadTask = IOManager.INSTANCE.createMultiUserCompDescriptionLoadingTask(nv);
                loadTask.setOnSucceeded(e -> {
                    List<UserCompDescriptionI> loadedComps = (List<UserCompDescriptionI>) e.getSource().getValue();
                    //Remove all delete
                    LCUtils.runOnFXThread(() -> {
                        this.userComponents.clear();
                        for (UserCompDescriptionI toAdd : loadedComps) {
                            this.addCompAfterSave(toAdd);
                        }
                    });
                });
                loadTask.setOnFailed(e -> LCUtils.runOnFXThread(this.userComponents::clear));
                AsyncExecutorController.INSTANCE.addAndExecute(true, true, loadTask);
            }
        });
    }

    @Override
    public void lcExit() {
    }
}
