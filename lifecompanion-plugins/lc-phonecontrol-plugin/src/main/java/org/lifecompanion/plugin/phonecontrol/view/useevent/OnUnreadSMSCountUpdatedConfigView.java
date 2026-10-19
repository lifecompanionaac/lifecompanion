package org.lifecompanion.plugin.phonecontrol.view.useevent;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.plugin.phonecontrol.event.OnUnreadSMSCountUpdatedEventGenerator;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public class OnUnreadSMSCountUpdatedConfigView extends VBox
        implements UseEventGeneratorConfigurationViewI<OnUnreadSMSCountUpdatedEventGenerator> {

    private ChoiceBox<OnUnreadSMSCountUpdatedEventGenerator.UnreadEventGenerateCondition> choiceGenerateCondition;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<OnUnreadSMSCountUpdatedEventGenerator> getConfiguredActionType() {
        return OnUnreadSMSCountUpdatedEventGenerator.class;
    }

    @Override
    public void initUI() {
        this.choiceGenerateCondition = new ChoiceBox<>(FXCollections.observableArrayList(OnUnreadSMSCountUpdatedEventGenerator.UnreadEventGenerateCondition.values()));
        this.choiceGenerateCondition.setConverter(new StringConverter<OnUnreadSMSCountUpdatedEventGenerator.UnreadEventGenerateCondition>() {
            @Override
            public String toString(OnUnreadSMSCountUpdatedEventGenerator.UnreadEventGenerateCondition value) {
                return value != null ? value.getText() : null;
            }

            @Override
            public OnUnreadSMSCountUpdatedEventGenerator.UnreadEventGenerateCondition fromString(String value) {
                for (OnUnreadSMSCountUpdatedEventGenerator.UnreadEventGenerateCondition cond : OnUnreadSMSCountUpdatedEventGenerator.UnreadEventGenerateCondition.values()) {
                    if (cond.getText().equals(value)) {
                        return cond;
                    }
                }
                return null;
            }
        });
        this.getChildren().addAll(new Label(Translation.getText("phonecontrol.plugin.unread.count.condition.label")), this.choiceGenerateCondition);
    }

    @Override
    public void editEnds(final OnUnreadSMSCountUpdatedEventGenerator element) {
        element.conditionProperty().set(choiceGenerateCondition.getValue());
    }

    @Override
    public void editStarts(final OnUnreadSMSCountUpdatedEventGenerator element) {
        choiceGenerateCondition.setValue(element.conditionProperty().get());
    }
}
