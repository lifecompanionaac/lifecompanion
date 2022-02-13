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

package org.lifecompanion.util.javafx;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static org.lifecompanion.util.LangUtils.isEgalsTo;

public class ImageUtils {

    // IMAGE TRANSFORM
    //========================================================================

    /**
     * Replace a color in a given image
     *
     * @param inputImage     the input image
     * @param colorToReplace the color to replace (with a threshold)
     * @param replacingColor the color replacing the colorToReplace
     * @param threshold      the threshold to detect the color to replace
     * @return the image with color replaced by a transparent white
     */
    public static Image replaceColorInImage(final Image inputImage, final Color colorToReplace, final Color replacingColor, final int threshold) {
        int wO = colorToReplace == null ? 255 : (int) (colorToReplace.getOpacity() * 255);//Issue #186, opacity should be tested
        int wR = colorToReplace == null ? 255 : (int) (colorToReplace.getRed() * 255);
        int wG = colorToReplace == null ? 255 : (int) (colorToReplace.getGreen() * 255);
        int wB = colorToReplace == null ? 255 : (int) (colorToReplace.getBlue() * 255);
        //colorToReplace.getOpacity();
        int destArgb = convertTo32Argb(replacingColor != null ? replacingColor : Color.TRANSPARENT);
        int imgWidth = (int) inputImage.getWidth();
        int imgHeight = (int) inputImage.getHeight();
        WritableImage outputImage = new WritableImage(imgWidth, imgHeight);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                int argb = reader.getArgb(x, y);
                int o = argb >> 24 & 0xFF;
                int r = argb >> 16 & 0xFF;
                int g = argb >> 8 & 0xFF;
                int b = argb & 0xFF;
                if (isEgalsTo(o, wO, threshold) && isEgalsTo(r, wR, threshold) && isEgalsTo(g, wG, threshold)
                        && isEgalsTo(b, wB, threshold)) {
                    argb = destArgb;
                }
                writer.setArgb(x, y, argb);
            }
        }
        return outputImage;
    }

    public static int convertTo32Argb(final Color c) {
        int a = (int) Math.round(c.getOpacity() * 255);
        int r = (int) Math.round(c.getRed() * 255);
        int g = (int) Math.round(c.getGreen() * 255);
        int b = (int) Math.round(c.getBlue() * 255);
        return a << 24 | r << 16 | g << 8 | b;
    }

    /**
     * Return a view port to contains the full image without any border and without breaking the ratio
     *
     * @param image the image we want to create the view port
     * @return the view port, or null if given image is null
     */
    public static Rectangle2D computeFullImageViewPort(final Image image) {
        if (image != null) {
            double minSize = Math.min(image.getWidth(), image.getHeight());
            Rectangle2D viewPort = new Rectangle2D(0.0, 0.0, minSize, minSize);
            return viewPort;
        } else {
            return null;
        }
    }
}
