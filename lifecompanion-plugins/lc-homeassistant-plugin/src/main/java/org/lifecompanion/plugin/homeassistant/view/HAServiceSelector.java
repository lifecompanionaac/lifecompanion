package org.lifecompanion.plugin.homeassistant.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.plugin.homeassistant.HomeAssistantPluginService;
import org.lifecompanion.plugin.homeassistant.model.HAEntity;
import org.lifecompanion.plugin.homeassistant.model.HAService;
import org.lifecompanion.plugin.homeassistant.view.listcell.HAServiceListCell;
import org.lifecompanion.plugin.homeassistant.view.listcell.HAServiceSimpleListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Collectors;

public class HAServiceSelector extends HBox implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HAServiceSelector.class);

    private ComboBox<HAService> comboboxService;
    private final StringProperty value;

    public HAServiceSelector() {
        value = new SimpleStringProperty();
        initAll();
    }

    @Override
    public void initUI() {
        comboboxService = new ComboBox<>();
        comboboxService.setCellFactory(lv -> new HAServiceListCell());
        comboboxService.setButtonCell(new HAServiceSimpleListCell());
        HBox.setHgrow(comboboxService, Priority.ALWAYS);
        comboboxService.setMinWidth(120.0);
        this.getChildren().addAll(comboboxService);
        this.setAlignment(Pos.CENTER);
    }

    @Override
    public void initListener() {
    }

    public StringProperty valueProperty() {
        return value;
    }

    @Override
    public void initBinding() {
        value.addListener((obs, ov, nv) -> this.comboboxService.getItems().stream().filter(e -> e.getServiceId().equals(nv)).findAny().ifPresentOrElse(comboboxService.getSelectionModel()::select, () -> comboboxService.getSelectionModel().clearSelection()));
        this.comboboxService.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> value.set(nv != null ? nv.getServiceId() : null));
    }

    public void updateForEntity(String entityId) {
        try {
            comboboxService.setItems(FXCollections.observableArrayList(HomeAssistantPluginService.INSTANCE.getServices().stream().filter(s ->
                    s.getDomainId().equals(HAEntity.getDomainFromEntityId(entityId))
            ).collect(Collectors.toList())));
        } catch (IOException e) {
            LOGGER.error("Error when getting services", e);
        }
    }
}
