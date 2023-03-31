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

package org.lifecompanion.util.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class CountingMap<T> {
    private final Map<T, AtomicInteger> counts;

    public CountingMap() {
        this.counts = new HashMap<>();
    }

    public void increment(T element) {
        counts.computeIfAbsent(element, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public void decrement(T element) {
        AtomicInteger existingCount = counts.get(element);
        if (existingCount != null) {
            if (existingCount.decrementAndGet() == 0) {
                counts.remove(element);
            }
        } else {
            // FIXME : warning
        }
    }

    public void setCountsFrom(Collection<T> collection) {
        counts.clear();
        collection.forEach(this::increment);
    }

    public int getCount(T element) {
        AtomicInteger count = counts.get(element);
        return count != null ? count.intValue() : 0;
    }

    public Map<T, Integer> getCountExtraction() {
        Map<T, Integer> countsBeforeChange = new HashMap<>();
        counts.forEach((e, v) -> countsBeforeChange.put(e, v.intValue()));
        return countsBeforeChange;
    }

    public CountingMap<T> clone() {
        CountingMap<T> countingMap = new CountingMap<>();
        counts.forEach((e, v) -> countingMap.counts.put(e, new AtomicInteger(v.intValue())));
        return countingMap;
    }

    public void forEach(BiConsumer<? super T, Integer> action) {
        counts.forEach((e, v) -> action.accept(e, v.intValue()));
    }
}
