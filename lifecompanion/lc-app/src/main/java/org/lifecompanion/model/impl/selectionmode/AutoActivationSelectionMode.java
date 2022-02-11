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
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.ActionExecutionResultI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.selectionmode.AutoDirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.DirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.controller.categorizedelement.useaction.UserActionController;
import org.lifecompanion.ui.selectionmode.AutoActivationSelectionModeView;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Selection mode with a selection done with a time that cursor stay over the key
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class AutoActivationSelectionMode extends AbstractDirectSelectionMode<AutoActivationSelectionModeView>
        implements DirectSelectionModeI, DrawSelectionModeI, AutoDirectSelectionModeI {

    /**
     * The scheduled task that will activate the key, or execute its over actions.
     */
    private ScheduledFuture<Void> currentScheduledActivation;

    /**
     * The current activation task
     */
    private ActivationTask currentActivationTask;

    public AutoActivationSelectionMode() {
        super(false);
        this.view = new AutoActivationSelectionModeView(this);
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
            currentActivationTask = new ActivationTask(key);
            this.currentScheduledActivation = this.scheduledExecutor.schedule(currentActivationTask,
                    this.parameters.autoActivationTimeProperty().get(), TimeUnit.MILLISECONDS);
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
                AutoActivationSelectionMode.this.strokeColor
                        .set(AutoActivationSelectionMode.this.parameters.selectionActivationViewColorProperty().get());
                //With timer, there is no delay
                UserActionController.INSTANCE.startEventOn(this.key, UseActionEvent.ACTIVATION, null);
                UserActionController.INSTANCE.endEventOn(this.key, UseActionEvent.ACTIVATION, null);
                //Execute simple
                UserActionController.INSTANCE.executeSimpleOn(this.key, UseActionEvent.ACTIVATION, null,
                        AutoActivationSelectionMode.this::handleActivationResult);
            }
            return null;
        }
    }
    //========================================================================
}
