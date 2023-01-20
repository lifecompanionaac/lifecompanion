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
import org.lifecompanion.plugin.email.actions.SelectAttachmentAction;
import org.lifecompanion.plugin.email.model.EmailAttachment;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class EmailAttachmentCellKeyOption extends AbstractKeyOption {

    private ObjectProperty<EmailAttachment> attachment;

    private SelectAttachmentAction selectAttachmentAction;

    public EmailAttachmentCellKeyOption() {
        super();
        this.optionNameId = "email.plugin.key.option.email.attachment.cell";
        this.disableTextContent.set(true);
        this.disableImage.set(true);
        this.attachment = new SimpleObjectProperty<>();
        this.considerKeyEmpty.set(false);
        this.initAttachmentBinding();
    }

    public ObjectProperty<EmailAttachment> attachmentProperty() {
        return attachment;
    }

    @Override
    public String getIconUrl() {
        return "icon_key_option_attachment.png";
    }

    @Override
    protected void attachToImpl(final GridPartKeyComponentI key) {
        //Get the existing action, or create new one
        this.selectAttachmentAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, SelectAttachmentAction.class);
        if (this.selectAttachmentAction == null) {
            this.selectAttachmentAction = new SelectAttachmentAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.selectAttachmentAction);
        }
        this.selectAttachmentAction.attachedToKeyOptionProperty().set(true);
        key.textContentProperty().set(AppModeController.INSTANCE.isUseMode() ? null
                : Translation.getText("email.plugin.key.option.email.attachment.cell.default.text"));
    }

    @Override
    protected void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.selectAttachmentAction);
        key.textContentProperty().set(null);
    }

    private void initAttachmentBinding() {
        this.attachment.addListener((obs, ov, nv) -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (nv != null) {
                key.textContentProperty().set(nv.getName());
            } else {
                key.textContentProperty().set(null);
            }
        });
    }

}
