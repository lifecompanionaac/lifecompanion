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

package org.lifecompanion.config.view.pane.tabs.selected.part.imageusecomp;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.lifecompanion.api.component.definition.ImageUseComponentI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.view.reusable.impl.BaseConfigurationViewBorderPane;
import org.lifecompanion.config.data.action.impl.KeyActions;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.reusable.ViewportSelectorControl;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class ImageUseComponentConfigurationView extends BaseConfigurationViewBorderPane<ImageUseComponentI> implements LCViewInitHelper {
    /**
     * Button to rotate image
     */
    private Button buttonRotateLeft, buttonRotateRight;

    /**
     * Label to display current rotation
     */
    private Label labelRotation;

    /**
     * Button to hide the stage
     */
    private Button buttonOk;

    /**
     * Toggle to enable/disable preserve ratio
     */
    private ToggleSwitch togglePreserveRatio;

    /**
     * Toggle to enable/disable viewport use
     */
    private ToggleSwitch toggleUseViewport;

    /**
     * Toggle to enable/disable color replacement
     */
    private ToggleSwitch toggleEnableReplaceColor;

    /**
     * Color picker to select color to replace
     */
    private ColorPicker pickerColorToReplace, pickerReplacingColor;

    /**
     * Pane that contains color replacement fields
     */
    private GridPane paneColorSelection;

    /**
     * change listener colors
     */
    private ChangeListener<Color> changeListenerColorToReplace, changeListenerReplacingColor;

    /**
     * Change listener for preserve ratio property/enable color replace
     */
    private ChangeListener<Boolean> changeListenerPreserveRatio, changeListenerEnableColorReplace;

    /**
     * Change listener to replace color threshold
     */
    private ChangeListener<Number> changeListenerReplaceThreshold;

    /**
     * Slider for replace color threshold
     */
    private Slider sliderReplaceThreshold;

    /**
     * Change listener for viewport changes
     */
    private ChangeListener<Boolean> changeListenerUseViewport;

    /**
     * Viewport control
     */
    private ViewportSelectorControl viewportSelectorControl;

    public ImageUseComponentConfigurationView() {
        initAll();
    }

    @Override
    public void initUI() {
        //Create buttons
        this.togglePreserveRatio = ConfigUIUtils.createToggleSwitch("image.use.preserve.ratio", "tooltip.explain.image.preserve.ratio");
        //Advanced parameters
        this.buttonRotateLeft = UIUtils.createTextButtonWithGraphics(Translation.getText("rotate.image.left"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.ROTATE_LEFT).size(20.0).color(LCGraphicStyle.SECOND_DARK),
                "tooltip.rotate.image.left");
        this.buttonRotateRight = UIUtils.createTextButtonWithGraphics(Translation.getText("rotate.image.right"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.ROTATE_RIGHT).size(20.0).color(LCGraphicStyle.SECOND_DARK),
                "tooltip.rotate.image.right");
        this.labelRotation = new Label();
        HBox rotateButtonBox = new HBox(this.buttonRotateLeft, this.buttonRotateRight);
        rotateButtonBox.setAlignment(Pos.CENTER);

        VBox paneParameters = new VBox(5.0, this.togglePreserveRatio, rotateButtonBox, labelRotation);
        paneParameters.setAlignment(Pos.CENTER);
        paneParameters.setPadding(new Insets(10.0));

        //Color replacement
        this.toggleEnableReplaceColor = ConfigUIUtils.createToggleSwitch("image.use.enable.color.replace",
                "tooltip.explain.image.enable.color.replace");
        this.pickerColorToReplace = new ColorPicker();
        UIUtils.createAndAttachTooltip(pickerColorToReplace, "tooltip.explain.image.color.replace");
        this.pickerReplacingColor = new ColorPicker();
        UIUtils.createAndAttachTooltip(pickerReplacingColor, "tooltip.explain.image.color.replacing");
        this.sliderReplaceThreshold = UIUtils.createBaseSlider(0.0, 200.0, 10.0);
        UIUtils.createAndAttachTooltip(sliderReplaceThreshold, "tooltip.explain.image.color.replace.threshold");
        this.sliderReplaceThreshold.setMajorTickUnit(20);
        this.paneColorSelection = new GridPane();
        Label labelColorToReplace = new Label(Translation.getText("image.use.color.to.replace.field"));
        this.paneColorSelection.add(labelColorToReplace, 0, 0);
        this.paneColorSelection.add(this.pickerColorToReplace, 1, 0);
        this.paneColorSelection.add(new Label(Translation.getText("image.use.color.replacing.field")), 0, 1);
        this.paneColorSelection.add(this.pickerReplacingColor, 1, 1);
        this.paneColorSelection.add(new Label(Translation.getText("image.use.color.replace.threshold.field")), 0, 2);
        this.paneColorSelection.add(this.sliderReplaceThreshold, 1, 2);
        this.paneColorSelection.setVgap(3.0);
        GridPane.setHgrow(labelColorToReplace, Priority.ALWAYS);
        Separator sepColor = new Separator(Orientation.HORIZONTAL);
        sepColor.visibleProperty().bind(this.toggleEnableReplaceColor.visibleProperty());
        sepColor.managedProperty().bind(sepColor.visibleProperty());
        paneParameters.getChildren().addAll(sepColor, this.toggleEnableReplaceColor, this.paneColorSelection);

        //Viewport selector
        this.toggleUseViewport = ConfigUIUtils.createToggleSwitch("image.use.use.viewport", "tooltip.explain.image.use.viewport");
        this.viewportSelectorControl = new ViewportSelectorControl();
        this.viewportSelectorControl.setPrefHeight(250);
        paneParameters.getChildren().addAll(new Separator(Orientation.HORIZONTAL), this.toggleUseViewport, this.viewportSelectorControl);

        // Button ok
        buttonOk = UIUtils.createLeftTextButton(Translation.getText("image.use.button.ok"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        HBox buttonBox = new HBox(buttonOk);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane.setMargin(buttonBox, new Insets(0.0, 0.0, 5.0, 0.0));

        this.setCenter(paneParameters);
        this.setBottom(buttonBox);
    }

    @Override
    public void initListener() {
        //Disable remove when there is no image
        this.paneColorSelection.disableProperty().bind(this.toggleEnableReplaceColor.selectedProperty().not());
        //View port visible only when needed
        MonadicBinding<Boolean> bindUseViewport = EasyBind.select(this.model).selectObject(ImageUseComponentI::useViewPortProperty).orElse(false);
        this.viewportSelectorControl.disableProperty().bind(Bindings.createBooleanBinding(() -> !bindUseViewport.get(), bindUseViewport));//MonadicBinding can't be used as BooleanBinding...
        //Actions
        this.buttonRotateLeft.setOnAction(ev -> {
            ImageUseComponentI imageUseComp = this.model.get();
            ConfigActionController.INSTANCE.executeAction(new KeyActions.ChangeImageRotateAction(imageUseComp, imageUseComp.rotateProperty().get() - 90.0));
        });
        this.buttonRotateRight.setOnAction(ev -> {
            ImageUseComponentI imageUseComp = this.model.get();
            ConfigActionController.INSTANCE.executeAction(new KeyActions.ChangeImageRotateAction(imageUseComp, imageUseComp.rotateProperty().get() + 90.0));
        });
        this.buttonOk.setOnAction(ev -> UIUtils.getSourceWindow(this).hide());
    }

    @Override
    public void initBinding() {
        this.viewportSelectorControl.modelProperty().bind(this.model);
        this.changeListenerPreserveRatio = LCConfigBindingUtils.createSimpleBinding(this.togglePreserveRatio.selectedProperty(), this.model,
                m -> m.preserveRatioProperty().get(), KeyActions.ChangePreserveRatioAction::new);
        this.changeListenerEnableColorReplace = LCConfigBindingUtils.createSimpleBinding(this.toggleEnableReplaceColor.selectedProperty(), this.model,
                m -> m.enableReplaceColorProperty().get(), KeyActions.ChangeEnableReplaceColorAction::new);
        this.changeListenerUseViewport = LCConfigBindingUtils.createSimpleBinding(this.toggleUseViewport.selectedProperty(), this.model,
                m -> m.useViewPortProperty().get(), KeyActions.ChangeUseViewportAction::new);
        this.changeListenerColorToReplace = LCConfigBindingUtils.createSimpleBinding(this.pickerColorToReplace.valueProperty(), this.model,
                m -> m.colorToReplaceProperty().get(), KeyActions.ChangeToReplaceColorAction::new);
        this.changeListenerReplacingColor = LCConfigBindingUtils.createSimpleBinding(this.pickerReplacingColor.valueProperty(), this.model,
                m -> m.replacingColorProperty().get(), KeyActions.ChangeReplacingColorAction::new);
        this.changeListenerReplaceThreshold = LCConfigBindingUtils.createSliderBindingWithScale(0, this.sliderReplaceThreshold, this.model,
                ImageUseComponentI::replaceColorThresholdProperty, (model, nv) -> new KeyActions.ChangeReplaceColorThresholdAction(model, nv.intValue()));
        this.labelRotation.textProperty().bind(TranslationFX.getTextBinding("image.use.rotation.label.current", EasyBind.select(model).selectObject(ImageUseComponentI::rotateProperty).orElse(0.0)));
    }

    @Override
    public void bind(ImageUseComponentI model) {
        model.enableReplaceColorProperty().addListener(this.changeListenerEnableColorReplace);
        model.colorToReplaceProperty().addListener(this.changeListenerColorToReplace);
        model.replacingColorProperty().addListener(this.changeListenerReplacingColor);
        model.preserveRatioProperty().addListener(this.changeListenerPreserveRatio);
        model.useViewPortProperty().addListener(this.changeListenerUseViewport);
        model.replaceColorThresholdProperty().addListener(this.changeListenerReplaceThreshold);
        this.togglePreserveRatio.setSelected(model.preserveRatioProperty().get());
        this.toggleUseViewport.setSelected(model.useViewPortProperty().get());
        this.toggleEnableReplaceColor.setSelected(model.enableReplaceColorProperty().get());
        this.pickerColorToReplace.setValue(model.colorToReplaceProperty().get());
        this.pickerReplacingColor.setValue(model.replacingColorProperty().get());
        this.sliderReplaceThreshold.setValue(model.replaceColorThresholdProperty().get());
    }

    @Override
    public void unbind(ImageUseComponentI model) {
        model.preserveRatioProperty().removeListener(this.changeListenerPreserveRatio);
        model.useViewPortProperty().removeListener(this.changeListenerUseViewport);
        model.enableReplaceColorProperty().removeListener(this.changeListenerEnableColorReplace);
        model.replaceColorThresholdProperty().removeListener(this.changeListenerReplaceThreshold);
        model.colorToReplaceProperty().removeListener(this.changeListenerColorToReplace);
        model.replacingColorProperty().removeListener(this.changeListenerReplacingColor);
    }

    public ObjectProperty<ImageUseComponentI> modelProperty() {
        return model;
    }
}
