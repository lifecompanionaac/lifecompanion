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

package org.lifecompanion.ui.common.control.specific.imagedictionary;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

class ImageDictionariesListView extends VBox implements LCViewInitHelper {
    private Consumer<ImageElementI> selectionCallback;
    private Label labelNoResult;

    public ImageDictionariesListView() {
        this.initAll();
    }

    public void setSelectionCallback(Consumer<ImageElementI> selectionCallback) {
        this.selectionCallback = selectionCallback;
    }

    @Override
    public void initUI() {
        labelNoResult = new Label(Translation.getText("image.dictionary.search.image.empty.result"));
        VBox.setMargin(labelNoResult, new Insets(60.0, 0, 0, 0));
        this.setAlignment(Pos.CENTER);
    }

    public void setDisplayedImages(List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchResult) {
        this.getChildren().clear();

        if (LangUtils.isNotEmpty(searchResult)) {
            for (Pair<ImageDictionaryI, List<List<ImageElementI>>> dictionaryAndResult : searchResult) {
                ImageDictionaryI dictionary = dictionaryAndResult.getKey();

                // Dictionary title
                Label labelDictionary = new Label(dictionary.getName());
                labelDictionary.getStyleClass().add("image-dictionary-title");
                labelDictionary.setTextAlignment(TextAlignment.LEFT);
                labelDictionary.setMaxWidth(Double.MAX_VALUE);
                if (StringUtils.isNotBlank(dictionary.getDescription()) && StringUtils.isNotBlank(dictionary.getAuthor())) {
                    Tooltip.install(labelDictionary, UIUtils.createTooltip(dictionary.getDescription() + "\n\n" + dictionary.getAuthor()));
                }

                // Copyright
                Button buttonCopyright = UIUtils.createGraphicButton(
                        LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.COPYRIGHT).size(12).color(LCGraphicStyle.LC_BLACK), dictionary.getUrl());
                buttonCopyright.setOnAction(e -> UIUtils.openUrlInDefaultBrowser(dictionary.getUrl()));
                buttonCopyright.getStyleClass().add("button-with-bottom-padding-only");

                // Box title
                HBox boxTitle = new HBox(5.0, labelDictionary);
                if (StringUtils.isNotBlank(dictionary.getUrl())) {
                    boxTitle.getChildren().add(buttonCopyright);
                }
                boxTitle.setAlignment(Pos.TOP_CENTER);
                boxTitle.getStyleClass().add("image-dictionary-title-box");
                HBox.setHgrow(labelDictionary, Priority.ALWAYS);
                VBox.setMargin(boxTitle, new Insets(10, 0, 0, 0));

                // Dictionary content
                TilePane tilePaneImages = new TilePane();
                tilePaneImages.setHgap(10);
                tilePaneImages.setVgap(10);
                tilePaneImages.setAlignment(Pos.CENTER);
                VBox.setMargin(tilePaneImages, new Insets(2, 0, 0, 0));

                // On page change, show corresponding images
                IntegerProperty currentPageIndex = new SimpleIntegerProperty(-1);
                currentPageIndex.addListener((obs, ov, nv) -> {
                    tilePaneImages.getChildren().clear();
                    tilePaneImages.getChildren().addAll(dictionaryAndResult.getValue().get(nv.intValue()).stream().map(imageElement -> new ImageElementView(imageElement, selectionCallback)).collect(Collectors.toList()));
                });
                currentPageIndex.set(0);

                // Create buttons for pages
                HBox boxPagesButton = new HBox(5.0,
                        createPageButton("<<", 0, currentPageIndex),
                        createPageButton("<", () -> currentPageIndex.get() > 0 ? currentPageIndex.get() - 1 : 0, currentPageIndex));
                boxPagesButton.setAlignment(Pos.CENTER);
                VBox.setMargin(boxPagesButton, new Insets(2.0, 0, 0, 0));
                Label currentPage = new Label();
                currentPage.textProperty().bind(TranslationFX.getTextBinding("image.dictionary.current.page.index", currentPageIndex.add(1), dictionaryAndResult.getValue().size()));
                boxPagesButton.getChildren().addAll(
                        currentPage,
                        createPageButton(">", () -> currentPageIndex.get() < dictionaryAndResult.getValue().size() - 1 ? currentPageIndex.get() + 1 : dictionaryAndResult.getValue().size() - 1, currentPageIndex),
                        createPageButton(">>", dictionaryAndResult.getValue().size() - 1, currentPageIndex));

                // Total content (show pages button only when needed)
                this.getChildren().add(boxTitle);
                if (dictionaryAndResult.getValue().size() > 1) this.getChildren().add(boxPagesButton);
                this.getChildren().add(tilePaneImages);
            }
        } else {
            this.getChildren().add(labelNoResult);
        }
    }

    private Node createPageButton(String text, int pageIndex, IntegerProperty currentPageIndex) {
        return createPageButton(text, () -> pageIndex, currentPageIndex);
    }

    private Node createPageButton(String text, IntSupplier pageIndexSupplier, IntegerProperty currentPageIndex) {
        Hyperlink linkPage = new Hyperlink(text);
        linkPage.setOnAction(e -> currentPageIndex.set(pageIndexSupplier.getAsInt()));
        return linkPage;
    }
}
