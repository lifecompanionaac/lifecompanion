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
package org.lifecompanion.model.impl.categorizedelement.useaction;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionManagerI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.util.binding.BindingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Base use action trigger component.<br>
 * This component is use as a base implementation by every component that could fire use action.
 *
 * @author Mathieu THEBAUD
 */
public class SimpleUseActionManager implements UseActionManagerI {
    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleUseActionManager.class);

    /**
     * The parent that will binded to actions
     */
    private final UseActionTriggerComponentI realParent;

    /**
     * Every use actions on this component
     */
    private final Map<UseActionEvent, ObservableList<BaseUseActionI<?>>> actions;

    /**
     * Create the base component that will allow
     *
     * @param realParentP  the parent to bind to the added actions
     * @param allowedEvent the allowed event
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public SimpleUseActionManager(final UseActionTriggerComponentI realParentP, final UseActionEvent... allowedEvent) {
        this.actions = new HashMap<>();
        this.realParent = realParentP;
        //Create listener : set the real parent to added action, remove it from removed
        ListChangeListener<BaseUseActionI> actionListener = BindingUtils.createListChangeListener((added) -> {
            added.parentComponentProperty().set(this.realParent);
        }, (removed) -> {
            removed.parentComponentProperty().set(null);
        });
        //Add a list for each possible component
        for (UseActionEvent event : allowedEvent) {
            ObservableList<BaseUseActionI<?>> actionList = FXCollections.observableArrayList();
            actionList.addListener(actionListener);
            this.actions.put(event, actionList);
        }
    }

    @Override
    public Map<UseActionEvent, ObservableList<BaseUseActionI<?>>> componentActions() {
        return this.actions;
    }

    @Override
    public UseActionTriggerComponentI getActionParent() {
        return this.realParent;
    }

    @Override
    public void shiftActionUp(final UseActionEvent event, final BaseUseActionI<?> action) {
        ObservableList<BaseUseActionI<?>> list = this.actions.get(event);
        int index = list.indexOf(action);
        if (index > 0) {
            Collections.swap(list, index, index - 1);
        }
    }

    @Override
    public void shiftActionDown(final UseActionEvent event, final BaseUseActionI<?> action) {
        ObservableList<BaseUseActionI<?>> list = this.actions.get(event);
        int index = list.indexOf(action);
        if (index < list.size() - 1) {
            Collections.swap(list, index, index + 1);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getFirstActionOfType(final UseActionEvent eventType, final Class<T> actionType) {
        ObservableList<BaseUseActionI<?>> actionList = this.actions.get(eventType);
        if (actionList != null) {
            for (BaseUseActionI<?> useAction : actionList) {
                if (actionType.isAssignableFrom(useAction.getClass())) {
                    return (T) useAction;
                }
            }
        }
        return null;
    }

    // Class part : "Event handler"
    //========================================================================
    @Override
    public boolean hasSimpleAction(final UseActionEvent eventType) {
        ObservableList<BaseUseActionI<?>> eventActions = this.actions.get(eventType);
        if (eventActions != null) {
            for (BaseUseActionI<?> action : eventActions) {
                if (action.isSimple()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasComplexAction(final UseActionEvent eventType) {
        ObservableList<BaseUseActionI<?>> eventActions = this.actions.get(eventType);
        if (eventActions != null) {
            for (BaseUseActionI<?> action : eventActions) {
                if (!action.isSimple()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int countAllActions() {
        int count = 0;
        Set<UseActionEvent> keys = this.actions.keySet();
        for (UseActionEvent key : keys) {
            count += this.actions.get(key).size();
        }
        return count;
    }

    @Override
    public boolean containsActions() {
        return countAllActions() > 0;
    }

    @Override
    public void clear() {
        Set<UseActionEvent> keys = this.actions.keySet();
        for (UseActionEvent key : keys) {
            ObservableList<BaseUseActionI<?>> actionList = this.actions.get(key);
            actionList.clear();
        }
    }

    @Override
    public void dispatchIdsChanged(final Map<String, String> changes) {
        Set<UseActionEvent> keys = this.actions.keySet();
        for (UseActionEvent key : keys) {
            ObservableList<BaseUseActionI<?>> actionList = this.actions.get(key);
            for (BaseUseActionI<?> baseUseActionI : actionList) {
                baseUseActionI.idsChanged(changes);
            }
        }
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    public static final String NODE_USE_ACTION_MANAGER_OLD = "UseActionManager"; //FIXME
    public static final String NODE_USE_ACTION_MANAGER = "UAM";
    private static final String NODE_ACTIONS_EVENT_OLD = "UseActionsEvent";
    private static final String NODE_ACTIONS_EVENT = "UAE";
    private static final String NODE_ACTIONS = "Acts";
    private static final String ATB_EVENT_TYPE = "etyp", ATB_EVENT_TYPE_OLD = "eventType";

    @Override
    public Element serialize(final IOContextI contextP) {
        if (this.actions.values().stream().flatMap(Collection::stream).anyMatch(a -> !a.attachedToKeyOptionProperty().get())) {
            //This action manager
            Element xmlElement = new Element(SimpleUseActionManager.NODE_USE_ACTION_MANAGER);
            ConfigurationComponentIOHelper.addTypeAlias(this, xmlElement, contextP);
            //Create base for each action
            Element actionsEventNode = new Element(SimpleUseActionManager.NODE_ACTIONS_EVENT);
            xmlElement.addContent(actionsEventNode);
            //Add each action of each event type
            Set<UseActionEvent> keySet = this.actions.keySet();
            for (UseActionEvent event : keySet) {
                //Write all action for the event type (if there is actions)
                if (this.actions.get(event).stream().anyMatch(a -> !a.attachedToKeyOptionProperty().get())) {
                    Element actionsNode = new Element(SimpleUseActionManager.NODE_ACTIONS);
                    XMLUtils.write(event, SimpleUseActionManager.ATB_EVENT_TYPE, actionsNode);
                    actionsEventNode.addContent(actionsNode);
                    ObservableList<BaseUseActionI<?>> eventActions = this.actions.get(event);
                    for (BaseUseActionI<?> baseUseActionI : eventActions) {
                        //Don't save action related to key options
                        if (!baseUseActionI.attachedToKeyOptionProperty().get()) {
                            actionsNode.addContent(baseUseActionI.serialize(contextP));
                        }
                    }
                }
            }
            return xmlElement;
        } else
            return null;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        //Get event
        Element actionsEventNodeOld = nodeP.getChild(SimpleUseActionManager.NODE_ACTIONS_EVENT_OLD);
        Element actionsEventNode = nodeP.getChild(SimpleUseActionManager.NODE_ACTIONS_EVENT);
        Element eventNodes = actionsEventNode != null ? actionsEventNode : actionsEventNodeOld;
        List<Element> eventNodesChildren = eventNodes.getChildren();
        //Get all possible event type
        for (Element eventNode : eventNodesChildren) {
            Enum<UseActionEvent> eventType = XMLUtils.readEnum(UseActionEvent.class, eventNode.getAttribute(ATB_EVENT_TYPE) != null ? ATB_EVENT_TYPE : ATB_EVENT_TYPE_OLD, eventNode);
            ObservableList<BaseUseActionI<?>> actionList = this.actions.get(eventType);
            //For each event, get all action registered on the event
            List<Element> eventNodeChildren = eventNode.getChildren();
            for (Element actionNode : eventNodeChildren) {
                try {
                    //Read the action and add it
                    Pair<Boolean, XMLSerializable<IOContextI>> useActionResult = ConfigurationComponentIOHelper.create(actionNode, contextP, null);
                    if (!useActionResult.getLeft()) {
                        BaseUseActionI<?> useAction = (BaseUseActionI<?>) useActionResult.getRight();
                        useAction.deserialize(actionNode, contextP);
                        actionList.add(useAction);
                    }
                } catch (Throwable t) {
                    SimpleUseActionManager.LOGGER.warn("Couldn't load the use action from a action node {}", actionNode, t);
                }
            }
        }
    }
    //========================================================================

    // Class part : "Use information serializable"
    //========================================================================
    @Override
    public void serializeUseInformation(final Map<String, Element> elements) {
        Set<UseActionEvent> keys = this.actions.keySet();
        for (UseActionEvent useActionEvent : keys) {
            ObservableList<BaseUseActionI<?>> actionList = this.actions.get(useActionEvent);
            for (BaseUseActionI<?> action : actionList) {
                action.serializeUseInformation(elements);
            }
        }
    }

    @Override
    public void deserializeUseInformation(final Map<String, Element> elements) throws LCException {
        Set<UseActionEvent> keys = this.actions.keySet();
        for (UseActionEvent useActionEvent : keys) {
            ObservableList<BaseUseActionI<?>> actionList = this.actions.get(useActionEvent);
            for (BaseUseActionI<?> action : actionList) {
                action.deserializeUseInformation(elements);
            }
        }
    }
    //========================================================================

}
