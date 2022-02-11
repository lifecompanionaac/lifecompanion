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

package org.lifecompanion.ui.common.control.specific;

import java.util.Arrays;
import java.util.List;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.controller.editaction.KeyActions.ChangeViewportAction;
import org.lifecompanion.util.PositionSize;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.ui.common.pane.generic.BaseConfigurationViewBorderPane;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Control to select the viewport for a image use component.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ViewportSelectorControl extends BaseConfigurationViewBorderPane<ImageUseComponentI> implements LCViewInitHelper {

	private static final double SIZE = LCGraphicStyle.VIEWPORT_COMPONENT_SIZE;
	private static final double STROKE_SIZE = 3;
	private static final double IMAGE_SIZE = 150;

	private ScrollPane scrollCenter;
	private ImageView imageViewDisplayed;
	private Pane paneDisplayer;
	private Label labelDescription;

	private PositionSize initialState;

	private Circle lt, lb, rt, rb;
	private Rectangle stroke;
	private double sX, sY;
	private double srX, srY;

	private DoubleProperty imageHeight, imageWidth;

	public ViewportSelectorControl() {
		this.initAll();
	}

	public ObjectProperty<ImageUseComponentI> modelProperty() {
		return this.model;
	}

	// Class part : "UI"
	//========================================================================
	@Override
	public void initUI() {
		//Image displayer
		this.imageViewDisplayed = new ImageView();
		this.imageViewDisplayed.setPreserveRatio(false);
		this.imageViewDisplayed.setFitHeight(ViewportSelectorControl.IMAGE_SIZE);
		this.imageViewDisplayed.setFitWidth(ViewportSelectorControl.IMAGE_SIZE);

		//Stack pane to display selection zone
		this.paneDisplayer = new StackPane();
		this.paneDisplayer.prefWidthProperty().bind(this.imageViewDisplayed.fitWidthProperty().multiply(4.0));
		this.paneDisplayer.prefHeightProperty().bind(this.imageViewDisplayed.fitHeightProperty().multiply(4.0));

		//Add
		StackPane.setAlignment(this.imageViewDisplayed, Pos.CENTER);
		this.paneDisplayer.getChildren().add(this.imageViewDisplayed);
		this.scrollCenter = new ScrollPane(this.paneDisplayer);
		this.setCenter(this.scrollCenter);

		//Description
		this.labelDescription = new Label(Translation.getText("viewport.selector.control.description"));
		this.labelDescription.setWrapText(true);
		this.labelDescription.getStyleClass().add("explain-text");
		this.setTop(this.labelDescription);

		this.createResize();
	}

	@Override
	public void initBinding() {
		this.imageWidth = this.imageViewDisplayed.fitWidthProperty();
		this.imageHeight = this.imageViewDisplayed.fitHeightProperty();
	}

	@Override
	public void initListener() {
		this.imageViewDisplayed.imageProperty().addListener((obs, ov, nv) -> {
			//Center scroll when image change
			this.scrollCenter.setVvalue(0.5);
			this.scrollCenter.setHvalue(0.5);
		});
	}
	//========================================================================

	// Class part : "State"
	//========================================================================
	private void viewPortChangeStarted() {
		this.initialState = PositionSize.create(this.model.get());
	}

	private void viewPortChangeEnded() {
		ChangeViewportAction action = new ChangeViewportAction(this.model.get(), this.initialState, PositionSize.create(this.model.get()));
		ConfigActionController.INSTANCE.addAction(action);
	}
	//========================================================================

	/**
	 * Create all the resize point and their behavior.
	 */
	private void createResize() {
		this.stroke = new Rectangle(100, 100);
		this.stroke.setTranslateX(50);
		this.stroke.setTranslateY(50);
		this.stroke.setFill(null);
		this.stroke.setStroke(LCGraphicStyle.SECOND_DARK);
		this.stroke.setStrokeWidth(ViewportSelectorControl.STROKE_SIZE);

		//Binding on side
		DoubleBinding leftSide = this.stroke.translateXProperty().subtract(ViewportSelectorControl.SIZE / 2.0);
		DoubleBinding topSide = this.stroke.translateYProperty().subtract(ViewportSelectorControl.SIZE / 2.0);
		DoubleBinding rightSide = this.stroke.translateXProperty().add(this.stroke.widthProperty()).subtract(ViewportSelectorControl.SIZE);
		DoubleBinding bottomSide = this.stroke.translateYProperty().add(this.stroke.heightProperty()).subtract(ViewportSelectorControl.SIZE);

		// Left
		this.lt = new Circle(ViewportSelectorControl.SIZE);
		this.lt.translateXProperty().bind(leftSide);
		this.lt.translateYProperty().bind(topSide);
		this.lb = new Circle(ViewportSelectorControl.SIZE);
		this.lb.translateXProperty().bind(leftSide);
		this.lb.translateYProperty().bind(bottomSide);
		// Right
		this.rt = new Circle(ViewportSelectorControl.SIZE);
		this.rt.translateXProperty().bind(rightSide);
		this.rt.translateYProperty().bind(topSide);
		this.rb = new Circle(ViewportSelectorControl.SIZE);
		this.rb.translateXProperty().bind(rightSide);
		this.rb.translateYProperty().bind(bottomSide);
		// Add
		List<Shape> nodes = Arrays.asList(this.stroke, this.lt, this.lb, this.rt, this.rb);
		for (Shape shape : nodes) {
			StackPane.setAlignment(shape, Pos.TOP_LEFT);
		}
		this.paneDisplayer.getChildren().addAll(nodes);
		// Resize
		this.setResize(this.lb, Cursor.SW_RESIZE);
		this.setResize(this.lt, Cursor.NW_RESIZE);
		this.setResize(this.rb, Cursor.SE_RESIZE);
		this.setResize(this.rt, Cursor.NE_RESIZE);

		//Stroke move
		this.stroke.setOnMouseEntered((me) -> {
			this.stroke.setCursor(Cursor.MOVE);
		});
		this.stroke.setOnMouseExited((me) -> {
			this.stroke.setCursor(Cursor.DEFAULT);
		});
		this.stroke.setOnMousePressed((me) -> {
			this.viewPortChangeStarted();
			this.srX = me.getX();
			this.srY = me.getY();
		});
		this.stroke.setOnMouseReleased((me) -> {
			this.viewPortChangeEnded();
		});
		this.stroke.setOnMouseDragged((me) -> {
			this.stroke.setCursor(Cursor.MOVE);
			double dx = me.getX() - this.srX;
			double dy = me.getY() - this.srY;
			ImageUseComponentI comp = this.model.get();
			if (comp != null) {
				double x = comp.viewportXPercentProperty().get() * this.imageWidth.get();
				double y = comp.viewportYPercentProperty().get() * this.imageHeight.get();
				comp.viewportXPercentProperty().set(this.convertToPercentWidth(x + dx));
				comp.viewportYPercentProperty().set(this.convertToPercentHeight(y + dy));
			}
		});
	}

	/**
	 * Define the event for the given rectangle function of the given cursor.
	 * @param circ the circle where event will happen
	 * @param cursor the cursor, that define the behavior
	 */
	private void setResize(final Circle circ, final Cursor cursor) {
		//Base style
		circ.setFill(LCGraphicStyle.SECOND_DARK);
		//Enter, cursor and color
		circ.setOnMouseEntered(me -> {
			circ.setCursor(cursor);
			circ.setFill(LCGraphicStyle.SECOND_LIGHT);
		});
		//Exit : color reset
		circ.setOnMouseExited((ea) -> {
			if (!ea.isPrimaryButtonDown()) {
				circ.setFill(LCGraphicStyle.SECOND_DARK);
			}
		});
		//On released : end resize
		circ.setOnMouseReleased((me) -> {
			this.viewPortChangeEnded();
			circ.setFill(LCGraphicStyle.SECOND_DARK);
		});
		//On press, start resize
		circ.setOnMousePressed(me -> {
			this.viewPortChangeStarted();
			circ.setFill(LCGraphicStyle.SECOND_LIGHT);
			this.sX = me.getX();
			this.sY = me.getY();
		});
		//On drag : resize/move
		circ.setOnMouseDragged(me -> {
			double dx = me.getX() - this.sX;
			double dy = me.getY() - this.sY;
			// Function of cursor
			ImageUseComponentI comp = this.model.get();
			double x = comp.viewportXPercentProperty().get() * this.imageWidth.get();
			double y = comp.viewportYPercentProperty().get() * this.imageHeight.get();
			double width = comp.viewportWidthPercentProperty().get() * this.imageWidth.get();
			double height = comp.viewportHeightPercentProperty().get() * this.imageHeight.get();
			if (cursor == Cursor.SE_RESIZE) {
				comp.viewportWidthPercentProperty().set(this.convertToPercentWidth(width + dx));
				comp.viewportHeightPercentProperty().set(this.convertToPercentHeight(height + dy));
			} else if (cursor == Cursor.SW_RESIZE) {
				comp.viewportXPercentProperty().set(this.convertToPercentWidth(x + dx));
				if (comp.viewportXPercentProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					comp.viewportWidthPercentProperty().set(this.convertToPercentWidth(width - dx));
				}
				comp.viewportHeightPercentProperty().set(this.convertToPercentHeight(height + dy));
			} else if (cursor == Cursor.NE_RESIZE) {
				comp.viewportYPercentProperty().set(this.convertToPercentHeight(y + dy));
				comp.viewportWidthPercentProperty().set(this.convertToPercentWidth(width + dx));
				if (comp.viewportYPercentProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					comp.viewportHeightPercentProperty().set(this.convertToPercentHeight(height - dy));
				}
				//this.sX = me.getX();
			} else if (cursor == Cursor.NW_RESIZE) {
				comp.viewportXPercentProperty().set(this.convertToPercentWidth(x + dx));
				comp.viewportYPercentProperty().set(this.convertToPercentHeight(y + dy));
				if (comp.viewportXPercentProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					comp.viewportWidthPercentProperty().set(this.convertToPercentWidth(width - dx));
				}
				if (comp.viewportYPercentProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					comp.viewportHeightPercentProperty().set(this.convertToPercentHeight(height - dy));
				}
			}
		});
	}

	private double convertToPercentWidth(final double w) {
		return w / this.imageWidth.get();
	}

	private double convertToPercentHeight(final double h) {
		return h / this.imageHeight.get();
	}

	// Class part : "Binding image use component"
	//========================================================================
	@Override
	public void bind(final ImageUseComponentI model) {
		this.imageViewDisplayed.imageProperty().bind(model.loadedImageProperty());
		//Bind stroke
		this.stroke.translateXProperty()
				.bind(model.viewportXPercentProperty().multiply(this.imageWidth).add(this.imageViewDisplayed.layoutXProperty()));
		this.stroke.translateYProperty()
				.bind(model.viewportYPercentProperty().multiply(this.imageHeight).add(this.imageViewDisplayed.layoutYProperty()));
		this.stroke.widthProperty().bind(model.viewportWidthPercentProperty().multiply(this.imageWidth));
		this.stroke.heightProperty().bind(model.viewportHeightPercentProperty().multiply(this.imageHeight));
	}

	@Override
	public void unbind(final ImageUseComponentI model) {
		this.imageViewDisplayed.imageProperty().unbind();
		this.stroke.translateXProperty().unbind();
		this.stroke.translateYProperty().unbind();
		this.stroke.widthProperty().unbind();
		this.stroke.heightProperty().unbind();
	}
	//========================================================================

}
