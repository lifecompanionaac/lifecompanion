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
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WinAutoHotKeyVirtualKeyboard implements VirtualKeyboardI {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinAutoHotKeyVirtualKeyboard.class);

    private final File exePath = new File(LCConstant.WIN_INPUT_SENDER_EXE);

    @Override
    public void sendText(String rawText) throws Exception {
        if (StringUtils.isEquals("\"", rawText)) rawText = "\\\""; // Single quote char should be escaped - Issue #167
        new ProcessBuilder().command(exePath.getAbsolutePath(), "SendRaw", rawText).start().waitFor();
    }

    @Override
    public void keyTyped(KeyCode keyCode) throws Exception {
        new ProcessBuilder().command(exePath.getAbsolutePath(), "SendUnique", Win32ToFxKeyCodeConverter.javaFXKeyCodeToAutoHotKey(keyCode, null)).start().waitFor();
    }

    @Override
    public void multiKeyPressThenRelease(KeyCode... keyCodes) throws Exception {
        if (keyCodes != null && keyCodes.length > 0) {
            List<String> commands = new ArrayList<>(Arrays.asList(exePath.getAbsolutePath(), "SendMulti"));
            for (KeyCode keyCode : keyCodes) {
                commands.add(Win32ToFxKeyCodeConverter.javaFXKeyCodeToAutoHotKey(keyCode, "down"));
            }
            for (KeyCode keyCode : keyCodes) {
                commands.add(Win32ToFxKeyCodeConverter.javaFXKeyCodeToAutoHotKey(keyCode, "up"));
            }
            new ProcessBuilder().command(commands).start().waitFor();
        }
    }


    @Override
    public void modeStart(LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
    }
}
