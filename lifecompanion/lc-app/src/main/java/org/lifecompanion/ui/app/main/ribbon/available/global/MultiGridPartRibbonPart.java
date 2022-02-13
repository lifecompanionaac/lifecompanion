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

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.SpanModifiableComponentI;
import org.lifecompanion.model.impl.configurationcomponent.DisplayableComponentBaseImpl;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.editaction.GridActions.*;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.function.Function;

/**
 * Part that is showed when keys are selected.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MultiGridPartRibbonPart extends RibbonBasePart<DisplayableComponentBaseImpl> implements LCViewInitHelper {

    /**
     * Button to split an expand grid part
     */
    private Button buttonSplitPart;

    /**
     * To add and shift keys
     */
    private ComboBox<AddKeyPosition> comboBoxAddKey;


    /**
     * listener for row/column span
     */
    private InvalidationListener spanInvalidationListener;

    public MultiGridPartRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        VBox totalBox = new VBox(8.0);
        totalBox.setAlignment(Pos.CENTER);

        //Action button
        this.buttonSplitPart = FXControlUtils.createTextButtonWithIcon(Translation.getText("grid.part.split.keys"), "actions/icon_split_grid_part.png",
                "tooltip.split.key.part");

        this.comboBoxAddKey = new ComboBox<>(FXCollections.observableArrayList(AddKeyPosition.values()));
        this.comboBoxAddKey.setCellFactory(lv -> new AddKeyPositionListCell());
        this.comboBoxAddKey.setButtonCell(new AddKeyPositionActionListCell());
        this.comboBoxAddKey.setValue(AddKeyPosition.LEFT);
        this.comboBoxAddKey.setPrefWidth(180.0);

        //Total
        totalBox.getChildren().addAll(comboBoxAddKey, new Separator(Orientation.HORIZONTAL), buttonSplitPart);
        this.setTitle(Translation.getText("grid.part.selected.title"));
        this.setContent(totalBox);

        //Pop over windows
        this.updateButtonSplitDisable(null);
        this.updateButtonAddDisable(null);
    }

    @Override
    public void initListener() {
        this.buttonSplitPart.setOnAction((ea) -> {
            if (this.model.get() != null && this.model.get() instanceof GridPartComponentI) {
                GridPartComponentI gridPart = (GridPartComponentI) this.model.get();
                ConfigActionController.INSTANCE.executeAction(new SplitGridPartAction(gridPart.gridParentProperty().get().getGrid(), gridPart));
            }
        });
    }


    @Override
    @SuppressWarnings("unchecked")
    public void initBinding() {
        //Props
        SelectionController.INSTANCE.selectedComponentBothProperty().addListener((obs, ov, nv) -> {
            this.model.set((DisplayableComponentBaseImpl) nv);
        });
        //Span listening for button activation
        this.spanInvalidationListener = (inv) -> {
            this.updateButtonSplitDisable(this.model.get());
        };
    }

    private void updateButtonSplitDisable(final DisplayableComponentBaseImpl comp) {
        if (comp instanceof SpanModifiableComponentI) {
            SpanModifiableComponentI spanComp = (SpanModifiableComponentI) comp;
            this.buttonSplitPart.setDisable(spanComp.rowSpanProperty().get() <= 1 && spanComp.columnSpanProperty().get() <= 1);
        } else {
            this.buttonSplitPart.setDisable(true);
        }
    }

    private void updateButtonAddDisable(final DisplayableComponentBaseImpl comp) {
        if (comp instanceof GridPartComponentI) {
            this.comboBoxAddKey.setDisable(((GridPartComponentI) comp).gridParentProperty().get() == null);
        } else {
            this.comboBoxAddKey.setDisable(true);
        }
    }

    // BIND/UNBIND
    //========================================================================
    @Override
    public void bind(final DisplayableComponentBaseImpl modelP) {
        this.updateButtonSplitDisable(modelP);
        this.updateButtonAddDisable(modelP);
        if (modelP instanceof SpanModifiableComponentI) {
            SpanModifiableComponentI spanComp = (SpanModifiableComponentI) modelP;
            spanComp.rowSpanProperty().addListener(this.spanInvalidationListener);
            spanComp.columnSpanProperty().addListener(this.spanInvalidationListener);
        }
    }

    @Override
    public void unbind(final DisplayableComponentBaseImpl modelP) {
        if (modelP instanceof SpanModifiableComponentI) {
            SpanModifiableComponentI spanComp = (SpanModifiableComponentI) modelP;
            spanComp.rowSpanProperty().removeListener(this.spanInvalidationListener);
            spanComp.columnSpanProperty().removeListener(this.spanInvalidationListener);
        }
        this.updateButtonSplitDisable(null);
        this.updateButtonAddDisable(null);
    }
    //========================================================================

    // ADD AND SHIFT KEYS
    //========================================================================
    private enum AddKeyPosition {
        TOP("button.add.key.on.top.grid", "actions/icon_add_key_top.png", AddKeyOnTopAction::new),
        RIGHT("button.add.key.on.right.grid", "actions/icon_add_key_right.png", AddKeyOnRightAction::new),
        BOTTOM("button.add.key.on.bottom.grid", "actions/icon_add_key_bottom.png", AddKeyOnBottomAction::new),
        LEFT("button.add.key.on.left.grid", "actions/icon_add_key_left.png", AddKeyOnLeftAction::new);

        private final String name, iconPath;
        private final Function<GridPartComponentI, BaseEditActionI> actionConstructor;

        AddKeyPosition(String name, String iconPath, Function<GridPartComponentI, BaseEditActionI> actionConstructor) {
            this.name = name;
            this.iconPath = iconPath;
            this.actionConstructor = actionConstructor;
        }
    }

    public static class AddKeyPositionActionListCell extends ListCell<AddKeyPosition> {
        @Override
        protected void updateItem(AddKeyPosition item, boolean empty) {
            super.updateItem(item, empty);
            this.setText(Translation.getText("button.add.key.generic.action"));
        }
    }

    private class AddKeyPositionListCell extends ListCell<AddKeyPosition> {
        private final ImageView imageView;

        AddKeyPositionListCell() {
            imageView = new ImageView();
            this.setContentDisplay(ContentDisplay.LEFT);
            this.setGraphicTextGap(10.0);
            this.setPrefHeight(40.0);
            this.setMaxHeight(40.0);
            this.setMinHeight(40.0);
            this.setOnMousePressed(m -> {
                if (m.getClickCount() <= 1) {
                    AddKeyPosition item = getItem();
                    if (item != null && model.get() != null && model.get() instanceof GridPartComponentI) {
                        GridPartComponentI comp = (GridPartComponentI) model.get();
                        ConfigActionController.INSTANCE.executeAction(item.actionConstructor.apply(comp));
                    }
                }
            });
        }

        @Override
        protected void updateItem(AddKeyPosition item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                imageView.setImage(null);
                this.setGraphic(null);
                this.setText(null);
            } else {
                imageView.setImage(IconHelper.get(item.iconPath));
                this.setGraphic(imageView);
                this.setText(Translation.getText(item.name));
            }
        }
    }
    //========================================================================
}
