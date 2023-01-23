package org.lifecompanion.plugin.spellgame.model.useevent;

import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.spellgame.controller.SpellGameController;

public class SpellGameGameEndedEventGenerator extends BaseUseEventGeneratorImpl {

    private final Runnable listener;

    public SpellGameGameEndedEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 20;
        this.category = SpellGameEventSubCategories.GENERAL;
        this.nameID = "spellgame.plugin.event.game.end.name";
        this.staticDescriptionID = "spellgame.plugin.event.game.end.description";
        this.configIconPath = "general/game_ended.png";
        this.variableDescriptionProperty().set(getStaticDescription());
        listener = () -> {
            if (AppModeController.INSTANCE.isUseMode()) {
                this.useEventListener.fireEvent(this, null, null);
            }
        };
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        SpellGameController.INSTANCE.addGameEndedListener(listener);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        SpellGameController.INSTANCE.removeGameEndedListener(listener);
    }
}

