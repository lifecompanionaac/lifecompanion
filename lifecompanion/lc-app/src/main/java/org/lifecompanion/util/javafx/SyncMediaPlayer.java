package org.lifecompanion.util.javafx;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SyncMediaPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncMediaPlayer.class);

    private final CopyOnWriteArrayList<Pair<Runnable, MediaPlayer>> players;

    public SyncMediaPlayer() {
        this.players = new CopyOnWriteArrayList<>();
    }

    public void playSync(File audioFile) throws Exception {
        play(audioFile, null, true);
    }

    public void play(File audioFile, Consumer<MediaPlayer> mediaPlayerInitializer, boolean sync) throws Exception {
        final MediaPlayer mediaPlayer = new MediaPlayer(new Media(audioFile.toURI().toURL().toString()));
        if (mediaPlayerInitializer != null) {
            mediaPlayerInitializer.accept(mediaPlayer);
        }
        final CountDownLatch countDownLatch = sync ? new CountDownLatch(1) : null;
        AtomicReference<Pair<Runnable, MediaPlayer>> playerRef = new AtomicReference<>();
        playerRef.set(Pair.of(() -> {
            players.remove(playerRef.get());
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            mediaPlayer.dispose();
        }, mediaPlayer));

        mediaPlayer.setOnEndOfMedia(playerRef.get().getLeft());
        mediaPlayer.setOnStopped(playerRef.get().getLeft());
        mediaPlayer.setOnHalted(playerRef.get().getLeft());
        mediaPlayer.setOnError(() -> {
            LOGGER.error("Error while playing sound : {}", audioFile, mediaPlayer.getError());
            playerRef.get().getLeft().run();
        });

        players.add(playerRef.get());
        mediaPlayer.play();

        // Wait if sync
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Exception e) {
                LOGGER.error("Can't wait for player to finish", e);
            }
        }
    }

    public void stopAllPlaying() {
        ArrayList<Pair<Runnable, MediaPlayer>> allPlayers = new ArrayList<>(players);
        for (Pair<Runnable, MediaPlayer> player : allPlayers) {
            try {
                player.getRight().stop();
                player.getLeft().run();
            } catch (Throwable t) {
                LOGGER.error("Could not stop media player", t);
            }
        }
    }
}
