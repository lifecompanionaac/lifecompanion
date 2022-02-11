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
package org.lifecompanion.controller.editmode;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Class that keep all the file chooser use in application.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCFileChoosers {

    private static FileChooser configurationFileChooser;
    private static FileChooser keylistFileChooser;
    private static FileChooser profileFileChooser;
    private static FileChooser imageFileChooser;
    private static FileChooser otherFileChooser;
    private static DirectoryChooser directoryChooser;

    // PRIVATE
    //========================================================================
    private static FileChooser getOrInitConfigurationFileChooser() {
        if (LCFileChoosers.configurationFileChooser == null) {
            LCFileChoosers.configurationFileChooser = new FileChooser();
            LCFileChoosers.configurationFileChooser.getExtensionFilters()
                    .add(new ExtensionFilter(Translation.getText("file.chooser.config.description"), "*." + LCConstant.CONFIG_FILE_EXTENSION));
        }
        return configurationFileChooser;
    }

    private static FileChooser getOrInitKeyListFileChooser() {
        if (keylistFileChooser == null) {
            LCFileChoosers.keylistFileChooser = new FileChooser();
            LCFileChoosers.keylistFileChooser.getExtensionFilters()
                    .add(new ExtensionFilter(Translation.getText("file.chooser.keylist.description"), "*." + LCConstant.KEYLIST_FILE_EXTENSION));
        }
        return keylistFileChooser;
    }

    private static FileChooser getOrInitProfileFileChooser() {
        if (profileFileChooser == null) {
            LCFileChoosers.profileFileChooser = new FileChooser();
            LCFileChoosers.profileFileChooser.getExtensionFilters()
                    .add(new ExtensionFilter(Translation.getText("file.chooser.profile.description"), "*." + LCConstant.PROFILE_FILE_EXTENSION));
        }
        return profileFileChooser;
    }

    private static FileChooser getOrInitImageFileChooser() {
        if (imageFileChooser == null) {
            LCFileChoosers.imageFileChooser = new FileChooser();
            LCFileChoosers.imageFileChooser.setTitle(Translation.getText("file.chooser.image.title"));
            String[] exts = new String[LCConstant.IMAGE_EXTENSIONS.length];
            for (int i = 0; i < exts.length; i++) {
                exts[i] = "*." + LCConstant.IMAGE_EXTENSIONS[i];
            }
            LCFileChoosers.imageFileChooser.getExtensionFilters().add(new ExtensionFilter(Translation.getText("file.chooser.image.description"), exts));
        }
        return imageFileChooser;
    }

    private static DirectoryChooser getOrInitDirectoryChooser() {
        if (directoryChooser == null) {
            LCFileChoosers.directoryChooser = new DirectoryChooser();
            LCFileChoosers.directoryChooser.setTitle(Translation.getText("file.chooser.directory"));
        }
        return directoryChooser;
    }

    //========================================================================

    // PUBLIC
    //========================================================================
    public static FileChooser getChooserConfiguration(final FileChooserType fileChooserType) {
        return initializeDirectory(LCFileChoosers.getOrInitConfigurationFileChooser(), fileChooserType);
    }

    public static FileChooser getChooserKeyList(final FileChooserType fileChooserType) {
        return initializeDirectory(LCFileChoosers.getOrInitKeyListFileChooser(), fileChooserType);
    }

    public static FileChooser getChooserProfile(final FileChooserType fileChooserType) {
        return initializeDirectory(LCFileChoosers.getOrInitProfileFileChooser(), fileChooserType);
    }

    public static FileChooser getChooserImage(final FileChooserType fileChooserType) {
        return initializeDirectory(LCFileChoosers.getOrInitImageFileChooser(), fileChooserType);
    }

    public static FileChooser getOtherFileChooser(final String title, final ExtensionFilter extensionFilter, final FileChooserType fileChooserType) {
        if (LCFileChoosers.otherFileChooser == null) {
            LCFileChoosers.otherFileChooser = new FileChooser();
        }
        LCFileChoosers.otherFileChooser.setTitle(title);
        LCFileChoosers.otherFileChooser.getExtensionFilters().clear();
        if (extensionFilter != null) {
            LCFileChoosers.otherFileChooser.getExtensionFilters().add(extensionFilter);
        }
        initializeDirectory(otherFileChooser, fileChooserType);
        return LCFileChoosers.otherFileChooser;
    }

    public static DirectoryChooser getChooserDirectory(final FileChooserType fileChooserType) {
        return getChooserDirectory(fileChooserType, null);
    }

    public static DirectoryChooser getChooserDirectory(final FileChooserType fileChooserType, final String title) {
        DirectoryChooser directoryChooser = initializeDirectory(getOrInitDirectoryChooser(), fileChooserType);
        if (title != null) {
            directoryChooser.setTitle(title);
        }
        return LCFileChoosers.directoryChooser;
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
