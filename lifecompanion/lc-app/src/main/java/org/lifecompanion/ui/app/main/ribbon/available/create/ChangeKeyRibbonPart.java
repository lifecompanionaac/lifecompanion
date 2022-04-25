package org.lifecompanion.ui.app.main.ribbon.available.create;

import javafx.beans.binding.BooleanBinding;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.ui.editmode.AddComponentCategoryEnum;

public class ChangeKeyRibbonPart extends AbstractAddComponentRibbonPart implements LCViewInitHelper {

    public ChangeKeyRibbonPart() {
        super(AddComponentCategoryEnum.KEY, 300);
    }

    @Override
    protected BooleanBinding enableTabBinding() {
        return SelectionController.INSTANCE.selectedKeyHelperProperty().isNotNull();
    }
}