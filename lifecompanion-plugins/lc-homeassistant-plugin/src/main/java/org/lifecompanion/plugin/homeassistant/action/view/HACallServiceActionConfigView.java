package org.lifecompanion.plugin.homeassistant.action.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.homeassistant.action.HACallServiceAction;
import org.lifecompanion.plugin.homeassistant.action.category.HAActionSubCategories;
import org.lifecompanion.plugin.homeassistant.view.HAEntitySelector;
import org.lifecompanion.plugin.homeassistant.view.HAServiceSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HACallServiceActionConfigView extends GridPane implements UseActionConfigurationViewI<HACallServiceAction> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HACallServiceActionConfigView.class);

    private HAServiceSelector serviceSelector;
    private HAEntitySelector entitySelector;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<HACallServiceAction> getConfiguredActionType() {
        return HACallServiceAction.class;
    }

    @Override
    public void initUI() {
        entitySelector = new HAEntitySelector();
        GridPane.setHalignment(entitySelector, HPos.LEFT);
        serviceSelector = new HAServiceSelector();
        GridPane.setHalignment(serviceSelector, HPos.LEFT);

        Label labelEntity = new Label(Translation.getText("ha.plugin.field.entity.id"));
        Label labelService = new Label(Translation.getText("ha.plugin.field.service.id"));

        int rowIndex = 0;
        this.add(labelEntity, 0, rowIndex);
        this.add(entitySelector, 1, rowIndex++);
        this.add(labelService, 0, rowIndex);
        this.add(serviceSelector, 1, rowIndex++);
        this.setHgap(10);
        this.setVgap(5);
    }

    @Override
    public void initListener() {
        UseActionConfigurationViewI.super.initListener();
        this.entitySelector.valueProperty().addListener((obs, ov, nv) -> {
            this.serviceSelector.updateForEntity(nv);
        });
    }

    @Override
    public void initBinding() {
        UseActionConfigurationViewI.super.initBinding();
    }

    @Override
    public void editStarts(HACallServiceAction element, ObservableList<UseVariableDefinitionI> possibleVariables) {
        entitySelector.refresh(element.entityIdProperty().get(), () -> serviceSelector.valueProperty().set(element.serviceIdProperty().get()));
    }

    @Override
    public void editEnds(HACallServiceAction element) {
        element.entityIdProperty().set(entitySelector.valueProperty().get());
        element.serviceIdProperty().set(serviceSelector.valueProperty().get());
        entitySelector.valueProperty().set(null);
        serviceSelector.valueProperty().set(null);
    }
}
