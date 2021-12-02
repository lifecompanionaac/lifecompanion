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

package org.lifecompanion.config.view.pane.general.view.simplercomp.keylist;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.lifecompanion.api.component.definition.simplercomp.KeyListNodeI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

public class KeyListNodeTreeCell extends TreeCell<KeyListNodeI> implements LCViewInitHelper {
    public final static double CELL_HEIGHT = 25;

    private final KeyListContentConfigView keyListContentConfigView;

    private ImageView imageView;
    private Glyph listGlyph, keyGlyph, linkGlyph;
    private HBox graphics;
    private HBox glyphPane;
    private Label labelText;
    private Rectangle rectangleColors;

    KeyListNodeTreeCell(KeyListContentConfigView keyListContentConfigView) {
        this.getStyleClass().add("keylist-tree-cell");
        this.keyListContentConfigView = keyListContentConfigView;
        initAll();
    }

    @Override
    public void initUI() {
        listGlyph = LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.FOLDER).size(14).color(LCGraphicStyle.LC_GRAY);
        keyGlyph = LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PICTURE_ALT).size(14).color(LCGraphicStyle.LC_GRAY);
        linkGlyph = LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.LINK).size(14).color(LCGraphicStyle.LC_GRAY);
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

        this.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    public void initListener() {
        final String nodeIdForImageLoading = "KeyListNodeTreeCell" + this.hashCode();
        this.itemProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.removeExternalLoadingRequest(nodeIdForImageLoading);
            }
            if (nv != null)
                nv.addExternalLoadingRequest(nodeIdForImageLoading);
        });
    }

    @Override
    protected void updateItem(KeyListNodeI item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            LCUtils.unbindAndSetNull(imageView.imageProperty());
            LCUtils.unbindAndSetNull(labelText.textProperty());
            LCUtils.unbindAndSetNull(rectangleColors.strokeProperty());
            LCUtils.unbindAndSetNull(rectangleColors.fillProperty());
            glyphPane.getChildren().clear();
        } else {
            glyphPane.getChildren().clear();
            imageView.imageProperty().bind(item.loadedImageProperty());
            glyphPane.getChildren().add(item.isLinkNode() ? linkGlyph : item.isLeafNode() ? keyGlyph : listGlyph);
            rectangleColors.strokeProperty().bind(item.strokeColorProperty());
            rectangleColors.fillProperty().bind(item.backgroundColorProperty());
            labelText.textProperty().bind(Bindings.createStringBinding(() -> {
                if (StringUtils.isNotBlank(item.textProperty().get())) return item.textProperty().get();
                if (item.enableWriteProperty().get() && StringUtils.isNotBlank(item.textToWriteProperty().get())) return item.textToWriteProperty().get();
                if (item.enableSpeakProperty().get() && StringUtils.isNotBlank(item.textToSpeakProperty().get())) return item.textToSpeakProperty().get();
                return Translation.getText("general.configuration.view.keylist.empty.text.in.node");
            }, item.textProperty(), item.enableWriteProperty(), item.textToWriteProperty(), item.enableSpeakProperty(), item.textToSpeakProperty()));
            setGraphic(graphics);
        }
    }


}
