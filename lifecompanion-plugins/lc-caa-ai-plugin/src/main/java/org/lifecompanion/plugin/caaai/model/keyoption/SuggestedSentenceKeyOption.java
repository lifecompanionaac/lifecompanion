/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.caaai.model.keyoption;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.model.impl.exception.LCException;


public class SuggestedSentenceKeyOption extends AbstractKeyOption {

    private final BooleanProperty exampleProperty;

    public SuggestedSentenceKeyOption() {
        super();
        this.disableTextContent.set(true);
        this.optionNameId = "caa.ai.plugin.todo";
        this.optionDescriptionId = "caa.ai.plugin.todo";
        this.iconName = "filler_icon_32px.png";
        this.disableTextContent.set(true);
        this.exampleProperty = new SimpleBooleanProperty();
    }

    public BooleanProperty examplePropertyProperty() {
        return exampleProperty;
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().set(null);
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
    }

    @Override
    public Element serialize(IOContextI context) {
        final Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(SuggestedSentenceKeyOption.class, this, node);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(SuggestedSentenceKeyOption.class, this, node);
    }

    public void suggestionUpdated(String suggestion) {
        GridPartKeyComponentI keyComponent = this.attachedKeyProperty().get();
        if (keyComponent != null) {
            keyComponent.textContentProperty().set(suggestion);
        }
    }
}
