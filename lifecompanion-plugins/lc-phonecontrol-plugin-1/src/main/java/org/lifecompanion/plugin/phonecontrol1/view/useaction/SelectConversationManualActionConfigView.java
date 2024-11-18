package org.lifecompanion.plugin.phonecontrol1.view.useaction;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.phonecontrol1.action.SelectConversationManualAction;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public class SelectConversationManualActionConfigView extends VBox
        implements UseActionConfigurationViewI<SelectConversationManualAction> {

    private TextField fieldPhoneNumber, fieldContactName;

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));
        this.fieldPhoneNumber = new TextField();
        this.fieldPhoneNumber.setPromptText("+33612345678");
        this.fieldContactName = new TextField();
        this.fieldContactName.setPromptText("John Doe");
        this.getChildren().addAll(new Label(Translation.getText("phonecontrol1.view.useaction.phonenumber")),
                this.fieldPhoneNumber,
                new Label(Translation.getText("phonecontrol1.view.useaction.contactname")), this.fieldContactName);
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final SelectConversationManualAction action,
            final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.fieldPhoneNumber.setText(action.phoneNumberProperty().get());
        this.fieldContactName.setText(action.contactNameProperty().get());
    }

    @Override
    public void editEnds(final SelectConversationManualAction action) {
        action.phoneNumberProperty().set(fieldPhoneNumber.getText());
        action.contactNameProperty().set(fieldContactName.getText());
    }

    @Override
    public Class<SelectConversationManualAction> getConfiguredActionType() {
        return SelectConversationManualAction.class;
    }
}
