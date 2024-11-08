package org.lifecompanion.plugin.phonecontrol.model.useevent;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.phonecontrol.model.useevent.PhoneControlEventSubCategories;
import org.lifecompanion.plugin.phonecontrol.controller.PhoneControlController;


public class PhoneControlEventEndCall extends BaseUseEventGeneratorImpl {

    private final Runnable callEndedCallback;

    public PhoneControlEventEndCall() {
        super();
        this.parameterizableAction = false;
        this.order = 20;
        this.category = PhoneControlEventSubCategories.GENERAL;
        this.nameID = "phonecontrol.plugin.event.endcall.name";
        this.staticDescriptionID = "phonecontrol.plugin.event.endcall.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
        callEndedCallback = () -> { this.useEventListener.fireEvent(this, null, null); };
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/phonecontrol.png";
    }

    // Class part : "Mode start/stop"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        PhoneControlController.INSTANCE.addCallEndedCallback(callEndedCallback);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
    }
    //========================================================================
}