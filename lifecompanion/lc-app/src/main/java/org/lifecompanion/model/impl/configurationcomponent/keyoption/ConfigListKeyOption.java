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

package org.lifecompanion.model.impl.configurationcomponent.keyoption;

import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeConfigurationAction;
import org.lifecompanion.model.impl.imagedictionary.StaticImageElement;
import org.lifecompanion.util.binding.BindingUtils;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigListKeyOption extends AbstractKeyOption {

    private ChangeConfigurationAction changeConfigurationAction;

    public ConfigListKeyOption() {
        super();
        this.optionNameId = "key.option.config.list.name";
        this.optionDescriptionId = "key.option.config.list.description";
        this.iconName = "icon_type_configlist.png";
        this.disableImage.set(true);
        this.disableTextContent.set(true);
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        this.changeConfigurationAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, ChangeConfigurationAction.class);
        if (this.changeConfigurationAction == null) {
            this.changeConfigurationAction = new ChangeConfigurationAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.changeConfigurationAction);
        }
        key.textContentProperty().set(Translation.getText("config.list.key.default.text"));
        key.imageVTwoProperty().set(new StaticImageElement(IconHelper.get("example_image_entry.png")));
        this.changeConfigurationAction.attachedToKeyOptionProperty().set(true);
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.changeConfigurationAction);
        clearKey(key);
    }

    private void clearKey(GridPartKeyComponentI key) {
        BindingUtils.unbindAndSetNull(key.textContentProperty());
        BindingUtils.unbindAndSetNull(key.imageVTwoProperty());
        if (this.changeConfigurationAction != null) {
            this.changeConfigurationAction.configurationIdProperty().set(null);
        }
    }

    private String lastLoadRequestId;

    public void updateConfiguration(LCConfigurationDescriptionI configurationDescription) {
        GridPartKeyComponentI key = this.attachedKey.get();
        if (key != null) {
            if (configurationDescription != null) {
                key.textContentProperty().set(configurationDescription.configurationNameProperty().get());
                key.imageVTwoProperty().set(null);
                this.changeConfigurationAction.configurationIdProperty().set(configurationDescription.getConfigurationId());
                final String loadId = StringUtils.getNewID();
                this.lastLoadRequestId = loadId;
                configurationDescription.requestImageLoad(image -> {
                    if (StringUtils.isEquals(loadId, lastLoadRequestId)) {
                        key.imageVTwoProperty().set(new StaticImageElement(image));
                    }
                });
            } else {
                clearKey(key);
            }

        }
    }
}
