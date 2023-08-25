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
package org.lifecompanion.model.api.configurationcomponent;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequencesI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorHolderI;
import org.lifecompanion.model.api.selectionmode.DirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.style.GridStyleUserI;
import org.lifecompanion.model.api.style.KeyStyleUserI;
import org.lifecompanion.model.api.style.TextDisplayerStyleUserI;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represent the unique top component of a configuration in LifeCompanion.<br>
 * This is where root component are added, etc...<br>
 * This is the source of loading/saving a configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface LCConfigurationI extends DisplayableComponentI, VoiceSynthesizerUserI, SelectionModeUserI, UseEventGeneratorHolderI, KeyStyleUserI, GridStyleUserI, TextDisplayerStyleUserI {

    /**
     * Set containing manually added plugin dependencies.<br>
     * This can be directly modified by plugins or other API to flag this configuration as the plugin user (by adding the plugin ID to the set).<br>
     * Most of the plugins doesn't require this as plugin dependencies are directly computed on serialization.
     *
     * @return modifiable set contains plugin dependencies ids
     */
    Set<String> getManualPluginDependencyIds();

    <T extends PluginConfigPropertiesI> T getPluginConfigProperties(String pluginId, Class<T> exceptedType);

    /**
     * @return a property that count every action on this configuration that are not saved.<br>
     * Must be updated on each configuration action.
     */
    IntegerProperty unsavedActionProperty();

    ObjectProperty<UserActionSequencesI> userActionSequencesProperty();

    /**
     * @return a list of all the root children for this configuration.
     */
    ObservableList<RootGraphicComponentI> getChildren();

    /**
     * @return a map that contains every child and sub child of this configuration.<br>
     * This map is useful to get a component by its id.
     */
    ObservableMap<String, DisplayableComponentI> getAllComponent();

    /**
     * @return a property that define this configuration width.<br>
     * This width is managed by the configuration implementation itself, it can be fixed, but it can also change
     */
    DoubleProperty widthProperty();

    ReadOnlyDoubleProperty automaticWidthProperty();

    ReadOnlyDoubleProperty computedWidthProperty();

    DoubleProperty displayedConfigurationScaleXProperty();

    DoubleProperty displayedConfigurationScaleYProperty();

    DoubleProperty configurationScaleInEditModeProperty();

    /**
     * @return a property that define this configuration height.<br>
     * This height is managed by the configuration implementation itself, it can be fixed, but it can also change
     */
    DoubleProperty heightProperty();

    ReadOnlyDoubleProperty automaticHeightProperty();

    ReadOnlyDoubleProperty computedHeightProperty();

    /**
     * @return a property that should be set to true if the configuration size shouldn't be computed with its children.<br>
     * If the property is true, the width and height property can be manually changed.
     */
    BooleanProperty fixedSizeProperty();

    /**
     * @return a property that can be enable/disable to use a grid when placing the component of this configuration.<br>
     * A grid will allow only location and size relative to its size ( {@link #gridSizeProperty()} )
     */
    BooleanProperty useGridProperty();

    /**
     * @return a property that define the grid size, the grid size define how component can use value to their location/size.<br>
     * Property have no use if {@link #useGridProperty()} is false
     */
    IntegerProperty gridSizeProperty();

    /**
     * Currently selection mode use by this configuration.<br>
     * Should never be changed manually. It's the selection mode controller that will set the needed selection model.
     *
     * @return the property that contains the currently used selection mode.<br>
     * This can change while configuration is used, if a grid have a different selection mode than the previous selected grid.
     */
    ObjectProperty<SelectionModeI> selectionModeProperty();

    ObjectProperty<DirectSelectionModeI> directSelectionOnMouseOnScanningSelectionModeProperty();

    BooleanProperty hideMainSelectionModeViewProperty();

    /**
     * @return the first component that should be selected/scanned by the current configuration when the user switch to use mode.
     */
    ReadOnlyObjectProperty<GridComponentI> firstSelectionPartProperty();

    StringProperty firstSelectionPartIdProperty();

    /**
     * To add a callback to the current configuration to known when a component is added or removed.<br>
     *
     * @param addedCallback   the callback to call when the component with the id is added.
     * @param removedCallback the callback to call when the component with the id is removed
     */
    <T extends DisplayableComponentI> void addComponentCallbacks(Consumer<T> addedCallback, Consumer<T> removedCallback);

    /**
     * To remove previously added component callback.<br>
     * If the callback doesn't exist, this will do nothing.
     *
     * @param addedCallback   the previously added "add" callback
     * @param removedCallback the previously added "remove" callback
     */
    <T extends DisplayableComponentI> void removeComponentCallbacks(Consumer<T> addedCallback, Consumer<T> removedCallback);

    /**
     * @return the wanted frame width for this configuration.<br>
     * This will be use only if the full screen is false
     */
    DoubleProperty frameWidthProperty();

    ReadOnlyDoubleProperty computedFrameWidthProperty();

    ReadOnlyDoubleProperty automaticFrameWidthProperty();

    /**
     * @return the wanted frame height for this configuration.<br>
     * This will be use only if the full screen is false
     */
    DoubleProperty frameHeightProperty();

    ReadOnlyDoubleProperty computedFrameHeightProperty();

    ReadOnlyDoubleProperty automaticFrameHeightProperty();

    ObjectProperty<StageMode> stageModeOnLaunchProperty();

    /**
     * @return a property that contains the frame position to set on launch
     */
    ObjectProperty<FramePosition> framePositionOnLaunchProperty();

    /**
     * @return the background color for this configuration
     */
    ObjectProperty<Color> backgroundColorProperty();

    /**
     * @return to keep the configuration ratio when frame is resized
     */
    BooleanProperty keepConfigurationRatioProperty();

    /**
     * @return the frame opacity for this configuration
     */
    DoubleProperty frameOpacityProperty();

    /**
     * If this configuration is noted as a virtual keyboard configuration.<br>
     * This mean that every written key will be written both in keyboard, and with the virtual keyboard.
     *
     * @return true if the configuration is use as a virtual keyboard
     */
    BooleanProperty virtualKeyboardProperty();

    /**
     * @return the virtual mouse parameters associated to this configuration.
     */
    VirtualMouseParameterI getVirtualMouseParameters();

    /**
     * @return the prediction parameter for this configuration
     */
    PredictionParameterI getPredictionParameters();

    /**
     * @return entries that are saved for use mode (can be used to restore current typed text, or to save it)
     */
    List<WriterEntryI> getUseModeWriterEntries();

    /**
     * @return list of available key list categories
     */
    ObjectProperty<KeyListNodeI> rootKeyListNodeProperty();

    void clearAllComponentViewCache();
}
