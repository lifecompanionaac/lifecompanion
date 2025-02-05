package org.lifecompanion.model.impl.configurationcomponent;

import org.lifecompanion.model.api.categorizedelement.useaction.ActionEventType;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class UseActionTriggerEventWrapper {
    private final Set<BiConsumer<ActionEventType, UseActionEvent>> eventFiredListeners;

    public UseActionTriggerEventWrapper() {
        this.eventFiredListeners = new HashSet<>(2);
    }

    public void eventFired(ActionEventType type, UseActionEvent event) {
        for (BiConsumer<ActionEventType, UseActionEvent> eventFiredListener : eventFiredListeners) {
            eventFiredListener.accept(type, event);
        }
    }

    public void addEventFiredListener(BiConsumer<ActionEventType, UseActionEvent> eventListener) {
        this.eventFiredListeners.add(eventListener);
    }

    public void removeEventFiredListener(BiConsumer<ActionEventType, UseActionEvent> eventListener) {
        this.eventFiredListeners.remove(eventListener);
    }
}
