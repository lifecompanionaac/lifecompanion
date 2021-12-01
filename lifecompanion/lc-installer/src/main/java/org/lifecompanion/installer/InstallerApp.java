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

package org.lifecompanion.installer;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.installer.controller.InstallerManager;
import org.lifecompanion.installer.ui.InstallerScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

public class InstallerApp extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallerApp.class);

    @Override
    public void init() throws Exception {
        String installerTranslationPath = "/translation/" + getLanguageCode() + "_lc_installer.xml";
        try (InputStream is = InstallerApp.class.getResourceAsStream(installerTranslationPath)) {
            Translation.INSTANCE.load(installerTranslationPath, is);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(Translation.getText("lc.installer.stage.title"));
        stage.setScene(new InstallerScene());
        stage.setWidth(600);
        stage.setHeight(700);
        stage.centerOnScreen();
        stage.getIcons().add(new Image("lifecompanion_icon_64px.png", -1.0, -1.0, true, true));
        stage.setOnCloseRequest(e -> {
            e.consume();
            InstallerManager.INSTANCE.cancelRequest();
        });
        stage.show();
        InstallerManager.INSTANCE.start(this.getParameters());
    }

    @Override
    public void stop() throws Exception {
        InstallerManager.INSTANCE.stop();
        LOGGER.info("Installer stopped");
    }


    public static void main(String[] args) throws Exception {
        LOGGER.info("Logs are saved to {}", new File(System.getProperty("java.io.tmpdir") + "/LifeCompanion/logs/installer.log").getAbsolutePath());
        LOGGER.info("Launching installer {} ({}) for app {} with args {}", InstallerManager.INSTANCE.getBuildProperties().getVersionLabel(),
                InstallerManager.INSTANCE.getBuildProperties().getBuildDate(),
                InstallerManager.INSTANCE.getBuildProperties().getAppId(),
                args);
        //TODO : should analyse args (as path to EXE on Windows to install default plugins > generate classpath config + download JAR)
        launch(args);
    }

    public static String getLanguageCode() {
        return "fr";
    }

}