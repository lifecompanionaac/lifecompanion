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

package org.lifecompanion.config.view.pane.menu;

import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.config.view.pane.left.BottomDetailView;
import javafx.scene.layout.VBox;

/**
 * Left menu, that contains every items that are not into ribbons.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MainMenu extends VBox implements LCViewInitHelper {
	public static final int MENU_WIDTH = 250;
	public static final double BUTTON_WIDTH = 90;

	private ProfileDetailView profileDetailView;
	private CurrentConfigDetailView currentConfigDetailView;
	private ProfilConfigDetailView profilConfigDetailView;
	private BottomDetailView bottomDetailView;

	/**
	 * Create a new main menu.
	 */
	public MainMenu() {
		this.initAll();
	}

	@Override
	public void initUI() {
		this.setMaxWidth(MainMenu.MENU_WIDTH);
		this.getStyleClass().add("main-menu");
		//Children
		this.profileDetailView = new ProfileDetailView();
		this.currentConfigDetailView = new CurrentConfigDetailView();
		this.profilConfigDetailView = new ProfilConfigDetailView();
		this.bottomDetailView = new BottomDetailView();
		this.getChildren().addAll(this.profileDetailView, this.currentConfigDetailView, this.profilConfigDetailView, this.bottomDetailView);
		UIUtils.applyPerformanceConfiguration(this);
	}

}
