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
package org.lifecompanion.plugin.email.keyoption;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.plugin.email.EmailPluginService;
import org.lifecompanion.plugin.email.EmailPluginUtils;
import org.lifecompanion.plugin.email.EmailService;
import org.lifecompanion.plugin.email.actions.SelectEmailAction;
import org.lifecompanion.plugin.email.model.EmailCellContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class EmailCellKeyOption extends AbstractKeyOption {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailCellKeyOption.class);

    private final ObjectProperty<EmailCellContent> message;
    private SelectEmailAction selectEmailAction;

    public EmailCellKeyOption() {
        super();
        this.optionNameId = "email.plugin.key.option.email.cell";
        this.disableTextContent.set(true);
        this.disableImage.set(true);
        this.message = new SimpleObjectProperty<>();
        this.considerKeyEmpty.set(false);
        this.initMessageBinding();
    }

    public ObjectProperty<EmailCellContent> messageProperty() {
        return this.message;
    }

    @Override
    public String getIconUrl() {
        return "icon_key_option_emails.png";
    }

    @Override
    protected void attachToImpl(final GridPartKeyComponentI key) {
        //Get the existing action, or create new one
        this.selectEmailAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, SelectEmailAction.class);
        if (this.selectEmailAction == null) {
            this.selectEmailAction = new SelectEmailAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.selectEmailAction);
        }
        this.selectEmailAction.attachedToKeyOptionProperty().set(true);
        key.textContentProperty().set(AppModeController.INSTANCE.isUseMode() ? null : Translation.getText("email.plugin.key.option.email.cell.default.text"));
    }

    @Override
    protected void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.selectEmailAction);
        key.textContentProperty().set(null);
    }

    private void initMessageBinding() {
        this.message.addListener((obs, ov, nv) -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (nv != null) {
                if (nv == EmailPluginService.FLAG_LOADING) {
                    key.textContentProperty().set(Translation.getText("email.plugin.loading.inbox.label"));
                } else if (nv == EmailPluginService.FLAG_NOT_CONNECTED) {
                    key.textContentProperty().set(Translation.getText("email.plugin.not.connected.label"));
                } else {
                    try {
                        key.textContentProperty().set(getMessageCellString(nv));
                        key.getKeyTextStyle().boldProperty().forced().setValue(!nv.isSeen());
                    } catch (MessagingException e) {
                        LOGGER.error("Couldn't get message information to update key", e);
                    }
                }
            } else {
                key.textContentProperty().set(null);
                key.getKeyTextStyle().boldProperty().forced().setValue(null);
            }
        });
    }

    private String getMessageCellString(final EmailCellContent msg) throws MessagingException {
        return new StringBuilder(EmailPluginUtils.trimToEmpty(msg.getSubject())).append(" (").append(EmailService.EMAIL_DATE_FORMAT.format(msg.getSentDate())).append(")\n")
                .append(EmailPluginUtils.getPersonalAddressFormatted(msg.getFrom())).toString();
    }

}
