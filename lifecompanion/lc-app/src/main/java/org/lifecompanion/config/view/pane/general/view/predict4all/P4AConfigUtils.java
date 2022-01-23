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
package org.lifecompanion.config.view.pane.general.view.predict4all;

import javafx.animation.Transition;

import java.util.concurrent.atomic.AtomicReference;

public class P4AConfigUtils {
    public static final double FACTOR_BOUND = 10.0;
    public static final double MAX_COST_BOUND = 5.0;

    public static final double COST_BOUND_UPPER = 2.5;
    public static final double COST_BOUND_LOWER = 0.2;

    public static double getFactorForSlider(final double factor) {
        if (factor == 1.0) {
            return 0.0;
        } else {
            return factor < 1.0 ? 1.0 / factor : factor;
        }
    }

    public static double getFactorForWord(final double factor) {
        if (factor == 0.0) {
            return 1.0;
        } else {
            return factor < 0.0 ? 1.0 / -factor : factor;
        }
    }

    public static void setCurrentTransitionAndPlay(final AtomicReference<Transition> reference, final Transition transition) {
        reference.set(transition);
        transition.play();
    }

    public static void unsetPreviousTransitionAndStop(final AtomicReference<Transition> reference) {
        Transition previousTransition = reference.getAndSet(null);
        if (previousTransition != null) {
            previousTransition.stop();
        }
    }
}
