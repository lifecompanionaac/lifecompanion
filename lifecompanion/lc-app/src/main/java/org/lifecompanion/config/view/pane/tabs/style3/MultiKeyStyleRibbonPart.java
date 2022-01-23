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

package org.lifecompanion.config.view.pane.tabs.style3;

import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.style2.definition.KeyCompStyleI;
import org.lifecompanion.base.data.style2.MultipleStylePropertyHelper;
import org.lifecompanion.base.data.style2.PropertyChangeListener;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.pane.tabs.style2.view.key.KeyStyleEditView;
import org.lifecompanion.config.view.reusable.colorpicker.LCColorPicker;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.Arrays;

public class MultiKeyStyleRibbonPart extends RibbonBasePart<Void> implements LCViewInitHelper {

    private KeyStyleEditView keyStyleView;

    private final MultipleStylePropertyHelper<GridPartKeyComponentI, KeyCompStyleI> multipleStylePropertyHelper;
    private final PropertyChangeListener<KeyCompStyleI, Color> backgroundColorProperty;
    private final PropertyChangeListener<KeyCompStyleI, Boolean> autoFontSizeProperty;
    private final PropertyChangeListener<KeyCompStyleI, Color> strokeColorProperty;
    private final PropertyChangeListener<KeyCompStyleI, Number> shapeRadiusProperty;
    private final PropertyChangeListener<KeyCompStyleI, Number> strokeSizeProperty;


    public MultiKeyStyleRibbonPart() {
        multipleStylePropertyHelper = new MultipleStylePropertyHelper<>(SelectionController.INSTANCE.getSelectedKeys(), GridPartKeyComponentI::getKeyStyle,
                Arrays.asList(
                        backgroundColorProperty = new PropertyChangeListener<>(KeyCompStyleI::backgroundColorProperty),
                        strokeColorProperty = new PropertyChangeListener<>(KeyCompStyleI::strokeColorProperty),
                        shapeRadiusProperty = new PropertyChangeListener<>(KeyCompStyleI::shapeRadiusProperty),
                        strokeSizeProperty = new PropertyChangeListener<>(KeyCompStyleI::strokeSizeProperty),
                        autoFontSizeProperty = new PropertyChangeListener<>(KeyCompStyleI::autoFontSizeProperty)
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
    }


    @Override
    public void bind(Void model) {
    }

    @Override
    public void unbind(Void model) {
    }
}
