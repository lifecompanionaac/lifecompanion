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

package org.lifecompanion.config.view.pane.left;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.lifecompanion.model.impl.ui.editmode.AddComponentProvider;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.config.data.control.DragController;
import org.lifecompanion.model.api.ui.editmode.PossibleAddComponentCategoryI;
import org.lifecompanion.model.api.ui.editmode.PossibleAddComponentI;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Pane to add component to the configuration.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class AddComponentView extends TitledPane implements LCViewInitHelper {
	//private final static Logger LOGGER = LoggerFactory.getLogger(AddComponentView.class);

	/**
	 * Box that contains every categories
	 */
	private VBox boxCategories;

	private Map<PossibleAddComponentCategoryI, Node> categoryNodes;

	private Map<PossibleAddComponentI<?>, Node> addCompViews;

	public AddComponentView() {
		this.categoryNodes = new HashMap<>();
		this.addCompViews = new HashMap<>();
		this.initAll();
	}

	/**
	 * Initialize the drag listener for the given label with the associated component that can be added
	 * @param addable the component to add
	 * @param label the label where drag is done
	 */
	private void setDragAction(final PossibleAddComponentI<?> addable, final Label label) {
		label.setOnDragDetected((ea) -> {
			Dragboard dragboard = label.startDragAndDrop(TransferMode.ANY);
			DragController.INSTANCE.currentDraggedPossibleAddProperty().set(addable);
			ClipboardContent content = new ClipboardContent();
			content.putString(label.getText());
			dragboard.setContent(content);
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initUI() {
		//Base for titled pane
		this.getStyleClass().add("left-titled-pane");
		this.setText(Translation.getText("panel.add.title").toUpperCase());
		this.boxCategories = new VBox(5.0);
		//Scroll
		ScrollPane scrollAdd = new ScrollPane();
		scrollAdd.setFitToWidth(true);
		scrollAdd.setContent(this.boxCategories);
		scrollAdd.setPrefHeight(180.0);
		this.setContent(scrollAdd);
	}

	@Override
	public void initBinding() {
		//For each category
		Consumer<PossibleAddComponentCategoryI> addListener = (category) -> {
			BorderPane paneCategory = new BorderPane();
			//Title
			Label labelTitle = new Label(category.getTitle());
			labelTitle.setTextAlignment(TextAlignment.CENTER);
			labelTitle.getStyleClass().add("left-part-sub-title");
			labelTitle.setMaxWidth(Double.MAX_VALUE);
			paneCategory.setTop(labelTitle);
			//Content
			TilePane categoryPane = this.createCategoryContent(category);
			paneCategory.setCenter(categoryPane);
			BorderPane.setMargin(categoryPane, new Insets(5.0));
			this.boxCategories.getChildren().add(paneCategory);
			this.categoryNodes.put(category, paneCategory);
		};
		AddComponentProvider.INSTANCE.getCategories().forEach(addListener);
		AddComponentProvider.INSTANCE.getCategories().addListener(LCUtils.createListChangeListener(addListener, (category) -> {
			this.boxCategories.getChildren().remove(this.categoryNodes.get(category));
			this.categoryNodes.remove(category);
		}));
	}

	private TilePane createCategoryContent(final PossibleAddComponentCategoryI category) {
		TilePane pane = new TilePane(5.0, 5.0);
		Consumer<PossibleAddComponentI<?>> addListener = (comp) -> {
			Label label = new Label(Translation.getText(comp.getNameID()));
			Image img = IconManager.get(comp.getIconPath());
			label.setContentDisplay(ContentDisplay.TOP);
			label.setTextAlignment(TextAlignment.CENTER);
			label.setAlignment(Pos.CENTER);
			label.setGraphic(new ImageView(img));
			pane.getChildren().add(label);
			label.setTooltip(UIUtils.createTooltip(Translation.getText(comp.getDescriptionID())));
			this.addCompViews.put(comp, label);
			label.getStyleClass().add("label-add-item");
			ConfigUIUtils.bindShowForLevelFrom(label, comp.getMinimumLevel());
			this.setDragAction(comp, label);
		};
		category.getPossibleAddList().forEach(addListener);
		category.getPossibleAddList().addListener(LCUtils.createListChangeListener(addListener, (comp) -> {
			pane.getChildren().remove(this.addCompViews.get(comp));
			this.addCompViews.remove(comp);
		}));
		return pane;
	}

}
