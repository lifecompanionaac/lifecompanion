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

package org.lifecompanion.controller.voicesynthesizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class SpeakTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeakTask.class);

    private volatile boolean enabled = true;

    private final CopyOnWriteArrayList<SpeakTask> taskList;

    protected SpeakTask(CopyOnWriteArrayList<SpeakTask> taskList) {
        this.taskList = taskList;
        this.taskList.add(this);
    }

    @Override
    public void run() {
        taskList.remove(this);
        if (enabled) {
            try {
                executeSpeakAction();
            } catch (Throwable t) {
                LOGGER.warn("Couldn't execute the text to speech call", t);
            }
        }
    }

    protected abstract void executeSpeakAction() throws Throwable;

    public void disable() {
        enabled = false;
    }
}
