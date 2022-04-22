package org.lifecompanion.ui.app.main.ribbon.available.create;

import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.ui.editmode.AddComponentCategoryEnum;

public class AddRootRibbonPart extends AbstractAddComponentRibbonPart implements LCViewInitHelper {

    public AddRootRibbonPart() {
        super(AddComponentCategoryEnum.ROOT);
    }
}
