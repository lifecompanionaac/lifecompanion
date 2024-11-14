package org.lifecompanion.plugin.phonecontrol2.model.useevent;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.phonecontrol2.controller.PhoneControlController;

public class PhoneControlEventIncomingCall extends BaseUseEventGeneratorImpl {

    private final Runnable callEnterCallback;

    public PhoneControlEventIncomingCall() {
        super();
        this.parameterizableAction = false;
        this.category = PhoneControlEventSubCategories.GENERAL;
        this.nameID = "phonecontrol2.plugin.event.incomingcall.name";
        this.staticDescriptionID = "phonecontrol2.plugin.event.incomingcall.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
        callEnterCallback = () -> {
            this.useEventListener.fireEvent(this, null, null);
        };
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/phonecontrol.png";
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        PhoneControlController.INSTANCE.addCallEnterCallback(callEnterCallback);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
    }
}
