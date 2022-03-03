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
import javafx.scene.Node;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.Duration;
import org.lifecompanion.controller.easteregg.JPDRetirementController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.ui.configurationcomponent.usemode.UseModeConfigurationDisplayer;
import org.lifecompanion.util.javafx.ColorUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JPDRetirementView extends BorderPane implements LCViewInitHelper {
    private final UseModeConfigurationDisplayer useModeConfigurationDisplayer;
    private final ReadOnlyDoubleProperty wantedWidth, wantedHeight;

    private ImageView imageView;

    public JPDRetirementView(UseModeConfigurationDisplayer useModeConfigurationDisplayer, ReadOnlyDoubleProperty wantedWidth, ReadOnlyDoubleProperty wantedHeight) {
        this.useModeConfigurationDisplayer = useModeConfigurationDisplayer;
        this.wantedWidth = wantedWidth;
        this.wantedHeight = wantedHeight;
        initAll();
    }

    @Override
    public void initUI() {
        imageView = new ImageView();
        this.setCenter(imageView);
        this.prefWidthProperty().bind(wantedWidth);
        this.prefHeightProperty().bind(wantedHeight);
    }

    @Override
    public void initListener() {
        LCViewInitHelper.super.initListener();
    }

    @Override
    public void initBinding() {
        LCViewInitHelper.super.initBinding();
    }

    public void initBeforeShow(WritableImage snapshot) {
        Color backgroundColor = AppModeController.INSTANCE.getUseModeContext().getConfiguration().backgroundColorProperty().get();
        this.setStyle("-fx-background-color: " + ColorUtils.toCssColor(backgroundColor) + ";");
        imageView.setImage(snapshot);
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);
    }

    public void launchFirstStep() {
        this.useModeConfigurationDisplayer.showJPDRetirementView();
        MediaView mediaView = new MediaView(JPDRetirementController.INSTANCE.getMediaPlayerIntroVideo());
        FXThreadUtils.runOnFXThread(() -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(20), imageView);
            fadeTransition.setToValue(0);
            fadeTransition.setCycleCount(200);
            fadeTransition.setAutoReverse(true);
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(40), imageView);
            translateTransition.setByY(-8);
            translateTransition.setByX(-3);
            translateTransition.setCycleCount(100);
            translateTransition.setAutoReverse(true);

            ParallelTransition parallelTransition = new ParallelTransition(translateTransition, fadeTransition);
            parallelTransition.setDelay(Duration.seconds(0.2));
            parallelTransition.setInterpolator(Interpolator.SPLINE(0.7, 0, 0.9, 0.9));
            parallelTransition.play();
            JPDRetirementController.INSTANCE.getMediaPlayerGlitchSound().play();

            parallelTransition.setOnFinished(e -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.8), imageView);
                scaleTransition.setToX(0);
                scaleTransition.setToY(0);
                parallelTransition.setInterpolator(Interpolator.SPLINE(0.7, 0, 0.9, 0.9));
                scaleTransition.setOnFinished(e1 -> {
                    mediaView.setFitWidth(wantedWidth.get());
                    mediaView.setFitHeight(wantedHeight.get());
                    mediaView.setPreserveRatio(false);
                    mediaView.setOpacity(0);
                    mediaView.getMediaPlayer().setVolume(0.0);
                    FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(2), mediaView);
                    fadeTransition1.setToValue(0.9);

                    setCenter(mediaView);
                    this.setStyle("-fx-background-color: #0f0f0f;");

                    AtomicBoolean endReached = new AtomicBoolean();
                    mediaView.getMediaPlayer().currentTimeProperty().addListener((obs, ov, nv) -> {
                        if (nv.greaterThan(Duration.seconds(6)) && !endReached.getAndSet(true)) {
                            FadeTransition fadeTransition2 = new FadeTransition(Duration.seconds(1), mediaView);
                            fadeTransition2.setToValue(0.0);
                            JPDRetirementController.INSTANCE.getBackgroundSound().play();
                            fadeTransition2.setOnFinished(e3 -> {
                                mediaView.getMediaPlayer().stop();
                                startTextTransition(
                                        () -> displayDemoConfigurationIntroAndLaunch(JPDRetirementController.INSTANCE.getDemoConfigurations().get(0)),
                                        getInitialStepNodes()
                                );
                            });
                            fadeTransition2.play();
                        }
                    });
                    JPDRetirementController.INSTANCE.getMediaPlayerOpeningSound().play();
                    mediaView.getMediaPlayer().play();
                    fadeTransition1.play();
                });
                scaleTransition.play();
            });
        });
    }

    private List<Node> getInitialStepNodes() {
        return Arrays.asList(createTextNode("Voyage au coeur des\n\"SYNTHÈSES DE PAROLE\"", true, 45),
                createTextNode("\nQuelques unes des contributions de\nJean-Paul DEPARTE, Ingénieur", true, 30),
                createTextNode("\n\nLaboratoire d'Électronique du CMRRF de Kerpape\n(1982-2021)", true, 24)
        );
    }

    private Text createTextNode(String text, boolean bold, double size) {
        Text t = new Text(text);
        t.setFill(Color.WHITE);
        t.setFont(Font.font("Deja Vu Sans", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        t.setEffect(new Bloom(0.5));
        return t;
    }

    private List<Node> getNodesFor(JPDRetirementController.DemoConfiguration demoConfiguration) {
        ImageView imageView = new ImageView(demoConfiguration.getImage());
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);
        return Arrays.asList(
                imageView,
                createTextNode("\n" + demoConfiguration.getName() + "\n", true, 45),
                createTextNode(demoConfiguration.getDescription() + "\n", false, 30),
                createTextNode(demoConfiguration.getYear(), false, 26)
        );
    }

    private void displayDemoConfigurationIntroAndLaunch(JPDRetirementController.DemoConfiguration demoConfiguration) {
        startTextTransition(() -> {
            AppModeController.INSTANCE.switchUseModeConfiguration(demoConfiguration.getConfiguration(), demoConfiguration.getConfigurationDescription());
        }, getNodesFor(demoConfiguration));
    }

    private void startTextTransition(Runnable callback, List<Node> nodes) {
        double fadeTime = 1.5, delay = 0.2;
        SequentialTransition sequentialTransition = new SequentialTransition();
        nodes.forEach(t -> {
            t.setOpacity(0.0);
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(fadeTime), t);
            fadeTransition.setDelay(Duration.seconds(delay));
            fadeTransition.setToValue(1.0);
            sequentialTransition.getChildren().add(fadeTransition);
        });
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().addAll(nodes);
        textFlow.setLineSpacing(20.0);
        textFlow.maxWidthProperty().bind(wantedWidth.multiply(0.6));
        textFlow.setTextAlignment(TextAlignment.CENTER);

        VBox box = new VBox(textFlow);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #0f0f0f;");
        box.setOpacity(0.0);
        setCenter(box);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(fadeTime), box);
        fadeTransition.setToValue(1.0);
        sequentialTransition.getChildren().add(0, fadeTransition);
        sequentialTransition.play();

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(fadeTransition.getDuration().toSeconds() + nodes.size() * (delay + fadeTime)), textFlow);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setInterpolator(Interpolator.LINEAR);
        SequentialTransition sequentialTransition1 = new SequentialTransition(scaleTransition, new PauseTransition(Duration.seconds(2)));
        sequentialTransition1.setOnFinished(e -> {
            if (callback != null) {
                ParallelTransition parallelTransition = new ParallelTransition();
                for (Node node : nodes) {
                    FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(fadeTime), node);
                    fadeTransition1.setToValue(0.0);
                    parallelTransition.getChildren().add(fadeTransition1);
                }
                parallelTransition.play();
                parallelTransition.setOnFinished(e1 -> callback.run());
            }
        });
        sequentialTransition1.play();
    }


    public void displayConfigurationStep(int index) {
        this.useModeConfigurationDisplayer.showJPDRetirementView();
        displayDemoConfigurationIntroAndLaunch(JPDRetirementController.INSTANCE.getDemoConfigurations().get(index));
    }

    public void unbindAndClean() {
        setCenter(null);
        prefWidthProperty().unbind();
        prefHeightProperty().unbind();
    }

    public void launchFinalStep() {
        this.useModeConfigurationDisplayer.showJPDRetirementView();
        startTextTransition(
                null,
                Arrays.asList(
                        createTextNode("MERCI encore Jean-Paul !", true, 60),
                        createTextNode("\nEt bonne retraite ! \uD83D\uDE42", true, 45)
                )
        );
    }
}
