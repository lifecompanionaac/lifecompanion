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
package org.lifecompanion.base.data.common;

import com.sun.glass.ui.Window;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import org.lifecompanion.api.component.definition.*;
import org.lifecompanion.api.component.definition.grid.ComponentGridI;
import org.lifecompanion.api.component.definition.keyoption.KeyOptionI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.base.data.component.simple.GridPartKeyComponent;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.useaction.impl.text.write.WriteAndSpeakTextAction;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utils class relative to LifeCompanion data model.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
@SuppressWarnings("restriction")
public class LCUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(LCUtils.class);
    private static final int COLOR_MAX_VAL = 255;
    private static final String DEFAULT_CSS_COLOR = "white";

    private LCUtils() {
    }

    /**
     * Round a double with 3 decimal
     *
     * @param value the value to round
     * @return rounded value
     */
    public static double tolerantRound(final double value) {
        return tolerantRound(value, 3);
    }

    public static double tolerantRound(final double value, final int scale) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Class part : "Utils"
    // ========================================================================
    public static <T extends IdentifiableComponentI> T findById(final String id, final List<T> componentList) {
        for (T component : componentList) {
            if (id.equals(component.getID())) {
                return component;
            }
        }
        return null;
    }

    /**
     * If the calling thread is on FX Thread, will execute the runnable directly, else, will call {@link Platform#runLater(Runnable)}
     *
     * @param runnable the runnable to execute
     */
    public static void runOnFXThread(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    public static void runOnFXThreadAndWaitFor(final Runnable runnable) {
        runOnFXThreadAndWaitFor(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T runOnFXThreadAndWaitFor(final Supplier<T> toExecute) {
        if (Platform.isFxApplicationThread()) return toExecute.get();
        else {
            AtomicReference<T> ref = new AtomicReference<>();
            Semaphore semaphore = new Semaphore(0);
            LCUtils.runOnFXThread(() -> {
                try {
                    ref.set(toExecute.get());
                } finally {
                    semaphore.release();
                }
            });
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                LOGGER.warn("Couldn't wait for Platform.runLater(...) call to be finished", e);
            }
            return ref.get();
        }
    }

    /**
     * Will run the given task without any executor.
     *
     * @param task the task to be run
     * @throws Exception if the task execution produce a exception
     */

    public static <T> T executeInCurrentThread(final Task<T> task) throws Exception {
        task.run();
        try {
            return task.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw cause instanceof Exception ? (Exception) cause : e;
        }
    }

    public static void safeSleep(final long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (Throwable t) {
            LCUtils.LOGGER.warn("Couldn't sleep the thread {} for {} ms", Thread.currentThread().getName(), sleep, t);
        }
    }

    private static ConcurrentHashMap<String, Future<?>> runningCalls;
    private static ExecutorService executorService;

    public static void debounce(long ms, String callId, Runnable call) {
        if (executorService == null) {
            runningCalls = new ConcurrentHashMap<>(5);
            executorService = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "debounce-thread");
                t.setDaemon(true);
                return t;
            });
        }
        Future<?> previousCall = runningCalls.get(callId);
        if (previousCall != null) previousCall.cancel(true);
        runningCalls.put(callId, executorService.submit(() -> {
            Thread.sleep(ms);
            call.run();
            return null;
        }));
    }

    public static void printStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stackTrace.length && i < 5; i++) {
            System.err.println(stackTrace[i]);
        }
    }

    public static void silentClose(Closeable closaeable) {
        try {
            closaeable.close();
        } catch (IOException e) {
            // Silent
        }
    }

    public static String durationToString(Duration duration) {
        if (duration == null) {
            return "0:00:00";
        } else {
            return durationToString((int) duration.toSeconds());
        }
    }

    public static String durationToString(int durationInSecond) {
        if (durationInSecond <= 0) {
            return "0:00:00";
        } else {
            return String.format("%d:%02d:%02d", durationInSecond / 3600, (durationInSecond % 3600) / 60, (durationInSecond % 60));
        }
    }

    public static String getValidFileName(String fileName, String replacingChar) {
        return StringUtils.isBlank(fileName) ? "" : fileName.replaceAll("[^-_. A-zÀ-ú0-9]", replacingChar);
    }

    public static String getValidFileName(String fileName) {
        return getValidFileName(fileName, "_");
    }

    /**
     * Method that use the internal API to set the stage focusable property.<br>
     * This is a workaround, this should be changed if a public API to change focus state is exposed.
     *
     * @param stage     the stage to change
     * @param focusable the focusable value
     * @deprecated internal API, should be changed ASAP
     */
    @Deprecated
    public static void setFocusableSafe(final Stage stage, final boolean focusable) {
        try {
            List<Window> allSunWindow = Window.getWindows();
            for (Window window : allSunWindow) {
                if (StringUtils.isEquals(stage.getTitle(), window.getTitle())) {
                    window.setFocusable(focusable);
                    LOGGER.info("Focusable state set to {}, enabled {}, focused {}", window.getTitle(), window.isEnabled(), window.isFocused());
                }
            }
        } catch (Throwable t) {
            LCUtils.LOGGER.warn("Couldn't use sun* internal API to change the window properties", t);
        }
    }
    // ========================================================================

    // Class part : "Grid utils"
    // ========================================================================
    public static void invertKeys(final GridPartKeyComponentI source, final GridPartComponentI destination) {
        // Get grids, and create a temp key
        ComponentGridI sourceGrid = source.gridParentProperty().get().getGrid();
        ComponentGridI destGrid = destination.gridParentProperty().get().getGrid();
        GridPartKeyComponent tempKey = new GridPartKeyComponent();
        // Replace it
        sourceGrid.replaceComponent(source, tempKey);
        destGrid.replaceComponent(destination, source);
        sourceGrid.replaceComponent(tempKey, destination);
    }
    // ========================================================================

    // Class part : "Component position calculation"
    // ========================================================================
    public static Pair<Double, Double> getConfigurationPosition(final GridPartComponentI component) {
        Pair<Double, Double> position = new Pair<>(component.layoutXProperty().get(), component.layoutYProperty().get());
        if (component.gridParentProperty().get() != null
                || component.stackParentProperty().get() != null && component.stackParentProperty().get() instanceof GridPartComponentI) {
            GridPartComponentI parent = component.gridParentProperty().get() != null ? component.gridParentProperty().get()
                    : (GridPartComponentI) component.stackParentProperty().get();
            Pair<Double, Double> parentPos = LCUtils.getConfigurationPosition(parent);
            position = new Pair<>(position.getKey() + parentPos.getKey(), position.getValue() + parentPos.getValue());
        } else {
            RootGraphicComponentI root = component.rootParentProperty().get();
            position = new Pair<>(root.xProperty().get() + position.getKey(), root.yProperty().get() + position.getValue());
        }
        return position;
    }

    public static Pair<Double, Double> getLinePosition(final GridComponentI grid, final int lineIndex) {
        Pair<Double, Double> basePos = LCUtils.getConfigurationPosition(grid);
        double x = basePos.getKey() + grid.hGapProperty().get();
        double y = basePos.getValue() + grid.caseHeightProperty().get() * lineIndex + (lineIndex + 1) * grid.vGapProperty().get();
        return new Pair<>(x, y);
    }

    public static Pair<Double, Double> getLineSize(final GridComponentI grid, final int lineIndex, final int lineSpan) {
        double h = grid.caseHeightProperty().get() * lineSpan + (lineSpan - 1) * grid.vGapProperty().get();
        double w = grid.caseWidthProperty().get() * grid.columnCountProperty().get()
                + (grid.columnCountProperty().get() - 1) * grid.hGapProperty().get();
        return new Pair<>(w, h);
    }

    public static Pair<Double, Double> getColumnPosition(final GridComponentI grid, final int columnIndex) {
        Pair<Double, Double> basePos = LCUtils.getConfigurationPosition(grid);
        double x = basePos.getKey() + grid.caseWidthProperty().get() * columnIndex + (columnIndex + 1) * grid.hGapProperty().get();
        double y = basePos.getValue() + grid.vGapProperty().get();
        return new Pair<>(x, y);
    }

    public static Pair<Double, Double> getColumnSize(final GridComponentI grid, final int columnIndex, final int columnSpan) {
        double w = grid.caseWidthProperty().get() * columnSpan + (columnSpan - 1) * grid.hGapProperty().get();
        double h = grid.caseHeightProperty().get() * grid.rowCountProperty().get() + (grid.rowCountProperty().get() - 1) * grid.vGapProperty().get();
        return new Pair<>(w, h);
    }

    public static void computeArcAndStrokeFor(final Rectangle rectangle, final double wantedArc, final double width, final double height,
                                              final double strokeSize) {
        rectangle.setStrokeWidth(strokeSize);
        double arcSize = LCUtils.computeArcAndStroke(wantedArc, width, height, strokeSize);
        rectangle.setArcWidth(arcSize);
        rectangle.setArcHeight(arcSize);
    }

    public static double computeArcAndStroke(final double wantedArc, final double width, final double height, final double strokeSize) {
        return Math.min(Math.min(height / 2.0, width / 2.0), wantedArc) * 2.0;
    }
    // ========================================================================

    // Class part : "Binding"
    // ========================================================================
    public final static Consumer<?> EMPTY_CONSUMER = (o) -> {
    };

    // Should be checked and replaced with V2 implementation
    @Deprecated
    public static <T> ListChangeListener<T> createListChangeListener(final Consumer<T> forEachAdd, final Consumer<T> forEachRemove) {
        ListChangeListener<T> changeListener = (change) -> {
            while (change.next()) {
                if (change.wasAdded() && forEachAdd != null) {
                    List<? extends T> addeds = change.getAddedSubList();
                    for (T added : addeds) {
                        forEachAdd.accept(added);
                    }
                }
                if (change.wasRemoved() && forEachRemove != null) {
                    List<? extends T> removeds = change.getRemoved();
                    for (T removed : removeds) {
                        forEachRemove.accept(removed);
                    }
                }
            }
        };
        return changeListener;
    }

    // This version is correctly implemented for other actions that simple add/remove
    public static <T> ListChangeListener<T> createListChangeListenerV2(final Consumer<T> forEachAdd, final Consumer<T> forEachRemove) {
        return (c) -> {
            while (c.next()) {
                if (c.wasPermutated() || c.wasUpdated()) {
                    // Don't do anything
                } else {
                    consumeEachIn(c.getAddedSubList(), forEachAdd);
                    consumeEachIn(c.getRemoved(), forEachRemove);
                }
            }
        };
    }

    public static <T> void consumeEachIn(List<? extends T> list, Consumer<T> consumer) {
        if (consumer != null && list != null) {
            for (T item : list) {
                consumer.accept(item);
            }
        }
    }

    public static void unbindAndSetNull(final Property<?> prop) {
        prop.unbind();
        prop.setValue(null);
    }

    public static <T> void unbindAndSet(final Property<T> prop, T val) {
        prop.unbind();
        prop.setValue(val);
    }


    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static StringBinding createFormatedIntBinding(ReadOnlyIntegerProperty longProperty, Function<Integer, Double> transformFunction) {
        return Bindings.createStringBinding(() -> {
            final int value = longProperty.get();
            return DOUBLE_DECIMAL_FORMAT.format((double) (transformFunction != null ? transformFunction.apply(value) : value));
        }, longProperty);
    }

    public static StringBinding createDivide1000Binding(ReadOnlyIntegerProperty longProperty) {
        return createFormatedIntBinding(longProperty, v -> v / 1000.0);
    }
    // ========================================================================

    // Class part : "Text utils"
    // ========================================================================
    /*
     * public static Bounds getTextBounds(final String text, TextStyleI textStyle, double factor) {
     * Text t = new Text(text);
     * t.setFont(textStyle.deriveFont(textStyle.fontProperty().get().getSize() * factor));
     * return t.getLayoutBounds();
     * }
     */

    public static Bounds getTextBounds(final String text, final Font font) {
        Text t = new Text(text);
        t.setFont(font);
        return t.getLayoutBounds();
    }
    // ========================================================================

    // Class part : "Converter"
    // ========================================================================
    public static String toCssColor(final Color color) {
        if (color != null) {
            return new StringBuilder(30).append("rgba(").append((int) (color.getRed() * LCUtils.COLOR_MAX_VAL)).append(",")
                    .append((int) (color.getGreen() * LCUtils.COLOR_MAX_VAL)).append(",").append((int) (color.getBlue() * LCUtils.COLOR_MAX_VAL))
                    .append(",").append(color.getOpacity()).append(")").toString();
        } else {
            return LCUtils.DEFAULT_CSS_COLOR;
        }
    }

    public static String toWebColor(final Color c) {
        if (c != null) {
            return String.format((Locale) null, "#%02x%02x%02x",
                    Math.round(c.getRed() * 255),
                    Math.round(c.getGreen() * 255),
                    Math.round(c.getBlue() * 255));
        } else {
            return null;
        }
    }

    /**
     * Compare using web string as double are "to precise" for 0-255 values of colors.
     * Note that it doesn't compare opacity
     */
    public static boolean colorEquals(Color c1, Color c2) {
        if (c1 == c2) return true;
        else if (c1 == null || c2 == null) return false;
        else return toWebColor(c1).equals(toWebColor(c2));
    }

    public static boolean isSupportedImage(final File imgFile) {
        for (int i = 0; i < LCConstant.IMAGE_EXTENSIONS.length; i++) {
            if (StringUtils.endsWithIgnoreCase(FileNameUtils.getExtension(imgFile), LCConstant.IMAGE_EXTENSIONS[i])) {
                return true;
            }
        }
        return false;
    }
    // ========================================================================

    // Class part : "Configuration child"
    // ========================================================================

    /**
     * Bind the callback to the configuration parent of a given configuration child.
     *
     * @param configChildProp                   the configuration child, that can change, as its configuration parent.
     * @param configurationAllComponentCallback the callback that will be call each time configuration change on the configuration child.<br>
     *                                          The parameter are the configuration components.
     * @param addedCallback                     the callback called each time a component is added to the configuration
     * @param removedCallback                   the callback called each time a component is removed from the configuration
     */
    public static <T extends DisplayableComponentI> void addComponentCallback(
            final ObjectProperty<? extends ConfigurationChildComponentI> configChildProp,
            final Consumer<ObservableMap<String, DisplayableComponentI>> configurationAllComponentCallback, final Consumer<T> addedCallback,
            final Consumer<T> removedCallback) {
        // Listen for the configurations changes
        ChangeListener<LCConfigurationI> configChangeListener = (obs, ov, nv) -> {
            if (ov != null) {
                ov.removeComponentCallbacks(addedCallback, removedCallback);
            }
            if (nv != null) {
                configurationAllComponentCallback.accept(nv.getAllComponent());
                nv.addComponentCallbacks(addedCallback, removedCallback);
            }
        };
        configChildProp.addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.configurationParentProperty().removeListener(configChangeListener);
            }
            if (nv != null) {
                nv.configurationParentProperty().addListener(configChangeListener);
                // Fix #112 : if the config parent is already set, listener should be triggered
                LCConfigurationI existingConfig = nv.configurationParentProperty().get();
                if (existingConfig != null) {
                    configChangeListener.changed(nv.configurationParentProperty(), null, existingConfig);
                }
            }
        });
        // Initial configuration value
        if (configChildProp.get() != null) {
            configChangeListener.changed(configChildProp.get().configurationParentProperty(), null,
                    configChildProp.get().configurationParentProperty().get());
        }
    }
    // ========================================================================

    // Class part : "Lang utilities"
    // ========================================================================
    public static int nullToZero(final Integer i) {
        return i != null ? i : 0;
    }

    public static double nullToZero(final Double d) {
        return d != null ? d : 0;
    }

    public static int nullToZeroInt(final Number n) {
        return n != null ? n.intValue() : 0;
    }

    public static String nullToEmpty(final String str) {
        return str != null ? str : "";
    }

    // public static int nullToBoundInt(final Number n, int min, int max) {
    // return Math.max(min, Math.min(max, n != null ? n.intValue() : 0));
    // }

    // public static double nullToBoundDouble(final Number n, double min, double max) {
    // return Math.max(min, Math.min(max, n != null ? n.doubleValue() : 0));
    // }

    public static double toBoundDouble(final double n, double min, double max) {
        return Math.max(min, Math.min(max, n));
    }

    public static int toBoundInt(final int n, int min, int max) {
        return Math.max(min, Math.min(max, n));
    }

    public static double nullToZeroDouble(final Number n) {
        return n != null ? n.doubleValue() : 0;
    }

    public static boolean isTrue(final Boolean b) {
        return b != null && b;
    }

    public static boolean safeEquals(final Object o1, final Object o2) {
        return Objects.equals(o1, o2);
    }

    public static boolean nullToFalse(Boolean value) {
        return value != null ? value : false;
    }

    public static String safeTrimToEmpty(final String str) {
        return str != null ? str.trim() : "";
    }

    public static DoubleBinding bindToValueOrIfInfinityOrNan(ObservableDoubleValue binding, double or) {
        return Bindings.createDoubleBinding(() -> {
            double val = binding.get();
            return Double.isFinite(val) ? val : or;
        }, binding);
    }

    public static boolean safeParseBoolean(String str) {
        return Boolean.parseBoolean(StringUtils.stripToEmpty(str));
    }
    // ========================================================================

    // Class part : "Component search"
    // ========================================================================
    // TODO : replace with existing implementation of fuzzy search algo ?
    // TODO : check algo in ImageDictionaries > merge

    public static final double SIMILARITY_EXACT_MATCH = 100.0, SIMILARITY_START_WITH = 30.0, SIMILARITY_EXACT_TERM_MATCH = 5.0, SIMILARITY_CONTAINS = 1.0;

    public static double getSimilarityScoreFor(String termRaw, DisplayableComponentI comp) {
        return getSimilarityScoreFor(termRaw, comp, getDisplayableComponentSearchGetter(comp));
    }

    public static <T> double getSimilarityScoreFor(String termRaw, T comp, Function<T, org.lifecompanion.framework.utils.Pair<String, Double>>... sourceNameAndFactorGetters) {
        // Clean input terms : remove accents and lower case
        termRaw = StringUtils.stripAccents(StringUtils.toLowerCase(safeTrimToEmpty(termRaw)));
        final String[] splitTerms = termRaw.split(" ");
        double factorToDivideContains = Arrays.stream(splitTerms).mapToDouble(String::length).sum();

        double score = 0.0;
        for (Function<T, org.lifecompanion.framework.utils.Pair<String, Double>> sourceNameAndFactorGetter : sourceNameAndFactorGetters) {
            final org.lifecompanion.framework.utils.Pair<String, Double> nameAndFactor = sourceNameAndFactorGetter.apply(comp);
            String source = StringUtils.stripAccents(StringUtils.toLowerCase(safeTrimToEmpty(nameAndFactor.getLeft())));
            if (StringUtils.isEquals(source, termRaw)) {
                score += SIMILARITY_EXACT_MATCH * nameAndFactor.getRight();
            }
            if (source.startsWith(termRaw)) {
                score += SIMILARITY_START_WITH * nameAndFactor.getRight();
            }
            final String[] sourceTerms = source.split(" ");
            for (String splitTerm : splitTerms) {
                double factor = splitTerm.length() / factorToDivideContains;
                for (String sourceTerm : sourceTerms) {
                    score += StringUtils.isEquals(sourceTerm, splitTerm) ? factor * SIMILARITY_EXACT_TERM_MATCH : 0.0;
                }
                score += StringUtils.containsIgnoreCase(source, splitTerm) ? factor * SIMILARITY_CONTAINS : 0.0;
            }
        }
        return score;
    }

    private static Function<DisplayableComponentI, org.lifecompanion.framework.utils.Pair<String, Double>>[] getDisplayableComponentSearchGetter(DisplayableComponentI comp) {
        List<Function<DisplayableComponentI, org.lifecompanion.framework.utils.Pair<String, Double>>> functions = new ArrayList<>(3);
        // Base search : on name
        functions.add(conf -> org.lifecompanion.framework.utils.Pair.of(comp.nameProperty().get(), 1.0));
        // For component with images, use the image name
        if (comp instanceof ImageUseComponentI) {
            ImageUseComponentI imageUseComponent = (ImageUseComponentI) comp;
            if (imageUseComponent.imageVTwoProperty().get() != null) {
                functions.add(conf -> org.lifecompanion.framework.utils.Pair.of(imageUseComponent.imageVTwoProperty().get().getName(), 0.8));
            }
        }
        // For component with actions, check on some textual actions
        if (comp instanceof UseActionTriggerComponentI) {
            UseActionTriggerComponentI useActionTriggerComponent = (UseActionTriggerComponentI) comp;
            WriteAndSpeakTextAction writeAndSpeakTextAction = useActionTriggerComponent.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, WriteAndSpeakTextAction.class);
            if (writeAndSpeakTextAction != null) {
                functions.add(conf -> org.lifecompanion.framework.utils.Pair.of(writeAndSpeakTextAction.textToWriteProperty().get(), 0.8));
                functions.add(conf -> org.lifecompanion.framework.utils.Pair.of(writeAndSpeakTextAction.textToSpeakProperty().get(), 0.8));
            }
        }

        return functions.toArray(new Function[0]);
    }
    // ========================================================================

    // Class part : "Key Option"
    // ========================================================================
    public static <T extends KeyOptionI> int findKeyOptionsByGrid(final Class<T> optionType, final LCConfigurationI configuration,
                                                                  final Map<GridComponentI, List<T>> gridMaps, final Predicate<GridPartKeyComponentI> keyPredicate) {
        ObservableMap<String, DisplayableComponentI> allComponent = configuration.getAllComponent();
        // Find all keys grouped by their highest grid parent
        final Map<GridComponentI, List<GridPartKeyComponentI>> keysByHighestGridParent = allComponent.values().stream()
                .filter(c -> c instanceof GridPartKeyComponentI)
                .map(c -> (GridPartKeyComponentI) c)
                .filter(key -> {
                    KeyOptionI option = key.keyOptionProperty().get();
                    return option != null && optionType.isAssignableFrom(option.getClass()) && (keyPredicate == null || keyPredicate.test(key));
                }).collect(Collectors.groupingBy(LCUtils::findHighestGridBeforeStackParent));
        // Sort them top-left > bottom-right
        AtomicInteger count = new AtomicInteger(0);
        keysByHighestGridParent.forEach((grid, keys) -> {
            count.set(Math.max(count.get(), keys.size()));
            gridMaps.put(grid, keys.stream().sorted(positionInGridParentIncludingParentComparator()).map(k -> (T) k.keyOptionProperty().get()).collect(Collectors.toList()));
        });
        return count.get();
    }

    private static final Comparator<GridPartComponentI> positionInGridParentIncludingParentComparator = (g1, g2) -> {
        final Pair<Integer, Integer> g1rc = computeRowColumnIndexWithParentIncludedFor(g1);
        final Pair<Integer, Integer> g2rc = computeRowColumnIndexWithParentIncludedFor(g2);
        if (g1rc.getKey() < g2rc.getKey()) {
            return -1;
        } else if (g1rc.getKey().equals(g2rc.getKey())) {
            if (g1rc.getValue() < g2rc.getValue()) {
                return -1;
            } else if (g1rc.getValue().equals(g2rc.getValue())) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    };

    public static Comparator<GridPartComponentI> positionInGridParentIncludingParentComparator() {
        return positionInGridParentIncludingParentComparator;
    }

    private static Pair<Integer, Integer> computeRowColumnIndexWithParentIncludedFor(GridPartComponentI gridPartComponent) {
        GridComponentI gridParent = gridPartComponent.gridParentProperty().get();
        int rowIndex = gridPartComponent.rowProperty().get(), columnIndex = gridPartComponent.columnProperty().get(), factor = 2;
        while (gridParent != null) {
            rowIndex += factor * gridParent.rowProperty().get();
            columnIndex += factor * gridParent.columnProperty().get();
            factor = factor * 2;
            gridParent = gridParent.gridParentProperty().get();
        }
        return new Pair<>(rowIndex, columnIndex);
    }

    private static GridComponentI findHighestGridBeforeStackParent(GridPartKeyComponentI key) {
        GridComponentI gridParent = key.gridParentProperty().get();
        while (gridParent != null) {
            final GridComponentI newGP = gridParent.gridParentProperty().get();
            if (newGP == null) return gridParent;
            gridParent = newGP;
        }
        return null;
    }
    // ========================================================================

    // Class part : "Image utils"
    // ========================================================================

    /**
     * Replace a color in a given image
     *
     * @param inputImage     the input image
     * @param colorToReplace the color to replace (with a threshold)
     * @param replacingColor the color replacing the colorToReplace
     * @param threshold      the threshold to detect the color to replace
     * @return the image with color replaced by a transparent white
     */
    public static Image replaceColorInImage(final Image inputImage, final Color colorToReplace, final Color replacingColor, final int threshold) {
        int wO = colorToReplace == null ? 255 : (int) (colorToReplace.getOpacity() * 255);//Issue #186, opacity should be tested
        int wR = colorToReplace == null ? 255 : (int) (colorToReplace.getRed() * 255);
        int wG = colorToReplace == null ? 255 : (int) (colorToReplace.getGreen() * 255);
        int wB = colorToReplace == null ? 255 : (int) (colorToReplace.getBlue() * 255);
        //colorToReplace.getOpacity();
        int destArgb = LCUtils.convertTo32Argb(replacingColor != null ? replacingColor : Color.TRANSPARENT);
        int imgWidth = (int) inputImage.getWidth();
        int imgHeight = (int) inputImage.getHeight();
        WritableImage outputImage = new WritableImage(imgWidth, imgHeight);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                int argb = reader.getArgb(x, y);
                int o = argb >> 24 & 0xFF;
                int r = argb >> 16 & 0xFF;
                int g = argb >> 8 & 0xFF;
                int b = argb & 0xFF;
                if (LCUtils.isEgalsTo(o, wO, threshold) && LCUtils.isEgalsTo(r, wR, threshold) && LCUtils.isEgalsTo(g, wG, threshold)
                        && LCUtils.isEgalsTo(b, wB, threshold)) {
                    argb = destArgb;
                }
                writer.setArgb(x, y, argb);
            }
        }
        return outputImage;
    }

    public static int convertTo32Argb(final Color c) {
        int a = (int) Math.round(c.getOpacity() * 255);
        int r = (int) Math.round(c.getRed() * 255);
        int g = (int) Math.round(c.getGreen() * 255);
        int b = (int) Math.round(c.getBlue() * 255);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static boolean isEgalsTo(final int toTest, final int value, final int threshold) {
        return toTest >= value - threshold && toTest <= value + threshold;
    }

    public static File getTempDir(String name) {
        return new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "tmp" + File.separator + name + "-" + System.currentTimeMillis() + File.separator);
    }


    // ========================================================================

    // TREE
    //========================================================================
    public static void exploreTree(TreeDisplayableComponentI node, Consumer<TreeDisplayableComponentI> consumer) {
        if (node != null) {
            consumer.accept(node);
        }
        if (!node.isNodeLeaf() && node.getChildrenNode() != null) {
            node.getChildrenNode().forEach(n -> exploreTree(n, consumer));
        }
    }
    //========================================================================
}
