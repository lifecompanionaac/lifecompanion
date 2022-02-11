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
package org.lifecompanion.model.impl.configurationcomponent.keyoption;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.base.data.control.UseVariableController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

import java.util.Map;

/**
 * Key option to display information with use variables.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VariableInformationKeyOption extends AbstractKeyOption {

    /**
     * Current display information for this key
     */
    private final StringProperty wantedDisplayedInformation;

    public VariableInformationKeyOption() {
        super();
        this.optionNameId = "key.option.name.variable.information";
        this.iconName = "icon_type_variable_info.png";
        this.disableTextContent.set(true);
        this.wantedDisplayedInformation = new SimpleStringProperty();
    }

    public StringProperty wantedDisplayedInformationProperty() {
        return this.wantedDisplayedInformation;
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().bind(wantedDisplayedInformation);
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().unbind();
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element elem = super.serialize(context);
        XMLObjectSerializer.serializeInto(VariableInformationKeyOption.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(VariableInformationKeyOption.class, this, node);
    }

    public void updateKeyInformations(final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI key = this.attachedKey.get();
        key.textContentProperty().unbind();
        key.textContentProperty().set(UseVariableController.INSTANCE.createText(this.wantedDisplayedInformation.get(), variables));
    }

}
