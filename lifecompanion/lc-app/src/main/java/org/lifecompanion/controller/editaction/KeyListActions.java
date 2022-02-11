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
package org.lifecompanion.controller.editaction;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.io.IOManager;
import org.lifecompanion.controller.io.task.KeyListExportTask;
import org.lifecompanion.controller.io.task.KeyListImportTask;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.editmode.LCFileChooser;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyListActions {
    private final static Logger LOGGER = LoggerFactory.getLogger(KeyListActions.class);

    private static final SimpleDateFormat DATE_FORMAT_FILENAME = new SimpleDateFormat("yyyyMMdd");


    public static class ExportKeyListsAction implements BaseEditActionI {
        private static final int MAX_FILE_NAME_LENGTH = 127;
        private final Node source;
        private final KeyListNodeI rootNode;

        public ExportKeyListsAction(Node source, KeyListNodeI rootNode) {
            this.source = source;
            this.rootNode = rootNode;
        }

        @Override
        public void doAction() throws LCException {
            FileChooser keyListFileChooser = LCFileChooser.getChooserKeyList(FileChooserType.KEYLIST_EXPORT);

            LCConfigurationDescriptionI configurationDescription = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();
            if (configurationDescription == null) {
                configurationDescription = ProfileController.INSTANCE.currentProfileProperty().get().getConfigurationById(AppModeController.INSTANCE.getEditModeContext().configurationProperty().get().getID());
            }
            String configurationName = Translation.getText("keylist.default.file.export.name") + (configurationDescription != null ? " - " + configurationDescription.configurationNameProperty().get() :
                    "");

            keyListFileChooser.setInitialFileName(DATE_FORMAT_FILENAME.format(new Date()) + "_" + LCUtils.getValidFileName(configurationName) + "");
            File keyListExportFile = keyListFileChooser.showSaveDialog(UIUtils.getSourceWindow(source));
            if (keyListExportFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.KEYLIST_EXPORT, keyListExportFile.getParentFile());
                KeyListExportTask keyListExportTask = IOManager.INSTANCE.createExportKeyListTask(keyListExportFile, rootNode.getChildren());
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, keyListExportTask);
            }
        }


        @Override
        public String getNameID() {
            return "todo";
        }
    }

    public static class ImportKeyListsAction implements BaseEditActionI {
        private final Node source;
        private final Consumer<List<KeyListNodeI>> importedNodesConsumer;

        public ImportKeyListsAction(Node source, Consumer<List<KeyListNodeI>> importedNodesConsumer) {
            this.source = source;
            this.importedNodesConsumer = importedNodesConsumer;
        }

        @Override
        public void doAction() throws LCException {
            FileChooser keyListFileChooser = LCFileChooser.getChooserKeyList(FileChooserType.KEYLIST_IMPORT);
            File keyListImportFile = keyListFileChooser.showOpenDialog(UIUtils.getSourceWindow(source));
            if (keyListImportFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.KEYLIST_IMPORT, keyListImportFile.getParentFile());
                KeyListImportTask keyListImportTask = IOManager.INSTANCE.createImportKeyListTask(List.of(keyListImportFile));
                keyListImportTask.setOnSucceeded(e -> importedNodesConsumer.accept(keyListImportTask.getValue()));
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, keyListImportTask);
            }
        }

        @Override
        public String getNameID() {
            return "todo";
        }
    }
}
