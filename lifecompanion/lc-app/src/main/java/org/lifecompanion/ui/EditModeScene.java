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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.lifecompanion.controller.editaction.*;
import org.lifecompanion.controller.editmode.ComponentActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.ui.app.main.CurrentLifeCompanionStateDetailView;
import org.lifecompanion.ui.app.main.MainView;
import org.lifecompanion.ui.app.main.addcomponent.AddComponentView;
import org.lifecompanion.ui.app.main.mainmenu.MainMenu;
import org.lifecompanion.ui.app.main.ribbon.RibbonTabs;
import org.lifecompanion.ui.app.main.usercomponent.UserCompView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Scene that contains every configuration components.
 *
 * @author Mathieu THEBAUD
 */
public class EditModeScene extends Scene implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditModeScene.class);


    //Animation
    private static final long MENU_ANIMATION_DURATION = 400;
    private static final long BUTTON_ANIMATION_DURATION = 250;
    private static final double COLLAPSED_LAYOUT_X = -MainMenu.MENU_WIDTH - 10.0;
    private Timeline animationShow, animationHide;
    private boolean menuShowing = false;

    private BorderPane borderPane;
    private RibbonTabs topRibbons;
    private MainMenu mainMenu;
    private final StackPane rootStackPane;

    /**
     * Create the configuration scene in the given root.<br>
     * Initialize must be called after.
     *
     * @param rootP the scene root
     */
    public EditModeScene(final StackPane rootP) {
        super(rootP);
        this.rootStackPane = rootP;
        this.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
    }

    @Override
    public void initUI() {
        this.topRibbons = new RibbonTabs();

        //Center
        //Element
        MainView mainPane = new MainView();
        VBox leftPane = new VBox(new AddComponentView(), new UserCompView());
        SplitPane center = new SplitPane(leftPane, mainPane);
        center.setOrientation(Orientation.HORIZONTAL);
        center.setDividerPositions(0.30);
        SplitPane.setResizableWithParent(leftPane, false);

        //Bottom
        CurrentLifeCompanionStateDetailView bottomPane = new CurrentLifeCompanionStateDetailView();

        //Total configuration pane
        this.borderPane = new BorderPane();
        this.borderPane.setCenter(center);
        this.borderPane.setTop(this.topRibbons);
        this.borderPane.setBottom(bottomPane);

        //Menu pane
        this.mainMenu = new MainMenu();
        StackPane.setMargin(this.mainMenu, new Insets(50, 0, 0, 0));
        StackPane.setAlignment(this.mainMenu, Pos.TOP_LEFT);
        this.mainMenu.setTranslateX(EditModeScene.COLLAPSED_LAYOUT_X);

        //Add to root
        this.rootStackPane.getChildren().addAll(this.borderPane, this.mainMenu);
        StackPane.setAlignment(this.borderPane, Pos.CENTER);

        //Animations
        this.createMenuAnimation();
    }

    @Override
    public void initBinding() {
        this.borderPane.prefWidthProperty().bind(this.widthProperty());
        this.borderPane.prefHeightProperty().bind(this.heightProperty());
    }

    @Override
    public void initListener() {
        //Keyboard shortcut
        this.addEventHandler(KeyEvent.KEY_PRESSED, eventP -> {
            if (LCConfigurationActions.KEY_COMBINATION_NEW.match(eventP)) {
                LCConfigurationActions.HANDLER_NEW.handle(null);
            } else if (GlobalActions.KEY_COMBINATION_CANCEL.match(eventP)) {
                GlobalActions.HANDLER_CANCEL.handle(null);
            } else if (LCConfigurationActions.KEY_COMBINATION_SAVE.match(eventP)) {
                LCConfigurationActions.HANDLER_SAVE.handle(null);
            } else if (LCConfigurationActions.KEY_COMBINATION_OPEN.match(eventP)) {
                LCConfigurationActions.HANDLER_MANAGE.handle(null);
            } else if (UndoRedoActions.KEY_COMBINATION_UNDO.match(eventP)) {
                UndoRedoActions.HANDLER_UNDO.handle(null);
            } else if (UndoRedoActions.KEY_COMBINATION_REDO.match(eventP)) {
                UndoRedoActions.HANDLER_REDO.handle(null);
            } else if (UndoRedoActions.KEY_COMBINATION_REMOVE.match(eventP)) {
                if (SelectionController.INSTANCE.selectedComponentBothProperty().get() != null) {
                    ComponentActionController.INSTANCE.removeComponent(SelectionController.INSTANCE.selectedComponentBothProperty().get(),
                            SelectionController.INSTANCE.getSelectedKeys());
                }
            } else if (OptionActions.KEY_COMBINATION_COPY.match(eventP)) {
                ComponentActionController.INSTANCE.copyComponent(SelectionController.INSTANCE.selectedComponentBothProperty().get());
            } else if (OptionActions.KEY_COMBINATION_PASTE.match(eventP)) {
                ComponentActionController.INSTANCE.pasteComponent(AppModeController.INSTANCE.getEditModeContext().configurationProperty().get(),
                        SelectionController.INSTANCE.selectedComponentBothProperty().get(),
                        new ArrayList<>(SelectionController.INSTANCE.getSelectedKeys()));
            } else if (KeyActions.KEY_COMBINATION_COPY_STYLE.match(eventP)) {
                KeyActions.HANDLER_COPY_STYLE.handle(null);
            } else if (KeyActions.KEY_COMBINATION_PASTE_STYLE.match(eventP)) {
                KeyActions.HANDLER_PASTE_STYLE.handle(null);
            }
        });
        //Change mode on key release, because the event will be caught by the use scene
        this.addEventHandler(KeyEvent.KEY_RELEASED, eventP -> {
            if (GlobalActions.KEY_COMBINATION_GO_USE_MODE.match(eventP)) {
                GlobalActions.HANDLER_GO_USE_MODE.handle(null);
            }
        });
        //When leave menu, close it
        this.mainMenu.setOnMouseExited((ea) -> this.hideMenu());

        SystemVirtualKeyboardController.INSTANCE.registerScene(this);
        SessionStatsController.INSTANCE.registerScene(this);
    }

    // Class part : "Menu hide/show"
    //========================================================================

    /**
     * Create the expand/collapse animation on menu.
     */
    private void createMenuAnimation() {
        //Animation to show menu
        this.animationShow = new Timeline();
        this.animationShow.setCycleCount(1);
        final KeyValue kvE = new KeyValue(this.mainMenu.translateXProperty(), 0, Interpolator.EASE_OUT);
        final KeyValue kvER = new KeyValue(this.topRibbons.getMenuButton().rotateProperty(), 90, Interpolator.EASE_BOTH);
        final KeyFrame kfE = new KeyFrame(Duration.millis(EditModeScene.MENU_ANIMATION_DURATION), kvE);
        final KeyFrame kfEM = new KeyFrame(Duration.millis(EditModeScene.BUTTON_ANIMATION_DURATION), kvER);
        this.animationShow.getKeyFrames().addAll(kfE, kfEM);
        //Animation to hide menu
        this.animationHide = new Timeline();
        this.animationHide.setCycleCount(1);
        final KeyValue kvC = new KeyValue(this.mainMenu.translateXProperty(), EditModeScene.COLLAPSED_LAYOUT_X, Interpolator.EASE_IN);
        final KeyValue kvCR = new KeyValue(this.topRibbons.getMenuButton().rotateProperty(), 0, Interpolator.EASE_BOTH);
        final KeyFrame kfC = new KeyFrame(Duration.millis(EditModeScene.MENU_ANIMATION_DURATION), kvC);
        final KeyFrame kfCM = new KeyFrame(Duration.millis(EditModeScene.BUTTON_ANIMATION_DURATION), kvCR);
        this.animationHide.getKeyFrames().addAll(kfC, kfCM);
    }

    /**
     * Show the menu if the menu is not already showing
     */
    public void showMenu() {
        if (!this.menuShowing) {
            this.animationHide.stop();
            this.animationShow.play();
            this.menuShowing = true;
        }
    }

    /**
     * Hide the menu if the menu is showing
     */
    public void hideMenu() {
        if (this.menuShowing) {
            this.animationShow.stop();
            this.animationHide.play();
            this.menuShowing = false;
        }
    }

    /**
     * Hide the menu if showing, else show the menu
     */
    public void switchMenu() {
        if (this.menuShowing) {
            this.hideMenu();
        } else {
            this.showMenu();
        }
    }
    //========================================================================
}
