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

package org.lifecompanion.model.impl.textcomponent;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleBinding;
import org.lifecompanion.model.api.textcomponent.CachedLineListenerDataI;
import org.lifecompanion.model.api.textcomponent.TextBoundsProviderI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerLineI;

import java.util.List;
import java.util.function.Consumer;

public class CachedLineListenerData implements CachedLineListenerDataI {
    private final Consumer<List<TextDisplayerLineI>> listener;
    private final DoubleBinding maxWidth;
    private final TextBoundsProviderI textBoundsProvider;
    private final InvalidationListener associatedInvalidationListener;
    private final Runnable unbind;

    public CachedLineListenerData(Consumer<List<TextDisplayerLineI>> listener, DoubleBinding maxWidth, TextBoundsProviderI textBoundsProvider,
                                  InvalidationListener associatedInvalidationListener, Runnable unbind) {
        super();
        this.listener = listener;
        this.maxWidth = maxWidth;
        this.textBoundsProvider = textBoundsProvider;
        this.associatedInvalidationListener = associatedInvalidationListener;
        this.unbind = unbind;
    }

    @Override
    public DoubleBinding maxWidthProperty() {
        return maxWidth;
    }

    @Override
    public TextBoundsProviderI getTextBoundsProvider() {
        return textBoundsProvider;
    }

    @Override
    public InvalidationListener getAssociatedInvalidationListener() {
        return associatedInvalidationListener;
    }

    @Override
    public Consumer<List<TextDisplayerLineI>> getListener() {
        return this.listener;
    }

    @Override
    public void unbind() {
        this.unbind.run();
    }
}
