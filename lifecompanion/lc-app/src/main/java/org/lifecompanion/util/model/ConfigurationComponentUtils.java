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

package org.lifecompanion.util.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WriteAndSpeakTextAction;
import org.lifecompanion.model.impl.configurationcomponent.GridPartKeyComponent;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.javafx.ImageUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConfigurationComponentUtils {

    public static final double SIMILARITY_EXACT_MATCH = 100.0;
    public static final double SIMILARITY_START_WITH = 30.0;
    public static final double SIMILARITY_EXACT_TERM_MATCH = 5.0;
    public static final double SIMILARITY_CONTAINS = 1.0;

    public static final Comparator<GridPartComponentI> POSITION_IN_GRID_PARENT_INCLUDING_PARENT_COMPARATOR = (g1, g2) -> {
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

    public static double getSimilarityScoreFor(String termRaw, DisplayableComponentI comp) {
        return getSimilarityScoreFor(termRaw, comp, getDisplayableComponentSearchGetter(comp));
    }

    public static <T> double getSimilarityScoreFor(String termRaw, T comp, Function<T, org.lifecompanion.framework.utils.Pair<String, Double>>... sourceNameAndFactorGetters) {
        // Clean input terms : remove accents and lower case
        termRaw = StringUtils.stripAccents(StringUtils.toLowerCase(LangUtils.safeTrimToEmpty(termRaw)));
        final String[] splitTerms = termRaw.split(" ");
        double factorToDivideContains = Arrays.stream(splitTerms).mapToDouble(String::length).sum();

        double score = 0.0;
        for (Function<T, org.lifecompanion.framework.utils.Pair<String, Double>> sourceNameAndFactorGetter : sourceNameAndFactorGetters) {
            final org.lifecompanion.framework.utils.Pair<String, Double> nameAndFactor = sourceNameAndFactorGetter.apply(comp);
            String source = StringUtils.stripAccents(StringUtils.toLowerCase(LangUtils.safeTrimToEmpty(nameAndFactor.getLeft())));
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
                }).collect(Collectors.groupingBy(ConfigurationComponentUtils::findHighestGridBeforeStackParent));
        // Sort them top-left > bottom-right
        AtomicInteger count = new AtomicInteger(0);
        keysByHighestGridParent.forEach((grid, keys) -> {
            count.set(Math.max(count.get(), keys.size()));
            gridMaps.put(grid, keys.stream().sorted(positionInGridParentIncludingParentComparator()).map(k -> (T) k.keyOptionProperty().get()).collect(Collectors.toList()));
        });
        return count.get();
    }

    public static Comparator<GridPartComponentI> positionInGridParentIncludingParentComparator() {
        return POSITION_IN_GRID_PARENT_INCLUDING_PARENT_COMPARATOR;
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

    public static void exploreTree(TreeDisplayableComponentI node, Consumer<TreeDisplayableComponentI> consumer) {
        if (node != null) {
            consumer.accept(node);
        }
        if (!node.isNodeLeaf() && node.getChildrenNode() != null) {
            node.getChildrenNode().forEach(n -> exploreTree(n, consumer));
        }
    }

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

    public static void exploreComponentViewChildrenToUnbind(Node node) {
        if (node instanceof Pane) {
            for (Node child : ((Pane) node).getChildren()) {
                if (child instanceof ComponentViewI) {
                    ((ComponentViewI<?>) child).unbindComponentAndChildren();
                }
            }
        }
    }

    public static GridPartComponentI getNextComponentInGrid(GridPartComponentI comp, boolean loop) {
        GridComponentI gridParent = comp.gridParentProperty().get();
        if (gridParent != null) {
            int nextColumn = comp.columnProperty().get() + comp.columnSpanProperty().get();
            int nextRow = comp.rowProperty().get() + 1;
            if (nextColumn < gridParent.columnCountProperty().get()) {
                return gridParent.getGrid().getComponent(comp.rowProperty().get(), nextColumn);
            } else if (nextRow < gridParent.rowCountProperty().get()) {
                return gridParent.getGrid().getComponent(nextRow, 0);
            } else if (loop) {
                return gridParent.getGrid().getComponent(0, 0);
            }
        }
        return null;
    }

    public static GridPartComponentI getPreviousComponent(GridPartComponentI comp, boolean loop) {
        GridComponentI gridParent = comp.gridParentProperty().get();
        if (gridParent != null) {
            int previousColumn = comp.columnProperty().get() - comp.columnSpanProperty().get();
            int previousRow = comp.rowProperty().get() - 1;
            if (previousColumn >= 0) {
                return gridParent.getGrid().getComponent(comp.rowProperty().get(), previousColumn);
            } else if (previousRow >= 0) {
                return gridParent.getGrid().getComponent(previousRow, gridParent.getGrid().getColumn() - 1);
            } else if (loop) {
                return gridParent.getGrid().getComponent(gridParent.getGrid().getRow() - 1, gridParent.getGrid().getColumn() - 1);
            }
        }
        return null;
    }

    public static <T> T findById(LCConfigurationI configuration, String id, Class<? extends T> componentType) {
        return (T) configuration.getAllComponent()
                .values()
                .stream()
                .filter(c -> componentType.isAssignableFrom(c.getClass()))
                .filter(c -> StringUtils.isEquals(c.getID(), id))
                .findAny()
                .orElse(null);
    }

    public static void bindImageViewWithImageUseComponent(ImageView imageView, ImageUseComponentI imageUseComponent) {
        imageView.preserveRatioProperty().bind(imageUseComponent.preserveRatioProperty());
        imageView.rotateProperty().bind(imageUseComponent.rotateProperty());
        imageView.scaleXProperty().bind(imageUseComponent.scaleXProperty());
        imageView.scaleYProperty().bind(imageUseComponent.scaleYProperty());
        imageView.effectProperty().bind(Bindings.createObjectBinding(() -> {
            if (imageUseComponent.colourToGreyProperty().get()) {
                return new ColorAdjust(0.0, -1.0, 0.0, 0.0);
            } else {
                return null;
            }
        }, imageUseComponent.colourToGreyProperty()));
        imageView.viewportProperty().bind(imageUseComponent.viewportProperty());
        imageView.imageProperty().bind(Bindings.createObjectBinding(() -> {
        Image img = imageUseComponent.loadedImageProperty().get();
        if (img == null) {
            return null;
        } else {
            if (imageUseComponent.enableReplaceColorProperty().get()) {
                img = ImageUtils.replaceColorInImage(img, imageUseComponent.colorToReplaceProperty().get(), imageUseComponent.replacingColorProperty().get(),
                        imageUseComponent.replaceColorThresholdProperty().get());
            }
            if (imageUseComponent.enableRemoveBackgroundProperty().get()) {
                img = ImageUtils.removeBackground(img, imageUseComponent.replaceRemoveBackgroundThresholdProperty().get());
            }
            return img;
        }
    }, imageUseComponent.loadedImageProperty(), imageUseComponent.enableReplaceColorProperty(), imageUseComponent.colorToReplaceProperty(),
            imageUseComponent.replacingColorProperty(), imageUseComponent.replaceColorThresholdProperty(), imageUseComponent.enableRemoveBackgroundProperty(), imageUseComponent.replaceRemoveBackgroundThresholdProperty()));
    }

    public static void unbindImageViewFromImageUseComponent(ImageView imageView) {
        BindingUtils.unbindAndSetNull(imageView.imageProperty());
        BindingUtils.unbindAndSet(imageView.preserveRatioProperty(), true);
        BindingUtils.unbindAndSet(imageView.rotateProperty(), 0.0);
        BindingUtils.unbindAndSet(imageView.scaleXProperty(), 1.0);
        BindingUtils.unbindAndSet(imageView.scaleYProperty(), 1.0);
        BindingUtils.unbindAndSetNull(imageView.effectProperty());
        BindingUtils.unbindAndSetNull(imageView.viewportProperty());
    }

}
