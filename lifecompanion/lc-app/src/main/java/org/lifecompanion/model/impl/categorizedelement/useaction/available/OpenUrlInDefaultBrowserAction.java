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
package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.DesktopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OpenUrlInDefaultBrowserAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenUrlInDefaultBrowserAction.class);

    private StringProperty url;

    public OpenUrlInDefaultBrowserAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.COMPUTER_FEATURES;
        this.nameID = "action.open.url.in.browser.name";
        this.order = 5;
        this.staticDescriptionID = "action.open.url.in.browser.static.description";
        this.configIconPath = "computeraccess/icon_open_url_browser.png";
        this.parameterizableAction = true;
        url = new SimpleStringProperty();
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.open.url.in.browser.variable.description", this.url));
    }

    public StringProperty urlProperty() {
        return url;
    }

    // Class part : "Execute"
    // ========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (StringUtils.isNotBlank(url.get())) {
            try {
                DesktopUtils.openUrlInDefaultBrowser(UseVariableController.INSTANCE.createText(this.url.get(), variables, varValue -> URLEncoder.encode(varValue, StandardCharsets.UTF_8)));
            } catch (Exception e) {
                LOGGER.warn("Couldn't not open URL in default browser / url is {}", url.get(), e);
            }
        }
    }
    // ========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(OpenUrlInDefaultBrowserAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(OpenUrlInDefaultBrowserAction.class, this, nodeP);
    }
    //========================================================================

}
