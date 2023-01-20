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
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.plugin.email.model.EmailContent;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class EmailContentKeyOption extends AbstractKeyOption {

    private ObjectProperty<EmailContent> message;

    public EmailContentKeyOption() {
        super();
        this.optionNameId = "email.plugin.key.option.email.content";
        this.disableTextContent.set(true);
        this.disableImage.set(true);
        this.message = new SimpleObjectProperty<>();
        this.considerKeyEmpty.set(false);
        this.initMessageBinding();
    }

    @Override
    public String getIconUrl() {
        return "icon_key_option_emails.png";
    }

    @Override
    protected void attachToImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().set(
                AppModeController.INSTANCE.isUseMode() ? null : Translation.getText("email.plugin.key.option.email.content.default.text"));
    }

    public ObjectProperty<EmailContent> messageProperty() {
        return this.message;
    }

    @Override
    protected void detachFromImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().unbind();
        key.textContentProperty().set(null);
    }

    private void initMessageBinding() {
        this.message.addListener((obs, ov, nv) -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (nv != null) {
                key.textContentProperty().bind(nv.currentTextProperty());
            } else {
                key.textContentProperty().unbind();
                key.textContentProperty().set(null);
            }
        });
    }

}
