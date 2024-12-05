package org.lifecompanion.plugin.phonecontrol;

import javafx.application.Platform;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.controller.resource.ResourceHelper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum PhoneControlController implements ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneControlController.class);
    private Timer refreshTimer;

    private PhoneControlPluginProperties currentPhoneControlPluginProperties;

    PhoneControlController() { }

    public void start(File dataDirectory) { }

    public void stop() { }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.currentPhoneControlPluginProperties = configuration.getPluginConfigProperties(PhoneControlPlugin.PLUGIN_ID, PhoneControlPluginProperties.class);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.currentPhoneControlPluginProperties = null;
    }
}
