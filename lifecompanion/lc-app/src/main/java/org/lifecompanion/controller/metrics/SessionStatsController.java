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

package org.lifecompanion.controller.metrics;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.impl.metrics.SessionEvent;
import org.lifecompanion.model.impl.metrics.SessionEventType;
import org.lifecompanion.model.impl.metrics.SessionPart;
import org.lifecompanion.model.impl.metrics.SessionType;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public enum SessionStatsController implements LCStateListener {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionStatsController.class);

    static final String SESSION_PART_DIRNAME = "parts";
    static final String EVENT_DIRNAME = "events";

    private final String currentSessionId = StringUtils.getNewID();
    private String currentSessionPartId;

    private final AtomicInteger currentUserInteractionInLastMinute;
    private final Map<Scene, Runnable> registredScenes;

    SessionStatsController() {
        currentUserInteractionInLastMinute = new AtomicInteger();
        registredScenes = new HashMap<>();
        InstallationController.INSTANCE.setInstallationRegistrationInformationSetCallback(() -> {
            if (UserConfigurationController.INSTANCE.recordAndSendSessionStatsProperty().get()) {
                LCNamedThreadFactory.daemonThreadFactoryWithPriority("SendPendingSessionStats", Thread.MIN_PRIORITY).newThread(new SendPendingSessionStatsRunnable()).start();
                LCNamedThreadFactory.daemonThreadFactoryWithPriority("RegisterUserInteractionForSessionStats", Thread.MIN_PRIORITY).newThread(new RegisterUserInteractionRunnable()).start();
            }
        });
    }

    // EVENT LOCAL CACHE
    //========================================================================
    public final static SimpleDateFormat DATE_FORMAT_FOR_DATA_MAP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void pushEvent(String typeId, Map<String, Object> data) {
        if (UserConfigurationController.INSTANCE.recordAndSendSessionStatsProperty().get() && currentSessionPartId != null) {
            File eventDataFile = new File(getAndCreateCurrentSessionPartDirectory() + File.separator + EVENT_DIRNAME + File.separator + StringUtils.getNewID() + File.separator + LCConstant.SESSION_DATA_FILENAME);
            IOUtils.createParentDirectoryIfNeeded(eventDataFile);
            try (PrintWriter pw = new PrintWriter(eventDataFile, StandardCharsets.UTF_8)) {
                JsonHelper.GSON.toJson(new SessionEvent(
                                typeId,
                                new Date(),
                                data)
                        , pw);
            } catch (Exception e) {
                LOGGER.error("Could not log session event", e);
            }
        }
    }

    public void pushEvent(String typeId) {
        pushEvent(typeId, null);
    }

    public void pushEvent(SessionEventType type, Map<String, Object> data) {
        pushEvent(type.getId(), data);
    }

    public void pushEvent(SessionEventType type) {
        pushEvent(type.getId());
    }

    /**
     * Called by both mode (use and config) to inform it start
     *
     * @param mode          the given mode
     * @param configuration the associated configuration to the started mode
     */
    public void modeStarted(AppMode mode, LCConfigurationI configuration) {
        if (UserConfigurationController.INSTANCE.recordAndSendSessionStatsProperty().get()) {
            // New session part started
            this.currentSessionPartId = StringUtils.getNewID();

            // Register info
            final LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
            SessionPart sessionPart = new SessionPart(
                    (mode == AppMode.USE ? SessionType.USE : SessionType.CONFIG).getId(),
                    new Date(),
                    configuration.getID(),
                    AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get() != null ? AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get().configurationNameProperty().get() : null,
                    currentProfile == null ? null : currentProfile.getID(),
                    currentProfile == null ? null : currentProfile.nameProperty().get()
            );
            // Save info
            try (PrintWriter pw = new PrintWriter(getAndCreateCurrentSessionPartDirectory() + File.separator + LCConstant.SESSION_DATA_FILENAME, StandardCharsets.UTF_8)) {
                JsonHelper.GSON.toJson(sessionPart, pw);
            } catch (Exception e) {
                LOGGER.error("Could not save session part information", e);
            }
            // Start event
            this.pushEvent(SessionEventType.PART_START);
            this.pushAndClearLastUserInteractionCount();
        }
    }

    /**
     * Called by both mode (use and config) to inform it stop
     *
     * @param mode the given mode
     */
    public void modeStopped(AppMode mode) {
        // In current session part, register a mode stop event
        if (currentSessionPartId != null) {
            this.pushAndClearLastUserInteractionCount();
            this.pushEvent(SessionEventType.PART_STOP);
        }
    }

    private File getAndCreateCurrentSessionPartDirectory() {
        final File directory = new File(LCConstant.PATH_SESSION_STATS_CACHE + File.separator + currentSessionId + File.separator + SESSION_PART_DIRNAME + File.separator + currentSessionPartId + File.separator);
        directory.mkdirs();
        return directory;
    }

    public String getCurrentSessionId() {
        return currentSessionId;
    }
    //========================================================================


    // START/STOP HOOK
    //========================================================================
    @Override
    public void lcStart() {
        AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().addListener((obs, ov, nv) -> {
            if (AppModeController.INSTANCE.modeProperty().get() == AppMode.EDIT) {
                if (ov != null) {
                    this.modeStopped(AppMode.EDIT);
                }
                if (nv != null) {
                    this.modeStarted(AppMode.EDIT, AppModeController.INSTANCE.getEditModeContext().configurationProperty().get());
                }
            }
        });
        LOGGER.info("Current session stat ID : {}", currentSessionId);
    }

    @Override
    public void lcExit() {
    }
    //========================================================================

    // INTERACTION COUNT LAST MINUTE
    //========================================================================
    public void increaseUserInteractionCount() {
        this.currentUserInteractionInLastMinute.incrementAndGet();
    }

    private void pushAndClearLastUserInteractionCount() {
        final int toSave = this.currentUserInteractionInLastMinute.getAndSet(0);
        this.pushEvent(SessionEventType.USER_INTERACTION_COUNT_LAST_MINUTE, FluentHashMap.map("value", toSave));
    }

    public class RegisterUserInteractionRunnable implements Runnable {

        @Override
        public void run() {
            while (UserConfigurationController.INSTANCE.recordAndSendSessionStatsProperty().get()) {
                try {
                    LCUtils.safeSleep(60_000);
                    pushAndClearLastUserInteractionCount();
                } catch (Throwable t) {
                    LOGGER.warn("Couldn't register user interaction count for stats", t);
                }
            }
        }
    }

    private final EventHandler<Event> eventHandlerToIncreaseInteractionCount = e -> increaseUserInteractionCount();

    public void registerSceneFromDialog(Dialog dialog) {
        if (dialog != null && dialog.getDialogPane() != null && dialog.getDialogPane().getScene() != null) {
            final Scene scene = dialog.getDialogPane().getScene();
            registerScene(scene);
            dialog.setOnHidden(e -> unregisterScene(scene));
        }
    }

    public void registerScene(Scene scene) {
        if (UserConfigurationController.INSTANCE.recordAndSendSessionStatsProperty().get()) {
            if (!registredScenes.containsKey(scene)) {
                Runnable unbind = () -> {
                    scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, eventHandlerToIncreaseInteractionCount);
                    scene.removeEventFilter(KeyEvent.KEY_PRESSED, eventHandlerToIncreaseInteractionCount);
                };
                registredScenes.put(scene, unbind);
                scene.addEventFilter(MouseEvent.MOUSE_PRESSED, eventHandlerToIncreaseInteractionCount);
                scene.addEventFilter(KeyEvent.KEY_PRESSED, eventHandlerToIncreaseInteractionCount);
            }
        }
    }

    public void unregisterScene(Scene scene) {
        final Runnable unbind = registredScenes.remove(scene);
        if (unbind != null) {
            unbind.run();
        }
    }
    //========================================================================
}
