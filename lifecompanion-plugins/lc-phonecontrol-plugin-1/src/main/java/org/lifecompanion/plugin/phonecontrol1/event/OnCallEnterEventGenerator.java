package org.lifecompanion.plugin.phonecontrol1.event;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.phonecontrol1.PhoneControlController;
import org.lifecompanion.plugin.phonecontrol1.event.categories.PhoneControlEventSubCategories;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public class OnCallEnterEventGenerator extends BaseUseEventGeneratorImpl {

    private final Runnable callEnterCallback;

    public OnCallEnterEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 20;
        this.category = PhoneControlEventSubCategories.MISC;
        this.nameID = "phonecontrol1.plugin.event.misc.call.enter.name";
        this.staticDescriptionID = "phonecontrol1.plugin.event.misc.call.enter.description";
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
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        PhoneControlController.INSTANCE.addCallEnterCallback(callEnterCallback);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
    }
    //========================================================================
}
