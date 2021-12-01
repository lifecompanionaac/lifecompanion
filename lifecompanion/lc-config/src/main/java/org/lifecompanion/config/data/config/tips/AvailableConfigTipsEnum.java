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

package org.lifecompanion.config.data.config.tips;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.base.data.config.LCConstant;

/**
 * Represent "tips" provided to user on application startup, or if user ask.<br>
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum AvailableConfigTipsEnum {
	CHANGE_PROFILE_LEVEL("config.tips.change.profile.level", "change_profile_level.mp4"), //
	LAUNCH_DEFAULT_CONFIG("config.tips.default.configuration", "launch_default_configuration.mp4"), //
	SHOW_GRID_CONFIG_MODE("config.tips.show.grid.config.mode", "show_grid_config_mode.mp4"), //
	MULTIPLE_KEY_STYLE_CHANGE("config.tips.multiple.key.style.change", "multiple_key_style.mp4"), //
	USE_USER_COMPONENTS("config.tips.use.user.components", "user_component.mp4"), //
	STACK_IN_GRID("config.tips.stack.in.grid", "stack_in_grid.mp4"), //
	FIND_COMPONENT("config.tips.find.component", "find_component.mp4"), //
	TRANSPARENT_IMAGE("config.tips.transparent.image", "transparent_image.mp4"), //
	VOICE_SYNTHESIZER_EXCEPTION("config.tips.handle.synthesizer.exception", "handle_synthesizer_exception.mp4"), //
	USE_MODE_FRAME("config.tips.use.mode.frame", "use_frame_configuration.mp4"), //
	CLONE_KEY_STYLE("config.tips.clone.key.style", "clone_key_style.mp4"), //
	ORGANIZE_IMAGES("config.tips.organize.images", "organize_images.mp4"), //
	ADD_LONG_SELECTION("config.tips.add.long.selection", "add_long_selection.mp4"), //
	ADD_IMAGE_FROM_COMPUTER("config.tips.add.image.computer", "add_image_from_computer.mp4")//
	;

	private final String id;
	private final String mediaName;
	private final String languageBaseId;

	private AvailableConfigTipsEnum(String languageBaseId, String mediaName) {
		this.id = this.name();
		this.languageBaseId = languageBaseId;
		this.mediaName = mediaName;
	}

	public String getId() {
		return id;
	}

	public String getMediaPath() {
		return LCConstant.EXT_PATH_DATA_TIPS_IMAGES + mediaName;
	}

	public boolean mediaExists() {
		return this.mediaName != null;
	}

	public String getTitle() {
		return Translation.getText(languageBaseId + ".title");
	}

	public String getMessage() {
		return Translation.getText(languageBaseId + ".message");
	}
}
