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

package org.lifecompanion.ui.configurationcomponent.base;

import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation that handle the common operation on StackComponent.<br>
 * Subclass must always call parent in overriden method.
 *
 * @param <T> the real stack component type displayed
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class StackComponentBaseImplView<T extends StackComponentI> extends Pane implements ComponentViewI<T>, LCViewInitHelper {
    protected ViewProviderI viewProvider;
    protected boolean useCache;

    /**
     * All the component that are into this stack
     */
    protected Map<GridPartComponentI, Region> componentsUI;

    /**
     * Displayed component
     */
    protected T model;

    private final ChangeListener<GridPartComponentI> displayedChangeListener;

    /**
     * Create the base for displaying stack component
     */
    protected StackComponentBaseImplView() {
        this.componentsUI = new HashMap<>();
        this.displayedChangeListener = (obs, oldv, newv) -> this.displayedChanged(oldv, newv);
    }

    @Override
    public void initBinding() {
        this.model.displayedComponentProperty().addListener(displayedChangeListener);
    }

    @Override
    public void initialize(ViewProviderI viewProvider, boolean useCache, final T componentP) {
        this.viewProvider = viewProvider;
        this.useCache = useCache;
        this.model = componentP;
        this.initAll();
        this.displayedChanged(null, this.model.displayedComponentProperty().get());
    }

    @Override
    public void unbindComponentAndChildren() {
        this.model.displayedComponentProperty().removeListener(displayedChangeListener);
        this.displayedChanged(model.displayedComponentProperty().get(), null);
        ConfigurationComponentUtils.exploreComponentViewChildrenToUnbind(this);
        this.componentsUI.values().forEach(ConfigurationComponentUtils::exploreComponentViewChildrenToUnbind);
        this.model = null;
    }

    /**
     * This method must be called to change the currently displayed component
     *
     * @param oldValueP the component that was displayed before
     * @param newValueP the new component that must be displayed now
     */
    protected void displayedChanged(final GridPartComponentI oldValueP, final GridPartComponentI newValueP) {
        //Remove previous
        if (oldValueP != null) {
            Region removed = this.componentsUI.get(oldValueP);
            FXThreadUtils.runOnFXThread(() -> this.getChildren().remove(removed));
        }
        //Check if new value is in map
        if (newValueP != null) {
            if (!this.componentsUI.containsKey(newValueP)) {
                Region added = newValueP.getDisplay(viewProvider, useCache).getView();
                this.componentsUI.put(newValueP, added);
            }
            //Display new
            FXThreadUtils.runOnFXThread(() -> this.getChildren().add(this.componentsUI.get(newValueP)));
        }
    }

    @Override
    public Region getView() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showToFront() {
        this.toFront();
    }
}
