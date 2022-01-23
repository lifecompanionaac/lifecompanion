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

package org.lifecompanion.config.view.pane.general.view.simplercomp;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.simplercomp.SimplerKeyContentContainerI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.data.config.LCGlyphFont;

public class DetailledSimplerKeyContentContainerListCell<T extends SimplerKeyContentContainerI> extends AbstractKeyListContentListCell<T> {
    public static final double CELL_HEIGHT = 100.0;
    private static final int BUTTON_SIZE = 14;

    protected final HBox fillerPane;


    public DetailledSimplerKeyContentContainerListCell(CommonListViewActionContainer<T> commonListViewActionContainer) {
        super(true);
        // Buttons
        Button buttonMoveUp = createButton(FontAwesome.Glyph.CHEVRON_LEFT, LCGraphicStyle.MAIN_PRIMARY, "todo");
        Button buttonMoveDown = createButton(FontAwesome.Glyph.CHEVRON_RIGHT, LCGraphicStyle.MAIN_PRIMARY, "todo");
        Button buttonDuplicate = createButton(FontAwesome.Glyph.PLUS_SQUARE, LCGraphicStyle.MAIN_DARK, "todo");
        Button buttonDelete = createButton(FontAwesome.Glyph.TRASH, LCGraphicStyle.SECOND_DARK, "todo");
        fillerPane = new HBox();
        fillerPane.setMaxWidth(Double.MAX_VALUE);
        fillerPane.setAlignment(Pos.CENTER);

        HBox.setHgrow(fillerPane, Priority.ALWAYS);
        HBox boxButtons = new HBox(5.0, buttonMoveUp, buttonMoveDown, fillerPane, buttonDuplicate, buttonDelete);

        buttonDelete.setOnAction(ae -> commonListViewActionContainer.deleteItem(this.getItem()));
        buttonMoveUp.setOnAction(ae -> commonListViewActionContainer.moveUp(this.getItem()));
        buttonMoveDown.setOnAction(ae -> commonListViewActionContainer.moveDown(this.getItem()));
        buttonDuplicate.setOnAction(ae -> commonListViewActionContainer.duplicate(this.getItem()));

        this.setOnMouseClicked(me -> {
            final T item = this.getItem();
            if (me.getClickCount() >= 2 && item != null) {
                commonListViewActionContainer.doubleClicOn(item);
            }
        });
        this.setOnDragDetected(me -> {
            final T item = getItem();
            Dragboard dragboard = this.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(item != null ? item.textProperty().get() : "key");
            dragboard.setContent(content);
            commonListViewActionContainer.dragStart(item);
        });
        this.setOnDragOver(me -> {
            if (commonListViewActionContainer.isDraggedNodes()) {
                me.acceptTransferModes(TransferMode.ANY);
            }
        });
        this.setOnDragDropped(me -> {
            if (commonListViewActionContainer.isDraggedNodes()) {
                commonListViewActionContainer.dragEnd(getItem());
            }
        });

        //Global content
        this.boxContent.setTop(boxButtons);
    }

    private Button createButton(FontAwesome.Glyph trash, Color color, String tooltip) {
        final Button button = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(trash)
                .size(BUTTON_SIZE).color(color), tooltip);
        button.getStyleClass().addAll("button-without-padding");
        return button;
    }

    @Override
    protected double getCellHeight() {
        return CELL_HEIGHT;
    }

    @Override
    protected double getCellWidth() {
        return 140.0;
    }

}
