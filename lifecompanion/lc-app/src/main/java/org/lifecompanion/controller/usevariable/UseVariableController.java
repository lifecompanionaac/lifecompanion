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
package org.lifecompanion.controller.usevariable;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.input.Clipboard;
import javafx.util.Duration;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.VariableInformationKeyOption;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.PowerSource;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This controller manage LifeCompanion variables (available variable and displayed).
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UseVariableController implements ModeListenerI {
    INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(UseVariableController.class);

    public final static String VARIABLE_OPEN_CHAR = "{", VARIABLE_CLOSE_CHAR = "}";
    public final static long MILLIS_TIME_UPDATE_INFO_KEY = 1000;
    private static final SimpleDateFormat DATE_ONLY_HOURS_MIN = new SimpleDateFormat("HH:mm");

    /**
     * Contains definition ID -> definition
     */
    private Map<String, UseVariableDefinitionI> possibleDefinitions;

    private ObservableList<UseVariableDefinitionI> possibleDefinitionList;

    private final List<VariableInformationKeyOption> variablesInformationKeyOptions;

    private final List<Consumer<Map<String, UseVariableI<?>>>> variableUpdateListeners;

    private final Map<String, Pattern> patternCache;

    private final Timeline keyUpdateTimeLine;

    private final Map<String, Pair<Long, UseVariableI<?>>> cachedVariableValues;

    private InvalidationListener listenerCurrentOverPart, listenerCurrentText, listenerCurrentWord, listenerCurrentChar, listenerLastCompleteWord;

    UseVariableController() {
        this.variablesInformationKeyOptions = new ArrayList<>();
        this.variableUpdateListeners = new ArrayList<>();
        this.cachedVariableValues = new HashMap<>();
        this.keyUpdateTimeLine = new Timeline(new KeyFrame(Duration.millis(UseVariableController.MILLIS_TIME_UPDATE_INFO_KEY),
                event -> UseVariableController.this.updateInformationKeyOptions(true)));
        this.keyUpdateTimeLine.setCycleCount(Animation.INDEFINITE);
        this.patternCache = new HashMap<>();
        initListeners();
    }

    private void initListeners() {
        this.listenerCurrentOverPart = createListener("CurrentOverPartName", "CurrentOverPartGridParentName");
        this.listenerCurrentText = createListener("CurrentTextInEditor");
        this.listenerCurrentWord = createListener("CurrentWordInEditor");
        this.listenerCurrentChar = createListener("CurrentCharInEditor");
        this.listenerLastCompleteWord = createListener("LastCompleteWordInEditor");
    }

    private InvalidationListener createListener(String... ids) {
        return inv -> {
            for (String id : ids) {
                clearFromCache(id);
            }
            requestVariablesUpdate(true);
        };
    }

    /**
     * @return a list of all possible variable
     */
    public ObservableList<UseVariableDefinitionI> getPossibleVariables() {
        return this.getPossibleDefinitionsList();
    }

    /**
     * To get notified on variable update
     *
     * @param listener the listener that will be called with new variables values
     */
    public void addVariableUpdateListener(final Consumer<Map<String, UseVariableI<?>>> listener) {
        this.variableUpdateListeners.add(listener);
    }

    /**
     * To remove a listener added with {@link #addVariableUpdateListener(Consumer)}
     *
     * @param listener listener to remove
     */
    public void removeVariableUpdateListener(final Consumer<Map<String, UseVariableI<?>>> listener) {
        this.variableUpdateListeners.remove(listener);
    }

    // Class part : "Get and init definition"
    //========================================================================
    private synchronized Map<String, UseVariableDefinitionI> getPossibleDefinitions() {
        if (this.possibleDefinitions == null) {
            this.initDefinitions();
        }
        return this.possibleDefinitions;
    }

    private synchronized ObservableList<UseVariableDefinitionI> getPossibleDefinitionsList() {
        if (this.possibleDefinitionList == null) {
            this.initDefinitions();
        }
        return this.possibleDefinitionList;
    }

    private void initDefinitions() {
        this.possibleDefinitions = new HashMap<>();
        this.possibleDefinitionList = FXCollections.observableArrayList();
        this.addDef(new UseVariableDefinition("CurrentTextInEditor", "use.variable.current.text.in.editor.name",
                "use.variable.current.text.in.editor.description", "use.variable.current.text.in.editor.example"));
        this.addDef(new UseVariableDefinition("CurrentDate", "use.variable.current.date.name", "use.variable.current.date.description",
                "use.variable.current.date.example"));
        this.addDef(new UseVariableDefinition("CurrentTime", "use.variable.current.time.name", "use.variable.current.time.description",
                "use.variable.current.time.example", 500));
        this.addDef(new UseVariableDefinition("CurrentTimeWithoutSeconds", "use.variable.current.time.without.seconds.name",
                "use.variable.current.time.without.seconds.description", "use.variable.current.time.without.seconds.example"));
        this.addDef(new UseVariableDefinition("CurrentDayOfWeek", "use.variable.current.day.of.week.name",
                "use.variable.current.day.of.week.description", "use.variable.current.day.of.week.example"));
        this.addDef(new UseVariableDefinition("CurrentOverPartName", "use.variable.current.over.part.name",
                "use.variable.current.over.part.description", "use.variable.current.over.part.example"));
        this.addDef(new UseVariableDefinition("CurrentOverPartGridParentName", "use.variable.current.over.part.grid.parent.name",
                "use.variable.current.over.part.grid.parent.description", "use.variable.current.over.part.grid.parent.example"));
        this.addDef(new UseVariableDefinition("CurrentWordInEditor", "use.variable.current.word.in.editor.name",
                "use.variable.current.word.in.editor.description", "use.variable.current.word.in.editor.example"));
        this.addDef(new UseVariableDefinition("LastCompleteWordInEditor", "use.variable.last.complete.word.in.editor.name",
                "use.variable.last.complete.word.in.editor.description", "use.variable.last.complete.word.in.editor.example"));
        this.addDef(new UseVariableDefinition("CurrentCharInEditor", "use.variable.current.char.in.editor.name",
                "use.variable.current.char.in.editor.description", "use.variable.current.char.in.editor.example"));
        this.addDef(new UseVariableDefinition("CurrentTextInClipboard", "use.variable.current.text.in.clipboard.name",
                "use.variable.current.text.in.clipboard.description", "use.variable.current.text.in.clipboard.example", 1000));
        this.addDef(new UseVariableDefinition("BatteryLevel", "use.variable.battery.level.percent.name",
                "use.variable.battery.level.percent.description", "use.variable.battery.level.percent.example", 30_000, true));
        this.addDef(new UseVariableDefinition("BatteryTimeRemaining", "use.variable.battery.time.remaining.name",
                "use.variable.battery.time.remaining.description", "use.variable.battery.time.remaining.example", 30_000, true));
        //Init plugin
        PluginController.INSTANCE.getUseVariableDefinitions().registerListenerAndDrainCache(this::addDef);
    }

    private void addDef(final UseVariableDefinitionI def) {
        this.possibleDefinitions.put(def.getId(), def);
        this.possibleDefinitionList.add(def);
    }
    //========================================================================


    // Class part : "Variable generation"
    //========================================================================
    private void putToVarMap(boolean useCachedValue, String id, Map<String, UseVariableI<?>> vars, Supplier<String> stringVarSupplier) {
        putToVarMap(useCachedValue, id, vars, def -> new StringUseVariable(def, stringVarSupplier.get()));
    }

    private void putToVarMap(boolean useCachedValue, String id, Map<String, UseVariableI<?>> vars, Function<UseVariableDefinitionI, UseVariableI<?>> varSupplier) {
        UseVariableDefinitionI varDef = this.getPossibleDefinitions().get(id);
        if (varDef != null && varSupplier != null) {
            if (useCachedValue || varDef.isCacheForced()) {
                Pair<Long, UseVariableI<?>> cached = cachedVariableValues.get(id);
                if (cached != null && System.currentTimeMillis() - cached.getLeft() <= varDef.getCacheLifetime()) {
                    vars.put(varDef.getId(), cached.getRight());
                    return;
                }
            }
            long start = System.currentTimeMillis();
            UseVariableI<?> value = varSupplier.apply(varDef);
            cachedVariableValues.put(id, Pair.of(System.currentTimeMillis(), value));
            vars.put(varDef.getId(), value);
            LOGGER.debug("\t{} computed in {} ms", id, System.currentTimeMillis() - start);
        } else {
            LOGGER.warn("Didn't find any use variable definition or supplier for variable {}", id);
        }
    }

    /**
     * Remove the variable cached value for the given variable ID
     *
     * @param id the use variable ID
     */
    public void clearFromCache(String id) {
        cachedVariableValues.remove(id);
    }

    /**
     * To generate use variable.<br>
     * This should be called directly only if you really need.<br>
     * If you want to update the variable, you should better call {@link #requestVariablesUpdate()}
     *
     * @param useCachedValue if the cached values for variables should be used
     * @return the generated variables.
     */
    public Map<String, UseVariableI<?>> generateVariables(boolean useCachedValue) {
        long start = System.currentTimeMillis();
        //TODO : generate only used variables
        Map<String, UseVariableI<?>> vars = new HashMap<>();
        Date currentDate = new Date();
        //Current date
        putToVarMap(useCachedValue, "CurrentDate", vars, () -> StringUtils.dateToStringWithoutHour(currentDate));
        putToVarMap(useCachedValue, "CurrentTime", vars, () -> StringUtils.dateToStringDateWithOnlyHoursMinuteSecond(currentDate));
        putToVarMap(useCachedValue, "CurrentTimeWithoutSeconds", vars, () -> DATE_ONLY_HOURS_MIN.format(currentDate));
        putToVarMap(useCachedValue, "CurrentDayOfWeek", vars, () -> LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        putToVarMap(useCachedValue, "CurrentTextInEditor", vars, () -> WritingStateController.INSTANCE.currentTextProperty().get());
        putToVarMap(useCachedValue, "CurrentWordInEditor", vars, () -> WritingStateController.INSTANCE.currentWordProperty().get());
        putToVarMap(useCachedValue, "LastCompleteWordInEditor", vars, () -> WritingStateController.INSTANCE.lastCompleteWordProperty().get());
        putToVarMap(useCachedValue, "CurrentCharInEditor", vars, () -> WritingStateController.INSTANCE.currentCharProperty().get());
        putToVarMap(useCachedValue, "CurrentTextInClipboard", vars, this::getClipboardContent);
        putToVarMap(useCachedValue, "BatteryLevel", vars, () -> {
            try {
                SystemInfo si = new SystemInfo();
                List<PowerSource> powerSources = si.getHardware().getPowerSources();
                if (!CollectionUtils.isEmpty(powerSources)) {
                    PowerSource powerSource = powerSources.get(0);
                    double remainingCapacityPercent = powerSource.getRemainingCapacityPercent();
                    double batteryLevel = remainingCapacityPercent != 1.0 ? remainingCapacityPercent : (1.0 * powerSource.getCurrentCapacity()) / powerSource.getMaxCapacity();
                    return "" + (int) (batteryLevel * 100.0) + "%";
                }
            } catch (Throwable t) {
                LOGGER.warn("Couldn't get power source information", t);
            }
            return "?";
        });
        putToVarMap(useCachedValue, "BatteryTimeRemaining", vars, () -> {
            try {
                SystemInfo si = new SystemInfo();
                List<PowerSource> powerSources = si.getHardware().getPowerSources();
                if (!CollectionUtils.isEmpty(powerSources)) {
                    PowerSource powerSource = powerSources.get(0);
                    double timeRemainingEstimated = powerSource.getTimeRemainingEstimated();
                    double batteryRemainingTime = timeRemainingEstimated >= 0 ? timeRemainingEstimated : powerSource.getTimeRemainingInstant();
                    return org.lifecompanion.util.StringUtils.durationToString((int) batteryRemainingTime);
                }
            } catch (Throwable t) {
                LOGGER.warn("Couldn't get power source information", t);
            }
            return "?";
        });
        putToVarMap(useCachedValue, "CurrentOverPartName", vars, () -> {
            GridPartComponentI currentOverPart = SelectionModeController.INSTANCE.currentOverPartProperty().get();
            return currentOverPart != null ? currentOverPart.nameProperty().get() : "";
        });
        putToVarMap(useCachedValue, "CurrentOverPartGridParentName", vars, () -> {
            GridPartComponentI currentOverPart = SelectionModeController.INSTANCE.currentOverPartProperty().get();
            if (currentOverPart != null) {
                if (currentOverPart.gridParentProperty().get() != null) {
                    return currentOverPart.gridParentProperty().get().nameProperty().get();
                }
            }
            return "";
        });

        // BACKWARD COMPATIBILITY : generate plugin variable and merge
        Map<String, UseVariableI<?>> oldPluginVars = PluginController.INSTANCE.generatePluginsUseVariableBackwardCompatibility();
        vars.putAll(oldPluginVars);

        // Plugin variable : new unique var generation method
        List<Pair<String, Function<UseVariableDefinitionI, UseVariableI<?>>>> pluginVars = PluginController.INSTANCE.generatePluginUseVariable();
        for (Pair<String, Function<UseVariableDefinitionI, UseVariableI<?>>> pluginVar : pluginVars) {
            putToVarMap(useCachedValue, pluginVar.getLeft(), vars, pluginVar.getRight());
        }

        //Call listener
        for (Consumer<Map<String, UseVariableI<?>>> listener : this.variableUpdateListeners) {
            listener.accept(vars);
        }

        LOGGER.debug("Took {} ms to generate use variable (cache {})", System.currentTimeMillis() - start, useCachedValue);
        return vars;
    }

    private String getClipboardContent() {
        return FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            Clipboard systemClipboard = Clipboard.getSystemClipboard();
            return systemClipboard.hasString() ? systemClipboard.getString() : "";
        });
    }

    /**
     * To get all the possible variable for an event.
     *
     * @param useEventGeneratorProp the event generator
     * @return a merge between LifeCompanion use variable and event variable
     */
    public ObservableList<UseVariableDefinitionI> getPossibleVariableList(final ReadOnlyObjectProperty<UseEventGeneratorI> useEventGeneratorProp) {
        ObservableList<UseVariableDefinitionI> resultList = FXCollections.observableArrayList();
        resultList.addAll(this.getPossibleDefinitionsList());
        if (useEventGeneratorProp != null && useEventGeneratorProp.get() != null) {
            final List<UseVariableDefinitionI> generatedVariables = useEventGeneratorProp.get().getGeneratedVariables();
            if (!CollectionUtils.isEmpty(generatedVariables)) {
                resultList.addAll(generatedVariables);
            }
        }
        return resultList;
    }
    //========================================================================

    /**
     * To search for variable
     *
     * @param terms the search content
     * @return the search predicate
     */
    public Predicate<UseVariableDefinitionI> searchForVariable(final String terms) {
        String[] termArray = terms != null ? terms.split(" ") : new String[]{};
        if (termArray.length > 0) {
            List<String> termList = Arrays.stream(termArray).filter(s -> s.length() > 2).collect(Collectors.toList());
            return (varDef) -> StringUtils.startWithIgnoreCase(varDef.getName(), terms)
                    || StringUtils.countContainsIgnoreCase(varDef.getName(), termList) > 0
                    || StringUtils.countContainsIgnoreCase(varDef.getDescription(), termList) > 0;
        } else {
            return (varDef) -> true;
        }
    }

    /**
     * To create a text from a pattern text and variables values.
     *
     * @param text      the text to fill with variables
     * @param variables the variable values
     * @return the text with variable replaced with their values
     */
    // Class part : "Variables replacement"
    //========================================================================
    public String createText(boolean useCachedValue, String text, final Map<String, UseVariableI<?>> variables) {
        return createText(useCachedValue, text, variables, null);
    }

    public String createText(String text, final Map<String, UseVariableI<?>> variables) {
        return createText(false, text, variables, null);
    }

    public String createText(String text, final Map<String, UseVariableI<?>> variables, Function<String, String> variableValueTransformer) {
        return createText(false, text, variables, variableValueTransformer);
    }

    public String createText(boolean useCachedValue, String text, final Map<String, UseVariableI<?>> variables, Function<String, String> variableValueTransformer) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        Map<String, UseVariableI<?>> updatedVariables = generateVariables(useCachedValue);
        if (variables != null) {
            variables.putAll(updatedVariables);
        }
        //Replace when needed
        final Set<String> keys = variables.keySet();
        for (String key : keys) {
            final UseVariableI<?> variable = variables.get(key);
            text = getPatternFor(key).matcher(text)
                    .replaceAll(Matcher.quoteReplacement(variableValueTransformer != null ? variableValueTransformer.apply(variable.toStringValue()) : variable.toStringValue()));
        }
        //TODO : if the user use a variable not in the map, should we replace it with a blank string ?
        return text;
    }


    private Pattern getPatternFor(String key) {
        return patternCache.computeIfAbsent(key,
                k -> Pattern.compile("\\" + UseVariableController.VARIABLE_OPEN_CHAR + key + "\\" + UseVariableController.VARIABLE_CLOSE_CHAR, Pattern.CASE_INSENSITIVE));
    }
    //========================================================================


    // Class part : "Information key option"
    //========================================================================

    /**
     * Equivalent to {@link #requestVariablesUpdate(boolean)} with useCachedValue to false
     */
    public void requestVariablesUpdate() {
        this.requestVariablesUpdate(false);
    }

    /**
     * To request a new variable update.<br>
     * Variable will be updated every second, but you could need to manually update variables.
     *
     * @param useCachedValue true if the variable cached values should be used
     */
    public void requestVariablesUpdate(boolean useCachedValue) {
        FXThreadUtils.runOnFXThread(() -> this.updateInformationKeyOptions(useCachedValue));
    }

    private void updateInformationKeyOptions(boolean useCachedValue) {
        if (!CollectionUtils.isEmpty(this.variablesInformationKeyOptions) || !CollectionUtils.isEmpty(this.variableUpdateListeners)) {
            long start = System.currentTimeMillis();
            Map<String, UseVariableI<?>> variables = this.generateVariables(useCachedValue);
            for (VariableInformationKeyOption infoKeyOption : this.variablesInformationKeyOptions) {
                infoKeyOption.updateKeyInformations(variables);
            }
            UseVariableController.LOGGER.debug("{} variable information key options updated in {} ms", this.variablesInformationKeyOptions.size(),
                    System.currentTimeMillis() - start);
        }
    }

    private void searchForVariableInformationKeys(final LCConfigurationI configuration) {
        ObservableMap<String, DisplayableComponentI> allComponentMap = configuration.getAllComponent();
        Set<String> keys = allComponentMap.keySet();
        for (String id : keys) {
            DisplayableComponentI configComponent = allComponentMap.get(id);
            if (configComponent instanceof GridPartKeyComponentI) {
                GridPartKeyComponentI key = (GridPartKeyComponentI) configComponent;
                if (key.keyOptionProperty().get() instanceof VariableInformationKeyOption) {
                    this.variablesInformationKeyOptions.add((VariableInformationKeyOption) key.keyOptionProperty().get());
                }
            }
        }
    }

    private void launchVariableUpdate() {
        if (!CollectionUtils.isEmpty(this.variablesInformationKeyOptions)) {
            this.keyUpdateTimeLine.playFromStart();
        }
    }
    //========================================================================

    // Class part : "Mode listener"
    //========================================================================

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.variablesInformationKeyOptions.clear();
        //Search for all key option
        this.searchForVariableInformationKeys(configuration);
        this.launchVariableUpdate();
        this.registerVariableUpdateListener();
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.keyUpdateTimeLine.stop();
        this.variablesInformationKeyOptions.clear();
        removeVariableUpdateListener();
        this.cachedVariableValues.clear();
    }

    private void registerVariableUpdateListener() {
        SelectionModeController.INSTANCE.currentOverPartProperty().addListener(this.listenerCurrentOverPart);
        WritingStateController.INSTANCE.currentWordProperty().addListener(this.listenerCurrentWord);
        WritingStateController.INSTANCE.currentCharProperty().addListener(this.listenerCurrentChar);
        WritingStateController.INSTANCE.lastCompleteWordProperty().addListener(this.listenerLastCompleteWord);
        WritingStateController.INSTANCE.currentTextProperty().addListener(this.listenerCurrentText);
    }

    private void removeVariableUpdateListener() {
        SelectionModeController.INSTANCE.currentOverPartProperty().removeListener(this.listenerCurrentOverPart);
        WritingStateController.INSTANCE.currentWordProperty().removeListener(this.listenerCurrentWord);
        WritingStateController.INSTANCE.currentCharProperty().removeListener(this.listenerCurrentChar);
        WritingStateController.INSTANCE.lastCompleteWordProperty().removeListener(this.listenerLastCompleteWord);
        WritingStateController.INSTANCE.currentTextProperty().removeListener(this.listenerCurrentText);
    }
    //========================================================================

}
