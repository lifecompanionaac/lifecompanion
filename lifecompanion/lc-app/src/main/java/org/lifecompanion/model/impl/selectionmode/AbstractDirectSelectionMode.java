/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lifecompanion.model.impl.selectionmode;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.ActionExecutionResultI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.selectionmode.DirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.FireActionEvent;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeParameterI;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.ui.selectionmode.AbstractSelectionModeView;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class AbstractDirectSelectionMode<T extends AbstractSelectionModeView<?>> extends AbstractSelectionMode<T>
        implements DrawSelectionModeI, DirectSelectionModeI {
    /**
     * Executor to add over timers
     */
    protected ScheduledExecutorService scheduledExecutor;

    /**
     * The scheduled task that will execute its over actions.
     */
    private ScheduledFuture<Void> currentScheduledOver;

    /**
     * Boolean to ignore the next selection enter in a key : this is useful to avoid directly starting over selection again, especially when the user move from a component to another.
     */
    protected boolean ignoreNextSelectionEnter;

    protected ObjectProperty<GridPartKeyComponentI> currentKey;
    protected Supplier<Boolean> nextSelectionListener;

    private final boolean directSelection;

    protected AbstractDirectSelectionMode(boolean directSelection) {
        this.currentKey = new SimpleObjectProperty<>();
        this.directSelection = directSelection;
    }

    // Class part : "Default behavior for enter/exit : show view"
    //========================================================================
    @Override
    public void selectionEnter(final GridPartKeyComponentI key) {
        //Bug #31 : doesn't show on empty key
        if (!this.isPartEmpty(key)) {
            this.currentKey.set(key);
            this.view.moveToPart(key, this.parameters.autoActivationTimeProperty().get(), false);
            if (!this.ignoreNextSelectionEnter) {
                this.currentScheduledOver = this.scheduledExecutor.schedule(new StartOverTask(key), this.parameters.autoOverTimeProperty().get(),
                        TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void selectionExit(final GridPartKeyComponentI key) {
        if (this.currentKey.get() == key) {
            this.setDefaultColors();
            this.currentKey.set(null);
            this.view.moveNullPart();
        }
        if (this.currentScheduledOver != null) {
            if (!this.currentScheduledOver.isDone()) {
                this.currentScheduledOver.cancel(false);
                this.currentScheduledOver = null;
            } else {
                UseActionController.INSTANCE.endEventOn(key, UseActionEvent.OVER, null);
            }
            //Fire the current next listener if present
            this.executeNextSelectionListener();
        }
    }

    @Override
    public void selectionMovedOver(GridPartKeyComponentI key) {
        if (this.ignoreNextSelectionEnter) {
            this.ignoreNextSelectionEnter = false;
            this.selectionEnter(key);
        }
    }
    //========================================================================

    // Class part : "Default behavior for selection : activation if enabled"
    //========================================================================
    @Override
    public void selectionPress(final GridPartKeyComponentI keyP) {
        if (this.directSelection || this.parameters.enableActivationWithSelectionProperty().get()) {
            this.selectionStarted();
            this.strokeColor.set(this.parameters.selectionActivationViewColorProperty().get());
            this.validSelectionPressStarted();
            if (this.parameters.fireActivationEventProperty().get() == FireActionEvent.ON_PRESS && this.isTimeBeforeRepeatCorrect()) {
                this.activationDone();
                if (!executeNextSelectionListener()) {
                    UseActionController.INSTANCE.executeSimpleOn(keyP, UseActionEvent.ACTIVATION, null, this::handleActivationResult);
                }
            }
            UseActionController.INSTANCE.startEventOn(keyP, UseActionEvent.ACTIVATION, null);
        }
    }

    @Override
    public void selectionRelease(final GridPartKeyComponentI keyP, final boolean skipAction) {
        if (this.directSelection || this.parameters.enableActivationWithSelectionProperty().get()) {
            this.setDefaultColors();
            if (!skipAction) {
                if (this.parameters.fireActivationEventProperty().get() == FireActionEvent.ON_RELEASE && this.isTimeToActivationCorrect()
                        && this.isTimeBeforeRepeatCorrect()) {
                    this.activationDone();
                    if (!executeNextSelectionListener()) {
                        UseActionController.INSTANCE.executeSimpleOn(keyP, UseActionEvent.ACTIVATION, null, this::handleActivationResult);
                    }
                }
            }
            //End on action should never be skipped because action are always started (selectionPress(...) always starts complex activation)
            UseActionController.INSTANCE.endEventOn(keyP, UseActionEvent.ACTIVATION, null);
        }
    }

    private boolean executeNextSelectionListener() {
        Supplier<Boolean> savedListener = this.nextSelectionListener;
        if (savedListener != null) {
            this.nextSelectionListener = null;
            savedListener.get();
            return true;
        } else return false;
    }

    /**
     * Called when a valid selection press is started on this key.<br>
     * This is usefull for subclass if they need to know if the activation will be done by selection.
     */
    protected void validSelectionPressStarted() {
    }

    /**
     * Called when actions are executed and ended.<br>
     * Subclass can use it to implement specific behavior.
     *
     * @param result action result (not null)
     */
    protected void handleActivationResult(ActionExecutionResultI result) {
    }
    //========================================================================

    @Override
    public void setNextSelectionListener(final Supplier<Boolean> nextSelectionListener) {
        this.nextSelectionListener = nextSelectionListener;
    }

    @Override
    public void dispose() {
        this.view.dispose();
        this.scheduledExecutor.shutdownNow();
    }

    @Override
    public void init(LCConfigurationI configuration, SelectionModeI previousSelectionMode) {
        super.init(configuration, previousSelectionMode);
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void parameterChanged(final SelectionModeParameterI parameters) {
    }

    // Class part : "Use progress mode"
    //========================================================================
    @Override
    public BooleanBinding currentPartNotNullProperty() {
        return this.currentKey.isNotNull();
    }
    //========================================================================

    // Class part : "Activation delay"
    //========================================================================
    protected static abstract class AbstractSafeScheduledTask implements Callable<Void> {
        protected GridPartKeyComponentI key;

        protected AbstractSafeScheduledTask(final GridPartKeyComponentI key) {
            this.key = key;
        }
    }

    private static class StartOverTask extends AbstractSafeScheduledTask {
        StartOverTask(final GridPartKeyComponentI key) {
            super(key);
        }

        @Override
        public Void call() throws Exception {
            //Execute and just start over event after timer, but event will ends when mouse leave the key
            UseActionController.INSTANCE.executeSimpleOn(this.key, UseActionEvent.OVER, null, null);
            UseActionController.INSTANCE.startEventOn(this.key, UseActionEvent.OVER, null);
            return null;
        }
    }
    //========================================================================

}
