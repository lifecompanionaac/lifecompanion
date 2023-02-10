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
package org.lifecompanion.ui.app.main.ribbon.available.withselection;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import org.lifecompanion.controller.editaction.AutoCompleteKeyboardEnum;
import org.lifecompanion.controller.editaction.KeyActions;
import org.lifecompanion.controller.editaction.UndoRedoActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteKeyboardTooltip extends Tooltip implements LCViewInitHelper {

    private static final long SHOWING_TIME_MS = 8000;

    private final AutoCompleteKeyboardEnum autoCompleteKeyboard;
    private final List<GridPartKeyComponentI> emptyKeys;
    private Button buttonExecuteComplete;
    private final GridPartKeyComponentI previousKey, currentKey;

    public AutoCompleteKeyboardTooltip(
            AutoCompleteKeyboardEnum autoCompleteKeyboard,
            List<GridPartKeyComponentI> emptyKeys, GridPartKeyComponentI previousKey, GridPartKeyComponentI currentKey) {
        this.autoCompleteKeyboard = autoCompleteKeyboard;
        this.emptyKeys = emptyKeys;
        this.previousKey = previousKey;
        this.currentKey = currentKey;
        initAll();
    }

    @Override
    public void initUI() {
        this.setText(Translation.getText("autocomplete.keyboard.suggest.tooltip", autoCompleteKeyboard.getName()));
        this.setTextAlignment(TextAlignment.CENTER);
        buttonExecuteComplete = FXControlUtils.createSimpleTextButton(Translation.getText("autocomplete.keyboard.suggest.button.complete"), null);
        buttonExecuteComplete.getStyleClass().addAll("text-fill-white", "text-font-size-110", "text-weight-bold");
        this.setWrapText(true);
        this.setGraphic(buttonExecuteComplete);
        this.setContentDisplay(ContentDisplay.BOTTOM);
    }

    @Override
    public void initListener() {
        buttonExecuteComplete.setOnAction(e -> {
            this.hide();
            List<UndoRedoActionI> actions = new ArrayList<>();
            int startIndex = autoCompleteKeyboard.getCompletionStartIndex(previousKey, currentKey);
            for (int i = startIndex; i < autoCompleteKeyboard.getElements().length; i++) {
                if (i - startIndex < emptyKeys.size()) {
                    GridPartKeyComponentI keyToComplete = emptyKeys.get(i - startIndex);
                    actions.add(new KeyActions.SetTextAction(keyToComplete, autoCompleteKeyboard.getElements()[i]));
                }
            }
            ConfigActionController.INSTANCE.executeAction(new UndoRedoActions.MultiActionWrapperAction("action.key.autocomplete.keyboard", actions));
        });
        this.setOnShowing(e -> ThreadUtils.runAfter(SHOWING_TIME_MS, () -> {
            if (this.isShowing()) {
                FXThreadUtils.runOnFXThread(this::hide);
            }
        }));
    }

    void showOn(Region field) {
        this.setMaxWidth(field.getWidth());
        Scene scene = field.getScene();
        Window window = scene.getWindow();
        Point2D point2D = field.localToScene(0, 0);
        this.show(field, window.getX() + scene.getX() + point2D.getX(), window.getY() + scene.getY() + point2D.getY() + field.getHeight() + 5.0);
    }
}
