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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.lifecompanion.controller.editaction.KeyActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.impl.configurationcomponent.GridPartKeyComponent;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey.KeyListNodeKeyOption;
import org.lifecompanion.ui.common.control.generic.LimitedTextArea;
import org.lifecompanion.ui.common.util.UndoRedoTextInputWrapper;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;

/**
 * Part to modify the text in the current key.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyTextRibbonPart extends RibbonBasePart<GridPartKeyComponent> implements LCViewInitHelper {

    /**
     * Change the text content of the key
     */
    private LimitedTextArea fieldKeyText;

    /**
     * Wrapper for key text
     */
    private UndoRedoTextInputWrapper fieldKeyTextWrapper;

    private final BooleanBinding multiKeySelected;

    public KeyTextRibbonPart() {
        multiKeySelected = SelectionController.INSTANCE.getListPropertySelectedKeys().sizeProperty().greaterThan(1);
        this.initAll();
    }

    @Override
    public void initUI() {
        //Base
        this.setTitle(Translation.getText("pane.title.text"));
        VBox rows = new VBox();
        rows.setAlignment(Pos.TOP_CENTER);
        //Text
        Label labelText = new Label(Translation.getText("pane.text.key.text"));
        this.fieldKeyText = new LimitedTextArea();
        this.fieldKeyText.setWrapText(true);
        this.fieldKeyText.setPrefRowCount(1);
        this.fieldKeyText.setPrefColumnCount(14);
        this.fieldKeyTextWrapper = new UndoRedoTextInputWrapper(this.fieldKeyText, ConfigActionController.INSTANCE.undoRedoEnabled());

        //Total
        rows.setSpacing(5.0);
        rows.getChildren().addAll(labelText, this.fieldKeyText);
        this.setContent(rows);
    }

    @Override
    public void initListener() {
        //Create action to undo when needed
        this.fieldKeyTextWrapper.setListener((oldV, newV) -> {
            if (this.model.get() != null) {
                KeyActions.SetTextAction textAction = new KeyActions.SetTextAction(this.model.get(), oldV, newV);
                ConfigActionController.INSTANCE.addAction(textAction);
            }
        });
        //On tab : select next component
        this.fieldKeyText.addEventFilter(KeyEvent.KEY_PRESSED, (ef) -> {
            if (ef.isShortcutDown() && ef.getCode() == KeyCode.TAB) {
                this.fieldKeyTextWrapper.fireChangeEvent();//Focus will not be lost, so we need to fire a change event if needed
                SelectionController.INSTANCE.selectNextKeyInCurrentGrid();
                ef.consume();
            } else if (ef.isShiftDown() && ef.getCode() == KeyCode.TAB) {
                this.fieldKeyTextWrapper.fireChangeEvent();//Focus will not be lost, so we need to fire a change event if needed
                SelectionController.INSTANCE.selectPreviousKeyInCurrentGrid();
                ef.consume();
            }
        });
    }

    @Override
    public void bind(final GridPartKeyComponent component) {
        this.fieldKeyText.textProperty().bindBidirectional(component.textContentProperty());
        this.fieldKeyTextWrapper.clearPreviousValue();
        //Option
        MonadicBinding<Boolean> disableText = EasyBind.select(component.keyOptionProperty()).selectObject(KeyOptionI::disableTextContentProperty);
        BooleanBinding disableTextField = Bindings.createBooleanBinding(() -> multiKeySelected.get() || disableText.getOrElse(false), multiKeySelected, disableText);
        this.fieldKeyText.disableProperty().bind(disableTextField);
        this.fieldKeyText.limitProperty().bind(EasyBind.select(component.keyOptionProperty()).selectObject(KeyOptionI::maxTextLengthProperty));
    }

    @Override
    public void initBinding() {
        SelectionController.INSTANCE.selectedKeyHelperProperty().addListener((o, oldV, newV) -> {
            if (newV instanceof GridPartKeyComponent) {
                // #46 : Text field shouldn't be focused automatically
                //this.fieldKeyText.requestFocus();
                this.model.set((GridPartKeyComponent) newV);
                if (this.fieldKeyText.isFocused()) {
                    this.fieldKeyText.selectAll();
                }
            } else {
                this.model.set(null);
            }
        });
        this.initVisibleAndManagedBinding(GridPartKeyComponentI.class, KeyListNodeKeyOption.class);
    }

    @Override
    public void unbind(final GridPartKeyComponent modelP) {
        this.fieldKeyText.textProperty().unbindBidirectional(modelP.textContentProperty());
        this.fieldKeyText.disableProperty().unbind();
        this.fieldKeyText.limitProperty().unbind();
        this.fieldKeyText.clear();
    }
}
