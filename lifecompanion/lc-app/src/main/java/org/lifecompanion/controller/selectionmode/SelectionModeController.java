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
package org.lifecompanion.controller.selectionmode;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.selectionmode.*;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.ConfigurationLoadingTask;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.selectionmode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Controller that manage all the selection mode.<br>
 * Most method of this controller should be called only in use mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum SelectionModeController implements ModeListenerI {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(SelectionModeController.class);

    /**
     * The current part where mouse/scanning is over
     */
    private final ObjectProperty<GridPartComponentI> currentOverPart;

    /**
     * List of all part that user displayed<br>
     * In use mode, we only add element inside it, never remove, as it {@link #currentHistoryIndex} just have to be increased
     */
    private final List<GridPartComponentI> scanningHistory;

    /**
     * The index in the history list
     */
    private int currentHistoryIndex = -1;

    /**
     * Contains all cached selection modes for the running configuration.
     */
    private final Map<SelectionModeParameterI, SelectionModeI> selectionModes;

    /**
     * Change listener for the current grid in selection mode
     */
    private final ChangeListener<GridComponentI> changeListenerGrid;

    /**
     * All components that are currently "under" the current key press event
     */
    private final HashSet<GridPartKeyComponentI> currentPressComponents;

    /**
     * Timer to fire clic time listener
     */
    private Timer timer;

    /**
     * List of all next timer task waiting for execution after the press started
     */
    private final List<TimerTask> waitingTimerTask;

    /**
     * All wanted clic time listener on the next press started
     */
    private final List<WaitingClicListener> clicTimeListeners;

    /**
     * Current valid clic listener (the last clic listener reached)
     */
    private WaitingClicListener validClicListener;

    /**
     * Listener to clear pressed keys (needed because if a dialog is shown, current pressed key are not released... > JavaFX bug ?)
     */
    private Runnable clearPressedKeyListener;

    /**
     * All the mouse event listener
     */
    private final List<Consumer<MouseEvent>> mouseEventListener;

    /**
     * Previous configuration in use mode : used to go to previous configuration in use mode
     */
    private LCConfigurationDescriptionI previousConfigurationInUseMode;

    /**
     * Property for playing property of current scanning mode
     */
    private final BooleanProperty playingProperty;

    /**
     * Listener called when there is a configuration change in use mode
     */
    private final Set<Consumer<Boolean>> configurationChangingListeners;

    SelectionModeController() {
        this.currentOverPart = new SimpleObjectProperty<>(this, "currentOverPart", null);
        this.playingProperty = new SimpleBooleanProperty(false);
        this.scanningHistory = new ArrayList<>();
        this.waitingTimerTask = new ArrayList<>();
        this.selectionModes = new HashMap<>();
        this.clicTimeListeners = new ArrayList<>();
        this.mouseEventListener = new ArrayList<>();
        this.currentPressComponents = new HashSet<>();
        this.changeListenerGrid = (obs, ov, nv) -> {
            if (nv != null) {
                this.gridChanged(nv);
            }
        };
        configurationChangingListeners = new HashSet<>();
        AppModeController.INSTANCE.modeProperty().addListener((obs, ov, nv) -> {
            if (nv == AppMode.EDIT) {
                previousConfigurationInUseMode = null;
            }
        });
    }

    private SelectionModeI getSelectionModeConfiguration() {
        final LCConfigurationI configuration = AppModeController.INSTANCE.getUseModeContext().getConfiguration();
        return configuration != null ? configuration.selectionModeProperty().get() : null;
    }

    private SelectionModeParameterI getSelectionModeParameter() {
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        return selectionMode != null ? selectionMode.getParameters() : null;
    }

    /**
     * @return a property that define the current part where the key is over.
     */
    // Class part : "Properties"
    //========================================================================
    public ObjectProperty<GridPartComponentI> currentOverPartProperty() {
        return this.currentOverPart;
    }

    /**
     * @return a property that is true if the current selection is not a scanning mode, or if the {@link ScanningSelectionModeI#playingProperty()} return true.
     */
    public ReadOnlyBooleanProperty playingProperty() {
        return this.playingProperty;
    }
    //========================================================================

    // Class part : "Mouse events"
    //========================================================================
    public boolean globalMouseEvent(final MouseEvent mouseEvent) {
        //Fire all mouse event listener
        this.fireMouseEventForListener(mouseEvent);
        //Handle
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        SelectionModeParameterI parameters = this.getSelectionModeParameter();
        //Consume every event when the current selection mode is scanning
        if (parameters != null && selectionMode instanceof ScanningSelectionModeI) {
            ScanningSelectionModeI scanningSelection = (ScanningSelectionModeI) selectionMode;
            // Check that we activate on mouse with the right button filter
            if (parameters.fireEventInputProperty().get() == FireEventInput.MOUSE && parameters.mouseButtonActivationProperty().get().checkEvent(mouseEvent)) {
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    this.pressStarted();
                    scanningSelection.selectionPress(false);
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    boolean skipAction = this.pressReleased();
                    scanningSelection.selectionRelease(skipAction);
                }
                return !parameters.enableDirectSelectionOnMouseOnScanningSelectionModeProperty().get();
            }
            // Check that we fire next scan on mouse with the right button filter
            if (parameters.nextScanEventInputProperty().get() == FireEventInput.MOUSE && parameters.mouseButtonNextScanProperty().get().checkEvent(mouseEvent) && parameters.scanningModeProperty().get() == ScanningMode.MANUAL) {
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    scanningSelection.nextScanSelectionPress();
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    scanningSelection.nextScanSelectionRelease();
                }
                return !parameters.enableDirectSelectionOnMouseOnScanningSelectionModeProperty().get();
            }
        }
        return false;
    }

    public boolean globalKeyboardEvent(final KeyEvent keyEvent) {
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        //Consume every event when the current selection mode is scanning
        if (this.isValidSelectionModeKeyboardEvent(keyEvent)) {
            if (selectionMode instanceof ScanningSelectionModeI) {
                ScanningSelectionModeI scanningSelection = (ScanningSelectionModeI) selectionMode;
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    this.pressStarted();
                    scanningSelection.selectionPress(false);
                } else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
                    boolean skipAction = this.pressReleased();
                    scanningSelection.selectionRelease(skipAction);
                }
            } else if (selectionMode instanceof DirectSelectionModeI) {
                DirectSelectionModeI directSelectionMode = (DirectSelectionModeI) selectionMode;
                // On key press, fire selection press on current part
                if (this.currentOverPart.get() != null && this.currentOverPart.get() instanceof GridPartKeyComponentI
                        && keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    this.pressStarted();
                    GridPartKeyComponentI key = (GridPartKeyComponentI) this.currentOverPart.get();
                    directSelectionMode.selectionPress(key);
                    this.currentPressComponents.add(key);
                }
                // On key release, fire selection release on all previous press components
                else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && !this.currentPressComponents.isEmpty()) {
                    boolean skipAction = this.pressReleased();
                    for (GridPartKeyComponentI key : this.currentPressComponents) {
                        directSelectionMode.selectionRelease(key, skipAction);
                    }
                    this.currentPressComponents.clear();
                }
            }
            return true;
        } else if (this.isValidNextScanSelectionModeKeyboardEvent(keyEvent) && selectionMode instanceof ScanningSelectionModeI) {
            ScanningSelectionModeI scanningSelection = (ScanningSelectionModeI) selectionMode;
            if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                scanningSelection.nextScanSelectionPress();
            } else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
                scanningSelection.nextScanSelectionRelease();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidSelectionModeKeyboardEvent(final KeyEvent keyEvent) {
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        SelectionModeParameterI parameters = this.getSelectionModeParameter();
        //The current mode use keyboard, and the key is the fire event key
        return parameters != null
                && (!(selectionMode instanceof AutoDirectSelectionModeI)
                || selectionMode.getParameters().enableActivationWithSelectionProperty().get())
                && parameters.fireEventInputProperty().get() == FireEventInput.KEYBOARD
                && keyEvent.getCode() == parameters.keyboardFireKeyProperty().get();
    }

    public boolean isValidNextScanSelectionModeKeyboardEvent(final KeyEvent keyEvent) {
        SelectionModeParameterI parameters = this.getSelectionModeParameter();
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        //The current mode use keyboard, and the key is the fire next scan
        return parameters != null
                && parameters.scanningModeProperty().get() == ScanningMode.MANUAL
                && selectionMode instanceof ScanningSelectionModeI
                && parameters.nextScanEventInputProperty().get() == FireEventInput.KEYBOARD
                && keyEvent.getCode() == parameters.keyboardNextScanKeyProperty().get();
    }

    /**
     * To fire a given mouse event on a key element
     *
     * @param key the key the event is on
     * @param me  the mouse event to fire
     */
    public void fireMouseEventOn(final GridPartKeyComponentI key, final MouseEvent me) {
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        final SelectionModeParameterI selectionModeParameter = this.getSelectionModeParameter();
        if (selectionMode instanceof DirectSelectionModeI) {
            executeMouseEventForMode(key, me, (DirectSelectionModeI) selectionMode, selectionModeParameter);
        } else if (selectionModeParameter != null && selectionModeParameter.enableDirectSelectionOnMouseOnScanningSelectionModeProperty().get()) {
            final LCConfigurationI configuration = key.configurationParentProperty().get();
            if (configuration != null) {
                final DirectSelectionModeI directSelectionMode = configuration.directSelectionOnMouseOnScanningSelectionModeProperty().get();
                if (directSelectionMode != null) {
                    configuration.hideMainSelectionModeViewProperty().set(true);
                    executeMouseEventForMode(key, me, directSelectionMode, directSelectionMode.getParameters());
                }
            }
        }
    }

    public void showScanningSelectionModeView() {
        configuration.hideMainSelectionModeViewProperty().set(false);
    }

    private void executeMouseEventForMode(GridPartKeyComponentI key, MouseEvent me, DirectSelectionModeI directSelectionMode, SelectionModeParameterI selectionModeParameter) {
        // Check that the mouse event is valid for an selection fire (before calling press/release)
        boolean ableToFireSelection =
                selectionModeParameter == null ||
                        (selectionModeParameter.fireEventInputProperty().get() == FireEventInput.MOUSE && selectionModeParameter.mouseButtonActivationProperty().get().checkEvent(me));
        if (me.getEventType() == MouseEvent.MOUSE_PRESSED && ableToFireSelection) {
            this.currentOverPart.set(key);
            this.pressStarted();
            directSelectionMode.selectionPress(key);
        } else if (me.getEventType() == MouseEvent.MOUSE_RELEASED && ableToFireSelection) {
            this.currentOverPart.set(key);
            boolean skipAction = this.pressReleased();
            directSelectionMode.selectionRelease(key, skipAction);
        } else if (me.getEventType() == MouseEvent.MOUSE_ENTERED) {
            this.currentOverPart.set(key);
            directSelectionMode.selectionEnter(key);
        } else if (me.getEventType() == MouseEvent.MOUSE_EXITED) {
            this.currentOverPart.set(null);
            directSelectionMode.selectionExit(key);
        } else if (me.getEventType() == MouseEvent.MOUSE_MOVED) {
            directSelectionMode.selectionMovedOver(key);
        }
    }
    //========================================================================

    // Class part : "Fire mouse time program"
    //========================================================================
    private void pressStarted() {
        this.validClicListener = null;
        for (WaitingClicListener clicListener : this.clicTimeListeners) {
            //Create the task and schedule it
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    //Remove from waiting
                    SelectionModeController.this.waitingTimerTask.remove(this);
                    //Execute now or after
                    if (clicListener.onRelease) {
                        SelectionModeController.this.validClicListener = clicListener;
                    } else {
                        clicListener.action.run();
                    }
                }
            };
            this.waitingTimerTask.add(task);
            this.timer.schedule(task, clicListener.clicTime);
        }
    }

    private boolean pressReleased() {
        //Cancel and remove waiting task
        for (TimerTask waitingTask : this.waitingTimerTask) {
            waitingTask.cancel();
        }
        this.waitingTimerTask.clear();
        if (this.validClicListener != null) {
            this.validClicListener.action.run();
            return true;
        }
        return false;
    }

    /**
     * To add a listener that will be directly fired if a given time (in ms) is reached.
     *
     * @param clickTime the time to reach (in ms)
     * @param action    the callback
     */
    public void addAfterPressTimeListener(final long clickTime, final Runnable action) {
        this.clicTimeListeners.add(new WaitingClicListener(clickTime, action, false));
    }

    /**
     * To add a listener that will be directly fired if the selected is press during a given amount of time (in ms) and then release.<br>
     * This will fire the last valid listener reached and not all the first one.<br>
     * <strong>Example</strong> : listen for 100,200,300 ms, user clic for 250ms : only the 200 listener will be fired.
     *
     * @param clickTime the time to reach (in ms)
     * @param action    the callback
     */
    public void addOnReleaseAfterTimeListener(final long clickTime, final Runnable action) {
        this.clicTimeListeners.add(new WaitingClicListener(clickTime, action, true));
    }

    /**
     * Represent a clic time listener.<br>
     * The {@link #onRelease} value indicates if the event if fired when time is reached, or if the time is reached and press released
     */
    private static class WaitingClicListener {
        private final long clicTime;
        private final Runnable action;
        private final boolean onRelease;

        public WaitingClicListener(final long clicTime, final Runnable action, final boolean onRelease) {
            this.clicTime = clicTime;
            this.action = action;
            this.onRelease = onRelease;
        }
    }

    public void changeTempStrokeColor(final Color newColor) {
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        if (selectionMode instanceof DrawSelectionModeI) {
            DrawSelectionModeI drawSelectionMode = (DrawSelectionModeI) selectionMode;
            Platform.runLater(() -> {
                drawSelectionMode.strokeFillProperty().set(newColor);
                drawSelectionMode.progressFillProperty().set(newColor);
            });
        }
    }
    //========================================================================

    /**
     * To add a listener on every global mouse event fired by user.
     *
     * @param listener the listener
     */
    // Class part : "Mouse listener"
    //========================================================================
    public void addMouseEventListener(final Consumer<MouseEvent> listener) {
        this.mouseEventListener.add(listener);
    }

    private void fireMouseEventForListener(final MouseEvent mouseEvent) {
        for (Consumer<MouseEvent> mouseListener : this.mouseEventListener) {
            mouseListener.accept(mouseEvent);
        }
    }
    //========================================================================

    // Class part : "Mode start/stop"
    //========================================================================
    private LCConfigurationI configuration;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.configuration = configuration;
        checkSelectionConfiguration(configuration);
        this.timer = new Timer(true);
        this.playingProperty.set(false);
        configuration.hideMainSelectionModeViewProperty().set(false);
        //Get the first grid where scanning will start
        GridPartComponentI firstPart = this.getFirstComponentSelection(configuration);
        this.LOGGER.info("First element of selection mode is {}", firstPart);
        if (firstPart != null) {
            this.setCurrentMode(configuration.getSelectionModeParameter(), configuration, () -> {
                //Start scanning in the first part
                this.goToGridPart(firstPart);
            });
        }
    }


    /**
     * Determine the first component to be selected in the configuration.<br>
     * Will first find if a component is set in configuration, and if not, will take the first grid.
     *
     * @param configuration the configuration
     * @return the first component to select, will never return null if the configuration is not empty
     */
    private GridPartComponentI getFirstComponentSelection(final LCConfigurationI configuration) {
        if (configuration.firstSelectionPartProperty().get() != null) {
            return configuration.firstSelectionPartProperty().get();
        } else {
            GridComponentI gridToScan = null;
            ObservableList<RootGraphicComponentI> children = configuration.getChildren();
            for (int i = 0; i < children.size() && gridToScan == null; i++) {
                gridToScan = this.getFirstGrid(children.get(i));
            }
            return gridToScan;
        }
    }

    /**
     * Return the first grid to in the component tree
     *
     * @param comp the first grid found
     * @return the first grid in component tree
     */
    private GridComponentI getFirstGrid(final TreeDisplayableComponentI comp) {
        if (!comp.isNodeLeaf()) {
            ObservableList<TreeDisplayableComponentI> childrenNode = comp.getChildrenNode();
            for (TreeDisplayableComponentI child : childrenNode) {
                if (child instanceof GridComponentI) {
                    return (GridComponentI) child;
                } else {
                    return this.getFirstGrid(child);
                }
            }
        }
        return null;
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        //Cancel clic listener
        this.timer.cancel();
        this.waitingTimerTask.clear();
        this.clicTimeListeners.clear();
        this.mouseEventListener.clear();
        //Dispose mode and reinit variables
        this.disposeCurrentMode();
        configuration.selectionModeProperty().set(null);
        this.currentOverPart.set(null);
        this.scanningHistory.clear();
        this.selectionModes.clear();
        this.currentPressComponents.clear();
        this.currentHistoryIndex = -1;
        this.playingProperty.unbind();
        final DirectSelectionModeI directSelectionModeI = configuration.directSelectionOnMouseOnScanningSelectionModeProperty().get();
        if (directSelectionModeI != null) {
            directSelectionModeI.dispose();
            configuration.directSelectionOnMouseOnScanningSelectionModeProperty().set(null);
        }
        this.configuration = null;
    }

    /**
     * Will check every selection mode in every selection mode user of the configuration.</br>
     * If the selection mode is a part scanning selection mode, and the selection mode user need to have specific parameter, this will automatically change parameters.</br>
     * <strong>Example</strong> : if a grid has only one row, but the selection mode is row/column, then the selection mode for this grid is automatically changed to horizontal scanning.</br>
     * Issue #136.
     *
     * @param configuration the configuration to check.
     */
    private void checkSelectionConfiguration(final LCConfigurationI configuration) {
        ObservableMap<String, DisplayableComponentI> allComponent = configuration.getAllComponent();
        Set<String> componentIds = allComponent.keySet();
        Class<? extends SelectionModeI> configSelectionMode = configuration.getSelectionModeParameter().selectionModeTypeProperty().get();
        boolean rowColumnMode = RowColumnScanSelectionMode.class.isAssignableFrom(configSelectionMode);
        boolean columnRowMode = ColumnRowScanSelectionMode.class.isAssignableFrom(configSelectionMode);
        if (rowColumnMode || columnRowMode) {
            for (String id : componentIds) {
                final DisplayableComponentI component = configuration.getAllComponent().get(id);
                if (component instanceof GridComponentI) {
                    GridComponentI gridComp = (GridComponentI) component;
                    SelectionModeParameterI gridSelectionParameter = gridComp.getSelectionModeParameter();
                    if (gridComp.useParentSelectionModeProperty().get()) {
                        if (gridComp.rowCountProperty().get() == 1 && rowColumnMode) {
                            gridComp.useParentSelectionModeProperty().set(false);
                            gridSelectionParameter.copyFrom(configuration.getSelectionModeParameter());
                            gridSelectionParameter.selectionModeTypeProperty().set(HorizontalDirectKeyScanSelectionMode.class);
                            LOGGER.info("Selection mode automatically changed for grid {}, set to horizontal scanning",
                                    gridComp.nameProperty().get());
                        } else if (gridComp.columnCountProperty().get() == 1 && columnRowMode) {
                            gridComp.useParentSelectionModeProperty().set(false);
                            gridSelectionParameter.copyFrom(configuration.getSelectionModeParameter());
                            gridSelectionParameter.selectionModeTypeProperty().set(VerticalDirectKeyScanSelectionMode.class);
                            LOGGER.info("Selection mode automatically changed for grid {}, set to vertical scanning", gridComp.nameProperty().get());
                        }
                    }
                }
            }
        }

    }
    //========================================================================

    // Class part : "Internal useful methods"
    //========================================================================
    private void startScanningGrid(final GridComponentI grid) {
        LCUtils.runOnFXThread(() -> {
            //Stop previous
            this.stopCurrentScan();
            //Change the grid, this can fire a selection mode change
            SelectionModeI selectionMode = this.getSelectionModeConfiguration();
            selectionMode.currentGridProperty().set(grid);
            //Get the selection mode after change (can be same if is not changed)
            selectionMode = this.getSelectionModeConfiguration();
            //Now start the new mode
            if (selectionMode instanceof ScanningSelectionModeI) {
                ScanningSelectionModeI scanningMode = (ScanningSelectionModeI) selectionMode;
                scanningMode.restart();
            }
        });
    }

    private void stopCurrentScan() {
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        if (selectionMode instanceof ScanningSelectionModeI) {
            ScanningSelectionModeI scanningMode = (ScanningSelectionModeI) selectionMode;
            scanningMode.stop();
        }
    }

    private void gridChanged(final GridComponentI newGrid) {
        LCConfigurationI configuration = AppModeController.INSTANCE.getUseModeContext().configurationProperty().get();
        SelectionModeI currentMode = this.getSelectionModeConfiguration();
        this.LOGGER.debug("Grid changed for a scanning selection mode, current mode is {}", currentMode);
        //If there there is a existing mode, but the new grid change the selection mode
        if (currentMode != null && !newGrid.useParentSelectionModeProperty().get()
                && currentMode.getParameters() != newGrid.getSelectionModeParameter()) {
            this.LOGGER.info("Grid changed and override the current selection parameter, will change for the new ones");
            this.setCurrentMode(newGrid.getSelectionModeParameter(), configuration, null);
        }
        //If there is no existing mode, or the previous mode was overriding
        else if (currentMode == null || currentMode.getParameters() != configuration.getSelectionModeParameter()) {
            this.LOGGER.info("Previous grid was overriding the selection parameters, will restore configuration parameters");
            this.setCurrentMode(configuration.getSelectionModeParameter(), configuration, null);
        }
    }

    private void setCurrentMode(final SelectionModeParameterI parameters, final LCConfigurationI configuration, final Runnable callback) {
        //Stop previous mode
        GridComponentI grid = this.disposeCurrentMode();
        //Create the selection mode if doesn't exist
        if (!this.selectionModes.containsKey(parameters)) {
            Class<? extends SelectionModeI> selectionModeType = parameters.selectionModeTypeProperty().get();
            this.LOGGER.debug("Will create the mode from type {}", selectionModeType.getSimpleName());
            try {
                SelectionModeI selectionMode = selectionModeType.getConstructor().newInstance();
                selectionMode.setParameters(parameters);
                this.selectionModes.put(parameters, selectionMode);
                this.LOGGER.debug("Selection mode {} created", selectionMode.getClass().getSimpleName());
            } catch (Exception e) {
                this.LOGGER.warn("Couldn't create the wanted selection mode", e);
            }
        }
        //Set and init
        SelectionModeI newSelectionMode = this.selectionModes.get(parameters);
        newSelectionMode.init(this.getSelectionModeConfiguration());
        newSelectionMode.currentGridProperty().set(grid);

        newSelectionMode.currentGridProperty().addListener(this.changeListenerGrid);
        LCUtils.runOnFXThread(() -> {
            configuration.selectionModeProperty().set(newSelectionMode);
            if (newSelectionMode instanceof ScanningSelectionModeI && grid != null) {
                ((ScanningSelectionModeI) newSelectionMode).play();
            }
            if (callback != null) {
                callback.run();
            }
        });
        //Bind playing property if needed
        if (newSelectionMode instanceof ScanningSelectionModeI) {
            this.playingProperty.bind(((ScanningSelectionModeI) newSelectionMode).playingProperty());
        } else {
            LCUtils.unbindAndSet(playingProperty, true);
        }

        // Enable supp mode when needed : only if enable and the current mode is a scanning mode
        if (parameters.enableDirectSelectionOnMouseOnScanningSelectionModeProperty().get() && configuration.directSelectionOnMouseOnScanningSelectionModeProperty().get() == null && newSelectionMode instanceof ScanningSelectionModeI) {
            DirectActivationSelectionMode directActivationSelectionMode = new DirectActivationSelectionMode();
            // Create new parameters for this specific mode
            SelectionModeParameter parameterForDirectSelectionMode = new SelectionModeParameter();
            parameterForDirectSelectionMode.copyFrom(parameters);
            parameterForDirectSelectionMode.fireEventInputProperty().set(FireEventInput.MOUSE);
            parameterForDirectSelectionMode.mouseButtonActivationProperty().set(MouseButton.PRIMARY);
            // Issue #413 : same time to fire action in direct selection mode
            //parameterForDirectSelectionMode.timeToFireActionProperty().set(0);
            //parameterForDirectSelectionMode.timeBeforeRepeatProperty().set(0);
            // Set parameters and init
            directActivationSelectionMode.setParameters(parameterForDirectSelectionMode);
            directActivationSelectionMode.init(null);
            // Set on configuration
            LCUtils.runOnFXThread(() -> configuration.directSelectionOnMouseOnScanningSelectionModeProperty().set(directActivationSelectionMode));
        }
    }

    private GridComponentI disposeCurrentMode() {
        GridComponentI previousGrid = null;
        SelectionModeI currentMode = this.getSelectionModeConfiguration();
        if (currentMode != null) {
            this.LOGGER.debug("Dispose current mode {}", currentMode);
            currentMode.dispose();
            currentMode.currentGridProperty().removeListener(this.changeListenerGrid);
            previousGrid = currentMode.currentGridProperty().get();
        }
        return previousGrid;
    }

    private void goToGridPart(final GridPartComponentI gridPart, final boolean addToHistory) {
        this.addComponentToHistory(gridPart, addToHistory);
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        this.LOGGER.info("Request to go to grid part {} of type {}", gridPart.nameProperty().get(), gridPart.getClass().getSimpleName());
        GridPartComponentI toShowToFront = gridPart;
        //Scanning
        if (gridPart instanceof GridComponentI) {
            this.LOGGER.info("Grid part is a grid, will scan/show it");
        } else if (gridPart instanceof StackComponentI) {
            this.LOGGER.info("Grid part is a stack, will scan the displayed grid");
            StackComponentI stack = (StackComponentI) gridPart;
            toShowToFront = stack.displayedComponentProperty().get();
        } else {
            //Change the grid before go
            GridComponentI gridParent = gridPart.gridParentProperty().get();
            if (gridParent != selectionMode.currentGridProperty().get()) {
                selectionMode.currentGridProperty().set(gridParent);
            }
            //Go to part (on FX thread, after the potential new selection mode set)
            LCUtils.runOnFXThread(() -> this.getSelectionModeConfiguration().goToGridPart(gridPart));
        }
        //Show (even if its a direct selection mode, because grid can be behind others
        if (toShowToFront != null) {
            //Try scanning if selection component is a grid
            if (toShowToFront instanceof GridComponentI) {
                this.startScanningGrid((GridComponentI) toShowToFront);
            }
            //Show in view
            final GridPartComponentI toShowToFrontFinal = toShowToFront;
            LCUtils.runOnFXThread(() -> {
                toShowToFrontFinal.showToFront(AppMode.USE.getViewProvider(), true);
                this.getSelectionModeConfiguration().getSelectionView().toFront();//Show the selection model to front (over displayed component)
            });
        }
    }
    //========================================================================

    /**
     * Allow the user to go on a given grid part.<br>
     * This will handle the current selection mode and will display the needed component to be able to go to the grid part.<br>
     * This method should be called each time the user need to display or to go in a specific component.
     *
     * @param gridPart the grid part where user wants to go
     */
    // Class part : "Action public API"
    //========================================================================
    public void goToGridPart(final GridPartComponentI gridPart) {
        this.goToGridPart(gridPart, true);
    }

    /**
     * To go in the parent part of a given element, only if the element has a parent
     *
     * @param gridPart the grid part that have a parent to go in
     */
    public void goToParentPart(final GridPartComponentI gridPart) {
        if (gridPart != null && gridPart.gridParentProperty().get() != null) {
            this.LOGGER.info("Will try to go in parent grid of {}", gridPart.nameProperty().get());
            this.goToGridPart(gridPart.gridParentProperty().get());
        } else if (gridPart != null && gridPart.stackParentProperty().get() != null
                && gridPart.stackParentProperty().get() instanceof GridPartComponentI) {
            this.LOGGER.info("Will try to go in stack parent grid of {}", gridPart.nameProperty().get());
            GridPartComponentI parentStack = (GridPartComponentI) gridPart.stackParentProperty().get();
            if (parentStack.gridParentProperty().get() != null) {
                this.goToGridPart(parentStack.gridParentProperty().get());
            }
        }
    }

    /**
     * To go to the parent of the currently selected part (or scanned)
     */
    public void goToParentPartCurrent() {
        SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        this.goToParentPart(selectionMode.currentGridProperty().get());
    }

    /**
     * To go to the previous selected/displayed part
     */
    public void goToPreviousPart() {
        if (this.currentHistoryIndex > 0) {
            GridPartComponentI previousPart = this.scanningHistory.get(--this.currentHistoryIndex);
            this.goToGridPart(previousPart, false);
        }
    }

    /**
     * To pause the current scanning until the next user selection.
     *
     * @param nextSelectionListener the listener that will be called after the next selection.<br>
     *                              Should return true if the scanning should be restarted, if false, scanning will just resume
     */
    public void pauseCurrentScanningUntilNextSelection(final Supplier<Boolean> nextSelectionListener) {
        final SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        if (selectionMode instanceof ScanningSelectionModeI) {
            ScanningSelectionModeI scanningMode = (ScanningSelectionModeI) selectionMode;
            scanningMode.pauseUntilNextSelection(nextSelectionListener);
        } else if (selectionMode instanceof DirectSelectionModeI) {
            DirectSelectionModeI directMode = (DirectSelectionModeI) selectionMode;
            directMode.setNextSelectionListener(nextSelectionListener);
        }
    }

    /**
     * To restart the current scanning (if it's a scanning selection mode)
     */
    public void restartCurrentScanning() {
        final SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        if (selectionMode instanceof ScanningSelectionModeI) {
            ScanningSelectionModeI scanningMode = (ScanningSelectionModeI) selectionMode;
            scanningMode.restart();
        }
    }

    /**
     * Should be called by any event in use mode that is an user direct activation.</br>
     * This will be used to detect activation repeat (with {@link SelectionModeParameterI#timeBeforeRepeatProperty()}
     */
    public void activationDone() {
        final SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        if (selectionMode != null) {
            selectionMode.activationDone();
        }
    }

    /**
     * This stop the current scanning (if it's a scanning selection mode), generate the scanning part again (check again for empty keys, added keys, etc...), and start it again.
     */
    public void generateScanningForCurrentGridAndRestart() {
        final SelectionModeI selectionMode = this.getSelectionModeConfiguration();
        if (selectionMode instanceof ScanningSelectionModeI) {
            ScanningSelectionModeI scanningMode = (ScanningSelectionModeI) selectionMode;
            scanningMode.stop();
            scanningMode.generateScanningPartForCurrentGrid();
            scanningMode.restart();
        }
    }

    public void addConfigurationChangingListener(Consumer<Boolean> listenerStartChangeConfigurationInUseMode) {
        this.configurationChangingListeners.add(listenerStartChangeConfigurationInUseMode);
    }

    public void removeConfigurationChangingListener(Consumer<Boolean> listenerStartChangeConfigurationInUseMode) {
        this.configurationChangingListeners.remove(listenerStartChangeConfigurationInUseMode);
    }

    /**
     * To change the current configuration displayed even if the current mode is USE mode.<br>
     * This is useful to navigate through configuration in use mode.
     *
     * @param configurationDescription the configuration description for the configuration to open.
     */
    public void changeConfigurationInUseMode(final LCConfigurationDescriptionI configurationDescription) {
        //If there is a configuration and a profile
        if (configurationDescription != null && ProfileController.INSTANCE.currentProfileProperty().get() != null) {
            UseActionController.INSTANCE.pauseActionLaunch();
            //Enable changing view
            configurationChangingListeners.forEach(l -> l.accept(true));
            System.gc();// FIXME ?
            //Load the configuration (synch. because the action is executed in another Thread)
            ConfigurationLoadingTask configurationLoadingTask = IOHelper.createLoadConfigurationTask(configurationDescription, ProfileController.INSTANCE.currentProfileProperty().get());
            try {
                LCConfigurationI loadedConfiguration = LCUtils.executeInCurrentThread(configurationLoadingTask);
                final LCConfigurationDescriptionI previous = AppModeController.INSTANCE.getUseModeContext().getConfigurationDescription();
                AppModeController.INSTANCE.switchUseModeConfiguration(loadedConfiguration, configurationDescription);
                this.previousConfigurationInUseMode = previous;
                AppModeController.INSTANCE.getEditModeContext().clearPreviouslyEditedConfiguration();
            } catch (Throwable t) {
                this.LOGGER.warn("Couldn't load the configuration for change configuration use action", t);
                configurationChangingListeners.forEach(l -> l.accept(false));
                UseActionController.INSTANCE.unpauseActionLaunch();
            }
        }
    }

    /**
     * Go back in the previous configuration before the last call of {@link #changeConfigurationInUseMode(LCConfigurationDescriptionI)}
     */
    public void changeConfigurationForPrevious() {
        if (this.previousConfigurationInUseMode != null) {
            this.changeConfigurationInUseMode(this.previousConfigurationInUseMode);
        }
    }
    //========================================================================

    // Class part : "History"
    //========================================================================
    private void addComponentToHistory(final GridPartComponentI part, final boolean addToHistory) {
        if (addToHistory) {
            //Ignore every history after the current history index
            if (this.currentHistoryIndex + 1 < this.scanningHistory.size()) {
                this.scanningHistory.subList(this.currentHistoryIndex + 1, this.scanningHistory.size()).clear();
            }
            //Add and change
            this.scanningHistory.add(part);
            this.currentHistoryIndex = this.scanningHistory.size() - 1;
        }
    }
    //========================================================================

}
