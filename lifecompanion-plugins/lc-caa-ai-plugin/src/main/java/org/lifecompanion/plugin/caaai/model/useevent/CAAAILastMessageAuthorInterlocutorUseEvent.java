package org.lifecompanion.plugin.caaai.model.useevent;

import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.caaai.model.ConversationMessageAuthor;
import org.lifecompanion.plugin.caaai.model.useevent.common.CAAAILastMessageAuthorChangeUseEvent;

public class CAAAILastMessageAuthorInterlocutorUseEvent extends CAAAILastMessageAuthorChangeUseEvent {
    public CAAAILastMessageAuthorInterlocutorUseEvent() {
        super(ConversationMessageAuthor.INTERLOCUTOR);
        this.order = 0;
        this.category = CAAAIEventSubCategories.TODO;
        this.nameID = "caa.ai.plugin.events.last_message_author_interlocutor.name";
        this.staticDescriptionID = "caa.ai.plugin.events.last_message_author_interlocutor.description";
        this.configIconPath = "filler_icon_32px.png";
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(CAAAILastMessageAuthorInterlocutorUseEvent.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(CAAAILastMessageAuthorInterlocutorUseEvent.class, this, node);
    }
}

