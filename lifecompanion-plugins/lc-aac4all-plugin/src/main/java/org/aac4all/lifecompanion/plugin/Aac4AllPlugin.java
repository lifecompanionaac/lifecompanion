package org.aac4all.lifecompanion.plugin;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.textprediction.WordPredictionController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.usevariable.IntegerUseVariable;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.SnapshotUtils;
import tobii.Tobii;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Aac4AllPlugin implements PluginI {
    public static final String ID = "lc-aac4all-plugin";

    // RES
    //========================================================================
    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_aac4all_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return null;
    }

    @Override
    public String[] getDefaultConfigurations(String languageCode) {
        return null;
    }
    //========================================================================


    // PLUGIN START/STOP
    //========================================================================
    @Override
    public void start(File dataDirectory) {
        // Plugin global init here
    }

    @Override
    public void stop(File dataDirectory) {
        // Plugin global stop here
    }
    //========================================================================

    // MODE START/STOP
    //========================================================================
    private final AtomicBoolean logging = new AtomicBoolean(false);

    @Override
    public void modeStart(LCConfigurationI configuration) {
        System.out.println("START");
        new Thread(() -> {
            ThreadUtils.safeSleep(2000);
            //WritingStateController.INSTANCE.insertText(WritingEventSource.SYSTEM, "toto");
            WritingStateController.INSTANCE.addWritingEventListener(writingEvent -> {
                System.out.println(writingEvent);
            });
        }).start();
        // testTobiiAndScreenshot();
    }

    private void testTobiiAndScreenshot() {
        Thread loggingThread = new Thread(() -> {
            AtomicInteger i = new AtomicInteger();
            ThreadUtils.safeSleep(5000);
            while (logging.get()) {
                float[] floats = Tobii.gazePosition();
                System.out.println(floats != null ? Arrays.toString(floats) : "null");
                Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
                System.out.println(stage.getWidth() + " , " + stage.getHeight());
                Platform.runLater(() -> {
                    BufferedImage buffImage = SwingFXUtils.fromFXImage(stage.getScene().getRoot().snapshot(new SnapshotParameters(), null), null);
                    try {
                        ImageIO.write(buffImage, "png", new File("./screen/" + (i.incrementAndGet()) + ".png"));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                });
                // Tobii.getXY()
                ThreadUtils.safeSleep(500);
            }
        });
        logging.set(true);
        loggingThread.start();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        System.out.println("STOP");
        logging.set(false);
    }
    //========================================================================


    // VARIABLES
    //========================================================================
    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return null;
    }

    @Override
    public Function<UseVariableDefinitionI, UseVariableI<?>> getSupplierForUseVariable(String id) {
        return null;
    }
    //========================================================================

    // PLUGIN PROPERTIES
    //========================================================================
    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new Aac4AllPluginProperties(parentConfiguration);
    }
    //========================================================================
}
