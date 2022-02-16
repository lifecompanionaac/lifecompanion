package org.lifecompanion.util.javafx;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.binding.Unbindable;

public abstract class AbstractAlertBuilder<T> {
    private final Window owner;
    private Node content;
    private String headerText, contentText;
    private Runnable onHidden;
    private Double width, height;

    protected AbstractAlertBuilder(Window owner) {
        this.owner = owner;
    }

    public T withHeaderText(String headerText) {
        this.headerText = headerText;
        return getThis();
    }

    public T withContentText(String contentText) {
        this.contentText = contentText;
        return getThis();
    }


    public T withOnHidden(Runnable onHidden) {
        this.onHidden = onHidden;
        return getThis();
    }

    public T withSize(double width, double height) {
        this.width = width;
        this.height = height;
        return getThis();
    }

    public T withContent(Node content) {
        this.content = content;
        return getThis();
    }

    protected abstract T getThis();

    private static void applyDialogConfiguration(AbstractAlertBuilder alertBuilder, Dialog<?> dlg) {
        Stage ownerWindow = (Stage) dlg.getDialogPane().getScene().getWindow();
        ownerWindow.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        dlg.setTitle(LCConstant.NAME);
        dlg.initOwner(alertBuilder.owner);
        dlg.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        final Unbindable unbindable = SystemVirtualKeyboardController.INSTANCE.registerSceneFromDialog(dlg);
        dlg.setOnHidden(e -> {
            if (alertBuilder.onHidden != null) alertBuilder.onHidden.run();
            if (unbindable != null) unbindable.unbind();
        });
        if (alertBuilder.headerText != null) dlg.setHeaderText(alertBuilder.headerText);
        if (alertBuilder.contentText != null) dlg.setContentText(alertBuilder.contentText);
        if (alertBuilder.content != null) dlg.getDialogPane().setContent(alertBuilder.content);
        if(alertBuilder.width!=null)dlg.setWidth(alertBuilder.width);
        if(alertBuilder.height!=null)dlg.setHeight(alertBuilder.height);
    }

    public static class TextInputDialogBuilder extends AbstractAlertBuilder<TextInputDialogBuilder> {

        private String defaultValue;

        protected TextInputDialogBuilder(Window owner) {
            super(owner);
        }

        @Override
        protected TextInputDialogBuilder getThis() {
            return this;
        }

        public TextInputDialogBuilder withDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public TextInputDialog build() {
            TextInputDialog dlg = new TextInputDialog(defaultValue);
            applyDialogConfiguration(this, dlg);
            return dlg;
        }

        public String showAndWait() {
            return build().showAndWait().orElse(null);
        }
    }

    public static class AlertBuilder extends AbstractAlertBuilder<AlertBuilder> {
        private final Alert.AlertType type;
        private ButtonType[] buttonTypes;

        public AlertBuilder(Window owner, Alert.AlertType type) {
            super(owner);
            this.type = type;
        }

        public AlertBuilder withButtonTypes(ButtonType... buttonTypes) {
            this.buttonTypes = buttonTypes;
            return this;
        }

        /**
         * @return the configured dialog. Prefer using {@link #show()} or {@link #showAndWait()} methods if you don't need the dialog directly.
         */
        public Alert build() {
            Alert dlg = new Alert(type);
            applyDialogConfiguration(this, dlg);
            if (buttonTypes != null) dlg.getButtonTypes().setAll(buttonTypes);
            return dlg;
        }

        public ButtonType showAndWait() {
            return build().showAndWait().orElse(null);
        }

        public void show() {
            build().show();
        }

        @Override
        protected AlertBuilder getThis() {
            return this;
        }
    }
}
