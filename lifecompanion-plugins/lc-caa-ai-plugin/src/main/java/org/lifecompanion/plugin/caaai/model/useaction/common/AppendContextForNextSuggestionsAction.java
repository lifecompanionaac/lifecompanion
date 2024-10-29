package org.lifecompanion.plugin.caaai.model.useaction.common;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.categorizedelement.useaction.SimpleUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.plugin.caaai.model.AiContextValue;

public interface AppendContextForNextSuggestionsAction<T extends UseActionTriggerComponentI, V extends AiContextValue> extends SimpleUseActionI<T> {
    public ObjectProperty<V> contextValueProperty();
}
