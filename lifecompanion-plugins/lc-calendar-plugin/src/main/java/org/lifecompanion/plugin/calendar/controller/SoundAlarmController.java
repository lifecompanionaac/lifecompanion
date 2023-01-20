package org.lifecompanion.plugin.calendar.controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public enum SoundAlarmController implements ModeListenerI {
    INSTANCE;


    private static final Logger LOGGER = LoggerFactory.getLogger(SoundAlarmController.class);

    private final Map<StandardAlarm, MediaPlayer> playerPerAlarm;

    SoundAlarmController() {
        this.playerPerAlarm = new HashMap<>();
    }

    private void initIfNeeded() {
        if (playerPerAlarm.isEmpty()) {
            for (StandardAlarm value : StandardAlarm.values()) {
                File destAlarm = new File(org.lifecompanion.util.IOUtils.getTempDir("alarm") + File.separator + value.filename);
                IOUtils.createParentDirectoryIfNeeded(destAlarm);
                try (FileOutputStream fos = new FileOutputStream(destAlarm)) {
                    try (InputStream is = ResourceHelper.getInputStreamForPath("/alarm/" + value.filename)) {
                        IOUtils.copyStream(is, fos);
                        Media media = new Media(destAlarm.toURI().toString());
                        MediaPlayer player = new MediaPlayer(media);
                        this.playerPerAlarm.put(value, player);
                        LOGGER.info("Alarm file prepared to {}", destAlarm);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Couldn't copy alarm sound", e);
                }
            }
        }
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        for (MediaPlayer mediaPlayer : this.playerPerAlarm.values()) {
            mediaPlayer.stop();
        }
    }

    public void playFromStart(StandardAlarm alarm) {
        initIfNeeded();
        MediaPlayer player = playerPerAlarm.get(alarm);
        if (player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.stop();
        }
        player.play();
    }

    public void stopAllAlarm() {
        for (MediaPlayer player : playerPerAlarm.values()) {
            if (player.getStatus() == MediaPlayer.Status.PLAYING) {
                player.stop();
            }
        }
    }

    public boolean togglePlayStop(StandardAlarm alarm, Runnable callback) {
        initIfNeeded();
        MediaPlayer player = playerPerAlarm.get(alarm);
        if (player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.stop();
            return false;
        } else {
            player.play();
            Runnable atEndCallback = () -> {
                player.setOnEndOfMedia(null);
                player.setOnStopped(null);
                player.setOnHalted(null);
                callback.run();
            };
            player.setOnEndOfMedia(atEndCallback);
            player.setOnStopped(atEndCallback);
            player.setOnHalted(atEndCallback);
            return true;
        }
    }

    public enum StandardAlarm {
        ALARM1("alarm1.wav", "calendar.plugin.alarm1.name"),
        ALARM2("alarm2.wav", "calendar.plugin.alarm2.name"),
        ALARM3("alarm3.wav", "calendar.plugin.alarm3.name");

        private final String filename, nameId;

        StandardAlarm(String filename, String nameId) {
            this.filename = filename;
            this.nameId = nameId;
        }

        public String getNameId() {
            return nameId;
        }
    }
}
