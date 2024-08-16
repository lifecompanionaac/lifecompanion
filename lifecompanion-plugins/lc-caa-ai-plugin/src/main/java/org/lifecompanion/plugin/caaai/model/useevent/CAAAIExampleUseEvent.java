package org.lifecompanion.plugin.caaai.model.useevent;

import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.exception.LCException;

public class CAAAIExampleUseEvent extends BaseUseEventGeneratorImpl {

    public CAAAIExampleUseEvent() {
        super();
        this.parameterizableAction = true;
        this.order = 0;
        this.category = CAAAIEventSubCategories.TODO;
        this.nameID = "caa.ai.plugin.todo";
        this.staticDescriptionID = "caa.ai.plugin.todo";
        this.configIconPath = "filler_icon_32px.png";
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        // TODO : plug listener
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        // TODO : unplug listener
    }

    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(CAAAIExampleUseEvent.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(CAAAIExampleUseEvent.class, this, node);
    }
}

