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

import javafx.scene.Node;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.model.api.categorizedelement.useaction.ActionExecutionResultI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.selectionmode.AutoDirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.DirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeParameterI;
import org.lifecompanion.ui.selectionmode.AbstractSelectionModeView;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Selection mode with a selection done with a time that cursor stay over the key
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractAutoActivationSelectionMode<T extends AbstractSelectionModeView<?>> extends AbstractDirectSelectionMode<T>
        implements DirectSelectionModeI, DrawSelectionModeI, AutoDirectSelectionModeI {

    /**
     * The scheduled task that will activate the key, or execute its over actions.
     */
    private ScheduledFuture<Void> currentScheduledActivation;

    /**
     * The current activation task
     */
    private ActivationTask currentActivationTask;

    public AbstractAutoActivationSelectionMode() {
        super(false);
    }

    @Override
    public Node getSelectionView() {
        return this.view;
    }

    @Override
    public void selectionEnter(final GridPartKeyComponentI key) {
        if (!this.ignoreNextSelectionEnter) {
            this.playingProperty.set(true);
            super.selectionEnter(key);
            if (shouldExecuteAutoActivation(parameters)) {
                currentActivationTask = new ActivationTask(key);
                this.currentScheduledActivation = this.scheduledExecutor.schedule(currentActivationTask,
                        this.parameters.autoActivationTimeProperty().get(), TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void selectionExit(final GridPartKeyComponentI key) {
        super.selectionExit(key);
        if (this.currentScheduledActivation != null && !this.currentScheduledActivation.isDone()) {
            this.currentScheduledActivation.cancel(false);
            this.currentScheduledActivation = null;
            this.currentActivationTask = null;
        }
    }

    protected abstract boolean shouldExecuteAutoActivation(SelectionModeParameterI parameters);

    @Override
    public void dispose() {
        super.dispose();
        this.playingProperty.set(false);
    }

    @Override
    public void init(SelectionModeI previousSelectionMode) {
        super.init(previousSelectionMode);
        this.playingProperty.set(true);
    }

    @Override
    protected void validSelectionPressStarted() {
        super.validSelectionPressStarted();
        if (this.currentActivationTask != null) {
            this.currentActivationTask.ignoreTask = true;
            this.playingProperty.set(false);
        }
    }

    @Override
    protected void handleActivationResult(ActionExecutionResultI result) {
        ignoreNextSelectionEnter = result.isMovingActionExecuted();
    }

    // Class part : "Task subclass"
    //========================================================================
    private class ActivationTask extends AbstractSafeScheduledTask {
        private boolean ignoreTask;

        ActivationTask(final GridPartKeyComponentI key) {
            super(key);
        }

        @Override
        public Void call() throws Exception {
            if (!this.ignoreTask) {
                AbstractAutoActivationSelectionMode.this.strokeColor
                        .set(AbstractAutoActivationSelectionMode.this.parameters.selectionActivationViewColorProperty().get());
                //With timer, there is no delay
                UseActionController.INSTANCE.startEventOn(this.key, UseActionEvent.ACTIVATION, null);
                UseActionController.INSTANCE.endEventOn(this.key, UseActionEvent.ACTIVATION, null);
                //Execute simple
                UseActionController.INSTANCE.executeSimpleOn(this.key, UseActionEvent.ACTIVATION, null,
                        AbstractAutoActivationSelectionMode.this::handleActivationResult);
            }
            return null;
        }
    }
    //========================================================================
}
