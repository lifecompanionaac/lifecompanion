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

package org.lifecompanion.ui.configurationcomponent.usemode;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.media.AutoRetryVideoPlayerView;
import org.lifecompanion.controller.media.VideoPlayerStage;
import org.lifecompanion.model.api.categorizedelement.useaction.ActionEventType;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.VideoDisplayMode;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.configurationcomponent.VideoPlayMode;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.ui.common.pane.generic.FittedViewPane;
import org.lifecompanion.ui.common.pane.generic.MediaViewFittedView;
import org.lifecompanion.ui.configurationcomponent.base.GridPartKeyViewBase;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.StageUtils;

import java.util.function.BiConsumer;

public class GridPartKeyViewUse extends GridPartKeyViewBase {

    private ChangeListener<KeyOptionI> keyOptionChanged;
    private final ChangeListener<Region> changeListenerKeyOptionAddedNode;

    private InvalidationListener videoLoadingListener;
    private BiConsumer<ActionEventType, UseActionEvent> eventListener;
    private FittedViewPane mediaViewPane;
    private AutoRetryVideoPlayerView mediaView;

    public GridPartKeyViewUse() {
        super();
        changeListenerKeyOptionAddedNode = (obs, ov, nv) -> {
            if (ov != null) {
                ov.prefWidthProperty().unbind();
                ov.prefHeightProperty().unbind();
                this.getChildren().remove(ov);
            }
            if (nv != null) {
                nv.prefWidthProperty().bind(prefWidthProperty());
                nv.prefHeightProperty().bind(prefHeightProperty());
                this.getChildren().add(nv);
            }
        };
    }

    @Override
    public void initListener() {
        super.initListener();
        EventHandler<? super MouseEvent> mouseEventListener = (me) -> {
            SelectionModeController.INSTANCE.fireMouseEventOn(this.model, me);
        };
        this.setOnMouseEntered(mouseEventListener);
        this.setOnMouseExited(mouseEventListener);
        this.setOnMousePressed(mouseEventListener);
        this.setOnMouseReleased(mouseEventListener);
        this.setOnMouseMoved(mouseEventListener);
    }

    @Override
    public void initUI() {
        super.initUI();
        mediaView = new AutoRetryVideoPlayerView();
        mediaViewPane = new FittedViewPane(new MediaViewFittedView(mediaView));
    }

    @Override
    public void initBinding() {
        super.initBinding();
        // On key option change, bind added node
        keyOptionChanged = (obs, ov, nv) -> {
            if (ov != null) {
                ov.keyViewAddedNodeProperty().removeListener(changeListenerKeyOptionAddedNode);
                this.changeListenerKeyOptionAddedNode.changed(null, ov.keyViewAddedNodeProperty().get(), null);
            }
            if (nv != null) {
                changeListenerKeyOptionAddedNode.changed(null, null, nv.keyViewAddedNodeProperty().get());
                nv.keyViewAddedNodeProperty().addListener(changeListenerKeyOptionAddedNode);
            }
        };
        keyOptionChanged.changed(null, null, model.keyOptionProperty().get());
        this.model.keyOptionProperty().addListener(keyOptionChanged);

        // Load video when the component is displayed in key
        // check for use mode as use mode views are used for component snapshots
        this.videoLoadingListener = inv -> {
            if (AppModeController.INSTANCE.isUseMode() &&
                    model.imageUseComponentDisplayedProperty().get() &&
                    model.videoProperty().get() != null &&
                    model.videoDisplayModeProperty()
                            .get() == VideoDisplayMode.IN_KEY) {
                VideoElementI videoElement = model.videoProperty().get();
                mediaView.setVideoFile(videoElement.getPath(), player -> model.configureVideoPlayer(player, mediaView));
            } else {
                mediaView.disposePlayer();
            }
        };
        this.model.imageUseComponentDisplayedProperty().addListener(videoLoadingListener);
        this.model.videoProperty().addListener(videoLoadingListener);
        this.model.videoDisplayModeProperty().addListener(videoLoadingListener);
        this.videoLoadingListener.invalidated(null);

        // On event fired on the component, catch it to play/stop/show the video when needed
        this.eventListener = (type, event) -> {
            if (model.videoProperty().get() != null) {
                if (model.videoDisplayModeProperty().get() == VideoDisplayMode.IN_KEY) {
                    VideoPlayMode playMode = model.videoPlayModeProperty().get();
                    // Simple activation : always play from start
                    if (playMode == VideoPlayMode.ON_ACTIVATION && type == ActionEventType.SIMPLE && event == UseActionEvent.ACTIVATION) {
                        mediaView.executeOnPlayer(m -> {
                            m.stop();
                            m.play();
                        });
                    } else if (playMode == VideoPlayMode.WHILE_OVER && event == UseActionEvent.OVER) {
                        if (type == ActionEventType.START) {
                            mediaView.executeOnPlayer(MediaPlayer::play);
                        } else if (type == ActionEventType.END) {
                            mediaView.executeOnPlayer(MediaPlayer::pause);
                        }
                    }
                } else if (model.videoDisplayModeProperty().get() == VideoDisplayMode.FULLSCREEN) {
                    if (type == ActionEventType.SIMPLE && event == UseActionEvent.ACTIVATION) {
                        FXThreadUtils.runOnFXThread(() -> {
                            VideoPlayerStage videoPlayerStage = new VideoPlayerStage(this.model);
                            StageUtils.centerOnOwnerOrOnCurrentStageAndShow(videoPlayerStage);
                        });
                    }
                }
            }
        };
        this.model.addEventFiredListener(eventListener);


        // Bind label content depending on selected graphics
        this.labelContent.graphicProperty()
                .bind(Bindings.createObjectBinding(() -> AppModeController.INSTANCE.isUseMode() && this.model.videoDisplayModeProperty()
                                .get() == VideoDisplayMode.IN_KEY ? mediaViewPane : imageViewPane,
                        this.model.videoDisplayModeProperty(), AppModeController.INSTANCE.modeProperty()));
    }

    @Override
    public void unbindComponentAndChildren() {
        final KeyOptionI prevValue = model.keyOptionProperty().get();
        this.model.keyOptionProperty().removeListener(keyOptionChanged);
        keyOptionChanged.changed(model.keyOptionProperty(), prevValue, null);

        this.labelContent.graphicProperty().unbind();

        this.model.imageUseComponentDisplayedProperty().removeListener(videoLoadingListener);
        this.model.videoProperty().removeListener(videoLoadingListener);
        this.model.videoDisplayModeProperty().removeListener(videoLoadingListener);
        this.model.removeEventFiredListener(this.eventListener);

        super.unbindComponentAndChildren();
    }
}
