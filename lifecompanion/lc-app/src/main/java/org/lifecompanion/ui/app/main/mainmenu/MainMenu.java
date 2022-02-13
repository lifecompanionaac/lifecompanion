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

package org.lifecompanion.ui.app.main.mainmenu;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.EditModeScene;
import javafx.scene.layout.VBox;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;

public class MainMenu extends VBox implements LCViewInitHelper {
	public static final int MENU_WIDTH = 250;
	public static final double BUTTON_WIDTH = 90;

	private Button buttonCollapse;

	public MainMenu() {
		this.initAll();
	}

	@Override
	public void initUI() {
		this.setMaxWidth(MainMenu.MENU_WIDTH);
		this.getStyleClass().add("main-menu");
		//Children
		ProfileDetailView profileDetailView = new ProfileDetailView();
		CurrentConfigDetailView currentConfigDetailView = new CurrentConfigDetailView();
		ConfigActionView configActionView = new ConfigActionView();

		//Collapse button
		this.buttonCollapse = FXControlUtils.createTextButtonWithGraphics(null,
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_LEFT).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY),
				"tooltip.main.menu.collapse");
		HBox boxButton = new HBox(buttonCollapse);
		boxButton.getStyleClass().addAll("main-menu-section", "main-menu-section-bottom");

		this.getChildren().addAll(profileDetailView, currentConfigDetailView, configActionView, boxButton);
		FXUtils.applyPerformanceConfiguration(this);
	}

	@Override
	public void initListener() {
		this.buttonCollapse.setOnAction((me) -> {
			EditModeScene scene = (EditModeScene) AppModeController.INSTANCE.getEditModeContext().getStage().getScene();
			scene.hideMenu();
		});
	}
}
