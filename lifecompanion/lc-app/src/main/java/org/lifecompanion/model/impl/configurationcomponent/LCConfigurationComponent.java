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
package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.*;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequencesI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventManagerI;
import org.lifecompanion.model.api.selectionmode.DirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeParameterI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.style.KeyCompStyleI;
import org.lifecompanion.model.api.style.ShapeCompStyleI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerParameterI;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListNode;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.UserActionSequences;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.style.StyleController;
import org.lifecompanion.util.debug.ConfigurationMemoryLeakChecker;
import org.lifecompanion.model.impl.selectionmode.SelectionModeParameter;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.model.impl.categorizedelement.useevent.UseEventManager;
import org.lifecompanion.model.impl.voicesynthesizer.VoiceSynthesizerParameter;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.impl.style.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class represent a configuration that can be open and use in the software.<br>
 * A configuration is a set of values and component that define its behavior.<br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCConfigurationComponent extends CoreDisplayableComponentBaseImpl implements LCConfigurationI {

    /**
     * List of all the component displayed into this configuration
     */
    private final ObservableList<RootGraphicComponentI> children;

    private final ObjectProperty<KeyListNodeI> rootKeyListNode;

    /**
     * The count of actions done since the last save of this configuration.
     */
    private final transient IntegerProperty unsavedAction;

    /**
     * Map that contains every component of this configuration (direct and undirect children)
     */
    private final ObservableMap<String, DisplayableComponentI> allComponents;

    /**
     * Configuration size
     */
    private final DoubleProperty width, height;

    /**
     * Automatic size : automatic size (computed from components) / computed (real value to use : use user value (width/height) or automatic size
     */
    private transient final DoubleProperty automaticWidth, computedWidth, automaticHeight, computedHeight;

    /**
     * If the configuration size is automatically computed
     */
    private final BooleanProperty fixedSize;

    /**
     * If grid is use, the size of the grid.
     */
    private final IntegerProperty gridSize;

    /**
     * If this configuration use a grid to layout the component
     */
    private final BooleanProperty useGrid;

    /**
     * If the configuration mode should be secured
     */
    private final BooleanProperty securedConfigurationMode;

    /**
     * Voice synthesizer parameter
     */
    private final VoiceSynthesizerParameter voiceSynthesizerParameter;

    /**
     * Current Selection mode in configuration
     */
    private final ObjectProperty<SelectionModeI> selectionMode;

    private final ObjectProperty<DirectSelectionModeI> directSelectionOnMouseOnScanningSelectionMode;

    /**
     * The selection mode parameters.
     */
    private final SelectionModeParameter selectionModeParameter;

    /**
     * Property that contains a reference on this configuration (this object)
     */
    private final ObjectProperty<LCConfigurationI> thisProperty;

    /**
     * First part to be scanned in configuration
     */
    private final ComponentHolder<GridComponentI> firstSelectionPart;

    /**
     * First part ID
     */
    private final StringProperty firstSelectionPartId;

    private final DoubleProperty frameWidth, frameHeight;

    private final transient DoubleProperty automaticFrameWidth, computedFrameWidth, automaticFrameHeight, computedFrameHeight;
    private final transient DoubleProperty displayedConfigurationScaleX, displayedConfigurationScaleY;

    private final BooleanProperty fullScreenOnLaunch;

    private final BooleanProperty virtualKeyboard;

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> backgroundColor;

    private final DoubleProperty frameOpacity;

    private final BooleanProperty useParentSelectionMode;

    private final BooleanProperty keepConfigurationRatio;

    private final transient BooleanProperty canUseParentSelectionModeConfiguration;

    private final UseEventManagerI eventManager;

    private final VirtualMouseParameterI virtualMouseParameter;

    private final PredictionParameterI predictionParameter;

    @XMLGenericProperty(FramePosition.class)
    private final ObjectProperty<FramePosition> framePositionOnLaunch;

    private final List<WriterEntryI> useModeWriterEntries;

    private final Set<String> manualPluginDependencyIds;

    private final ObjectProperty<UserActionSequencesI> userActionSequences;

    private final Map<String, PluginConfigPropertiesI> pluginsConfigProperties;

    private final ShapeCompStyleI gridShapeStyle;
    private final KeyCompStyleI keyStyle;
    private final TextCompStyleI keyTextStyle;
    private final ShapeCompStyleI textDisplayerShapeStyle;
    private final TextCompStyleI textDisplayerTextStyle;

    private final transient BooleanProperty hideMainSelectionModeView;

    private final DoubleProperty configurationScaleInEditMode;

    /**
     * Create a empty configuration
     */
    public LCConfigurationComponent() {
        super();
        this.thisProperty = new SimpleObjectProperty<>(this);
        this.children = FXCollections.observableList(new ArrayList<>());
        this.addCallbacks = new ArrayList<>();
        this.removeCallbacks = new ArrayList<>();
        this.voiceSynthesizerParameter = new VoiceSynthesizerParameter();
        this.allComponents = FXCollections.observableHashMap();
        this.unsavedAction = new SimpleIntegerProperty(this, "unsavedAction");
        this.width = new SimpleDoubleProperty(this, "width", 0.0);
        this.automaticWidth = new SimpleDoubleProperty(this, "automaticWidth", 0.0);
        this.computedWidth = new SimpleDoubleProperty(this, "computedWidth", 0.0);
        this.height = new SimpleDoubleProperty(this, "height", 0.0);
        this.automaticHeight = new SimpleDoubleProperty(this, "automaticHeight", 0.0);
        this.computedHeight = new SimpleDoubleProperty(this, "computedHeight", 0.0);
        this.framePositionOnLaunch = new SimpleObjectProperty<>(this, "framePositionOnLaunch", FramePosition.CENTER);
        this.fixedSize = new SimpleBooleanProperty(this, "fixedSize", false);
        this.virtualKeyboard = new SimpleBooleanProperty(this, "virtualKeyboard", false);
        this.securedConfigurationMode = new SimpleBooleanProperty(this, "securedConfigurationMode", false);
        this.useGrid = new SimpleBooleanProperty(this, "useGrid", true);
        this.useParentSelectionMode = new SimpleBooleanProperty(this, "useParentSelectionMode", false);
        this.canUseParentSelectionModeConfiguration = new SimpleBooleanProperty(this, "parentSelectionModeEnabled", false);
        this.gridSize = new SimpleIntegerProperty(this, "gridSize", 15);
        this.selectionMode = new SimpleObjectProperty<>(this, "selectionMode");
        this.directSelectionOnMouseOnScanningSelectionMode = new SimpleObjectProperty<>();
        hideMainSelectionModeView = new SimpleBooleanProperty(false);
        this.selectionModeParameter = new SelectionModeParameter();
        this.firstSelectionPartId = new SimpleStringProperty(this, "firstSelectionPartId");
        this.frameWidth = new SimpleDoubleProperty(this, "frameWidth", 0.0);
        this.automaticFrameWidth = new SimpleDoubleProperty(this, "automaticFrameWidth", 0.0);
        this.computedFrameWidth = new SimpleDoubleProperty(this, "computedFrameWidth", 0.0);
        this.frameHeight = new SimpleDoubleProperty(this, "frameHeight", 0.0);
        this.automaticFrameHeight = new SimpleDoubleProperty(this, "automaticFrameHeight", 0.0);
        this.computedFrameHeight = new SimpleDoubleProperty(this, "computedFrameHeight", 0.0);
        this.fullScreenOnLaunch = new SimpleBooleanProperty(this, "fullScreenOnLaunch", true);
        this.keepConfigurationRatio = new SimpleBooleanProperty(this, "keepConfigurationRatio", true);
        this.backgroundColor = new SimpleObjectProperty<>(this, "backgroundColor", Color.web("#E6E6E6"));
        this.frameOpacity = new SimpleDoubleProperty(this, "frameOpacity", 1.0);
        this.displayedConfigurationScaleY = new SimpleDoubleProperty(this, "displayedConfigurationScaleY", 1.0);
        this.displayedConfigurationScaleX = new SimpleDoubleProperty(this, "displayedConfigurationScaleX", 1.0);
        this.configurationScaleInEditMode = new SimpleDoubleProperty(1.0);
        this.eventManager = new UseEventManager(this);
        this.virtualMouseParameter = new VirtualMouseParameter();
        this.predictionParameter = new PredictionParameter();
        this.manualPluginDependencyIds = new HashSet<>(5);
        this.useModeWriterEntries = new ArrayList<>(20);
        this.rootKeyListNode = new SimpleObjectProperty<>(new KeyListNode());
        this.userActionSequences = new SimpleObjectProperty<>(new UserActionSequences());
        // Styles : create and bind to default values
        this.gridShapeStyle = new GridShapeCompStyle();
        this.gridShapeStyle.parentComponentStyleProperty().set(StyleController.INSTANCE.getDefaultShapeStyleForGrid());
        this.keyStyle = new KeyCompStyle();
        this.keyStyle.parentComponentStyleProperty().set(StyleController.INSTANCE.getDefaultKeyStyle());
        this.keyTextStyle = new KeyTextCompStyle();
        this.keyTextStyle.parentComponentStyleProperty().set(StyleController.INSTANCE.getDefaultTextStyleForKey());
        this.textDisplayerShapeStyle = new TextDisplayerShapeCompStyle();
        this.textDisplayerShapeStyle.parentComponentStyleProperty().set(StyleController.INSTANCE.getDefaultShapeStyleForTextEditor());
        this.textDisplayerTextStyle = new TextDisplayerTextCompStyle();
        this.textDisplayerTextStyle.parentComponentStyleProperty().set(StyleController.INSTANCE.getDefaultTextStyleForTextEditor());

        this.initListener();
        //Useful to be able to create configuration copy via XML serialization
        //Done after every initialization, because the configuration is added to "All components"
        this.configurationParent.set(this);
        this.firstSelectionPart = new ComponentHolder<>(this.firstSelectionPartId, this.thisProperty);
        this.pluginsConfigProperties = PluginController.INSTANCE.getPluginConfigurationPropertiesMap(thisProperty);

        ConfigurationMemoryLeakChecker.registerConfiguration(this);
    }

    /**
     * Create the needed listener for this configuration.
     */
    private void initListener() {
        this.children.addListener((ListChangeListener<RootGraphicComponentI>) change -> {
            while (change.next()) {
                //On add
                if (change.wasAdded()) {
                    List<? extends RootGraphicComponentI> added = change.getAddedSubList();
                    for (RootGraphicComponentI add : added) {
                        add.configurationParentProperty().set(LCConfigurationComponent.this);
                        this.addRootComponent(add);
                    }
                }
                //On remove
                if (change.wasRemoved()) {
                    List<? extends RootGraphicComponentI> removed = change.getRemoved();
                    for (RootGraphicComponentI rm : removed) {
                        rm.configurationParentProperty().set(null);
                        rm.dispatchRemovedPropertyValue(true);
                        this.removeRootComponent(rm);
                    }
                }
            }
        });
        /*
         * On each added component, add a listener on deleted property.
         * A deleted property set to true will remove the component from the map.
         */
        this.allComponents.addListener((MapChangeListener<String, DisplayableComponentI>) change -> {
            if (change.wasAdded()) {
                this.componentAdded(change.getValueAdded());
            }
            if (change.wasRemoved()) {
                this.componentRemoved(change.getValueRemoved());
            }
        });
        this.computedWidth.bind(Bindings.createDoubleBinding(() -> fixedSize.get() ? width.get() : automaticWidth.get(), fixedSize, width, automaticWidth));
        this.computedHeight.bind(Bindings.createDoubleBinding(() -> fixedSize.get() ? height.get() : automaticHeight.get(), fixedSize, height, automaticHeight));
        this.computedFrameWidth.bind(Bindings.createDoubleBinding(() -> fullScreenOnLaunch.get() ? automaticFrameWidth.get() : frameWidth.get(), fullScreenOnLaunch, automaticFrameWidth, frameWidth));
        this.computedFrameHeight.bind(Bindings.createDoubleBinding(() -> fullScreenOnLaunch.get() ? automaticFrameHeight.get() : frameHeight.get(), fullScreenOnLaunch, automaticFrameHeight, frameHeight));

        // Default frame automatic width/height is based on configuration size (could be changed later, that's why there is automatic* properties)
        this.automaticFrameWidth.bind(this.computedWidth);
        this.automaticFrameHeight.bind(this.computedHeight.add(LCGraphicStyle.STAGE_TITLE_BAR_HEIGHT));

        this.useGrid.addListener((obs, ov, nv) -> {
            if (nv) {
                this.checkAllGridLocationAndSize();
                this.computeConfigurationAutomaticSize();
            }
        });
        this.gridSize.addListener((obs, ov, nv) -> {
            this.checkAllGridLocationAndSize();
            this.computeConfigurationAutomaticSize();
        });
    }

    // Class part : "Getter"
    //========================================================================
    @Override
    public ObjectProperty<UserActionSequencesI> userActionSequencesProperty() {
        return userActionSequences;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<RootGraphicComponentI> getChildren() {
        return this.children;
    }

    /**
     * {@inheritDoc}
     */
    // FIXME : does this bellongs to here ?
    @Override
    public IntegerProperty unsavedActionProperty() {
        return this.unsavedAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableMap<String, DisplayableComponentI> getAllComponent() {
        return this.allComponents;
    }

    @Override
    public Set<String> getManualPluginDependencyIds() {
        return manualPluginDependencyIds;
    }

    @Override
    public <T extends PluginConfigPropertiesI> T getPluginConfigProperties(String pluginId, Class<T> exceptedType) {
        final PluginConfigPropertiesI prop = pluginsConfigProperties.get(pluginId);
        if (prop != null) return (T) prop;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchRemovedPropertyValue(final boolean valueP) {
        super.dispatchRemovedPropertyValue(valueP);
        for (RootGraphicComponentI rootGraphicComponentI : this.children) {
            rootGraphicComponentI.dispatchRemovedPropertyValue(valueP);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchDisplayedProperty(final boolean valueP) {
        super.dispatchDisplayedProperty(valueP);
        for (RootGraphicComponentI rootGraphicComponentI : this.children) {
            rootGraphicComponentI.dispatchDisplayedProperty(valueP);
        }
    }

    @Override
    public void idsChanged(final Map<String, String> changes) {
        super.idsChanged(changes);
        this.eventManager.dispatchIdsChanged(changes);
        this.firstSelectionPart.idsChanged(changes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty widthProperty() {
        return this.width;
    }

    @Override
    public ReadOnlyDoubleProperty automaticWidthProperty() {
        return automaticWidth;
    }

    @Override
    public ReadOnlyDoubleProperty computedWidthProperty() {
        return computedWidth;
    }

    @Override
    public ReadOnlyDoubleProperty automaticHeightProperty() {
        return automaticHeight;
    }

    @Override
    public ReadOnlyDoubleProperty computedHeightProperty() {
        return computedHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty heightProperty() {
        return this.height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty fixedSizeProperty() {
        return this.fixedSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty useGridProperty() {
        return this.useGrid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegerProperty gridSizeProperty() {
        return this.gridSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoiceSynthesizerParameterI getVoiceSynthesizerParameter() {
        return this.voiceSynthesizerParameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<SelectionModeI> selectionModeProperty() {
        return this.selectionMode;
    }

    @Override
    public ObjectProperty<DirectSelectionModeI> directSelectionOnMouseOnScanningSelectionModeProperty() {
        return directSelectionOnMouseOnScanningSelectionMode;
    }

    @Override
    public BooleanProperty hideMainSelectionModeViewProperty() {
        return hideMainSelectionModeView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectionModeParameterI getSelectionModeParameter() {
        return this.selectionModeParameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<GridComponentI> firstSelectionPartProperty() {
        return this.firstSelectionPart.componentProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty frameWidthProperty() {
        return this.frameWidth;
    }

    @Override
    public ReadOnlyDoubleProperty computedFrameWidthProperty() {
        return computedFrameWidth;
    }

    @Override
    public ReadOnlyDoubleProperty automaticFrameWidthProperty() {
        return automaticFrameWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty frameHeightProperty() {
        return this.frameHeight;
    }

    @Override
    public ReadOnlyDoubleProperty computedFrameHeightProperty() {
        return computedFrameHeight;
    }

    @Override
    public ReadOnlyDoubleProperty automaticFrameHeightProperty() {
        return automaticFrameHeight;
    }

    @Override
    public List<WriterEntryI> getUseModeWriterEntries() {
        return useModeWriterEntries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty fullScreenOnLaunchProperty() {
        return this.fullScreenOnLaunch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<Color> backgroundColorProperty() {
        return this.backgroundColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty keepConfigurationRatioProperty() {
        return this.keepConfigurationRatio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty frameOpacityProperty() {
        return this.frameOpacity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<FramePosition> framePositionOnLaunchProperty() {
        return this.framePositionOnLaunch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty useParentSelectionModeProperty() {
        return this.useParentSelectionMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty canUseParentSelectionModeConfigurationProperty() {
        return this.canUseParentSelectionModeConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UseEventManagerI getEventManager() {
        return this.eventManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty virtualKeyboardProperty() {
        return this.virtualKeyboard;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VirtualMouseParameterI getVirtualMouseParameters() {
        return this.virtualMouseParameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PredictionParameterI getPredictionParameters() {
        return this.predictionParameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty securedConfigurationModeProperty() {
        return this.securedConfigurationMode;
    }

    @Override
    public DoubleProperty displayedConfigurationScaleXProperty() {
        return displayedConfigurationScaleX;
    }

    @Override
    public DoubleProperty displayedConfigurationScaleYProperty() {
        return displayedConfigurationScaleY;
    }

    @Override
    public DoubleProperty configurationScaleInEditModeProperty() {
        return configurationScaleInEditMode;
    }

    @Override
    public ObjectProperty<KeyListNodeI> rootKeyListNodeProperty() {
        return rootKeyListNode;
    }

    @Override
    public void clearAllComponentViewCache() {
        for (DisplayableComponentI value : new ArrayList<>(this.allComponents.values())) {
            value.clearViewCache();
        }
    }
    //========================================================================

    // Class part : "Component callbacks"
    //========================================================================
    private List<Consumer<?>> addCallbacks, removeCallbacks;

    @Override
    public <T extends DisplayableComponentI> void addComponentCallbacks(final Consumer<T> addedCallback, final Consumer<T> removedCallback) {
        if (addedCallback != null) {
            this.addCallbacks.add(addedCallback);
        }
        if (removedCallback != null) {
            this.removeCallbacks.add(removedCallback);
        }
    }

    @Override
    public <T extends DisplayableComponentI> void removeComponentCallbacks(final Consumer<T> addedCallback, final Consumer<T> removedCallback) {
        if (addedCallback != null) {
            this.addCallbacks.remove(addedCallback);
        }
        if (removedCallback != null) {
            this.removeCallbacks.remove(removedCallback);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void componentAdded(final DisplayableComponentI component) {
        component.removedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                LCConfigurationComponent.this.allComponents.remove(component.getID());
            }
        });
        for (Consumer addedCallback : this.addCallbacks) {
            addedCallback.accept(component);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void componentRemoved(final DisplayableComponentI component) {
        for (Consumer removedCallback : this.removeCallbacks) {
            removedCallback.accept(component);
        }
    }
    //========================================================================

    // Class part : "Automatic/fixed sizing"
    //========================================================================

    /**
     * Add listener on a root component to compute the configuration size on component size change.
     *
     * @param rootComponent the component added
     */
    private void addRootComponent(final RootGraphicComponentI rootComponent) {
        //Change width
        ChangeListener<? super Number> widthChangeListener = (obs, ov, nv) -> {
            this.computeConfigurationWidth();
        };
        rootComponent.xProperty().addListener(widthChangeListener);
        rootComponent.widthProperty().addListener(widthChangeListener);
        //Change height
        ChangeListener<? super Number> heightChangeListener = (obs, ov, nv) -> {
            this.computeConfigurationHeight();
        };
        rootComponent.yProperty().addListener(heightChangeListener);
        rootComponent.heightProperty().addListener(heightChangeListener);
        //When resizing/moving ends, check grid
        ChangeListener<? super Boolean> resizingMovingListener = (obs, ov, nv) -> {
            if (ov) {
                this.checkGridLocationAndSize(rootComponent);
            }
        };
        rootComponent.resizingProperty().addListener(resizingMovingListener);
        rootComponent.movingProperty().addListener(resizingMovingListener);

        //On add, check size and location for the grid
        this.checkGridLocationAndSize(rootComponent);
        //On add, fire a size compute
        this.computeConfigurationAutomaticSize();
    }

    /**
     * Fire size change for the removed component
     *
     * @param rootComponent the root component removed
     */
    private void removeRootComponent(final RootGraphicComponentI rootComponent) {
        this.computeConfigurationAutomaticSize();
    }

    /**
     * Compute the configuration width when its an automatic size configuration
     */
    private void computeConfigurationWidth() {
        RootGraphicComponentI maxChild = this.getMaxXRootComponent();
        if (maxChild != null) {
            this.automaticWidth.set(maxChild.xProperty().get() + maxChild.widthProperty().get() + LCConstant.CONFIG_ROOT_COMPONENT_GAP);
        } else {
            this.automaticWidth.set(0);
        }
    }

    /**
     * Compute the configuration height when its an automatic size configuration
     */
    private void computeConfigurationHeight() {
        RootGraphicComponentI maxChild = this.getMaxYRootComponent();
        if (maxChild != null) {
            this.automaticHeight.set(maxChild.yProperty().get() + maxChild.heightProperty().get() + LCConstant.CONFIG_ROOT_COMPONENT_GAP);
        } else {
            this.automaticHeight.set(0);
        }
    }

    /**
     * Compute automatic configuration width and height if the fixed size is disabled.<br>
     * This will also set the frame width and height if configuration is planned to be launched in full screen
     */
    private void computeConfigurationAutomaticSize() {
        this.computeConfigurationWidth();
        this.computeConfigurationHeight();
    }

    /**
     * @return the root component that has its right border to the max position
     */
    private RootGraphicComponentI getMaxXRootComponent() {
        RootGraphicComponentI maxX = null;
        for (RootGraphicComponentI child : this.children) {
            if (maxX == null || child.xProperty().get() + child.widthProperty().get() > maxX.xProperty().get() + maxX.widthProperty().get()) {
                maxX = child;
            }
        }
        return maxX;
    }

    /**
     * @return the root component that has its bottom border to the max position
     */
    private RootGraphicComponentI getMaxYRootComponent() {
        RootGraphicComponentI maxY = null;
        for (RootGraphicComponentI child : this.children) {
            if (maxY == null || child.yProperty().get() + child.heightProperty().get() > maxY.yProperty().get() + maxY.heightProperty().get()) {
                maxY = child;
            }
        }
        return maxY;
    }
    //========================================================================

    // Class part : "Grid"
    //========================================================================

    /**
     * Should be called each time a component move and change its size.<br>
     * This method is use to check if the component size is ok relative to the grid size when enable.<br>
     * If the grid is disabled, calling this method has no effect.<br>
     *
     * @param rootComponent the root component to check.
     */
    private void checkGridLocationAndSize(final RootGraphicComponentI rootComponent) {
        if (this.useGrid.get()) {
            rootComponent.xProperty().set(this.getCorrectValueForGrid(rootComponent.xProperty().get()));
            rootComponent.yProperty().set(this.getCorrectValueForGrid(rootComponent.yProperty().get()));
            rootComponent.widthProperty().set(this.getCorrectValueForGrid(rootComponent.widthProperty().get()));
            rootComponent.heightProperty().set(this.getCorrectValueForGrid(rootComponent.heightProperty().get()));
        }
    }

    /**
     * Correct a value to respect the grid allowed values
     *
     * @param value the value to correct
     * @return corrected value
     */
    private double getCorrectValueForGrid(final double value) {
        int valueToInt = (int) Math.round(value);
        int correctedDown = valueToInt - valueToInt % this.gridSize.get();
        int correctedUp = valueToInt + this.gridSize.get() - valueToInt % this.gridSize.get();
        return Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, valueToInt - correctedDown > correctedUp - valueToInt ? correctedUp : correctedDown);
    }

    /**
     * To call {@link #checkGridLocationAndSize(RootGraphicComponentI)} on every configuration component
     */
    private void checkAllGridLocationAndSize() {
        if (this.useGrid.get()) {
            for (RootGraphicComponentI rootGraphicComponentI : this.children) {
                this.checkGridLocationAndSize(rootGraphicComponentI);
            }
        }
    }
    //========================================================================


    // DUPLICATION
    //========================================================================

    @Override
    public DuplicableComponentI duplicate(boolean changeID) {
        final LCConfigurationI duplicated = (LCConfigurationI) super.duplicate(changeID);
        // Because key list are saved to a separated file they are not part of the configuration serialize/deserialize methods : duplication is manually done
        duplicated.rootKeyListNodeProperty().set((KeyListNodeI) this.rootKeyListNode.get().duplicate(changeID));
        duplicated.userActionSequencesProperty().set((UserActionSequencesI) this.userActionSequences.get().duplicate(changeID));
        return duplicated;
    }

    //========================================================================

    // XML
    //========================================================================
    private static final String NODE_CHIDREN = "Components";

    /**
     * {@inheritDoc}
     */
    @Override
    public Element serialize(final IOContextI ioContext) {
        //Root
        Element node = super.serialize(ioContext);
        //Properties
        XMLObjectSerializer.serializeInto(LCConfigurationComponent.class, this, node);
        //Children list
        Element childrenElement = new Element(LCConfigurationComponent.NODE_CHIDREN);
        node.addContent(childrenElement);
        for (RootGraphicComponentI component : this.children) {
            childrenElement.addContent(component.serialize(ioContext));
        }
        // Styles
        StyleSerialializer.serializeKeyStyle(this, node, ioContext);
        StyleSerialializer.serializeGridStyle(this, node, ioContext);
        StyleSerialializer.serializeTextDisplayerStyle(this, node, ioContext);
        //Prediction parameter
        node.addContent(this.predictionParameter.serialize(ioContext));
        //Voice synthesizer parameters
        node.addContent(this.voiceSynthesizerParameter.serialize(ioContext));
        //Selection mode parameter
        node.addContent(this.selectionModeParameter.serialize(ioContext));
        //Use event
        node.addContent(this.eventManager.serialize(ioContext));
        //Virtual mouse
        node.addContent(this.virtualMouseParameter.serialize(ioContext));
        //Serialize dependencies
        ConfigurationComponentIOHelper.serializeComponentDependencies(ioContext, this, node);
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deserialize(final Element node, final IOContextI ioContext) throws LCException {
        super.deserialize(node, ioContext);
        ConfigurationComponentIOHelper.deserializeComponentDependencies(ioContext, this, node);
        //Voice synthesizer
        Element voiceParameterNode = node.getChild(VoiceSynthesizerParameter.NODE_VOICE_PARAMETERS);
        this.voiceSynthesizerParameter.deserialize(voiceParameterNode, ioContext);
        //Selection mode
        Element selectionModeParameter = node.getChild(SelectionModeParameter.NODE_SELECTION_MODE);
        this.selectionModeParameter.deserialize(selectionModeParameter, ioContext);
        //Use event
        Element useEventManager = node.getChild(UseEventManager.NODE_USE_EVENT_MANAGER);
        if (useEventManager != null) {
            this.eventManager.deserialize(useEventManager, ioContext);
        }

        //Virtual mouse
        Element nodeVirtualMouseParam = node.getChild(VirtualMouseParameter.NODE_VIRTUAL_MOUSE_PARAMETER);
        if (nodeVirtualMouseParam != null) {
            this.virtualMouseParameter.deserialize(nodeVirtualMouseParam, ioContext);
        }

        //Prediction param.
        Element nodePredictionParam = node.getChild(PredictionParameter.NODE_PREDICTION_PARAMETER);
        if (nodePredictionParam != null) {
            this.predictionParameter.deserialize(nodePredictionParam, ioContext);
        }

        XMLObjectSerializer.deserializeInto(LCConfigurationComponent.class, this, node);

        // Styles
        StyleSerialializer.deserializeKeyStyle(this, node, ioContext);
        StyleSerialializer.deserializeGridStyle(this, node, ioContext);
        StyleSerialializer.deserializeTextDisplayerStyle(this, node, ioContext);

        //For each child
        Element childrenElement = node.getChild(LCConfigurationComponent.NODE_CHIDREN);
        List<Element> childrenList = childrenElement.getChildren();
        List<RootGraphicComponentI> loadedComponents = new ArrayList<>(childrenList.size() + 5);
        for (Element childElement : childrenList) {
            //Load and add
            Pair<Boolean, XMLSerializable<IOContextI>> childComponentResult = ConfigurationComponentIOHelper.create(childElement, ioContext, null);
            if (!childComponentResult.getLeft()) {
                RootGraphicComponentI childComponent = (RootGraphicComponentI) childComponentResult.getRight();
                childComponent.deserialize(childElement, ioContext);
                loadedComponents.add(childComponent);
            }
        }
        this.children.addAll(loadedComponents);
    }


    static final String NODE_CONFIGURATION_INFORMATION = "ConfigurationInformation";
    static final String NODE_SAVED_TEXT_ENTRIES = "TextEntries";

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {
        super.serializeUseInformation(elements);
        Element textEditorInfo = new Element(NODE_CONFIGURATION_INFORMATION);
        Element entries = new Element(NODE_SAVED_TEXT_ENTRIES);
        textEditorInfo.addContent(entries);
        for (WriterEntryI writerEntry : this.useModeWriterEntries) {
            entries.addContent(writerEntry.serialize(null));
        }
        elements.put(this.getID(), textEditorInfo);
    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
        super.deserializeUseInformation(elements);
        this.useModeWriterEntries.clear();
        if (elements.containsKey(this.getID())) {
            Element element = elements.get(this.getID());
            Element entriesElement = element.getChild(NODE_SAVED_TEXT_ENTRIES);
            List<Element> textEntriesElements = entriesElement.getChildren();
            for (Element textEntryElement : textEntriesElements) {
                WriterEntry entry = new WriterEntry();
                entry.deserialize(textEntryElement, null);
                useModeWriterEntries.add(entry);
            }
        }
    }
    //========================================================================

    // Class part : "Tree"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ObservableList<? extends TreeDisplayableComponentI> getChildrenNode() {
        return this.getChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNodeLeaf() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeDisplayableType getNodeType() {
        return TreeDisplayableType.BASE;
    }
    //========================================================================

    // STYLES
    //========================================================================
    @Override
    public ShapeCompStyleI getGridShapeStyle() {
        return gridShapeStyle;
    }

    @Override
    public KeyCompStyleI getKeyStyle() {
        return keyStyle;
    }

    @Override
    public TextCompStyleI getKeyTextStyle() {
        return keyTextStyle;
    }

    @Override
    public ShapeCompStyleI getTextDisplayerShapeStyle() {
        return textDisplayerShapeStyle;
    }

    @Override
    public TextCompStyleI getTextDisplayerTextStyle() {
        return textDisplayerTextStyle;
    }
    //========================================================================

}
