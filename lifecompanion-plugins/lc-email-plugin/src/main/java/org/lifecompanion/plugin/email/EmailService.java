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

import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerLineI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerWordI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerWordPartI;
import org.lifecompanion.model.impl.configurationcomponent.WriterEntry;
import org.lifecompanion.model.impl.textcomponent.TextDisplayerLineHelper;
import org.lifecompanion.plugin.email.model.EmailAttachment;
import org.lifecompanion.plugin.email.model.EmailContent;
import org.lifecompanion.plugin.email.model.MessageToSend;
import org.predict4all.nlp.Separator;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.email.EmailPopulatingBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.MailerBuilder.MailerRegularBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum EmailService {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public static final SimpleDateFormat EMAIL_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy - HH:mm");

    private static final double KEY_MARGIN_Y = 50.0, KEY_MARGIN_X = 50.0;
    private static final String MIME_TEXT_PLAIN = "text/plain";
    private static final String MIME_TEXT_HTML = "text/html";
    private static final String MIME_MULTIPART = "multipart/*";
    private static final String MIME_MULTIPART_ALTERNATIVE = "multipart/alternative";

    private static final Map<String, String> VALID_ATTACHMENT_TYPES = new HashMap<>();

    static {
        VALID_ATTACHMENT_TYPES.put("image/jpeg", "jpeg");
        VALID_ATTACHMENT_TYPES.put("image/jpg", "jpg");
        VALID_ATTACHMENT_TYPES.put("image/png", "png");
        VALID_ATTACHMENT_TYPES.put("image/gif", "gif");
    }

    // Tech object
    private Store imapStore;
    private Folder imapFolder;
    private Mailer mailer;

    private final Set<Consumer<Boolean>> mailSentCallbacks;
    private final Set<Consumer<Integer>> unreadCountUpdateCallbacks;

    private final AtomicBoolean openingConnection;

    EmailService() {
        openingConnection = new AtomicBoolean(false);
        mailSentCallbacks = new HashSet<>(5);
        unreadCountUpdateCallbacks = new HashSet<>(5);
    }

    // Class part : "Listeners"
    //========================================================================
    public void addMailSentCallback(Consumer<Boolean> mailSentCallback) {
        this.mailSentCallbacks.add(mailSentCallback);
    }

    public void removeMailSentCallback(Consumer<Boolean> mailSentCallback) {
        this.mailSentCallbacks.remove(mailSentCallback);
    }

    public void addUnreadCountUpdateCallback(Consumer<Integer> unreadCountUpdatedCallback) {
        this.unreadCountUpdateCallbacks.add(unreadCountUpdatedCallback);
    }

    public void removeUnreadCountUpdateCallback(Consumer<Integer> unreadCountUpdatedCallback) {
        this.unreadCountUpdateCallbacks.remove(unreadCountUpdatedCallback);
    }
    //========================================================================

    // Class part : "Getting emails"
    //========================================================================
    public int getMessageCount() throws MessagingException {
        return imapFolder.getMessageCount();
    }

    public int getUnreadMessageCount() throws MessagingException {
        final int unreadMessageCount = imapFolder.getUnreadMessageCount();
        this.unreadCountUpdateCallbacks.forEach(l -> l.accept(unreadMessageCount));
        return unreadMessageCount;
    }

    public Message[] getMessages(int start, int end) throws MessagingException {
        return imapFolder.getMessages(start, end);
    }
    //========================================================================

    // Class part : "Send emails"
    //========================================================================
    public boolean sendMessage(EmailPluginProperties emailPluginProperties, MessageToSend toSend) {
        try {
            // Make sure that javax.mail API class are correctly loaded (because of plugin loading, this issue pops..)
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            // Create email (because modern client support re without referring original email, the "Re:" in the subject is enough)
            EmailPopulatingBuilder builder = EmailBuilder.startingBlank()//
                    .to(toSend.getToName(), toSend.getToAddress())//
                    .withSubject(toSend.getSubject())//
                    .from(emailPluginProperties.fromNameProperty().get(), emailPluginProperties.loginProperty().get())//
                    .withPlainText(toSend.getContent());

            // Set In-Reply-To header if response
            String msgId = findEmailId(toSend);
            if (msgId != null) {
                builder = builder.withHeader("In-Reply-To", msgId);
            }

            final Email email = builder.buildEmail();
            LOGGER.info("Mail built, will try to send it...");
            mailer.sendMail(email);
            LOGGER.info("Mail sent!");
            this.mailSentCallbacks.forEach(l -> l.accept(true));
            return true;
        } catch (Exception e) {
            LOGGER.warn("Couldn't send email", e);
            this.mailSentCallbacks.forEach(l -> l.accept(false));
        }
        return false;
    }

    private String findEmailId(MessageToSend toSend) {
        if (toSend.getRepliedMessage() != null) {
            try {
                final String[] header = toSend.getRepliedMessage().getHeader("Message-Id");
                if (header.length > 0)
                    return header[0];
            } catch (MessagingException e) {
                LOGGER.warn("Failed to get replied message id", e);
            }
        }
        return null;
    }
    //========================================================================

    // Class part : "Email content"
    //========================================================================
    Pair<List<EmailAttachment>, EmailContent> getEmailContent(Message message, GridPartKeyComponentI emailContentKey) {
        try {
            // Read email content and attachments
            long startRead = System.currentTimeMillis();
            List<EmailAttachment> attachments = new ArrayList<>();
            String emailTextContent = cleanEmailText(convertPartToString(message, new StringBuilder(), attachments).toString());
            LOGGER.info("Email fully read from server in {} s", (System.currentTimeMillis() - startRead) / 1000.0);
            return new Pair<>(attachments, new EmailContent(message, emailTextContent, getPartsForEmailText(emailContentKey, emailTextContent)));
        } catch (Exception e) {
            LOGGER.error("Couldn't convert message to email content", e);
            return null;
        }
    }

    private String cleanEmailText(String emailTextContent) {
        // Detect double lines
        emailTextContent = emailTextContent.replaceAll("[\r\n]+", "\n");

        // Remove after signature
        int lastIndexOf = emailTextContent.indexOf("--");
        if (lastIndexOf > 1) {
            emailTextContent = emailTextContent.substring(0, lastIndexOf);
        }

        // Detect inline tags (images...)
        Pattern pattern = Pattern.compile("\\[.*:(.*)\\]", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(emailTextContent);
        StringBuffer sb = new StringBuffer(emailTextContent.length());
        while (matcher.find()) {
            String text = matcher.group(1);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(text));
        }
        matcher.appendTail(sb);
        emailTextContent = sb.toString();

        // Detect html link
        emailTextContent = emailTextContent.replaceAll("<http.*>", "");

        // Remove "________________________________"
        int lastIndexOfSep = emailTextContent.indexOf("________________________________");
        if (lastIndexOfSep > 1) {
            emailTextContent = emailTextContent.substring(0, lastIndexOfSep);
        }

        // Check each line, detect previous message start
        pattern = Pattern.compile("Le (.{2,4}\\.)? ?\\d{1,2} .{3,4}\\. \\d{2,4} à \\d{2,4}:\\d{2,4}", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(emailTextContent);
        if (matcher.find()) {
            int startIndex = matcher.start();
            if (startIndex > 1) {
                emailTextContent = emailTextContent.substring(0, startIndex);
            }
        }
        return emailTextContent;
    }

    private List<String> getPartsForEmailText(GridPartKeyComponentI emailContentKey, String emailTextContent) {
        List<String> convertedTextParts = new ArrayList<>();

        WriterEntry we = new WriterEntry(emailTextContent, false);
        final TextCompStyleI keyTextStyle = emailContentKey.getKeyTextStyle();
        final double keyWidth = emailContentKey.layoutWidthProperty().get() - KEY_MARGIN_X;
        final double keyHeight = emailContentKey.layoutHeightProperty().get() - KEY_MARGIN_Y;
        final List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(Collections.singletonList(we), keyTextStyle, keyWidth, true);

        double height = 0.0;
        StringBuilder currentKeyContent = new StringBuilder();
        for (TextDisplayerLineI line : lines) {
            String lineStr = convertLineToString(line);
            if (height + line.getTextHeight() >= keyHeight) {
                //currentKeyContent.append();
                convertedTextParts.add(currentKeyContent.toString());
                currentKeyContent = new StringBuilder(EmailPluginUtils.trimToEmpty(lineStr));
                height = line.getTextHeight();
            } else {
                height += line.getTextHeight();
                currentKeyContent.append(EmailPluginUtils.trimToEmpty(lineStr)).append("\n");
            }
        }
        convertedTextParts.add(currentKeyContent.toString());
        return convertedTextParts;
    }

    private String convertLineToString(TextDisplayerLineI line) {
        StringBuilder l = new StringBuilder();
        for (TextDisplayerWordI w : line.getWords()) {
            l.append(w.getParts().stream().map(TextDisplayerWordPartI::getPart).collect(Collectors.joining("")));
            if (w.getWordSeparatorChar() != null && Separator.getSeparatorFor(w.getWordSeparatorChar()) != Separator.NEWLINE) {
                l.append(w.getWordSeparatorChar());
            }
        }
        return l.toString();
    }
    //========================================================================

    // Class part : "Email converter"
    //========================================================================
    private StringBuilder convertPartToString(Part part, StringBuilder sb, List<EmailAttachment> attachments) throws IOException, MessagingException {
        // Part is plain text
        final Object content = part.getContent();
        if (part.isMimeType(MIME_TEXT_PLAIN)) {
            LOGGER.info("Email part is plain text");
            sb.append(handleStringOrInputStream(content, part));
        }
        // Part is html text
        else if (part.isMimeType(MIME_TEXT_HTML)) {
            LOGGER.info("Email part is html (will read text from html content)");
            sb.append(Jsoup.parse(handleStringOrInputStream(content, part)).text());
        }
        // Part is multipart (instance)
        else if (content instanceof Multipart) {
            LOGGER.info("Email part is Multipart (will read text from each part)");
            handleMultipart((Multipart) content, sb, attachments, part.isMimeType(MIME_MULTIPART_ALTERNATIVE));
        }
        // Part is multipart/* (no existing handler for this one)
        else if (part.isMimeType(MIME_MULTIPART)) {
            LOGGER.info("Email part is Multipart/* (will read text from the best matching part)");
            MimeMultipart mmp = new MimeMultipart(part.getDataHandler().getDataSource());
            this.handleMultipart(mmp, sb, attachments, part.isMimeType(MIME_MULTIPART_ALTERNATIVE));
        }
        // Part is attachment
        else {
            boolean foundValidMimeType = false;
            final Set<String> validTypes = VALID_ATTACHMENT_TYPES.keySet();
            for (String type : validTypes) {
                if (part.isMimeType(type)) {
                    final File attFile = File.createTempFile("lc-email-attachment-", "." + VALID_ATTACHMENT_TYPES.get(type));
                    LOGGER.info("Found a valid mime type {} for the email attachment, will be saved to {}", type, attFile);
                    long startIO = System.currentTimeMillis();
                    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(attFile))) {
                        try (InputStream is = new BufferedInputStream(part.getInputStream())) {
                            IOUtils.copy(is, os, 8192);
                        }
                    }
                    LOGGER.info("Attachment successfully saved to {} in {} ms", attFile, System.currentTimeMillis() - startIO);
                    attachments.add(new EmailAttachment(part.getFileName(), attFile));
                    foundValidMimeType = true;
                    break;
                }
            }
            if (!foundValidMimeType) {
                LOGGER.warn("Didn't find any valid mime type for the attachment {}, content type {}", part.getFileName(), part.getContentType());
                attachments.add(new EmailAttachment(part.getFileName(), null));
            }
        }
        return sb;
    }

    private void handleMultipart(Multipart multipart, StringBuilder sb, List<EmailAttachment> attachments, boolean selectSinglePart) throws IOException, MessagingException {
        Part favoriteAlternative = null;
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            final BodyPart bodyPart = multipart.getBodyPart(i);
            if (selectSinglePart) {
                if (bodyPart.isMimeType(MIME_TEXT_PLAIN) && isPartNotEmpty(bodyPart)) {
                    favoriteAlternative = bodyPart;
                    break;
                } else if (bodyPart.isMimeType(MIME_TEXT_HTML) && isPartNotEmpty(bodyPart)) {
                    favoriteAlternative = bodyPart;
                }
            } else {
                convertPartToString(bodyPart, sb, attachments);
            }
        }
        if (favoriteAlternative != null) {
            LOGGER.info("Found a matching multipart for the alternative mail, type is {}", favoriteAlternative.getContentType());
            convertPartToString(favoriteAlternative, sb, attachments);
        } else {
            LOGGER.warn("Didn't find any correct alternative in alternative email parts");
        }
    }

    private boolean isPartNotEmpty(BodyPart bodyPart) throws IOException, MessagingException {
        final StringBuilder contentTester = new StringBuilder();
        convertPartToString(bodyPart, contentTester, new ArrayList<>());
        return StringUtils.isNotBlank(contentTester.toString());
    }

    private String handleStringOrInputStream(final Object content, Part part) throws MessagingException, IOException {
        // Try to determine charset
        String charset = "UTF-8";
        try {
            String contentType = part.getContentType();
            LOGGER.info("Will try to read charset from \"{}\" or will use default charset \"{}\"", contentType, charset);
            if (StringUtils.containsIgnoreCase(contentType, "charset")) {
                final String[] headerParts = StringUtils.split(contentType, ";");
                String partWithCharset = Arrays.stream(headerParts).filter(p -> StringUtils.containsIgnoreCase(p, "charset")).findAny().orElse(null);
                final String[] split = StringUtils.split(partWithCharset, '=');
                if (split != null && split.length > 1) {
                    String ncharset = StringUtils.upperCase(StringUtils.defaultIfBlank(StringUtils.removeEnd(StringUtils.removeStart(split[1], "\""), "\""), charset));
                    LOGGER.info("Charset read for the part from Content-Type header : {}, will check it", charset);
                    Charset.forName(ncharset);//will throw an exception if charset is unknown
                    charset = ncharset;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Wasn't able to determine charset", e);
        }

        StringBuilder sb = new StringBuilder();
        if (content instanceof InputStream) {
            try (BufferedReader bf = new BufferedReader(new InputStreamReader((InputStream) content, charset))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    if (!StringUtils.startsWith(line, ">")) {//Cancel "answer" lines
                        sb.append(StringUtils.replaceChars(line, (char) 146, '\'')).append("\n");//fix rare encoding problem...
                    }
                }
            }
        } else {
            sb.append(content);
        }
        return sb.toString();
    }
    //========================================================================

    // Class part : "Connection management"
    //========================================================================
    public Pair<Boolean, Boolean> testConnections(EmailPluginProperties emailPluginProperties) {
        boolean smtp = false;
        boolean imap = false;
        // Check SMTP
        try {
            final Mailer smtpTest = this.createSmtpConnection(emailPluginProperties);
            smtpTest.testConnection();
            smtp = true;
        } catch (Throwable t) {
            LOGGER.warn("SMTP connection check failed", t);
        }
        // Check IMAP
        try {
            final Pair<Store, Folder> imapConnection = this.createImapConnection(emailPluginProperties);
            this.closeImapConnection(imapConnection.getKey(), imapConnection.getValue());
            imap = true;
        } catch (Throwable t) {
            LOGGER.warn("IMAP connection check failed", t);
        }
        return new Pair<>(imap, smtp);
    }

    private Pair<Store, Folder> createImapConnection(EmailPluginProperties emailPluginProperties) throws Exception {
        LOGGER.info("Will initialize IMAP connection...");
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", emailPluginProperties.imapsHostProperty().get());
        properties.put("mail.imaps.port", emailPluginProperties.imapsPortProperty().get());
        properties.put("mail.imaps.timeout", 10_000);
        // Increase chunk size to 5MB : very important for download performance (but consume memory)
        properties.put("mail.imaps.fetchsize", "5000000");
        Session imapSession = Session.getInstance(properties, null);
        Store imapStore = imapSession.getStore("imaps");
        LOGGER.info("IMAP session and store initialized");
        imapStore.connect(emailPluginProperties.loginProperty().get(), emailPluginProperties.passwordProperty().get());
        LOGGER.info("IMAP connection successful");
        Folder imapFolder = imapStore.getFolder(emailPluginProperties.imapsFolderProperty().get());
        imapFolder.open(Folder.READ_WRITE);
        LOGGER.info("IMAP folder {} openned", imapFolder.getName());
        return new Pair<>(imapStore, imapFolder);
    }

    private void closeImapConnection(Store imapStore, Folder imapFolder) {
        if (imapFolder != null) {
            try {
                LOGGER.info("Closing IMAP folder...");
                imapFolder.close(false);
                LOGGER.info("IMAP folder closed");
            } catch (Exception e) {
                LOGGER.warn("Couldn't close IMAP folder", e);
            }
        }
        if (imapStore != null) {
            try {
                LOGGER.info("Closing IMAP store...");
                imapStore.close();
                LOGGER.info("IMAP store closed");
            } catch (Exception e) {
                LOGGER.warn("Couldn't close IMAP store", e);
            }
        }
    }

    private Mailer createSmtpConnection(EmailPluginProperties emailPluginProperties) {
        LOGGER.info("Will initialize SMTP configuration...");
        MailerRegularBuilder mailerBuilder = MailerBuilder//
                .withSMTPServerHost(emailPluginProperties.smtpHostProperty().get())//
                .withSMTPServerPort(Integer.parseInt(emailPluginProperties.smtpPortProperty().get()))//
                .withTransportStrategy(TransportStrategy.SMTP_TLS)//
                .withSMTPServerUsername(emailPluginProperties.loginProperty().get())//
                .withSMTPServerPassword(emailPluginProperties.passwordProperty().get());
        if (!org.lifecompanion.framework.commons.utils.lang.StringUtils.isBlank(emailPluginProperties.proxyHostProperty().get())) {
            mailerBuilder = mailerBuilder.withProxy(emailPluginProperties.proxyHostProperty().get(),
                    !org.lifecompanion.framework.commons.utils.lang.StringUtils.isBlank(emailPluginProperties.proxyPortProperty().get()) ? Integer.parseInt(emailPluginProperties.proxyPortProperty().get()) : 80);
        }
        final Mailer m = mailerBuilder//
                .buildMailer();
        LOGGER.info("SMTP configuration initialized");
        return m;
    }

    public boolean isConnectionOpened() {
        return this.imapStore != null && this.imapStore.isConnected() && this.imapFolder != null && this.imapFolder.isOpen();
    }

    public void requestOpenConnection(EmailPluginProperties emailPluginProperties, Consumer<Boolean> callback) {
        // Open connection in background Thread (only when needed)
        if (!isConnectionOpened()) {
            if (!openingConnection.getAndSet(true)) {
                Thread openConnectionThread = new Thread(() -> {
                    this.closeConnection();
                    try {
                        Pair<Store, Folder> imap = this.createImapConnection(emailPluginProperties);
                        this.imapStore = imap.getKey();
                        this.imapFolder = imap.getValue();
                        this.mailer = this.createSmtpConnection(emailPluginProperties);
                        callback.accept(isConnectionOpened());
                    } catch (Exception e) {
                        LOGGER.error("Couldn't initialize IMAP connection", e);
                        callback.accept(false);
                    } finally {
                        openingConnection.set(false);
                    }
                });
                openConnectionThread.setName("lc-email-plugin-OpenConnectionThread");
                openConnectionThread.setDaemon(true);
                openConnectionThread.start();
            }
        } else {
            callback.accept(true);
        }
    }

    public void closeConnection() {
        this.closeImapConnection(imapStore, imapFolder);
        this.imapStore = null;
        this.imapFolder = null;
        this.mailer = null;
    }
    //========================================================================

}
