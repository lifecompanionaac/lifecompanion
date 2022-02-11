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

package org.lifecompanion.model.impl.textcomponent;

import org.lifecompanion.model.api.textcomponent.WritingControllerStateI;
import org.lifecompanion.model.api.textcomponent.WritingEventI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.textcomponent.WritingEventType;

import java.util.Map;

public class WritingEvent implements WritingEventI {
    private final long timestamp;
    private final WritingControllerStateI beforeEventState, afterEventState;
    private final WritingEventSource source;
    private final WritingEventType type;
    private final Map<String, Object> values;

    public WritingEvent(WritingControllerStateI beforeEventState, WritingControllerStateI afterEventState, WritingEventSource source,
                        WritingEventType type, Map<String, Object> values) {
        super();
        this.timestamp = System.currentTimeMillis();
        this.beforeEventState = beforeEventState;
        this.afterEventState = afterEventState;
        this.source = source;
        this.type = type;
        this.values = values;
    }

    @Override
    public WritingControllerStateI getBeforeEventState() {
        return beforeEventState;
    }

    @Override
    public WritingControllerStateI getAfterEventState() {
        return afterEventState;
    }

    @Override
    public WritingEventSource getSource() {
        return source;
    }

    @Override
    public WritingEventType getType() {
        return type;
    }

    @Override
    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return "WritingEvent{" +
                "timestamp=" + timestamp +
                ", source=" + source +
                ", type=" + type +
                ",\n\tbeforeEventState=" + beforeEventState +
                ",\n\tafterEventState=" + afterEventState +
                ",\n\tvalues=" + values +
                '}';
    }
}
