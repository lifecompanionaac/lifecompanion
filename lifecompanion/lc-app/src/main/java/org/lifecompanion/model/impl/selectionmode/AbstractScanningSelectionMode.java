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

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.ActionExecutionResultI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.ui.selectionmode.AbstractSelectionModeView;
import org.lifecompanion.model.api.selectionmode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Abstract selection mode for all selection mode that define a scanning mode.<br>
 * This is useful to determine on the same place the way to play,pause, etc... scanning mode.
 *
 * @param <T> view to display the scanning mode
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractScanningSelectionMode<T extends AbstractSelectionModeView<?>> extends AbstractSelectionMode<T>
        implements ScanningSelectionModeI, DrawSelectionModeI {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScanningSelectionMode.class);

    /**
     * Timeline to repeat the scanning
     */
    private final Timeline scanningTimeLine;

    /**
     * The keyframe currently used with {@link #scanningTimeLine}
     */
    private KeyFrame currentScanningKeyFrame;

    /**
     * Property that contains the current scanned part
     */
    protected ObjectProperty<GridPartComponentI> currentPart;

    /**
     * Boolean true if we are currently executing action (on over) on the current part (useful to skip play if action are still executing)
     */
    protected boolean executingActionOnCurrentPart = false;

    /**
     * Boolean true if we are currently paused executing action (on activation) on the current part (useful to skip play if action are still executing)</br>
     * Issue #192
     */
    protected boolean pauseToExecuteSimpleActions = false;

    /**
     * Number of times in the same part/grid (to stop or quit the scanning)
     */
    protected int timesInSamePart;

    /**
     * Indicates if the mode is disposed
     */
    protected boolean disposed = false;

    /**
     * When this boolean is true, the next selection will restart the scanning in the current part.<br>
     * This is use to pause the scanning.
     */
    private boolean restartScanningOnNextAction = false;

    /**
     * To flag that the current scanning has action on press on last {@link #selectionPress(boolean)} : this is useful to avoid playing again the scanning selection if the scanning is paused for simple action
     */
    private boolean simpleActionOnPress;

    /**
     * Supplier to know if the scanning should be restart or just played on next action.
     */
    private Supplier<Boolean> nextSelectionListener;

    /**
     * Indicate if the pause was done for mouse press
     */
    private boolean pauseForMousePress;

    protected AbstractScanningSelectionMode() {
        this.scanningTimeLine = new Timeline();
        this.scanningTimeLine.setCycleCount(Animation.INDEFINITE);
        this.currentPart = new SimpleObjectProperty<>();
        this.view = this.createView();
        //Each time the current part change, we ends the over event on it
        this.currentPart.addListener((obs, ov, nv) -> {
            SelectionModeController.INSTANCE.currentOverPartProperty().set(nv);// "Bind" the selection mode controller value
            if (ov instanceof UseActionTriggerComponentI) {
                UseActionTriggerComponentI actionTriggerComp = (UseActionTriggerComponentI) ov;
                UseActionController.INSTANCE.endEventOn(actionTriggerComp, UseActionEvent.OVER, null);
            }
        });
    }

    // Class part : "Subclass helpers"
    //========================================================================

    /**
     * @return true if the {@link #timesInSamePart} is equals or more than the authorized count in parameters
     */
    protected boolean isTimesInPartSupMax() {
        return this.timesInSamePart >= this.parameters.maxScanBeforeStopProperty().get();
    }

    @Override
    public void generateScanningPartForCurrentGrid() {
        if (this.currentGrid.get() != null) {
            this.scannedGridChanged(this.currentGrid.get());
        }
    }

    /**
     * This method will check if the scanning count is invalid, and if it is, will execute the action that should be executed at the end of scanning (go in part, stop scanning...)
     *
     * @return true if the scanning was invalid and a change action was executed
     */
    protected boolean checkRestartAndMaxScanValid() {
        if (this.isTimesInPartSupMax()) {
            AbstractScanningSelectionMode.LOGGER.debug("Scanned {} the same grid, will try to go in the parent, or just stop and restart",
                    this.timesInSamePart);
            if (this.currentGrid.get().gridParentProperty().get() != null || this.currentGrid.get().stackParentProperty().get() != null
                    && this.currentGrid.get().stackParentProperty().get() instanceof GridPartComponentI
                    && ((GridPartComponentI) this.currentGrid.get().stackParentProperty().get()).gridParentProperty().get() != null) {
                SelectionModeController.INSTANCE.goToParentPart(this.currentGrid.get());
                return false;
            } else {
                this.timesInSamePart = 0;
            }
        }
        return true;
    }

    /**
     * @param firstScan true if it's the first part of the grid
     * @return the real scanning time of the current part
     */
    protected long getProgressTime(final boolean firstScan) {
        return firstScan ? this.parameters.scanFirstPauseProperty().get() + this.parameters.scanPauseProperty().get()
                : this.parameters.scanPauseProperty().get();
    }

    protected boolean isMoveAnimationEnabled(final boolean firstScan) {
        return !firstScan;
    }
    //========================================================================

    // Class part : "Subclass impl"
    //========================================================================

    /**
     * @return the selection mode (will be called just once)
     */
    protected abstract T createView();

    /**
     * Should determine the selection behavior when there is no current part selected.<br>
     * Will be called when mouse is pressed/release (determined by parameters) and when there is no current part.
     *
     * @return if this method return true, this means that a current part part was determined by this method call, and that the
     * mouse event should be execute as if a part was present.
     */
    protected boolean fireActionNoCurrentPart() {
        return false;
    }

    /**
     * Should execute the next move.<br>
     * This is called each times determined by the scanning parameters.
     */
    protected abstract void executeNext();

    /**
     * @return true only if {@link #executeNext()} could be called.<br>
     * Returning false will restart the scanning.
     */
    protected abstract boolean isScanningNextPossible();

    /**
     * Method that will update the current component by calling the needed methods.<br>
     * Subclass should take care of updating the view.
     *
     * @param firstPart will be true if we consider that the current component change is like the first grid part (and should pause or not)
     */
    protected abstract void updateCurrentComponent(boolean firstPart);

    /**
     * Should scan and prepare the current grid component to scan it.<br>
     * If this method is called, it's that a valid grid is currently set as {@link #currentGrid}
     */
    protected abstract void generateScannedComponents();

    @Override
    protected void scannedGridChanged(final GridComponentI gridP) {
        this.timesInSamePart = 0;
        this.generateScannedComponents();
        AbstractScanningSelectionMode.LOGGER.info("Scanned grid changed and scan components generated");
    }
    //========================================================================

    // Class part : "Selection mode methods"
    //========================================================================

    @Override
    public void selectionPress(final boolean skipAction) {
        if (this.isTimeBeforeRepeatCorrect()) {
            this.selectionStarted();
            this.strokeColor.set(this.parameters.selectionActivationViewColorProperty().get());
            this.pause();
            this.pauseForMousePress = true;
            if (this.restartScanningOnNextAction && this.parameters.fireActivationEventProperty().get() == FireActionEvent.ON_PRESS) {
                this.restartOrPlay();
            }
            //Double check on current part because callFireAction can set it
            else if (!this.restartScanningOnNextAction && this.currentPart.get() != null
                    || this.callFireActionNoCurrentPart(FireActionEvent.ON_PRESS, skipAction) && this.currentPart.get() != null) {
                //Actions
                this.executeMouseEvent(FireActionEvent.ON_PRESS, skipAction, () -> this.simpleActionOnPress = true,
                        (key, event) -> UseActionController.INSTANCE.startEventOn(key, event, null));
            }
        }
    }

    @Override
    public void selectionRelease(final boolean skipAction) {
        if (this.isTimeBeforeRepeatCorrect() || this.parameters.fireActivationEventProperty().get() == FireActionEvent.ON_PRESS) {
            this.setDefaultColors();
            if (!this.simpleActionOnPress && !pauseToExecuteSimpleActions) {
                this.play();
            }
            this.pauseForMousePress = false;
            //Double check on current part because callFireAction can set it
            if (this.isTimeToActivationCorrect()) {
                if (this.restartScanningOnNextAction && this.parameters.fireActivationEventProperty().get() == FireActionEvent.ON_RELEASE) {
                    this.restartOrPlay();
                } else if (!this.restartScanningOnNextAction && this.currentPart.get() != null
                        || this.callFireActionNoCurrentPart(FireActionEvent.ON_RELEASE, skipAction) && this.currentPart.get() != null) {
                    //Actions
                    this.executeMouseEvent(FireActionEvent.ON_RELEASE, skipAction, () -> {
                        pauseToExecuteSimpleActions = true;
                        this.pause();
                    }, (key, event) -> UseActionController.INSTANCE.endEventOn(key, event, null));
                }
            } else {
                AbstractScanningSelectionMode.LOGGER.debug("Event selectionRelease was skipped because time to activation is not correct");
            }
        } else {
            AbstractScanningSelectionMode.LOGGER.debug("Event selectionRelease is skipped, time before repeat correct {}",
                    this.isTimeBeforeRepeatCorrect());
        }
    }

    @Override
    public void nextScanSelectionPress() {
        if (this.isTimeBeforeRepeatCorrect() && this.parameters.fireActivationEventProperty().get() == FireActionEvent.ON_PRESS) {
            this.updateNextMove();
        }
    }

    @Override
    public void nextScanSelectionRelease() {
        if (this.isTimeBeforeRepeatCorrect() && this.parameters.fireActivationEventProperty().get() == FireActionEvent.ON_RELEASE) {
            this.updateNextMove();
        }
    }

    private void restartOrPlay() {
        if (this.nextSelectionListener != null && !this.nextSelectionListener.get()) {
            this.restartScanningOnNextAction = false;
            this.play();
        } else {
            this.restart();
        }
        this.nextSelectionListener = null;
    }

    @Override
    public void parameterChanged(final SelectionModeParameterI parameters) {
        AbstractScanningSelectionMode.LOGGER.info("Parameters changed for mode {}", parameters);
        synchronized (this.scanningTimeLine) {
            if (this.currentScanningKeyFrame != null) {
                this.scanningTimeLine.getKeyFrames().remove(this.currentScanningKeyFrame);
            }
            if (parameters.scanningModeProperty().get() == ScanningMode.AUTO) {
                this.currentScanningKeyFrame = new KeyFrame(Duration.millis(parameters.scanPauseProperty().get()), (ae) -> {
                    this.updateNextMove();
                });
                this.scanningTimeLine.getKeyFrames().add(this.currentScanningKeyFrame);
            }
        }
    }

    @Override
    public void dispose() {
        this.view.dispose();
        this.disposed = true;
        this.stop();
    }

    @Override
    public void init(SelectionModeI previousSelectionMode) {
        super.init(previousSelectionMode);
        AbstractScanningSelectionMode.LOGGER.info("Initialize scanning selection mode {}", this.getClass().getSimpleName());
        this.disposed = false;
    }
    //========================================================================

    // Class part : "Internal methods"
    //========================================================================

    /**
     * Usefull method to call {@link #fireActionNoCurrentPart()} if the event is the one that should fire action
     *
     * @param event      the source event type
     * @param skipAction if the action fire should be skiped
     * @return true if the event was correct and the method {@link #fireActionNoCurrentPart()} returned true
     */
    private boolean callFireActionNoCurrentPart(final FireActionEvent event, final boolean skipAction) {
        if (event == this.parameters.fireActivationEventProperty().get() && !skipAction && this.isTimeBeforeRepeatCorrect()) {
            this.activationDone();
            return this.fireActionNoCurrentPart();
        } else {
            return false;
        }
    }

    private void executeMouseEvent(final FireActionEvent fireEvent, final boolean skipAction, final Runnable hasSimpleAction,
                                   final BiConsumer<GridPartKeyComponentI, UseActionEvent> complexActionHandler) {
        if (!skipAction) {
            //If the current par is a key, check actions
            if (this.currentPart.get() instanceof GridPartKeyComponentI) {
                GridPartKeyComponentI keyPart = (GridPartKeyComponentI) this.currentPart.get();
                //Fire simple actions
                if (this.parameters.fireActivationEventProperty().get() == fireEvent) {
                    if (keyPart.getActionManager().hasSimpleAction(UseActionEvent.ACTIVATION)) {
                        if (hasSimpleAction != null) {
                            hasSimpleAction.run();
                        }
                        this.activationDone();
                        //After action execution, scanning restart, on simply play if grid changed
                        UseActionController.INSTANCE.executeSimpleOn(keyPart, UseActionEvent.ACTIVATION, null, (result) -> {
                            pauseToExecuteSimpleActions = false;
                            if (!this.restartScanningOnNextAction) {
                                this.simpleActionOnPress = false;
                                if (!result.isMovingActionExecuted()) {
                                    this.restart();
                                } else {
                                    this.play();
                                }
                            }
                        });
                    }
                }
                //Fire complex actions
                if (keyPart.getActionManager().hasComplexAction(UseActionEvent.ACTIVATION)) {
                    complexActionHandler.accept(keyPart, UseActionEvent.ACTIVATION);
                }
            }
            //Go deeper in grid
            else if (this.parameters.fireActivationEventProperty().get() == fireEvent) {
                SelectionModeController.INSTANCE.goToGridPart(this.currentPart.get());
            }
        }
    }

    /**
     * This will restart the scanning time line with the initial delay
     */
    private void restartTimeLine() {
        if (!this.executingActionOnCurrentPart && !this.pauseToExecuteSimpleActions && !this.disposed) {
            synchronized (this.scanningTimeLine) {
                this.scanningTimeLine.stop();
                if ((this.parameters.startScanningOnClicProperty().get() && this.parameters.scanningModeProperty().get() != ScanningMode.MANUAL) && !this.restartScanningOnNextAction) {
                    this.restartScanningOnNextAction = true;
                    this.playingProperty.set(false);
                } else {
                    this.restartScanningOnNextAction = false;
                    this.scanningTimeLine.setDelay(Duration.millis(this.parameters.scanFirstPauseProperty().get()));
                    this.scanningTimeLine.playFrom(Duration.ZERO);
                    this.playingProperty.set(true);
                }
            }
        }
    }
    //========================================================================

    /**
     * Should be called by subclass to update the current component.<br>
     * The given component can be null.<br>
     * This method will fire all OVER event on the current part (complex and simple actions)
     *
     * @param newPart   the current scanned component (can be null)
     * @param firstScan if the current part can be considered as the first grid part (and should pause if needed)
     */
    protected void updateCurrentPart(final GridPartComponentI newPart, final boolean firstScan) {
        this.currentPart.set(newPart);
        //Execute the action after simple OVER actions executed (block play because scanning should not be playing until simple action ended)
        Runnable actionAfter = firstScan ? this::restartTimeLine : this::play;
        Consumer<ActionExecutionResultI> actionAfterRunnable = (result) -> {
            this.executingActionOnCurrentPart = false;
            //The scanner should be restarted only if the selection didn't start
            if (!this.pauseForMousePress) {
                actionAfter.run();
            }
        };
        boolean executeActionOver = false;
        //Start over event
        if (newPart != null && this.currentPart.get() instanceof UseActionTriggerComponentI) {
            UseActionTriggerComponentI keyPart = (UseActionTriggerComponentI) this.currentPart.get();
            UseActionController.INSTANCE.startEventOn(keyPart, UseActionEvent.OVER, null);
            //Execute simple action
            if (keyPart.getActionManager().hasSimpleAction(UseActionEvent.OVER)) {
                this.pause();
                //When we pause for over action, the playing property should stay true to draw progress
                this.playingProperty.set(true);
                this.executingActionOnCurrentPart = true;
                AbstractScanningSelectionMode.LOGGER.debug("Execute simple over action");
                executeActionOver = true;
                UseActionController.INSTANCE.executeSimpleOn(keyPart, UseActionEvent.OVER, null, actionAfterRunnable);
            }
        }
        if (firstScan && !executeActionOver) {
            AbstractScanningSelectionMode.LOGGER.debug("In {}, update current part {} with a first scan {}, disposed {}",
                    this.getClass().getSimpleName(), newPart, firstScan, this.disposed);
            this.restartTimeLine();
        }
    }

    /**
     * Call {@link #executeNext()} or restart if needed.
     */
    private void updateNextMove() {
        if (this.disposed) {
            // FIXME : this happens sometimes, can't know why, bug is not reproducible efficiently.
            // stop() has not effect because when it happens, animation status is already STOPPED...
            AbstractScanningSelectionMode.LOGGER.warn(
                    "Scanning selection mode was disposed updateNextMove is called, stop scanning time line, timeLine statut {}",
                    this.scanningTimeLine.getStatus());
            synchronized (this.scanningTimeLine) {
                this.scanningTimeLine.stop();
            }
        } else {
            SelectionModeController.INSTANCE.showScanningSelectionModeView(); // Request to display main view
            
            //Check if scanning exist, and the extra start pause
            if (this.isScanningNextPossible()) {
                this.executeNext();
                this.updateCurrentComponent(false);
            } else {
                this.timesInSamePart++;
                this.restart();
            }
        }
    }

    // Class part : "Simple"
    //========================================================================
    //Subclass will override
    @Override
    public Node getSelectionView() {
        return this.view;
    }

    @Override
    public void play() {
        if (!this.executingActionOnCurrentPart && !this.pauseToExecuteSimpleActions && !this.disposed) {
            synchronized (this.scanningTimeLine) {
                this.playingProperty.set(true);
                this.scanningTimeLine.play();
            }
        }
    }

    @Override
    public void pause() {
        synchronized (this.scanningTimeLine) {
            this.playingProperty.set(false);
            this.scanningTimeLine.pause();
        }
    }

    @Override
    public void stop() {
        synchronized (this.scanningTimeLine) {
            this.playingProperty.set(false);
            this.scanningTimeLine.stop();
        }
    }

    @Override
    public void pauseUntilNextSelection(final Supplier<Boolean> isRestartOrPlay) {
        synchronized (this.scanningTimeLine) {
            this.nextSelectionListener = isRestartOrPlay;
            this.scanningTimeLine.stop();
            this.playingProperty.set(false);
            this.restartScanningOnNextAction = true;
        }
    }
    //========================================================================

    // Class part : "Progress draw properties"
    //========================================================================
    @Override
    public BooleanBinding currentPartNotNullProperty() {
        return this.currentPart.isNotNull();
    }
    //========================================================================

}
