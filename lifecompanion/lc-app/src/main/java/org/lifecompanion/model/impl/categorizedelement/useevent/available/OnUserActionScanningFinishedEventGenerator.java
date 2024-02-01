package org.lifecompanion.model.impl.categorizedelement.useevent.available;

import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.controller.virtualmouse.ScanningMouseController;
public class OnUserActionScanningFinishedEventGenerator extends BaseUseEventGeneratorImpl {

    public OnUserActionScanningFinishedEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 0;
        this.category = DefaultUseEventSubCategories.END_CURSOR_STRIP;
        this.nameID = "use.event.on.ua.scanning.finished.name";
        this.staticDescriptionID = "use.event.on.ua.scanning.finished.description";
        this.configIconPath = "sequence/icon_on_sequence_finished.png";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    // Class part : "Mode start/stop"
    //========================================================================
    private Runnable listener;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.listener = () -> {
            this.useEventListener.fireEvent(this, null, null);
        };
        ScanningMouseController.INSTANCE.getOnScanningFinishedListeners().add(this.listener);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        ScanningMouseController.INSTANCE.getOnScanningFinishedListeners().remove(listener);
    }
    //========================================================================
}