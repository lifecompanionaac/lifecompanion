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
package org.lifecompanion.config.view.pane.tabs.part;

import org.lifecompanion.config.view.pane.tabs.api.AbstractRibbonTabContent;
import org.lifecompanion.config.view.pane.tabs.selected.part.*;

/**
 * Class that contains every ribbon part to display for each type that is selectable.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectedRibbonParts {
    public static class SelectedGridPart extends AbstractRibbonTabContent {

        public SelectedGridPart() {
            super(//
                    RibbonTabPart.create(ExpandCollapseRibbonPart.class), //
                    RibbonTabPart.create(RowColumnRibbonPart.class), //
                    RibbonTabPart.create(GridGapRibbonPart.class), //
                    RibbonTabPart.create(GridComponentSelectionModeRibbonPart.class) //
            );
        }
    }

    public static class SelectedGridPartStack extends AbstractRibbonTabContent {
        public SelectedGridPartStack() {
            super(//
                    RibbonTabPart.create(ExpandCollapseRibbonPart.class), //
                    RibbonTabPart.create(GridStackRibbonPart.class) //
            );
        }
    }

    public static class SelectedGridTextEditorPart extends AbstractRibbonTabContent {

        public SelectedGridTextEditorPart() {
            super(//
                    RibbonTabPart.create(ExpandCollapseRibbonPart.class), //
                    RibbonTabPart.create(TextDisplayerRibbonPart.class) //
            );
        }
    }

    public static class SelectedKeyPart extends AbstractRibbonTabContent {

        public SelectedKeyPart() {
            super(//
                    RibbonTabPart.create(ExpandCollapseRibbonPart.class), //
                    RibbonTabPart.create(KeyTextRibbonPart.class), //
                    RibbonTabPart.create(ImageUseComponentRibbonPart.class), //
                    RibbonTabPart.create(SingleSelectionKeyOptionRibbonPart.class));
        }
    }

    public static class MultiSelectedKeyPart extends AbstractRibbonTabContent {

        public MultiSelectedKeyPart() {
            super(//
                    RibbonTabPart.create(KeyTextRibbonPart.class), //
                    RibbonTabPart.create(MultiSelectionKeyOptionRibbonPart.class));
        }
    }

    public static class SelectedStackPart extends AbstractRibbonTabContent {

        public SelectedStackPart() {
            super(//
                    RibbonTabPart.create(RootStackRibbonPart.class) //
            );
        }

    }

    public static class SelectedTextEditorPart extends AbstractRibbonTabContent {

        public SelectedTextEditorPart() {
            super(//
                    RibbonTabPart.create(TextDisplayerRibbonPart.class) //
            );
        }
    }
}
