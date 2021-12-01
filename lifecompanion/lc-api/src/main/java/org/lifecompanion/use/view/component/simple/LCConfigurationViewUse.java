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

package org.lifecompanion.use.view.component.simple;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.definition.selection.SelectionModeI;
import org.lifecompanion.base.view.component.simple.LCConfigurationViewBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * View for {@link LCConfigurationI} view in use mode
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCConfigurationViewUse extends LCConfigurationViewBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(LCConfigurationViewUse.class);

    private Node lastMainSelectionMode, lastSecondarySelectionMode;

    @Override
    public void initialize(final LCConfigurationI componentP) {
        super.initialize(componentP);
    }

    @Override
    public void initBinding() {
        this.model.selectionModeProperty().addListener(createSelectionModeViewChangeListener(true));
        this.model.directSelectionOnMouseOnScanningSelectionModeProperty().addListener(createSelectionModeViewChangeListener(false));
        this.model.hideMainSelectionModeViewProperty().addListener((obs, ov, nv) -> {
            if (lastMainSelectionMode != null && !lastMainSelectionMode.visibleProperty().isBound()) {
                lastMainSelectionMode.setVisible(!nv);
            }
            if (lastSecondarySelectionMode != null && !lastSecondarySelectionMode.visibleProperty().isBound()) {
                lastSecondarySelectionMode.setVisible(nv);
            }
        });
    }

    private ChangeListener<SelectionModeI> createSelectionModeViewChangeListener(boolean mainSelectionMode) {
        return (obs, ov, nv) -> {
            if (ov != null) {
                if (mainSelectionMode) lastMainSelectionMode = null;
                else lastSecondarySelectionMode = null;
                this.getChildren().remove(ov.getSelectionView());
            }
            if (nv != null) {
                final Node selectionView = nv.getSelectionView();
                this.getChildren().add(selectionView);
                if (mainSelectionMode) lastMainSelectionMode = selectionView;
                else lastSecondarySelectionMode = selectionView;
            }
        };
    }
}
