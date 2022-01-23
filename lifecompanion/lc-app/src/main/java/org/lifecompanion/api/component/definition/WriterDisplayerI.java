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
package org.lifecompanion.api.component.definition;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import org.lifecompanion.api.component.definition.text.CachedLineListenerDataI;
import org.lifecompanion.api.component.definition.text.TextBoundsProviderI;
import org.lifecompanion.api.component.definition.text.TextDisplayerLineI;
import org.lifecompanion.api.control.events.WritingStateControllerI;
import org.lifecompanion.api.style2.definition.TextDisplayerStyleUserI;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface WriterDisplayerI extends DisplayableComponentI, TextDisplayerStyleUserI {
    /**
     * @return the wanted image height if {@link #enableImageProperty()} is true
     */
    DoubleProperty imageHeightProperty();

    /**
     * @return space between text line on the text displayer
     */
    DoubleProperty lineSpacingProperty();

    /**
     * @return if we should display the images in the editor
     */
    BooleanProperty enableImageProperty();

    /**
     * @return if we should enable word wrap in editor
     */
    BooleanProperty enableWordWrapProperty();

    CachedLineListenerDataI setCachedLinesUpdateListener(Consumer<List<TextDisplayerLineI>> listener, DoubleBinding maxWithProperty, TextBoundsProviderI textBoundsProvider);

    CachedLineListenerDataI getCachedLineUpdateListener();

    List<TextDisplayerLineI> getLastCachedLines();

}
