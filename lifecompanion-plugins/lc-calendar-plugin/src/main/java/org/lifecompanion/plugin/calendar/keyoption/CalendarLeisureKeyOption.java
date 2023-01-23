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

package org.lifecompanion.plugin.calendar.keyoption;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreDefaultBooleanValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey.AbstractSimplerKeyContentContainerKeyOption;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.calendar.action.leisure.SelectCalendarLeisureAction;
import org.lifecompanion.plugin.calendar.model.CalendarLeisure;

import java.util.List;

public class CalendarLeisureKeyOption extends AbstractSimplerKeyContentContainerKeyOption<CalendarLeisure> {
    @XMLIgnoreDefaultBooleanValue(value = false)
    private final BooleanProperty forCurrentSelection;

    private final SelectCalendarLeisureAction selectCalendarLeisureAction;

    public CalendarLeisureKeyOption() {
        this.optionNameId = "calendar.plugin.key.option.calendar.leisure.list";
        this.iconName = "icon_keyoption_leisure.png";
        this.forCurrentSelection = new SimpleBooleanProperty(false);
        selectCalendarLeisureAction = new SelectCalendarLeisureAction();
        selectCalendarLeisureAction.attachedToKeyOptionProperty().set(true);
        InvalidationListener selectionUpdate = inv -> selectionUpdate();
        this.currentSimplerKeyContentContainerProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.selectedPropertyProperty().removeListener(selectionUpdate);
            }
            if (nv != null) {
                nv.selectedPropertyProperty().addListener(selectionUpdate);
            }
            selectionUpdate.invalidated(null);
        });
    }

    @Override
    protected String getDefaultTextContentProperty() {
        return AppModeController.INSTANCE.isEditMode() ? Translation.getText("calendar.plugin.key.option.calendar.leisure.list.default.text") : "";
    }

    public BooleanProperty forCurrentSelectionProperty() {
        return forCurrentSelection;
    }

    private void selectionUpdate() {
        final CalendarLeisure calendarLeisure = currentSimplerKeyContentContainer.get();
        final GridPartKeyComponentI key = this.attachedKeyProperty().get();
        if (key != null && !forCurrentSelection.get()) {
            if (calendarLeisure != null && calendarLeisure.selectedPropertyProperty().get()) {
                key.getKeyStyle().backgroundColorProperty().forced().setValue(Color.GRAY);
            } else {
                key.getKeyStyle().backgroundColorProperty().forced().setValue(null);
            }
        }
    }

    @Override
    protected List<BaseUseActionI<?>> getActionsToAddFor(UseActionEvent event) {
        final List<BaseUseActionI<?>> actionsToAddFor = super.getActionsToAddFor(event);
        if (event == UseActionEvent.ACTIVATION) {
            actionsToAddFor.add(0, selectCalendarLeisureAction);
        }
        return actionsToAddFor;
    }

    @Override
    protected List<BaseUseActionI<?>> getActionsToRemoveFor(UseActionEvent event) {
        final List<BaseUseActionI<?>> actionsToRemoveFor = super.getActionsToRemoveFor(event);
        if (event == UseActionEvent.ACTIVATION) {
            actionsToRemoveFor.add(selectCalendarLeisureAction);
        }
        return actionsToRemoveFor;
    }

    @Override
    public Element serialize(final IOContextI context) {
        return XMLObjectSerializer.serializeInto(CalendarLeisureKeyOption.class, this, super.serialize(context));
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(CalendarLeisureKeyOption.class, this, node);
    }
}
