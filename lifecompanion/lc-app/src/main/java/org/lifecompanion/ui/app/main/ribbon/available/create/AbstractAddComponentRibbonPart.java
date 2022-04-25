package org.lifecompanion.ui.app.main.ribbon.available.create;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.ui.editmode.AddComponentCategoryEnum;
import org.lifecompanion.model.api.ui.editmode.AddComponentI;
import org.lifecompanion.model.impl.ui.editmode.AddComponentProvider;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.util.javafx.FXControlUtils;

public abstract class AbstractAddComponentRibbonPart extends RibbonBasePart<Void> implements LCViewInitHelper {
    private final AddComponentCategoryEnum category;
    private final double wrapWidth;

    protected AbstractAddComponentRibbonPart(AddComponentCategoryEnum category, double wrapWidth) {
        this.wrapWidth = wrapWidth;
        this.category = category;
        this.initAll();
    }

    @Override
    public void initUI() {
        FlowPane content = new FlowPane();
        content.setAlignment(Pos.CENTER);
        content.setPrefWrapLength(wrapWidth);

        ObservableList<AddComponentI> addComponents = AddComponentProvider.INSTANCE.getAvailable().get(category);
        for (AddComponentI addComponent : addComponents) {
            content.getChildren().addAll(createButton(addComponent));
        }

        this.setTitle(Translation.getText(category.getTitle()));
        this.setContent(content);
    }

    @Override
    public void initListener() {
    }

    private Button createButton(AddComponentI addComponent) {
        BorderPane pane = new BorderPane(new ImageView(IconHelper.get(addComponent.getIconPath(), 32, 32, true, true)));
        pane.setMinHeight(32);
        pane.setMinWidth(32);
        Button button = FXControlUtils.createRightTextButton(Translation.getText(addComponent.getNameID()), pane, null);
        button.setContentDisplay(ContentDisplay.TOP);
        //        label.setContentDisplay(ContentDisplay.TOP);
        //        label.setTextAlignment(TextAlignment.CENTER);
        //        label.setAlignment(Pos.CENTER);
        //        label.setGraphic(new ImageView(img));
        //        pane.getChildren().add(label);
        //        label.setTooltip(FXControlUtils.createTooltip(Translation.getText(comp.getDescriptionID())));
        button.getStyleClass().addAll("opacity-80-hover");
        button.setOnAction(e -> ConfigActionController.INSTANCE.executeAction(addComponent.createAddAction()));
        return button;
    }

    @Override
    public void initBinding() {
        this.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty().isNull().or(enableTabBinding().not()));
    }

    protected abstract BooleanBinding enableTabBinding();

    @Override
    public void bind(final Void modelP) {
    }

    @Override
    public void unbind(final Void modelP) {
    }
}
