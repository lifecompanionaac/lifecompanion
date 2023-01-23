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

package org.lifecompanion.plugin.calendar.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.controller.configurationcomponent.UseModeProgressDisplayerController;
import org.lifecompanion.controller.configurationcomponent.dynamickey.UserActionSequenceController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.virtualkeyboard.VirtualKeyboardController;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.PlaySoundAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SpeakTextAction;
import org.lifecompanion.model.impl.configurationcomponent.TimeOfDay;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey.UserActionSequenceCurrentKeyOption;
import org.lifecompanion.plugin.calendar.CalendarPlugin;
import org.lifecompanion.plugin.calendar.CalendarPluginProperties;
import org.lifecompanion.plugin.calendar.keyoption.CalendarEventListKeyOption;
import org.lifecompanion.plugin.calendar.keyoption.CalendarLeisureKeyOption;
import org.lifecompanion.plugin.calendar.model.*;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum CalendarController implements ModeListenerI {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarController.class);

    private LCCalendar calendar;

    /**
     * Current selected calendar day
     */
    private final ObjectProperty<CalendarDay> displayedDay;

    /**
     * Current displayed event (in the list, if user go from previous/next event)
     */
    private final ObjectProperty<CalendarEvent> displayedEvent;

    /**
     * Running event : started or not, finished or not, but the main running event
     */
    private final ObjectProperty<CalendarEvent> runningEvent;

    // Key options to display events
    private final List<CalendarEventListKeyOption> calendarEventListBeforeCurrent;
    private final List<CalendarEventListKeyOption> calendarEventListAfterCurrent;
    private CalendarEventListKeyOption forDisplayedEventKeyOption;
    private CalendarEventListKeyOption forRunningEventKeyOption;

    private CalendarLeisureKeyOption forCurrentSelectionLeisureKeyOption;
    private final List<CalendarLeisureKeyOption> calendarLeisureKeyOptions;

    /**
     * Disable user actions (useful to disable action when a event is running, an action is running, etc.)
     */
    private final AtomicInteger disableEventCancelOrSelectRequest;

    private final Consumer<String> sequenceFinishedCallback;
    private String lastLaunchedSequenceId;

    private ExecutorService wakingUpService;
    private Timer alarmTimer, currentDayChecker;
    private final AtomicReference<CalendarEvent> currentAlarmEvent = new AtomicReference<>();

    CalendarController() {
        calendarEventListAfterCurrent = new ArrayList<>();
        calendarEventListBeforeCurrent = new ArrayList<>();
        calendarLeisureKeyOptions = new ArrayList<>();
        disableEventCancelOrSelectRequest = new AtomicInteger();
        displayedDay = new SimpleObjectProperty<>();
        displayedDay.addListener(inv -> displayedDayUpdated());
        displayedEvent = new SimpleObjectProperty<>();
        displayedEvent.addListener(inv -> displayedEventUpdate());
        runningEvent = new SimpleObjectProperty<>();
        runningEvent.addListener(inv -> runningEventUpdate());
        sequenceFinishedCallback = this::sequenceFinished;
    }


    // UI UPDATE
    //========================================================================
    private void displayedDayUpdated() {
        final CalendarDay displayedDayV = displayedDay.get();
        LOGGER.info("Displayed day updated to {}", displayedDayV);
        if (displayedDayV != null) {
            // If it is the current day : try to find the last past event with a fixed time
            if (displayedDayV.dayOfWeekProperty().get() == DayOfWeek.current()) {
                final TimeOfDay now = TimeOfDay.now();
                // Find the closest past event
                CalendarEvent eventToSelect = displayedDayV.getEvents().stream()
                        .filter(c -> c.enableAtFixedTimeProperty().get() && c.getFixedTime().compareTo(now) <= 0)
                        .max(Comparator.comparing(CalendarEvent::getFixedTime))
                        .orElse(CollectionUtils.isEmpty(displayedDayV.getEvents()) ? null : displayedDayV.getEvents().get(0));
                displayedEvent.set(eventToSelect);
                runningEvent.set(eventToSelect);

                restartAlarmServicesForDay(displayedDayV);
            }
            // Other day : start at day start
            else {
                displayedEvent.set(CollectionUtils.isEmpty(displayedDayV.getEvents()) ? null : displayedDayV.getEvents().get(0));
            }
        } else {
            displayedEvent.set(null);
        }
    }

    private void displayedEventUpdate() {
        FXThreadUtils.runOnFXThread(() -> {
            // Set displayed
            final CalendarEvent displayEventV = displayedEvent.get();
            if (forDisplayedEventKeyOption != null) {
                forDisplayedEventKeyOption.currentSimplerKeyContentContainerProperty().set(displayEventV);
            }
            final Pair<List<CalendarEvent>, Integer> displayedEventsAndDisplayedIndex = getDisplayedEventsAndDisplayedIndex();
            int indexBefore = displayedEventsAndDisplayedIndex.getRight() - 1;
            for (int i = calendarEventListBeforeCurrent.size() - 1; i >= 0; i--) {
                calendarEventListBeforeCurrent.get(i).currentSimplerKeyContentContainerProperty().set(
                        displayedEventsAndDisplayedIndex.getRight() >= 0 && indexBefore >= 0 ? displayedEventsAndDisplayedIndex.getLeft().get(indexBefore) : null
                );
                indexBefore--;
            }
            for (int i = 0; i < calendarEventListAfterCurrent.size(); i++) {
                calendarEventListAfterCurrent.get(i).currentSimplerKeyContentContainerProperty().set(
                        displayedEventsAndDisplayedIndex.getRight() >= 0 && displayedEventsAndDisplayedIndex.getRight() + i + 1 < displayedEventsAndDisplayedIndex.getLeft().size() ? displayedEventsAndDisplayedIndex.getLeft().get(displayedEventsAndDisplayedIndex.getRight() + i + 1) : null
                );
            }
        });
    }

    private void runningEventUpdate() {
        FXThreadUtils.runOnFXThread(() -> {
            final CalendarEvent newRunningEvent = runningEvent.get();
            forRunningEventKeyOption.currentSimplerKeyContentContainerProperty().set(newRunningEvent);
            if (forRunningEventKeyOption != null && newRunningEvent != null) {
                newRunningEvent.statusProperty().set(CalendarEventStatus.CURRENT);
                disableEventCancelOrSelectRequest.set(0);
                setAllEventBeforeToPastAndAllEventAfterToNotPast(newRunningEvent);
                UseModeProgressDisplayerController.INSTANCE.hideAllProgress();
                SelectionModeController.INSTANCE.goToGridPart(forRunningEventKeyOption.attachedKeyProperty().get());
                // Auto start this event (if possible and enabled)
                if (newRunningEvent.enableAutostartWhenPreviousFinishedProperty().get() && !newRunningEvent.enableAtFixedTimeProperty().get()) {
                    stopAlarmAndStartEvent(newRunningEvent);
                }
            } else if (forDisplayedEventKeyOption != null) {
                SelectionModeController.INSTANCE.goToGridPart(forDisplayedEventKeyOption.attachedKeyProperty().get());
            }
        });
    }

    private void setAllEventBeforeToPastAndAllEventAfterToNotPast(CalendarEvent event) {
        final List<CalendarEvent> events = displayedDay.get() != null ? displayedDay.get().getEvents() : Collections.emptyList();
        final int currentEventIndex = events.indexOf(event);
        if (currentEventIndex >= 0) {
            // All event before to "past" if they are not done
            for (int i = currentEventIndex - 1; i >= 0; i--) {
                if (events.get(i).statusProperty().get() != CalendarEventStatus.DONE) {
                    events.get(i).statusProperty().set(CalendarEventStatus.PAST);
                }
                events.get(i).hadBeenStartedProperty().set(true);
                events.get(i).hadBeenFinishedProperty().set(true);
            }
            // All event after : remove status
            for (int i = currentEventIndex + 1; i < events.size(); i++) {
                events.get(i).statusProperty().set(null);
                events.get(i).hadBeenStartedProperty().set(false);
                events.get(i).hadBeenFinishedProperty().set(false);
            }
        }
    }

    private Pair<List<CalendarEvent>, Integer> getDisplayedEventsAndDisplayedIndex() {
        final List<CalendarEvent> events = displayedDay.get() != null ? displayedDay.get().getEvents() : Collections.emptyList();
        final CalendarEvent displayEventV = displayedEvent.get();
        return Pair.of(events, displayEventV != null ? events.indexOf(displayEventV) : -1);
    }
    //========================================================================


    // PUBLIC ACTIONS
    //========================================================================

    /**
     * To display the next events in the current day
     */
    public void displayNextEventsInCurrentDay() {
        final Pair<List<CalendarEvent>, Integer> displayedEventsAndDisplayedIndex = getDisplayedEventsAndDisplayedIndex();
        if (displayedEventsAndDisplayedIndex.getRight() >= 0 && displayedEventsAndDisplayedIndex.getRight() < displayedEventsAndDisplayedIndex.getLeft().size() - 1) {
            displayedEvent.set(displayedEventsAndDisplayedIndex.getLeft().get(displayedEventsAndDisplayedIndex.getRight() + 1));
        }
    }

    /**
     * To display the previous events in the current day
     */
    public void displayPreviousEventsInCurrentDay() {
        final Pair<List<CalendarEvent>, Integer> displayedEventsAndDisplayedIndex = getDisplayedEventsAndDisplayedIndex();
        if (displayedEventsAndDisplayedIndex.getRight() > 0) {
            displayedEvent.set(displayedEventsAndDisplayedIndex.getLeft().get(displayedEventsAndDisplayedIndex.getRight() - 1));
        }
    }

    /**
     * When a selection is done on running event key.<br>
     * If a alarm is running, will stop it.<br>
     * Will then launch start action for this event
     */
    public void selectionOnRunningEvent() {
        final CalendarEvent eventToStart = runningEvent.get();
        LOGGER.info("selectionOnRunningEvent() {}", eventToStart);
        if (eventToStart != null && eventToStart.hadBeenStartedProperty().get()) {
            // Event is already start but we came back on day view : should allow user to go back to destination grid when click
            final UserActionSequenceCurrentKeyOption firstCurrentItemKeyOption = UserActionSequenceController.INSTANCE.getFirstCurrentItemKeyOption();
            if (eventToStart.enableLinkToSequenceProperty().get() && lastLaunchedSequenceId != null && firstCurrentItemKeyOption != null) {
                SelectionModeController.INSTANCE.goToGridPart(firstCurrentItemKeyOption.attachedKeyProperty().get());
            } else if (eventToStart.enableLeisureSelectionProperty().get()) {
                if (!CollectionUtils.isEmpty(this.calendarLeisureKeyOptions)) {
                    final GridPartKeyComponentI firstKeyInList = calendarLeisureKeyOptions.get(0).attachedKeyProperty().get();
                    SelectionModeController.INSTANCE.goToGridPart(firstKeyInList);
                }
            }
        } else if (disableEventCancelOrSelectRequest.get() <= 0) {
            stopAlarmAndStartEvent(eventToStart);
        }
    }

    private void stopAlarmAndStartEvent(CalendarEvent eventToStart) {
        if (eventToStart != null) {
            if (!eventToStart.hadBeenStartedProperty().get()) {
                // There is a running alarm : stop it and start action for current event
                if (currentAlarmEvent.getAndSet(null) == eventToStart) {
                    SoundAlarmController.INSTANCE.stopAllAlarm();
                }
                // Check the event and start its actions if possible : the event is current and not at a fixed time (or before the current time)
                if (eventToStart.statusProperty().get() == CalendarEventStatus.CURRENT && (!eventToStart.enableAtFixedTimeProperty().get() || eventToStart.getFixedTime().compareTo(TimeOfDay.now()) <= 0)) {
                    eventToStart.hadBeenStartedProperty().set(true);
                    eventToStart.hadBeenFinishedProperty().set(false);
                    final Runnable afterStartActionCallback = getAfterStartAction();
                    disableEventCancelOrSelectRequest.incrementAndGet();
                    UseActionController.INSTANCE.executeSimpleDetachedActionsInNewThread(forRunningEventKeyOption.attachedKeyProperty().get(), getActionOnStartForEvent(eventToStart), result -> {
                        disableEventCancelOrSelectRequest.decrementAndGet();
                        FXThreadUtils.runOnFXThread(afterStartActionCallback);
                    });
                }
            }
        }
    }

    /**
     * Stop the current running event (can be stopped only if it had been started)
     */
    public void finishCurrentCalendarEvent() {
        final CalendarEvent eventToFinish = runningEvent.get();
        LOGGER.info("finishCurrentCalendarEvent()  disabled : {}, running : {}", disableEventCancelOrSelectRequest.get() > 0, eventToFinish);
        if (disableEventCancelOrSelectRequest.get() <= 0 && eventToFinish != null && eventToFinish.hadBeenStartedProperty().get() && !eventToFinish.hadBeenFinishedProperty().get() && currentAlarmEvent.get() != eventToFinish) {
            eventToFinish.hadBeenFinishedProperty().set(true);
            // After actions on finish : set previous as past/done and select next
            Runnable updateEventsAfterCurrentIsFinished = () -> FXThreadUtils.runOnFXThread(() -> {
                // All event before current become past
                final List<CalendarEvent> events = displayedDay.get() != null ? displayedDay.get().getEvents() : Collections.emptyList();
                final int finishedEventIndex = events.indexOf(eventToFinish);
                // Finished event become done
                eventToFinish.statusProperty().set(CalendarEventStatus.DONE);

                // Set the event after the last current to new running, and display it (if exist)
                CalendarEvent newRunningEvent = null;
                if (finishedEventIndex + 1 < events.size()) {
                    CalendarEvent newCurrent = events.get(finishedEventIndex + 1);
                    displayedEvent.set(newCurrent);
                    // Set as running event only if not an event with date (or with date but was past)
                    newRunningEvent = !newCurrent.enableAtFixedTimeProperty().get() || newCurrent.getFixedTime().compareTo(TimeOfDay.now()) < 0 ? newCurrent : null;
                }
                runningEvent.set(newRunningEvent);
            });

            // Try to run action on finish if needed
            LOGGER.info("Action on finish : {}", forRunningEventKeyOption);
            if (forRunningEventKeyOption != null) {
                disableEventCancelOrSelectRequest.incrementAndGet();
                UseActionController.INSTANCE.executeSimpleDetachedActionsInNewThread(forRunningEventKeyOption.attachedKeyProperty().get(), getActionOnFinishForEvent(eventToFinish), result -> {
                    disableEventCancelOrSelectRequest.decrementAndGet();
                    updateEventsAfterCurrentIsFinished.run();
                });
            } else {
                updateEventsAfterCurrentIsFinished.run();
            }
        }
    }
    //========================================================================


    // ACTIONS
    //========================================================================

    /**
     * @return the callback to run after start action had been executed (should also be executed if there is no start actions).<br>
     * Will contains the action to move and start the linked sequence, or to start event timer
     */
    private Runnable getAfterStartAction() {
        List<Runnable> runnableList = new ArrayList<>();
        // Event is linked to a sequence
        final CalendarEvent runningEventV = runningEvent.get();
        if (runningEventV.enableLinkToSequenceProperty().get() && StringUtils.isNotBlank(runningEventV.linkedSequenceIdProperty().get())) {
            lastLaunchedSequenceId = runningEventV.linkedSequenceIdProperty().get();
            final UserActionSequenceCurrentKeyOption firstCurrentItemKeyOption = UserActionSequenceController.INSTANCE.getFirstCurrentItemKeyOption();
            if (firstCurrentItemKeyOption != null) {
                runnableList.add(() -> {
                    disableEventCancelOrSelectRequest.incrementAndGet();
                    SelectionModeController.INSTANCE.goToGridPart(firstCurrentItemKeyOption.attachedKeyProperty().get());
                    UserActionSequenceController.INSTANCE.startSequenceById(lastLaunchedSequenceId);
                });
            }
        }
        // Event use a timer
        if (runningEventV.enableAutomaticItemProperty().get()) {
            runnableList.add(() -> {
                disableEventCancelOrSelectRequest.incrementAndGet();
                UseModeProgressDisplayerController.INSTANCE.launchTimer(runningEventV.automaticItemTimeMsProperty().get(), disableEventCancelOrSelectRequest::decrementAndGet);
            });
        }
        // Leisure selection
        if (runningEventV.enableLeisureSelectionProperty().get()) {
            runnableList.add(this::initAndShowLeisureSelection);
        }
        // Execute all actions
        return () -> {
            runnableList.forEach(Runnable::run);
        };
    }

    private List<BaseUseActionI<UseActionTriggerComponentI>> getActionOnStartForEvent(CalendarEvent calendarEvent) {
        List<BaseUseActionI<UseActionTriggerComponentI>> actions = new ArrayList<>();
        if (calendarEvent.enableTextOnStartProperty().get()) {
            final SpeakTextAction speakTextAction = new SpeakTextAction();
            speakTextAction.textToSpeakProperty().set(calendarEvent.textOnStartProperty().get());
            actions.add(speakTextAction);
        }
        if (calendarEvent.enablePlayRecordedSoundPropertyOnStartProperty().get()) {
            PlaySoundAction playSoundAction = new PlaySoundAction();
            playSoundAction.getSoundResourceHolder().updateSound(
                    calendarEvent.getSoundOnStartResourceHolder().filePathProperty().get(), calendarEvent.getSoundOnStartResourceHolder().durationInSecondProperty().get());
            playSoundAction.setMaxGainOnPlay(true);
            actions.add(playSoundAction);
        }
        return actions;
    }

    private List<BaseUseActionI<UseActionTriggerComponentI>> getActionOnFinishForEvent(CalendarEvent calendarEvent) {
        List<BaseUseActionI<UseActionTriggerComponentI>> actions = new ArrayList<>();
        if (calendarEvent.enableTextOnFinishProperty().get()) {
            final SpeakTextAction speakTextAction = new SpeakTextAction();
            speakTextAction.textToSpeakProperty().set(calendarEvent.textOnFinishProperty().get());
            actions.add(speakTextAction);
        }
        if (calendarEvent.enablePlayRecordedSoundPropertyOnEndProperty().get()) {
            PlaySoundAction playSoundAction = new PlaySoundAction();
            playSoundAction.getSoundResourceHolder().updateSound(
                    calendarEvent.getSoundOnEndResourceHolder().filePathProperty().get(), calendarEvent.getSoundOnEndResourceHolder().durationInSecondProperty().get());
            playSoundAction.setMaxGainOnPlay(true);
            actions.add(playSoundAction);
        }
        return actions;
    }
    //========================================================================

    // SEQUENCE
    //========================================================================
    private void sequenceFinished(String sequenceId) {
        if (StringUtils.isEquals(sequenceId, lastLaunchedSequenceId)) {
            lastLaunchedSequenceId = null;
            disableEventCancelOrSelectRequest.decrementAndGet();
            finishCurrentCalendarEvent();
        }
    }
    //========================================================================

    // LEISURE
    //========================================================================
    public void initAndShowLeisureSelection() {
        if (!CollectionUtils.isEmpty(this.calendarLeisureKeyOptions)) {
            final GridPartKeyComponentI firstKeyInList = calendarLeisureKeyOptions.get(0).attachedKeyProperty().get();
            // Fill out randomly
            List<CalendarLeisure> shuffledLeisure = new ArrayList<>(this.calendar.getAvailableLeisure());
            Collections.shuffle(shuffledLeisure);
            // Display choices
            FXThreadUtils.runOnFXThread(() -> {
                if (forCurrentSelectionLeisureKeyOption != null)
                    forCurrentSelectionLeisureKeyOption.currentSimplerKeyContentContainerProperty().set(null);
                shuffledLeisure.forEach(c -> c.selectedPropertyProperty().set(false));
                for (int i = 0; i < calendarLeisureKeyOptions.size(); i++) {
                    calendarLeisureKeyOptions.get(i).currentSimplerKeyContentContainerProperty().set(i < shuffledLeisure.size() ? shuffledLeisure.get(i) : null);
                }
            });
            SelectionModeController.INSTANCE.goToGridPart(firstKeyInList);
        }
    }

    public void selectCalendarLeisure(GridPartKeyComponentI srcKey, CalendarLeisure calendarLeisure) {
        FXThreadUtils.runOnFXThread(() -> {
            if (forCurrentSelectionLeisureKeyOption != null)
                forCurrentSelectionLeisureKeyOption.currentSimplerKeyContentContainerProperty().set(calendarLeisure);
            if (calendarLeisure != null) {
                calendarLeisure.selectedPropertyProperty().set(true);
                UseActionController.INSTANCE.executeSimpleDetachedActionsInNewThread(srcKey, getActionOnLeisureSelection(calendarLeisure), result -> {
                });
            }
        });
    }

    private List<BaseUseActionI<UseActionTriggerComponentI>> getActionOnLeisureSelection(CalendarLeisure calendarLeisure) {
        List<BaseUseActionI<UseActionTriggerComponentI>> actionOnSelection = new ArrayList<>();
        if (calendarLeisure != null) {
            if (calendarLeisure.enableLeisureSpeechProperty().get()) {
                final SpeakTextAction speakTextAction = new SpeakTextAction();
                speakTextAction.textToSpeakProperty().set(calendarLeisure.leisureSpeechProperty().get());
                actionOnSelection.add(speakTextAction);
            }
            if (calendarLeisure.enablePlayRecordedSoundPropertyOnSelectionProperty().get()) {
                PlaySoundAction playSoundAction = new PlaySoundAction();
                playSoundAction.getSoundResourceHolder().updateSound(
                        calendarLeisure.getSoundOnSelectionResourceHolder().filePathProperty().get(), calendarLeisure.getSoundOnSelectionResourceHolder().durationInSecondProperty().get());
                playSoundAction.setMaxGainOnPlay(true);
                actionOnSelection.add(playSoundAction);
            }
        }
        return actionOnSelection;
    }
    //========================================================================


    // START/STOP CALENDAR
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        UserActionSequenceController.INSTANCE.getOnSequenceFinishedListeners().add(sequenceFinishedCallback);
        this.calendar = configuration.getPluginConfigProperties(CalendarPlugin.PLUGIN_ID, CalendarPluginProperties.class).getCalendar();

        // Find all event list keys
        Map<GridComponentI, List<CalendarEventListKeyOption>> keys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(CalendarEventListKeyOption.class, configuration, keys, null);
        for (CalendarEventListKeyOption calendarEventListKeyOption : keys.values().stream().flatMap(List::stream).collect(Collectors.toList())) {
            // Current main
            if (forRunningEventKeyOption == null && calendarEventListKeyOption.forRunningEventProperty().get()) {
                forRunningEventKeyOption = calendarEventListKeyOption;
            }
            // Current and not set
            if (!calendarEventListKeyOption.forRunningEventProperty().get()) {
                if (forDisplayedEventKeyOption == null && calendarEventListKeyOption.forCurrentEventProperty().get()) {
                    forDisplayedEventKeyOption = calendarEventListKeyOption;
                } else if (forDisplayedEventKeyOption != null) {
                    calendarEventListAfterCurrent.add(calendarEventListKeyOption);
                } else {
                    calendarEventListBeforeCurrent.add(calendarEventListKeyOption);
                }
            }
        }
        // Find all leisure keys
        Map<GridComponentI, List<CalendarLeisureKeyOption>> leisureKeys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(CalendarLeisureKeyOption.class, configuration, leisureKeys, null);
        this.calendarLeisureKeyOptions.addAll(leisureKeys.values().stream().flatMap(List::stream).peek(leisureKeyOption -> {
            if (leisureKeyOption.forCurrentSelectionProperty().get())
                forCurrentSelectionLeisureKeyOption = leisureKeyOption;
        }).filter(leisureKeyOption -> !leisureKeyOption.forCurrentSelectionProperty().get()).collect(Collectors.toList()));
        startCurrentDayChecker();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        stopCurrentDayChecker();
        UserActionSequenceController.INSTANCE.getOnSequenceFinishedListeners().remove(sequenceFinishedCallback);
        cancelAlarmServicesIfNeeded();
        displayedDay.set(null);
        displayedEvent.set(null);
        runningEvent.set(null);
        calendarEventListAfterCurrent.clear();
        calendarEventListBeforeCurrent.clear();
        calendarLeisureKeyOptions.clear();
        forDisplayedEventKeyOption = null;
        forRunningEventKeyOption = null;
        forCurrentSelectionLeisureKeyOption = null;
        disableEventCancelOrSelectRequest.set(0);
        calendar = null;
    }
    //========================================================================

    // DAY UPDATE
    //========================================================================
    private void startCurrentDayChecker() {
        // Timer to check a day change
        currentDayChecker = new Timer(true);
        currentDayChecker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCalendarToToday();
            }
        }, 10_000, 5 * 60 * 1000);// check every 5 minutes
    }

    private void stopCurrentDayChecker() {
        if (currentDayChecker != null) {
            currentDayChecker.cancel();
            currentDayChecker = null;
        }
    }

    public void updateCalendarToToday() {
        final CalendarDay todayInCalendar = calendar.getDays().stream().filter(d -> d.dayOfWeekProperty().get() == DayOfWeek.current()).findFirst().orElse(null);
        if (todayInCalendar != null && displayedDay.get() != todayInCalendar) {
            FXThreadUtils.runOnFXThread(() -> this.displayedDay.set(todayInCalendar));
        }
    }
    //========================================================================

    // ALARM
    //========================================================================
    private void restartAlarmServicesForDay(CalendarDay today) {
        cancelAlarmServicesIfNeeded();
        wakingUpService = Executors.newSingleThreadExecutor(LCNamedThreadFactory.daemonThreadFactory("Calendar-Alarm-Service"));
        alarmTimer = new Timer(true);
        final int maxAlarmRepeatTimeMs = this.calendar.maxAlarmRepeatTimeMsProperty().get();
        final int repeatAlarmIntervalTimeMs = this.calendar.repeatAlarmIntervalTimeMsProperty().get();
        final TimeOfDay now = TimeOfDay.now();
        final ObservableList<CalendarEvent> events = today.getEvents();
        for (CalendarEvent eventWithAlarm : events) {
            if (eventWithAlarm.enableAtFixedTimeProperty().get()) {
                final TimeOfDay fixedTime = eventWithAlarm.getFixedTime();
                if (fixedTime.compareTo(now) > 0) {
                    final Date dateForToday = fixedTime.getDateForToday();
                    LOGGER.info("Will plan an alarm to : {} / {}", fixedTime, dateForToday);
                    alarmTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Set the current alarm and then launching waking up for this alarm
                            currentAlarmEvent.set(eventWithAlarm);

                            // Launch waking up service
                            wakingUpService.submit(() -> {

                                // Set the event as the current, update previous events, update displayed event
                                runningEvent.set(eventWithAlarm);
                                displayedEvent.set(eventWithAlarm);

                                // Animation to inform that an alarm is running
                                LOGGER.info("Attached key : {}", forRunningEventKeyOption.attachedKeyProperty().get());
                                AlarmTransition alarmTransition = new AlarmTransition(forRunningEventKeyOption.attachedKeyProperty().get()).playAndReturnThis();

                                long lastAlarmTS = 0;
                                final long alarmStartedAt = System.currentTimeMillis();
                                do {
                                    ThreadUtils.safeSleep(500);

                                    // Alarm action (only if under the max alarm time)
                                    if (
                                            System.currentTimeMillis() - alarmStartedAt < maxAlarmRepeatTimeMs
                                                    && System.currentTimeMillis() - lastAlarmTS >= repeatAlarmIntervalTimeMs
                                                    && !eventWithAlarm.hadBeenStartedProperty().get()
                                    ) {
                                        // Send a key event to wake up the computer
                                        VirtualKeyboardController.INSTANCE.keyPressThenRelease(KeyCode.CONTROL);

                                        // Start sound and voices
                                        if (eventWithAlarm.enableSoundOnAlarmProperty().get() && eventWithAlarm.soundOnAlarmProperty().get() != null) {
                                            SoundAlarmController.INSTANCE.playFromStart(eventWithAlarm.soundOnAlarmProperty().get());
                                        }
                                        if (eventWithAlarm.enableTextOnAlarmProperty().get() && StringUtils.isNotBlank(eventWithAlarm.textOnAlarmProperty().get())) {
                                            VoiceSynthesizerController.INSTANCE.speakSync(eventWithAlarm.textOnAlarmProperty().get());
                                        }

                                        // Wait till next repeat
                                        lastAlarmTS = System.currentTimeMillis();
                                    }
                                } while (currentAlarmEvent.get() == eventWithAlarm);

                                // When alarm was stopped by user : stop transition
                                alarmTransition.stopAndRestore();
                            });
                        }
                    }, dateForToday);
                }
            }
        }
    }

    public void catchUpCalendarEvent(CalendarEvent calendarEvent) {
        // Set the display and running event to the event to catch up
        if (calendarEvent != null) {
            if (runningEvent.get() != calendarEvent) {
                calendarEvent.hadBeenStartedProperty().set(false);
                calendarEvent.hadBeenFinishedProperty().set(false);
            }
            // FIXME : disable on next app update
            UserActionSequenceController.INSTANCE.cancelRunningSequence();
            disableEventCancelOrSelectRequest.set(0);
            displayedEvent.set(calendarEvent);
            runningEvent.set(calendarEvent);
        }
    }

    private void cancelAlarmServicesIfNeeded() {
        currentAlarmEvent.set(null);
        if (alarmTimer != null) {
            alarmTimer.cancel();
            alarmTimer = null;
        }
        if (wakingUpService != null) {
            wakingUpService.shutdown();
            wakingUpService = null;
        }
    }
    //========================================================================

}
