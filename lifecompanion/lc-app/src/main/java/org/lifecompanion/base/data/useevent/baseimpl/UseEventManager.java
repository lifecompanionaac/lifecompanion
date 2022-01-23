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
package org.lifecompanion.base.data.useevent.baseimpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.useevent.UseEventGeneratorHolderI;
import org.lifecompanion.api.component.definition.useevent.UseEventGeneratorI;
import org.lifecompanion.api.component.definition.useevent.UseEventListenerI;
import org.lifecompanion.api.component.definition.useevent.UseEventManagerI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.framework.utils.Pair;

import java.util.List;
import java.util.Map;

/**
 * Use event manager implementation.<br>
 * Hold the use event generator list.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseEventManager implements UseEventManagerI {

    private ObservableList<UseEventGeneratorI> eventGenerators;
    protected UseEventGeneratorHolderI parent;

    public UseEventManager(final UseEventGeneratorHolderI parentP) {
        this.parent = parentP;
        this.eventGenerators = FXCollections.observableArrayList();
        this.eventGenerators.addListener(LCUtils.createListChangeListener((add) -> {
            add.configurationParentProperty().bind(parentP.configurationParentProperty());
        }, (removed) -> {
            removed.configurationParentProperty().unbind();
            removed.configurationParentProperty().set(null);
        }));
    }

    // Class part : "Public API"
    //========================================================================
    @Override
    public ObservableList<UseEventGeneratorI> componentEventGenerators() {
        return this.eventGenerators;
    }

    @Override
    public void attachAndStart(final UseEventListenerI listener, final LCConfigurationI configuration) {
        for (UseEventGeneratorI useEventGen : this.eventGenerators) {
            useEventGen.attachListener(listener);
            useEventGen.modeStart(configuration);
        }
    }

    @Override
    public void detachAndStop(final LCConfigurationI configuration) {
        for (UseEventGeneratorI useEventGen : this.eventGenerators) {
            useEventGen.detachListener();
            useEventGen.modeStop(configuration);
        }
    }

    //========================================================================

    // Class part : "IO"
    //========================================================================
    public static final String NODE_USE_EVENT_MANAGER = "UseEventManager";
    private static final String NODE_EVENT_GENERATORS = "EventGenerators";

    @Override
    public Element serialize(final IOContextI context) {
        //Base
        Element element = new Element(UseEventManager.NODE_USE_EVENT_MANAGER);
        IOManager.addTypeAlias(this, element, context);
        //Generator list
        Element generatorsElement = new Element(UseEventManager.NODE_EVENT_GENERATORS);
        element.addContent(generatorsElement);
        for (UseEventGeneratorI useEventGeneratorI : this.eventGenerators) {
            generatorsElement.addContent(useEventGeneratorI.serialize(context));
        }
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        //Load each generator
        Element generatorsElement = node.getChild(UseEventManager.NODE_EVENT_GENERATORS);
        List<Element> generatorsChildren = generatorsElement.getChildren();
        for (Element generatorElement : generatorsChildren) {
            Pair<Boolean, XMLSerializable<IOContextI>> generatorResult = IOManager.create(generatorElement, context, null);
            if (!generatorResult.getLeft()) {
                UseEventGeneratorI generator = (UseEventGeneratorI) generatorResult.getRight();
                generator.deserialize(generatorElement, context);
                eventGenerators.add(generator);
            }
        }
    }
    //========================================================================

    // Class part : "Use info IO"
    //========================================================================
    @Override
    public void serializeUseInformation(final Map<String, Element> elements) {
        for (UseEventGeneratorI eventGenerator : this.eventGenerators) {
            eventGenerator.serializeUseInformation(elements);
        }
    }

    @Override
    public void deserializeUseInformation(final Map<String, Element> elements) throws LCException {
        for (UseEventGeneratorI eventGenerator : this.eventGenerators) {
            eventGenerator.deserializeUseInformation(elements);
        }
    }

    @Override
    public void dispatchIdsChanged(final Map<String, String> changes) {
        for (UseEventGeneratorI eventGenerator : this.eventGenerators) {
            eventGenerator.idsChanged(changes);
        }
    }
    //========================================================================

}
