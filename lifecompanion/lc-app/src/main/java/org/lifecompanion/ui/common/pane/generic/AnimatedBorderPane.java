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

package org.lifecompanion.ui.common.pane.generic;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * A subclass of {@link BorderPane} that include a smooth animation when center change.<br>
 * This class provide a method to change center with a FadeAnimation with method {@link #changeCenter(Node)} because {@link #setCenter(Node)} is final.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class AnimatedBorderPane extends BorderPane {
	private static final long CHANGE_PAGE_ANIMATION_DURATION = 250;

	/**
	 * To know if the transition between page must be enabled
	 */
	private boolean enableTransition = true;

	/**
	 * The currently played transition on page change
	 */
	private FadeTransition currentTransition;

	/**
	 * Create a new border pane with animation inside.
	 */
	public AnimatedBorderPane() {
		super();
	}

	public void setEnableTransition(final boolean enableTransitionP) {
		this.enableTransition = enableTransitionP;
	}

	/**
	 * Change center, with a animation if enabled.<br>
	 * Node that {@link #getCenter()} will return newCenter only when animations ended (or immediately if animation are disabled)
	 * @param newCenter the new center to change.
	 */
	public void changeCenter(final Node newCenter) {
		//Remove previous
		Node previous = this.getCenter();
		EventHandler<ActionEvent> onFinishedRemove = (ea) -> {
			//Add new
			if (newCenter != null) {
				newCenter.setOpacity(0.0);
				if (this.currentTransition != null) {
					this.currentTransition.stop();
				}
				this.setCenter(newCenter);
				if (this.enableTransition) {
					this.currentTransition = new FadeTransition(Duration.millis(AnimatedBorderPane.CHANGE_PAGE_ANIMATION_DURATION), newCenter);
					this.currentTransition.setFromValue(0.0);
					this.currentTransition.setToValue(1.0);
					this.currentTransition.playFromStart();
				} else {
					newCenter.setOpacity(1.0);
				}
			}
		};
		//Handle null
		if (previous != null && this.enableTransition) {
			if (this.currentTransition != null) {
				this.currentTransition.stop();
			}
			this.currentTransition = new FadeTransition(Duration.millis(AnimatedBorderPane.CHANGE_PAGE_ANIMATION_DURATION), previous);
			this.currentTransition.setFromValue(1.0);
			this.currentTransition.setToValue(0.0);
			this.currentTransition.setOnFinished(onFinishedRemove);
			this.currentTransition.playFromStart();
		} else {
			onFinishedRemove.handle(null);
		}
	}
}
