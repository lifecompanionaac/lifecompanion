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

import javafx.beans.property.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.model.api.selectionmode.*;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.controller.io.IOManager;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for selection mode parameters.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectionModeParameter implements SelectionModeParameterI {
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectionModeParameter.class);

    private final ObjectProperty<Class<? extends SelectionModeI>> selectionModeType;

    @XMLGenericProperty(FireActionEvent.class)
    private final ObjectProperty<FireActionEvent> fireActivationEvent;

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> selectionViewColor;

    @XMLGenericProperty(ProgressDrawMode.class)
    private final ObjectProperty<ProgressDrawMode> progressDrawMode;

    @XMLGenericProperty(FireEventInput.class)
    private final ObjectProperty<FireEventInput> fireEventInput;

    @XMLGenericProperty(ScanningMode.class)
    private final ObjectProperty<ScanningMode> scanningMode;

    @XMLGenericProperty(FireEventInput.class)
    private final ObjectProperty<FireEventInput> nextScanEventInput;

    @XMLGenericProperty(KeyCode.class)
    private final ObjectProperty<KeyCode> keyboardFireKey;

    @XMLGenericProperty(KeyCode.class)
    private final ObjectProperty<KeyCode> keyboardNextScanKey;

    @XMLGenericProperty(MouseButton.class)
    private final ObjectProperty<MouseButton> mouseButtonActivation;

    @XMLGenericProperty(MouseButton.class)
    private final ObjectProperty<MouseButton> mouseButtonNextScan;

    private final IntegerProperty autoActivationTime;
    private final IntegerProperty autoOverTime;
    private final BooleanProperty enableActivationWithSelection;

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> progressViewColor;

    private final BooleanProperty drawProgress;
    private final BooleanProperty manifyKeyOver;
    private final BooleanProperty backgroundReductionEnabled;
    private final IntegerProperty scanPause;
    private final IntegerProperty scanFirstPause;
    private final IntegerProperty maxScanBeforeStop;

    private final IntegerProperty timeToFireAction, timeBeforeRepeat;

    private final BooleanProperty startScanningOnClic;

    private final BooleanProperty enableDirectSelectionOnMouseOnScanningSelectionMode;

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> selectionActivationViewColor;

    private final BooleanProperty skipEmptyComponent;

    private final DoubleProperty selectionViewSize;

    private final DoubleProperty backgroundReductionLevel;

    private final DoubleProperty progressViewBarSize;

    public SelectionModeParameter() {
        this.selectionModeType = new SimpleObjectProperty<>(this, "selectionModeType", DirectActivationSelectionMode.class);
        this.fireActivationEvent = new SimpleObjectProperty<>(this, "fireActionEvent", FireActionEvent.ON_RELEASE);
        this.fireEventInput = new SimpleObjectProperty<>(this, "fireEventInput", FireEventInput.MOUSE);
        this.nextScanEventInput = new SimpleObjectProperty<>(this, "nextScanEventInput", FireEventInput.KEYBOARD);
        this.selectionViewColor = new SimpleObjectProperty<>(this, "selectionViewColor", Color.DARKBLUE);
        this.keyboardFireKey = new SimpleObjectProperty<>(this, "keyboardFireKey", KeyCode.ENTER);
        this.keyboardNextScanKey = new SimpleObjectProperty<>(this, "keyboardNextScanKey", KeyCode.SPACE);
        this.autoOverTime = new SimpleIntegerProperty(this, "autoOverTime", 1000);
        this.autoActivationTime = new SimpleIntegerProperty(this, "autoActivationTime", 2500);
        this.progressViewColor = new SimpleObjectProperty<>(this, "progressViewColor", Color.rgb(3, 189, 244, 0.5));
        this.drawProgress = new SimpleBooleanProperty(this, "drawProgress", true);
        this.enableActivationWithSelection = new SimpleBooleanProperty(this, " enableActivationWithSelection", true);
        this.scanPause = new SimpleIntegerProperty(this, "scanPause", 1000);
        this.scanFirstPause = new SimpleIntegerProperty(this, "scanFirstPause", 0);
        this.maxScanBeforeStop = new SimpleIntegerProperty(this, "maxScanBeforeStop", 2);
        this.timeToFireAction = new SimpleIntegerProperty(this, "timeToFireAction", 0);
        this.timeBeforeRepeat = new SimpleIntegerProperty(this, "timeBeforeRepeat", 0);
        this.selectionViewSize = new SimpleDoubleProperty(this, "selectionViewSize", 5.0);
        this.progressViewBarSize = new SimpleDoubleProperty(this, "progressViewBarSize", 5.0);
        this.startScanningOnClic = new SimpleBooleanProperty(this, "startScanningOnClic", false);
        this.selectionActivationViewColor = new SimpleObjectProperty<>(this, "selectionActivationViewColor", LCGraphicStyle.SECOND_DARK);
        this.skipEmptyComponent = new SimpleBooleanProperty(this, "skipEmptyComponent", true);
        this.progressDrawMode = new SimpleObjectProperty<>(this, "progressDrawMode", ProgressDrawMode.PROGRESS_BAR);
        this.scanningMode = new SimpleObjectProperty<>(this, "scanningMode", ScanningMode.AUTO);
        this.manifyKeyOver = new SimpleBooleanProperty(this, "manifyKeyOver", false);
        this.backgroundReductionEnabled = new SimpleBooleanProperty(this, "backgroundReductionEnabled", false);
        this.backgroundReductionLevel = new SimpleDoubleProperty(this, "backgroundReductionLevel", 0.8);
        this.enableDirectSelectionOnMouseOnScanningSelectionMode = new SimpleBooleanProperty(false);
        this.mouseButtonActivation = new SimpleObjectProperty<>(MouseButton.ANY);
        this.mouseButtonNextScan = new SimpleObjectProperty<>(MouseButton.SECONDARY);
    }

    // Class part : "Properties"
    //========================================================================
    @Override
    public ObjectProperty<Class<? extends SelectionModeI>> selectionModeTypeProperty() {
        return this.selectionModeType;
    }

    @Override
    public BooleanProperty enableDirectSelectionOnMouseOnScanningSelectionModeProperty() {
        return enableDirectSelectionOnMouseOnScanningSelectionMode;
    }

    @Override
    public ObjectProperty<FireActionEvent> fireActivationEventProperty() {
        return this.fireActivationEvent;
    }

    @Override
    public ObjectProperty<Color> selectionViewColorProperty() {
        return this.selectionViewColor;
    }

    @Override
    public IntegerProperty autoActivationTimeProperty() {
        return this.autoActivationTime;
    }

    @Override
    public IntegerProperty autoOverTimeProperty() {
        return this.autoOverTime;
    }

    @Override
    public ObjectProperty<Color> progressViewColorProperty() {
        return this.progressViewColor;
    }

    @Override
    public BooleanProperty drawProgressProperty() {
        return this.drawProgress;
    }

    @Override
    public IntegerProperty scanPauseProperty() {
        return this.scanPause;
    }

    @Override
    public IntegerProperty scanFirstPauseProperty() {
        return this.scanFirstPause;
    }

    @Override
    public IntegerProperty maxScanBeforeStopProperty() {
        return this.maxScanBeforeStop;
    }

    @Override
    public BooleanProperty startScanningOnClicProperty() {
        return this.startScanningOnClic;
    }

    @Override
    public ObjectProperty<Color> selectionActivationViewColorProperty() {
        return this.selectionActivationViewColor;
    }

    @Override
    public BooleanProperty skipEmptyComponentProperty() {
        return this.skipEmptyComponent;
    }

    @Override
    public ObjectProperty<ProgressDrawMode> progressDrawModeProperty() {
        return this.progressDrawMode;
    }

    @Override
    public BooleanProperty manifyKeyOverProperty() {
        return this.manifyKeyOver;
    }

    @Override
    public BooleanProperty backgroundReductionEnabledProperty() {
        return backgroundReductionEnabled;
    }

    @Override
    public DoubleProperty backgroundReductionLevelProperty() {
        return backgroundReductionLevel;
    }

    @Override
    public ObjectProperty<FireEventInput> fireEventInputProperty() {
        return this.fireEventInput;
    }

    @Override
    public IntegerProperty timeToFireActionProperty() {
        return this.timeToFireAction;
    }

    @Override
    public IntegerProperty timeBeforeRepeatProperty() {
        return this.timeBeforeRepeat;
    }

    @Override
    public ObjectProperty<KeyCode> keyboardFireKeyProperty() {
        return this.keyboardFireKey;
    }

    @Override
    public DoubleProperty selectionViewSizeProperty() {
        return this.selectionViewSize;
    }

    @Override
    public DoubleProperty progressViewBarSizeProperty() {
        return this.progressViewBarSize;
    }

    @Override
    public ObjectProperty<ScanningMode> scanningModeProperty() {
        return this.scanningMode;
    }

    @Override
    public BooleanProperty enableActivationWithSelectionProperty() {
        return enableActivationWithSelection;
    }

    @Override
    public ObjectProperty<FireEventInput> nextScanEventInputProperty() {
        return nextScanEventInput;
    }

    @Override
    public ObjectProperty<KeyCode> keyboardNextScanKeyProperty() {
        return keyboardNextScanKey;
    }

    @Override
    public ObjectProperty<MouseButton> mouseButtonActivationProperty() {
        return mouseButtonActivation;
    }

    @Override
    public ObjectProperty<MouseButton> mouseButtonNextScanProperty() {
        return mouseButtonNextScan;
    }

    @Override
    public void copyFrom(final SelectionModeParameterI parameters) {
        this.selectionModeTypeProperty().set(parameters.selectionModeTypeProperty().get());
        this.fireActivationEventProperty().set(parameters.fireActivationEventProperty().get());
        this.fireEventInputProperty().set(parameters.fireEventInputProperty().get());
        this.nextScanEventInputProperty().set(parameters.nextScanEventInputProperty().get());
        this.keyboardNextScanKeyProperty().set(parameters.keyboardNextScanKeyProperty().get());
        this.timeToFireActionProperty().set(parameters.timeToFireActionProperty().get());
        this.timeBeforeRepeatProperty().set(parameters.timeBeforeRepeatProperty().get());
        this.selectionViewColorProperty().set(parameters.selectionViewColorProperty().get());
        this.selectionActivationViewColorProperty().set(parameters.selectionActivationViewColorProperty().get());
        this.selectionViewSizeProperty().set(parameters.selectionViewSizeProperty().get());
        this.keyboardFireKeyProperty().set(parameters.keyboardFireKeyProperty().get());
        this.progressViewColorProperty().set(parameters.progressViewColorProperty().get());
        this.drawProgressProperty().set(parameters.drawProgressProperty().get());
        this.progressDrawModeProperty().set(parameters.progressDrawModeProperty().get());
        this.manifyKeyOverProperty().set(parameters.manifyKeyOverProperty().get());
        this.autoActivationTimeProperty().set(parameters.autoActivationTimeProperty().get());
        this.scanningModeProperty().set(parameters.scanningModeProperty().get());
        this.autoOverTimeProperty().set(parameters.autoOverTimeProperty().get());
        this.scanPauseProperty().set(parameters.scanPauseProperty().get());
        this.scanFirstPauseProperty().set(parameters.scanFirstPauseProperty().get());
        this.maxScanBeforeStopProperty().set(parameters.maxScanBeforeStopProperty().get());
        this.startScanningOnClicProperty().set(parameters.startScanningOnClicProperty().get());
        this.skipEmptyComponentProperty().set(parameters.skipEmptyComponentProperty().get());
        this.progressViewBarSizeProperty().set(parameters.progressViewBarSizeProperty().get());
        this.enableActivationWithSelectionProperty().set(parameters.enableActivationWithSelectionProperty().get());
        this.scanningModeProperty().set(parameters.scanningModeProperty().get());
        this.nextScanEventInputProperty().set(parameters.nextScanEventInputProperty().get());
        this.keyboardNextScanKeyProperty().set(parameters.keyboardNextScanKeyProperty().get());
        this.backgroundReductionEnabledProperty().set(parameters.backgroundReductionEnabledProperty().get());
        this.backgroundReductionLevelProperty().set(parameters.backgroundReductionLevelProperty().get());
        this.enableDirectSelectionOnMouseOnScanningSelectionModeProperty().set(parameters.enableDirectSelectionOnMouseOnScanningSelectionModeProperty().get());
        this.mouseButtonActivationProperty().set(parameters.mouseButtonActivationProperty().get());
        this.mouseButtonNextScanProperty().set(parameters.mouseButtonNextScanProperty().get());
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    public static final String NODE_SELECTION_MODE = "SelectionMode";
    private static final String ATB_SELECT_MODE_TYPE = "selectionModeType";

    @Override
    public Element serialize(final IOContextI context) {
        Element node = new Element(SelectionModeParameter.NODE_SELECTION_MODE);
        XMLUtils.write(this.selectionModeType.get().getName(), SelectionModeParameter.ATB_SELECT_MODE_TYPE, node);
        XMLObjectSerializer.serializeInto(SelectionModeParameter.class, this, node);
        return node;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        String selectModeName = XMLUtils.readString(SelectionModeParameter.ATB_SELECT_MODE_TYPE, node);
        if (selectModeName != null) {
            try {
                this.selectionModeType.set(IOManager.getClassForName(selectModeName));
            } catch (ClassNotFoundException e) {
                SelectionModeParameter.LOGGER.warn("Couldn't load the select mode class", e);
            }
        }
        XMLObjectSerializer.deserializeInto(SelectionModeParameter.class, this, node);
    }
    //========================================================================

}
