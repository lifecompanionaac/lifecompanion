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
package org.lifecompanion.config.view.scene;

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
import javafx.util.Duration;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.control.stats.SessionStatsController;
import org.lifecompanion.config.data.action.impl.*;
import org.lifecompanion.config.data.control.ComponentActionController;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.common.SystemVirtualKeyboardHelper;
import org.lifecompanion.config.view.pane.bottom.BottomPaneView;
import org.lifecompanion.config.view.pane.left.LeftPartView;
import org.lifecompanion.config.view.pane.main.MainView;
import org.lifecompanion.config.view.pane.menu.MainMenu;
import org.lifecompanion.config.view.pane.top.RibbonTabs;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Scene that contains every configuration components.
 *
 * @author Mathieu THEBAUD
 */
public class ConfigurationScene extends Scene implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationScene.class);


    //Animation
    private static final long MENU_ANIMATION_DURATION = 400;
    private static final long BUTTON_ANIMATION_DURATION = 250;
    private static final double COLLAPSED_LAYOUT_X = -MainMenu.MENU_WIDTH - 10.0;
    private Timeline animationShow, animationHide;
    private boolean menuShowing = false;

    //Element
    private MainView mainPane;
    private LeftPartView leftPane;
    private BorderPane borderPane;
    private RibbonTabs topRibbons;
    private BottomPaneView bottomPane;
    private MainMenu mainMenu;
    private StackPane rootStackPane;

    /**
     * Create the configuration scene in the given root.<br>
     * Initialize must be called after.
     *
     * @param rootP the scene root
     */
    public ConfigurationScene(final StackPane rootP) {
        super(rootP);
        this.rootStackPane = rootP;
        this.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
    }

    @Override
    public void initUI() {
        this.topRibbons = new RibbonTabs();

        //Center
        this.mainPane = new MainView();
        this.leftPane = new LeftPartView();
        SplitPane center = new SplitPane(this.leftPane, mainPane);
        center.setOrientation(Orientation.HORIZONTAL);
        center.setDividerPositions(0.30);
        SplitPane.setResizableWithParent(this.leftPane, false);

        //Bottom
        this.bottomPane = new BottomPaneView();

        //Total configuration pane
        this.borderPane = new BorderPane();
        this.borderPane.setCenter(center);
        this.borderPane.setTop(this.topRibbons);
        this.borderPane.setBottom(this.bottomPane);

        //Menu pane
        this.mainMenu = new MainMenu();
        StackPane.setMargin(this.mainMenu, new Insets(50, 0, 0, 0));
        StackPane.setAlignment(this.mainMenu, Pos.TOP_LEFT);
        this.mainMenu.setTranslateX(ConfigurationScene.COLLAPSED_LAYOUT_X);

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
                ComponentActionController.INSTANCE.pasteComponent(AppController.INSTANCE.currentConfigConfigurationProperty().get(),
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

        SystemVirtualKeyboardHelper.INSTANCE.registerScene(this);
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
        final KeyFrame kfE = new KeyFrame(Duration.millis(ConfigurationScene.MENU_ANIMATION_DURATION), kvE);
        final KeyFrame kfEM = new KeyFrame(Duration.millis(ConfigurationScene.BUTTON_ANIMATION_DURATION), kvER);
        this.animationShow.getKeyFrames().addAll(kfE, kfEM);
        //Animation to hide menu
        this.animationHide = new Timeline();
        this.animationHide.setCycleCount(1);
        final KeyValue kvC = new KeyValue(this.mainMenu.translateXProperty(), ConfigurationScene.COLLAPSED_LAYOUT_X, Interpolator.EASE_IN);
        final KeyValue kvCR = new KeyValue(this.topRibbons.getMenuButton().rotateProperty(), 0, Interpolator.EASE_BOTH);
        final KeyFrame kfC = new KeyFrame(Duration.millis(ConfigurationScene.MENU_ANIMATION_DURATION), kvC);
        final KeyFrame kfCM = new KeyFrame(Duration.millis(ConfigurationScene.BUTTON_ANIMATION_DURATION), kvCR);
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
