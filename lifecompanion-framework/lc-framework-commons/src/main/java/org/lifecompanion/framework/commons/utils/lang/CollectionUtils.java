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
package org.lifecompanion.framework.commons.utils.lang;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Class that provide utils functions for array and list.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CollectionUtils {
	private CollectionUtils() {}

	/**
	 * To remove a interval from a given list
	 * @param list the list where the interval must be removed
	 * @param fromIndex the begin interval, inclusive
	 * @param toIndex the end interval, exclusive
	 */
	public static void removeInterval(final List<?> list, final int fromIndex, final int toIndex) {
		Iterator<?> rowIterator = list.iterator();
		int index = 0;
		while (rowIterator.hasNext() && index < toIndex) {
			rowIterator.next();
			if (index >= fromIndex) {
				rowIterator.remove();
			}
			index++;
		}
	}

	/**
	 * To create a copy of array, the base size for the copy in the size of src.<br>
	 * This must be use when dst.length>=src.length
	 * @param src the source array
	 * @param dst the destination array
	 */
	public static <T> void copyGrow(final T[][] src, final T[][] dst) {
		for (int r = 0; r < src.length; r++) {
			for (int c = 0; c < src[r].length; c++) {
				dst[r][c] = src[r][c];
			}
		}
	}

	/**
	 * To create a copy of array, the base size for the copy in the size of dst<br>
	 * This must be use when dst.length<=src.length
	 * @param src the source array
	 * @param dst the destination array
	 */
	public static <T> void copyReduce(final T[][] src, final T[][] dst) {
		for (int r = 0; r < dst.length; r++) {
			for (int c = 0; c < dst[r].length; c++) {
				dst[r][c] = src[r][c];
			}
		}
	}

	/**
	 * Clone a array into another
	 * @param src the array to copy
	 * @param dst the destination array where copy will be done
	 */
	public static <T> void clone(final T[][] src, final T[][] dst) {
		CollectionUtils.copyGrow(src, dst);
	}

	/**
	 * Null safe isEmpty
	 * @param coll collection to check
	 * @return true if coll is null or empty
	 */
	public static boolean isEmpty(final Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}
}
