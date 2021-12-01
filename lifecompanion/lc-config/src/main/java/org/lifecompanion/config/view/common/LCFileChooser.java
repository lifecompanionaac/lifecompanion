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
package org.lifecompanion.config.view.common;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.config.data.control.FileChooserType;
import org.lifecompanion.config.data.control.LCStateController;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Class that keep all the file chooser use in application.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCFileChooser {

    private static FileChooser configurationFileChooser;
    private static FileChooser keylistFileChooser;
    private static FileChooser profileFileChooser;
    private static FileChooser imageFileChooser;
    private static FileChooser otherFileChooser;
    private static DirectoryChooser directoryChooser;

    // PRIVATE
    //========================================================================
    private static FileChooser getOrInitConfigurationFileChooser() {
        if (LCFileChooser.configurationFileChooser == null) {
            LCFileChooser.configurationFileChooser = new FileChooser();
            LCFileChooser.configurationFileChooser.getExtensionFilters()
                    .add(new ExtensionFilter(Translation.getText("file.chooser.config.description"), "*." + LCConstant.CONFIG_FILE_EXTENSION));
        }
        return configurationFileChooser;
    }

    private static FileChooser getOrInitKeyListFileChooser() {
        if (keylistFileChooser == null) {
            LCFileChooser.keylistFileChooser = new FileChooser();
            LCFileChooser.keylistFileChooser.getExtensionFilters()
                    .add(new ExtensionFilter(Translation.getText("file.chooser.keylist.description"), "*." + LCConstant.KEYLIST_FILE_EXTENSION));
        }
        return keylistFileChooser;
    }

    private static FileChooser getOrInitProfileFileChooser() {
        if (profileFileChooser == null) {
            LCFileChooser.profileFileChooser = new FileChooser();
            LCFileChooser.profileFileChooser.getExtensionFilters()
                    .add(new ExtensionFilter(Translation.getText("file.chooser.profile.description"), "*." + LCConstant.PROFILE_FILE_EXTENSION));
        }
        return profileFileChooser;
    }

    private static FileChooser getOrInitImageFileChooser() {
        if (imageFileChooser == null) {
            LCFileChooser.imageFileChooser = new FileChooser();
            LCFileChooser.imageFileChooser.setTitle(Translation.getText("file.chooser.image.title"));
            String[] exts = new String[LCConstant.IMAGE_EXTENSIONS.length];
            for (int i = 0; i < exts.length; i++) {
                exts[i] = "*." + LCConstant.IMAGE_EXTENSIONS[i];
            }
            LCFileChooser.imageFileChooser.getExtensionFilters().add(new ExtensionFilter(Translation.getText("file.chooser.image.description"), exts));
        }
        return imageFileChooser;
    }

    private static DirectoryChooser getOrInitDirectoryChooser() {
        if (directoryChooser == null) {
            LCFileChooser.directoryChooser = new DirectoryChooser();
            LCFileChooser.directoryChooser.setTitle(Translation.getText("file.chooser.directory"));
        }
        return directoryChooser;
    }

    //========================================================================

    // PUBLIC
    //========================================================================
    public static FileChooser getChooserConfiguration(final FileChooserType fileChooserType) {
        return initializeDirectory(LCFileChooser.getOrInitConfigurationFileChooser(), fileChooserType);
    }

    public static FileChooser getChooserKeyList(final FileChooserType fileChooserType) {
        return initializeDirectory(LCFileChooser.getOrInitKeyListFileChooser(), fileChooserType);
    }

    public static FileChooser getChooserProfile(final FileChooserType fileChooserType) {
        return initializeDirectory(LCFileChooser.getOrInitProfileFileChooser(), fileChooserType);
    }

    public static FileChooser getChooserImage(final FileChooserType fileChooserType) {
        return initializeDirectory(LCFileChooser.getOrInitImageFileChooser(), fileChooserType);
    }

    public static FileChooser getOtherFileChooser(final String title, final ExtensionFilter extensionFilter, final FileChooserType fileChooserType) {
        if (LCFileChooser.otherFileChooser == null) {
            LCFileChooser.otherFileChooser = new FileChooser();
        }
        LCFileChooser.otherFileChooser.setTitle(title);
        LCFileChooser.otherFileChooser.getExtensionFilters().clear();
        if (extensionFilter != null) {
            LCFileChooser.otherFileChooser.getExtensionFilters().add(extensionFilter);
        }
        initializeDirectory(otherFileChooser, fileChooserType);
        return LCFileChooser.otherFileChooser;
    }

    public static DirectoryChooser getChooserDirectory(final FileChooserType fileChooserType) {
        return getChooserDirectory(fileChooserType, null);
    }

    public static DirectoryChooser getChooserDirectory(final FileChooserType fileChooserType, final String title) {
        DirectoryChooser directoryChooser = initializeDirectory(getOrInitDirectoryChooser(), fileChooserType);
        if (title != null) {
            directoryChooser.setTitle(title);
        }
        return LCFileChooser.directoryChooser;
    }
    //========================================================================


    // INIT DIR
    //========================================================================
    private static FileChooser initializeDirectory(FileChooser fileChooser, final FileChooserType fileChooserType) {
        fileChooser.setInitialDirectory(LCStateController.INSTANCE.getDefaultDirectoryFor(fileChooserType));
        return fileChooser;
    }

    private static DirectoryChooser initializeDirectory(DirectoryChooser directoryChooser, final FileChooserType fileChooserType) {
        directoryChooser.setInitialDirectory(LCStateController.INSTANCE.getDefaultDirectoryFor(fileChooserType));
        return directoryChooser;
    }
    //========================================================================

}
