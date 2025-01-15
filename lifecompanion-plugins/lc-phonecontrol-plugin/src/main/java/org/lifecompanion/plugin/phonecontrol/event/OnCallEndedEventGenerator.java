package org.lifecompanion.plugin.phonecontrol.event;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;
import org.lifecompanion.plugin.phonecontrol.event.categories.PhoneControlEventSubCategories;

public class OnCallEndedEventGenerator extends BaseUseEventGeneratorImpl {
    private final Runnable callEndedCallback;

    public OnCallEndedEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 20;
        this.category = PhoneControlEventSubCategories.MISC;
        this.nameID = "phonecontrol.plugin.event.misc.callhangup.name";
        this.staticDescriptionID = "phonecontrol.plugin.event.misc.callhangup.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
        callEndedCallback = () -> {
            this.useEventListener.fireEvent(this, null, null);
        };
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/phone_hangup.png";
    }

    // Class part : "Mode start/stop"
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        ConnexionController.INSTANCE.addCallEndedCallback(callEndedCallback);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) { }
}
