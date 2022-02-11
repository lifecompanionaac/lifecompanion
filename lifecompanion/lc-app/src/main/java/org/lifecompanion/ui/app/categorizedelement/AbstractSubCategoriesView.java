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

package org.lifecompanion.ui.app.categorizedelement;

import java.util.function.Consumer;

import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementI;
import org.lifecompanion.model.api.categorizedelement.MainCategoryI;
import org.lifecompanion.model.api.categorizedelement.SubCategoryI;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public abstract class AbstractSubCategoriesView<V extends CategorizedElementI<T>, T extends SubCategoryI<K, V>, K extends MainCategoryI<T>>
		extends ScrollPane implements LCViewInitHelper {
	/**
	 * Main category
	 */
	private K mainCategory;

	/**
	 * Box that contains every sub categories content
	 */
	private VBox boxSubCategories;

	private Consumer<V> selectionCallback;

	/**
	 * Construct a sub category view for a given main category.<br>
	 * Display all the use action and sub categories in the given main category.
	 */
	public AbstractSubCategoriesView(final K mainCategoryP, final Consumer<V> selectionCallbackP) {
		this.mainCategory = mainCategoryP;
		this.selectionCallback = selectionCallbackP;
		this.initAll();
	}

	// Class part : "UI"
	//========================================================================
	@Override
	public void initUI() {
		this.boxSubCategories = new VBox();
		this.boxSubCategories.setAlignment(Pos.TOP_LEFT);
		//Scroll
		this.getStyleClass().add("transparent-border-scroll-pane");
		this.setContent(this.boxSubCategories);
		this.setFitToWidth(true);
	}

	@Override
	public void initListener() {}

	@Override
	public void initBinding() {
		//For each sub category, create the component
		ObservableList<T> subCategories = this.mainCategory.getSubCategories();
		for (T subCategory : subCategories) {
			this.boxSubCategories.getChildren().add(this.createContentView(subCategory, this.selectionCallback));
		}
	}

	protected abstract Node createContentView(T subCategory, Consumer<V> selectionCallback);
	//========================================================================

}
