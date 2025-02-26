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
package org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.KeyListIndicatorAction;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SelectKeyNodeAction;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

import java.util.List;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyListNodeKeyOption extends AbstractSimplerKeyActionContainerKeyOption<KeyListNodeI> {
    private final SelectKeyNodeAction selectKeyNodeAction;
    private KeyListIndicatorAction keyListIndicatorActionActivation, keyListIndicatorActionOver;

    // Keep for backward compatibility if we want to restore specific level feature
    private final IntegerProperty selectedLevel;
    private final BooleanProperty specificLevel;
    private final BooleanProperty displayLevelBellow;

    public KeyListNodeKeyOption() {
        super();
        this.optionNameId = "key.option.key.list.category.display.name";
        this.optionDescriptionId = "key.option.key.list.category.display.description";
        this.iconName = "icon_type_keylist_key.png";

        selectKeyNodeAction = new SelectKeyNodeAction();
        selectKeyNodeAction.attachedToKeyOptionProperty().set(true);

        selectedLevel = new SimpleIntegerProperty(1);
        specificLevel = new SimpleBooleanProperty(false);
        displayLevelBellow = new SimpleBooleanProperty(true);
    }

//    public IntegerProperty selectedLevelProperty() {
//        return selectedLevel;
//    }
//
//    public BooleanProperty specificLevelProperty() {
//        return specificLevel;
//    }
//
//    public BooleanProperty displayLevelBellowProperty() {
//        return displayLevelBellow;
//    }

    @Override
    public void attachToImpl(GridPartKeyComponentI key) {
        super.attachToImpl(key);
        this.keyListIndicatorActionActivation = addKeylistIndicatorAction(key, UseActionEvent.ACTIVATION);
        this.keyListIndicatorActionOver = addKeylistIndicatorAction(key, UseActionEvent.OVER);
    }

    private KeyListIndicatorAction addKeylistIndicatorAction(GridPartKeyComponentI key, UseActionEvent event) {
        KeyListIndicatorAction indicatorAction = key.getActionManager().getFirstActionOfType(event, KeyListIndicatorAction.class);
        if (indicatorAction == null) {
            indicatorAction = new KeyListIndicatorAction();
            key.getActionManager().componentActions().get(event).add(0, indicatorAction);
        }
        indicatorAction.attachedToKeyOptionProperty().set(true);
        return indicatorAction;
    }

    @Override
    public void detachFromImpl(GridPartKeyComponentI key) {
        super.detachFromImpl(key);
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.keyListIndicatorActionActivation);
        key.getActionManager().componentActions().get(UseActionEvent.OVER).remove(this.keyListIndicatorActionOver);
    }

    @Override
    protected List<BaseUseActionI<?>> getActionsToAddFor(UseActionEvent event) {
        final List<BaseUseActionI<?>> actionsToAddFor = super.getActionsToAddFor(event);
        if (event == UseActionEvent.ACTIVATION) {
            actionsToAddFor.add(0, selectKeyNodeAction);
        }
        return actionsToAddFor;
    }

    @Override
    protected List<BaseUseActionI<?>> getActionsToRemoveFor(UseActionEvent event) {
        final List<BaseUseActionI<?>> actionsToRemoveFor = super.getActionsToRemoveFor(event);
        if (event == UseActionEvent.ACTIVATION) {
            actionsToRemoveFor.add(selectKeyNodeAction);
        }
        return actionsToRemoveFor;
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element elem = super.serialize(context);
        XMLObjectSerializer.serializeInto(KeyListNodeKeyOption.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(KeyListNodeKeyOption.class, this, node);
        // Find a previous update incorrect default value problem
        if (this.selectedLevel.get() <= 0) {
            selectedLevel.set(1);
        }
    }
}
