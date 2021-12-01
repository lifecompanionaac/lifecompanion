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
package org.lifecompanion.config.view.pane.tabs.selected.part;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.keyoption.KeyOptionI;
import org.lifecompanion.base.data.component.keyoption.simplercomp.KeyListNodeKeyOption;
import org.lifecompanion.base.data.component.simple.GridPartKeyComponent;
import org.lifecompanion.base.view.reusable.LimitedTextArea;
import org.lifecompanion.base.view.reusable.UndoRedoTextInputWrapper;
import org.lifecompanion.config.data.action.impl.KeyActions;
import org.lifecompanion.config.data.common.keycollection.GridPartKeyCollectionPropertyHolder;
import org.lifecompanion.config.data.common.keycollection.GridPartKeyPropertyChangeListener;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.pane.tabs.style3.MultiKeyHelper;
import org.lifecompanion.config.view.reusable.ContentDisplayListCell;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.Arrays;

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

    /**
     * Text position selection
     */
    private ComboBox<ContentDisplay> comboBoxTextPosition;

    private final GridPartKeyPropertyChangeListener<GridPartKeyComponentI, ContentDisplay, ContentDisplay> textPositionProperty;
    private final GridPartKeyCollectionPropertyHolder selectedKeyProperties;
    private final BooleanBinding multiKeySelected;

    public KeyTextRibbonPart() {
        selectedKeyProperties = new GridPartKeyCollectionPropertyHolder(SelectionController.INSTANCE.getSelectedKeys(), Arrays.asList(
                textPositionProperty = new GridPartKeyPropertyChangeListener<>(GridPartKeyComponentI::textPositionProperty)
        ));
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

        comboBoxTextPosition = new ComboBox<>(FXCollections.observableArrayList(ContentDisplay.CENTER, ContentDisplay.BOTTOM, ContentDisplay.TOP, ContentDisplay.LEFT, ContentDisplay.RIGHT));
        this.comboBoxTextPosition.setButtonCell(new ContentDisplayListCell(true));
        this.comboBoxTextPosition.setCellFactory(lv -> new ContentDisplayListCell(true));
        this.comboBoxTextPosition.prefWidthProperty().bind(fieldKeyText.widthProperty());

        //Total
        rows.setSpacing(5.0);
        rows.getChildren().addAll(labelText, this.fieldKeyText, new Label(Translation.getText("pane.text.text.location")), this.comboBoxTextPosition);
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
                SelectionController.INSTANCE.selectNextGridPartInCurrentGrid();
                ef.consume();
            }
        });

        // Text position (multiple key)
        MultiKeyHelper.initMultiKeyConfigActionListener(this.comboBoxTextPosition,
                ComboBoxBase::setOnAction,
                ComboBoxBase::getValue,
                ComboBoxBase::setValue,
                KeyActions.ChangeMultiTextPositionAction::new,
                this.textPositionProperty);
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
        SelectionController.INSTANCE.selectedComponentProperty().addListener((o, oldV, newV) -> {
            if (newV instanceof GridPartKeyComponent) {
                // #46 : Text field shouldn't be focused automatically
                //this.fieldKeyText.requestFocus();
                this.model.set((GridPartKeyComponent) newV);
            } else {
                this.model.set(null);
            }
        });
        // TODO : also bind on key option change
        this.initVisibleAndManagedBinding(component -> !(component.keyOptionProperty().get() instanceof KeyListNodeKeyOption));
    }

    @Override
    public void unbind(final GridPartKeyComponent modelP) {
        this.fieldKeyText.textProperty().unbindBidirectional(modelP.textContentProperty());
        this.fieldKeyText.disableProperty().unbind();
        this.fieldKeyText.limitProperty().unbind();
        this.fieldKeyText.clear();
    }
}
