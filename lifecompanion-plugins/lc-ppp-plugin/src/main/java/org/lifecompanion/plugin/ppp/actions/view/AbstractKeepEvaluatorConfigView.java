package org.lifecompanion.plugin.ppp.actions.view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.ppp.actions.KeepEvaluatorActionI;

public abstract class AbstractKeepEvaluatorConfigView<T extends KeepEvaluatorActionI> extends VBox implements UseActionConfigurationViewI<T> {
    private ToggleSwitch keepPrevEvaluatorField;

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));

        this.keepPrevEvaluatorField = new ToggleSwitch(
                Translation.getText("ppp.plugin.actions.abstract.keep_evaluator.fields.take_prev_evaluator.label"));
        this.keepPrevEvaluatorField.setMaxWidth(Double.MAX_VALUE);

        this.getChildren().addAll(this.keepPrevEvaluatorField);
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final T action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.keepPrevEvaluatorField.setSelected(action.takePrevEvaluatorProperty().getValue());
    }

    @Override
    public void editEnds(final T action) {
        action.takePrevEvaluatorProperty().set(this.keepPrevEvaluatorField.isSelected());
    }
}
