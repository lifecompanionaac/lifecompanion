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

package org.lifecompanion.ui.common.control.specific.imagedictionary;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.lifecompanion.controller.editaction.KeyActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.VideoDisplayMode;
import org.lifecompanion.model.api.configurationcomponent.VideoPlayMode;
import org.lifecompanion.model.api.configurationcomponent.VideoUseComponentI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.ui.common.pane.generic.BaseConfigurationViewBorderPane;
import org.lifecompanion.ui.common.pane.specific.cell.SimpleTextListCell;
import org.lifecompanion.ui.common.pane.specific.cell.VideoDisplayModeListCell;
import org.lifecompanion.ui.common.pane.specific.cell.VideoPlayModeListCell;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.controlsfx.glyphfont.Glyph;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.List;

public class ImageUseComponentConfigurationView extends BaseConfigurationViewBorderPane<VideoUseComponentI> implements LCViewInitHelper {
    /**
     * Button to rotate image
     */
    private Button buttonRotateLeft, buttonRotateRight, buttonMirrorHorizontal, buttonMirrorVertical;

    /**
     * Button to hide the stage
     */
    private Button buttonOk;

    private Button buttonConfigureViewport;

    /**
     * Toggle to enable/disable preserve ratio
     */
    private ToggleSwitch togglePreserveRatio;

    /**
     * Toggle to enable/disable viewport use
     */
    private ToggleSwitch toggleUseViewport;

    private ToggleSwitch toggleDisplayFullscreen;

    /**
     * Toggle to enable/disable color change
     */
    private ToggleSwitch toggleEnableReplaceColor, toggleEnableColorToGrey, toggleEnableRemoveBackground;

    private ToggleSwitch toggleSwitchMuteVideo;
    private ComboBox<VideoDisplayMode> comboBoxVideoDisplayMode;
    private ComboBox<VideoPlayMode> comboBoxVideoPlayMode;

    /**
     * Color picker to select color to replace
     */
    private LCColorPicker pickerColorToReplace, pickerReplacingColor;

    /**
     * change listener colors
     */
    private ChangeListener<Color> changeListenerColorToReplace, changeListenerReplacingColor;

    /**
     * Change listener for preserve ratio property/enable color replace
     */
    private ChangeListener<Boolean> changeListenerPreserveRatio, changeListenerEnableColorReplace, changeListenerEnableColorToGrey, changeListenerEnableRemoveBackground, changeListenerVideoMute, changeListenerDisplayFullscreen;

    private ChangeListener<VideoDisplayMode> changeListenerVideoDisplayMode;
    private ChangeListener<VideoPlayMode> changeListenerVideoPlayMode;

    /**
     * Change listener to replace color threshold
     */
    private ChangeListener<Number> changeListenerReplaceThreshold, changeListenerRemoveBackgroundThreshold;

    /**
     * Slider for replace color threshold
     */
    private Slider sliderReplaceThreshold, sliderRemoveBackgroundThreshold;

    /**
     * Change listener for viewport changes
     */
    private ChangeListener<Boolean> changeListenerUseViewport;

    /**
     * Viewport configuration
     */
    private ViewportSelectorStage viewportSelectorStage;

    private ImageView imageViewPreview;

    private List<Node> imageOnlyNodes, videoOnlyNodes;
    private Label labelTitle;
    private GridPane paneColorReplace;

    public ImageUseComponentConfigurationView() {
        initAll();
    }

    @Override
    public void initUI() {
        labelTitle = new Label();
        labelTitle.getStyleClass().addAll("text-weight-bold", "text-font-size-120", "text-label-center", "text-fill-dimgrey");
        HBox boxTitle = new HBox(labelTitle);
        boxTitle.setAlignment(Pos.CENTER);
        boxTitle.getStyleClass().addAll("border-bottom-lightgrey");
        BorderPane.setMargin(boxTitle, new Insets(5));

        // Preview
        this.imageViewPreview = new ImageView();
        this.imageViewPreview.setFitHeight(300);
        this.imageViewPreview.setFitWidth(300);
        StackPane imageViewContainer = new StackPane(imageViewPreview);
        imageViewContainer.getStyleClass().addAll("border-lightgrey", "border-width-1-right");

        //Create buttons
        Label labelTitleRatioRotate = FXControlUtils.createTitleLabel(Translation.getText("image.use.config.part.ratio.rotate.title"));
        this.togglePreserveRatio = FXControlUtils.createToggleSwitch("image.use.preserve.ratio", "tooltip.explain.image.preserve.ratio");
        //Advanced parameters
        this.buttonRotateLeft = FXControlUtils.createTextButtonWithGraphics(Translation.getText("rotate.image.left"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.ROTATE_LEFT).size(20.0).color(LCGraphicStyle.SECOND_DARK),
                "tooltip.rotate.image.left");
        GridPane.setHalignment(buttonRotateLeft, HPos.CENTER);
        this.buttonRotateRight = FXControlUtils.createTextButtonWithGraphics(Translation.getText("rotate.image.right"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.ROTATE_RIGHT).size(20.0).color(LCGraphicStyle.SECOND_DARK),
                "tooltip.rotate.image.right");
        GridPane.setHalignment(buttonRotateRight, HPos.CENTER);
        Glyph iconMirrorImageVertical = GlyphFontHelper.FONT_MATERIAL.create('\ue3e8').size(20.0).color(LCGraphicStyle.SECOND_DARK);
        iconMirrorImageVertical.setRotate(90);
        this.buttonMirrorHorizontal = FXControlUtils.createTextButtonWithGraphics(Translation.getText("mirror.image.horizontal"),
                GlyphFontHelper.FONT_MATERIAL.create('\ue3e8').size(20.0).color(LCGraphicStyle.SECOND_DARK),
                "tooltip.mirror.image.horizontal");
        GridPane.setHalignment(buttonMirrorHorizontal, HPos.CENTER);
        this.buttonMirrorVertical = FXControlUtils.createTextButtonWithGraphics(Translation.getText("mirror.image.vertical"),
                iconMirrorImageVertical,
                "tooltip.mirror.image.vertical");
        GridPane.setHalignment(buttonMirrorVertical, HPos.CENTER);

        GridPane rotateAndMirrorNode = new GridPane();
        VBox.setMargin(rotateAndMirrorNode, new Insets(5, 0, 0, 0));
        rotateAndMirrorNode.setVgap(10.0);
        rotateAndMirrorNode.setHgap(10.0);
        rotateAndMirrorNode.add(buttonRotateLeft, 0, 0);
        rotateAndMirrorNode.add(buttonRotateRight, 1, 0);
        rotateAndMirrorNode.add(buttonMirrorHorizontal, 0, 1);
        rotateAndMirrorNode.add(buttonMirrorVertical, 1, 1);

        // Video configuration
        Label labelTitleVideo = FXControlUtils.createTitleLabel(Translation.getText("image.use.config.part.video.title"));
        toggleSwitchMuteVideo = FXControlUtils.createToggleSwitch("image.use.video.mute.toggle",
                null);
        this.comboBoxVideoDisplayMode = new ComboBox<>(FXCollections.observableArrayList(VideoDisplayMode.values()));
        this.comboBoxVideoDisplayMode.setCellFactory(lv -> new VideoDisplayModeListCell());
        this.comboBoxVideoDisplayMode.setButtonCell(new SimpleTextListCell<>(VideoDisplayMode::getTitle));
        this.comboBoxVideoDisplayMode.setPrefWidth(180);
        this.comboBoxVideoPlayMode = new ComboBox<>(FXCollections.observableArrayList(VideoPlayMode.values()));
        this.comboBoxVideoPlayMode.setCellFactory(lv -> new VideoPlayModeListCell());
        this.comboBoxVideoPlayMode.setButtonCell(new SimpleTextListCell<>(VideoPlayMode::getTitle));
        this.comboBoxVideoPlayMode.setPrefWidth(180);
        GridPane paneVideoConfig = new GridPane();
        paneVideoConfig.setVgap(3.0);
        Label labelVideoDisplayMode = new Label(Translation.getText("image.use.video.display.mode"));
        paneVideoConfig.add(labelVideoDisplayMode, 0, 0);
        GridPane.setHgrow(labelVideoDisplayMode, Priority.ALWAYS);
        paneVideoConfig.add(comboBoxVideoDisplayMode, 1, 0);
        Label labelPlayMode = new Label(Translation.getText("image.use.video.play.mode"));
        paneVideoConfig.add(labelPlayMode, 0, 1);
        paneVideoConfig.add(comboBoxVideoPlayMode, 1, 1);
        labelPlayMode.disableProperty().bind(comboBoxVideoPlayMode.disabledProperty());

        //Remove background
        Label labelTitleRemoveBackground = FXControlUtils.createTitleLabel(Translation.getText("image.use.config.part.remove.background.title"));
        this.toggleEnableRemoveBackground = FXControlUtils.createToggleSwitch("image.use.enable.remove.background",
                "tooltip.explain.image.enable.remove.background");
        Label labelBackgroundThreshold = new Label(Translation.getText("image.use.color.replace.threshold.field"));
        this.sliderRemoveBackgroundThreshold = FXControlUtils.createBaseSlider(0.0, 200.0, 10.0);
        FXControlUtils.createAndAttachTooltip(sliderRemoveBackgroundThreshold, "tooltip.explain.image.color.replace.threshold");
        this.sliderRemoveBackgroundThreshold.setMajorTickUnit(20);
        BorderPane borderPaneRemoveBackgroundThreshold = new BorderPane();
        borderPaneRemoveBackgroundThreshold.setLeft(labelBackgroundThreshold);
        borderPaneRemoveBackgroundThreshold.setRight(sliderRemoveBackgroundThreshold);

        //Color replace
        Label labelTitleColorReplace = FXControlUtils.createTitleLabel(Translation.getText("image.use.config.part.color.replace.title"));
        this.toggleEnableColorToGrey = FXControlUtils.createToggleSwitch("image.use.enable.grey.color",
                "tooltip.explain.image.enable.grey.color");
        this.toggleEnableReplaceColor = FXControlUtils.createToggleSwitch("image.use.enable.color.replace",
                "tooltip.explain.image.enable.color.replace");
        VBox.setMargin(toggleEnableReplaceColor, new Insets(5, 0, 0, 0));
        this.pickerColorToReplace = new LCColorPicker();
        FXControlUtils.createAndAttachTooltip(pickerColorToReplace, "tooltip.explain.image.color.replace");
        this.pickerReplacingColor = new LCColorPicker();
        FXControlUtils.createAndAttachTooltip(pickerReplacingColor, "tooltip.explain.image.color.replacing");
        this.sliderReplaceThreshold = FXControlUtils.createBaseSlider(0.0, 200.0, 10.0);
        FXControlUtils.createAndAttachTooltip(sliderReplaceThreshold, "tooltip.explain.image.color.replace.threshold");
        this.sliderReplaceThreshold.setMajorTickUnit(20);
        paneColorReplace = new GridPane();
        paneColorReplace.setVgap(3.0);
        Label labelColorToReplace = new Label(Translation.getText("image.use.color.to.replace.field"));
        paneColorReplace.add(labelColorToReplace, 0, 0);
        paneColorReplace.add(this.pickerColorToReplace, 1, 0);
        paneColorReplace.add(new Label(Translation.getText("image.use.color.replacing.field")), 0, 1);
        paneColorReplace.add(this.pickerReplacingColor, 1, 1);
        paneColorReplace.add(new Label(Translation.getText("image.use.color.replace.threshold.field")), 0, 2);
        paneColorReplace.add(this.sliderReplaceThreshold, 1, 2);
        GridPane.setHgrow(labelColorToReplace, Priority.ALWAYS);

        // Fullscreen
        toggleDisplayFullscreen = FXControlUtils.createToggleSwitch("image.use.use.display.fullscreen", "tooltip.image.use.use.display.fullscreen");

        //Viewport selector
        Label labelTitleViewport = FXControlUtils.createTitleLabel(Translation.getText("image.use.config.part.viewport.title"));
        this.toggleUseViewport = FXControlUtils.createToggleSwitch("image.use.use.viewport", "tooltip.explain.image.use.viewport");
        buttonConfigureViewport = FXControlUtils.createLeftTextButton(Translation.getText("image.configure.viewport"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GEARS).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        HBox boxConfigureViewPort = new HBox(buttonConfigureViewport);
        boxConfigureViewPort.setAlignment(Pos.CENTER);
        this.viewportSelectorStage = new ViewportSelectorStage();

        // Button ok
        buttonOk = FXControlUtils.createLeftTextButton(Translation.getText("image.use.button.ok"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        HBox buttonBox = new HBox(buttonOk);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane.setMargin(buttonBox, new Insets(0.0, 0.0, 5.0, 0.0));

        imageOnlyNodes = List.of(labelTitleRemoveBackground,
                toggleEnableRemoveBackground,
                borderPaneRemoveBackgroundThreshold,
                labelTitleColorReplace,
                toggleEnableColorToGrey,
                toggleEnableReplaceColor,
                paneColorReplace,
                labelTitleViewport,
                toggleDisplayFullscreen,
                toggleUseViewport,
                boxConfigureViewPort);
        videoOnlyNodes = List.of(labelTitleVideo, toggleSwitchMuteVideo, paneVideoConfig);

        // Total
        VBox paneParameters = new VBox(5.0,
                labelTitleRatioRotate,
                toggleDisplayFullscreen,
                togglePreserveRatio,
                rotateAndMirrorNode,
                labelTitleVideo,
                toggleSwitchMuteVideo,
                paneVideoConfig,
                labelTitleRemoveBackground,
                toggleEnableRemoveBackground,
                borderPaneRemoveBackgroundThreshold,
                labelTitleColorReplace,
                toggleEnableColorToGrey,
                toggleEnableReplaceColor,
                paneColorReplace,
                labelTitleViewport,
                toggleUseViewport,
                boxConfigureViewPort
        );
        paneParameters.setAlignment(Pos.CENTER_LEFT);
        paneParameters.setPadding(new Insets(5.0));

        ScrollPane scrollPaneParameters = new ScrollPane(paneParameters);
        scrollPaneParameters.setFitToWidth(true);
        scrollPaneParameters.setMinWidth(340.0);

        this.setTop(boxTitle);
        this.setCenter(imageViewContainer);
        this.setRight(scrollPaneParameters);
        this.setBottom(buttonBox);
    }

    @Override
    public void initListener() {
        this.sliderRemoveBackgroundThreshold.disableProperty().bind(this.toggleEnableRemoveBackground.selectedProperty().not());
        this.paneColorReplace.disableProperty().bind(this.toggleEnableReplaceColor.selectedProperty().not());
        this.comboBoxVideoPlayMode.disableProperty().bind(this.comboBoxVideoDisplayMode.getSelectionModel().selectedItemProperty().isNotEqualTo(VideoDisplayMode.IN_KEY));
        //Actions
        this.buttonRotateLeft.setOnAction(ev -> {
            ImageUseComponentI imageUseComp = this.model.get();
            ConfigActionController.INSTANCE.executeAction(new KeyActions.ChangeImageRotateAction(imageUseComp, imageUseComp.rotateProperty().get() - 90.0));
        });
        this.buttonRotateRight.setOnAction(ev -> {
            ImageUseComponentI imageUseComp = this.model.get();
            ConfigActionController.INSTANCE.executeAction(new KeyActions.ChangeImageRotateAction(imageUseComp, imageUseComp.rotateProperty().get() + 90.0));
        });
        this.buttonMirrorHorizontal.setOnAction(ev -> {
            ImageUseComponentI imageUseComp = this.model.get();
            ConfigActionController.INSTANCE.executeAction(new KeyActions.FlipImageHorizontalAction(imageUseComp, imageUseComp.scaleXProperty().get() * -1));
        });
        this.buttonMirrorVertical.setOnAction(ev -> {
            ImageUseComponentI imageUseComp = this.model.get();
            ConfigActionController.INSTANCE.executeAction(new KeyActions.FlipImageVerticalAction(imageUseComp, imageUseComp.scaleYProperty().get() * -1));

        });
        this.toggleEnableColorToGrey.selectedProperty().addListener((obs, ov, nv) -> {
            ImageUseComponentI imageUseComp = this.model.get();
            ConfigActionController.INSTANCE.executeAction(new KeyActions.ChangeEnableColorToGreyAction(imageUseComp, this.toggleEnableColorToGrey.selectedProperty().get()));
        });
        this.toggleDisplayFullscreen.selectedProperty().addListener((obs, ov, nv) -> {
            ImageUseComponentI imageUseComp = this.model.get();
            ConfigActionController.INSTANCE.executeAction(new KeyActions.ChangeDisplayInFullscreenImageAction(imageUseComp, this.toggleEnableColorToGrey.selectedProperty().get()));
        });
        this.buttonConfigureViewport.disableProperty().bind(toggleUseViewport.selectedProperty().not());
        this.buttonOk.setOnAction(ev -> FXUtils.getSourceWindow(this).hide());
        this.toggleUseViewport.selectedProperty().addListener((obs, ov, nv) -> {
            if (nv && !binding) {
                buttonConfigureViewport.fire();
            }
        });
        this.buttonConfigureViewport.setOnAction(e -> this.viewportSelectorStage.prepareAndShow(this.model.get()));
    }

    @Override
    public void initBinding() {
        this.changeListenerPreserveRatio = EditActionUtils.createSimpleBinding(this.togglePreserveRatio.selectedProperty(), this.model,
                m -> m.preserveRatioProperty().get(), KeyActions.ChangePreserveRatioAction::new);
        this.changeListenerEnableRemoveBackground = EditActionUtils.createSimpleBinding(this.toggleEnableRemoveBackground.selectedProperty(), this.model,
                m -> m.enableRemoveBackgroundProperty().get(), KeyActions.ChangeEnableRemoveBackground::new);
        this.changeListenerRemoveBackgroundThreshold = EditActionUtils.createSliderBindingWithScale(0, this.sliderRemoveBackgroundThreshold, this.model,
                ImageUseComponentI::removeBackgroundThresholdProperty, (model, nv) -> new KeyActions.RemoveBackgroundThresholdAction(model, nv.intValue()));
        this.changeListenerEnableColorToGrey = EditActionUtils.createSimpleBinding(this.toggleEnableColorToGrey.selectedProperty(), this.model,
                m -> m.enableColorToGreyProperty().get(), KeyActions.ChangeEnableColorToGreyAction::new);
        this.changeListenerDisplayFullscreen = EditActionUtils.createSimpleBinding(this.toggleDisplayFullscreen.selectedProperty(), this.model,
                m -> m.displayInFullScreenProperty().get(), KeyActions.ChangeDisplayInFullscreenImageAction::new);
        this.changeListenerEnableColorReplace = EditActionUtils.createSimpleBinding(this.toggleEnableReplaceColor.selectedProperty(), this.model,
                m -> m.enableReplaceColorProperty().get(), KeyActions.ChangeEnableReplaceColorAction::new);
        this.changeListenerUseViewport = EditActionUtils.createSimpleBinding(this.toggleUseViewport.selectedProperty(), this.model,
                m -> m.useViewPortProperty().get(), KeyActions.ChangeUseViewportAction::new);
        this.changeListenerColorToReplace = EditActionUtils.createSimpleBinding(this.pickerColorToReplace.valueProperty(), this.model,
                m -> m.colorToReplaceProperty().get(), KeyActions.ChangeToReplaceColorAction::new);
        this.changeListenerReplacingColor = EditActionUtils.createSimpleBinding(this.pickerReplacingColor.valueProperty(), this.model,
                m -> m.replacingColorProperty().get(), KeyActions.ChangeReplacingColorAction::new);
        this.changeListenerReplaceThreshold = EditActionUtils.createSliderBindingWithScale(0, this.sliderReplaceThreshold, this.model,
                ImageUseComponentI::replaceColorThresholdProperty, (model, nv) -> new KeyActions.ChangeReplaceColorThresholdAction(model, nv.intValue()));
        this.changeListenerVideoDisplayMode = EditActionUtils.createSelectionModelBinding(this.comboBoxVideoDisplayMode.getSelectionModel(), //
                this.model, model -> model.videoDisplayModeProperty().get(), //
                KeyActions.ChangeVideoDisplayMode::new);
        this.changeListenerVideoPlayMode = EditActionUtils.createSelectionModelBinding(this.comboBoxVideoPlayMode.getSelectionModel(), //
                this.model, model -> model.videoPlayModeProperty().get(), //
                KeyActions.ChangeVideoPlayMode::new);
        this.changeListenerVideoMute = EditActionUtils.createSimpleBinding(this.toggleSwitchMuteVideo.selectedProperty(), this.model,
                m -> m.muteVideoProperty().get(), KeyActions.ChangeVideoMute::new);
        bindManagedOnVisible(imageOnlyNodes);
        bindManagedOnVisible(videoOnlyNodes);
    }

    private void bindManagedOnVisible(List<Node> nodes) {
        for (Node node : nodes) {
            node.managedProperty().bind(node.visibleProperty());
        }
    }

    private boolean binding = false;

    @Override
    public void bind(VideoUseComponentI model) {
        binding = true;
        ConfigurationComponentUtils.bindImageViewWithImageUseComponent(this.imageViewPreview, model);
        model.enableRemoveBackgroundProperty().addListener(this.changeListenerEnableRemoveBackground);
        model.removeBackgroundThresholdProperty().addListener(this.changeListenerRemoveBackgroundThreshold);
        model.enableColorToGreyProperty().addListener(this.changeListenerEnableColorToGrey);
        model.enableReplaceColorProperty().addListener(this.changeListenerEnableColorReplace);
        model.colorToReplaceProperty().addListener(this.changeListenerColorToReplace);
        model.replacingColorProperty().addListener(this.changeListenerReplacingColor);
        model.preserveRatioProperty().addListener(this.changeListenerPreserveRatio);
        model.useViewPortProperty().addListener(this.changeListenerUseViewport);
        model.replaceColorThresholdProperty().addListener(this.changeListenerReplaceThreshold);
        model.videoDisplayModeProperty().addListener(this.changeListenerVideoDisplayMode);
        model.videoPlayModeProperty().addListener(this.changeListenerVideoPlayMode);
        model.muteVideoProperty().addListener(this.changeListenerVideoMute);
        model.displayInFullScreenProperty().addListener(this.changeListenerDisplayFullscreen);
        this.togglePreserveRatio.setSelected(model.preserveRatioProperty().get());
        this.toggleUseViewport.setSelected(model.useViewPortProperty().get());
        this.toggleEnableRemoveBackground.setSelected(model.enableRemoveBackgroundProperty().get());
        this.sliderRemoveBackgroundThreshold.setValue(model.removeBackgroundThresholdProperty().get());
        this.toggleEnableColorToGrey.setSelected(model.enableColorToGreyProperty().get());
        this.toggleEnableReplaceColor.setSelected(model.enableReplaceColorProperty().get());
        this.pickerColorToReplace.setValue(model.colorToReplaceProperty().get());
        this.pickerReplacingColor.setValue(model.replacingColorProperty().get());
        this.sliderReplaceThreshold.setValue(model.replaceColorThresholdProperty().get());
        this.comboBoxVideoDisplayMode.getSelectionModel().select(model.videoDisplayModeProperty().get());
        this.comboBoxVideoPlayMode.getSelectionModel().select(model.videoPlayModeProperty().get());
        this.toggleSwitchMuteVideo.setSelected(model.muteVideoProperty().get());
        this.toggleDisplayFullscreen.setSelected(model.displayInFullScreenProperty().get());
        imageOnlyNodes.forEach(n -> n.setVisible(model.videoProperty().get() == null));
        videoOnlyNodes.forEach(n -> n.setVisible(model.videoProperty().get() != null));
        labelTitle.setText(Translation.getText("image.use.config.view.title.for." + (model.videoProperty().get() != null ? "video" : "image")));
        binding = false;
    }

    @Override
    public void unbind(VideoUseComponentI model) {
        ConfigurationComponentUtils.unbindImageViewFromImageUseComponent(this.imageViewPreview);
        model.preserveRatioProperty().removeListener(this.changeListenerPreserveRatio);
        model.useViewPortProperty().removeListener(this.changeListenerUseViewport);
        model.enableColorToGreyProperty().removeListener(this.changeListenerEnableColorToGrey);
        model.enableRemoveBackgroundProperty().removeListener(this.changeListenerEnableRemoveBackground);
        model.removeBackgroundThresholdProperty().removeListener(this.changeListenerRemoveBackgroundThreshold);
        model.enableReplaceColorProperty().removeListener(this.changeListenerEnableColorReplace);
        model.replaceColorThresholdProperty().removeListener(this.changeListenerReplaceThreshold);
        model.colorToReplaceProperty().removeListener(this.changeListenerColorToReplace);
        model.replacingColorProperty().removeListener(this.changeListenerReplacingColor);
        model.videoDisplayModeProperty().removeListener(this.changeListenerVideoDisplayMode);
        model.videoPlayModeProperty().removeListener(this.changeListenerVideoPlayMode);
        model.muteVideoProperty().removeListener(this.changeListenerVideoMute);
        model.displayInFullScreenProperty().removeListener(this.changeListenerDisplayFullscreen);
    }

    public ObjectProperty<VideoUseComponentI> modelProperty() {
        return model;
    }
}
