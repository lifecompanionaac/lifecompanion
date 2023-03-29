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
package org.lifecompanion.controller.categorizedelement.useaction;

import javafx.collections.ObservableList;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventListenerI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.model.impl.categorizedelement.useaction.ActionExecutionResult;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.categorizedelement.useaction.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


/**
 * Controller that control how action are executed.<br>
 * This controller is responsible for action execution.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UseActionController implements LCStateListener, ModeListenerI {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(UseActionController.class);

    private static final int ACTION_THREAD_POOL_SIZE = 4;

    /**
     * Thread pool for actions
     */
    private ExecutorService threadPool;

    /**
     * Listener when a use event fire an event
     */
    private final UseEventListenerI useEventListener;

    /**
     * Listener for the next simple action execution end.
     */
    private final Map<UseActionEvent, List<Consumer<ActionExecutionResultI>>> nextSimpleActionExecutionListener;

    /**
     * Listener for the end of the current execution of all actions
     */
    private final Map<UseActionEvent, List<Consumer<ActionExecutionResultI>>> endOfSimpleActionExecutionListener;

    /**
     * To pause new action launch : useful when changing current configuration (reset in modeStart())
     */
    private boolean pauseActionLaunch;

    // TODO : action shouldn't be fire in config mode!
    // check in execute and in event listener

    UseActionController() {
        this.nextSimpleActionExecutionListener = new HashMap<>();
        this.endOfSimpleActionExecutionListener = new HashMap<>();
        for (UseActionEvent event : UseActionEvent.values()) {
            this.nextSimpleActionExecutionListener.put(event, new ArrayList<>());
            this.endOfSimpleActionExecutionListener.put(event, new ArrayList<>());
        }
        //Listener
        this.useEventListener = (generator, variables, callback) -> {
            Map<String, UseVariableI<?>> variablesMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(variables)) {
                for (UseVariableI<?> var : variables) {
                    variablesMap.put(var.getDefinition().getId(), var);
                }
            }
            this.executeSimpleOn(generator, UseActionEvent.EVENT, variablesMap, callback);
        };
    }

    // Class part : "Properties"
    //========================================================================

    /**
     * To add a listener that will be called on the next action execution (if you add it through an executing action, this will not be called until the next actions is executed)<br>
     * Will be called just before the next execution.
     *
     * @param event the event type we want to be called, same listener can be added in different list
     * @return a list where listener for next simple action execution on the wanted event.<br>
     * Every listener added on this list will be deleted after being called.
     */
    public List<Consumer<ActionExecutionResultI>> getNextSimpleActionExecutionListener(final UseActionEvent event) {
        return this.nextSimpleActionExecutionListener.get(event);
    }

    /**
     * To add a listener that will be called directly at the end of the current action (if there is).<br>
     * If you add it through an executing action, it will be called once every action are executed.
     *
     * @param event the event type we want to be called, same listener can be added in different list
     * @return a list where listener for end of simple action execution on the wanted event.<br>
     * Every listener added on this list will be deleted after being called.
     */
    public List<Consumer<ActionExecutionResultI>> getEndOfSimpleActionExecutionListener(final UseActionEvent event) {
        return this.endOfSimpleActionExecutionListener.get(event);
    }
    //========================================================================

    // Class part : "Actions"
    //========================================================================
    public void pauseActionLaunch() {
        this.pauseActionLaunch = true;
    }

    public void unpauseActionLaunch() {
        this.pauseActionLaunch = false;
    }

    private static List<BaseUseActionI<?>> getActionListCopy(UseActionTriggerComponentI useActionTriggerComponent, UseActionEvent eventType) {
        final ObservableList<BaseUseActionI<?>> actionOriginalList = useActionTriggerComponent.getActionManager().componentActions().get(eventType);
        return CollectionUtils.isEmpty(actionOriginalList) ? Collections.emptyList() : new ArrayList<>(actionOriginalList);
    }

    /**
     * @param element   the element to start event on
     * @param event     the event type to start on
     * @param variables the variables associated to event
     */
    public void startEventOn(final UseActionTriggerComponentI element, final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        if (!this.pauseActionLaunch) {
            this.threadPool.submit(() -> {
                try {
                    for (BaseUseActionI<?> action : getActionListCopy(element, event)) {
                        if (!action.isSimple()) {
                            action.eventStarts(event);
                        }
                    }
                } catch (Throwable t) {
                    this.LOGGER.error("Error on use action after start end", t);
                }
            });
        } else {
            LOGGER.warn("Start event ignored because action launch is paused ({}, {})", element, event);
        }
    }

    /**
     * @param element   the element to end event on
     * @param event     the event type to stop
     * @param variables the variables associated to event
     */
    public void endEventOn(final UseActionTriggerComponentI element, final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        final Runnable executedEndEvent = () -> {
            try {
                for (BaseUseActionI<?> action : getActionListCopy(element, event)) {
                    if (!action.isSimple()) {
                        action.eventEnds(event);
                    }
                }
            } catch (Throwable t) {
                this.LOGGER.error("Error on use action after event end", t);
            }
        };
        if (threadPool != null) {
            this.threadPool.submit(executedEndEvent);
        } else {
            LOGGER.warn("Will execute endEventOn on {} in current Thread because thread pool was disposed. This may have happened because the event ends was fired after mode changed to config...", element);
            executedEndEvent.run();
        }
    }

    /**
     * To execute actions of a element.<br>
     * This will call {@link #executeSimpleOn(UseActionTriggerComponentI, UseActionEvent, Map, boolean, Consumer)} with inNewThread to true
     *
     * @param element   the element that has simple actions
     * @param event     event type
     * @param variables variables associated to the executions
     * @param callback  callback called once action are executed
     */
    public void executeSimpleOn(final UseActionTriggerComponentI element, final UseActionEvent event, final Map<String, UseVariableI<?>> variables, final Consumer<ActionExecutionResultI> callback) {
        this.executeSimpleOn(element, event, variables, true, callback);
    }

    /**
     * To execute actions of a element.<br>
     *
     * @param element     the element that has simple actions
     * @param event       event type
     * @param variables   variables associated to the executions
     * @param callback    callback called once action are executed
     * @param inNewThread to execute action in another thread, most of the time it should be true because we don't want to block action execution caller.
     */
    public void executeSimpleOn(final UseActionTriggerComponentI element, final UseActionEvent event, final Map<String, UseVariableI<?>> variables, final boolean inNewThread, final Consumer<ActionExecutionResultI> callback) {
        this.executeSimpleActionsInternal(element, element.getActionManager().componentActions().get(event), event, variables, callback, inNewThread);
    }

    public void executeSimpleDetachedActionsInNewThread(final UseActionTriggerComponentI useActionTriggerComponent, List<BaseUseActionI<UseActionTriggerComponentI>> actions, final Consumer<ActionExecutionResultI> callback) {
        // Attach to parent component
        actions.forEach(action -> action.parentComponentProperty().set(useActionTriggerComponent));
        // Execute, detach and call callback
        executeSimpleActionsInternal(useActionTriggerComponent, new ArrayList<>(actions), UseActionEvent.INTERNAL, null, result -> {
            actions.forEach(action -> action.parentComponentProperty().set(null));
            callback.accept(result);
        }, true);
    }

    private void executeSimpleActionsInternal(final UseActionTriggerComponentI useActionTriggerComponent, List<BaseUseActionI<?>> actions, final UseActionEvent event, final Map<String, UseVariableI<?>> variables, final Consumer<ActionExecutionResultI> callback, final boolean inNewThread) {
        if (!this.pauseActionLaunch) {
            List<Consumer<ActionExecutionResultI>> nextActionListener = this.getAndCleanNextSimpleActionListeners(event);
            final Map<String, UseVariableI<?>> finalVariables = this.checkAndMergeVariables(variables);
            Runnable actionExecutable = () -> {
                ActionExecutionResultI result = new ActionExecutionResult(true);
                try {
                    result = executeActions(event, actions, finalVariables);
                } catch (Throwable t) {
                    this.LOGGER.error("Error on simple use action execution", t);
                } finally {
                    //First run the callback
                    try {
                        if (callback != null) {
                            callback.accept(result);
                        }
                    } finally {
                        //Run end of action listener
                        this.executeSimpleActionListeners(this.getAndCleanEndSimpleActionListeners(event), result);
                        //Run next action listener
                        this.executeSimpleActionListeners(nextActionListener, result);
                    }
                }
            };
            if (inNewThread) {
                this.threadPool.submit(actionExecutable);
            } else {
                actionExecutable.run();
            }
        } else {
            LOGGER.warn("Execute simple action ignored because action launch is paused ({}, {})", useActionTriggerComponent, event);
        }
    }

    private ActionExecutionResultI executeActions(UseActionEvent eventType, List<BaseUseActionI<?>> actions, Map<String, UseVariableI<?>> variables) {
        int count = 0;
        boolean movingAction = false;
        final List<BaseUseActionI<?>> actionList = CollectionUtils.isEmpty(actions) ? Collections.emptyList() : new ArrayList<>(actions);
        for (BaseUseActionI<?> action : actionList) {
            if (action.isSimple()) {
                SimpleUseActionI<?> simpleAction = (SimpleUseActionI<?>) action;
                if (AppModeController.INSTANCE.modeProperty().get() == AppMode.USE) {
                    try {
                        simpleAction.execute(eventType, variables);
                        movingAction |= simpleAction.isMovingAction();
                        count++;
                    } catch (Throwable t) {
                        LOGGER.error("Use action simple execution failed for action {}", simpleAction.getClass().getSimpleName(), t);
                    }
                } else {
                    LOGGER.warn("Didn't run simple action {} because current mode is not use mode anymore", simpleAction.getClass().getSimpleName());
                }
            }
        }
        return new ActionExecutionResult(movingAction, count, variables);
    }

    private Map<String, UseVariableI<?>> checkAndMergeVariables(Map<String, UseVariableI<?>> variables) {
        //Null not allowed
        if (variables == null) {
            variables = new HashMap<>();
        }
        //Merge with the software variables
        final Map<String, UseVariableI<?>> lcVariables = UseVariableController.INSTANCE.generateVariables(false);
        final Set<String> keys = lcVariables.keySet();
        for (String key : keys) {
            variables.put(key, lcVariables.get(key));
        }
        return variables;
    }

    private List<Consumer<ActionExecutionResultI>> getAndCleanNextSimpleActionListeners(final UseActionEvent event) {
        List<Consumer<ActionExecutionResultI>> list = this.nextSimpleActionExecutionListener.get(event);
        List<Consumer<ActionExecutionResultI>> copy = new ArrayList<>(list);
        list.clear();
        return copy;
    }

    private List<Consumer<ActionExecutionResultI>> getAndCleanEndSimpleActionListeners(final UseActionEvent event) {
        List<Consumer<ActionExecutionResultI>> list = this.endOfSimpleActionExecutionListener.get(event);
        List<Consumer<ActionExecutionResultI>> copy = new ArrayList<>(list);
        list.clear();
        return copy;
    }

    private void executeSimpleActionListeners(final List<Consumer<ActionExecutionResultI>> listeners, final ActionExecutionResultI result) {
        for (Consumer<ActionExecutionResultI> listener : listeners) {
            listener.accept(result);
        }
        listeners.clear();
    }
    //========================================================================

    // Class part : "LC starts/exit"
    //========================================================================
    @Override
    public void lcStart() {
    }

    @Override
    public void lcExit() {
        if (this.threadPool != null) {
            this.threadPool.shutdownNow();
        }
    }
    //========================================================================

    // Class part : "Mode listener"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.threadPool = Executors.newFixedThreadPool(UseActionController.ACTION_THREAD_POOL_SIZE, LCNamedThreadFactory.threadFactory("UserActionController"));
        this.unpauseActionLaunch();
        configuration.getEventManager().attachAndStart(this.useEventListener, configuration);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.pauseActionLaunch();
        configuration.getEventManager().detachAndStop(configuration);
        for (UseActionEvent event : UseActionEvent.values()) {
            this.endOfSimpleActionExecutionListener.get(event).clear();
        }
        this.threadPool.shutdownNow();
        this.threadPool = null;
        this.LOGGER.info("User actions thread pool disposed");
    }
    //========================================================================

}
