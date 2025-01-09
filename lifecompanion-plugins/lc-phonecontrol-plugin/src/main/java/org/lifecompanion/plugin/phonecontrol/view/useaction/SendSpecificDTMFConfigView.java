package org.lifecompanion.plugin.phonecontrol.view.useaction;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.phonecontrol.action.SendSpecificDTMF;

public class SendSpecificDTMFConfigView extends VBox implements UseActionConfigurationViewI<SendSpecificDTMF> {
    private TextField dtmf;

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));
        this.dtmf = new TextField();
        this.dtmf.setPromptText("0");
        this.getChildren().addAll(
            new Label(Translation.getText("phonecontrol.view.useaction.dtmf")),
            this.dtmf
        );
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final SendSpecificDTMF action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.dtmf.setText(action.dtmfProperty().get());
    }

    @Override
    public void editEnds(final SendSpecificDTMF action) {
        action.dtmfProperty().set(dtmf.getText());
    }

    @Override
    public Class<SendSpecificDTMF> getConfiguredActionType() {
        return SendSpecificDTMF.class;
    }
}
