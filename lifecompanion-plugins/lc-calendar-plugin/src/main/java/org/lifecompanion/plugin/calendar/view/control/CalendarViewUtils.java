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

package org.lifecompanion.plugin.calendar.view.control;

import javafx.beans.value.ChangeListener;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.model.api.configurationcomponent.SoundResourceHolderI;
import org.lifecompanion.ui.common.control.generic.SoundRecordingControl;

import java.util.function.Function;
import java.util.function.Supplier;

public class CalendarViewUtils {
    public static <M> void addSoundRecordingControlListener(Supplier<M> modelSupplier, ToggleSwitch toggleSwitch, SoundRecordingControl soundRecordingControl, Function<M, SoundResourceHolderI> soundResourceHolderGetter) {
        toggleSwitch.selectedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                soundRecordingControl.dispose();
            }
        });
        soundRecordingControl.setFileAndDurationChangeListener((file, duration) -> {
            final M current = modelSupplier.get();
            if (current != null) {
                soundResourceHolderGetter.apply(current).updateSound(file, duration);
            }
        });
    }

    public static ChangeListener<Boolean> disableIfSelected(ToggleSwitch toggleSwitch) {
        return (obs, ov, nv) -> {
            if (nv) {
                toggleSwitch.setSelected(false);
            }
        };
    }
}
