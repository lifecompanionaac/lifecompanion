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

package org.lifecompanion.config.view.pane.tabs.home.part;

import java.util.ArrayList;

import org.controlsfx.glyphfont.FontAwesome;

import org.lifecompanion.config.data.action.impl.GlobalActions;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.config.data.action.impl.UndoRedoActions;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.StackComponentI;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.config.data.control.ComponentActionController;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.api.ui.config.ConfigurationProfileLevelEnum;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

/**
 * Ribbon part that allow access to quick actions
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class QuickActionRibbonPart extends RibbonBasePart<Void> implements LCViewInitHelper {
	private Button buttonUndo, buttonRedo, buttonGoUseMode, buttonCopy, buttonPaste, buttonRemove;

	public QuickActionRibbonPart() {
		this.initAll();
	}

	@Override
	public void bind(final Void modelP) {}

	@Override
	public void unbind(final Void modelP) {}

	@Override
	public void initUI() {
		//Create undo/redo, copy/paste part
		FlowPane paneBaseActions = new FlowPane();
		paneBaseActions.setAlignment(Pos.CENTER);
		paneBaseActions.setPrefWrapLength(150);
		this.buttonUndo = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.edit.item.undo"),
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.UNDO).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK), "tooltip.undo.action.button");
		this.buttonRedo = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.edit.item.redo"),
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.REPEAT).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK),
				"tooltip.redo.action.button");
		this.buttonGoUseMode = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.go.use.mode"),
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLAY).sizeFactor(2).color(LCGraphicStyle.MAIN_PRIMARY),
				"tooltip.go.use.mode.button");
		this.buttonCopy = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.edit.item.copy"),
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.COPY).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK),
				"tooltip.copy.select.button");
		this.buttonPaste = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.edit.item.paste"),
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PASTE).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK),
				"tooltip.paste.select.button");
		this.buttonRemove = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.edit.item.remove"),
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK),
				"tooltip.remove.select.button");
		this.buttonRemove.setDisable(true);
		HBox boxButtonFL = new HBox(this.buttonUndo, this.buttonRedo, this.buttonGoUseMode);
		HBox boxButtonSL = new HBox(this.buttonCopy, this.buttonPaste, this.buttonRemove);
		paneBaseActions.getChildren().addAll(boxButtonFL, boxButtonSL);
		this.setContent(paneBaseActions);
		this.setTitle(Translation.getText("menu.edit.name"));
	}

	@Override
	public void initListener() {
		//Use mode
		this.buttonGoUseMode.setOnAction(GlobalActions.HANDLER_GO_USE_MODE);
		//Undo/redo
		this.buttonUndo.setOnAction(UndoRedoActions.HANDLER_UNDO);
		this.buttonRedo.setOnAction(UndoRedoActions.HANDLER_REDO);
		//Copy
		this.buttonCopy.setOnAction((ea) -> {
			ComponentActionController.INSTANCE.copyComponent(SelectionController.INSTANCE.selectedComponentBothProperty().get());
		});
		this.buttonPaste.setOnAction((ea) -> {
			ComponentActionController.INSTANCE.pasteComponent(AppController.INSTANCE.currentConfigConfigurationProperty().get(),
					SelectionController.INSTANCE.selectedComponentBothProperty().get(),
					new ArrayList<>(SelectionController.INSTANCE.getSelectedKeys()));
		});
		this.buttonRemove.setOnAction((ea) -> {
			ComponentActionController.INSTANCE.removeComponent(SelectionController.INSTANCE.selectedComponentBothProperty().get(),
					SelectionController.INSTANCE.getSelectedKeys());
		});
	}

	@Override
	public void initBinding() {
		//Undo/redo
		this.buttonUndo.disableProperty().bind(ConfigActionController.INSTANCE.undoDisabledProperty());
		this.buttonRedo.disableProperty().bind(ConfigActionController.INSTANCE.redoDisabledProperty());
		//Copy/paste/remove
		this.buttonCopy.disableProperty().bind(SelectionController.INSTANCE.selectedComponentBothProperty().isNull());
		this.buttonPaste.disableProperty().bind(ComponentActionController.INSTANCE.copiedComponentProperty().isNull());
		SelectionController.INSTANCE.selectedComponentBothProperty().addListener((obs, ov, nv) -> {
			boolean removeDisable = false;
			if (nv != null) {
				if (nv instanceof GridComponentI) {
					GridComponentI gridComp = (GridComponentI) nv;
					StackComponentI stackParent = gridComp.stackParentProperty().get();
					if (stackParent != null && stackParent.isDirectStackChild(gridComp)) {
						removeDisable = gridComp.lastStackChildProperty().get();
					}
				}
			} else {
				removeDisable = true;
			}
			this.buttonRemove.setDisable(removeDisable);
		});
		//Show for profile level
		ConfigUIUtils.bindShowForLevelFrom(this.buttonCopy, ConfigurationProfileLevelEnum.NORMAL);
		ConfigUIUtils.bindShowForLevelFrom(this.buttonPaste, ConfigurationProfileLevelEnum.NORMAL);
	}

}
