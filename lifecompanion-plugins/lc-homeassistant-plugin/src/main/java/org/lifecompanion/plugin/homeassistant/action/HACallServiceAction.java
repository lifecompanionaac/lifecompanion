package org.lifecompanion.plugin.homeassistant.action;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.homeassistant.HomeAssistantPluginService;
import org.lifecompanion.plugin.homeassistant.action.category.HAActionSubCategories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class HACallServiceAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private StringProperty entityId;
    private StringProperty serviceId;

    private static final Logger LOGGER = LoggerFactory.getLogger(HACallServiceAction.class);

    public HACallServiceAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = HAActionSubCategories.ALL;
        this.parameterizableAction = true;
        this.entityId = new SimpleStringProperty();
        this.serviceId = new SimpleStringProperty();
        this.nameID = "ha.plugin.action.call.service.name";
        this.staticDescriptionID = "ha.plugin.action.call.service.name";
        this.configIconPath = "icon_call_service.png";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }


    public StringProperty entityIdProperty() {
        return entityId;
    }

    public StringProperty serviceIdProperty() {
        return serviceId;
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        try {
            HomeAssistantPluginService.INSTANCE.executeService(serviceId.get(), entityId.get());
        } catch (IOException e) {
            LOGGER.error("Can't call service ", e);
        }
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        return XMLObjectSerializer.serializeInto(HACallServiceAction.class, this, super.serialize(contextP));
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(HACallServiceAction.class, this, nodeP);
    }


}
