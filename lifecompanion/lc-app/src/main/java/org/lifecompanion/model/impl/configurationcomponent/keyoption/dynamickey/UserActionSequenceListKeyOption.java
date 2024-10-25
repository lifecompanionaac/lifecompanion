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

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequenceI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.KeyListIndicatorAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SelectKeyNodeAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.StartCurrentUserActionSequenceAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.StartUserActionSequenceAction;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.imagedictionary.StaticImageElement;

import java.util.List;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserActionSequenceListKeyOption extends AbstractSimplerKeyContentContainerKeyOption<UserActionSequenceI> {
    private StartCurrentUserActionSequenceAction startUserActionSequenceAction;

    public UserActionSequenceListKeyOption() {
        super();
        this.optionNameId = "key.option.user.action.sequence.list.name";
        this.optionDescriptionId = "key.option.user.action.sequence.list.description";
        this.iconName = "icon_type_user_action_sequence_items.png";
    }


    @Override
    public void attachToImpl(GridPartKeyComponentI key) {
        super.attachToImpl(key);
        this.startUserActionSequenceAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, StartCurrentUserActionSequenceAction.class);
        if (startUserActionSequenceAction == null) {
            startUserActionSequenceAction = new StartCurrentUserActionSequenceAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(0, startUserActionSequenceAction);
        }
        key.imageVTwoProperty().set(new StaticImageElement(IconHelper.get("example_image_entry.png")));
        startUserActionSequenceAction.attachedToKeyOptionProperty().set(true);
    }

    @Override
    protected String getDefaultTextContentProperty() {
        return AppModeController.INSTANCE.isEditMode() ? Translation.getText("key.option.user.action.sequence.list.example.text") : "";
    }

    @Override
    public void detachFromImpl(GridPartKeyComponentI key) {
        super.detachFromImpl(key);
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.startUserActionSequenceAction);
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element elem = super.serialize(context);
        XMLObjectSerializer.serializeInto(UserActionSequenceListKeyOption.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(UserActionSequenceListKeyOption.class, this, node);
    }
}
