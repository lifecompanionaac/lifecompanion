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

package org.lifecompanion.ui.common.control.generic;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * View to display a list and to provide a way to change item order, add/remove item in the list.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OrderModifiableListView<T> extends BorderPane implements LCViewInitHelper {
    private static final double ICON_SIZE = 20.0;

    /**
     * Children inside the list
     */
    private ListView<T> listChildren;

    private final ListProperty<T> listProperty;

    /**
     * Button for base functions
     */
    private Button buttonAdd, buttonModify, buttonRemove, buttonDown, buttonUp;

    /**
     * To known if this list allow the list to be empty (eg disable/enable remove button on different condition)
     */
    private final boolean allowEmptyList;

    /**
     * To known where button should be placed
     */
    private final Pos rightOrLeft;

    public OrderModifiableListView(final boolean allowEmptyListP) {
        this(allowEmptyListP, Pos.CENTER_RIGHT);
    }

    public OrderModifiableListView(final boolean allowEmptyListP, final Pos rightOrLeft) {
        this.allowEmptyList = allowEmptyListP;
        this.rightOrLeft = rightOrLeft;
        this.listProperty = new SimpleListProperty<>();
        this.initAll();
    }

    @Override
    public void initUI() {
        //Button on right
        VBox buttons = new VBox();
        buttons.setAlignment(Pos.CENTER);
        //TODO : tooltip
        this.buttonAdd = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE)
                .size(OrderModifiableListView.ICON_SIZE).color(LCGraphicStyle.MAIN_PRIMARY), null);
        this.buttonModify = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_MATERIAL.create('\uE254').size(OrderModifiableListView.ICON_SIZE - 2.0).color(LCGraphicStyle.MAIN_PRIMARY), null);
        this.buttonModify.setVisible(false);
        this.buttonModify.managedProperty().bind(this.buttonModify.visibleProperty());
        this.buttonRemove = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH_ALT)
                .size(OrderModifiableListView.ICON_SIZE).color(LCGraphicStyle.SECOND_PRIMARY), null);
        this.buttonUp = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_UP).size(OrderModifiableListView.ICON_SIZE).color(LCGraphicStyle.MAIN_DARK),
                null);
        this.buttonDown = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_DOWN)
                .size(OrderModifiableListView.ICON_SIZE).color(LCGraphicStyle.MAIN_DARK), null);
        buttons.getChildren().addAll(this.buttonAdd, this.buttonModify, this.buttonRemove, this.buttonUp, this.buttonDown);
        //Total
        if (this.rightOrLeft == Pos.CENTER_RIGHT) {
            this.setRight(buttons);
        } else {
            this.setLeft(buttons);
        }
        this.listChildren = new ListView<>();
        this.setCenter(listChildren);
    }

    @Override
    public void initBinding() {
        this.listProperty.bind(listChildren.itemsProperty());
        //disable remove if there is no selection
        int minSize = this.allowEmptyList ? 0 : 1;
        this.buttonRemove.disableProperty().bind(this.listProperty.sizeProperty().isEqualTo(minSize)
                .or(this.listChildren.getSelectionModel().selectedItemProperty().isNull()));
        //Disable up/down by selected item
        this.buttonUp.disableProperty().bind(this.listChildren.getSelectionModel().selectedItemProperty().isNull()
                .or(this.listChildren.getSelectionModel().selectedIndexProperty().isEqualTo(0)));
        this.buttonDown.disableProperty().bind(this.listChildren.getSelectionModel().selectedItemProperty().isNull().or(this.listChildren
                .getSelectionModel().selectedIndexProperty().isEqualTo(this.listProperty.sizeProperty().subtract(1))));
    }

    // Class part : "Needed getter and setter"
    //========================================================================
    public void setCellFactory(final Callback<ListView<T>, ListCell<T>> cellFactory) {
        this.listChildren.setCellFactory(cellFactory);
    }

    public Button getButtonAdd() {
        return this.buttonAdd;
    }

    public Button getButtonRemove() {
        return this.buttonRemove;
    }

    public Button getButtonDown() {
        return this.buttonDown;
    }

    public Button getButtonUp() {
        return this.buttonUp;
    }

    public Button getButtonModify() {
        return this.buttonModify;
    }

    public ReadOnlyObjectProperty<T> selectedItemProperty() {
        return listChildren.getSelectionModel().selectedItemProperty();
    }

    public T getSelectedItem() {
        return listChildren.getSelectionModel().getSelectedItem();
    }

    public void scrollTo(T item) {
        listChildren.scrollTo(item);
    }

    public ReadOnlyBooleanProperty listEmptyProperty() {
        return this.listProperty.emptyProperty();
    }

    public void setItems(ObservableList<T> items) {
        listChildren.setItems(items);
    }

    public void select(T item) {
        this.listChildren.getSelectionModel().select(item);
    }
    //========================================================================

}
