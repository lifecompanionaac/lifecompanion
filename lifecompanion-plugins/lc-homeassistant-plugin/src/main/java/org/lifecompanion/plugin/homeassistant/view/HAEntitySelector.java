package org.lifecompanion.plugin.homeassistant.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.homeassistant.HomeAssistantPluginService;
import org.lifecompanion.plugin.homeassistant.model.HAEntity;
import org.lifecompanion.plugin.homeassistant.view.listcell.HAEntityListCell;
import org.lifecompanion.plugin.homeassistant.view.listcell.HAEntitySimpleListCell;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.FXControlUtils;

public class HAEntitySelector extends HBox implements LCViewInitHelper {
    private ComboBox<HAEntity> comboboxEntity;
    private final StringProperty value;
    private Button buttonRefresh;

    public HAEntitySelector() {
        value = new SimpleStringProperty();
        initAll();
    }

    @Override
    public void initUI() {
        comboboxEntity = new ComboBox<>();
        comboboxEntity.setCellFactory(lv -> new HAEntityListCell());
        comboboxEntity.setButtonCell(new HAEntitySimpleListCell());
        HBox.setHgrow(comboboxEntity, Priority.ALWAYS);
        comboboxEntity.setMinWidth(120.0);
        buttonRefresh = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.REFRESH).size(12).color(LCGraphicStyle.MAIN_PRIMARY), null);
        this.getChildren().addAll(comboboxEntity, buttonRefresh);
        this.setAlignment(Pos.CENTER);
    }

    public StringProperty valueProperty() {
        return value;
    }

    @Override
    public void initListener() {
        this.buttonRefresh.setOnAction(e -> refresh(value.get(), null));
    }

    @Override
    public void initBinding() {
        value.addListener((obs, ov, nv) -> selectItemForId(nv));
        this.comboboxEntity.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> value.set(nv != null ? nv.getId() : null));
    }

    private void selectItemForId(String nv) {
        this.comboboxEntity.getItems().stream().filter(e -> e.getId().equals(nv)).findAny().ifPresentOrElse(comboboxEntity.getSelectionModel()::select, () -> comboboxEntity.getSelectionModel().clearSelection());
    }

    public void refresh(String entityIdToSelect, Runnable callback) {
        if (!buttonRefresh.isDisabled()) {
            comboboxEntity.setItems(FXCollections.observableArrayList());
            HomeAssistantPluginService.GetEntitiesTask task = HomeAssistantPluginService.INSTANCE.getEntities();
            task.setOnSucceeded(entities -> {
                comboboxEntity.setItems(FXCollections.observableArrayList(task.getValue()));
                selectItemForId(entityIdToSelect);
                if (callback != null) callback.run();
            });
            buttonRefresh.disableProperty().bind(task.runningProperty());
            comboboxEntity.disableProperty().bind(task.runningProperty());
            AsyncExecutorController.INSTANCE.addAndExecute(true, true, task);
        }
    }

}
