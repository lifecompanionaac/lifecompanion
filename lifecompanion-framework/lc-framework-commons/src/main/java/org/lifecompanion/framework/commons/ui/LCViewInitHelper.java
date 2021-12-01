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

package org.lifecompanion.framework.commons.ui;

/**
 * Define all the basic method that a view part must implements.<br>
 * This class is just a easy way to help the class to implements initialization in a good way.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface LCViewInitHelper {

	/**
	 * Initialize all the things for this view part. <br>
	 * Default implementation is :
	 *
	 * <pre>
	 * {@link #initUI()}
	 * {@link #initListener()}
	 * {@link #initBinding()}
	 * </pre>
	 */
	default void initAll() {
		this.initUI();
		this.initListener();
		this.initBinding();
	}

	/**
	 * Create the component of this view part.
	 */
	void initUI();

	/**
	 * Initialize all the UI listener, all the buttons, spinner, listener must be initialized here when they don't directly change the values with binding.<br>
	 * The default implementation is NOOP
	 */
	default void initListener() {}

	/**
	 * Initialize the binding with the model component.<br>
	 * Each view part must add listener on model data that could change.<br>
	 * The default implementation is NOOP
	 */
	default void initBinding() {}
}
