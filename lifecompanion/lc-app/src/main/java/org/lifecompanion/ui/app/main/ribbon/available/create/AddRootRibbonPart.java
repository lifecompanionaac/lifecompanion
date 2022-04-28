package org.lifecompanion.ui.app.main.ribbon.available.create;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.ui.editmode.AddComponentCategoryEnum;

public class AddRootRibbonPart extends AbstractAddComponentRibbonPart implements LCViewInitHelper {

    public AddRootRibbonPart() {
        super(AddComponentCategoryEnum.ROOT, 200);
    }

}
