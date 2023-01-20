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
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.model.impl.imagedictionary.StaticImageElement;
import org.lifecompanion.plugin.email.model.EmailAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class EmailAttachmentContentKeyOption extends AbstractKeyOption {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailAttachmentContentKeyOption.class);

    private ObjectProperty<EmailAttachment> attachment;

    public EmailAttachmentContentKeyOption() {
        super();
        this.optionNameId = "email.plugin.key.option.email.attachment.content";
        this.disableTextContent.set(true);
        this.disableImage.set(true);
        this.attachment = new SimpleObjectProperty<>();
        this.considerKeyEmpty.set(false);
        this.initAttachmentBinding();
    }

    @Override
    public String getIconUrl() {
        return "icon_key_option_attachment.png";
    }

    @Override
    protected void attachToImpl(final GridPartKeyComponentI key) {
        if (key.getKeyStyle().textPositionProperty().value().getValue() != TextPosition.BOTTOM) {
            key.getKeyStyle().textPositionProperty().selected().setValue(TextPosition.BOTTOM);
        }
        key.textContentProperty().set(AppModeController.INSTANCE.isUseMode() ? null
                : Translation.getText("email.plugin.key.option.email.attachment.content.default.text"));
    }

    public ObjectProperty<EmailAttachment> attachmentProperty() {
        return this.attachment;
    }

    @Override
    protected void detachFromImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().set(null);
        key.imageVTwoProperty().set(null);
    }

    private void initAttachmentBinding() {
        this.attachment.addListener((obs, ov, nv) -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (nv != null) {
                try {
                    key.textContentProperty().set(nv.getName());
                    File attachmentFile = nv.getFile();
                    if (attachmentFile != null) {
                        Image image = new Image(nv.getFile().toURI().toString(), key.wantedImageWidthProperty().get(), key.wantedImageHeightProperty().get(), true, true, true);
                        key.imageVTwoProperty().set(new StaticImageElement(image));
                    }
                } catch (Exception e) {
                    LOGGER.error("Couldn't get message attachment to update key", e);
                }
            } else {
                key.textContentProperty().set(null);
                key.imageVTwoProperty().set(null);
            }
        });
    }

}
