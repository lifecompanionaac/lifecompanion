/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.configurationcomponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.util.javafx.FXThreadUtils;

public enum DynamicKeyFillController implements ModeListenerI {
    INSTANCE;

    private final ObjectProperty<GridPartKeyComponentI> sourceKey;


    DynamicKeyFillController() {
        sourceKey = new SimpleObjectProperty<>();
    }


    @Override
    public void modeStart(LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.sourceKey.set(null);
    }

    public void startFill(GridPartKeyComponentI key) {
        sourceKey.set(key);
    }

    public void endFill(GridPartKeyComponentI key) {
        GridPartKeyComponentI sourceKeyV = sourceKey.get();
        if (sourceKeyV != null) {
            sourceKey.set(null);
            fillWith(sourceKeyV, key);
        }
    }

    public void clearFillOn(GridPartKeyComponentI targetKeyComp) {
        fillWith(targetKeyComp, null);
    }

    public void clearFill() {
        endFill(null);
    }

    public void fillWith(GridPartKeyComponentI destinationKey, GridPartKeyComponentI sourceKey) {
        FXThreadUtils.runOnFXThread(() -> {
            setIfNotBound(destinationKey.textContentProperty(), sourceKey != null ? sourceKey.textContentProperty() : null);
            setIfNotBound(destinationKey.imageVTwoProperty(), sourceKey != null ? sourceKey.imageVTwoProperty() : null);
            setIfNotBound(destinationKey.videoProperty(), sourceKey != null ? sourceKey.videoProperty() : null);
            destinationKey.getKeyStyle().copyChanges(sourceKey != null ? sourceKey.getKeyStyle() : null, true);
            destinationKey.getKeyTextStyle().copyChanges(sourceKey != null ? sourceKey.getKeyTextStyle() : null, true);
        });
    }

    private <T> void setIfNotBound(Property<T> toSet, Property<T> toGet) {
        if (!toSet.isBound()) {
            toSet.setValue(toGet != null ? toGet.getValue() : null);
        }
    }
}
