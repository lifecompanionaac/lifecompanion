package org.lifecompanion.plugin.phonecontrol.model.useevent;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.phonecontrol.model.useevent.PhoneControlEventSubCategories;
import org.lifecompanion.plugin.phonecontrol.controller.PhoneControlController;

public class PhoneControlEventIncomingCall extends BaseUseEventGeneratorImpl {

    private final Runnable callEnterCallback;

    public PhoneControlEventIncomingCall() {
        super();
        this.parameterizableAction = false;
        this.category = PhoneControlEventSubCategories.GENERAL;
        this.nameID = "phonecontrol.plugin.event.incomingcall.name";
        this.staticDescriptionID = "phonecontrol.plugin.event.incomingcall.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
        callEnterCallback = () -> { this.useEventListener.fireEvent(this, null, null); };
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