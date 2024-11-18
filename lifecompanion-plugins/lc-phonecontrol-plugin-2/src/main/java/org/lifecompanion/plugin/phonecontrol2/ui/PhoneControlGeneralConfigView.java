package org.lifecompanion.plugin.phonecontrol2.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.phonecontrol2.PhoneControlPlugin;
import org.lifecompanion.plugin.phonecontrol2.PhoneControlPluginProperties;
import org.lifecompanion.plugin.phonecontrol2.controller.PhoneControlController;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.FXControlUtils;

public class PhoneControlGeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI {

    static final String STEP_ID = "PhoneControlGeneralConfigView2";

    private Label labelConnectedDevice;
    private Button buttonChooseDevice;
    private Button buttonRefresh;
    private ComboBox<String> comboBoxDevices;

    public PhoneControlGeneralConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "phonecontrol2.plugin.config.view.general.title";
    }

    @Override
    public String getStep() {
        return STEP_ID;
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
    public void initUI() {
        // Initial configuration for the grid pane
        GridPane gridPaneConfiguration = new GridPane();
        gridPaneConfiguration.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneConfiguration.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int gridRowIndex = 0;

        // Category title
        gridPaneConfiguration.add(
                FXControlUtils.createTitleLabel("phonecontrol2.plugin.config.test.adb.connection.title.part"), 0,
                gridRowIndex++, 2, 1);

        // ComboBox for selecting connected devices
        comboBoxDevices = new ComboBox<>();
        comboBoxDevices.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(comboBoxDevices, Priority.ALWAYS);

        // Button to refresh the list of connected devices
        buttonRefresh = FXControlUtils.createLeftTextButton(
                Translation.getText("phonecontrol2.plugin.config.button.refresh.devices"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.REFRESH).size(16).color(LCGraphicStyle.MAIN_DARK),
                null);

        // HBox to contain ComboBox and Refresh button side by side
        HBox hboxComboAndRefresh = new HBox(5, comboBoxDevices, buttonRefresh); // 5 is the spacing between elements
        hboxComboAndRefresh.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(comboBoxDevices, Priority.ALWAYS);

        gridPaneConfiguration.add(hboxComboAndRefresh, 0, gridRowIndex++, 2, 1);

        // Button to choose the selected device
        buttonChooseDevice = FXControlUtils.createLeftTextButton(
                Translation.getText("phonecontrol2.plugin.config.button.choose.device"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_DARK),
                null);

        gridPaneConfiguration.add(buttonChooseDevice, 0, gridRowIndex++, 2, 1);

        // Label to show the currently selected device
        labelConnectedDevice = new Label();
        labelConnectedDevice.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelConnectedDevice, Priority.ALWAYS);
        gridPaneConfiguration.add(labelConnectedDevice, 0, gridRowIndex++, 2, 1);

        gridPaneConfiguration.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(gridPaneConfiguration);

        // Initialize the ComboBox with an empty list
        comboBoxDevices.setItems(FXCollections.emptyObservableList());

        // Populate the ComboBox with connected devices
        updateConnectedDevices();
    }

    private void updateConnectedDevices() {
        // Get the list of connected devices
        ObservableList<String> connectedDevices = FXCollections.observableArrayList(
                PhoneControlController.getConnectedDevices());

        // Check if the list is empty
        if (connectedDevices.isEmpty()) {
            // If no devices are connected, add "Aucun appareil connecté" to the list
            connectedDevices.add("Aucun appareil connecté");
        }

        // Set the items in ComboBox
        comboBoxDevices.setItems(connectedDevices);

        // Automatically select the first device if available
        if (!connectedDevices.isEmpty()) {
            comboBoxDevices.getSelectionModel().selectFirst();
            String defaultDevice = connectedDevices.get(0);
            updateDeviceLabel(defaultDevice); // Update the label to show the default selected device
            // Inform the controller about the selected default device
            PhoneControlController.setSelectedDevice(defaultDevice);
        } else {
            // Update the label to show no device connected
            labelConnectedDevice.setText(
                    Translation.getText("phonecontrol2.plugin.config.label.no.device.connected"));
        }
    }

    @Override
    public void initListener() {
        // Mettre à jour l'étiquette lorsque le ComboBox sélectionne un nouvel appareil
        comboBoxDevices.setOnAction(e -> {
            String selectedDevice = comboBoxDevices.getSelectionModel().getSelectedItem();
            updateDeviceLabel(selectedDevice);
            // Mettre à jour le contrôleur avec l'appareil sélectionné
            PhoneControlController.setSelectedDevice(selectedDevice);
        });

        // Choisir l'appareil sélectionné et mettre à jour l'étiquette
        buttonChooseDevice.setOnAction(e -> {
            String selectedDevice = comboBoxDevices.getSelectionModel().getSelectedItem();
            if (selectedDevice != null && !selectedDevice.equals("Aucun appareil connecté")) {
                // Mettre à jour le contrôleur avec l'appareil sélectionné
                PhoneControlController.setSelectedDevice(selectedDevice);

                // Mettre à jour l'étiquette pour afficher l'appareil sélectionné
                updateDeviceLabel(selectedDevice);
            } else {
                labelConnectedDevice.setText(
                        Translation.getText("phonecontrol2.plugin.config.label.no.device.selected"));
            }
        });

        // Rafraîchir la liste des appareils connectés lorsque le bouton "Rafraîchir" est cliqué
        buttonRefresh.setOnAction(e -> updateConnectedDevices());
    }

    private void updateDeviceLabel(String device) {
        if (device != null && !device.equals("Aucun appareil connecté")) {
            labelConnectedDevice.setText(
                    Translation.getText("phonecontrol2.plugin.config.label.device.selected") + " " + device);
        } else {
            labelConnectedDevice.setText(
                    Translation.getText("phonecontrol2.plugin.config.label.no.device.connected"));
        }
    }

    private LCConfigurationI configuration;

    @Override
    public void saveChanges() {
        PhoneControlPluginProperties pluginConfigProperties = configuration
                .getPluginConfigProperties(PhoneControlPlugin.ID, PhoneControlPluginProperties.class);
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.configuration = model;
        PhoneControlPluginProperties pluginConfigProperties = configuration
                .getPluginConfigProperties(PhoneControlPlugin.ID, PhoneControlPluginProperties.class);
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.configuration = null;
    }

    @Override
    public boolean shouldCancelBeConfirmed() {
        return GeneralConfigurationStepViewI.super.shouldCancelBeConfirmed();
    }
}
