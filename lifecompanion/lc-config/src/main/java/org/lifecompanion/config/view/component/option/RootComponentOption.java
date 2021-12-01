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

package org.lifecompanion.config.view.component.option;

import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.config.data.action.impl.OptionActions.ResizeAction;
import org.lifecompanion.base.data.common.PositionSize;
import org.lifecompanion.api.component.definition.RootGraphicComponentI;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.config.data.control.ConfigActionController;
import javafx.scene.Cursor;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Class that keep all the possible option for root component.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RootComponentOption extends BaseOptionRegion<RootGraphicComponentI> implements LCViewInitHelper {
	public RootComponentOption(final RootGraphicComponentI modelP) {
		super(modelP);
		this.initAll();
		this.setPickOnBounds(false);
	}

	/**
	 * Initialize ui controllers
	 */
	@Override
	public void initUI() {
		this.createResize();
	}

	// Class part : "Resize"
	//========================================================================
	private static final double SIZE = UserBaseConfiguration.INSTANCE.selectionStrokeSizeProperty().get() * 1.8;
	private static final double GAP = 1;
	private Circle lt, lb, rt, rb, lm, rm, tm, bm;
	private Rectangle stroke;
	private double sX, sY;
	private PositionSize initialResizeState;

	/**
	 * Called when the resize start.<br>
	 * Save the current model state.
	 */
	private void resizeStart() {
		this.initialResizeState = PositionSize.create(this.model);
		this.model.resizingProperty().set(true);
	}

	/**
	 * Called when the resize ended.<br>
	 * Add a resize action in history.
	 */
	private void resizeEnd() {
		this.model.resizingProperty().set(false);
		ResizeAction<RootGraphicComponentI> action = new ResizeAction<>(this.initialResizeState, PositionSize.create(this.model), this.model);
		ConfigActionController.INSTANCE.addAction(action);
	}

	/**
	 * Create all the rectangle and their behavior.
	 */
	private void createResize() {
		// Left
		this.lt = new Circle(-RootComponentOption.GAP - RootComponentOption.SIZE, -RootComponentOption.GAP - RootComponentOption.SIZE,
				RootComponentOption.SIZE);
		this.lb = new Circle(-RootComponentOption.GAP - RootComponentOption.SIZE, -1, RootComponentOption.SIZE);
		this.lb.centerYProperty().bind(this.heightProperty().add(RootComponentOption.GAP + RootComponentOption.SIZE));
		// Right
		this.rt = new Circle(-1, -RootComponentOption.GAP - RootComponentOption.SIZE, RootComponentOption.SIZE);
		this.rt.centerXProperty().bind(this.widthProperty().add(RootComponentOption.GAP + RootComponentOption.SIZE));
		this.rb = new Circle(-1, -1, RootComponentOption.SIZE);
		this.rb.centerXProperty().bind(this.widthProperty().add(RootComponentOption.GAP + RootComponentOption.SIZE));
		this.rb.centerYProperty().bind(this.heightProperty().add(RootComponentOption.GAP + RootComponentOption.SIZE));
		// Middle bottom/top
		this.tm = new Circle(-1, -RootComponentOption.GAP - RootComponentOption.SIZE, RootComponentOption.SIZE);
		this.tm.centerXProperty().bind(this.widthProperty().divide(2));
		this.bm = new Circle(-1, -1, RootComponentOption.SIZE);
		this.bm.centerXProperty().bind(this.widthProperty().divide(2));
		this.bm.centerYProperty().bind(this.heightProperty().add(RootComponentOption.GAP + RootComponentOption.SIZE));
		// Middle left/right
		this.lm = new Circle(-RootComponentOption.GAP - RootComponentOption.SIZE, -1, RootComponentOption.SIZE);
		this.lm.centerYProperty().bind(this.heightProperty().divide(2));
		this.rm = new Circle(-1, -1, RootComponentOption.SIZE);
		this.rm.centerYProperty().bind(this.heightProperty().divide(2));
		this.rm.centerXProperty().bind(this.widthProperty().add(RootComponentOption.GAP + RootComponentOption.SIZE));
		//Over
		this.stroke = new Rectangle();
		this.stroke.layoutXProperty().set(-RootComponentOption.GAP - RootComponentOption.SIZE);
		this.stroke.layoutYProperty().set(-RootComponentOption.GAP - RootComponentOption.SIZE);
		this.stroke.widthProperty().bind(this.widthProperty().add(2.0 * (RootComponentOption.GAP + RootComponentOption.SIZE)));
		this.stroke.heightProperty().bind(this.heightProperty().add(2.0 * (RootComponentOption.GAP + RootComponentOption.SIZE)));
		this.stroke.setFill(null);
		this.stroke.setStroke(LCGraphicStyle.SECOND_DARK);
		this.stroke.visibleProperty().bind(this.model.showSelectedProperty());
		this.stroke.strokeWidthProperty().bind(UserBaseConfiguration.INSTANCE.selectionStrokeSizeProperty());
		// Add
		this.getChildren().addAll(this.stroke, this.lt, this.lb, this.rt, this.rb, this.lm, this.rm, this.tm, this.bm);
		// Resize
		this.setResize(this.lb, Cursor.SW_RESIZE);
		this.setResize(this.lt, Cursor.NW_RESIZE);
		this.setResize(this.rb, Cursor.SE_RESIZE);
		this.setResize(this.rt, Cursor.NE_RESIZE);
		this.setResize(this.tm, Cursor.N_RESIZE);
		this.setResize(this.bm, Cursor.S_RESIZE);
		this.setResize(this.lm, Cursor.W_RESIZE);
		this.setResize(this.rm, Cursor.E_RESIZE);
	}

	/**
	 * Define the event for the given rectangle function of the given cursor.
	 * @param circ the circle where event will happen
	 * @param cursor the cursor, that define the behavior
	 */
	private void setResize(final Circle circ, final Cursor cursor) {
		//Base style
		circ.visibleProperty().bind(this.model.showSelectedProperty());
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
			this.resizeEnd();
			circ.setFill(LCGraphicStyle.SECOND_DARK);
		});
		//On press, start resize
		circ.setOnMousePressed(me -> {
			this.resizeStart();
			circ.setFill(LCGraphicStyle.SECOND_LIGHT);
			RootComponentOption.this.sX = me.getX();
			RootComponentOption.this.sY = me.getY();
		});
		//On drag : resize/move
		circ.setOnMouseDragged(me -> {
			double dx = me.getX() - RootComponentOption.this.sX;
			double dy = me.getY() - RootComponentOption.this.sY;
			// Function of cursor
			if (cursor == Cursor.SE_RESIZE) {
				RootComponentOption.this.model.widthProperty()
						.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.widthProperty().get() + dx));
				RootComponentOption.this.model.heightProperty()
						.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.heightProperty().get() + dy));
				RootComponentOption.this.sX = Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, me.getX());
				RootComponentOption.this.sY = me.getY();
			} else if (cursor == Cursor.SW_RESIZE) {
				RootComponentOption.this.model.xProperty()
						.set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, RootComponentOption.this.model.xProperty().get() + dx));
				if (this.model.xProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					RootComponentOption.this.model.widthProperty()
							.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.widthProperty().get() - dx));
				}
				RootComponentOption.this.model.heightProperty()
						.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.heightProperty().get() + dy));
				RootComponentOption.this.sY = me.getY();
			} else if (cursor == Cursor.NE_RESIZE) {
				RootComponentOption.this.model.yProperty()
						.set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, RootComponentOption.this.model.yProperty().get() + dy));
				RootComponentOption.this.model.widthProperty()
						.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.widthProperty().get() + dx));
				if (this.model.yProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					RootComponentOption.this.model.heightProperty()
							.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.heightProperty().get() - dy));
				}
				RootComponentOption.this.sX = me.getX();
			} else if (cursor == Cursor.NW_RESIZE) {
				RootComponentOption.this.model.xProperty()
						.set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, RootComponentOption.this.model.xProperty().get() + dx));
				RootComponentOption.this.model.yProperty()
						.set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, RootComponentOption.this.model.yProperty().get() + dy));
				if (this.model.xProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					RootComponentOption.this.model.widthProperty()
							.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.widthProperty().get() - dx));
				}
				if (this.model.yProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					RootComponentOption.this.model.heightProperty()
							.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.heightProperty().get() - dy));
				}
			} else if (cursor == Cursor.N_RESIZE) {
				RootComponentOption.this.model.yProperty()
						.set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, RootComponentOption.this.model.yProperty().get() + dy));
				if (this.model.yProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					RootComponentOption.this.model.heightProperty()
							.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.heightProperty().get() - dy));
				}
			} else if (cursor == Cursor.S_RESIZE) {
				RootComponentOption.this.model.heightProperty()
						.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.heightProperty().get() + dy));
				RootComponentOption.this.sY = me.getY();
			} else if (cursor == Cursor.E_RESIZE) {
				RootComponentOption.this.model.widthProperty()
						.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.widthProperty().get() + dx));
				RootComponentOption.this.sX = me.getX();
			} else if (cursor == Cursor.W_RESIZE) {
				RootComponentOption.this.model.xProperty()
						.set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, RootComponentOption.this.model.xProperty().get() + dx));
				if (this.model.xProperty().get() != LCConstant.CONFIG_ROOT_COMPONENT_GAP) {
					RootComponentOption.this.model.widthProperty()
							.set(Math.max(LCConstant.MIN_SIZE_COMPONENT, RootComponentOption.this.model.widthProperty().get() - dx));
				}
			}
		});
	}
	//========================================================================

}
