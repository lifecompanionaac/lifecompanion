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

package org.lifecompanion.config.view.pane.main.notification2;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.text.DecimalFormat;

import static javafx.concurrent.Worker.State.SUCCEEDED;

public class NotificationScene extends Scene implements LCViewInitHelper {
    private final BorderPane borderPaneNotifContent;
    private final LCNotification notification;

    private static final DecimalFormat PROGRESS_FORMAT = new DecimalFormat("##0");

    private Button buttonClose;
    private Runnable closeRequestListener;

    public NotificationScene(final LCNotification notification) {
        super(new BorderPane());
        this.borderPaneNotifContent = (BorderPane) getRoot();
        this.notification = notification;
        this.getStylesheets().addAll(LCConstant.CSS_NOTIFICATION_STYLE_PATH);
        this.setFill(Color.TRANSPARENT);
        initAll();
    }

    public LCNotification getNotification() {
        return notification;
    }

    @Override
    public void initUI() {

        // Scene
        borderPaneNotifContent.setPadding(new Insets(4.0, 10.0, 4.0, 10.0));
        borderPaneNotifContent.getStyleClass().add("notification-background");
        borderPaneNotifContent.getStyleClass().add("notification-background-" + notification.getType().name().toLowerCase());

        // Main content : task title
        final Label labelTitle = new Label();
        labelTitle.textProperty().bind(notification.titleProperty());
        labelTitle.getStyleClass().add("label-title");
        VBox boxContent = new VBox(3.0, labelTitle);
        boxContent.setAlignment(Pos.CENTER_LEFT);
        borderPaneNotifContent.setCenter(boxContent);

        // Right : progress bar, action button, close button
        HBox boxButton = new HBox(3.0);
        BorderPane.setAlignment(boxButton, Pos.CENTER);
        boxButton.setAlignment(Pos.CENTER_RIGHT);
        borderPaneNotifContent.setRight(boxButton);

        // Close button
        if (!notification.isAutomaticClose() && notification.getType() != LCNotification.LCNotificationType.TASK) {
            buttonClose = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CLOSE).size(18).color(Color.WHITE), null);
            boxButton.getChildren().add(buttonClose);
        }

        // Action button
        if (notification.getAction() != null) {
            Button buttonAction = UIUtils.createSimpleTextButton(StringUtils.toUpperCase(notification.getActionButtonName()), null);
            buttonAction.getStyleClass().add("button-action");
            buttonAction.setOnAction(e -> {
                closeRequestListener.run();
                notification.getAction().run();
            });
            buttonAction.setAlignment(Pos.CENTER);
            boxButton.getChildren().add(0, buttonAction);
        }

        // Task progress
        if (notification.getType() == LCNotification.LCNotificationType.TASK) {
            Label labelProgress = new Label();
            labelProgress.getStyleClass().add("label-progress");
            labelProgress.setAlignment(Pos.CENTER_RIGHT);
            ProgressBar progressBar = new ProgressBar(-1);
            progressBar.setPrefHeight(15.0);
            progressBar.setPrefWidth(65.0);
            if (notification.getTask().getState() == SUCCEEDED) {
                progressBar.progressProperty().set(1.0);
            } else {
                progressBar.progressProperty().bind(notification.getTask().progressProperty());
            }
            labelProgress.textProperty().bind(Bindings.createStringBinding(() -> progressBar.getProgress() > 0 ? (PROGRESS_FORMAT.format(100.0 * progressBar.getProgress()) + "%") : "", progressBar.progressProperty()));
            boxButton.getChildren().add(0, progressBar);
            boxButton.getChildren().add(0, labelProgress);
        }
    }

    public void setCloseRequestListener(Runnable closeRequestListener) {
        this.closeRequestListener = closeRequestListener;
    }

    @Override
    public void initListener() {
        if (buttonClose != null) {
            buttonClose.setOnAction(e -> closeRequestListener.run());
        }
        this.setOnMouseClicked(m -> closeRequestListener.run());
    }
}
