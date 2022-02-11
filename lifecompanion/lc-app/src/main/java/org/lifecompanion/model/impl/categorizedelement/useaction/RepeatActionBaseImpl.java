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
package org.lifecompanion.model.impl.categorizedelement.useaction;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.util.LCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This action represent the action that are executed while the event is executing.<br>
 * This is a simple way to create action that are repeated.
 *
 * @param <T>
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class RepeatActionBaseImpl<T extends UseActionTriggerComponentI> extends BaseUseActionImpl<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(RepeatActionBaseImpl.class);

    /**
     * Indicate if this action is repeating
     */
    private boolean repeatEvent = false;

    /**
     * The thread used to repeat the action in a loop
     */
    private Thread repeatThread;

    public RepeatActionBaseImpl(final Class<T> allowedParentP) {
        super(allowedParentP);
    }

    @Override
    public void eventStarts(final UseActionEvent eventType) {
        this.repeatEvent = true;
        //First execution directly on event start
        this.executeFirstBeforeRepeat(eventType);
        //Repeat after a while
        LCUtils.safeSleep(this.getDelayBeforeRepeatStartMillis());
        if (this.repeatEvent) {
            this.repeatThread = new Thread(() -> {
                while (this.repeatEvent) {
                    this.executeOnRepeat(eventType);
                    LCUtils.safeSleep(this.getDelayBetweenEachRepeatMillis());
                }
            });
            this.repeatThread.setDaemon(true);
            this.repeatThread.start();
        }
    }

    @Override
    public void eventEnds(final UseActionEvent eventType) {
        this.repeatEvent = false;
        if (this.repeatThread != null) {
            try {
                this.repeatThread.join();
            } catch (Throwable e) {
                RepeatActionBaseImpl.LOGGER.warn("Couldn't stop and wait for repeat Thread", e);
            }
        }
        this.repeatEnded(eventType);
    }

    // Class part : "Subclass information"
    //========================================================================

    /**
     * Called once on event start.<br>
     * This is executed without delay and before the action starts to be repeated.<br>
     * This can be useful to provide a direct behavior before repetition.<br>
     * For example, when a key is held on a keyboard, it's firstly writing, then after a delay the key is written multiple times.
     *
     * @param eventType the event type
     */
    protected abstract void executeFirstBeforeRepeat(UseActionEvent eventType);

    /**
     * Called multiple time in repeat loop.<br>
     * This should execute the action to repeat.
     *
     * @param eventType the event type
     */
    protected abstract void executeOnRepeat(UseActionEvent eventType);

    /**
     * Called when the event ends is detected (and after the repeat thread is stopped)
     *
     * @param eventType the event type
     */
    protected abstract void repeatEnded(UseActionEvent eventType);

    /**
     * @return the delay after {@link #executeFirstBeforeRepeat(UseActionEvent)} before starting the repeat loop
     */
    protected abstract long getDelayBeforeRepeatStartMillis();

    /**
     * @return the delay between each repetition in the repeat loop
     */
    protected abstract long getDelayBetweenEachRepeatMillis();
    //========================================================================

}
