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

package org.lifecompanion.plugin.email;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.plugin.email.keyoption.EmailAttachmentCellKeyOption;
import org.lifecompanion.plugin.email.keyoption.EmailAttachmentContentKeyOption;
import org.lifecompanion.plugin.email.keyoption.EmailCellKeyOption;
import org.lifecompanion.plugin.email.keyoption.EmailContentKeyOption;
import org.lifecompanion.plugin.email.model.EmailAttachment;
import org.lifecompanion.plugin.email.model.EmailCellContent;
import org.lifecompanion.plugin.email.model.EmailContent;
import org.lifecompanion.plugin.email.model.MessageToSend;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

public enum EmailPluginService {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailPluginService.class);

    public static final EmailCellContent FLAG_LOADING = new EmailCellContent();
    public static final EmailCellContent FLAG_NOT_CONNECTED = new EmailCellContent();

    public static final String VAR_FROM_SELECTED = "FromInSelectedEmail", VAR_TO_SELECTED = "ToInSelectedEmail", VAR_SUBJECT_SELECTED = "SubjectInSelectedEmail",
            VAR_DATE_SELECTED = "DateInSelectedEmail", VAR_UNREAD_COUNT = "UnreadEmailCount", VAR_DISPLAYED_INDEX = "DisplayedEmailsIndex", VAR_TOTAL_COUNT = "EmailTotalCount",
            VAR_WRITE_TO = "WriteEmailTo", VAR_WRITE_SUBJECT = "WriteEmailSubject";
    ;
    public static final SimpleDateFormat EMAIL_DATE_FULL_FORMAT = new SimpleDateFormat("EEEEE d MMMMM yyyy à HH:mm");
    public static final String RE_TAG = "Re: ";

    private int startMessageIndex, endMessageIndex, totalMessageCount, possibleMessageDisplayedCount, messagePageIndex, unreadCount;
    private int userMessageStartIndex, userMessageEndIndex;

    private final List<EmailCellKeyOption> emailCells;
    private final List<EmailAttachmentCellKeyOption> emailAttachmentCells;
    private final ObjectProperty<EmailContent> selectedMessage;
    private final ObjectProperty<EmailAttachment> selectedAttachment;
    private final MessageToSend currentMessageToSend;

    private EmailContentKeyOption emailContentKey;
    private EmailAttachmentContentKeyOption emailAttachmentContentKey;

    private EmailPluginService() {
        emailCells = new ArrayList<>();
        emailAttachmentCells = new ArrayList<>();
        selectedMessage = new SimpleObjectProperty<>();
        selectedAttachment = new SimpleObjectProperty<>();
        currentMessageToSend = new MessageToSend();
    }

    // TODO : if message count change, we should refresh with default indexes

    // Class part : "Properties/values"
    //========================================================================
    public EmailContent getCurrentMessage() {
        return this.selectedMessage.get();
    }

    public int getUnreadCount() {
        return unreadCount;
    }
    //========================================================================

    // Class part : "Message inbox"
    //========================================================================
    private void loadCurrentInboxPage() {
        LOGGER.info("Load current inbox page");
        // Unload every message (loading)
        for (int i = 0; i < emailCells.size(); i++) {
            final EmailCellKeyOption cell = this.emailCells.get(i);
            Platform.runLater(() -> cell.messageProperty().set(FLAG_LOADING));
        }
        // Load new messages
        try {
            final Message[] messages = EmailService.INSTANCE.getMessages(startMessageIndex, endMessageIndex);
            for (int i = 0; i < emailCells.size(); i++) {
                final EmailCellKeyOption cell = this.emailCells.get(i);
                final int messageIndex = messages.length - 1 - i;
                EmailCellContent cellContent = messageIndex >= 0 && messageIndex < messages.length ? new EmailCellContent(messages[messageIndex]) : null;
                Platform.runLater(() -> cell.messageProperty().set(cellContent));
            }
            LOGGER.info("Loaded {} messages", messages.length);
        } catch (Exception e) {
            LOGGER.error("Problem when refreshing emails...", e);
            handleNotConnected();
        }
    }

    public void refreshInbox() {
        EmailService.INSTANCE.requestOpenConnection(currentEmailProperties, connected -> {
            if (connected) {
                try {
                    // If message count has changed : we need to refresh page indexes
                    int newTotalMessageCount = EmailService.INSTANCE.getMessageCount();
                    if (newTotalMessageCount != this.totalMessageCount) {
                        this.messagePageIndex = 0;
                    }
                    // Read properties and get unread count - update page message indexes
                    this.totalMessageCount = newTotalMessageCount;
                    this.unreadCount = EmailService.INSTANCE.getUnreadMessageCount();
                    this.updateStartAndEndIndexes();

                    // Load messages to display
                    this.loadCurrentInboxPage();
                } catch (Exception e) {
                    LOGGER.error("Couldn't refresh inbox", e);
                }
            } else {
                handleNotConnected();
            }
        });

    }

    private void handleNotConnected() {
        for (int i = 0; i < emailCells.size(); i++) {
            final EmailCellKeyOption cell = this.emailCells.get(i);
            Platform.runLater(() -> cell.messageProperty().set(FLAG_NOT_CONNECTED));
        }
    }

    public void olderMessagePage() {
        int maxPageCount = (int) Math.ceil((1.0 * this.totalMessageCount) / (1.0 * this.possibleMessageDisplayedCount));
        if (this.messagePageIndex + 1 < maxPageCount) {
            this.messagePageIndex = this.messagePageIndex + 1;
            updateStartAndEndIndexes();
            this.loadCurrentInboxPage();
        }
    }

    public void newerMessagePage() {
        if (this.messagePageIndex > 0) {
            this.messagePageIndex = this.messagePageIndex - 1;
            updateStartAndEndIndexes();
            this.loadCurrentInboxPage();
        }
    }

    private void updateStartAndEndIndexes() {
        this.endMessageIndex = Math.min(this.totalMessageCount, this.totalMessageCount - this.messagePageIndex * this.possibleMessageDisplayedCount);
        this.startMessageIndex = Math.max(1, endMessageIndex - this.possibleMessageDisplayedCount + 1);
        userMessageStartIndex = 1 + this.messagePageIndex * this.possibleMessageDisplayedCount;
        userMessageEndIndex = Math.min(this.totalMessageCount, (this.messagePageIndex + 1) * this.possibleMessageDisplayedCount);
        LOGGER.info("Messages {}/{}, total is {}, page index is {}", startMessageIndex, endMessageIndex, this.totalMessageCount, this.messagePageIndex);
    }
    //========================================================================

    // Class part : "Use variables"
    //========================================================================
    public Map<String, UseVariableI<?>> generateVariables(final Map<String, UseVariableDefinitionI> variablesToGenerate) {
        Map<String, UseVariableI<?>> useVariables = new HashMap<>();

        final EmailContent currentEmailContent = this.getCurrentMessage();
        final Message currentMessage = currentEmailContent != null ? currentEmailContent.getMessage() : null;

        try {
            useVariables.put(VAR_DATE_SELECTED,
                    new StringUseVariable(variablesToGenerate.get(VAR_DATE_SELECTED), currentMessage != null ? EMAIL_DATE_FULL_FORMAT.format(currentMessage.getSentDate()) : ""));
            useVariables.put(VAR_SUBJECT_SELECTED, new StringUseVariable(variablesToGenerate.get(VAR_SUBJECT_SELECTED), currentMessage != null ? currentMessage.getSubject() : ""));
            useVariables.put(VAR_FROM_SELECTED,
                    new StringUseVariable(variablesToGenerate.get(VAR_FROM_SELECTED), currentMessage != null ? EmailPluginUtils.getPersonalAddressFormatted(currentMessage.getFrom()) : ""));
            useVariables.put(VAR_TO_SELECTED,
                    new StringUseVariable(variablesToGenerate.get(VAR_TO_SELECTED), currentMessage != null ? EmailPluginUtils.getPersonalAddressFormatted(currentMessage.getAllRecipients()) : ""));
            useVariables.put(VAR_DISPLAYED_INDEX, new StringUseVariable(variablesToGenerate.get(VAR_DISPLAYED_INDEX), userMessageStartIndex + "-" + userMessageEndIndex));
            useVariables.put(VAR_TOTAL_COUNT, new StringUseVariable(variablesToGenerate.get(VAR_TOTAL_COUNT), "" + this.totalMessageCount));
            useVariables.put(VAR_UNREAD_COUNT, new StringUseVariable(variablesToGenerate.get(VAR_UNREAD_COUNT), "" + this.unreadCount));
            useVariables.put(VAR_WRITE_TO, new StringUseVariable(variablesToGenerate.get(VAR_WRITE_TO),
                    EmailPluginUtils.trimToEmpty(this.currentMessageToSend.getToName()) + " <" + EmailPluginUtils.trimToEmpty(this.currentMessageToSend.getToAddress()) + ">"));
            useVariables.put(VAR_WRITE_SUBJECT, new StringUseVariable(variablesToGenerate.get(VAR_WRITE_SUBJECT), EmailPluginUtils.trimToEmpty(this.currentMessageToSend.getSubject())));
        } catch (Exception e) {
            LOGGER.error("Couldn't generate current variable", e);
        }

        return useVariables;
    }
    //========================================================================

    // Class part : "Writting emails"
    //========================================================================
    public void startNewEmailTo(String name, String address) {
        this.currentMessageToSend.clear();
        this.currentMessageToSend.setToName(name);
        this.currentMessageToSend.setToAddress(address);
    }

    public void startReplyToCurrentEmail() throws MessagingException {
        final EmailContent currentMessage = this.getCurrentMessage();
        if (currentMessage != null && currentMessage.getMessage() != null) {
            final Message message = currentMessage.getMessage();
            this.currentMessageToSend.clear();
            // TODO : reply all ?
            final Address[] from = message.getFrom();
            if (from != null && from.length > 0) {
                InternetAddress ia = (InternetAddress) from[0];
                this.currentMessageToSend.setToAddress(ia.getAddress());
                this.currentMessageToSend.setToName(ia.getPersonal());
            }
            String subject = message.getSubject();
            if (subject != null) {
                if (!subject.regionMatches(true, 0, RE_TAG, 0, 4))
                    subject = RE_TAG + subject;
            } else {
                subject = RE_TAG;
            }
            this.currentMessageToSend.setSubject(subject);
            this.currentMessageToSend.setRepliedMessage(message);
        }
    }

    public void setCurrentEmailSubject(String text) {
        this.currentMessageToSend.setSubject(text);
    }

    public void setCurrentEmailContent(String text) {
        this.currentMessageToSend.setContent(text);
    }

    public void cancelEmailWritting() {
        this.currentMessageToSend.clear();
    }

    public boolean sendCurrentEmail() {
        final MessageToSend copiedMessageToSend = this.currentMessageToSend.copy();
        this.currentMessageToSend.clear();
        return EmailService.INSTANCE.sendMessage(currentEmailProperties, copiedMessageToSend);
    }
    //========================================================================

    // Class part : "Current message/attachment"
    //========================================================================
    public void nextContentInSelectedMessage() {
        if (this.selectedMessage.get() != null) {
            this.selectedMessage.get().nextItem();
        }
    }

    public void previousContentInSelectedMessage() {
        if (this.selectedMessage.get() != null) {
            this.selectedMessage.get().previousItem();
        }
    }

    public void selectEmail(EmailCellContent message) {
        if (message != FLAG_LOADING && message != FLAG_NOT_CONNECTED) {
            // Clear attachment list and selected attachment
            this.selectAttachment(null);
            this.updateAttachments(Arrays.asList());

            // Indicate loading
            Platform.runLater(() -> selectedMessage
                    .set(new EmailContent(null, Translation.getText("email.plugin.loading.message.content.label"), Arrays.asList(Translation.getText("email.plugin.loading.message.content.label")))));

            try {
                // Read email
                final GridPartKeyComponentI key = this.emailContentKey.attachedKeyProperty().get();
                final Pair<List<EmailAttachment>, EmailContent> contentAndAttachments = EmailService.INSTANCE.getEmailContent(message.getMessage(), key);

                // Set values
                if (contentAndAttachments != null) {
                    Platform.runLater(() -> selectedMessage.set(contentAndAttachments.getValue()));
                    updateAttachments(contentAndAttachments.getKey());
                } else {
                    setErrorOnSelectedMessage();
                }
            } catch (Exception e) {
                LOGGER.error("Couldn't convert message to email content", e);
                setErrorOnSelectedMessage();
            }
        }
    }

    private void setErrorOnSelectedMessage() {
        Platform.runLater(() -> selectedMessage.set(new EmailContent(null, Translation.getText("email.plugin.not.connected.label"), Arrays.asList(Translation.getText("email.plugin.not.connected.label")))));
    }

    public void selectAttachment(EmailAttachment attachment) {
        Platform.runLater(() -> selectedAttachment.set(attachment));
    }

    private void updateAttachments(List<EmailAttachment> attachments) {
        for (int i = 0; i < this.emailAttachmentCells.size(); i++) {
            final EmailAttachmentCellKeyOption emailAttachmentCell = this.emailAttachmentCells.get(i);
            if (i < attachments.size()) {
                final int iF = i;
                Platform.runLater(() -> emailAttachmentCell.attachmentProperty().set(attachments.get(iF)));
            } else {
                Platform.runLater(() -> emailAttachmentCell.attachmentProperty().set(null));
            }
        }
    }
    //========================================================================

    // Class part : "Start/stop"
    //========================================================================
    private EmailPluginProperties currentEmailProperties;

    public void start(final LCConfigurationI configuration) {
        currentEmailProperties = configuration.getPluginConfigProperties(EmailPlugin.PLUGIN_ID, EmailPluginProperties.class);
        if (currentEmailProperties.isEmailConfigurationSet()) {
            // Find every email list cells
            Map<GridComponentI, List<EmailCellKeyOption>> keys = new HashMap<>();
            ConfigurationComponentUtils.findKeyOptionsByGrid(EmailCellKeyOption.class, configuration, keys, null);
            keys.values().stream().flatMap(List::stream).distinct().forEach(emailCells::add);
            possibleMessageDisplayedCount = emailCells.size();
            LOGGER.info("Found {} email cell in configuration", possibleMessageDisplayedCount);

            // Find first email content key
            Map<GridComponentI, List<EmailContentKeyOption>> emailContentKeys = new HashMap<>();
            ConfigurationComponentUtils.findKeyOptionsByGrid(EmailContentKeyOption.class, configuration, emailContentKeys, null);
            this.emailContentKey = emailContentKeys.values().stream().flatMap(List::stream).findFirst().orElse(null);
            if (this.emailContentKey != null) {
                this.emailContentKey.messageProperty().bind(selectedMessage);
            }
            LOGGER.info("Found message content key in configuration : {}", this.emailContentKey != null);

            // Find every email attachment cells
            Map<GridComponentI, List<EmailAttachmentCellKeyOption>> attachmentKeys = new HashMap<>();
            ConfigurationComponentUtils.findKeyOptionsByGrid(EmailAttachmentCellKeyOption.class, configuration, attachmentKeys, null);
            attachmentKeys.values().stream().flatMap(List::stream).distinct().forEach(emailAttachmentCells::add);
            LOGGER.info("Found {} email attachment cell in configuration", emailAttachmentCells.size());

            // Find first email attachment content key
            Map<GridComponentI, List<EmailAttachmentContentKeyOption>> emailAttachmentContentKeys = new HashMap<>();
            ConfigurationComponentUtils.findKeyOptionsByGrid(EmailAttachmentContentKeyOption.class, configuration, emailAttachmentContentKeys, null);
            this.emailAttachmentContentKey = emailAttachmentContentKeys.values().stream().flatMap(List::stream).findFirst().orElse(null);
            if (this.emailAttachmentContentKey != null) {
                this.emailAttachmentContentKey.attachmentProperty().bind(selectedAttachment);
                LOGGER.info("Found attachment content key in configuration : {}", this.emailAttachmentContentKey != null);
            }

            if (!emailCells.isEmpty()) {
                this.totalMessageCount = -1;
                this.refreshInbox();
            } else {
                LOGGER.warn("Will not open email connection because there is no email cell in configuration");
            }
        } else {
            LOGGER.info("Email service not initialized because not configuration is set");
        }
    }

    public void stop(final LCConfigurationI configuration) {
        try {
            // Clear current message
            this.currentMessageToSend.clear();

            // Clear key list
            emailCells.clear();
            emailAttachmentCells.clear();

            // Clear binded
            selectedAttachment.set(null);
            selectedMessage.set(null);

            if (this.emailContentKey != null) {
                this.emailContentKey.messageProperty().unbind();
                this.emailContentKey = null;
            }
            if (this.emailAttachmentContentKey != null) {
                this.emailAttachmentContentKey.attachmentProperty().unbind();
                this.emailAttachmentContentKey = null;
            }

            // Clear variables
            userMessageEndIndex = 0;
            totalMessageCount = 0;
            unreadCount = 0;
            currentEmailProperties = null;
        } finally {
            EmailService.INSTANCE.closeConnection();
        }
    }
    //========================================================================

}
