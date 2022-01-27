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

package org.lifecompanion.config.view.pane.tabs.selected.part;

import org.controlsfx.glyphfont.FontAwesome;

import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.view.selection.ComponentSelectionSearchList;
import org.lifecompanion.config.view.selection.ComponentSelectionTree;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * A component that just display a simple button to trigger the selection tree view to show.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ComponentSelectionPopTrigger extends BorderPane implements LCViewInitHelper {
	private static final long ANIMATION_DURATION = 400;
	private static final double EXPANDED_WIDTH = 200.0;
	private Button buttonPop;
	private ComponentSelectionTree selectionTreeView;
	private ComponentSelectionSearchList componentSelectionSearchList;
	private TextField fieldSearch;
	private boolean expanded = true;
	private Timeline animationExpand, animationCollapse;

	private BorderPane selectionPaneView;

	public ComponentSelectionPopTrigger() {
		this.initAll();
	}

	@Override
	public void initUI() {
		//Center part, show/hide
		this.selectionTreeView = new ComponentSelectionTree();
		//Right part, button to hide/show
		VBox boxRight = new VBox();
		boxRight.getStyleClass().add("box-selection-button");
		boxRight.setAlignment(Pos.CENTER);
		this.buttonPop = UIUtils.createGraphicButton(
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY),
				"tooltip.component.selection.view");
		boxRight.getChildren().add(this.buttonPop);
		this.buttonPop.setRotate(180.0);
		//Total
		this.setRight(boxRight);

		//Bottom
		this.fieldSearch = new TextField();
		this.fieldSearch.setPromptText(Translation.getText("search.component.selection.tree"));

		// Search list
		this.componentSelectionSearchList = new ComponentSelectionSearchList();

		//Selection view
		this.selectionPaneView = new BorderPane();
		//this.selectionPaneView.setVisible(false);
		this.selectionPaneView.setPrefWidth(ComponentSelectionPopTrigger.EXPANDED_WIDTH);
		this.selectionPaneView.managedProperty().bind(this.selectionPaneView.visibleProperty());
		this.selectionPaneView.setCenter(this.selectionTreeView);
		this.selectionPaneView.setBottom(this.fieldSearch);
		this.selectionPaneView.getStyleClass().add("box-selection-center");
		BorderPane.setMargin(this.fieldSearch, new Insets(3.0, 0.0, 3.0, 0.0));
		this.setCenter(this.selectionPaneView);

		//Expand animation
		this.animationExpand = new Timeline();
		this.animationExpand.setCycleCount(1);
		final KeyValue kvE = new KeyValue(this.selectionPaneView.prefWidthProperty(), ComponentSelectionPopTrigger.EXPANDED_WIDTH,
				Interpolator.EASE_BOTH);
		final KeyValue kvER = new KeyValue(this.buttonPop.rotateProperty(), 180, Interpolator.EASE_BOTH);
		final KeyFrame kfE = new KeyFrame(Duration.millis(ComponentSelectionPopTrigger.ANIMATION_DURATION), kvE, kvER);
		this.animationExpand.getKeyFrames().add(kfE);

		//Collapse animation
		this.animationCollapse = new Timeline();
		this.animationCollapse.setCycleCount(1);
		final KeyValue kvC = new KeyValue(this.selectionPaneView.prefWidthProperty(), 0, Interpolator.EASE_BOTH);
		final KeyValue kvCR = new KeyValue(this.buttonPop.rotateProperty(), 0, Interpolator.EASE_BOTH);
		final KeyFrame kfC = new KeyFrame(Duration.millis(ComponentSelectionPopTrigger.ANIMATION_DURATION), kvC, kvCR);
		this.animationCollapse.getKeyFrames().add(kfC);
		this.animationCollapse.setOnFinished((ea) -> {
			this.selectionPaneView.setVisible(false);
		});
		UIUtils.applyPerformanceConfiguration(boxRight);
	}

	@Override
	public void initListener() {
		this.buttonPop.setOnAction((ea) -> {
			//Expand
			if (!this.expanded) {
				this.expand();
			}
			//Collapse
			else {
				this.collapse();
			}
		});
		this.fieldSearch.textProperty().addListener((obs, ov, nv) -> {
			if (StringUtils.isBlank(nv)) {
				this.displaySelectionTree();
			} else {
				this.displaySearchResult(nv);
			}
		});
	}

	private void displaySearchResult(final String txt) {
		if (this.selectionPaneView.getCenter() != this.componentSelectionSearchList) {
			this.selectionPaneView.setCenter(this.componentSelectionSearchList);
		}
		this.componentSelectionSearchList.search(txt);
	}

	private void displaySelectionTree() {
		if (this.selectionPaneView.getCenter() != this.selectionTreeView) {
			this.selectionPaneView.setCenter(this.selectionTreeView);
		}
	}

	public void collapse() {
		//TODO : one animation with rate
		this.animationExpand.stop();
		this.animationCollapse.play();
		this.expanded = false;
	}

	public void expand() {
		this.animationCollapse.stop();
		this.animationExpand.play();
		this.selectionPaneView.setVisible(true);
		this.expanded = true;
	}

	@Override
	public void initBinding() {
		AppModeController.INSTANCE.getEditModeContext().configurationProperty().addListener((obs, ov, nv) -> {
			this.fieldSearch.clear();
		});
	}

}
