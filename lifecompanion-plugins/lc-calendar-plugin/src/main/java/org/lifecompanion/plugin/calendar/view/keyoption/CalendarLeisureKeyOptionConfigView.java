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

package org.lifecompanion.plugin.calendar.view.keyoption;

import javafx.beans.value.ChangeListener;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.editaction.BasePropertyChangeAction;
import org.lifecompanion.plugin.calendar.keyoption.CalendarLeisureKeyOption;
import org.lifecompanion.ui.configurationcomponent.editmode.keyoption.BaseKeyOptionConfigView;
import org.lifecompanion.util.binding.EditActionUtils;

public class CalendarLeisureKeyOptionConfigView extends BaseKeyOptionConfigView<CalendarLeisureKeyOption> {
    private ToggleSwitch toggleSwitchForCurrentSelection;
    private ChangeListener<Boolean> changeListenerForCurrentSelection;

    @Override
    public Class<CalendarLeisureKeyOption> getConfiguredKeyOptionType() {
        return CalendarLeisureKeyOption.class;
    }

    @Override
    public void initUI() {
        super.initUI();
        toggleSwitchForCurrentSelection = new ToggleSwitch(Translation.getText("calendar.plugin.field.key.option.leisure.for.main.current.selection"));
        this.getChildren().addAll(toggleSwitchForCurrentSelection);
    }

    @Override
    public void initBinding() {
        this.changeListenerForCurrentSelection = EditActionUtils.createSimpleBinding(this.toggleSwitchForCurrentSelection.selectedProperty(), this.model,
                m -> m.forCurrentSelectionProperty().get(), ChangeForCurrentSelectionAction::new);
    }

    @Override
    public void bind(CalendarLeisureKeyOption model) {
        toggleSwitchForCurrentSelection.setSelected(model.forCurrentSelectionProperty().get());
        model.forCurrentSelectionProperty().addListener(changeListenerForCurrentSelection);
    }

    @Override
    public void unbind(CalendarLeisureKeyOption model) {
        model.forCurrentSelectionProperty().removeListener(changeListenerForCurrentSelection);
    }

    public static class ChangeForCurrentSelectionAction extends BasePropertyChangeAction<Boolean> {
        public ChangeForCurrentSelectionAction(final CalendarLeisureKeyOption option, final Boolean wantedValueP) {
            super(option.forCurrentSelectionProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "todo";
        }
    }
}

