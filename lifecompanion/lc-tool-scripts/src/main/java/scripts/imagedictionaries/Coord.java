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

import java.util.Collection;

class Coord {
    final int x, y;

    public Coord(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public static Coord of(int x, int y) {
        return new Coord(x, y);
    }

    public String toString() {
        return x + "," + y;
    }

    public void addToExplore(Collection<Coord> toExplore, int w, int h) {
        // top/right/bottom/left
        if (y - 1 > 0)
            toExplore.add(Coord.of(x, y - 1));
        if (x + 1 < w)
            toExplore.add(Coord.of(x + 1, y));
        if (y + 1 < h)
            toExplore.add(Coord.of(x, y + 1));
        if (x - 1 > 0)
            toExplore.add(Coord.of(x - 1, y));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coord other = (Coord) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}
