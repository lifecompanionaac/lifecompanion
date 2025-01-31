package org.lifecompanion.plugin.phonecontrol.keyoption;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.plugin.phonecontrol.action.SelectConversationFromListAction;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;
import org.lifecompanion.plugin.phonecontrol.model.ConversationListContent;

public class ConversationListKeyOption extends AbstractKeyOption {
    private final ObjectProperty<ConversationListContent> conv;
    private SelectConversationFromListAction selectConversationFromListAction;

    public ConversationListKeyOption() {
        super();
        this.optionNameId = "phonecontrol.plugin.key.option.conversationlist.name";
        this.optionDescriptionId = "phonecontrol.plugin.key.option.conversationlist.description";
        this.disableTextContent.set(true);
        this.disableImage.set(true);
        this.conv = new SimpleObjectProperty<>();
        this.considerKeyEmpty.set(false);
        this.initConvBinding();
    }

    public ObjectProperty<ConversationListContent> convProperty() {
        return this.conv;
    }

    @Override
    public String getIconUrl() {
        return "phonecontrol.png";
    }

    @Override
    protected void attachToImpl(final GridPartKeyComponentI key) {
        // Get the existing action, or create new one
        this.selectConversationFromListAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, SelectConversationFromListAction.class);

        if (this.selectConversationFromListAction == null) {
            this.selectConversationFromListAction = new SelectConversationFromListAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.selectConversationFromListAction);
        }

        this.selectConversationFromListAction.attachedToKeyOptionProperty().set(true);
        key.textContentProperty().set(AppModeController.INSTANCE.isUseMode() ? null : Translation.getText("phonecontrol.plugin.key.option.conversationlist.default.text"));
    }

    @Override
    protected void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.selectConversationFromListAction);
        key.textContentProperty().set(null);
    }

    private void initConvBinding() {
        this.conv.addListener((obs, ov, nv) -> {
            final GridPartKeyComponentI key = this.attachedKey.get();

            if (nv != null) {
                if (nv == ConnexionController.CONV_LOADING) {
                    key.textContentProperty().set(Translation.getText("phonecontrol.plugin.key.option.list.loading"));
                } else if (nv == ConnexionController.CONV_NOT_CONNECTED) {
                    key.textContentProperty().set(Translation.getText("phonecontrol.plugin.key.option.list.notconnected"));
                } else if (nv == ConnexionController.CONV_END_MESSAGE) {
                    key.textContentProperty().set(Translation.getText("phonecontrol.plugin.key.option.conversationlist.empty"));
                } else {
                    key.textContentProperty().set(getConversationCellString(nv));
                    key.getKeyTextStyle().boldProperty().forced().setValue(!nv.isSeen());
                }
            } else {
                key.textContentProperty().set(null);
                key.getKeyTextStyle().boldProperty().forced().setValue(null);
            }
        });
    }

    private String getConversationCellString(final ConversationListContent conv) {
        return conv.toString();
    }
}
