package org.lifecompanion.plugin.phonecontrol.keyoption;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.binding.Bindings;
import javafx.scene.text.TextAlignment;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;

import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;
import org.lifecompanion.plugin.phonecontrol.model.SMSListContent;

public class SMSListKeyOption extends AbstractKeyOption {
    private final ObjectProperty<SMSListContent> sms;
    private final ObjectProperty<Node> dateNode;

    public SMSListKeyOption() {
        super();
        this.optionNameId = "phonecontrol.plugin.key.option.smslist.name";
        this.optionDescriptionId = "phonecontrol.plugin.key.option.smslist.description";
        this.disableTextContent.set(true);
        this.disableImage.set(true);
        this.sms = new SimpleObjectProperty<>();
        this.dateNode = new SimpleObjectProperty<>();
        this.considerKeyEmpty.set(false);
        this.initSMSBinding();
        this.initNode();
    }

    public ObjectProperty<SMSListContent> smsProperty() {
        return this.sms;
    }

    @Override
    public String getIconUrl() {
        return "phonecontrol.png";
    }

    @Override
    protected void attachToImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().set(AppModeController.INSTANCE.isUseMode() ? null : Translation.getText("phonecontrol.plugin.key.option.smslist.default.text"));
        // No action to attach
    }

    @Override
    protected void detachFromImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().set(null);
        // No action to detach
    }

    private void initSMSBinding() {
        this.sms.addListener((obs, ov, nv) -> {
            final GridPartKeyComponentI key = this.attachedKey.get();

            if (nv != null) {
                if (nv == ConnexionController.SMS_LOADING) {
                    key.textContentProperty().set(Translation.getText("phonecontrol.plugin.key.option.list.loading"));
                    // Clear date node
                    dateNode.set(null);
                } else if (nv == ConnexionController.SMS_NOT_CONNECTED) {
                    key.textContentProperty().set(Translation.getText("phonecontrol.plugin.key.option.list.notconnected"));
                } else if (nv == ConnexionController.SMS_END_MESSAGE) {
                    key.textContentProperty().set(Translation.getText("phonecontrol.plugin.key.option.smslist.empty"));
                } else {
                    // Set text content
                    key.textContentProperty().set(nv.getSMS());

                    // Align text to the right if the SMS is sent by me
                    if (nv.isSendByMe()) {
                        key.getKeyTextStyle().textAlignmentProperty().forced().setValue(TextAlignment.RIGHT);
                    } else {
                        key.getKeyTextStyle().textAlignmentProperty().forced().setValue(TextAlignment.LEFT);
                    }

                    // Set date node
                    Text dateText = new Text(nv.getSentDate());
                    final TextCompStyleI keyTextStyle = attachedKey.get().getKeyTextStyle();
                    final Font font = keyTextStyle.fontProperty().get();
                    double currentSize = font.getSize();
                    double newSize = Math.max(currentSize - 2, 1);
                    Font adjustedFont = Font.font(font.getFamily(), newSize);
                    dateText.setFont(adjustedFont);
                    dateText.setFill(keyTextStyle.colorProperty().value().getValue());
                    dateNode.set(dateText);
                }
            } else {
                key.textContentProperty().set(null);
                key.getKeyTextStyle().boldProperty().forced().setValue(null);
            }
        });
    }

    private void initNode() {
        keyViewAddedNodeProperty().bind(Bindings.createObjectBinding(() -> {
            if (dateNode.get() != null) {
                StackPane detailNode = new StackPane();

                if (dateNode.get() != null) {
                    StackPane.setAlignment(dateNode.get(), Pos.TOP_LEFT);
                    detailNode.getChildren().add(dateNode.get());
                }

                return detailNode;
            }

            return null;
        }, dateNode));
    }
}
