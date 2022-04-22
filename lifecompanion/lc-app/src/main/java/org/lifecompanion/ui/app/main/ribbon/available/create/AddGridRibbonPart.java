package org.lifecompanion.ui.app.main.ribbon.available.create;

import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;

public class AddGridRibbonPart extends RibbonBasePart<Void> implements LCViewInitHelper {

    public AddGridRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        FlowPane content = new FlowPane();
        content.setAlignment(Pos.CENTER);
        content.setPrefWrapLength(250);

        content.getChildren().addAll(
                AddRibbonHelper.createButton("add.component.grid.in.stack", "component.stack.add.description", "component/icon_add_grid_in_stack.png"),
                AddRibbonHelper.createButton("add.component.grid.in.copy", "component.texteditor.add.description", "component/icon_add_grid_in_stack.png"),
                AddRibbonHelper.createButton("add.user.model.name", "component.texteditor.add.description", "component/add_user_model.png")
                );

        this.setTitle(Translation.getText("ribbon.part.create.grid"));
        this.setContent(content);
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initBinding() {
        this.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty().isNull());
    }

    @Override
    public void bind(final Void modelP) {
    }

    @Override
    public void unbind(final Void modelP) {
    }
}
