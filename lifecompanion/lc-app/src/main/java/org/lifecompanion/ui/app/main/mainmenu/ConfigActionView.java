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

import org.controlsfx.glyphfont.FontAwesome;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.api.ui.editmode.ConfigurationProfileLevelEnum;
import org.lifecompanion.util.ConfigUIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Menu part that display general configuration actions
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigActionView extends VBox implements LCViewInitHelper {
	private Button buttonNew, buttonImport, buttonOpenManage;

	public ConfigActionView() {
		this.initAll();
	}

	@Override
	public void initUI() {
		//Style
		this.getStyleClass().addAll("main-menu-section");
		Label labelPartTitle = new Label(Translation.getText("my.configurations.menu.detail.title"));
		labelPartTitle.getStyleClass().add("menu-part-title");
		labelPartTitle.setMaxWidth(Double.MAX_VALUE);
		//Action
		HBox boxButtonL1 = new HBox(10.0);
		VBox.setMargin(boxButtonL1, new Insets(10, 0, 0, 0));
		this.buttonNew = UIUtils.createFixedWidthTextButton(Translation.getText("my.configurations.menu.item.new"),
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).sizeFactor(2).color(LCGraphicStyle.MAIN_PRIMARY),
				MainMenu.BUTTON_WIDTH, "tooltip.new.configuration");
		this.buttonImport = UIUtils.createFixedWidthTextButton(Translation.getText("my.configurations.menu.item.import"),
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK), MainMenu.BUTTON_WIDTH,
				"tooltip.import.configuration");
		this.buttonOpenManage = UIUtils.createFixedWidthTextButton(Translation.getText("my.configurations.menu.item.open.manage"),
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.LIST).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK), MainMenu.BUTTON_WIDTH,
				"tooltip.open.configuration.list");
		boxButtonL1.getChildren().addAll(this.buttonNew, this.buttonImport, this.buttonOpenManage);
		boxButtonL1.setAlignment(Pos.CENTER);
		//Total
		this.getChildren().addAll(labelPartTitle, boxButtonL1);
	}

	@Override
	public void initListener() {
		this.buttonNew.setOnAction(LCConfigurationActions.HANDLER_NEW);
		this.buttonImport.setOnAction(LCConfigurationActions.HANDLER_IMPORT_OPEN);
		this.buttonOpenManage.setOnAction(LCConfigurationActions.HANDLER_MANAGE);
	}

	@Override
	public void initBinding() {
		ConfigUIUtils.bindShowForLevelFrom(this.buttonImport, ConfigurationProfileLevelEnum.NORMAL);
	}
}
