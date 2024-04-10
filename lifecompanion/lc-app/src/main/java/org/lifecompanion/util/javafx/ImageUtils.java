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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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

    /**
     * Removes the background from a given image using an approach based on the colour threshold and the BFS algorithm.
     *
     * This method works in several stages:
     * 1. it first determines the background colour by analysing the colours present at the edges of the image.
     * 2. It initializes a queue with the edge points of the image that have the same colour as the background.
     * 3. It then enters a while loop that continues until the queue is empty.
     * In this loop, it removes a point from the queue, examines its neighbours (points to the left, right, top and bottom), and if a neighbour has the same colour as the background, it is added to the queue.
     * 4 Finally, it scans all the points in the image and makes transparent those that have been visited (i.e. those that are part of the background), leaving the image without a background.
     *
     * This approach is effective for images with a background of a uniform colour that is distinct from the main object in the image.
     *
     * @param inputImage the input image
     * @param threshold the threshold to detect the color to replace
     * @author Oscar PAVOINE
     */
    // TODO : optimize performance and object creation
    public static Image removeBackground(final Image inputImage, final int threshold) {
        int imgWidth = (int) inputImage.getWidth();
        int imgHeight = (int) inputImage.getHeight();

        PixelReader reader = inputImage.getPixelReader();
        WritableImage outputImage = new WritableImage(imgWidth, imgHeight);
        PixelWriter writer = outputImage.getPixelWriter();

        Color backgroundColor = findBackgroundColor(reader, imgWidth, imgHeight);
        int backgroundO = backgroundColor == null ? 255 : (int) (backgroundColor.getOpacity() * 255);//Issue #186, opacity should be tested
        int backgroundR = backgroundColor == null ? 255 : (int) (backgroundColor.getRed() * 255);
        int backgroundG = backgroundColor == null ? 255 : (int) (backgroundColor.getGreen() * 255);
        int backgroundB = backgroundColor == null ? 255 : (int) (backgroundColor.getBlue() * 255);

        Color currentColour;
        int currentO = 0;
        int currentR = 0;
        int currentG = 0;
        int currentB = 0;
        List<Point> edgePoints = new LinkedList<>();
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                if (y == 0 || y == imgHeight - 1 || x == 0 || x == imgWidth - 1) {
                    edgePoints.add(new Point(x, y));
                }
            }
        }

        Queue<Point> queueNeighbours = new LinkedList<>();
        boolean[][] pointVisited = new boolean[imgWidth][imgHeight];
        boolean[][] InvalidVisitedPoint = new boolean[imgWidth][imgHeight];
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        Point startPoint = findPixelStart(backgroundO, backgroundR, backgroundG, backgroundB, reader, threshold, imgWidth, imgHeight);
        queueNeighbours.add(startPoint);
        pointVisited[startPoint.x][startPoint.y] = true;

        while (!queueNeighbours.isEmpty()) {
            Point point = queueNeighbours.poll();
            for (int[] direction : directions) {
                int newX = point.x + direction[0];
                int newY = point.y + direction[1];

                if (newX >= 0 && newX < imgWidth && newY >= 0 && newY < imgHeight && !pointVisited[newX][newY] && !InvalidVisitedPoint[newX][newY]) {
                    currentColour = reader.getColor(newX, newY);
                    currentO = currentColour == null ? 255 : (int) (currentColour.getOpacity() * 255);//Issue #186, opacity should be tested
                    currentR = currentColour == null ? 255 : (int) (currentColour.getRed() * 255);
                    currentG = currentColour == null ? 255 : (int) (currentColour.getGreen() * 255);
                    currentB = currentColour == null ? 255 : (int) (currentColour.getBlue() * 255);
                    if (isEgalsTo(backgroundO, currentO, threshold) && isEgalsTo(backgroundR, currentR, threshold) && isEgalsTo(backgroundG, currentG, threshold) && isEgalsTo(backgroundB, currentB, threshold)) {
                        queueNeighbours.add(new Point(newX, newY));
                        pointVisited[newX][newY] = true;
                        writer.setColor(newX, newY, Color.TRANSPARENT);
                    } else {
                        Color originalColor = reader.getColor(newX, newY);
                        Color borderColor = Color.color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), 0.3);
                        InvalidVisitedPoint[newX][newY] = true;
                        writer.setColor(newX, newY,borderColor);
                    }
                }
            }

            if (queueNeighbours.isEmpty()) {
                for (Point edgePoint : edgePoints) {
                    currentColour = reader.getColor(edgePoint.x, edgePoint.y);
                    currentO = currentColour == null ? 255 : (int) (currentColour.getOpacity() * 255);//Issue #186, opacity should be tested
                    currentR = currentColour == null ? 255 : (int) (currentColour.getRed() * 255);
                    currentG = currentColour == null ? 255 : (int) (currentColour.getGreen() * 255);
                    currentB = currentColour == null ? 255 : (int) (currentColour.getBlue() * 255);
                    if (!pointVisited[edgePoint.x][edgePoint.y] && isEgalsTo(backgroundO, currentO, threshold) && isEgalsTo(backgroundR, currentR, threshold) && isEgalsTo(backgroundG, currentG, threshold) && isEgalsTo(backgroundB, currentB, threshold)) {
                        queueNeighbours.add(edgePoint);
                        pointVisited[edgePoint.x][edgePoint.y] = true;
                        writer.setColor(edgePoint.x, edgePoint.y, Color.TRANSPARENT);
                        break;
                    }
                }
            }
        }

         for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                if (!pointVisited[x][y] && !InvalidVisitedPoint[x][y]) {
                    writer.setColor(x, y, reader.getColor(x, y));
                }
            }
        }

        return outputImage;
    }

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private  static Color findBackgroundColor(PixelReader reader, double imgWidth, double imgHeight) {
        int edgeWidth = (int) (imgWidth * 0.05);
        int edgeHeight = (int) (imgHeight * 0.05);

        Map<Color, Integer> colorFrequency = new HashMap<>();
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                if (y < edgeHeight || y >= imgHeight - edgeHeight || x < edgeWidth || x >= imgWidth - edgeWidth) {
                    Color color = reader.getColor(x, y);
                    colorFrequency.put(color, colorFrequency.getOrDefault(color, 0) + 1);
                }
            }
        }

        Color backgroundColor = null;
        int maxFrequency = -1;
        for (Map.Entry<Color, Integer> entry : colorFrequency.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                backgroundColor = entry.getKey();
                maxFrequency = entry.getValue();
            }
        }
        return backgroundColor;
    }

    private static Point findPixelStart( int wO, int wR, int wG, int wB, PixelReader reader, int threshold, int imgWidth, int imgHeight) {
        boolean start = false;
        int retX = 0;
        int retY = 0;
        for (int x = 0; x < imgWidth && !start; x++) {
            if (isSameColor(wO, wR, wG, wB, reader.getArgb(x, 0), threshold)) {
                retX = x;
                retY = 0;
                start = true;
            } else if (isSameColor(wO, wR, wG, wB, reader.getArgb(x, imgHeight - 1), threshold)) {
                retX = x;
                retY = imgHeight - 1;
                start = true;
            }
        }

        for (int y = 0; y < imgHeight && !start; y++) {
            if (isSameColor(wO, wR, wG, wB, reader.getArgb(0, y), threshold)) {
                retX = 0;
                retY = y;
                start = true;
            } else if (isSameColor(wO, wR, wG, wB, reader.getArgb(imgWidth - 1, y), threshold)) {
                retX = imgWidth - 1;
                retY = y;
                start = true;
            }
        }
        return new Point(retX, retY);
    }

    private static boolean isSameColor(int o1, int r1, int g1, int b1, int c2, int threshold) {
        int o2 = c2 >> 24 & 0xFF;
        int r2 = c2 >> 16 & 0xFF;
        int g2 = c2 >> 8 & 0xFF;
        int b2 = c2 & 0xFF;

        return isEgalsTo(o1, o2, threshold) && isEgalsTo(r1, r2, threshold) && isEgalsTo(g1, g2, threshold) && isEgalsTo(b1, b2, threshold);
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
