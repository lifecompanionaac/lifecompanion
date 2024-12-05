package org.lifecompanion.plugin.phonecontrol.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.phonecontrol.PhoneControlController;
import org.lifecompanion.plugin.phonecontrol.PhoneControlPlugin;
import org.lifecompanion.plugin.phonecontrol.PhoneControlPluginProperties;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.common.control.generic.DurationPickerControl;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.ArrayList;

public class PhoneControlGeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI {
    private LCConfigurationI configuration;

    public PhoneControlGeneralConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "phonecontrol.plugin.config.title";
    }

    @Override
    public String getStep() {
        return "PhoneControlGeneralConfigView";
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() { }

    @Override
    public void initListener() { }

    @Override
    public void beforeShow(Object[] stepArgs) { }

    @Override
    public void saveChanges() {
        PhoneControlPluginProperties phoneControlPluginProperties = configuration.getPluginConfigProperties(PhoneControlPlugin.PLUGIN_ID, PhoneControlPluginProperties.class);
    }

    @Override
    public void cancelChanges() { }

    @Override
    public void bind(LCConfigurationI model) { }

    @Override
    public void unbind(LCConfigurationI model) {
        this.configuration = null;
    }
}
