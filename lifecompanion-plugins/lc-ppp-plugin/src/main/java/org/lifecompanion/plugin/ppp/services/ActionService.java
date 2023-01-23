package org.lifecompanion.plugin.ppp.services;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.ppp.keyoption.ActionCellKeyOption;
import org.lifecompanion.plugin.ppp.model.Action;
import org.lifecompanion.plugin.ppp.model.ActionRecord;
import org.lifecompanion.plugin.ppp.model.Evaluator;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.*;
import java.util.function.Consumer;

public enum ActionService implements ModeListenerI {
    INSTANCE;

    private LCConfigurationI config;
    private final ObservableList<Action> actions;

    private int keyOptionsSize;
    private final List<ActionCellKeyOption> keyOptions;
    private final SimpleIntegerProperty currentPage;

    private Evaluator currentEvaluator;

    private final Set<Consumer<ActionRecord>> actionStopCallbacks;
    private Consumer<ActionRecord> internalActionStopCallback;

    private final ListChangeListener<Action> actionsListener;
    private final ChangeListener<Number> currentPageListener;

    ActionService() {
        this.actions = FXCollections.observableArrayList();

        this.keyOptions = FXCollections.observableArrayList();
        this.currentPage = new SimpleIntegerProperty(0);

        this.actionStopCallbacks = new HashSet<>();

        this.actionsListener = change -> {
            this.updateDisplayedKeyOptions();

            UserProfile profile = ProfileService.INSTANCE.getCurrentProfile();
            profile.setActions(new ArrayList<>(this.actions));

            ProfileService.INSTANCE.saveProfile(this.config, profile);
        };
        this.currentPageListener = (obs, ov, nv) -> this.updateDisplayedKeyOptions();
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.config = configuration;

        Map<GridComponentI, List<ActionCellKeyOption>> keys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(ActionCellKeyOption.class, configuration, keys, null);
        keys.values().stream().flatMap(List::stream).forEach(this.keyOptions::add);

        this.keyOptionsSize = this.keyOptions.size();

        if (this.keyOptionsSize != 0) {
            this.updateDisplayedKeyOptions();

            this.actions.addListener(this.actionsListener);
            this.currentPage.addListener(this.currentPageListener);
        }

        this.actions.setAll(ProfileService.INSTANCE.getCurrentProfile().getActions());
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.config = null;
        this.internalActionStopCallback = null;

        this.actions.removeListener(this.actionsListener);
        this.currentPage.removeListener(this.currentPageListener);

        this.keyOptions.clear();
    }

    public ObservableList<Action> getActions() {
        return this.actions;
    }

    public void startAction(boolean keepPrevEvaluator, Consumer<ActionRecord> stopCallback) {
        this.internalActionStopCallback = stopCallback;

        EvaluatorService.INSTANCE.startEvaluatorSelection(keepPrevEvaluator, this::initActionGrid);
    }

    private void initActionGrid(Evaluator evaluator) {
        this.currentEvaluator = evaluator;

        this.currentPage.set(0);

        this.moveToActionGrid();
    }

    private void moveToActionGrid() {
        NavigationService.INSTANCE.moveToActionGrid();
    }

    public void stopAction(ActionRecord action) {
        if (action == null) {
            return;
        }

        KeyboardInputService.INSTANCE.startInput(comment -> {
            action.setComment(comment);

            RecordsService.INSTANCE.save(this.config, action);

            Consumer<ActionRecord> stopCallback = this.internalActionStopCallback;
            if (stopCallback != null) {
                this.internalActionStopCallback = null;
                stopCallback.accept(action);
            }

            this.actionStopCallbacks.forEach(l -> l.accept(action));
        }, Translation.getText("ppp.plugin.variables.current_keyboard_input.value.actions.comment"), this::moveToActionGrid);
    }

    public void selectAction(Action action) {
        if (this.currentEvaluator == null || action == null) {
            return;
        }

        this.stopAction(new ActionRecord(this.currentEvaluator, action));
    }

    public void createAction() {
        KeyboardInputService.INSTANCE.startInput((actionName) -> {
            if (actionName != null) {
                Action action = new Action(actionName);
                this.actions.add(action);
                this.selectAction(action);
            } else {
                this.moveToActionGrid();
            }
        }, Translation.getText("ppp.plugin.variables.current_keyboard_input.value.actions.create"), this::moveToActionGrid);
    }

    public void previousActionsPage() {
        int maxPage = this.getMaxPage();

        this.currentPage.set((this.currentPage.get() > 0 ? this.currentPage.get() : maxPage) - 1);
    }

    public void nextActionsPage() {
        int maxPage = this.getMaxPage();
        int futurePage = this.currentPage.get() + 1;

        this.currentPage.set(futurePage < maxPage ? futurePage : 0);
    }

    public void addActionEndListener(Consumer<ActionRecord> callback) {
        this.actionStopCallbacks.add(callback);
    }

    public void removeActionEndListener(Consumer<ActionRecord> callback) {
        this.actionStopCallbacks.remove(callback);
    }

    private int getMaxPage() {
        if (this.keyOptionsSize == 0 || this.actions.isEmpty()) {
            return 1;
        }

        return (int) Math.ceil((1.0 * this.actions.size()) / (1.0 * this.keyOptionsSize));
    }

    private void updateDisplayedKeyOptions() {
        List<Action> actions = this.actions;

        for (int cellIndex = 0; cellIndex < this.keyOptionsSize; cellIndex++) {
            int actionIndex = this.currentPage.get() * this.keyOptionsSize + cellIndex;
            ActionCellKeyOption actionCell = this.keyOptions.get(cellIndex);
            Action action = actionIndex < actions.size() ? actions.get(actionIndex) : null;

            FXThreadUtils.runOnFXThread(() -> actionCell.actionProperty().set(action));
        }
    }
}
