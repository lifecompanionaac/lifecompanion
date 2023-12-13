package org.lifecompanion.plugin.ppp.keyoption;

import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.plugin.ppp.actions.ActionSelectAction;
import org.lifecompanion.plugin.ppp.model.Action;

public class ActionCellKeyOption extends AbstractKeyOption {
    private SimpleObjectProperty<Action> action;

    private ActionSelectAction selectAction;

    public ActionCellKeyOption() {
        super();
        this.optionNameId = "ppp.plugin.keyoption.action.cell.name";
        this.optionDescriptionId = "ppp.plugin.keyoption.action.cell.description";
        this.disableTextContent.set(true);
        this.disableImage.set(true);
        this.considerKeyEmpty.set(false);
        this.action = new SimpleObjectProperty<>();
        this.initActionBinding();
    }

    public SimpleObjectProperty<Action> actionProperty() {
        return this.action;
    }

    @Override
    public String getIconUrl() {
        return "keyoptions/icon_action_cell.png";
    }

    @Override
    protected void attachToImpl(final GridPartKeyComponentI key) {
        this.selectAction = key.getActionManager()
                .getFirstActionOfType(UseActionEvent.ACTIVATION, ActionSelectAction.class);
        if (this.selectAction == null) {
            this.selectAction = new ActionSelectAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.selectAction);
        }
        this.selectAction.attachedToKeyOptionProperty().set(true);

        key.textContentProperty().set(AppModeController.INSTANCE.isUseMode() ? null : Translation.getText("ppp.plugin.keyoption.action.cell.default_text"));
    }

    @Override
    protected void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.selectAction);
        key.textContentProperty().set(null);
    }

    private void initActionBinding() {
        this.action.addListener((obs, ov, nv) -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (nv != null) {
                key.textContentProperty().set(nv.getName());
                this.selectAction.actionProperty().set(nv);
            } else {
                key.textContentProperty().set(null);
                this.selectAction.actionProperty().set(null);
            }
        });
    }
}
