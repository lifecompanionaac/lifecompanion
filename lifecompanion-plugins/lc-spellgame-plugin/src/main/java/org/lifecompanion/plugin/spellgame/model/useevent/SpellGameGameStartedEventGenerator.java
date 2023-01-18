package org.lifecompanion.plugin.spellgame.model.useevent;

import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.spellgame.controller.SpellGameController;

public class SpellGameGameStartedEventGenerator extends BaseUseEventGeneratorImpl {

    private final Runnable listener;

    public SpellGameGameStartedEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 10;
        this.category = SpellGameEventSubCategories.GENERAL;
        this.nameID = "spellgame.plugin.event.game.started.name";
        this.staticDescriptionID = "spellgame.plugin.event.game.started.description";
        this.configIconPath = "filler_icon.png";
        this.variableDescriptionProperty().set(getStaticDescription());
        listener = () -> {
            if (AppModeController.INSTANCE.isUseMode()) {
                this.useEventListener.fireEvent(this, null, null);
            }
        };
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        SpellGameController.INSTANCE.addGameStartedListener(listener);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        SpellGameController.INSTANCE.removeGameStartedListener(listener);
    }
}

