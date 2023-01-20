package org.lifecompanion.plugin.ppp.view.records;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;

public class RecordsStage extends Stage {
    public RecordsStage(Window owner) {
        this.setTitle(LCConstant.NAME);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.DECORATED);
        this.initOwner(owner);
        this.setWidth(800);
        this.setHeight(650);
        this.setMinWidth(560);
        this.setMinHeight(560);
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
    }
}
