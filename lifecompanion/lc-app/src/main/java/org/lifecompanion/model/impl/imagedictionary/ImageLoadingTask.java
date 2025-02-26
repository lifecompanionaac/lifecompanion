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

import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import org.lifecompanion.controller.hub.HubService;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.imagedictionary.PathSupplier;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ImageLoadingTask extends Task<Image> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageLoadingTask.class);

    private static final double IMAGE_MAX_WIDTH = 7680;
    private static final double IMAGE_MAX_HEIGHT = 4320;

    private final String imageId;
    private final ObjectProperty<Image> target;
    private final PathSupplier pathSupplier;
    private final double width;
    private final double height;
    private final boolean keepRatio;
    private final boolean smooth;
    private final Runnable callback;

    // Added as this task is finished when runOnFXThread is executed so original isCancelled wasn't working (as the task is terminated)
    private final AtomicBoolean cancelTaskAndSetTarget;

    public ImageLoadingTask(final String imageId,
                            final ObjectProperty<Image> targetP,
                            final File pathP,
                            final double widthP,
                            final double heightP,
                            final boolean keepRatioP,
                            final boolean smoothP,
                            final Runnable callbackP) {
        this(imageId, targetP, () -> pathP, widthP, heightP, keepRatioP, smoothP, callbackP);
    }

    public ImageLoadingTask(final String imageId,
                            final ObjectProperty<Image> targetP,
                            PathSupplier pathSupplier,
                            final double widthP,
                            final double heightP,
                            final boolean keepRatioP,
                            final boolean smoothP,
                            final Runnable callbackP) {
        this.imageId = imageId;
        this.target = targetP;
        this.pathSupplier = pathSupplier;
        this.width = widthP;
        this.height = heightP;
        this.keepRatio = keepRatioP;
        this.smooth = smoothP;
        this.callback = callbackP;
        cancelTaskAndSetTarget = new AtomicBoolean(false);
    }

    @Override
    public boolean isCancelled() {
        return super.isCancelled() || cancelTaskAndSetTarget.get();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        final boolean cancel = super.cancel(mayInterruptIfRunning);
        cancelTaskAndSetTarget.set(true);
        return cancel;
    }

    public File getPath() {
        try {
            return pathSupplier.getPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Image call() {
        try {
            if (!isCancelled()) {
                File path = pathSupplier.getPath();
                File imagePath;
                // Try to load from HUB when needed
                if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.USE_HUB_IMAGES) && !path.exists()) {
                    String extension = FileNameUtils.getExtension(path);
                    File imageTempCacheFolder = new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "tmp" + File.separator + "downloaded-image-cache");
                    imageTempCacheFolder.mkdirs();
                    imagePath = new File(imageTempCacheFolder + File.separator + imageId + "." + extension);
                    try {
                        if (!imagePath.exists() || StringUtils.isDifferent(imageId, IOUtils.fileSha256HexToString(imagePath))) {
                            LOGGER.info("Will try to download image {} from hub to {}", imageId, imagePath);
                            HubService.INSTANCE.downloadImageFromHub(imageId, imagePath);
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Image download {} from hub failed", imageId, e);
                    }
                } else {
                    imagePath = path;
                }

                // Load from stream
                try (FileInputStream fis = new FileInputStream(imagePath)) {
                    Image image = new Image(fis, Math.min(this.width, IMAGE_MAX_WIDTH), Math.min(this.height, IMAGE_MAX_HEIGHT), this.keepRatio, this.smooth);
                    if (!isCancelled()) {
                        FXThreadUtils.runOnFXThread(() -> {
                            if (target != null && !isCancelled()) {
                                this.target.set(image);
                            }
                        });
                        if (callback != null) {
                            this.callback.run();
                        }
                        return image;
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.info("Couldn't load the image {}", imageId, e);
        }
        return null;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public String getImageId() {
        return imageId;
    }
}
