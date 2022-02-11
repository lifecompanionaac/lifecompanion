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

package org.lifecompanion.base.view.pane.configuration;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * List cell to display a configuration description.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationDescriptionListCell extends ListCell<LCConfigurationDescriptionI> {
	/**
	 * Image view to see configuration preview
	 */
	private ImageView configurationImage;

	private Label labelConfigName;
	private Label labelConfigInfos;
	protected BorderPane boxContent;

	public ConfigurationDescriptionListCell() {
		this.getStyleClass().add("custom-list-cell");
		//Base content
		this.boxContent = new BorderPane();
		this.labelConfigName = new Label();
		this.labelConfigName.setTextAlignment(TextAlignment.CENTER);
		this.labelConfigName.setAlignment(Pos.CENTER);
		this.labelConfigName.getStyleClass().add("import-blue-title");
		this.labelConfigInfos = new Label();
		this.labelConfigInfos.setStyle("-fx-text-fill: gray");
		this.labelConfigInfos.setTextAlignment(TextAlignment.CENTER);

		//Label style and positions
		VBox boxLabel = new VBox(this.labelConfigName, this.labelConfigInfos);
		boxLabel.setAlignment(Pos.CENTER);
		VBox.setMargin(this.labelConfigInfos, new Insets(0, 0, 10, 6.0));
		this.labelConfigName.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(boxLabel, Priority.ALWAYS);
		//Images view
		this.configurationImage = new ImageView();
		this.configurationImage.setFitHeight(100);
		this.configurationImage.fitWidthProperty().bind(this.widthProperty().subtract(20));
		this.configurationImage.setPreserveRatio(true);
		this.configurationImage.setSmooth(true);
		this.setAlignment(Pos.CENTER);
		//Global content
		this.boxContent.setCenter(this.configurationImage);
		this.boxContent.setBottom(boxLabel);
		this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}

	@Override
	protected void updateItem(final LCConfigurationDescriptionI itemP, final boolean emptyP) {
		super.updateItem(itemP, emptyP);
		if (itemP == null || emptyP) {
			this.configurationImage.imageProperty().unbind();
			this.configurationImage.imageProperty().set(null);
			this.labelConfigName.textProperty().unbind();
			this.labelConfigInfos.textProperty().unbind();
			this.setGraphic(null);
		} else {
			itemP.requestImageLoad();
			this.configurationImage.imageProperty().bind(itemP.configurationImageProperty());
			this.labelConfigName.textProperty().bind(itemP.configurationNameProperty());
			this.labelConfigInfos.textProperty().bind(Bindings.createStringBinding(() -> {
				return StringUtils.dateToStringDateWithHour(itemP.configurationLastDateProperty().get()) + "\n"
						+ itemP.configurationAuthorProperty().get();
			}, itemP.configurationLastDateProperty(), itemP.configurationAuthorProperty()));
			this.setGraphic(this.boxContent);
		}
	}

}
