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

package org.lifecompanion.ui;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.framework.commons.translation.Translation;

public class LoadingScene extends Scene {
    private final ParallelTransition parallelTransition;

    public LoadingScene(VBox vbox) {
        super(vbox);

        ImageView imageViewIcon = new ImageView(IconHelper.get(LCConstant.LC_BIG_ICON_ONLY_PATH));
        ImageView imageViewText = new ImageView(IconHelper.get(LCConstant.LC_BIG_TITLE_ONLY_ICON_PATH));
        ImageView imageViewCopyright = new ImageView(IconHelper.get(LCConstant.LC_COPYRIGHT_ICON_PATH));

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(1500), imageViewIcon);
        rotateTransition.setToAngle(360.0);
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setCycleCount(Transition.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.EASE_BOTH);

        parallelTransition = new ParallelTransition(rotateTransition);
        parallelTransition.setDelay(Duration.millis(200));
        parallelTransition.play();

        HBox boxImage = new HBox(40.0, imageViewIcon, imageViewText);
        boxImage.setAlignment(Pos.CENTER);
        VBox.setMargin(boxImage, new Insets(0, 0, 20, 0));

        Label labelMessage = new Label(Translation.getText(InstallationController.INSTANCE.isUpdateDownloadFinished() ? "updating.lc.loading" : "main.ui.loading"));
        labelMessage.setStyle("-fx-font-size: 20.0px; -fx-font-weight: bold; -fx-text-fill: darkgray;");

        Pane paneFill1 = new Pane();
        VBox.setVgrow(paneFill1, Priority.ALWAYS);
        Pane paneFill2 = new Pane();
        VBox.setVgrow(paneFill2, Priority.ALWAYS);

        VBox.setMargin(imageViewCopyright, new Insets(0, 0, 10, 0));


        vbox.getChildren().addAll(paneFill1, boxImage, labelMessage, paneFill2, imageViewCopyright);
        vbox.setSpacing(30.0);
        vbox.setAlignment(Pos.CENTER);
    }

    public void stopAndClear() {
        parallelTransition.stop();
    }
}
