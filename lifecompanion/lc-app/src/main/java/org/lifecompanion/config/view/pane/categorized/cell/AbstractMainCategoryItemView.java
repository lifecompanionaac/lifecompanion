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
package org.lifecompanion.config.view.pane.categorized.cell;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.lifecompanion.api.component.definition.eventaction.MainCategoryI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.function.Consumer;

/**
 * Grid cell that display a main use action category.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class AbstractMainCategoryItemView<T extends MainCategoryI<?>> extends Label implements LCViewInitHelper {
    private final T item;
    private ImageView imageView;
    private Tooltip tooltip;
    private Rectangle backgroundShape;
    private final Consumer<T> categorySelectionCallback;

    public AbstractMainCategoryItemView(T item, final Consumer<T> categorySelectionCallbackP) {
        super();
        this.item = item;
        this.categorySelectionCallback = categorySelectionCallbackP;
        this.initAll();
    }

    @Override
    public void initUI() {
        this.setContentDisplay(ContentDisplay.TOP);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("main-category-grid-cell");
        //Create graphics
        StackPane centerPane = new StackPane();
        this.backgroundShape = new Rectangle(100, 80);
        this.backgroundShape.setArcHeight(10.0);
        this.backgroundShape.setArcWidth(10.0);
        StackPane.setAlignment(this.backgroundShape, Pos.CENTER);
        this.imageView = new ImageView();
        StackPane.setAlignment(this.imageView, Pos.CENTER);
        //Add
        centerPane.getChildren().addAll(this.backgroundShape, this.imageView);
        this.setGraphic(centerPane);
        //Create tooltip
        this.tooltip = UIUtils.createTooltip(null);
    }

    @Override
    public void initBinding() {
        this.backgroundShape.setFill(item.getColor());
        this.imageView.setImage(IconManager.get(item.getConfigIconPath()));
        this.setText(item.getName());
        this.setTooltip(this.tooltip);
        this.tooltip.setText(item.getStaticDescription());
    }

    @Override
    public void initListener() {
        //Select category on cell clic
        this.setOnMouseClicked((ea) -> {
            this.categorySelectionCallback.accept(this.item);
        });
    }
}
