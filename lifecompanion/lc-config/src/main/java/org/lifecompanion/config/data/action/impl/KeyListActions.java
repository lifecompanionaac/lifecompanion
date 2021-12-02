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
package org.lifecompanion.config.data.action.impl;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import org.lifecompanion.api.action.definition.BaseConfigActionI;
import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.api.component.definition.simplercomp.KeyListNodeI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.control.AsyncExecutorController;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.base.data.io.task.KeyListExportTask;
import org.lifecompanion.base.data.io.task.KeyListImportTask;
import org.lifecompanion.config.data.control.FileChooserType;
import org.lifecompanion.config.data.control.LCStateController;
import org.lifecompanion.config.view.common.LCFileChooser;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyListActions {
    private final static Logger LOGGER = LoggerFactory.getLogger(KeyListActions.class);

    private static final SimpleDateFormat DATE_FORMAT_FILENAME = new SimpleDateFormat("yyyyMMdd");


    public static class ExportKeyListsAction implements BaseConfigActionI {
        private static final int MAX_FILE_NAME_LENGTH = 127;
        private final Node source;
        private final KeyListNodeI node;

        public ExportKeyListsAction(Node source, KeyListNodeI node) {
            this.source = source;
            this.node = node;
        }

        @Override
        public void doAction() throws LCException {
            FileChooser keyListFileChooser = LCFileChooser.getChooserKeyList(FileChooserType.KEYLIST_EXPORT);

            LCConfigurationDescriptionI configurationDescription = AppController.INSTANCE.currentConfigDescriptionProperty().get();
            if (configurationDescription == null) {
                configurationDescription = AppController.INSTANCE.currentProfileProperty().get().getConfigurationById(AppController.INSTANCE.currentConfigConfigurationProperty().get().getID());
            }
            String configurationName = Translation.getText("keylist.default.file.export.name") + (configurationDescription != null ? " - " + configurationDescription.configurationNameProperty().get() :
                    "");

            keyListFileChooser.setInitialFileName(DATE_FORMAT_FILENAME.format(new Date()) + "_" + LCUtils.getValidFileName(configurationName) + "");
            File keyListExportFile = keyListFileChooser.showSaveDialog(UIUtils.getSourceWindow(source));
            if (keyListExportFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.KEYLIST_EXPORT, keyListExportFile.getParentFile());
                KeyListExportTask keyListExportTask = IOManager.INSTANCE.createExportKeyListTask(keyListExportFile, List.of(node));
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, keyListExportTask);
            }
        }


        @Override
        public String getNameID() {
            return "todo";
        }
    }

    public static class ImportKeyListsAction implements BaseConfigActionI {
        private final Node source;
        private final Consumer<List<KeyListNodeI>> importedNodeConsumer;

        public ImportKeyListsAction(Node source, Consumer<List<KeyListNodeI>> importedNodeConsumer) {
            this.source = source;
            this.importedNodeConsumer = importedNodeConsumer;
        }

        @Override
        public void doAction() throws LCException {
            FileChooser keyListFileChooser = LCFileChooser.getChooserKeyList(FileChooserType.KEYLIST_IMPORT);
            List<File> keyListImportFiles = keyListFileChooser.showOpenMultipleDialog(UIUtils.getSourceWindow(source));
            if (LangUtils.isNotEmpty(keyListImportFiles)) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.KEYLIST_IMPORT, keyListImportFiles.get(0).getParentFile());
                KeyListImportTask keyListExportTask = IOManager.INSTANCE.createImportKeyListTask(keyListImportFiles);
                keyListExportTask.setOnSucceeded(e -> importedNodeConsumer.accept(keyListExportTask.getValue()));
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, keyListExportTask);
            }
        }

        @Override
        public String getNameID() {
            return "todo";
        }
    }
}
