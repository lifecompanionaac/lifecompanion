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

package org.lifecompanion.ui.common.control.generic;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.StringUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.media.SoundPlayerController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.editmode.ErrorHandlingController;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;

/**
 * Control to record a sound and to save it on a location.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SoundRecordingControl extends HBox implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(SoundRecordingControl.class);

    private final static AudioFormat[] AUDIO_FORMATS = {
            new AudioFormat(44100, 32, 1, true, true),
            new AudioFormat(32000, 16, 2, true, true),
            new AudioFormat(32000, 16, 1, true, true),
            new AudioFormat(16000, 16, 1, true, true)
    };

    /**
     * Current duration in second (already recorded sound, or currently recording)
     */
    private final IntegerProperty currentSoundDurationInSecond;

    /**
     * Property that contains the current recorded file (or the previous file)
     */
    private final ObjectProperty<File> currentRecordedFile;

    /**
     * Label that display current sound duration
     */
    private Label labelCurrentSoundDuration;

    /**
     * Button to record a sound
     */
    private ToggleButton buttonRecordPlayStop;

    /**
     * Button to play the recorded sound
     */
    private Button buttonPlayCurrent;

    /**
     * Property to record a sound
     */
    private final BooleanProperty recordingProperty;

    /**
     * Current recording thread
     */
    private RecordingThread currentRecordingThread;

    /**
     * Scale transition to apply when recording
     */
    private ScaleTransition scaleTransitionRecording;

    private BiConsumer<File, Integer> fileAndDurationChangeListener;

    public SoundRecordingControl() {
        currentSoundDurationInSecond = new SimpleIntegerProperty(-1);
        this.currentRecordedFile = new SimpleObjectProperty<>();
        recordingProperty = new SimpleBooleanProperty(false);
        this.currentRecordedFile.addListener((obs, ov, nv) -> {
            if (fileAndDurationChangeListener != null) {
                fileAndDurationChangeListener.accept(nv, currentSoundDurationInSecond.get());
            }
        });
        this.initAll();
    }

    @Override
    public void initUI() {
        //UI
        this.buttonRecordPlayStop = FXControlUtils.createGraphicsToggleButton(Translation.getText("sound.record.start.button"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.MICROPHONE).sizeFactor(1).color(LCGraphicStyle.SECOND_DARK), null);
        buttonRecordPlayStop.getStyleClass().addAll("stroke-selected","background-selected-lightgrey");
        buttonRecordPlayStop.setContentDisplay(ContentDisplay.LEFT);
        buttonRecordPlayStop.setMinWidth(100.0);
        FXUtils.applyPerformanceConfiguration(buttonRecordPlayStop);

        this.labelCurrentSoundDuration = new Label();
        labelCurrentSoundDuration.getStyleClass().add("text-font-size-120");
        buttonPlayCurrent = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLAY).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.explain.play.recorded.sound");

        //Transition when recording
        scaleTransitionRecording = new ScaleTransition(Duration.millis(600), buttonRecordPlayStop);
        scaleTransitionRecording.setAutoReverse(true);
        scaleTransitionRecording.setInterpolator(Interpolator.EASE_OUT);
        scaleTransitionRecording.setCycleCount(Transition.INDEFINITE);
        scaleTransitionRecording.setToX(1.1);
        scaleTransitionRecording.setToY(1.1);

        //Total
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(5.0);
        this.getChildren().addAll(this.buttonRecordPlayStop, new Separator(Orientation.VERTICAL), labelCurrentSoundDuration, buttonPlayCurrent);
    }

    @Override
    public void initListener() {
        this.recordingProperty.addListener((obs, ov, nv) -> {
            if (!nv) {
                this.buttonRecordPlayStop.setSelected(false);
                this.scaleTransitionRecording.stop();
                this.buttonRecordPlayStop.setScaleX(1.0);
                this.buttonRecordPlayStop.setScaleY(1.0);
            } else {
                this.scaleTransitionRecording.playFromStart();
            }
        });
        this.buttonPlayCurrent.setOnAction(e -> {
            if (this.currentRecordedFile.get() != null) {
                SoundPlayerController.INSTANCE.playSoundAsync(this.currentRecordedFile.get(), true);
            }
        });

        this.buttonRecordPlayStop.setOnAction(e -> {
            if (this.buttonRecordPlayStop.isSelected()) {
                startRecording();
            } else {
                stopRecording(false);
            }
        });
    }

    @Override
    public void initBinding() {
        this.labelCurrentSoundDuration.textProperty().bind(Bindings.createStringBinding(() -> StringUtils.durationToString(this.currentSoundDurationInSecond.get()), currentSoundDurationInSecond));
        buttonRecordPlayStop.textProperty().bind(Bindings.createStringBinding(
                () -> Translation.getText(recordingProperty.get() ? "sound.record.stop.button" : "sound.record.start.button"), recordingProperty));
        this.buttonPlayCurrent.disableProperty().bind(this.currentRecordedFile.isNull().or(recordingProperty));
    }

    // Class part : "Recording"
    //========================================================================
    private void startRecording() {
        //If previous record was not cancelled
        if (this.currentRecordingThread != null) {
            this.stopRecording(true);
        }
        this.recordingProperty.set(true);
        this.currentRecordingThread = new RecordingThread();
        this.currentRecordingThread.start();
    }

    private void stopRecording(boolean cancelSoundSaving) {
        if (this.currentRecordingThread != null) {
            this.currentRecordingThread.dipose(cancelSoundSaving);
            this.currentRecordingThread = null;
        }
    }

    public class RecordingThread extends Thread {
        private TargetDataLine line;
        private Timer timer;
        private boolean cancelSoundSaving;

        public RecordingThread() {
            super("LCSoundRecordingThread");
            timer = new Timer(true);
        }

        public void dipose(boolean cancelSoundSaving) {
            this.cancelSoundSaving = cancelSoundSaving;
            if (line != null) {
                line.stop();
                line.close();
            }
        }

        @Override
        public void run() {
            try {
                DataLine.Info info = null;
                AudioFormat format = null;

                for (AudioFormat formatToTest : AUDIO_FORMATS) {
                    info = new DataLine.Info(TargetDataLine.class, formatToTest);
                    if (AudioSystem.isLineSupported(info)) {
                        format = formatToTest;
                        LOGGER.info("Selected line : {}", formatToTest);
                        break;
                    }
                }

                if (format == null) {
                    LOGGER.error("Line not supported for {}", info);
                    FXThreadUtils.runOnFXThread(() -> LCNotificationController.INSTANCE.showNotification(LCNotification.createWarning("record.sound.action.not.available.title")));
                    return;
                }

                //Start capturing audio
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                //Small delay before capture : we want the result sound to immediately start
                Thread.sleep(100);

                line.start();
                LOGGER.info("Audio capture started");
                long startTime = System.currentTimeMillis();

                //Launch timer task to update current recording duration
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        FXThreadUtils.runOnFXThread(() -> {
                            currentSoundDurationInSecond.set((int) ((System.currentTimeMillis() - startTime) / 1000));
                        });
                    }
                }, 0, 1000);

                // Recording the audio in a temp file
                File tempFile = File.createTempFile("lifecompanion-sound-record", ".wav");
                AudioInputStream ais = new AudioInputStream(line);
                LOGGER.info("Will try to save audio to {}", tempFile);
                int written = AudioSystem.write(ais, AudioFileFormat.Type.WAVE, tempFile);
                LOGGER.info("Audio recording ended successfully, byte written {}", FileNameUtils.getFileSize(written));

                //Set the result file
                if (!cancelSoundSaving) {
                    FXThreadUtils.runOnFXThread(() -> {
                        currentRecordedFile.set(tempFile);
                    });
                }
            } catch (Exception e) {
                LOGGER.error("Error while recording sound", e);
                FXThreadUtils.runOnFXThread(() -> ErrorHandlingController.INSTANCE.showErrorNotificationWithExceptionDetails( Translation.getText("record.sound.action.error.message"),  e));
            } finally {
                this.timer.cancel();
                FXThreadUtils.runOnFXThread(() -> {
                    recordingProperty.set(false);
                });
            }
        }
    }
    //========================================================================

    // Class part : "Public API"
    //========================================================================
    public void dispose() {
        stopRecording(true);
        SoundPlayerController.INSTANCE.stopEveryPlayer();
    }

    public void setFileAndDuration(File file, int duration) {
        if (file == null || duration <= 0) {
            this.currentSoundDurationInSecond.set(-1);
            this.currentRecordedFile.set(null);
        } else {
            this.currentSoundDurationInSecond.set(duration);
            this.currentRecordedFile.set(file);
        }
    }

    public File getFile() {
        return this.currentRecordedFile.get();
    }

    public int getSoundDurationInSecond() {
        return this.currentSoundDurationInSecond.get();
    }

    public void setFileAndDurationChangeListener(BiConsumer<File, Integer> listener) {
        this.fileAndDurationChangeListener = listener;
    }
    //========================================================================

}
