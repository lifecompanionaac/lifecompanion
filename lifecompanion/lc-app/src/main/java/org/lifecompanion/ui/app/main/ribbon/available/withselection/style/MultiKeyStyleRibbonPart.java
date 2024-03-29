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

package org.lifecompanion.ui.app.main.ribbon.available.withselection.style;

import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.style.KeyCompStyleI;
import org.lifecompanion.model.api.style.ShapeStyle;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.style.MultipleStylePropertyHelper;
import org.lifecompanion.model.impl.style.PropertyChangeListener;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.ui.common.pane.specific.styleedit.KeyStyleEditView;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;

import java.util.Arrays;

public class MultiKeyStyleRibbonPart extends RibbonBasePart<Void> implements LCViewInitHelper {

    private KeyStyleEditView keyStyleView;

    final MultipleStylePropertyHelper<GridPartKeyComponentI, KeyCompStyleI> multipleStylePropertyHelper;
    private final PropertyChangeListener<KeyCompStyleI, Color> backgroundColorProperty;
    private final PropertyChangeListener<KeyCompStyleI, Boolean> autoFontSizeProperty;
    private final PropertyChangeListener<KeyCompStyleI, Color> strokeColorProperty;
    private final PropertyChangeListener<KeyCompStyleI, Number> shapeRadiusProperty;
    private final PropertyChangeListener<KeyCompStyleI, Number> strokeSizeProperty;
    private final PropertyChangeListener<KeyCompStyleI, TextPosition> textPositionProperty;
    private final PropertyChangeListener<KeyCompStyleI, ShapeStyle> shapeStyleProperty;


    public MultiKeyStyleRibbonPart() {
        multipleStylePropertyHelper = new MultipleStylePropertyHelper<>(SelectionController.INSTANCE.getSelectedKeys(), GridPartKeyComponentI::getKeyStyle,
                Arrays.asList(
                        backgroundColorProperty = new PropertyChangeListener<>(KeyCompStyleI::backgroundColorProperty),
                        strokeColorProperty = new PropertyChangeListener<>(KeyCompStyleI::strokeColorProperty),
                        shapeRadiusProperty = new PropertyChangeListener<>(KeyCompStyleI::shapeRadiusProperty),
                        strokeSizeProperty = new PropertyChangeListener<>(KeyCompStyleI::strokeSizeProperty),
                        autoFontSizeProperty = new PropertyChangeListener<>(KeyCompStyleI::autoFontSizeProperty),
                        textPositionProperty = new PropertyChangeListener<>(KeyCompStyleI::textPositionProperty),
                        shapeStyleProperty = new PropertyChangeListener<>(KeyCompStyleI::shapeStyleProperty)
                ));
        this.initAll();
    }

    @Override
    public void initUI() {
        this.setTitle(Translation.getText("style.ribbon.part.key.style.single"));
        keyStyleView = new KeyStyleEditView(false);
        this.setContent(this.keyStyleView);
    }

    @Override
    public void initListener() {
        MultiKeyHelper.initStyleConfigActionListener(this.keyStyleView.getFieldBackgroundColor(),
                this.keyStyleView.getModifiedIndicatorFieldBackgroundColor(),
                LCColorPicker::setOnAction,
                LCColorPicker::getValue,
                LCColorPicker::setValue,
                k -> k.getKeyStyle().backgroundColorProperty(),
                this.backgroundColorProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.keyStyleView.getFieldStrokeColor(),
                this.keyStyleView.getModificationIndicatorFieldStrokeColor(),
                LCColorPicker::setOnAction,
                LCColorPicker::getValue,
                LCColorPicker::setValue,
                k -> k.getKeyStyle().strokeColorProperty(),
                this.strokeColorProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.keyStyleView.getSpinnerStrokeSize(),
                this.keyStyleView.getModificationIndicatorSpinnerStrokeSize(),
                MultiKeyHelper.createSpinnerActionEventListenerSetter(),
                Spinner::getValue,
                MultiKeyHelper.createSpinnerSetValue(),
                k -> k.getKeyStyle().strokeSizeProperty(),
                this.strokeSizeProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.keyStyleView.getSpinnerShapeRadius(),
                this.keyStyleView.getModificationIndicatorSpinnerShapeRadius(),
                MultiKeyHelper.createSpinnerActionEventListenerSetter(),
                Spinner::getValue,
                MultiKeyHelper.createSpinnerSetValue(),
                k -> k.getKeyStyle().shapeRadiusProperty(),
                this.shapeRadiusProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.keyStyleView.getToggleEnableAutoFontSizing(),
                this.keyStyleView.getModificationIndicatorToggleAutoFont(),
                MultiKeyHelper.createToggleSwitchActionEventListenerSetter(),
                ToggleSwitch::isSelected,
                MultiKeyHelper.createToggleSwitchSetValue(),
                k -> k.getKeyStyle().autoFontSizeProperty(),
                this.autoFontSizeProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.keyStyleView.getComboBoxTextPosition(),
                this.keyStyleView.getModificationIndicatorTextPosition(),
                MultiKeyHelper.createComboBoxActionEventSetter(),
                c -> c.getSelectionModel().getSelectedItem(),
                MultiKeyHelper.createComboBoxSetValue(),
                k -> k.getKeyStyle().textPositionProperty(),
                this.textPositionProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.keyStyleView.getComboBoxShapeStyle(),
                this.keyStyleView.getModificationIndicatorShapeStyle(),
                MultiKeyHelper.createComboBoxActionEventSetter(),
                c -> c.getSelectionModel().getSelectedItem(),
                MultiKeyHelper.createComboBoxSetValue(),
                k -> k.getKeyStyle().shapeStyleProperty(),
                this.shapeStyleProperty);
    }


    @Override
    public void bind(Void model) {
    }

    @Override
    public void unbind(Void model) {
    }
}
