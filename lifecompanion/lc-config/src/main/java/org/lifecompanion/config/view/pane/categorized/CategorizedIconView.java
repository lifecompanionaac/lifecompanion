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

package org.lifecompanion.config.view.pane.categorized;

import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * View for use action icons.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CategorizedIconView extends StackPane implements LCViewInitHelper {
	private Circle circle;
	private ImageView imageView;

	public CategorizedIconView() {
		this.initAll();
	}

	@Override
	public void initUI() {
		this.imageView = new ImageView();
		this.circle = new Circle(30, Color.TRANSPARENT);
		this.imageView.preserveRatioProperty().set(true);
		StackPane.setAlignment(this.circle, Pos.CENTER);
		this.getChildren().addAll(this.circle, this.imageView);
	}

	public ObjectProperty<Paint> circleColorProperty() {
		return this.circle.fillProperty();
	}

	public void setIconSize(final double size) {
		this.circle.setRadius(size - 2);
		this.imageView.fitWidthProperty().set(size);
		this.imageView.fitHeightProperty().set(size);
	}

	public ObjectProperty<Image> imageProperty() {
		return this.imageView.imageProperty();
	}
}
