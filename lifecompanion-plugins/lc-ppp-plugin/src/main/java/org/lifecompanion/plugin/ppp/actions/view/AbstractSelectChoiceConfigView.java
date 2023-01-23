package org.lifecompanion.plugin.ppp.actions.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.ppp.actions.SelectChoiceActionI;
import org.lifecompanion.plugin.ppp.model.Choice;
import org.lifecompanion.plugin.ppp.view.commons.FormatterListCell;

import java.util.List;

public abstract class AbstractSelectChoiceConfigView<T extends SelectChoiceActionI> extends VBox implements UseActionConfigurationViewI<T> {
    private ComboBox<Choice> choiceField;

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));

        Label choiceLabel = new Label(
                Translation.getText("ppp.plugin.actions.abstract.select_choice.fields.choice.label"));

        this.choiceField = new ComboBox<>(FXCollections.observableArrayList(this.availableChoices()));
        this.choiceField.setButtonCell(new FormatterListCell<>(Choice::getText));
        this.choiceField.setCellFactory((lv) -> new FormatterListCell<>(Choice::getText));

        this.getChildren().addAll(choiceLabel, this.choiceField);
    }

    protected abstract List<Choice> availableChoices();

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final T action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.choiceField.getSelectionModel().select(action.choiceProperty().get());
    }

    @Override
    public void editEnds(final T action) {
        action.choiceProperty().set(this.choiceField.getValue());
    }
}
