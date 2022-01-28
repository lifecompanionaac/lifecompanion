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
package org.lifecompanion.base.data.component.keyoption.simplercomp;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequenceItemI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.base.data.control.refacto.AppMode;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.translation.Translation;

public class UserActionSequenceItemKeyOption extends AbstractSimplerKeyContentContainerKeyOption<UserActionSequenceItemI> {
    @XMLGenericProperty(UserActionSequenceDisplayFilter.class)
    private final ObjectProperty<UserActionSequenceDisplayFilter> displayFilter;

    public UserActionSequenceItemKeyOption() {
        super();
        this.optionNameId = "key.option.user.action.sequence.item.key.option.name";
        this.iconName = "icon_type_user_action_sequence_items.png";
        this.considerKeyEmpty.set(true);
        this.displayFilter = new SimpleObjectProperty<>(UserActionSequenceDisplayFilter.BOTH);
        final InvalidationListener statusUpdate = i -> this.statusUpdate();
        this.currentSimplerKeyContentContainer.addListener(statusUpdate);
        this.currentSimplerKeyContentContainerProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.actionExecutedProperty().removeListener(statusUpdate);
                ov.currentActionProperty().removeListener(statusUpdate);
            }
            if (nv != null) {
                nv.actionExecutedProperty().addListener(statusUpdate);
                nv.currentActionProperty().addListener(statusUpdate);
            }
        });
    }

    // PROPS
    //========================================================================
    public ObjectProperty<UserActionSequenceDisplayFilter> displayFilterProperty() {
        return displayFilter;
    }

    @Override
    protected String getDefaultTextContentProperty() {
        return AppModeController.INSTANCE.modeProperty().get() == AppMode.EDIT ? Translation.getText("key.option.user.action.items.key.default.text") : "";
    }
    //========================================================================

    // IO
    //========================================================================
    @Override
    public Element serialize(final IOContextI context) {
        Element elem = super.serialize(context);
        XMLObjectSerializer.serializeInto(UserActionSequenceItemKeyOption.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(UserActionSequenceItemKeyOption.class, this, node);
    }
    //========================================================================


    // UI
    //========================================================================
    private void statusUpdate() {
        final UserActionSequenceItemI userActionSequenceItem = currentSimplerKeyContentContainer.get();
        if (userActionSequenceItem != null && (userActionSequenceItem.currentActionProperty().get() || userActionSequenceItem.actionExecutedProperty().get())) {
            final RectangleOnKeyForKeyViewAdded rectangleOnKeyForKeyViewAdded = new RectangleOnKeyForKeyViewAdded();
            if (userActionSequenceItem.currentActionProperty().get()) {
                rectangleOnKeyForKeyViewAdded.withStrokeColor(RectangleOnKeyForKeyViewAdded.CURRENT_COLOR);
            } else if (userActionSequenceItem.actionExecutedProperty().get()) {
                rectangleOnKeyForKeyViewAdded.withStrokeColor(RectangleOnKeyForKeyViewAdded.STRIKE_OUT_COLOR).withStrikeout().withBackgroundReduction();
            }
            this.keyViewAddedNodeProperty().set(rectangleOnKeyForKeyViewAdded);
        } else {
            this.keyViewAddedNodeProperty().set(null);
        }
    }
    //========================================================================


}
