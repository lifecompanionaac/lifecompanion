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

import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.email.EmailPluginService;
import org.lifecompanion.plugin.email.actions.categories.EmailActionSubCategories;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.plugin.email.EmailPlugin;
import org.lifecompanion.plugin.email.EmailPluginService;
import org.lifecompanion.plugin.email.EmailPluginUtils;
import org.lifecompanion.plugin.email.actions.categories.EmailActionSubCategories;
import org.lifecompanion.plugin.email.model.EmailContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class SpeakCurrentEmailAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeakCurrentEmailAction.class);

    public SpeakCurrentEmailAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "email.plugin.use.action.speak.current.email.name";
        this.staticDescriptionID = "email.plugin.use.action.speak.current.email.description";
        this.category = EmailActionSubCategories.CURRENT;
        this.order = 10;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/icon_action_speak_mail.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        final EmailContent emailContent = EmailPluginService.INSTANCE.getCurrentMessage();
        if (emailContent != null && emailContent.getMessage() != null) {
            try {
                Message currentMessage = emailContent.getMessage();
                VoiceSynthesizerController.INSTANCE.speakSync(new StringBuilder(//
                                Translation.getText("email.plugin.speak.all.email.intro.text", //
                                        EmailPluginService.EMAIL_DATE_FULL_FORMAT.format(currentMessage.getSentDate()), //
                                        Arrays.stream(currentMessage.getFrom())//
                                                .map(a -> ((InternetAddress) a))//
                                                .map(EmailPluginUtils::getPersonalOrAddress)//
                                                .collect(Collectors.joining(" et ")))).append("\n").append(emailContent.getFullText()).toString());
            } catch (MessagingException e) {
                LOGGER.error("Couldn't read current message informations");
            }
        }
    }

}
