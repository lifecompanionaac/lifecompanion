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

package org.lifecompanion.ui.app.main.ribbon.available.global;

import java.util.ArrayList;

import org.controlsfx.glyphfont.FontAwesome;

import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.editaction.GlobalActions;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.controller.editaction.UndoRedoActions;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.editmode.ComponentActionController;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.model.api.ui.editmode.ConfigurationProfileLevelEnum;
import org.lifecompanion.util.ConfigUIUtils;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
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
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.UNDO).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK), "tooltip.undo.action.button");
		this.buttonRedo = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.edit.item.redo"),
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.REPEAT).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK),
				"tooltip.redo.action.button");
		this.buttonGoUseMode = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.go.use.mode"),
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLAY).sizeFactor(2).color(LCGraphicStyle.MAIN_PRIMARY),
				"tooltip.go.use.mode.button");
		this.buttonCopy = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.edit.item.copy"),
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.COPY).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK),
				"tooltip.copy.select.button");
		this.buttonPaste = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.edit.item.paste"),
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PASTE).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK),
				"tooltip.paste.select.button");
		this.buttonRemove = UIUtils.createTextButtonWithGraphics(Translation.getText("menu.edit.item.remove"),
				GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK),
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
			ComponentActionController.INSTANCE.pasteComponent(AppModeController.INSTANCE.getEditModeContext().configurationProperty().get(),
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
		this.buttonGoUseMode.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty().isNull());
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
