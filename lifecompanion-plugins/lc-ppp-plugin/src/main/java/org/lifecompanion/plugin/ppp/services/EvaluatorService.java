package org.lifecompanion.plugin.ppp.services;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.ppp.model.Evaluator;
import org.lifecompanion.plugin.ppp.model.EvaluatorType;

import java.util.function.Consumer;

public enum EvaluatorService implements ModeListenerI {
    INSTANCE;

    private Consumer<Evaluator> internalEvaluatorCallback;

    private Evaluator prevEvaluator;

    @Override
    public void modeStart(LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.internalEvaluatorCallback = null;
        this.prevEvaluator = null;
    }

    public void startEvaluatorSelection(boolean keepPrevEvaluator, Consumer<Evaluator> evaluatorCallback) {
        this.internalEvaluatorCallback = evaluatorCallback;

        if (keepPrevEvaluator && this.prevEvaluator != null) {
            this.finishEvaluatorSelection(this.prevEvaluator);
        } else {
            NavigationService.INSTANCE.moveToEvaluatorGrid();
        }
    }

    public void selectEvaluatorType(EvaluatorType evaluatorType) {
        if (evaluatorType == null) {
            return;
        }

        this.requestNameInput(evaluatorType);
    }

    private void requestNameInput(EvaluatorType evaluatorType) {
        KeyboardInputService.INSTANCE.startInput(
                (name) -> {
                    Evaluator evaluator = new Evaluator(evaluatorType, name);
                    if (evaluatorType != EvaluatorType.PROFESSIONAL) {
                        this.finishEvaluatorSelection(evaluator);

                        return;
                    }

                    this.requestLocalizationInput(evaluator);
                },
                Translation.getText("ppp.plugin.variables.current_keyboard_input.value.evaluators.name"),
                NavigationService.INSTANCE::moveToEvaluatorGrid);
    }

    private void requestLocalizationInput(Evaluator evaluator) {
        KeyboardInputService.INSTANCE.startInput((localization) -> {
            evaluator.setLocalization(localization);

            this.finishEvaluatorSelection(evaluator);
        }, Translation.getText("ppp.plugin.variables.current_keyboard_input.value.evaluators.localization"), () -> {
            this.requestNameInput(evaluator.getType());
        });
    }

    private void finishEvaluatorSelection(Evaluator evaluator) {
        this.prevEvaluator = evaluator;

        if (this.internalEvaluatorCallback != null) {
            this.internalEvaluatorCallback.accept(evaluator);
            this.internalEvaluatorCallback = null;
        }
    }
}
