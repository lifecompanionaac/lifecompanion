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

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.impl.style.MultipleStylePropertyHelper;
import org.lifecompanion.model.impl.style.PropertyChangeListener;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.pane.tabs.style2.view.text.TextStyleEditView;
import org.lifecompanion.config.view.reusable.colorpicker.LCColorPicker;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.Arrays;

public class MultiKeyTextStyleRibbonPart extends RibbonBasePart<Void> implements LCViewInitHelper {

    private TextStyleEditView textStyleEditView;
    private final MultipleStylePropertyHelper<GridPartKeyComponentI, TextCompStyleI> multipleStylePropertyHelper;

    private final PropertyChangeListener<TextCompStyleI, String> fontFamilyProperty;
    private final PropertyChangeListener<TextCompStyleI, Color> colorProperty;
    private final PropertyChangeListener<TextCompStyleI, Number> fontSizeProperty;
    private final PropertyChangeListener<TextCompStyleI, Boolean> italicProperty;
    private final PropertyChangeListener<TextCompStyleI, Boolean> boldProperty;
    private final PropertyChangeListener<TextCompStyleI, Boolean> underlineProperty;
    private final PropertyChangeListener<TextCompStyleI, Boolean> upperCaseProperty;
    private final PropertyChangeListener<TextCompStyleI, TextAlignment> textAlignmentProperty;

    public MultiKeyTextStyleRibbonPart() {
        multipleStylePropertyHelper = new MultipleStylePropertyHelper<>(SelectionController.INSTANCE.getSelectedKeys(), GridPartKeyComponentI::getKeyTextStyle,
                Arrays.asList(
                        fontFamilyProperty = new PropertyChangeListener<>(TextCompStyleI::fontFamilyProperty),
                        colorProperty = new PropertyChangeListener<>(TextCompStyleI::colorProperty),
                        fontSizeProperty = new PropertyChangeListener<>(TextCompStyleI::fontSizeProperty),
                        italicProperty = new PropertyChangeListener<>(TextCompStyleI::italicProperty),
                        boldProperty = new PropertyChangeListener<>(TextCompStyleI::boldProperty),
                        underlineProperty = new PropertyChangeListener<>(TextCompStyleI::underlineProperty),
                        upperCaseProperty = new PropertyChangeListener<>(TextCompStyleI::upperCaseProperty),
                        textAlignmentProperty = new PropertyChangeListener<>(TextCompStyleI::textAlignmentProperty)
                ));
        this.initAll();
    }

    @Override
    public void initUI() {
        this.setTitle(Translation.getText("style.ribbon.part.key.text.style.single"));
        textStyleEditView = new TextStyleEditView(false);
        this.setContent(this.textStyleEditView);
    }

    @Override
    public void initListener() {
        MultiKeyHelper.initStyleConfigActionListener(this.textStyleEditView.getComboboxFontFamilly(),
                this.textStyleEditView.getModifiedIndicatorFontFamilly(),
                ComboBoxBase::setOnAction,
                ComboBoxBase::getValue,
                ComboBoxBase::setValue,
                k -> k.getKeyTextStyle().fontFamilyProperty(),
                this.fontFamilyProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.textStyleEditView.getFieldColor(),
                this.textStyleEditView.getModifiedIndicatorFieldColor(),
                LCColorPicker::setOnAction,
                LCColorPicker::getValue,
                LCColorPicker::setValue,
                k -> k.getKeyTextStyle().colorProperty(),
                this.colorProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.textStyleEditView.getSpinnerSize(),
                this.textStyleEditView.getModifiedIndicatorFontSize(),
                MultiKeyHelper.createSpinnerActionEventListenerSetter(),
                Spinner::getValue,
                MultiKeyHelper.createSpinnerSetValue(),
                k -> k.getKeyTextStyle().fontSizeProperty(),
                this.fontSizeProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.textStyleEditView.getBoxItalic(),
                this.textStyleEditView.getModifiedIndicatorItalic(),
                MultiKeyHelper.createCheckboxActionEventListenerSetter(),
                CheckBox::isSelected,
                MultiKeyHelper.createCheckboxSetValue(),
                k -> k.getKeyTextStyle().italicProperty(),
                this.italicProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.textStyleEditView.getBoxUnderline(),
                this.textStyleEditView.getModifiedIndicatorUnderline(),
                MultiKeyHelper.createCheckboxActionEventListenerSetter(),
                CheckBox::isSelected,
                MultiKeyHelper.createCheckboxSetValue(),
                k -> k.getKeyTextStyle().underlineProperty(),
                this.underlineProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.textStyleEditView.getBoxBold(),
                this.textStyleEditView.getModifiedIndicatorBold(),
                MultiKeyHelper.createCheckboxActionEventListenerSetter(),
                CheckBox::isSelected,
                MultiKeyHelper.createCheckboxSetValue(),
                k -> k.getKeyTextStyle().boldProperty(),
                this.boldProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.textStyleEditView.getBoxUpperCase(),
                this.textStyleEditView.getModifiedIndicatorUpperCase(),
                MultiKeyHelper.createCheckboxActionEventListenerSetter(),
                CheckBox::isSelected,
                MultiKeyHelper.createCheckboxSetValue(),
                k -> k.getKeyTextStyle().upperCaseProperty(),
                this.upperCaseProperty);
        MultiKeyHelper.initStyleConfigActionListener(this.textStyleEditView.getTextAlignGroup(),
                this.textStyleEditView.getModifiedIndicatorTextAlignment(),
                MultiKeyHelper.createToggleButtonGroupActionEventSetter(),
                MultiKeyHelper.createToggleButtonGroupValueGetter(textStyleEditView.getTextAlignButtons()),
                MultiKeyHelper.createToggleButtonGroupValueSetter(textStyleEditView.getTextAlignButtons()),
                k -> k.getKeyTextStyle().textAlignmentProperty(),
                this.textAlignmentProperty);
    }


    @Override
    public void bind(Void model) {
    }

    @Override
    public void unbind(Void model) {
    }
}
