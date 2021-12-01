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

package org.lifecompanion.installer.ui.model.step;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.fx.control.SimpleDirectorySelector;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.installer.controller.InstallerManager;
import org.lifecompanion.installer.ui.model.InstallerStep;

import java.util.Arrays;
import java.util.List;

public class ConfigStep extends VBox implements InstallerStep {
    private SimpleDirectorySelector fileSelectorData, fileSelectorSoftware;
    private Label labelPluginIds;
    private TextField fieldPluginIds;

    public ConfigStep() {
        super(5.0);
    }

    @Override
    public Node getContent() {
        return this;
    }

    // Class part : "UI"
    //========================================================================

    @Override
    public void initUI() {
        Label labelExplainConfiguration = new Label(Translation.getText("lc.installer.step.config.explain.text"));
        labelExplainConfiguration.setWrapText(true);
        VBox.setMargin(labelExplainConfiguration, new Insets(0.0, 0.0, 10.0, 0.0));
        fileSelectorData = new SimpleDirectorySelector("LifeCompanion");
        fileSelectorSoftware = new SimpleDirectorySelector("LifeCompanion");
        fieldPluginIds = new TextField();

        this.setAlignment(Pos.CENTER);
        Label labelDataDir = new Label(Translation.getText("lc.installer.step.config.file.selector.data.dir"));
        labelDataDir.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(labelExplainConfiguration, labelDataDir, fileSelectorData,
                new Label(Translation.getText("lc.installer.step.config.file.selector.soft.dir")), fileSelectorSoftware, labelPluginIds = new Label(Translation.getText("lc.installer.step.config.plugin.ids")), fieldPluginIds);
    }

    @Override
    public void initBinding() {
        this.labelPluginIds.visibleProperty().bind(this.labelPluginIds.managedProperty());
        this.fieldPluginIds.managedProperty().bind(this.fieldPluginIds.visibleProperty());
        this.labelPluginIds.managedProperty().bind(fieldPluginIds.managedProperty());
    }
    //========================================================================

    // Class part : "MODEL"
    //========================================================================
    @Override
    public void stepDisplayed() {
        this.fileSelectorData.fileProperty().set(InstallerManager.INSTANCE.getConfiguration().getInstallationUserDataDirectory());
        this.fileSelectorSoftware.fileProperty().set(InstallerManager.INSTANCE.getConfiguration().getInstallationSoftwareDirectory());
        List<String> pluginToInstallIds = InstallerManager.INSTANCE.getConfiguration().getPluginToInstallIds();
        if (CollectionUtils.isEmpty(pluginToInstallIds) && SystemType.current() == SystemType.WINDOWS) {
            this.fieldPluginIds.setText(null);
            this.fieldPluginIds.setVisible(false);
        } else {
            this.fieldPluginIds.setText(CollectionUtils.isEmpty(pluginToInstallIds) ? "" : String.join(";", pluginToInstallIds));
            this.fieldPluginIds.setVisible(true);
        }
    }

    @Override
    public void stepHidden() {
        InstallerManager.INSTANCE.getConfiguration().setInstallationUserDataDirectory(this.fileSelectorData.fileProperty().get());
        InstallerManager.INSTANCE.getConfiguration().setInstallationSoftwareDirectory(this.fileSelectorSoftware.fileProperty().get());
        if (StringUtils.isNotBlank(fieldPluginIds.getText())) {
            InstallerManager.INSTANCE.getConfiguration().setPluginToInstallIds(Arrays.asList(fieldPluginIds.getText().split(";")));
        } else {
            InstallerManager.INSTANCE.getConfiguration().setPluginToInstallIds(null);
        }
    }

    public ReadOnlyBooleanProperty nextButtonAvailable() {
        return new SimpleBooleanProperty(true);
    }

    public ReadOnlyBooleanProperty previousButtonAvailable() {
        return new SimpleBooleanProperty(true);
    }
    //========================================================================

}
