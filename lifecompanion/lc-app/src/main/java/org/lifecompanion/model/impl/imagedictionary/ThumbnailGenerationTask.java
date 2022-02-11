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

package org.lifecompanion.model.impl.imagedictionary;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

class ThumbnailGenerationTask extends Task<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThumbnailGenerationTask.class);

    private final ImageElementI imageElement;
    private final File thumbnailPath;

    ThumbnailGenerationTask(ImageElementI imageElement, File thumbnailPath) {
        this.imageElement = imageElement;
        this.thumbnailPath = thumbnailPath;
    }

    @Override
    protected Void call() throws Exception {
        File imagePath = imageElement.getRealFilePath();
        IOUtils.createParentDirectoryIfNeeded(thumbnailPath);
        try (FileInputStream fis = new FileInputStream(imagePath)) {
            Image image = new Image(fis, ImageDictionaries.THUMBNAIL_WIDTH, ImageDictionaries.THUMBNAIL_HEIGHT, true, true);
            BufferedImage buffImage = SwingFXUtils.fromFXImage(image, null);

            //When JavaFX loading fail, load with AWT (for example, gif can't be converted to FXImage)
            if (buffImage == null) {
                java.awt.Image ci = ImageIO.read(imagePath).getScaledInstance(-1, ImageDictionaries.THUMBNAIL_HEIGHT, java.awt.Image.SCALE_SMOOTH);
                buffImage = new BufferedImage(ci.getWidth(null), ci.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = buffImage.createGraphics();
                g2d.drawImage(ci, 0, 0, null);
                g2d.dispose();
            }
            ImageIO.write(buffImage, FileNameUtils.getExtension(thumbnailPath), thumbnailPath);
        } catch (Exception e) {
            LOGGER.error("Thumbnail couldn't be generated for {}", imagePath, e);
        }
        return null;
    }
}
