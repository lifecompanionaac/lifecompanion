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

package scripts;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class TestMedia extends Application {
    int index = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        final File[] files = new File("TODO").listFiles();
        Button buttonTest = new Button("Test");
        buttonTest.setOnAction(e -> {
            if (index >= files.length) index = 0;
            final File file = files[index++];
            Media media = new Media(file.toURI().toString());
            media.setOnError(() -> {
                final MediaException mediaException = media.getError();
                mediaException.printStackTrace();
            });
            MediaPlayer player = new MediaPlayer(media);
            player.setOnError(() -> {
                final MediaException mediaException = player.getError();
                mediaException.printStackTrace();
                System.out.println(""+mediaException.getType());
            });
            player.play();
            final Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    player.getStatus();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
            thread.start();
        });
        primaryStage.setScene(new Scene(buttonTest));
        primaryStage.show();
    }
}
