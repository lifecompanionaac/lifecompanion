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
package org.lifecompanion.model.impl.profile;

import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.profile.UserCompI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserCompImpl implements UserCompI {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserCompImpl.class);

    /**
     * Component loaded : this component can be null if it's not loaded yet.
     */
    private DisplayableComponentI loadedComponent;

    // Class part : "API"
    //========================================================================
    void setLoadedComponent(final DisplayableComponentI loadedComponent) {
        this.loadedComponent = loadedComponent;
    }

    @Override
    public DisplayableComponentI getLoadedComponent() {
        return this.loadedComponent;
    }//FIXME : delete this

    @Override
    public boolean isLoaded() {
        return this.loadedComponent != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DisplayableComponentI> T createNewComponent() {
        return (T) this.loadedComponent.duplicate(true);
    }

    @Override
    public void unloadComponent() {
        this.loadedComponent = null;
    }
    //========================================================================

    // Class part : "IO"
    //========================================================================
    private static final String NODE_USER_COMP = "UserComp", NODE_COMP = "ComponentNode";

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element(UserCompImpl.NODE_USER_COMP);
        //Add this element and serialize it
        Element containerElem = new Element(UserCompImpl.NODE_COMP);
        element.addContent(containerElem);
        containerElem.addContent(this.loadedComponent.serialize(context));
        //Dependencies (plugin and styles)
        ConfigurationComponentIOHelper.serializeComponentDependencies(context, this.loadedComponent, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        // Dependencies (styles and plugins)
        ConfigurationComponentIOHelper.deserializeComponentDependencies(context, this.loadedComponent, node);//FIXME : loaded component will always be null here
        //Get the element
        Element childComp = node.getChild(UserCompImpl.NODE_COMP);
        if (!childComp.getChildren().isEmpty()) {
            Element compElem = childComp.getChildren().get(0);

            // Here, we want the loading to fail if the base component cannot be read (but then, on loading for child component, we restore the default value)
            boolean previousFallbackOnDefaultInstanceOnFail = context.isFallbackOnDefaultInstanceOnFail();
            context.setFallbackOnDefaultInstanceOnFail(false);
            this.loadedComponent = (DisplayableComponentI) ConfigurationComponentIOHelper.create(compElem, context, null).getRight();
            // Load children
            context.setFallbackOnDefaultInstanceOnFail(previousFallbackOnDefaultInstanceOnFail);
            this.loadedComponent.deserialize(compElem, context);
        } else {
            UserCompImpl.LOGGER.warn("No component found for this component");
        }
    }

    static Element getElementFromRootXml(Element node) {
        Element childComp = node.getChild(UserCompImpl.NODE_COMP);
        if (!childComp.getChildren().isEmpty()) {
            return childComp.getChildren().get(0);
        } else return null;
    }
    //========================================================================

}
