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

import org.lifecompanion.api.component.definition.WriterDisplayerI;
import org.lifecompanion.base.data.action.definition.BasePropertyChangeAction;

/**
 * All actions link to {@link org.lifecompanion.api.component.definition.WriterDisplayerI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TextDisplayerActions {

    public static class SetLineSpacingAction extends BasePropertyChangeAction<Number> {

        public SetLineSpacingAction(final WriterDisplayerI textDisplayer, final Number wantedValue) {
            super(textDisplayer.lineSpacingProperty(), wantedValue);
        }

        @Override
        public String getNameID() {
            return "action.set.line.spacing.name";
        }
    }

    public static class SetImageHeightAction extends BasePropertyChangeAction<Number> {

        public SetImageHeightAction(final WriterDisplayerI textDisplayer, final Number wantedValue) {
            super(textDisplayer.imageHeightProperty(), wantedValue);
        }

        @Override
        public String getNameID() {
            return "action.set.image.height.name";
        }
    }

    public static class SetEnableImageAction extends BasePropertyChangeAction<Boolean> {

        public SetEnableImageAction(final WriterDisplayerI textDisplayer, final Boolean wantedValue) {
            super(textDisplayer.enableImageProperty(), wantedValue);
        }

        @Override
        public String getNameID() {
            return "action.set.enable.image.name";
        }
    }

    public static class SetEnableWordWrapAction extends BasePropertyChangeAction<Boolean> {

        public SetEnableWordWrapAction(final WriterDisplayerI textDisplayer, final Boolean wantedValue) {
            super(textDisplayer.enableWordWrapProperty(), wantedValue);
        }

        @Override
        public String getNameID() {
            return "action.set.word.wrap.name";
        }
    }
}
