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

package org.lifecompanion.base.data.ui;

import java.util.HashSet;

import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.StackComponentI;
import org.lifecompanion.api.component.definition.grid.ComponentGridI;

/**
 * Utils class to help component to use
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CharKeyOptionHelper {

	//To char array
	public final static String CHAR_LIST = "abcdefghijklmnopqrstuvwxyz_éèçêù.',!?";
	public static final char[] CHAR_LIST_ARRAY = CharKeyOptionHelper.CHAR_LIST.toCharArray();

	public static String getNextCharFor(final GridComponentI grid) {
		HashSet<String> alreadyAddedChar = new HashSet<>();
		//Try to get every grid of the stack
		StackComponentI stack = grid.stackParentProperty().get();
		if (stack != null) {
			ObservableList<GridComponentI> components = stack.getComponentList();
			for (GridComponentI gridComponentI : components) {
				CharKeyOptionHelper.addTextContentFor(gridComponentI, alreadyAddedChar);
			}
		} else {
			CharKeyOptionHelper.addTextContentFor(grid, alreadyAddedChar);
		}
		//Detect the first unadded char
		for (char c : CharKeyOptionHelper.CHAR_LIST_ARRAY) {
			String nextChar = "" + c;
			if (!alreadyAddedChar.contains(nextChar)) {
				return nextChar;
			}
		}
		return null;
	}

	private static void addTextContentFor(final GridPartComponentI component, final HashSet<String> alreadyAddedChar) {
		if (component instanceof GridComponentI) {
			ComponentGridI grid = ((GridComponentI) component).getGrid();
			int rows = grid.getRow();
			int columns = grid.getColumn();
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < columns; c++) {
					GridPartComponentI gridPart = grid.getComponent(r, c);
					CharKeyOptionHelper.addTextContentFor(gridPart, alreadyAddedChar);
				}
			}
		} else if (component instanceof GridPartKeyComponentI) {
			GridPartKeyComponentI key = (GridPartKeyComponentI) component;
			alreadyAddedChar.add(key.textContentProperty().get());
		}
	}
}
