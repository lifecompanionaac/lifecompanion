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

package org.lifecompanion.ui.common.pane.generic;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 * Class to wrap a image view into a region to allow its image view to be resizable.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ImageViewPane extends Region {

	private ObjectProperty<ImageView> imageView = new SimpleObjectProperty<>();

	private InvalidationListener invalidationListener;

	// Class part : "Constructor/init"
	//========================================================================
	public ImageViewPane() {
		this(new ImageView());
	}

	public ImageViewPane(final ImageView imageView) {
		this.invalidationListener = (inv) -> {
			this.requestLayout();
		};
		this.imageView.addListener((arg0, oldIV, newIV) -> {
			if (oldIV != null) {
				oldIV.rotateProperty().removeListener(this.invalidationListener);
				oldIV.viewportProperty().removeListener(this.invalidationListener);
				ImageViewPane.this.getChildren().remove(oldIV);
			}
			if (newIV != null) {
				newIV.rotateProperty().addListener(this.invalidationListener);
				newIV.viewportProperty().addListener(this.invalidationListener);
				ImageViewPane.this.getChildren().add(newIV);
			}
			this.requestLayout();
		});
		this.imageView.set(imageView);

	}

	//========================================================================

	// Class part : "Override"
	//========================================================================
	@Override
	protected void layoutChildren() {
		ImageView imageView = this.imageView.get();
		if (imageView != null) {
			double ivWidth = Math.max(1, this.getWidth());
			double ivHeight = Math.max(1, this.getHeight());
			double rotation = imageView.getRotate();
			if (Math.abs(rotation / 90.0 % 2) == 1.0) {
				imageView.setFitWidth(ivHeight);
				imageView.setFitHeight(ivWidth);
			} else {
				imageView.setFitWidth(ivWidth);
				imageView.setFitHeight(ivHeight);
			}
			this.layoutInArea(imageView, 0, 0, ivWidth, ivHeight, 0, HPos.CENTER, VPos.CENTER);
		}
		super.layoutChildren();
	}

	@Override
	protected double computeMinHeight(final double h) {
		return 1.0;
	}

	@Override
	protected double computeMinWidth(final double w) {
		return 1.0;
	}

	//========================================================================

	// Class part : "Getter/setter"
	//========================================================================
	public ObjectProperty<ImageView> imageViewProperty() {
		return this.imageView;
	}

	public ImageView getImageView() {
		return this.imageView.get();
	}

	public void setImageView(final ImageView imageView) {
		this.imageView.set(imageView);
	}
	//========================================================================

}
