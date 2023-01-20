package org.lifecompanion.plugin.ppp.services;

import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.api.categorizedelement.useaction.SimpleUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.ppp.actions.*;
import org.lifecompanion.plugin.ppp.keyoption.ActionCellKeyOption;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum NavigationService implements ModeListenerI {
    INSTANCE;

    private GridPartComponentI evaluatorGridPart;
    private GridPartComponentI pppAssessmentGridPart;
    private GridPartComponentI evsAssessmentGridPart;
    private GridPartComponentI autoEvsAssessmentGridPart;
    private GridPartComponentI painLocalizationGridPart;
    private GridPartKeyComponentI actionGridPartKey;
    private GridPartComponentI keyboardGridPart;

    @Override
    public void modeStart(LCConfigurationI config) {
        this.evaluatorGridPart = this.findGridPart(config, SelectEvaluatorAction.class);
        this.pppAssessmentGridPart = this.findGridPart(config, AssessmentSelectChoicePPPAction.class);
        this.evsAssessmentGridPart = this.findGridPart(config, AssessmentSelectChoiceEvsAction.class);
        this.autoEvsAssessmentGridPart = this.findGridPart(config, AssessmentSelectChoiceAutoEvsAction.class);
        this.painLocalizationGridPart = this.findGridPart(config, AssessmentSelectPainLocalizationAction.class);
        this.actionGridPartKey = this.findGridPartKey(config, ActionCellKeyOption.class);
        this.keyboardGridPart = this.findGridPart(config, KeyboardValidateEntryAction.class);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.evaluatorGridPart = null;
        this.pppAssessmentGridPart = null;
        this.evsAssessmentGridPart = null;
        this.autoEvsAssessmentGridPart = null;
        this.painLocalizationGridPart = null;
        this.actionGridPartKey = null;
        this.keyboardGridPart = null;
    }

    private <T extends KeyOptionI> GridPartKeyComponentI findGridPartKey(LCConfigurationI config, Class<T> keyOptionClass) {
        Map<GridComponentI, List<T>> keys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(keyOptionClass, config, keys, null);

        return keys.values().stream()
                .flatMap(List::stream)
                .map(k -> k.attachedKeyProperty().get())
                .findFirst()
                .orElse(null);
    }

    private GridPartComponentI findGridPart(LCConfigurationI config, Class<? extends SimpleUseActionI<UseActionTriggerComponentI>> actionClass) {
        return config.getAllComponent().values().stream()
                .filter(c -> c instanceof UseActionTriggerComponentI)
                .map(c -> (UseActionTriggerComponentI) c)
                .filter(c -> c instanceof GridPartComponentI
                        && c.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, actionClass) != null)
                .map(c -> (GridPartComponentI) c)
                .min(ConfigurationComponentUtils.positionInGridParentIncludingParentComparator())
                .orElse(null);
    }

    public void moveToEvaluatorGrid() {
        this.moveToGridPart(this.evaluatorGridPart);
    }

    public void moveToPPPAssessmentGrid() {
        this.moveToGridPart(this.pppAssessmentGridPart);
    }

    public void moveToEvsAssessmentGrid() {
        this.moveToGridPart(this.evsAssessmentGridPart);
    }

    public void moveToAutoEvsAssessmentGrid() {
        this.moveToGridPart(this.autoEvsAssessmentGridPart);
    }

    public void moveToPainLocalizationGrid() {
        this.moveToGridPart(this.painLocalizationGridPart);
    }

    public void moveToActionGrid() {
        this.moveToGridPart(this.actionGridPartKey);
    }

    public void moveToKeyboardGrid() {
        this.moveToGridPart(this.keyboardGridPart);
    }

    private void moveToGridPart(GridPartComponentI gridPart) {
        if (gridPart != null) {
            SelectionModeController.INSTANCE.goToGridPart(gridPart);
        }
    }
}
