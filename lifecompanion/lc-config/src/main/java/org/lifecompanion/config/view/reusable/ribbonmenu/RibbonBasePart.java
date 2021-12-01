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

package org.lifecompanion.config.view.reusable.ribbonmenu;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.view.reusable.impl.BaseConfigurationViewBorderPane;

import java.util.function.Function;

/**
 * Base class for a ribbon part.<br>
 * A ribbon part contains a content and a title.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class RibbonBasePart<T> extends BaseConfigurationViewBorderPane<T> {
    private final Label labelTitle;

    /**
     * Create the new ribbon base
     */
    public RibbonBasePart(final String title, final Node partContent) {
        this();
        this.setTitle(title);
        this.setContent(partContent);
    }

    /**
     * Create a new empty ribbon base part.
     */
    public RibbonBasePart() {
        //Label
        this.labelTitle = new Label();
        this.labelTitle.getStyleClass().add("ribbon-part-title");
        BorderPane.setAlignment(this.labelTitle, Pos.CENTER);
        BorderPane.setMargin(this.labelTitle, new Insets(6, 0, 0, 0));
        this.setBottom(this.labelTitle);
    }

    /**
     * Change the content of this ribbon part
     *
     * @param contentP the new content for this ribbon part
     */
    protected void setContent(final Node contentP) {
        BorderPane.setAlignment(contentP, Pos.CENTER);
        this.setCenter(contentP);
    }

    /**
     * @return the content into this ribbon part, or null if there is no content
     */
    protected Node getContent() {
        return this.getCenter();
    }

    /**
     * Change the title of this ribbon part
     *
     * @param title the new title for this ribbon part
     */
    protected void setTitle(final String title) {
        this.labelTitle.setText(title);
    }

    protected void initVisibleAndManagedBinding(Function<T, Boolean> visibleAndManagedFct) {
        final BooleanBinding visibleAndManaged = Bindings.createBooleanBinding(() -> this.model.get() != null && LCUtils.nullToFalse(visibleAndManagedFct.apply(model.get())), this.model);
        this.visibleProperty().bind(visibleAndManaged);
        this.managedProperty().bind(visibleAndManaged);
    }
}
