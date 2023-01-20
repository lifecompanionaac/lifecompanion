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
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.email.EmailService;
import org.lifecompanion.plugin.email.event.categories.EmailEventSubCategories;
import org.lifecompanion.util.LangUtils;

import java.util.function.Consumer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OnMailFailSendEventGenerator extends BaseUseEventGeneratorImpl {

    private final Consumer<Boolean> mailSentCallback;

    public OnMailFailSendEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 20;
        this.category = EmailEventSubCategories.SEND;
        this.nameID = "email.plugin.use.event.mail.fail.sent.name";
        this.staticDescriptionID = "email.plugin.use.event.mail.fail.sent.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
        mailSentCallback = (sent) -> {
            if (!LangUtils.isTrue(sent)) {
                this.useEventListener.fireEvent(this, null, null);
            }
        };
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/icon_event_message_failed.png";
    }

    // Class part : "Mode start/stop"
    //========================================================================

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        EmailService.INSTANCE.addMailSentCallback(mailSentCallback);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        EmailService.INSTANCE.removeMailSentCallback(mailSentCallback);
    }
    //========================================================================
}
