package org.lifecompanion.ui.app.main.ribbon.available.create;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.ui.editmode.AddComponentCategoryEnum;

public class AddGridRibbonPart extends AbstractAddComponentRibbonPart implements LCViewInitHelper {

    public AddGridRibbonPart() {
        super(AddComponentCategoryEnum.GRID, 250);
    }

    @Override
    protected BooleanBinding enableTabBinding() {
        return Bindings.createBooleanBinding(() -> SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().get() instanceof StackComponentI, SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty());
    }
}