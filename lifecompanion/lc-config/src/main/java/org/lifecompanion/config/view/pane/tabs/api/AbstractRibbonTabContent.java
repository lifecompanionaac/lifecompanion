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
package org.lifecompanion.config.view.pane.tabs.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;

/**
 * A ribbon pane is a pane that can contains multiple content.<br>
 * Content can be {@link RibbonBasePart} but also any node.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractRibbonTabContent extends AbstractTabContent implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRibbonTabContent.class);

    private List<RibbonTabPart> items;

    private HBox boxContent;

    protected AbstractRibbonTabContent(final RibbonTabPart... itemsP) {
        this.items = new ArrayList<>();
        for (RibbonTabPart item : itemsP) {
            this.items.add(item);
        }
        this.initAll();
    }

    private static Node getInstanceFor(final RibbonTabPart item) {
        if (item.isUniqueNode()) {
            return item.getUniqueNodeInstance();
        } else {
            Class<? extends Node> partType = item.getCommonNodeClass();
            AbstractRibbonTabContent.LOGGER.debug("Request selection part instance : {}", partType.getSimpleName());
            try {
                AbstractRibbonTabContent.LOGGER.debug("Will try to create a new instance of {}", partType.getSimpleName());
                Node instance = partType.getDeclaredConstructor().newInstance();
                item.setUniqueNodeInstance(instance);
                return instance;
            } catch (Exception e) {
                AbstractRibbonTabContent.LOGGER.warn("Couldn't create instance of {}", partType.getSimpleName(), e);
                return null;
            }
        }
    }

    @Override
    public void initUI() {
        this.boxContent = new HBox();
        this.setCenter(this.boxContent);
        for (RibbonTabPart item : this.items) {
            this.addPart(item);
        }
    }

    protected void addPart(final RibbonTabPart item) {
        Separator sep = this.addPart(AbstractRibbonTabContent.getInstanceFor(item));
        item.setAssociatedSeparator(sep);
    }

    protected void removePart(final RibbonTabPart item) {
        Node partInstance = item.getUniqueNodeInstance();
        this.boxContent.getChildren().remove(partInstance);
        if (item.getAssociatedSeparator() != null) {
            this.boxContent.getChildren().remove(item.getAssociatedSeparator());
        }
    }

    // Class part : "Add part to ribbon"
    //========================================================================

    /**
     * Create a new part for the given part content and add it to this ribbon children.
     *
     * @param partContent the content of the part to add
     * @return the separator added to the node when one exist
     */
    private Separator addPart(final Node content) {
        Separator separator = null;
        //Check separator
        if (!this.boxContent.getChildren().isEmpty()) {
            separator = new Separator(Orientation.VERTICAL);
            HBox.setMargin(separator, new Insets(2, 4, 2, 4));
            separator.visibleProperty().bind(content.visibleProperty());
            separator.managedProperty().bind(content.managedProperty());
            this.boxContent.getChildren().add(separator);
        } else {
            HBox.setMargin(content, new Insets(0, 0, 0, 10));
        }
        //Add
        this.boxContent.getChildren().add(content);
        return separator;
    }
    //========================================================================

    // Class part : "Item class"
    //========================================================================
    public static class RibbonTabPart {
        private Class<? extends Node> commonNodeClass;
        private Node uniqueNodeInstance;

        private Separator associatedSeparator;

        private RibbonTabPart(final Class<? extends Node> commonNodeClass) {
            this.commonNodeClass = commonNodeClass;
        }

        private RibbonTabPart(final Node uniqueNodeInstance) {
            this.uniqueNodeInstance = uniqueNodeInstance;
        }

        public static RibbonTabPart create(final Node uniqueNodeInstance) {
            return new RibbonTabPart(uniqueNodeInstance);
        }

        public static RibbonTabPart create(final Class<? extends Node> commonNodeClass) {
            return new RibbonTabPart(commonNodeClass);
        }

        public void setUniqueNodeInstance(final Node uniqueNodeInstance) {
            this.uniqueNodeInstance = uniqueNodeInstance;
        }

        public Class<? extends Node> getCommonNodeClass() {
            return this.commonNodeClass;
        }

        public Node getUniqueNodeInstance() {
            return this.uniqueNodeInstance;
        }

        public boolean isUniqueNode() {
            return this.uniqueNodeInstance != null;
        }

        public Separator getAssociatedSeparator() {
            return this.associatedSeparator;
        }

        public void setAssociatedSeparator(final Separator associatedSeparator) {
            this.associatedSeparator = associatedSeparator;
        }
    }
    //========================================================================
}
