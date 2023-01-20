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
package org.lifecompanion.plugin.homeassistant.event;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.jetbrains.annotations.Nullable;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.homeassistant.HomeAssistantPluginService;
import org.lifecompanion.plugin.homeassistant.event.category.HAEventSubCategory;

import java.util.function.Consumer;

public class OnItemStateChangedEventGenerator extends BaseUseEventGeneratorImpl {

    private final Consumer<String> valueUpdatedCallback;
    private final StringProperty entityId;
    private final StringProperty valueFilter;

    public OnItemStateChangedEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 20;
        this.category = HAEventSubCategory.ALL;
        this.nameID = "ha.plugin.event.item.state.changed.name";
        this.staticDescriptionID = "ha.plugin.event.item.state.changed.descripion";
        this.configIconPath = "icon_event_state_updated.png";
        this.entityId = new SimpleStringProperty();
        this.valueFilter = new SimpleStringProperty();
        valueUpdatedCallback = value -> {
            if (StringUtils.isBlank(valueFilter.get()) || cleanValue(valueFilter.get()).equals(cleanValue(value))) {
                this.useEventListener.fireEvent(this, null, null);
            }
        };
    }

    private String cleanValue(String v) {
        return StringUtils.toLowerCase(StringUtils.trimToEmpty(v));
    }


    public StringProperty entityIdProperty() {
        return entityId;
    }

    public StringProperty valueFilterProperty() {
        return valueFilter;
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        if (StringUtils.isNotBlank(entityId.get())) {
            HomeAssistantPluginService.INSTANCE.registerStateListener(entityId.get(), this.valueUpdatedCallback);
        }
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        HomeAssistantPluginService.INSTANCE.removeStateListener(entityId.get(), this.valueUpdatedCallback);
    }

    @Override
    public Element serialize(final IOContextI context) {
        return XMLObjectSerializer.serializeInto(OnItemStateChangedEventGenerator.class, this, super.serialize(context));
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(OnItemStateChangedEventGenerator.class, this, node);
    }
}
