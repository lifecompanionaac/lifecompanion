package org.lifecompanion.plugin.ppp.keyoption;

import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.plugin.ppp.actions.UserProfileSelectAction;
import org.lifecompanion.plugin.ppp.model.UserProfile;

public class UserProfileCellKeyOption extends AbstractKeyOption {
    private SimpleObjectProperty<UserProfile> user;
    private UserProfileSelectAction selectAction;

    public UserProfileCellKeyOption() {
        super();
        this.optionNameId = "userprofile_cell";
        this.disableTextContent.set(true);
        this.disableImage.set(true);
        this.considerKeyEmpty.set(false);
        this.user = new SimpleObjectProperty<>(new UserProfile());
        this.initActionBinding();
    }

    public SimpleObjectProperty<UserProfile> userProperty() {
        return user;
    }

    @Override
    public String getIconUrl() {
        return "keyoptions/icon_action_cell.png";
    }

    @Override
    protected void attachToImpl(final GridPartKeyComponentI key) {
        this.selectAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, UserProfileSelectAction.class);
        if (this.selectAction == null) {
            this.selectAction = new UserProfileSelectAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(0, this.selectAction);
        }
        this.selectAction.attachedToKeyOptionProperty().set(true);
        key.textContentProperty().set(AppModeController.INSTANCE.isUseMode() ? null : Translation.getText("todo"));
    }

    @Override
    protected void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.selectAction);
        key.textContentProperty().set(null);
    }

    private void initActionBinding() {
        this.user.addListener((obs, ov, nv) -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (nv != null) {
                key.textContentProperty().set(nv.getUserName());
            } else {
                key.textContentProperty().set(null);
            }
        });
    }
}
