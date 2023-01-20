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
import org.lifecompanion.plugin.calendar.keyoption.CalendarEventListKeyOption;
import org.lifecompanion.ui.configurationcomponent.editmode.keyoption.BaseKeyOptionConfigView;
import org.lifecompanion.util.binding.EditActionUtils;

public class CalendarEventListKeyOptionConfigView extends BaseKeyOptionConfigView<CalendarEventListKeyOption> {
    private ToggleSwitch toggleSwitchForCurrentEvent, toggleSwitchForRunningEvent;
    private ChangeListener<Boolean> changeListenerForCurrentEvent, changeListenerForRunningEvent;

    @Override
    public Class<CalendarEventListKeyOption> getConfiguredKeyOptionType() {
        return CalendarEventListKeyOption.class;
    }

    @Override
    public void initUI() {
        super.initUI();
        toggleSwitchForCurrentEvent = new ToggleSwitch(Translation.getText("calendar.plugin.field.key.option.event.for.current"));
        toggleSwitchForRunningEvent = new ToggleSwitch(Translation.getText("calendar.plugin.field.key.option.event.for.running"));
        toggleSwitchForCurrentEvent.setPrefWidth(250);
        toggleSwitchForRunningEvent.setPrefWidth(250);
        this.getChildren().addAll(toggleSwitchForCurrentEvent, toggleSwitchForRunningEvent);
    }

    @Override
    public void initListener() {
        createDisableOtherSwitchIfSelected(this.toggleSwitchForRunningEvent, toggleSwitchForCurrentEvent);
        createDisableOtherSwitchIfSelected(this.toggleSwitchForCurrentEvent, toggleSwitchForRunningEvent);
    }

    // FIXME : disable in UI
    private void createDisableOtherSwitchIfSelected(ToggleSwitch toggle1, ToggleSwitch toggle2) {
        //        toggle1.selectedProperty().addListener((obs, ov, nv) -> {
        //            if (nv) toggle2.setSelected(false);
        //        });
    }

    @Override
    public void initBinding() {
        this.changeListenerForCurrentEvent = EditActionUtils.createSimpleBinding(this.toggleSwitchForCurrentEvent.selectedProperty(), this.model,
                m -> m.forCurrentEventProperty().get(), ChangeForCurrentEventProperty::new);
        this.changeListenerForRunningEvent = EditActionUtils.createSimpleBinding(this.toggleSwitchForRunningEvent.selectedProperty(), this.model,
                m -> m.forRunningEventProperty().get(), ChangeForCurrentEventProperty::new);
    }

    @Override
    public void bind(CalendarEventListKeyOption model) {
        toggleSwitchForCurrentEvent.setSelected(model.forCurrentEventProperty().get());
        model.forCurrentEventProperty().addListener(changeListenerForCurrentEvent);
        toggleSwitchForRunningEvent.setSelected(model.forRunningEventProperty().get());
        model.forRunningEventProperty().addListener(changeListenerForRunningEvent);
    }

    @Override
    public void unbind(CalendarEventListKeyOption model) {
        model.forCurrentEventProperty().removeListener(changeListenerForCurrentEvent);
        model.forRunningEventProperty().removeListener(changeListenerForRunningEvent);
    }

    public static class ChangeForCurrentEventProperty extends BasePropertyChangeAction<Boolean> {
        public ChangeForCurrentEventProperty(final CalendarEventListKeyOption option, final Boolean wantedValueP) {
            super(option.forCurrentEventProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "todo";
        }
    }

    public static class ChangeForRunningEventProperty extends BasePropertyChangeAction<Boolean> {
        public ChangeForRunningEventProperty(final CalendarEventListKeyOption option, final Boolean wantedValueP) {
            super(option.forRunningEventProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "todo";
        }
    }
}

