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
package org.lifecompanion.config.data.action.impl;

import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.style.StylePropertyI;
import org.lifecompanion.util.LCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that keeps every actions relative to style.<br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StyleActions {
    private static final Logger LOGGER = LoggerFactory.getLogger(StyleActions.class);

    // Class part : "Style 2"
    //========================================================================
    public static class ChangeMultipleStylePropAction<T> implements UndoRedoActionI {
        private final List<ChangeStylePropAction<T>> changeStylePropActions;

        public ChangeMultipleStylePropAction(List<StylePropertyI<T>> properties, T newValue) {
            changeStylePropActions = properties.stream().map(p -> new ChangeStylePropAction<>(p, newValue)).collect(Collectors.toList());
        }


        @Override
        public void doAction() throws LCException {
            for (ChangeStylePropAction<T> changeStylePropAction : changeStylePropActions) {
                changeStylePropAction.doAction();
            }
        }

        @Override
        public void undoAction() throws LCException {
            for (ChangeStylePropAction<T> changeStylePropAction : changeStylePropActions) {
                changeStylePropAction.undoAction();
            }
        }


        @Override
        public void redoAction() throws LCException {
            for (ChangeStylePropAction<T> changeStylePropAction : changeStylePropActions) {
                changeStylePropAction.redoAction();
            }
        }

        @Override
        public String getNameID() {
            //TODO: translate
            return "style.change.multiple.property.value";
        }
    }

    public static class ChangeStylePropAction<T> implements UndoRedoActionI {

        private final StylePropertyI<T> property;
        private final T newValue;
        private T oldValue;

        public ChangeStylePropAction(final StylePropertyI<T> property, final T val) {
            super();
            this.property = property;
            this.newValue = val;
        }

        @Override
        public void doAction() throws LCException {
            this.oldValue = this.property.selected().getValue();
            this.property.selected().setValue(!LCUtils.safeEquals(this.property.parent().getValue(), this.newValue) ? this.newValue : null);
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }

        @Override
        public String getNameID() {
            return "style.change.property.value";
        }

        @Override
        public void undoAction() throws LCException {
            this.property.selected().setValue(this.oldValue);
        }
    }
    //========================================================================

}
