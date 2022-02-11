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

package org.lifecompanion.util;


import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.MovableComponentI;
import org.lifecompanion.model.api.configurationcomponent.ResizableComponentI;

import java.util.Objects;

/**
 * Class that keep a position and a size as double values.<br>
 * This class is a easy way to save/get position and size.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PositionSize {
	private final double x, y, width, height;

	public PositionSize(final double xP, final double yP, final double widthP, final double heightP) {
		this.x = xP;
		this.y = yP;
		this.width = widthP;
		this.height = heightP;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getWidth() {
		return this.width;
	}

	public double getHeight() {
		return this.height;
	}

	@Override
	public String toString() {
		return "x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PositionSize that = (PositionSize) o;
		return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.width, width) == 0 && Double.compare(that.height, height) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, width, height);
	}

	// Class part : "Helpful methods"
	//========================================================================

	public <T extends ResizableComponentI> void setSizeOn(final T comp) {
		PositionSize.setSize(this, comp);
	}

	public <T extends MovableComponentI> void setPositionOn(final T comp) {
		PositionSize.setPosition(this, comp);
	}

	public static PositionSize create(final ImageUseComponentI imageUseComp) {
		return new PositionSize(imageUseComp.viewportXPercentProperty().get(), imageUseComp.viewportYPercentProperty().get(),
				imageUseComp.viewportWidthPercentProperty().get(), imageUseComp.viewportHeightPercentProperty().get());
	}

	public void setPositionAndSizeOn(final ImageUseComponentI comp) {
		comp.viewportXPercentProperty().set(this.x);
		comp.viewportYPercentProperty().set(this.y);
		comp.viewportWidthPercentProperty().set(this.width);
		comp.viewportHeightPercentProperty().set(this.height);
	}

	public <T extends ResizableComponentI & MovableComponentI> void setPositionAndSizeOn(final T comp) {
		PositionSize.setPositionAndSize(this, comp);
	}

	public static <T extends ResizableComponentI & MovableComponentI> PositionSize create(final T comp) {
		return new PositionSize(comp.xProperty().get(), comp.yProperty().get(), comp.widthProperty().get(), comp.heightProperty().get());
	}

	public static void setSize(final PositionSize size, final ResizableComponentI comp) {
		comp.widthProperty().set(size.getWidth());
		comp.heightProperty().set(size.getHeight());
	}

	public static void setPosition(final PositionSize size, final MovableComponentI comp) {
		comp.xProperty().set(size.getX());
		comp.yProperty().set(size.getY());
	}

	public static <T extends ResizableComponentI & MovableComponentI> void setPositionAndSize(final PositionSize size, final T comp) {
		PositionSize.setSize(size, comp);
		PositionSize.setPosition(size, comp);
	}
	//========================================================================
}
