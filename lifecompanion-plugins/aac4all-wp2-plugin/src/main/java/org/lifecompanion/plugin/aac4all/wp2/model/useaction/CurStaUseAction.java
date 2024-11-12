package org.lifecompanion.plugin.aac4all.wp2.model.useaction;

import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.categorizedelement.useaction.BaseUseActionImpl;
import org.lifecompanion.plugin.aac4all.wp2.controller.AAC4AllWp2Controller;

public class CurStaUseAction extends BaseUseActionImpl<GridPartKeyComponentI> {

    public CurStaUseAction() {
        super(GridPartKeyComponentI.class);
        this.category = AAC4AllWp2SubCategories.TODO;
        this.nameID = "aac4aal.wp2.plugin.action.cur.sta.use.action.name";
        this.staticDescriptionID = "aac4aal.wp2.plugin.action.cur.sta.use.action.description";
        this.configIconPath = "filler_icon_32px.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    String lastText;

    public void cleanLastText(){
        this.lastText = null;
    }

    @Override
    public void eventStarts(UseActionEvent useActionEvent) {
        GridPartKeyComponentI key = parentComponentProperty().get();
        if (key != null && AppModeController.INSTANCE.isUseMode()) {
            this.lastText = key.textContentProperty().get();
        }
    }

    @Override
    public void eventEnds(UseActionEvent useActionEvent) {
        GridPartKeyComponentI key = parentComponentProperty().get();
        if (key != null && AppModeController.INSTANCE.isUseMode()) {
            if (StringUtils.isEquals(lastText, key.textContentProperty().get())) {
                AAC4AllWp2Controller.INSTANCE.shiftCurSta();
            }
            SelectionModeController.INSTANCE.goToGridPart(key);
        }
    }
}