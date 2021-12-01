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

package scripts.imagedictionaries;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class LoggingProgressIndicator {
    private final String name;
    private final long max;
    private AtomicLong count;
    private long finalCount;
    private AtomicInteger lastPercent = new AtomicInteger(-1);

    public LoggingProgressIndicator(long max, String name) {
        this.name = name;
        this.max = max;
        count = new AtomicLong();
    }

    public long getFinalCount() {
        return finalCount;
    }

    public void increment() {
        if (this.max > 0) {
            count.incrementAndGet();
            printPercent();
        }
    }

    private void printPercent() {
        int percent = (int) ((1.0 * count.get()) / (1.0 * max) * 100);
        if (percent != lastPercent.getAndSet(percent)) {
            if (lastPercent.get() % 5 == 0) {
                System.out.println(name + " : " + lastPercent.get() + " %");
            }
        }
    }

    public long getMax() {
        return max;
    }

    public long getCount() {
        return count.get();
    }

    public void finished(long finalCount) {
        this.finalCount = finalCount;
        this.count.set(max);
        printPercent();
    }
}
