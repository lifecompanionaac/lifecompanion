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

package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.useractionsequence;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.lifecompanion.controller.editaction.KeyActions;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.ui.common.control.specific.imagedictionary.ImageUseComponentSelectorControl;
import org.lifecompanion.ui.common.pane.specific.cell.TextPositionListCell;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequenceI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequenceItemI;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.UserActionSequenceItem;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.CommonListViewActionContainer;
import org.lifecompanion.ui.common.pane.specific.cell.DetailledSimplerKeyContentContainerListCell;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class UserActionSequenceItemsEditionView extends ScrollPane implements LCViewInitHelper {
    private final ObjectProperty<UserActionSequenceI> editedSequence;

    private ListView<UserActionSequenceItemI> listViewItems;
    private Label labelSequenceTitle;
    private TextField fieldSequenceName;
    private ImageUseComponentSelectorControl imageUseComponentSelectorControl;
    private ComboBox<TextPosition> comboBoxTextPosition;
    private Button buttonAddItem;
    private CommonListViewActionContainer<UserActionSequenceItemI> commonListViewActionContainer;

    private UserActionSequenceItemPropertiesEditionView userActionSequenceItemPropertiesEditionView;

    public UserActionSequenceItemsEditionView() {
        editedSequence = new SimpleObjectProperty<>();
        initAll();
    }

    public ObjectProperty<UserActionSequenceI> editedSequenceProperty() {
        return editedSequence;
    }

    @Override
    public void initUI() {
        // Sequence props
        GridPane gridPaneSequenceProps = new GridPane();
        gridPaneSequenceProps.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneSequenceProps.setVgap(5.0);
        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(50);
        gridPaneSequenceProps.getColumnConstraints().add(firstColumn);

        labelSequenceTitle = FXControlUtils.createTitleLabel(null);
        Label labelSequenceName = new Label(Translation.getText("sequence.configuration.view.field.sequence.name"));
        fieldSequenceName = new TextField();
        fieldSequenceName.setPromptText(Translation.getText("sequence.configuration.view.field.sequence.name"));
        GridPane.setValignment(fieldSequenceName, VPos.TOP);

        Label labelTextPosition = new Label(Translation.getText("sequence.configuration.view.field.text.position"));
        comboBoxTextPosition = new ComboBox<>(FXCollections.observableArrayList(TextPosition.values()));
        this.comboBoxTextPosition.setButtonCell(new TextPositionListCell(false));
        this.comboBoxTextPosition.setCellFactory(lv -> new TextPositionListCell(true));
        comboBoxTextPosition.setMaxWidth(Double.MAX_VALUE);
        GridPane.setValignment(comboBoxTextPosition, VPos.TOP);

        imageUseComponentSelectorControl = new ImageUseComponentSelectorControl();
        GridPane.setHalignment(imageUseComponentSelectorControl, HPos.RIGHT);
        GridPane.setMargin(imageUseComponentSelectorControl, new Insets(0, 10, 0, 0));

        gridPaneSequenceProps.add(labelSequenceName, 0, 0);
        gridPaneSequenceProps.add(fieldSequenceName, 0, 1);
        gridPaneSequenceProps.add(labelTextPosition, 0, 2);
        gridPaneSequenceProps.add(comboBoxTextPosition, 0, 3);
        Pane filler = new Pane();
        GridPane.setHgrow(filler, Priority.ALWAYS);
        gridPaneSequenceProps.add(filler, 1, 0, 1, 4);
        gridPaneSequenceProps.add(imageUseComponentSelectorControl, 2, 0, 1, 4);

        buttonAddItem = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(18).color(LCGraphicStyle.MAIN_DARK), null);

        // Sequence items
        listViewItems = new ListView<>();
        this.commonListViewActionContainer = new CommonListViewActionContainer<>(listViewItems);
        listViewItems.setOrientation(Orientation.HORIZONTAL);
        listViewItems.setMaxHeight(DetailledSimplerKeyContentContainerListCell.CELL_HEIGHT + 40.0);
        listViewItems.setPrefHeight(DetailledSimplerKeyContentContainerListCell.CELL_HEIGHT + 40.0);
        listViewItems.setCellFactory(lv -> new DetailledSimplerKeyContentContainerListCell<>(commonListViewActionContainer));
        HBox.setHgrow(listViewItems, Priority.ALWAYS);
        HBox boxItems = new HBox(5.0, listViewItems, buttonAddItem);
        boxItems.setAlignment(Pos.CENTER);
        GridPane.setMargin(boxItems, new Insets(5, 0, 0, 0));

        userActionSequenceItemPropertiesEditionView = new UserActionSequenceItemPropertiesEditionView();

        // Total
        this.setFitToWidth(true);
        this.setContent(new VBox(GeneralConfigurationStepViewI.GRID_V_GAP, labelSequenceTitle, gridPaneSequenceProps, FXControlUtils.createTitleLabel("sequence.configuration.view.item.list.title"), boxItems, userActionSequenceItemPropertiesEditionView));
    }

    @Override
    public void initListener() {
        buttonAddItem.setOnAction(event -> {
            this.commonListViewActionContainer.addAndScrollTo(new UserActionSequenceItem());
        });
        this.commonListViewActionContainer.setDuplicateFunction(item -> {
            UserActionSequenceItemI duplicated = (UserActionSequenceItemI) item.duplicate(true);
            duplicated.textProperty().set(Translation.getText("general.configuration.view.user.action.copy.label.key.text") + " " + duplicated.textProperty().get());
            return duplicated;
        });
        KeyActions.installImageAutoSelect(fieldSequenceName, editedSequence::get);
    }

    @Override
    public void initBinding() {
        this.editedSequence.addListener((obs, ov, nv) -> {
            if (ov != null) {
                fieldSequenceName.textProperty().unbindBidirectional(ov.textProperty());
                this.listViewItems.setItems(null);
                comboBoxTextPosition.valueProperty().unbindBidirectional(ov.textPositionProperty());
                comboBoxTextPosition.setValue(null);
            }
            this.labelSequenceTitle.textProperty().unbind();
            if (nv != null) {
                fieldSequenceName.textProperty().bindBidirectional(nv.textProperty());
                comboBoxTextPosition.valueProperty().bindBidirectional(nv.textPositionProperty());
                this.labelSequenceTitle.textProperty().bind(TranslationFX.getTextBinding("label.sequence.name.title", nv.textProperty()));
                this.listViewItems.setItems(nv.getItems());
            } else {
                this.labelSequenceTitle.setText(Translation.getText("label.sequence.name.title.none.selected"));
            }
        });
        this.imageUseComponentSelectorControl.modelProperty().bind(this.editedSequence);
        userActionSequenceItemPropertiesEditionView.selectedNodeProperty().bind(this.listViewItems.getSelectionModel().selectedItemProperty());

    }
}
