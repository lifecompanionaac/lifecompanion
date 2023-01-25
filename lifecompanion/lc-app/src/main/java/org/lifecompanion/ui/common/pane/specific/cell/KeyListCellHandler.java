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

package org.lifecompanion.ui.common.pane.specific.cell;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.binding.BindingUtils;

public class KeyListCellHandler implements LCViewInitHelper {
    public final static double CELL_HEIGHT = 25;

    private final IndexedCell<? extends KeyListNodeI> thisCell;

    private ImageView imageView;
    private Node listGlyph, keyGlyph, linkGlyph;
    private HBox graphics;
    private HBox glyphPane;
    private Label labelText;
    private Rectangle rectangleColors;

    protected KeyListCellHandler(IndexedCell<? extends KeyListNodeI> thisCell) {
        this.thisCell = thisCell;
        thisCell.getStyleClass().add("keylist-tree-cell");
        initAll();
    }

    @Override
    public void initUI() {
        listGlyph = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.FOLDER).size(14).color(LCGraphicStyle.LC_GRAY);
        final HBox paneImageView = new HBox(new ImageView(IconHelper.get("keylist/icon_type_leaf.png")));
        paneImageView.getStyleClass().add("padding-3_5");
        this.keyGlyph = paneImageView;
        linkGlyph = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.LINK).size(14).color(LCGraphicStyle.LC_GRAY);
        glyphPane = new HBox();
        glyphPane.setAlignment(Pos.CENTER);

        labelText = new Label();
        labelText.getStyleClass().addAll("text-font-size-120");

        imageView = new ImageView();
        imageView.setFitHeight(CELL_HEIGHT);
        imageView.setFitWidth(CELL_HEIGHT);
        imageView.setSmooth(true);

        rectangleColors = new Rectangle(CELL_HEIGHT / 2.0, CELL_HEIGHT / 2.0);
        rectangleColors.setStrokeWidth(2.0);

        graphics = new HBox(10, glyphPane, labelText, imageView, rectangleColors);
        graphics.setPadding(new Insets(0, 0, 0, 2));
        graphics.setAlignment(Pos.CENTER_LEFT);

        thisCell.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    public void initListener() {
        final String nodeIdForImageLoading = "KeyListNodeTreeCell" + this.hashCode();
        thisCell.itemProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.removeExternalLoadingRequest(nodeIdForImageLoading);
            }
            if (nv != null)
                nv.addExternalLoadingRequest(nodeIdForImageLoading);
        });
    }

    void cellUpdateItem(KeyListNodeI item, boolean empty) {
        if (item == null || empty) {
            thisCell.setGraphic(null);
            BindingUtils.unbindAndSetNull(imageView.imageProperty());
            BindingUtils.unbindAndSetNull(labelText.textProperty());
            BindingUtils.unbindAndSetNull(labelText.textFillProperty());
            BindingUtils.unbindAndSetNull(rectangleColors.strokeProperty());
            BindingUtils.unbindAndSetNull(rectangleColors.fillProperty());
            glyphPane.getChildren().clear();
        } else {
            glyphPane.getChildren().clear();
            imageView.imageProperty().bind(item.loadedImageProperty());
            glyphPane.getChildren().add(item.isLinkNode() ? linkGlyph : item.isLeafNode() ? keyGlyph : listGlyph);
            rectangleColors.strokeProperty().bind(item.strokeColorProperty());
            rectangleColors.fillProperty().bind(item.backgroundColorProperty());
            labelText.textProperty()
                    .bind(Bindings.createStringBinding(item::getHumanReadableText,
                            item.textProperty(),
                            item.enableWriteProperty(),
                            item.textToWriteProperty(),
                            item.enableSpeakProperty(),
                            item.textToSpeakProperty()));
            labelText.textFillProperty().bind(Bindings.createObjectBinding(() -> item.textColorProperty().get() != null ? item.textColorProperty().get() : Color.BLACK, item.textColorProperty()));
            thisCell.setGraphic(graphics);
        }
    }
}
