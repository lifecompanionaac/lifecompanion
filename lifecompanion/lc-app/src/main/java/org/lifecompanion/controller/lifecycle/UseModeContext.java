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

package org.lifecompanion.controller.lifecycle;

import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.model.api.configurationcomponent.IdentifiableComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;

import java.util.Map;
import java.util.stream.Collectors;

public class UseModeContext extends AbstractModeContext {
    private UseModeState savedUseState;

    @Override
    public void cleanAfterStop() {
        this.configurationDescription.set(null);
        this.configuration.set(null);
        if (stage.get() != null) {
            stage.get().hide();
            this.stage.set(null);
        }
    }

    void saveStateBeforeStop() {
        LCConfigurationI configuration = this.configuration.get();
        Map<String, String> displayedComponentsInStack = configuration.getAllComponent().values()
                .stream()
                .filter(c -> c instanceof StackComponentI)
                .map(c -> (StackComponentI) c)
                .collect(Collectors.toMap(IdentifiableComponentI::getID, stack -> stack.displayedComponentProperty().get() != null ? stack.displayedComponentProperty().get().getID() : "none"));
        this.savedUseState = new UseModeState(displayedComponentsInStack, KeyListController.INSTANCE.getCurrentNodeId());
    }

    UseModeState getUseStateAndClear() {
        UseModeState useModeState = savedUseState;
        savedUseState = null;
        return useModeState;
    }

    static class UseModeState {
        private final Map<String, String> displayedComponentInStack;
        private final String currentKeyListNodeId;

        public UseModeState(Map<String, String> displayedComponentInStack, String currentKeyListNodeId) {
            this.displayedComponentInStack = displayedComponentInStack;
            this.currentKeyListNodeId = currentKeyListNodeId;
        }

        public Map<String, String> getDisplayedComponentInStack() {
            return displayedComponentInStack;
        }

        public String getCurrentKeyListNodeId() {
            return currentKeyListNodeId;
        }
    }
}
