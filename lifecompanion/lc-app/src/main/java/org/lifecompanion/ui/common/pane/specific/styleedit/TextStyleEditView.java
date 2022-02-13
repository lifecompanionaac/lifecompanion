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
package org.lifecompanion.ui.common.pane.specific.styleedit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.controller.editaction.StyleActions.ChangeStylePropAction;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.ui.common.pane.generic.cell.FontListCell;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.HashMap;
import java.util.Map;

public class TextStyleEditView extends AbstractStyleEditView<TextCompStyleI> implements LCViewInitHelper {

    private LCColorPicker fieldColor;
    private CheckBox boxBold, boxItalic, boxUnderline, boxUpperCase;
    private ComboBox<String> comboboxFontFamilly;
    private Spinner<Integer> spinnerSize;
    private HBox boxCheckboxs;
    private HBox boxTextAlign;
    private ToggleGroup textAlignGroup;
    private Label labelTextAlign;
    private Map<TextAlignment, ToggleButton> textAlignButtons;
    private BooleanProperty disableFontSizeRequest;
    private BooleanProperty disableFont;

    private ChangeListener<String> changeListenerFont;
    private ChangeListener<Number> changeListenerFontSize;
    private ChangeListener<Boolean> changeListenerBold, changeListenerItalic, changeListenerUnderline, changeListenerUpperCase;
    private ChangeListener<TextAlignment> changeListenerTextAlignment;
    private ChangeListener<Color> changeListenerColor;

    private Node modifiedIndicatorFieldColor, modifiedIndicatorItalic, modifiedIndicatorBold, modifiedIndicatorUnderline, modifiedIndicatorUpperCase, modifiedIndicatorFontFamilly, modifiedIndicatorFontSize, modifiedIndicatorTextAlignment;


    public TextStyleEditView(boolean bindOnModel) {
        super(bindOnModel);
    }

    public BooleanProperty disableFontSizeRequestProperty() {
        if (this.disableFontSizeRequest == null) {
            this.disableFontSizeRequest = new SimpleBooleanProperty(false);
        }
        return this.disableFontSizeRequest;
    }

    private BooleanProperty disableFontProperty() {
        if (this.disableFont == null) {
            this.disableFont = new SimpleBooleanProperty(false);
        }
        return this.disableFont;
    }

    @Override
    public void initUI() {
        super.initUI();

        //Create fields
        this.fieldColor = new LCColorPicker();
        this.boxBold = new CheckBox(Translation.getText("text.style.font.bold"));
        this.boxItalic = new CheckBox(Translation.getText("text.style.font.italic"));
        this.boxUnderline = new CheckBox(Translation.getText("text.style.font.underline"));
        this.boxUpperCase = new CheckBox(Translation.getText("text.style.font.uppercase"));
        this.spinnerSize = FXControlUtils.createIntSpinner(1, 250, 12, 2, 75.0);
        this.comboboxFontFamilly = new ComboBox<>(FXCollections.observableList(Font.getFamilies()));
        this.comboboxFontFamilly.setCellFactory(lv -> new FontListCell());
        this.comboboxFontFamilly.setButtonCell(new FontListCell());

        //Text align
        this.textAlignButtons = new HashMap<>();
        this.textAlignGroup = FXControlUtils.createAlwaysSelectedToggleGroup();
        this.boxTextAlign = new HBox();
        this.boxTextAlign.setAlignment(Pos.CENTER_RIGHT);
        this.boxTextAlign.getChildren().add(this.createTextAlignToggle(TextAlignment.LEFT, FontAwesome.Glyph.ALIGN_LEFT, "left"));
        this.boxTextAlign.getChildren().add(this.createTextAlignToggle(TextAlignment.CENTER, FontAwesome.Glyph.ALIGN_CENTER, "center"));
        this.boxTextAlign.getChildren().add(this.createTextAlignToggle(TextAlignment.RIGHT, FontAwesome.Glyph.ALIGN_RIGHT, "right"));

        //Layout fields
        this.fieldGrid.add(new Label(Translation.getText("text.style.font.familly")), 0, 0);
        this.fieldGrid.add(this.comboboxFontFamilly, 1, 0);
        this.fieldGrid.add(modifiedIndicatorFontFamilly = this.createModifiedIndicator(TextCompStyleI::fontFamilyProperty, comboboxFontFamilly), 2, 0);
        this.fieldGrid.add(new Label(Translation.getText("text.style.font.size")), 0, 1);
        this.fieldGrid.add(this.spinnerSize, 1, 1);
        this.fieldGrid.add(modifiedIndicatorFontSize = this.createModifiedIndicator(TextCompStyleI::fontSizeProperty, this.disableFontProperty(), false), 2, 1);
        GridPane.setHalignment(this.spinnerSize, HPos.RIGHT);
        this.fieldGrid.add(new Label(Translation.getText("text.style.font.color")), 0, 2);
        this.fieldGrid.add(this.fieldColor, 1, 2);
        GridPane.setHalignment(this.fieldColor, HPos.RIGHT);
        this.fieldGrid.add(modifiedIndicatorFieldColor = this.createModifiedIndicator(TextCompStyleI::colorProperty, fieldColor), 2, 2);
        GridPane.setHalignment(this.fieldColor, HPos.RIGHT);
        this.labelTextAlign = new Label(Translation.getText("text.style.text.align"));
        this.fieldGrid.add(this.labelTextAlign, 0, 3);
        this.fieldGrid.add(this.boxTextAlign, 1, 3);
        this.fieldGrid.add(modifiedIndicatorTextAlignment = this.createModifiedIndicator(TextCompStyleI::textAlignmentProperty, boxTextAlign), 2, 3);
        this.boxCheckboxs = new HBox(15.0, //
                this.boxBold, modifiedIndicatorBold = this.createModifiedIndicator(TextCompStyleI::boldProperty, boxBold, true), //
                this.boxItalic, modifiedIndicatorItalic = this.createModifiedIndicator(TextCompStyleI::italicProperty, boxItalic, true), //
                this.boxUnderline, modifiedIndicatorUnderline = this.createModifiedIndicator(TextCompStyleI::underlineProperty, boxUnderline, true), //
                this.boxUpperCase, modifiedIndicatorUpperCase = this.createModifiedIndicator(TextCompStyleI::upperCaseProperty, boxUpperCase, true));
        this.boxCheckboxs.setAlignment(Pos.CENTER);
        this.fieldGrid.add(this.boxCheckboxs, 0, 4, 2, 1);

        GridPane.setHgrow(this.labelTextAlign, Priority.ALWAYS);
    }

    private ToggleButton createTextAlignToggle(final TextAlignment value, final Glyph glyph, final String tooltipEnd) {
        ToggleButton button = FXControlUtils.createGraphicsToggleButton(null, GlyphFontHelper.FONT_AWESOME.create(glyph).size(12).color(Color.GRAY),
                "tooltip.align.text." + tooltipEnd);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.textAlignButtons.put(value, button);
        this.textAlignGroup.getToggles().add(button);
        button.setUserData(value);
        //Listener
        if (bindOnModel) {
            button.setOnAction((ev) -> {
                ConfigActionController.INSTANCE.executeAction(this.createChangePropAction(this.model.get().textAlignmentProperty(), value));
            });
        }
        return button;
    }

    @Override
    public void initListener() {
        super.initListener();
        if (bindOnModel) {
            this.changeListenerFont = EditActionUtils.createSelectionModelBinding(this.comboboxFontFamilly.getSelectionModel(), //
                    this.model, model -> model.fontFamilyProperty().value().getValue(), //
                    (model, fontFamily) -> this.createChangePropAction(model.fontFamilyProperty(), fontFamily));
            this.changeListenerFontSize = EditActionUtils.createIntegerSpinnerBinding(this.spinnerSize, this.model,
                    g -> g.fontSizeProperty().value(), (m, nv) -> new ChangeStylePropAction<>(m.fontSizeProperty(), nv));
            this.changeListenerColor = EditActionUtils.createSimpleBinding(this.fieldColor.valueProperty(), this.model,
                    c -> c.colorProperty().value().getValue(), (model, nv) -> this.createChangePropAction(model.colorProperty(), nv));
            this.changeListenerBold = EditActionUtils.createSimpleBinding(this.boxBold.selectedProperty(), this.model,
                    c -> c.boldProperty().value().getValue(), (model, nv) -> this.createChangePropAction(model.boldProperty(), nv));
            this.changeListenerUnderline = EditActionUtils.createSimpleBinding(this.boxUnderline.selectedProperty(), this.model,
                    c -> c.underlineProperty().value().getValue(), (model, nv) -> this.createChangePropAction(model.underlineProperty(), nv));
            this.changeListenerUpperCase = EditActionUtils.createSimpleBinding(this.boxUpperCase.selectedProperty(), this.model,
                    c -> c.upperCaseProperty().value().getValue(), (model, nv) -> this.createChangePropAction(model.upperCaseProperty(), nv));
            this.changeListenerItalic = EditActionUtils.createSimpleBinding(this.boxItalic.selectedProperty(), this.model,
                    c -> c.italicProperty().value().getValue(), (model, nv) -> this.createChangePropAction(model.italicProperty(), nv));
            this.changeListenerTextAlignment = (obs, ov, nv) -> {
                //Check change ?
                this.textAlignGroup.selectToggle(this.textAlignButtons.get(nv));
            };
        }
    }

    @Override
    public void initBinding() {
        super.initBinding();
        this.spinnerSize.disableProperty().bind(this.disableFontProperty().or(this.disableFontSizeRequestProperty()));
    }

    @Override
    public void bind(final TextCompStyleI model) {
        super.bind(model);
        if (bindOnModel) {
            this.comboboxFontFamilly.getSelectionModel().select(model.fontFamilyProperty().value().getValue());
            this.spinnerSize.getValueFactory().setValue(model.fontSizeProperty().value().getValue().intValue());
            this.fieldColor.setValue(model.colorProperty().value().getValue());
            this.boxBold.setSelected(model.boldProperty().value().getValue());
            this.boxItalic.setSelected(model.italicProperty().value().getValue());
            this.boxUnderline.setSelected(model.underlineProperty().value().getValue());
            this.boxUpperCase.setSelected(model.upperCaseProperty().value().getValue());
            this.textAlignGroup.selectToggle(this.textAlignButtons.get(model.textAlignmentProperty().value().getValue()));
            model.fontSizeProperty().value().addListener(this.changeListenerFontSize);
            model.colorProperty().value().addListener(this.changeListenerColor);
            model.boldProperty().value().addListener(this.changeListenerBold);
            model.italicProperty().value().addListener(this.changeListenerItalic);
            model.underlineProperty().value().addListener(this.changeListenerUnderline);
            model.textAlignmentProperty().value().addListener(this.changeListenerTextAlignment);
            model.upperCaseProperty().value().addListener(changeListenerUpperCase);
        }
    }

    @Override
    public void unbind(final TextCompStyleI model) {
        super.unbind(model);
        if (bindOnModel) {
            model.fontFamilyProperty().value().removeListener(this.changeListenerFont);
            model.fontSizeProperty().value().removeListener(this.changeListenerFontSize);
            model.colorProperty().value().removeListener(this.changeListenerColor);
            model.boldProperty().value().removeListener(this.changeListenerBold);
            model.italicProperty().value().removeListener(this.changeListenerItalic);
            model.underlineProperty().value().removeListener(this.changeListenerUnderline);
            model.textAlignmentProperty().value().removeListener(this.changeListenerTextAlignment);
            model.upperCaseProperty().value().removeListener(changeListenerUpperCase);
        }
    }

    public ToggleGroup getTextAlignGroup() {
        return textAlignGroup;
    }

    public Map<TextAlignment, ToggleButton> getTextAlignButtons() {
        return textAlignButtons;
    }

    public LCColorPicker getFieldColor() {
        return fieldColor;
    }

    public CheckBox getBoxBold() {
        return boxBold;
    }

    public CheckBox getBoxItalic() {
        return boxItalic;
    }

    public CheckBox getBoxUnderline() {
        return boxUnderline;
    }

    public CheckBox getBoxUpperCase() {
        return boxUpperCase;
    }

    public ComboBox<String> getComboboxFontFamilly() {
        return comboboxFontFamilly;
    }

    public Spinner<Integer> getSpinnerSize() {
        return spinnerSize;
    }

    public Node getModifiedIndicatorItalic() {
        return modifiedIndicatorItalic;
    }

    public Node getModifiedIndicatorBold() {
        return modifiedIndicatorBold;
    }

    public Node getModifiedIndicatorUnderline() {
        return modifiedIndicatorUnderline;
    }

    public Node getModifiedIndicatorUpperCase() {
        return modifiedIndicatorUpperCase;
    }

    public Node getModifiedIndicatorFontFamilly() {
        return modifiedIndicatorFontFamilly;
    }

    public Node getModifiedIndicatorFontSize() {
        return modifiedIndicatorFontSize;
    }

    public Node getModifiedIndicatorTextAlignment() {
        return modifiedIndicatorTextAlignment;
    }

    public Node getModifiedIndicatorFieldColor() {
        return modifiedIndicatorFieldColor;
    }


}
