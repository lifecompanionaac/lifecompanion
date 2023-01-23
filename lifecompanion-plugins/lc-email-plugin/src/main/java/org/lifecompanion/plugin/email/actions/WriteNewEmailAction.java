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
package org.lifecompanion.plugin.email.actions;

import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.email.EmailPluginService;
import org.lifecompanion.plugin.email.actions.categories.EmailActionSubCategories;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.email.EmailPluginService;
import org.lifecompanion.plugin.email.actions.categories.EmailActionSubCategories;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;

import java.util.Map;

public class WriteNewEmailAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private StringProperty emailToAddress, emailToName;

    public WriteNewEmailAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "email.plugin.use.action.write.new.email.to.name";
        this.staticDescriptionID = "email.plugin.use.action.write.new.email.to.description";
        this.category = EmailActionSubCategories.SEND;
        this.order = 10;
        this.parameterizableAction = true;
        this.emailToAddress = new SimpleStringProperty();
        this.emailToName = new SimpleStringProperty();
        this.variableDescriptionProperty().bind(
                TranslationFX.getTextBinding("email.plugin.use.action.write.new.email.to.variable.description", this.emailToName, this.emailToAddress));
    }

    public StringProperty emailToAddressProperty() {
        return this.emailToAddress;
    }

    public StringProperty emailToNameProperty() {
        return this.emailToName;
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/icon_action_new_mail.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        EmailPluginService.INSTANCE.startNewEmailTo(emailToName.get(), emailToAddress.get());
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(WriteNewEmailAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(WriteNewEmailAction.class, this, nodeP);
    }
}
