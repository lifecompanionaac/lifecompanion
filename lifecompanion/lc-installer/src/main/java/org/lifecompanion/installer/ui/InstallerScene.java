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

package org.lifecompanion.installer.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.installer.controller.InstallerManager;
import org.lifecompanion.installer.ui.model.InstallerStep;

import java.util.HashMap;
import java.util.Map;

public class InstallerScene extends Scene implements LCViewInitHelper {
    private Button buttonCancel, buttonPrevious, buttonNext;
    private final Map<InstallerStep, Node> cachedViews;
    private final BorderPane root;

    public InstallerScene() {
        super(new BorderPane());
        this.root = (BorderPane) this.getRoot();
        cachedViews = new HashMap<>();
        this.getStylesheets().add("style/installer.css");
        initAll();
    }

    @Override
    public void initUI() {
        Image imageLogo = new Image("lifecompanion_title_icon_400px.png");
        ImageView imageViewLogo = new ImageView(imageLogo);
        imageViewLogo.setPreserveRatio(true);
        imageViewLogo.setSmooth(true);
        imageViewLogo.setFitHeight(80.0);

        Image imageCopyright = new Image("lifecompanion_copyright_400.png");
        ImageView imageViewCopyright = new ImageView(imageCopyright);
        imageViewCopyright.setPreserveRatio(true);
        imageViewCopyright.setSmooth(true);
        imageViewCopyright.setFitHeight(80.0);

        HBox boxImages = new HBox(30.0, imageViewCopyright, imageViewLogo);
        boxImages.setAlignment(Pos.CENTER);


        // Bottom : buttons
        buttonCancel = createButton("lc.installer.steps.button.cancel");
        buttonPrevious = createButton("lc.installer.steps.button.previous");
        buttonNext = createButton("lc.installer.steps.button.next");
        Pane fillerPane = new Pane();
        HBox.setHgrow(fillerPane, Priority.ALWAYS);
        HBox boxButtons = new HBox(10.0, buttonCancel, fillerPane, buttonPrevious, buttonNext);

        this.root.setPadding(new Insets(20.0));
        this.root.setTop(boxImages);
        this.root.setBottom(boxButtons);
    }

    private Button createButton(String translationId) {
        Button button = new Button(Translation.getText(translationId));
        button.setPrefWidth(110.0);
        return button;
    }

    @Override
    public void initBinding() {
        InstallerManager.INSTANCE.currentStepProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.stepHidden();
            }
            this.root.setCenter(this.cachedViews.computeIfAbsent(nv, k -> {
                k.initAll();
                return nv.getContent();
            }));
            nv.stepDisplayed();
            this.buttonPrevious.disableProperty().bind(nv.previousButtonAvailable().not());
            this.buttonNext.disableProperty().bind(nv.nextButtonAvailable().not());
        });
    }

    @Override
    public void initListener() {
        this.buttonCancel.setOnAction(e -> InstallerManager.INSTANCE.cancelRequest());
        this.buttonPrevious.setOnAction(e -> InstallerManager.INSTANCE.previousStep());
        this.buttonNext.setOnAction(e -> InstallerManager.INSTANCE.nextStep());
    }

}
