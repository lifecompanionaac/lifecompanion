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

package org.lifecompanion.model.api.selectionmode;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

/**
 * The parameter object used by component to indicate the selection mode to use and the parameter for it.<br>
 * All parameters are not used by all selection mode, but the parameters instance will be shared by every mode.<br>
 * Each selection mode is free to use the parameters, and it's the configuration view that will show/hide the parameters field for each selection mode subtype.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface SelectionModeParameterI extends XMLSerializable<IOContextI> {

    // Common properties
    //========================================================================

    /**
     * The selection mode type
     *
     * @return wanted selection mode type for this parameters
     */
    ObjectProperty<Class<? extends SelectionModeI>> selectionModeTypeProperty();

    /**
     * @return true if a direct selection is allowed to be combined with other selection (ex : allow mouse selection even if scanning is enabled)
     */
    BooleanProperty enableDirectSelectionOnMouseOnScanningSelectionModeProperty();

    /**
     * @return the fire action type property (press,release)
     */
    ObjectProperty<FireActionEvent> fireActivationEventProperty();

    /**
     * @return input event to fire activation (keyboard, mouse...)
     */
    ObjectProperty<FireEventInput> fireEventInputProperty();

    /**
     * @return input event to fire next scan in scanning selection mode - if not automatic scanning with timing (keyboard, mouse...)
     */
    ObjectProperty<FireEventInput> nextScanEventInputProperty();

    /**
     * @return selection press time needed to fire activation
     */
    IntegerProperty timeToFireActionProperty();

    /**
     * @return time needed between each activation (to avoid miss click and repetition)
     */
    IntegerProperty timeBeforeRepeatProperty();

    /**
     * @return wanted color for selection view
     */
    ObjectProperty<Color> selectionViewColorProperty();

    /**
     * @return wanted color on activation for selection view
     */
    ObjectProperty<Color> selectionActivationViewColorProperty();

    /**
     * @return size for selection view
     */
    DoubleProperty selectionViewSizeProperty();

    /**
     * @return the keycode to fire keyboard event (if {@link #fireEventInputProperty()} is keyboard)
     */
    ObjectProperty<KeyCode> keyboardFireKeyProperty();

    /**
     * @return the keycode to fire keyboard event (if {@link #nextScanEventInputProperty()} is keyboard)
     */
    ObjectProperty<KeyCode> keyboardNextScanKeyProperty();

    ObjectProperty<MouseButton> mouseButtonActivationProperty();

    ObjectProperty<MouseButton> mouseButtonNextScanProperty();

    BooleanProperty hideMouseCursorProperty();
    //========================================================================


    // Scanning draw properties
    //========================================================================

    /**
     * @return the progress view color
     */
    ObjectProperty<Color> progressViewColorProperty();

    /**
     * @return if the progress have to be drawn
     */
    BooleanProperty drawProgressProperty();

    /**
     * @return the progress draw mode (used only if {@link #drawProgressProperty()} is true)
     */
    ObjectProperty<ProgressDrawMode> progressDrawModeProperty();

    /**
     * @return if the key have to be magnified on over
     */
    BooleanProperty manifyKeyOverProperty();

    /**
     * @return if the background should be "reduced" on selection
     */
    BooleanProperty backgroundReductionEnabledProperty();

    /**
     * @return to tune the background reduction if applied ({@link #backgroundReductionEnabledProperty()}). Should range 0.0 - 1.0
     */
    DoubleProperty backgroundReductionLevelProperty();

    /**
     * @return the size of progress bar (depends on {@link #progressDrawModeProperty()} )
     */
    DoubleProperty progressViewBarSizeProperty();

    /**
     * @return the selection scanning mode (automatic or manual scanning)
     */
    ObjectProperty<ScanningMode> scanningModeProperty();
    //========================================================================

    // Auto clic properties
    //========================================================================

    /**
     * @return time (in ms) to fire the activation for {@link AutoDirectSelectionModeI}
     */
    IntegerProperty autoActivationTimeProperty();

    /**
     * @return time (in ms) to fire the over action (for {@link AutoDirectSelectionModeI} but also {@link DirectSelectionModeI})
     */
    IntegerProperty autoOverTimeProperty();

    /**
     * @return true if even if the mode is auto activation, the direct selection should be considered as direct selection (to enable faster selection)
     */
    BooleanProperty enableActivationWithSelectionProperty();
    //========================================================================

    // Scanning properties
    //========================================================================

    /**
     * @return time between each element to scan
     */
    IntegerProperty scanPauseProperty();

    /**
     * @return time on first element
     */
    IntegerProperty scanFirstPauseProperty();

    /**
     * @return the scanning count before going into the parent
     */
    IntegerProperty maxScanBeforeStopProperty();

    /**
     * @return if the scanning should automatically starts or if the user should fire an action to start it
     */
    BooleanProperty startScanningOnClicProperty();

    /**
     * @return if the empty components should be scanned
     */
    BooleanProperty skipEmptyComponentProperty();
    //========================================================================

    // Utils
    //========================================================================

    /**
     * Copy all the selection mode parameters from another parameters into this one
     *
     * @param parameters the parameters to copy (shouldn't be null)
     */
    void copyFrom(SelectionModeParameterI parameters);

    /**
     * @return true if this selection mode parameters are not the result of user configuration but a LifeCompanion change.<br>
     * This can be true if for example, the grid selection mode parameter are automatically changed by LifeCompanion to optimize selection.
     */
    BooleanProperty selectionModeParameterAreSystemDefinedProperty();
    //========================================================================

    // Virtual cursor
    //========================================================================
    BooleanProperty enableAutoActivationProperty();

    BooleanProperty showVirtualCursorProperty();

    DoubleProperty virtualCursorSizeProperty();

    ObjectProperty<Color> virtualCursorColorProperty();
    //========================================================================
}
