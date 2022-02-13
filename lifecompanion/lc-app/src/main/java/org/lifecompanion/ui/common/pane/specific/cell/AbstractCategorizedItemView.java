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

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementI;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.ui.app.categorizedelement.CategorizedIconView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Grid cell to display a use action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class AbstractCategorizedItemView<T extends CategorizedElementI<?>> extends Label implements LCViewInitHelper {
    private final T item;
    private Tooltip tooltip;
    private CategorizedIconView useActionIconView;
    private Consumer<T> selectionCallback;
    private Function<T, T> itemCreator;

    public AbstractCategorizedItemView(T item, final Consumer<T> selectionCallbackP, final Function<T, T> itemCreator) {
        super();
        this.item = item;
        this.itemCreator = itemCreator;
        this.selectionCallback = selectionCallbackP;
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.setContentDisplay(ContentDisplay.TOP);
        this.setAlignment(Pos.TOP_CENTER);
        this.setTextAlignment(TextAlignment.CENTER);
        this.setWrapText(true);
        this.getStyleClass().add("dropshadow-hover");
        //Icon view
        this.useActionIconView = new CategorizedIconView();
        //Tooltip : description
        this.tooltip = FXControlUtils.createTooltip(null);
        //Total
        this.setGraphic(this.useActionIconView);
    }

    @Override
    public void initListener() {
        //Select action on clic
        this.setOnMouseClicked((ea) -> {
            //Create a new instance of the configuration and select it
            this.selectionCallback.accept(this.itemCreator.apply(this.item));
        });
    }

    @Override
    public void initBinding() {
        this.setTooltip(this.tooltip);
        this.useActionIconView.circleColorProperty().set(item.getCategory().getColor());
        this.setText(item.getName());
        this.useActionIconView.imageProperty().set(IconHelper.get(item.getConfigIconPath()));
        this.tooltip.setText(item.getStaticDescription());
    }


}
