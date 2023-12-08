package org.lifecompanion.plugin.ppp.keyoption;

import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.plugin.ppp.actions.ActionSelectAction;
import org.lifecompanion.plugin.ppp.actions.UserGroupSelectAction;
import org.lifecompanion.plugin.ppp.model.Action;
import org.lifecompanion.plugin.ppp.model.UserGroup;

public class UserGroupCellKeyOption extends AbstractKeyOption {
    private SimpleObjectProperty<UserGroup> group;

    private UserGroupSelectAction selectAction;

    public UserGroupCellKeyOption() {
        super();
        this.optionNameId = "group_cell";
        this.disableTextContent.set(true);
        this.disableImage.set(true);
        this.considerKeyEmpty.set(false);
        this.group = new SimpleObjectProperty<>(new UserGroup());
        this.initActionBinding();
    }

    public SimpleObjectProperty<UserGroup> groupProperty() {
        return group;
    }

    @Override
    public String getIconUrl() {
        return "keyoptions/icon_action_cell.png";
    }

    @Override
    protected void attachToImpl(final GridPartKeyComponentI key) {
        this.selectAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, UserGroupSelectAction.class);
        if (this.selectAction == null) {
            this.selectAction = new UserGroupSelectAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(0,this.selectAction);
        }
        this.selectAction.attachedToKeyOptionProperty().set(true);
        // FIXME : translation
        key.textContentProperty().set(AppModeController.INSTANCE.isUseMode() ? null : Translation.getText("todo"));
    }

    @Override
    protected void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.selectAction);
        key.textContentProperty().set(null);
    }

    private void initActionBinding() {
        this.group.addListener((obs, ov, nv) -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (nv != null) {
                key.textContentProperty().set(nv.getGroupName());
            } else {
                key.textContentProperty().set(null);
            }
        });
    }
}
