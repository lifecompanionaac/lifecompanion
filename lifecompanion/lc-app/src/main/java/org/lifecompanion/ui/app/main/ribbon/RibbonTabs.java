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

package org.lifecompanion.ui.app.main.ribbon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.ui.app.main.ribbon.available.*;
import org.lifecompanion.ui.EditModeScene;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Class that contains all the ribbons in different tabs and the menu button.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RibbonTabs extends StackPane implements LCViewInitHelper {
    private Button buttonMenu;
    private TabPane tabPane;
    //private Tab tabSelectedPart;

    public RibbonTabs() {
        this.initAll();
    }

    @Override
    public void initUI() {
        //Create buttons
        this.buttonMenu = new Button();
        this.buttonMenu.setBackground(null);
        this.buttonMenu.setGraphic(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.BARS).sizeFactor(2).color(Color.WHITE));
        this.buttonMenu.setShape(new Circle(32.0));
        this.buttonMenu.setStyle("-fx-background-color:-fx-main-primary;");
        StackPane.setAlignment(this.buttonMenu, Pos.TOP_LEFT);
        StackPane.setMargin(this.buttonMenu, new Insets(3.0));
        this.buttonMenu.setCache(true);
        //Create tab pane
        this.tabPane = new TabPane();
        this.tabPane.getStyleClass().add("ribbon-tabs");
        this.tabPane.tabClosingPolicyProperty().set(TabClosingPolicy.UNAVAILABLE);
        //Add tabs
        this.addTab(new RibbonTabHome());
        this.addTab(new RibbonTabCreate());
        this.addTab(new RibbonTabSelected());
        this.addTab(new RibbonTabStyle());
        this.addTab(new RibbonTabAction());

        //Add all
        this.getChildren().addAll(this.tabPane, this.buttonMenu);
    }

    private Tab addTab(final AbstractTabContent tabContent) {
        Tab tab = new Tab();
        tab.setContent(tabContent);
        tab.textProperty().bind(EasyBind.map(tabContent.tabTitleProperty(), String::toUpperCase));
        tab.disableProperty().bind(tabContent.disableTabProperty().or(AppModeController.INSTANCE.getEditModeContext().configurationProperty().isNull()));
        this.tabPane.getTabs().add(tab);
        return tab;
    }

    public Button getMenuButton() {
        return this.buttonMenu;
    }

    @Override
    public void initListener() {
        //Button behavior
        this.buttonMenu.setOnAction(ea -> {
            EditModeScene scene = (EditModeScene) AppModeController.INSTANCE.getEditModeContext().getStage().getScene();
            scene.switchMenu();
        });
    }
}
