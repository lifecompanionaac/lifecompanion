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

import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class ConfigurationSimpleListCell extends ListCell<LCConfigurationDescriptionI> {
	/**
	 * Image view to see configuration preview
	 */
	private final ImageView configurationImage;

	public ConfigurationSimpleListCell() {
		//Images view
		this.configurationImage = new ImageView();
		this.configurationImage.setFitHeight(100);
		this.configurationImage.fitWidthProperty().bind(this.widthProperty().subtract(20));
		this.configurationImage.setPreserveRatio(true);
		this.configurationImage.setSmooth(true);
		this.setAlignment(Pos.CENTER);
		//Global content
		this.setContentDisplay(ContentDisplay.TOP);
	}

	@Override
	protected void updateItem(final LCConfigurationDescriptionI itemP, final boolean emptyP) {
		super.updateItem(itemP, emptyP);
		if (itemP == null || emptyP) {
			this.configurationImage.imageProperty().unbind();
			this.configurationImage.imageProperty().set(null);
			this.textProperty().unbind();
			this.setText(null);
			this.setGraphic(null);
		} else {
			itemP.requestImageLoad();
			this.configurationImage.imageProperty().bind(itemP.configurationImageProperty());
			this.textProperty().bind(itemP.configurationNameProperty());
			this.setGraphic(this.configurationImage);
		}
	}
}
