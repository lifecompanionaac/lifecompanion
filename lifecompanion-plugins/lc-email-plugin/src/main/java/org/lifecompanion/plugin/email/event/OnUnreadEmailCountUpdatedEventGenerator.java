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
package org.lifecompanion.plugin.email.event;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.email.EmailPluginService;
import org.lifecompanion.plugin.email.EmailService;
import org.lifecompanion.plugin.email.event.categories.EmailEventSubCategories;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.function.Consumer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OnUnreadEmailCountUpdatedEventGenerator extends BaseUseEventGeneratorImpl {

    @XMLGenericProperty(UnreadEventGenerateCondition.class)
    private ObjectProperty<UnreadEventGenerateCondition> condition;

    private final Consumer<Integer> unreadCountUpdatedCallback;

    public OnUnreadEmailCountUpdatedEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 20;
        this.category = EmailEventSubCategories.MISC;
        this.nameID = "email.plugin.use.event.mail.unread.count.updated.name";
        this.staticDescriptionID = "email.plugin.use.event.mail.unread.count.updated.description";
        this.condition = new SimpleObjectProperty<>(UnreadEventGenerateCondition.ALWAYS);
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("email.plugin.use.event.mail.unread.count.updated.variable.description", this.condition));
        unreadCountUpdatedCallback = (unreadCount) -> {
            final UnreadEventGenerateCondition cond = this.condition.get();
            if (cond == UnreadEventGenerateCondition.ALWAYS || (unreadCount > 0 && cond == UnreadEventGenerateCondition.UNREAD)
                    || (unreadCount == 0 && cond == UnreadEventGenerateCondition.NONE)) {
                this.useEventListener.fireEvent(this, null, null);
            }
        };
    }

    public ObjectProperty<UnreadEventGenerateCondition> conditionProperty() {
        return condition;
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/icon_event_unread_count_updated.png";
    }

    // Class part : "Mode start/stop"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        EmailService.INSTANCE.addUnreadCountUpdateCallback(unreadCountUpdatedCallback);
        // On start, fire first event
        int unreadCount = EmailPluginService.INSTANCE.getUnreadCount();
        unreadCountUpdatedCallback.accept(unreadCount);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        EmailService.INSTANCE.removeUnreadCountUpdateCallback(unreadCountUpdatedCallback);
    }
    //========================================================================

    // Class part : "Generation condition"
    //========================================================================
    public static enum UnreadEventGenerateCondition {
        ALWAYS("email.plugin.unread.count.condition.always"), //
        UNREAD("email.plugin.unread.count.condition.unread.positive"), //
        NONE("email.plugin.unread.count.condition.unread.none");//
        private final String textId;

        private UnreadEventGenerateCondition(String textId) {
            this.textId = textId;
        }

        public String getText() {
            return Translation.getText(textId);
        }

        public String toString() {
            return getText();
        }
    }
    //========================================================================

    // Class part : "IO"
    //========================================================================
    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(OnUnreadEmailCountUpdatedEventGenerator.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(OnUnreadEmailCountUpdatedEventGenerator.class, this, node);
    }
    //========================================================================
}
