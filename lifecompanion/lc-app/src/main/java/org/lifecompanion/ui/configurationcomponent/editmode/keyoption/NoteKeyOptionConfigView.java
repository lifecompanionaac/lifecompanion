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
package org.lifecompanion.ui.configurationcomponent.editmode.keyoption;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.lifecompanion.ui.common.pane.specific.cell.NoteKeyDisplayModeListCell;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.note.NoteKeyDisplayMode;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.note.NoteKeyOption;
import org.lifecompanion.ui.common.util.UndoRedoTextInputWrapper;
import org.lifecompanion.controller.editaction.KeyOptionActions.ChangeNoteCustomTextAction;
import org.lifecompanion.controller.editaction.KeyOptionActions.ChangeNoteKeyDisplayModeAction;
import org.lifecompanion.controller.editaction.KeyOptionActions.ChangeNoteKeyStrokeColorAction;
import org.lifecompanion.controller.editaction.KeyOptionActions.ChangeNoteKeyStrokeSizeAction;
import org.lifecompanion.util.binding.LCConfigBindingUtils;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Configuration view for note key option.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class NoteKeyOptionConfigView extends BaseKeyOptionConfigView<NoteKeyOption> {
    private LCColorPicker pickerWantedColor;
    private Spinner<Integer> spinnerStrokeSize;
    private ComboBox<NoteKeyDisplayMode> comboboxDisplayMode;
    private TextField textFieldCustomText;
    private UndoRedoTextInputWrapper fieldCustomTextWrapper;
    private Label labelCustomText;

    private ChangeListener<Number> changeListenerStrokeSize;
    private ChangeListener<Color> changeListenerStrokeColor;
    private ChangeListener<NoteKeyDisplayMode> changeListenerDisplayMode;

    @Override
    public Class<NoteKeyOption> getConfiguredKeyOptionType() {
        return NoteKeyOption.class;
    }

    @Override
    public void initUI() {
        super.initUI();
        Label labelWantedColor = new Label(Translation.getText("use.action.save.load.note.color.field"));
        this.pickerWantedColor = new LCColorPicker();
        Label labelWantedStrokeSize = new Label(Translation.getText("use.action.save.load.note.stroke.size.field"));
        this.spinnerStrokeSize = UIUtils.createIntSpinner(0, 30, 3, 1, 130.0);
        Label labelDisplayMode = new Label(Translation.getText("label.note.key.display.mode"));
        this.comboboxDisplayMode = new ComboBox<>(FXCollections.observableArrayList(NoteKeyDisplayMode.values()));
        this.comboboxDisplayMode.setButtonCell(new NoteKeyDisplayModeListCell());
        this.comboboxDisplayMode.setCellFactory((lv) -> new NoteKeyDisplayModeListCell());
        labelCustomText = new Label(Translation.getText("label.note.key.custom.text"));
        this.textFieldCustomText = new TextField();
        this.fieldCustomTextWrapper = new UndoRedoTextInputWrapper(this.textFieldCustomText, ConfigActionController.INSTANCE.undoRedoEnabled());

        final VBox boxContent = new VBox(5.0, labelDisplayMode, comboboxDisplayMode, labelCustomText, textFieldCustomText, labelWantedColor,
                this.pickerWantedColor, labelWantedStrokeSize, spinnerStrokeSize);
        ScrollPane scrollPane = new ScrollPane(boxContent);
        scrollPane.prefHeightProperty().bind(this.heightProperty());
        scrollPane.getStyleClass().add("transparent-scroll-pane");
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(scrollPane);
    }

    @Override
    public void initListener() {
        super.initListener();
        changeListenerStrokeSize = LCConfigBindingUtils.createIntegerSpinnerBinding(this.spinnerStrokeSize, this.model,
                NoteKeyOption::wantedStrokeSizeProperty, ChangeNoteKeyStrokeSizeAction::new);
        changeListenerStrokeColor = LCConfigBindingUtils.createSimpleBinding(this.pickerWantedColor.valueProperty(), this.model,
                c -> c.wantedActivatedColorProperty().get(), ChangeNoteKeyStrokeColorAction::new);
        this.changeListenerDisplayMode = LCConfigBindingUtils.createSelectionModelBinding(this.comboboxDisplayMode.getSelectionModel(), //
                this.model, model -> model.displayModeProperty().get(), //
                ChangeNoteKeyDisplayModeAction::new);
        this.fieldCustomTextWrapper.setListener((oldV, newV) -> {
            if (this.model.get() != null) {
                ConfigActionController.INSTANCE.addAction(new ChangeNoteCustomTextAction(this.model.get(), oldV, newV));
            }
        });
    }

    @Override
    public void bind(final NoteKeyOption model) {
        this.textFieldCustomText.textProperty().bindBidirectional(model.keyCustomTextProperty());
        this.fieldCustomTextWrapper.clearPreviousValue();
        this.spinnerStrokeSize.getValueFactory().setValue(model.wantedStrokeSizeProperty().get());
        this.pickerWantedColor.setValue(model.wantedActivatedColorProperty().get());
        this.comboboxDisplayMode.setValue(model.displayModeProperty().get());
        this.textFieldCustomText.setText(model.keyCustomTextProperty().get());
        model.wantedStrokeSizeProperty().addListener(changeListenerStrokeSize);
        model.wantedActivatedColorProperty().addListener(changeListenerStrokeColor);
        model.displayModeProperty().addListener(changeListenerDisplayMode);

        // Bind visibility
        this.labelCustomText.managedProperty().bind(this.labelCustomText.visibleProperty());
        this.textFieldCustomText.managedProperty().bind(this.textFieldCustomText.visibleProperty());
        final BooleanBinding equalsToCustomTextBinding = model.displayModeProperty().isEqualTo(NoteKeyDisplayMode.CUSTOM_TEXT);
        this.labelCustomText.visibleProperty().bind(equalsToCustomTextBinding);
        this.textFieldCustomText.visibleProperty().bind(equalsToCustomTextBinding);

    }

    @Override
    public void unbind(final NoteKeyOption model) {
        this.textFieldCustomText.textProperty().unbindBidirectional(model.keyCustomTextProperty());
        model.wantedStrokeSizeProperty().removeListener(changeListenerStrokeSize);
        model.wantedActivatedColorProperty().removeListener(changeListenerStrokeColor);
        model.displayModeProperty().addListener(changeListenerDisplayMode);
        this.labelCustomText.visibleProperty().unbind();
        this.textFieldCustomText.visibleProperty().unbind();
    }

}
