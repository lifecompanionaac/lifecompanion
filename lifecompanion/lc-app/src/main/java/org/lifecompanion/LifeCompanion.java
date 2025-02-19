/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2024 CMRRF KERPAPE (Lorient, France)
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
package org.lifecompanion;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.doublelaunch.DoubleLaunchListenerImpl;
import org.lifecompanion.controller.editmode.ErrorHandlingController;
import org.lifecompanion.controller.lifecycle.LifeCompanionController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.useapi.LifeCompanionControlServerController;
import org.lifecompanion.framework.commons.doublelaunch.DoubleLaunchController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class that represent the main JavaFX application.<br>
 * This class is only useful to correctly start the JavaFX thread, but all the loading is done in {@link LifeCompanionBootstrap}.<br>
 * The exit of the application is done once the JavaFX Thread ends.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LifeCompanion extends Application {
    private final static Logger LOGGER = LoggerFactory.getLogger(LifeCompanion.class);
    private static List<String> argsCollection;

    @Override
    public void start(final Stage stage) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LifeCompanion.LOGGER.error("A uncaught exception was thrown on the JavaFX Thread", e);
            ErrorHandlingController.INSTANCE.showErrorNotificationWithExceptionDetails(Translation.getText("unknown.error.happened.notif.title"), e);
        });
        Platform.setImplicitExit(false);
        new LifeCompanionBootstrap(stage, argsCollection).startLifeCompanion();
    }

    /**
     * Launch the launcher
     *
     * @param args the path to a configuration to load
     */
    public static void main(final String[] args) {
        LOGGER.info("Logs are saved to {}", getLogPath().getAbsolutePath());
        argsCollection = args != null ? new ArrayList<>(Arrays.asList(args)) : new ArrayList<>();

        // Detect global runtime configurations
        GlobalRuntimeConfigurationController.INSTANCE.init(argsCollection);

        boolean disableDoubleLaunchCheck = GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_DOUBLE_LAUNCH_CHECK);
        boolean doubleRun = DoubleLaunchController.INSTANCE.startAndDetect(new DoubleLaunchListenerImpl(), !disableDoubleLaunchCheck, args);
        if (!doubleRun || disableDoubleLaunchCheck) {
            //Start
            Instant startDate = Instant.now();
            LifeCompanion.LOGGER.info("{} version {} (build {}) launching with args\n\t{}",
                    LCConstant.NAME,
                    InstallationController.INSTANCE.getBuildProperties().getVersionLabel(),
                    InstallationController.INSTANCE.getBuildProperties().getBuildDate(),
                    args);
            // Run the service server (if needed)
            LifeCompanionControlServerController.INSTANCE.startControlServer();
            // Run the FX app
            Application.launch(args);
            //Inform
            Instant endDate = Instant.now();
            long between = ChronoUnit.SECONDS.between(startDate, endDate);
            LifeCompanion.LOGGER.info("Program will exit (session duration : {} second )\n\n", between);
        } else {
            LifeCompanion.LOGGER.warn("Double run detected for LifeCompanion, LifeCompanion will exit");
            System.exit(-1);
        }
    }

    @Override
    public void stop() throws Exception {
        LifeCompanionControlServerController.INSTANCE.setAppStopping(true);
        LifeCompanion.LOGGER.info("Will launch the exit task...");
        LifeCompanionController.INSTANCE.lcExit();
        DoubleLaunchController.INSTANCE.stop();
        LifeCompanionControlServerController.INSTANCE.stopControlServer();
        LifeCompanion.LOGGER.info("Every exit task are done, LifeCompanion will close...");
    }

    private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static File getLogPath() {
        return new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "logs" + File.separator +
                "application." + LOG_DATE_FORMAT.format(new Date()) + ".log");
    }
}
