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
import org.lifecompanion.plugin.phonecontrol.PhoneCommunicationManager;
import org.lifecompanion.plugin.phonecontrol.PhoneCommunicationManager.ProtocolType;
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

    // Select device
    private ComboBox<Device> selectDeviceComboBox;
    private Button refreshDeviceListButton;
    private ProgressIndicator progressIndicatorRefresh;

    // Settings
    private ToggleSwitch speakerToggleButton;  // Determine if the speaker is activated or not in a call
    private DurationPickerControl durationIntervalPicker;  // The refresh interval

    // Install App
    private Button installAppButton;
    private ProgressIndicator installingProgressIndicator;
    private Label labelInstallResult;

    // Communication Protocol
    private ComboBox<PhoneCommunicationManager.ProtocolType> protocolSelectionComboBox;

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
    public void initUI() {
        // Device selection
        selectDeviceComboBox = new ComboBox<Device>();
        refreshDeviceListButton = new Button(Translation.getText("phonecontrol.plugin.config.button.refresh.list"));
        refreshDeviceListButton.setAlignment(Pos.CENTER);
        progressIndicatorRefresh = new ProgressIndicator(-1);
        progressIndicatorRefresh.setPrefSize(30, 30);
        VBox paneProgressRefresh = new VBox(progressIndicatorRefresh);
        paneProgressRefresh.setAlignment(Pos.CENTER);

        HBox.setHgrow(refreshDeviceListButton, Priority.ALWAYS);
        HBox boxDeviceSelection = new HBox(10.0, selectDeviceComboBox, refreshDeviceListButton, paneProgressRefresh);
        boxDeviceSelection.setAlignment(Pos.CENTER_LEFT);

        // Speaker toggle switch
        speakerToggleButton = new ToggleSwitch(Translation.getText("phonecontrol.plugin.config.label.speaker.on"));
        speakerToggleButton.setSelected(true);

        // Duration picker
        this.durationIntervalPicker = new DurationPickerControl();
        Label labelDurationIntervalPicker = new Label(Translation.getText("phonecontrol.plugin.config.label.duration"));
        labelDurationIntervalPicker.setMaxWidth(Double.MAX_VALUE);
        HBox durationRow = new HBox(10.0, labelDurationIntervalPicker, durationIntervalPicker);

        // Install app
        installingProgressIndicator = new ProgressIndicator(-1);
        installingProgressIndicator.setPrefSize(30, 30);
        VBox paneProgressInstalling = new VBox(installingProgressIndicator);
        paneProgressInstalling.setAlignment(Pos.CENTER);

        installAppButton = new Button(Translation.getText("phonecontrol.plugin.config.button.install.app"));
        installAppButton.setAlignment(Pos.CENTER);

        labelInstallResult = new Label();
        labelInstallResult.setAlignment(Pos.CENTER);
        HBox.setHgrow(labelInstallResult, Priority.ALWAYS);

        HBox boxInstalling = new HBox(10.0, installAppButton, paneProgressInstalling, labelInstallResult);
        boxInstalling.setAlignment(Pos.CENTER_LEFT);

        // Communication protocol selection
        protocolSelectionComboBox = new ComboBox<>();
        protocolSelectionComboBox.getItems().addAll(ProtocolType.ADB, ProtocolType.BLUETOOTH);
        protocolSelectionComboBox.setValue(PhoneCommunicationManager.INSTANCE.getCurrentProtocolType());
        protocolSelectionComboBox.setOnAction(event -> onProtocolSelectionChanged());

        HBox protocolSelectionBox = new HBox(10, new Label(Translation.getText("phonecontrol.plugin.config.label.protocol.selection")), protocolSelectionComboBox);
        protocolSelectionBox.setAlignment(Pos.CENTER_LEFT);
        protocolSelectionBox.setPadding(new Insets(10));
        HBox.setHgrow(protocolSelectionComboBox, Priority.ALWAYS);

        // Main pane
        VBox vboxTotal = new VBox(5.0,
            FXControlUtils.createTitleLabel(Translation.getText("phonecontrol.plugin.config.category.device.selection.title")),
            new Label(Translation.getText("phonecontrol.plugin.config.label.device.selection")),
            boxDeviceSelection,
            speakerToggleButton,
            durationRow,
            FXControlUtils.createTitleLabel(Translation.getText("phonecontrol.plugin.config.category.protocol.selection.title")),
            protocolSelectionBox,
            FXControlUtils.createTitleLabel(Translation.getText("phonecontrol.plugin.config.category.install.app.title")),
            new Label(Translation.getText("phonecontrol.plugin.config.label.install.app")),
            boxInstalling
        );

        vboxTotal.setPadding(new Insets(5.0));
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        ScrollPane scrollPane = new ScrollPane(vboxTotal);
        scrollPane.setFitToWidth(true);

        this.setCenter(scrollPane);
    }

    private void onProtocolSelectionChanged() {
        ProtocolType selectedProtocol = protocolSelectionComboBox.getValue();
        if (selectedProtocol != null) {
            PhoneCommunicationManager.INSTANCE.setProtocolType(selectedProtocol);
        }
    }

    @Override
    public void initListener() {
        this.refreshDeviceListButton.setOnAction(event -> {
            refreshDeviceListButton.setDisable(true);
            progressIndicatorRefresh.setProgress(-1);
            progressIndicatorRefresh.setVisible(true);
            saveChanges();

            Thread refreshThread = new Thread(() -> {
                try {
                    Platform.runLater(() -> {
                        refreshDeviceList();
                    });
                } finally {
                    Platform.runLater(() -> {
                        refreshDeviceListButton.setDisable(false);
                        progressIndicatorRefresh.setProgress(1.0);
                    });
                }
            });
            refreshThread.setDaemon(true);
            refreshThread.start();
        });

        this.installAppButton.setOnAction(event -> {
            installAppButton.setDisable(true);
            installingProgressIndicator.setProgress(-1);
            installingProgressIndicator.setVisible(true);
            labelInstallResult.setVisible(false);
            saveChanges();

            Thread installThread = new Thread(() -> {
                String deviceSerialNumber = selectDeviceComboBox.getSelectionModel().getSelectedItem().getSerialNumber();
                if (deviceSerialNumber != null) {
                    boolean isInstalled = PhoneControlController.INSTANCE.installApp(deviceSerialNumber);
                    Platform.runLater(() -> {
                        labelInstallResult.setText(Translation.getText(
                            isInstalled ? "phonecontrol.plugin.config.label.install.app.success": "phonecontrol.plugin.config.label.install.app.error"
                        ));
                    });
                } else {
                    Platform.runLater(() -> {
                        labelInstallResult.setText(Translation.getText("phonecontrol.plugin.config.label.no.device"));
                    });
                }

                Platform.runLater(() -> {
                    installAppButton.setDisable(false);
                    installingProgressIndicator.setProgress(1.0);
                    labelInstallResult.setVisible(true);
                });
            });
            installThread.setDaemon(true);
            installThread.start();
        });
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        progressIndicatorRefresh.setVisible(false);
        installingProgressIndicator.setVisible(false);
        labelInstallResult.setVisible(false);
    }

    @Override
    public void saveChanges() {
        PhoneControlPluginProperties phoneControlPluginProperties = configuration.getPluginConfigProperties(PhoneControlPlugin.PLUGIN_ID, PhoneControlPluginProperties.class);
        phoneControlPluginProperties.deviceProperty().set(selectDeviceComboBox.getSelectionModel().getSelectedItem().getSerialNumber());
        phoneControlPluginProperties.speakerOnProperty().set(speakerToggleButton.isSelected());
        phoneControlPluginProperties.durationInternalProperty().set(durationIntervalPicker.durationProperty().get());
    }

    @Override
    public void cancelChanges() { }

    @Override
    public void bind(LCConfigurationI model) {
        this.configuration = model;
        PhoneControlPluginProperties phoneControlPluginProperties = configuration.getPluginConfigProperties(PhoneControlPlugin.PLUGIN_ID, PhoneControlPluginProperties.class);
        this.speakerToggleButton.setSelected(phoneControlPluginProperties.speakerOnProperty().get());
        this.durationIntervalPicker.durationProperty().set(phoneControlPluginProperties.durationInternalProperty().get());
        refreshDeviceList();
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.configuration = null;
    }

    /**
     * Refresh the device list (ComboBox) :
     * - Get the list of devices
     * - Get the name of each device
     * - Add the device to the ComboBox
     * - Select the device that was previously selected ( set in the configuration )
     * - If the device was not found, add a "selected device not found" device with the serial number save in the configuration
     * - Add il all situation a "no device selected" device with a null serial number
     */
    private void refreshDeviceList() {
        ArrayList<String> devicesList = ADBService.INSTANCE.getDevices();
        selectDeviceComboBox.getItems().clear();

        if (!devicesList.isEmpty()) {
            ObservableList<Device> observableDevicesList = FXCollections.observableArrayList();

            // Add all devices with its name to the ComboBox
            for (String deviceSerialNumber : devicesList) {
                String deviceName = ADBService.INSTANCE.getDeviceName(deviceSerialNumber);
                observableDevicesList.add(new Device(deviceName, deviceSerialNumber));
            }

            selectDeviceComboBox.getItems().setAll(observableDevicesList);
        }

        // Add a "no device selected" device
        Device noDeviceSelected = new Device(Translation.getText("phonecontrol.plugin.config.label.no.device"), null);
        selectDeviceComboBox.getItems().add(noDeviceSelected);

        // Device selection / Search by serial number
        PhoneControlPluginProperties phoneControlPluginProperties = configuration.getPluginConfigProperties(PhoneControlPlugin.PLUGIN_ID, PhoneControlPluginProperties.class);
        String deviceSerialNumber = phoneControlPluginProperties.deviceProperty().get();
        if (deviceSerialNumber == null) {  // If no device was selected
            selectDeviceComboBox.getSelectionModel().select(noDeviceSelected);
        } else {  // If a device was selected, we try to find it in the list
            Device selectedDevice = null;

            for (Device device : selectDeviceComboBox.getItems()) {
                // If the device is found, we save it in variable
                if (device.getSerialNumber() != null && device.getSerialNumber().equals(deviceSerialNumber)) {
                    selectedDevice = device;

                    break;
                }
            }

            if (selectedDevice != null) {  // If the device was found, we select it
                selectDeviceComboBox.getSelectionModel().select(selectedDevice);
            } else {  // If the device was not found, we add a "selected device not found" device
                selectDeviceComboBox.getItems().add(new Device(
                    Translation.getText("phonecontrol.plugin.config.label.device.unfound"), deviceSerialNumber
                ));
                selectDeviceComboBox.getSelectionModel().selectLast();
            }
        }
    }

    /**
     * Internal class to represent a device
     * Each device has a name and a serial number
     * This class allow to display a name different from the serial number in the ComboBox
     */
    public class Device {
        private String name;
        private String serialNumber;

        public Device(String name, String serialNumber) {
            this.name = name;
            this.serialNumber = serialNumber;
        }

        public String getName() {
            return name;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
