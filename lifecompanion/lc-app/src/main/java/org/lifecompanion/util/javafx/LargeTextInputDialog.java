package org.lifecompanion.util.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.binding.Unbindable;

public class LargeTextInputDialog extends Dialog<String> implements LCViewInitHelper {
    private final String content, fieldName;
    private TextField textField;

    public LargeTextInputDialog(String content, String fieldName) {
        this.content = content;
        this.fieldName = fieldName;
        initAll();
    }


    @Override
    public void initUI() {
        this.setTitle(LCConstant.NAME);
        this.initStyle(StageStyle.UTILITY);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10.0);
        gridPane.setVgap(10.0);
        gridPane.setAlignment(Pos.CENTER);
        int rowIndex = 0;

        Label labelContent = new Label(content);
        labelContent.getStyleClass().addAll("text-fill-gray", "text-wrap-enabled");
        labelContent.setPrefWidth(400);
        gridPane.add(labelContent, 0, rowIndex++, 2, 1);
        gridPane.add(new Label(fieldName), 0, rowIndex);
        gridPane.add(textField = new TextField(), 1, rowIndex++);

        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        this.getDialogPane().setContent(gridPane);
        this.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? textField.getText() : null);

        Unbindable unbindable = SystemVirtualKeyboardController.INSTANCE.registerSceneFromDialog(this);
        this.setOnHidden(e -> {
            if (unbindable != null) unbindable.unbind();
        });
    }
}
