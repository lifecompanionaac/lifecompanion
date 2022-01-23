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

package org.lifecompanion.api.action.definition;

/**
 * This class represent a {@link UndoRedoActionI} that can be created by typing a command as String.<br>
 * This action must be able to parse a command, a to create a String for this action.<br>
 * Not in use for now.
 */
public interface StringSerializableActionI extends BaseConfigActionI {
	public String getCommandID();

	public String getHelpMessageID();

	public String getCommandString();

	/**
	 * Must set the command properties from the given args
	 * @param commandString the command arguments as string
	 */
	public void createFromString(String... args);
}
