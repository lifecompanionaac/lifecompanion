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
package org.lifecompanion.config.view.pane.tabs.style.part;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class GeneralConfigurationStylePane extends VBox implements LCViewInitHelper {

    //	private LCConfigurationStylePane lcConfigurationStyleView;
    //	private FrameSizePane frameSizeView;
    //	private ConfigurationSizePane configurationSizeView;

    public GeneralConfigurationStylePane() {
        this.initAll();
    }

    @Override
    public void initUI() {
        //		this.lcConfigurationStyleView = new LCConfigurationStylePane();
        //		this.frameSizeView = new FrameSizePane();
        //		this.configurationSizeView = new ConfigurationSizePane();
        //		this.setSpacing(5.0);
        //		this.setPadding(new Insets(10.0));
        //		this.getChildren().addAll(this.createLabelFor("config.style.pane.part.config.style", this.lcConfigurationStyleView),
        //				this.lcConfigurationStyleView, this.createLabelFor("config.style.pane.part.config.size", this.configurationSizeView),
        //				this.configurationSizeView, this.createLabelFor("config.style.pane.part.config.frame", this.frameSizeView), this.frameSizeView);
    }

    private Label createLabelFor(final String textID, final Node node) {
        Label labelConfigStyle = new Label(Translation.getText(textID));
        labelConfigStyle.getStyleClass().add("menu-part-title");
        labelConfigStyle.setMaxWidth(Double.MAX_VALUE);
        labelConfigStyle.managedProperty().bind(labelConfigStyle.visibleProperty());
        labelConfigStyle.visibleProperty().bind(node.visibleProperty());
        return labelConfigStyle;
    }

}
