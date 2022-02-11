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

package org.lifecompanion.util.model;

import javafx.concurrent.Task;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Implementation for all Task implementation in LC : to force a task title.
 *
 * @param <T> task return type
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class LCTask<T> extends Task<T> {
    protected LCTask(final String title) {
        if (title == null)
            throw new NullPointerException("Title on LC task is mandatory");
        this.updateTitle(Translation.getText(title));
        this.updateProgress(-1, 0);
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        updateProgress(1, 1);
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        if (getProgress() < 0) {
            updateProgress(1, 2);
        }
    }

    @Override
    protected void failed() {
        super.failed();
        if (getProgress() < 0) {
            updateProgress(1, 2);
        }
    }
}
