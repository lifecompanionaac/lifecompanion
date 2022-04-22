package org.lifecompanion.ui.app.main.ribbon.available.create;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.javafx.FXControlUtils;

public class AddRibbonHelper {
    public static Button createButton(String nameId, String descriptionId, String iconPath) {
        BorderPane pane = new BorderPane(new ImageView(IconHelper.get(iconPath, 32, 32, true, true)));
        pane.setMinHeight(32);
        pane.setMinWidth(32);
        Button button = FXControlUtils.createRightTextButton(Translation.getText(nameId), pane, null);
        button.setContentDisplay(ContentDisplay.TOP);
        //        label.setContentDisplay(ContentDisplay.TOP);
        //        label.setTextAlignment(TextAlignment.CENTER);
        //        label.setAlignment(Pos.CENTER);
        //        label.setGraphic(new ImageView(img));
        //        pane.getChildren().add(label);
        //        label.setTooltip(FXControlUtils.createTooltip(Translation.getText(comp.getDescriptionID())));
        button.getStyleClass().addAll("opacity-80-hover");
        return button;
    }
}
