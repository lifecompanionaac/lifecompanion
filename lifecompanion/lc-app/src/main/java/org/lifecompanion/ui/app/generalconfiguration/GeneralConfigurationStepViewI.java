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

package org.lifecompanion.ui.app.generalconfiguration;

import javafx.scene.Node;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.ui.editmode.BaseConfigurationViewI;

public interface GeneralConfigurationStepViewI extends BaseConfigurationViewI<LCConfigurationI>, LCViewInitHelper {
    double PADDING = 20.0;
    double LEFT_COLUMN_MIN_WIDTH = 250.0;
    double GRID_H_GAP = 10.0, GRID_V_GAP = 10.0;
    double FIELD_WIDTH = 150.0;

    // GENERAL
    //========================================================================
    boolean shouldBeAddedToMainMenu();

    String getTitleId();

    String getStep();

    default String getMenuStepToSelect() {
        return getStep();
    }

    default int getStepMenuOrder() {
        try {
            GeneralConfigurationStep configStepFromEnum = GeneralConfigurationStep.valueOf(getStep());
            return configStepFromEnum.ordinal();
        } catch (IllegalArgumentException e) {
            return Integer.MAX_VALUE;
        }
    }

    String getPreviousStep();
    //========================================================================

    // VIEW
    //========================================================================
    Node getViewNode();

    default void beforeShow(Object[] stepArgs) {
    }

    default void afterHide() {
    }

    default boolean shouldCancelBeConfirmed() {
        return false;
    }
    //========================================================================

    // BINDING
    //========================================================================
    void saveChanges();

    default void cancelChanges() {
    }
    //========================================================================

}
