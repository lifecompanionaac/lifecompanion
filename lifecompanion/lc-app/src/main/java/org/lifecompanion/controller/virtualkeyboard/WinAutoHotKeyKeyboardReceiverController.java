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

package org.lifecompanion.controller.virtualkeyboard;

import javafx.scene.input.KeyCode;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.configurationcomponent.GlobalKeyEventManager;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Route;
import spark.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Allows LifeCompanion to receive global keyboard events, even if not targeted on running instance.<br>
 * This is useful to synchronize external input (e.g. a configuration used to complete current writing as virtual device) with current input or to listen global key events<br>
 * This was implemented with AutoHotKey (added as an exe file and launched on mode use : will then call an internal server on each event)
 */
public enum WinAutoHotKeyKeyboardReceiverController implements ModeListenerI {
    INSTANCE;

    private static final int PORT = 8647;

    private static final Logger LOGGER = LoggerFactory.getLogger(WinAutoHotKeyKeyboardReceiverController.class);

    private final File exePath = new File(LCConstant.WIN_INPUT_LISTENER_EXE);

    private Process currentAhkProcess;
    private Service currentListenerServer;

    WinAutoHotKeyKeyboardReceiverController() {

    }

    // HANDLING EVENTS
    //========================================================================
    private static final Map<Character, Runnable> SPECIAL_ACTIONS = new HashMap<>();

    static {
        SPECIAL_ACTIONS.put('\n', () -> WritingStateController.INSTANCE.newLine(WritingEventSource.EXTERNAL_USER_INPUT));
        SPECIAL_ACTIONS.put('\t', () -> WritingStateController.INSTANCE.tab(WritingEventSource.EXTERNAL_USER_INPUT));
        SPECIAL_ACTIONS.put(' ', () -> WritingStateController.INSTANCE.space(WritingEventSource.EXTERNAL_USER_INPUT));
    }

    private void charTyped(String charAsString) {
        if (StringUtils.safeLength(charAsString) > 0) {
            final char firstChar = charAsString.charAt(0);
            final Runnable specialAction = SPECIAL_ACTIONS.get(firstChar);
            if (specialAction != null) {
                specialAction.run();
            } else if (firstChar > 32 && firstChar != 127) {
                WritingStateController.INSTANCE.insertText(WritingEventSource.EXTERNAL_USER_INPUT, new String(charAsString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            } else {
                LOGGER.info("Wasn't able to write char : {}", charAsString);
            }
        }
    }

    private void keyDown(String keyCodeAsString) {
        final KeyCode keyCode = getKeyCodeSafe(keyCodeAsString);
        if (keyCode == KeyCode.DELETE) {
            WritingStateController.INSTANCE.removeNextChar(WritingEventSource.EXTERNAL_USER_INPUT);
        } else if (keyCode == KeyCode.BACK_SPACE) {
            WritingStateController.INSTANCE.removeLastChar(WritingEventSource.EXTERNAL_USER_INPUT);
        } else if (keyCode == KeyCode.LEFT) {
            WritingStateController.INSTANCE.moveCaretBackward(WritingEventSource.EXTERNAL_USER_INPUT);
        } else if (keyCode == KeyCode.RIGHT) {
            WritingStateController.INSTANCE.moveCaretForward(WritingEventSource.EXTERNAL_USER_INPUT);
        } else if (keyCode == KeyCode.DOWN) {
            WritingStateController.INSTANCE.moveCaretDown(WritingEventSource.EXTERNAL_USER_INPUT);
        } else if (keyCode == KeyCode.UP) {
            WritingStateController.INSTANCE.moveCaretUp(WritingEventSource.EXTERNAL_USER_INPUT);
        } else if (keyCode == KeyCode.HOME) {
            WritingStateController.INSTANCE.moveCaretToStart(WritingEventSource.EXTERNAL_USER_INPUT);
        } else if (keyCode == KeyCode.END) {
            WritingStateController.INSTANCE.moveCaretToEnd(WritingEventSource.EXTERNAL_USER_INPUT);
        } else if (keyCode == KeyCode.SPACE) {
            WritingStateController.INSTANCE.space(WritingEventSource.EXTERNAL_USER_INPUT);
        }
        if (keyCode != null) {
            GlobalKeyEventManager.INSTANCE.genericLcEventFired(new GlobalKeyEventManager.LCKeyEvent(keyCode, GlobalKeyEventManager.LCKeyEventType.PRESSED));
        }
    }

    private void keyUp(String keyCodeAsString) {
        final KeyCode keyCode = getKeyCodeSafe(keyCodeAsString);
        if (keyCode != null) {
            GlobalKeyEventManager.INSTANCE.genericLcEventFired(new GlobalKeyEventManager.LCKeyEvent(keyCode, GlobalKeyEventManager.LCKeyEventType.RELEASED));
        }
    }

    private KeyCode getKeyCodeSafe(String keyCodeAsString) {
        try {
            final int valueInt = Integer.parseInt(keyCodeAsString);
            final KeyCode keyCodeMapped = Win32ToFxKeyCodeConverter.WIN32_TO_JAVAFX.get(valueInt);
            if (keyCodeMapped != null) {
                return keyCodeMapped;
            } else {
                LOGGER.warn("Didn't find any Win32 key code mapping to JavaFX for {}, will return raw mapping, this might result in incorrect keycode...", keyCodeAsString);
                for (KeyCode keyCode : KeyCode.values()) {
                    if (valueInt == keyCode.getCode()) return keyCode;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not find JavaFX keycode from {}", keyCodeAsString, e);
        }
        return null;
    }
    //========================================================================

    // RECEIVE SERVER
    //========================================================================
    private void startListenerServer() {
        currentListenerServer = Service.ignite();
        currentListenerServer.port(PORT);
        currentListenerServer.path("/input-hook/", () -> {
            currentListenerServer.post("/char", this.call(this::charTyped));
            currentListenerServer.post("/keydown", this.call(this::keyDown));
            currentListenerServer.post("/keyup", this.call(this::keyUp));
        });
    }

    private void stopListenerServer() {
        if (currentListenerServer != null) {
            currentListenerServer.stop();
            currentListenerServer.awaitStop();
            currentListenerServer = null;
        }
    }

    private Route call(Consumer<String> fct) {
        return (request, response) -> {
            fct.accept(request.body());
            return "";
        };
    }
    //========================================================================

    // AHK HOOK PROGRAM
    //========================================================================
    private void startAhkHook(String toBlockInAhkFormat) {
        try {
            currentAhkProcess = new ProcessBuilder()
                    .command(exePath.getAbsolutePath(), "" + PORT, toBlockInAhkFormat)
                    .start();
        } catch (Exception e) {
            LOGGER.error("Couldn't start AHK hook program", e);
        }
    }

    private void stopAhkHook() {
        if (currentAhkProcess != null) {
            currentAhkProcess.destroy();
        }
    }
    //========================================================================

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        if (SystemType.current() == SystemType.WINDOWS && configuration.virtualKeyboardProperty().get()) {
            startListenerServer();
            final Set<KeyCode> blockedKeyCodes = GlobalKeyEventManager.INSTANCE.getBlockedKeyCodes();
            LOGGER.info("Detected {} keys to block in external input hook", blockedKeyCodes.size());
            String injectedKeys = blockedKeyCodes.stream().map(k -> Win32ToFxKeyCodeConverter.javaFXKeyCodeToAutoHotKey(k, null)).collect(Collectors.joining());
            LOGGER.info("Injected AHK keys : {}", injectedKeys);
            startAhkHook(injectedKeys);
        }
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        stopAhkHook();
        stopListenerServer();
    }
    //========================================================================
}
