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
package org.lifecompanion.controller.editaction;

import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.SelectionModeUserI;
import org.lifecompanion.model.api.selectionmode.AutoDirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.DirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.ScanningSelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.editaction.BasePropertyChangeAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class that keep action for selection mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectionModeActions {

    /**
     * Change selection mode
     */
    public static class ChangeSelectionModeAction extends BasePropertyChangeAction<Class<? extends SelectionModeI>> {
        private final LCConfigurationI configuration;
        private final SelectionModeUserI selectionModeUser;
        private List<SelectionModeUserI> parentSelectionModeSet;

        public ChangeSelectionModeAction(final LCConfigurationI configuration, final SelectionModeUserI selectionModeUserP,
                                         final Class<? extends SelectionModeI> selectionModeTypeP) {
            super(selectionModeUserP.getSelectionModeParameter().selectionModeTypeProperty(), selectionModeTypeP);
            this.configuration = configuration;
            this.selectionModeUser = selectionModeUserP;
        }

        @Override
        public void doAction() throws LCException {
            super.doAction();
            /*
             * ISSUE #144 : if selection mode change on configuration.
             * When the new selection mode is a different type than the custom selection (e.g. go from scanning to direct), the custom selection mode is cancelled.
             * However, if it's the same type (e.g. go from line/column to column/line), the custom selection mode is kept.
             */
            if (configuration != null && !this.selectionModeUser.canUseParentSelectionModeConfigurationProperty().get()) {
                parentSelectionModeSet = new ArrayList<>();
                byte selectedModeType = getSelectionModeType(wantedValue);
                final Set<String> allComponentIds = configuration.getAllComponent().keySet();
                for (String id : allComponentIds) {
                    final DisplayableComponentI component = configuration.getAllComponent().get(id);
                    if (component instanceof SelectionModeUserI) {
                        final SelectionModeUserI otherSelectionModeUser = (SelectionModeUserI) component;
                        if (otherSelectionModeUser.canUseParentSelectionModeConfigurationProperty().get()) {
                            if (selectedModeType != getSelectionModeType(
                                    otherSelectionModeUser.getSelectionModeParameter().selectionModeTypeProperty().get())) {
                                otherSelectionModeUser.useParentSelectionModeProperty().set(true);
                                parentSelectionModeSet.add(otherSelectionModeUser);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void undoAction() throws LCException {
            super.undoAction();
            if (parentSelectionModeSet != null) {
                for (SelectionModeUserI otherSelectionModeUser : parentSelectionModeSet) {
                    otherSelectionModeUser.useParentSelectionModeProperty().set(false);
                }
            }
        }

        @Override
        public String getNameID() {
            return "action.selectionmode.select.modetype";
        }

        private byte getSelectionModeType(final Class<? extends SelectionModeI> selectionModeTypeP) {
            if (selectionModeTypeP != null) {
                if (AutoDirectSelectionModeI.class.isAssignableFrom(selectionModeTypeP)) {
                    return 1;
                } else if (DirectSelectionModeI.class.isAssignableFrom(selectionModeTypeP)) {
                    return 2;
                } else if (ScanningSelectionModeI.class.isAssignableFrom(selectionModeTypeP)) {
                    return 3;
                }
            }
            return -1;
        }
    }


    public static class ChangeUseParentAction extends BasePropertyChangeAction<Boolean> {
        private LCConfigurationI configuration;
        private SelectionModeUserI selectionModeUser;

        public ChangeUseParentAction(final LCConfigurationI configuration, final SelectionModeUserI selectionModeUserP, final Boolean wantedValue) {
            super(selectionModeUserP.useParentSelectionModeProperty(), wantedValue);
            this.configuration = configuration;
            this.selectionModeUser = selectionModeUserP;
        }

        @Override
        public String getNameID() {
            return "action.selectionmode.useparent.mode";
        }

        @Override
        public void doAction() throws LCException {
            super.doAction();
            if (!this.wantedValue && this.configuration != null) {
                // Copy the configuration mode parameters
                this.selectionModeUser.getSelectionModeParameter().copyFrom(this.configuration.getSelectionModeParameter());
            }
        }
    }
}
