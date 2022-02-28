/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.ui.easteregg;

import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import javafx.animation.*;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.ui.configurationcomponent.usemode.UseModeConfigurationDisplayer;
import org.lifecompanion.util.javafx.FXThreadUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JPDRetirementView extends BorderPane implements LCViewInitHelper {
    private final UseModeConfigurationDisplayer useModeConfigurationDisplayer;
    private final ReadOnlyDoubleProperty wantedWidth, wantedHeight;

    public JPDRetirementView(UseModeConfigurationDisplayer useModeConfigurationDisplayer, ReadOnlyDoubleProperty wantedWidth, ReadOnlyDoubleProperty wantedHeight) {
        this.useModeConfigurationDisplayer = useModeConfigurationDisplayer;
        this.wantedWidth = wantedWidth;
        this.wantedHeight = wantedHeight;
        initAll();
    }

    @Override
    public void initUI() {
        this.setCenter(new Pane());
        this.prefWidthProperty().bind(wantedWidth);
        this.prefHeightProperty().bind(wantedHeight);
        this.setStyle("-fx-background-color: #0f0f0f;");
    }

    @Override
    public void initListener() {
        LCViewInitHelper.super.initListener();
    }

    @Override
    public void initBinding() {
        LCViewInitHelper.super.initBinding();
    }

    public void launchFirstStep() {
        Media introVideo = new Media(new File("D:\\Dev\\jpd-day\\intro-video_v2.mp4").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(introVideo);
        MediaView mediaView = new MediaView(mediaPlayer); // FIXME : be sure that it will be loaded ?


        FXThreadUtils.runOnFXThread(() -> {
            ImageView imageView = new ImageView(baseViewSnapshot);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(wantedWidth.get());
            imageView.setFitHeight(wantedHeight.get());
            imageView.setCache(true);
            imageView.setCacheHint(CacheHint.SPEED);
            setCenter(imageView);

            int timeInSecond = 10;
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(timeInSecond), imageView);
            rotateTransition.setToAngle(360 * 10);
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(timeInSecond), imageView);
            scaleTransition.setToX(0);
            scaleTransition.setToY(0);
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(timeInSecond), imageView);
            fadeTransition.setToValue(0.1);
            ParallelTransition parallelTransition = new ParallelTransition(rotateTransition, scaleTransition, fadeTransition);// FIXME : enable
            parallelTransition.setInterpolator(Interpolator.SPLINE(0.7, 0, 0.9, 0.9));
            parallelTransition.play();

            parallelTransition.setOnFinished(e -> {
                mediaView.setFitWidth(wantedWidth.get());
                mediaView.setFitHeight(wantedHeight.get());
                mediaView.setPreserveRatio(true);
                mediaView.setOpacity(0);
                mediaPlayer.setVolume(0.0);
                FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(2), mediaView);
                fadeTransition1.setToValue(0.9);
                setCenter(mediaView);

                AtomicBoolean endReached = new AtomicBoolean();
                mediaPlayer.currentTimeProperty().addListener((obs, ov, nv) -> {
                    if (nv.greaterThan(Duration.seconds(20)) && !endReached.getAndSet(true)) {//FIXME : correct duration
                        FadeTransition fadeTransition2 = new FadeTransition(Duration.seconds(3), mediaView);
                        fadeTransition2.setToValue(0.0);
                        fadeTransition2.setOnFinished(e3 -> {
                            mediaPlayer.stop();
                            mediaPlayer.dispose();
                            startTextTransition();
                        });
                        fadeTransition2.play();
                    }
                });
                mediaPlayer.play();
                fadeTransition1.play();
            });
        });
    }

    private void startTextTransition() {
        List<Text> texts = Arrays.asList(
                new Text("Préparez vous\n"),
                new Text("à un voyage au coeur\n"),
                new Text("des aides à la communication"));
        SequentialTransition sequentialTransition = new SequentialTransition();
        texts.forEach(t -> {
            t.setOpacity(0.0);
            t.setFill(Color.WHITE);
            Reflection effect = new Reflection(10.0, 0.1, 0.7, 0.0);
            effect.setInput(new Bloom(0.5));
            t.setEffect(effect);
            t.setFont(Font.font("Deja Vu Sans", FontWeight.BOLD, 70));
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2.0), t);
            fadeTransition.setDelay(Duration.seconds(0.8));
            fadeTransition.setToValue(1.0);
            sequentialTransition.getChildren().add(fadeTransition);
        });
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().addAll(texts);
        textFlow.setLineSpacing(40.0);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        VBox box = new VBox(textFlow);
        box.setAlignment(Pos.CENTER);
        setCenter(box);
        sequentialTransition.play();
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(10), textFlow);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.play();
    }

    private WritableImage baseViewSnapshot;

    public void setBaseViewSnapshot(WritableImage snapshot) {
        this.baseViewSnapshot = snapshot;
    }
}
