package org.lifecompanion.plugin.phonecontrol.event;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;
import org.lifecompanion.plugin.phonecontrol.event.categories.PhoneControlEventSubCategories;

public class OnCallEnterEventGenerator extends BaseUseEventGeneratorImpl {
    private final Runnable callEnterCallback;

    public OnCallEnterEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 20;
        this.category = PhoneControlEventSubCategories.MISC;
        this.nameID = "phonecontrol.plugin.event.misc.callenter.name";
        this.staticDescriptionID = "phonecontrol.plugin.event.misc.callenter.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
        callEnterCallback = () -> {
            this.useEventListener.fireEvent(this, null, null);
        };
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/phone.png";
    }

    // Class part : "Mode start/stop"
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        ConnexionController.INSTANCE.addCallEnterCallback(callEnterCallback);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) { }
}
