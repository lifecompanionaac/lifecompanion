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
package org.lifecompanion.config.view.pane.tips;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.config.tips.AvailableConfigTipsEnum;
import org.lifecompanion.config.data.config.tips.ConfigTipsController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * View to display configuration tips to user.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigTipViewPane extends BorderPane implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigTipViewPane.class);
    private Label labelCurrentTipIndex, labelTipTitle, labelTipMessage, labelMediaPlayerTime;
    private Button buttonClose, buttonPrevious, buttonNext;
    private Button buttonPlay, buttonPause, buttonRestart;
    private AvailableConfigTipsEnum previouslyDisplayed;
    private ProgressIndicator progressIndicatorImage;
    private CheckBox checkboxShowStartup;

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    public ConfigTipViewPane() {
        this.initAll();
    }

    // Class part : "Init UI"
    //========================================================================
    @Override
    public void initUI() {

        //Top : index and title
        labelCurrentTipIndex = new Label();
        this.labelTipTitle = new Label();
        this.labelTipTitle.getStyleClass().add("config-tip-label");
        labelTipTitle.setMaxWidth(Double.MAX_VALUE);
        HBox boxTop = new HBox(10.0, labelCurrentTipIndex, labelTipTitle);
        HBox.setHgrow(labelTipTitle, Priority.ALWAYS);

        //Media content
        mediaView = new MediaView();
        mediaView.fitWidthProperty().bind(this.widthProperty().divide(1.7));
        mediaView.setSmooth(true);

        //Player controls
        labelMediaPlayerTime = new Label("?:??:?? / ?:??:??");
        labelMediaPlayerTime.setAlignment(Pos.BOTTOM_RIGHT);
        labelMediaPlayerTime.setMaxWidth(Double.MAX_VALUE);
        buttonPause = UIUtils
                .createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PAUSE).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY), null);
        buttonPlay = UIUtils
                .createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLAY).sizeFactor(1).color(LCGraphicStyle.SECOND_DARK), null);
        buttonRestart = UIUtils
                .createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.UNDO).sizeFactor(1).color(LCGraphicStyle.MAIN_DARK), null);
        HBox boxMediaControls = new HBox(10, buttonPlay, buttonPause, buttonRestart, labelMediaPlayerTime);
        HBox.setHgrow(labelMediaPlayerTime, Priority.ALWAYS);

        //Media
        VBox boxMediaContent = new VBox(5.0, mediaView, boxMediaControls);
        boxMediaContent.setAlignment(Pos.CENTER);

        //Tip message
        this.labelTipMessage = new Label();
        labelTipMessage.getStyleClass().add("config-tip-message-label");

        //Center : content
        HBox boxCenter = new HBox(5.0, boxMediaContent, new Separator(Orientation.VERTICAL), labelTipMessage);
        boxCenter.setAlignment(Pos.CENTER);
        BorderPane.setMargin(boxCenter, new Insets(10.0));

        //Buttons
        buttonClose = UIUtils.createLeftTextButton(Translation.getText("config.tip.close.button"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TIMES).sizeFactor(1).color(LCGraphicStyle.SECOND_PRIMARY),
                "tooltip.config.tip.close.button");
        buttonPrevious = UIUtils.createLeftTextButton(Translation.getText("config.tip.previous.button"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_LEFT).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.config.tip.previous.button");
        buttonNext = UIUtils.createRightTextButton(Translation.getText("config.tip.next.button"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.config.tip.next.button");
        progressIndicatorImage = new ProgressIndicator(-1);
        progressIndicatorImage.setPrefSize(40, 40);
        checkboxShowStartup = new CheckBox(Translation.getText("display.tips.stage.on.startup"));
        HBox boxButtons = new HBox(10.0, buttonClose, checkboxShowStartup, progressIndicatorImage, buttonPrevious, buttonNext);
        boxButtons.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(progressIndicatorImage, Priority.ALWAYS);

        this.setPadding(new Insets(15.0));
        this.setTop(boxTop);
        this.setCenter(boxCenter);
        this.setBottom(boxButtons);
    }

    @Override
    public void initBinding() {
        ConfigTipsController.INSTANCE.currentDisplayedTipIndexProperty().addListener((obs, ov, nv) -> this.displayTip(nv.intValue()));
    }

    public void updateModel() {
        this.checkboxShowStartup.selectedProperty().set(UserBaseConfiguration.INSTANCE.showTipsOnStartupProperty().get());
    }

    public boolean isShowOnStartupEnabled() {
        return this.checkboxShowStartup.isSelected();
    }

    @Override
    public void initListener() {
        this.buttonClose.setOnAction(e -> ConfigTipsController.INSTANCE.hideConfigTipsStage());
        this.buttonPrevious.setOnAction(e -> ConfigTipsController.INSTANCE.displayPrevious());
        this.buttonNext.setOnAction(e -> ConfigTipsController.INSTANCE.displayNext());
        this.mediaView.setOnMouseClicked(e -> this.doOnMediaPlayerIfExist(mediaPlayer -> {
            if (mediaPlayer.getStatus() != Status.PAUSED) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }));
        this.buttonPause.setOnAction(e -> this.doOnMediaPlayerIfExist(mp -> mp.pause()));
        this.buttonPlay.setOnAction(e -> this.doOnMediaPlayerIfExist(mp -> mp.play()));
        this.buttonRestart.setOnAction(e -> this.doOnMediaPlayerIfExist(mp -> {
            mp.setOnStopped(() -> {
                mp.setOnStopped(null);
                mp.play();
            });
            mp.stop();
        }));
        //changeListenerMediaStatus = (obs, ov, nv) -> this.updateMediaPlayerStatus(nv);
        this.mediaView.mediaPlayerProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                buttonPause.disableProperty().unbind();
                buttonPlay.disableProperty().unbind();
                labelMediaPlayerTime.textProperty().unbind();
            }
            if (nv != null) {
                buttonPause.disableProperty().bind(nv.statusProperty().isEqualTo(Status.PAUSED));
                buttonPlay.disableProperty().bind(nv.statusProperty().isEqualTo(Status.PLAYING));
                labelMediaPlayerTime.textProperty().bind(Bindings.createStringBinding(() -> {
                    Duration current = nv.currentTimeProperty().get();
                    Duration total = nv.getMedia() != null ? nv.getMedia().getDuration() : null;
                    return LCUtils.durationToString(current) + " / " + LCUtils.durationToString(total);
                }, nv.currentTimeProperty()));
            }
        });
    }

    private void doOnMediaPlayerIfExist(Consumer<MediaPlayer> action) {
        if (this.mediaPlayer != null) {
            action.accept(mediaPlayer);
        }
    }

    private void displayTip(int tipIndex) {
        progressIndicatorImage.setVisible(true);

        //Clear previous media (free memory)
        if (this.mediaPlayer != null) {
            this.mediaPlayer.dispose();
            this.mediaView.setMediaPlayer(null);
            LOGGER.debug("Previous media player disposed in config tips view");
        }

        //Display new
        AvailableConfigTipsEnum[] list = AvailableConfigTipsEnum.values();
        if (tipIndex >= 0 && tipIndex < list.length) {
            previouslyDisplayed = list[tipIndex];
            this.labelCurrentTipIndex.setText((tipIndex + 1) + " / " + list.length);
            this.labelTipTitle.setText(previouslyDisplayed.getTitle());
            this.labelTipMessage.setText(previouslyDisplayed.getMessage());
            if (previouslyDisplayed.mediaExists()) {
                //                AppController.INSTANCE.getUseActionThreadPool().submit(() -> {
                //                    try {
                //                        Media media = new Media(new File(previouslyDisplayed.getMediaPath()).toURI().toString());
                //                        this.mediaPlayer = new MediaPlayer(media);
                //                        this.mediaPlayer.setAutoPlay(true);
                //                        this.mediaPlayer.setMute(true);
                //                        this.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                //                        LCUtils.runOnFXThread(() -> {
                //                            LOGGER.debug("Media player created, will set it to media view");
                //                            this.mediaView.setMediaPlayer(mediaPlayer);
                //                            progressIndicatorImage.setVisible(false);
                //                        });
                //                    } catch (Exception e) {
                //                        LOGGER.warn("Couldn't load tip image with path {}", previouslyDisplayed.getMediaPath(), e);
                //                    }
                //                });
            }
        }
    }
    //========================================================================

}
