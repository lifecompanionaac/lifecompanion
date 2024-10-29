/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.media;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.api.configurationcomponent.VideoUseComponentI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.pane.generic.FittedViewI;
import org.lifecompanion.ui.common.pane.generic.FittedViewPane;
import org.lifecompanion.ui.common.pane.generic.MediaViewFittedView;
import org.lifecompanion.util.javafx.ColorUtils;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractPlayerStage<T> extends Stage {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPlayerStage.class);
    protected T model;
    private final Color backgroundColor;

    public AbstractPlayerStage(T model, final Color backgroundColor) {
        this.model = model;
        this.backgroundColor = backgroundColor;
        Stage useModeStage = AppModeController.INSTANCE.getUseModeContext().getStage();
        boolean fullScreenBeforeShow = useModeStage.isFullScreen();
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.initStyle(StageStyle.UNDECORATED);
        this.initOwner(useModeStage);
        this.setTitle(StageUtils.getStageDefaultTitle());
        this.setFullScreen(true);
        this.setResizable(false);
        this.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        this.setAlwaysOnTop(true);
        this.setOnShown(e -> onShown());
        this.setOnCloseRequest(Event::consume);
        this.setScene(this.createScene());
        this.setOnHiding(e -> {
            onHiding();
            this.model = null;
        });
        this.setOnHidden(e -> {
            if (fullScreenBeforeShow) {
                useModeStage.setFullScreen(true);
            }
        });
    }

    abstract FittedViewI createContent(T model);

    abstract void onHiding();

    protected void onShown() {
    }

    private Scene createScene() {
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background:" + ColorUtils.toCssColor(backgroundColor != null ? backgroundColor : Color.BLACK) + ";");
        Scene scene = new Scene(stackPane);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> hide());
        scene.addEventFilter(KeyEvent.ANY, e -> hide());
        FittedViewPane fittedViewPane = new FittedViewPane(createContent(this.model));
        stackPane.getChildren().add(fittedViewPane);
        return scene;
    }
}
