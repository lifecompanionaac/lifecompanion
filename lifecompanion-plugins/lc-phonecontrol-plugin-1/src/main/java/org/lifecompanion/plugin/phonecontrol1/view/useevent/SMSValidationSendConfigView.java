package org.lifecompanion.plugin.phonecontrol1.view.useevent;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.plugin.phonecontrol1.event.SMSValidationSendEventGenerator;

public class SMSValidationSendConfigView extends VBox
        implements UseEventGeneratorConfigurationViewI<SMSValidationSendEventGenerator> {

    private ChoiceBox<SMSValidationSendEventGenerator.ValidationSendCondition> choiceGenerateCondition;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<SMSValidationSendEventGenerator> getConfiguredActionType() {
        return SMSValidationSendEventGenerator.class;
    }

    @Override
    public void initUI() {
        this.choiceGenerateCondition = new ChoiceBox<>(FXCollections.observableArrayList(SMSValidationSendEventGenerator.ValidationSendCondition.values()));
        this.choiceGenerateCondition.setConverter(new StringConverter<SMSValidationSendEventGenerator.ValidationSendCondition>() {
            @Override
            public String toString(SMSValidationSendEventGenerator.ValidationSendCondition value) {
                return value != null ? value.getText() : null;
            }

            @Override
            public SMSValidationSendEventGenerator.ValidationSendCondition fromString(String value) {
                for (SMSValidationSendEventGenerator.ValidationSendCondition cond : SMSValidationSendEventGenerator.ValidationSendCondition.values()) {
                    if (cond.getText().equals(value)) {
                        return cond;
                    }
                }
                return null;
            }
        });
        this.getChildren().addAll(new Label(Translation.getText("phonecontrol1.plugin.unread.count.condition.label")), this.choiceGenerateCondition);
    }

    @Override
    public void editEnds(final SMSValidationSendEventGenerator element) {
        element.conditionProperty().set(choiceGenerateCondition.getValue());
    }

    @Override
    public void editStarts(final SMSValidationSendEventGenerator element) {
        choiceGenerateCondition.setValue(element.conditionProperty().get());
    }
}
