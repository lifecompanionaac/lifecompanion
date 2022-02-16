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

package org.lifecompanion.controller.systemvk;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.util.binding.Unbindable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public enum SystemVirtualKeyboardController {
    INSTANCE;

    private static final long EVENT_DISTANCE_TIMING_THRESHOLD = 300;
    private static final long TOUCH_EVENT_DISTANCE_TIMING_THRESHOLD = 10_000;

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemVirtualKeyboardController.class);


    private final static Map<SystemVirtualKeyboardType, String> CMDS = FluentHashMap
            .map(SystemVirtualKeyboardType.WINDOWS_TABTIP, "C:\\Program Files\\Common Files\\Microsoft Shared\\ink\\TabTip.exe")
            .with(SystemVirtualKeyboardType.WINDOWS_TABTIP32, "C:\\Program Files (x86)\\Common Files\\Microsoft Shared\\ink\\TabTip32.exe")
            .with(SystemVirtualKeyboardType.WINDOWS_OSK, "osk");

    private final AtomicBoolean vkTaskRunning;

    private long lastTouchEvent;
    private long lastFocusOwnerChange;
    private final ExecutorService executorService;
    private boolean touchEventWasDetectedAtLeastOnce;

    private final Map<Scene, Runnable> registredScenes;

    SystemVirtualKeyboardController() {
        vkTaskRunning = new AtomicBoolean(false);
        registredScenes = new HashMap<>();
        if (SystemType.current() == SystemType.WINDOWS) {
            executorService = Executors.newSingleThreadExecutor(LCNamedThreadFactory.daemonThreadFactory("TouchKeyboardHelper"));
        } else {
            executorService = null;
        }
    }

    // PUBLIC API
    //========================================================================
    public Unbindable registerSceneFromDialog(Dialog<?> dialog) {
        if (dialog != null && dialog.getDialogPane() != null && dialog.getDialogPane().getScene() != null) {
            final Scene scene = dialog.getDialogPane().getScene();
            registerScene(scene);
            return () -> unregisterScene(scene);
        }
        return null;
    }

    public void registerScene(Scene scene) {
        if (SystemType.current() == SystemType.WINDOWS) {
            if (!registredScenes.containsKey(scene)) {
                final EventHandler<MouseEvent> mouseEventEventHandler = me -> {
                    if (me.isSynthesized()) {
                        touchEventWasDetectedAtLeastOnce = true;
                        this.lastTouchEvent = System.currentTimeMillis();
                        this.checkIfShowTouchKeyboard(getClosestTextInputControl(scene.getFocusOwner()));
                    }
                };
                scene.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventEventHandler);
                final ChangeListener<Node> changeListenerFocus = (obs, ov, nv) -> {
                    final TextInputControl closestTextInputControl = getClosestTextInputControl(nv);
                    if (closestTextInputControl != null) {
                        this.lastFocusOwnerChange = System.currentTimeMillis();
                        this.checkIfShowTouchKeyboard(closestTextInputControl);
                    }
                };
                scene.focusOwnerProperty().addListener(changeListenerFocus);
                Runnable unbind = () -> {
                    scene.focusOwnerProperty().removeListener(changeListenerFocus);
                    scene.removeEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventEventHandler);
                };
                registredScenes.put(scene, unbind);
            }
        }
    }

    public void unregisterScene(Scene scene) {
        final Runnable unbind = registredScenes.remove(scene);
        if (unbind != null) {
            unbind.run();
        }
    }

    public void showIfEnabled() {
        if (SystemType.current() == SystemType.WINDOWS && UserConfigurationController.INSTANCE.autoVirtualKeyboardShowProperty().get() && touchEventWasDetectedAtLeastOnce) {
            launchShowTask();
        }
    }
    //========================================================================

    // TOOLS/LAUNCH
    //========================================================================
    private void checkIfShowTouchKeyboard(final TextInputControl closestTextInputControl) {
        if (UserConfigurationController.INSTANCE.autoVirtualKeyboardShowProperty().get()) {
            long now = System.currentTimeMillis();
            if ((now - lastTouchEvent) < TOUCH_EVENT_DISTANCE_TIMING_THRESHOLD && ((now - lastFocusOwnerChange) < EVENT_DISTANCE_TIMING_THRESHOLD || closestTextInputControl != null)) {
                launchShowTask();
            }
        }
    }

    private void launchShowTask() {
        if (!this.vkTaskRunning.getAndSet(true)) {
            this.executorService.submit(new LaunchSystemVirtualKeyboardTask());
        }
    }

    private TextInputControl getClosestTextInputControl(Object element) {
        if (element instanceof TextInputControl) return (TextInputControl) element;
        else if (element instanceof Parent) {
            Parent parent = (Parent) element;
            for (Node node : parent.getChildrenUnmodifiable()) {
                if (node instanceof TextInputControl) return (TextInputControl) node;
            }
        }
        return null;
    }
    //========================================================================

    // LAUNCH TASK
    //========================================================================
    private class LaunchSystemVirtualKeyboardTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            try {
                for (SystemVirtualKeyboardType virtualKeyboardType : SystemVirtualKeyboardType.values()) {
                    try {
                        if (virtualKeyboardType.isFilePath() && !new File(CMDS.get(virtualKeyboardType)).exists()) {
                            throw new IOException("Virtual keyboard file " + CMDS.get(virtualKeyboardType) + " doesn't exist");
                        }
                        // Doesn't check for process result because some VK doesn't return 0...
                        final Process process = new ProcessBuilder(Arrays.asList("cmd", "/c", CMDS.get(virtualKeyboardType))).start();
                        process.waitFor();
                        break;// if no exception here, doesn't need to start next virtual keyboard
                    } catch (Exception ex) {
                        LOGGER.error("Impossible to launch virtual keyboard : {}", virtualKeyboardType, ex);
                    }
                }
            } finally {
                vkTaskRunning.set(false);
            }
            return null;
        }
    }
    //========================================================================
}
