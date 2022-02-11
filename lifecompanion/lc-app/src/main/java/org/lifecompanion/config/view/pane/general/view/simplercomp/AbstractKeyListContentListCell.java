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

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyContentContainerI;
import org.lifecompanion.util.LCUtils;

public abstract class AbstractKeyListContentListCell<T extends SimplerKeyContentContainerI> extends ListCell<T> {
    private static final double STROKE_SIZE_SIDE = 4.0;
    private static final double STROKE_SIZE_TOP_BOTTOM = 4.0;
    private final ImageView imageView;
    private final Label labelText;
    protected final BorderPane boxContent;
    private final String nodeIdForImageLoading;

    public AbstractKeyListContentListCell(boolean enabledImage) {
        this.nodeIdForImageLoading = "AbstractKeyListContentListCell" + this.hashCode();
        //Base content
        this.boxContent = new BorderPane();
        this.labelText = new Label();
        this.labelText.setMaxWidth(getCellWidth() - 10.0);
        labelText.getStyleClass().addAll("center-label", "key-list-item-title");

        //Images view
        if (enabledImage) {
            this.imageView = new ImageView();
            final double imageHeight = getCellHeight() - 30.0;
            this.imageView.setFitHeight(imageHeight);
            final double imageWidth = getCellWidth() - 20.0;
            this.imageView.setFitWidth(imageWidth);
            this.imageView.setPreserveRatio(true);
            this.imageView.setSmooth(true);

            this.itemProperty().addListener((obs, ov, nv) -> {
                if (ov != null) {
                    ov.removeExternalLoadingRequest(nodeIdForImageLoading);
                }
                if (nv != null)
                    nv.addExternalLoadingRequest(nodeIdForImageLoading);
            });
        } else {
            imageView = null;
        }

        //Global content
        this.boxContent.setCenter(enabledImage ? this.imageView : labelText);
        this.boxContent.setBottom(enabledImage ? labelText : null);
        this.boxContent.setMaxSize(getCellWidth() - 10.0, getCellHeight() - 20.0);
        this.boxContent.setPrefSize(getCellWidth() - 10.0, getCellHeight() - 20.0);
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setPadding(new Insets(4.0));
    }

    @Override
    protected void updateItem(final T itemP, final boolean emptyP) {
        super.updateItem(itemP, emptyP);
        if (itemP == null || emptyP) {
            if (imageView != null) {
                this.imageView.imageProperty().unbind();
                this.imageView.imageProperty().set(null);
            }
            this.labelText.textProperty().unbind();
            this.setGraphic(null);
            this.boxContent.styleProperty().unbind();
            this.boxContent.setStyle(null);
        } else {
            if (imageView != null) {
                this.imageView.imageProperty().bind(itemP.loadedImageProperty());
            }
            this.labelText.textProperty().bind(itemP.textProperty());
            this.setGraphic(this.boxContent);
            this.boxContent.styleProperty().bind(Bindings.createStringBinding(() -> {
                StringBuilder styleSb = new StringBuilder();
                if (itemP.backgroundColorProperty().get() != null) {
                    styleSb.append("-fx-background-color:").append(LCUtils.toCssColor(itemP.backgroundColorProperty().get())).append(";");
                }
                if (itemP.strokeColorProperty().get() != null) {
                    styleSb.append("-fx-border-color:").append(LCUtils.toCssColor(itemP.strokeColorProperty().get())).append(";")
                            .append("-fx-border-width: ").append(STROKE_SIZE_TOP_BOTTOM).append(" ").append(STROKE_SIZE_SIDE).append(" ").append(STROKE_SIZE_TOP_BOTTOM).append(" ").append(STROKE_SIZE_SIDE).append(";")
                            .append("-fx-background-insets: ").append(STROKE_SIZE_TOP_BOTTOM - 1).append(" ").append(STROKE_SIZE_SIDE - 1).append(" ").append(STROKE_SIZE_TOP_BOTTOM - 1).append(" ").append(STROKE_SIZE_SIDE - 1).append(";")
                            .append("-fx-border-style: solid inside;");
                }
                return styleSb.toString();
            }, itemP.strokeColorProperty(), itemP.backgroundColorProperty()));
        }
    }

    protected abstract double getCellHeight();

    protected abstract double getCellWidth();


}
