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

package scripts.imagedictionaries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.utils.FluentHashMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static org.lifecompanion.util.LCUtils.convertTo32Argb;
import static org.lifecompanion.util.LCUtils.isEgalsTo;
import static org.lifecompanion.framework.commons.utils.io.FileNameUtils.getExtension;
import static org.lifecompanion.framework.commons.utils.io.FileNameUtils.getNameWithoutExtension;
import static org.lifecompanion.framework.commons.utils.io.IOUtils.fileSha256HexToString;

/**
 * @author Mathieu THEBAUD
 */
public class ImageDictionariesCreationScript {

    private static final int WIDTH = 400;
    private static final int THRESHOLD = 60;
    private static final int SIM_SQUARE_SIZE = 80;
    private static final double SIM_THRESHOLD = 0.95;

    private static final String LANGUAGE_CODE = "fr";

    private static final long MAX_COUNT = 100_000;

    private static final Gson GSON = new GsonBuilder()//
            //.setPrettyPrinting()//
            .create();


    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        DicInfo arasaac = new DicInfo(
                new ImageDictionary("ARASAAC", "image.dictionary.description.arasaac", "image.dictionary.author.arasaac",
                        "png", "http://www.arasaac.org/", false),
                new File("D:\\ARASAAC\\FR_Pictogrammes_couleur"), "arasaac", true, true, true, false, false, false);
        DicInfo sclera = new DicInfo(
                new ImageDictionary("SCLERA", "image.dictionary.description.sclera", "image.dictionary.author.sclera",
                        "png", "https://www.sclera.be/fr/picto/overview", false),
                new File("D:\\ARASAAC\\picto_fr\\francais"), "sclera", false, false, false, true, false, false);
        DicInfo parlerPicto = new DicInfo(
                new ImageDictionary("Parler Pictos", "image.dictionary.description.parlerpictos", "image.dictionary.author.parlerpictos",
                        "png", "http://recitas.ca/parlerpictos/", false),
                new File("D:\\ARASAAC\\Parlerpictos_sc\\All"), "parlerpictos", false, true, true, true, true, true);

        //        generateImageDictionary(sclera);
        //        generateImageDictionary(arasaac);

        generateImageDictionary(parlerPicto);
    }

    private static void generateImageDictionary(DicInfo dictionaryInformation) throws FileNotFoundException, UnsupportedEncodingException {
        File outputDir = new File("D:\\ARASAAC\\OUT\\" + dictionaryInformation.dicId);
        outputDir.mkdirs();

        Map<String, List<Pair<File, String>>> imageFileAndNamesByHash = new ConcurrentHashMap<>();

        boolean[][] similarityReference = dictionaryInformation.checkSim ? getImageMultiplePart(new File(dictionaryInformation.inputdir.getPath() + File.separator + "abeilles.png")) : null;

        // Detect all image, excluding doubles + similarity
        File[] files = dictionaryInformation.inputdir.listFiles();
        LoggingProgressIndicator pi = new LoggingProgressIndicator(Math.min(MAX_COUNT, files.length), "Image search");
        Arrays.stream(files).limit(MAX_COUNT).parallel().forEach(file -> {
            pi.increment();
            if (getExtension(file.getPath()).equalsIgnoreCase(dictionaryInformation.dictionary.imageExtension) && (!dictionaryInformation.deleteNB || !file.getName().contains("_NB"))) {
                if (!dictionaryInformation.checkSim || percentEgals(getImageMultiplePart(file), similarityReference) < SIM_THRESHOLD) {
                    try {
                        String fileSha256HexToString = fileSha256HexToString(file);
                        imageFileAndNamesByHash.computeIfAbsent(fileSha256HexToString, k -> new CopyOnWriteArrayList<>()).add(new Pair<>(file, getNameWithoutExtension(file)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        System.out.println("Found " + imageFileAndNamesByHash.size() + " uniques images (" + files.length + " files in the directory)");

        // Handle images > create transparent images
        LoggingProgressIndicator pi2 = new LoggingProgressIndicator(Math.min(files.length, imageFileAndNamesByHash.entrySet().size()), "Image conversion");
        List<ImageElement> images = imageFileAndNamesByHash.entrySet().parallelStream().limit(MAX_COUNT).map(entry -> {
            if (dictionaryInformation.logDouble && entry.getValue().size() > 1) {
                System.out.println("Double detected for " + entry.getValue().stream().map(Pair::getValue).collect(Collectors.joining(", ")));
            }
            File inputImageFile = entry.getValue().get(0).getKey();
            try (InputStream fis = new BufferedInputStream(new FileInputStream(inputImageFile))) {
                // Read and resize
                Image inputImage = new Image(fis, dictionaryInformation.resize ? WIDTH : -1, -1, true, true);
                // Delete label when needed
                inputImage = dictionaryInformation.deleteImageIntegratedLabel ? deleteLabelInImage(inputImage) : inputImage;
                // Replace background when needed
                inputImage = dictionaryInformation.replaceBackground ? replaceColorInImage(inputImage, Color.WHITE, Color.TRANSPARENT, THRESHOLD) : inputImage;

                // Save to temp
                BufferedImage buffImage = SwingFXUtils.fromFXImage(inputImage, null);
                File tempOutputImage = File.createTempFile("lcimage", "." + dictionaryInformation.dictionary.imageExtension);
                ImageIO.write(buffImage, dictionaryInformation.dictionary.imageExtension, tempOutputImage);

                // Hash and copy to final directory
                String sha256 = fileSha256HexToString(tempOutputImage);
                IOUtils.copyFiles(tempOutputImage, new File(outputDir.getPath() + File.separator + sha256 + "." + dictionaryInformation.dictionary.imageExtension));
                pi2.increment();

                // Create associated element
                ImageElement imageElement = new ImageElement();
                imageElement.id = sha256;
                imageElement.keywords = FluentHashMap.map(LANGUAGE_CODE,
                        entry.getValue().stream().map(Pair::getValue).map(s -> s.replaceAll("_\\d+", " ").replace('_', ' ').replace('-', ' ').trim().toLowerCase()).toArray(l -> new String[l]));
                imageElement.name = imageElement.keywords.get(LANGUAGE_CODE)[0];
                return imageElement;
            } catch (Exception e) {
                System.out.println("Problem with : " + inputImageFile);
                e.printStackTrace();
                return null;
            }
        }).filter(i -> i != null).collect(Collectors.toList());
        System.out.println("Real value count : " + imageFileAndNamesByHash.size() + " vs " + files.length);

        dictionaryInformation.dictionary.images = images;

        // Save dictionary
        try (PrintWriter pw = new PrintWriter(new File(outputDir.getParentFile() + File.separator + dictionaryInformation.dicId + ".json"), "UTF-8")) {
            GSON.toJson(dictionaryInformation.dictionary, pw);
        }
    }

    private final static int ARGB_BLACK = convertTo32Argb(Color.BLACK);
    private final static int ARGB_WHITE = convertTo32Argb(Color.WHITE);

    private static boolean[][] getImageMultiplePart(File filePath) {
        WritableImage outputImage = new WritableImage(SIM_SQUARE_SIZE, SIM_SQUARE_SIZE);
        PixelWriter writer = outputImage.getPixelWriter();

        boolean[][] specificPart = new boolean[SIM_SQUARE_SIZE][SIM_SQUARE_SIZE];
        try (InputStream fis = new BufferedInputStream(new FileInputStream(filePath))) {
            Image inputImage = new Image(fis);
            int width = (int) inputImage.getWidth();
            PixelReader reader = inputImage.getPixelReader();
            for (int x = 0; x < SIM_SQUARE_SIZE; x++) {
                for (int y = 0; y < SIM_SQUARE_SIZE; y++) {
                    int argb = reader.getArgb(width - SIM_SQUARE_SIZE + x, y);
                    int r = argb >> 16 & 0xFF;
                    int g = argb >> 8 & 0xFF;
                    int b = argb & 0xFF;
                    specificPart[x][y] = (r + g + b) / 3 < 60;
                    writer.setArgb(x, y, specificPart[x][y] ? ARGB_BLACK : ARGB_WHITE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return specificPart;
    }

    private static double percentEgals(boolean[][] tab1, boolean[][] tab2) {
        int egalCount = 0;
        for (int x = 0; x < tab1.length; x++) {
            for (int y = 0; y < tab1.length; y++) {
                egalCount += (tab1[x][y] == tab2[x][y]) ? 1 : 0;
            }
        }
        return (egalCount * 1.0) / (tab1.length * tab1.length);
    }

    public static Image deleteLabelInImage(final Image inputImage) {
        int imgWidth = (int) inputImage.getWidth();
        int imgHeight = (int) inputImage.getHeight();
        int blank = convertTo32Argb(Color.WHITE);

        WritableImage outputImage = new WritableImage(imgWidth, imgHeight);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        // First copy the image
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                writer.setArgb(x, y, x < 177 && y > 385 ? blank : reader.getArgb(x, y));
            }
        }

        return outputImage;
    }


    public static Image replaceColorInImage(final Image inputImage, final Color colorToReplace, final Color replacingColor, final int threshold) {
        int wO = colorToReplace == null ? 255 : (int) (colorToReplace.getOpacity() * 255);
        int wR = colorToReplace == null ? 255 : (int) (colorToReplace.getRed() * 255);
        int wG = colorToReplace == null ? 255 : (int) (colorToReplace.getGreen() * 255);
        int wB = colorToReplace == null ? 255 : (int) (colorToReplace.getBlue() * 255);
        int destArgb = convertTo32Argb(replacingColor != null ? replacingColor : Color.TRANSPARENT);

        int imgWidth = (int) inputImage.getWidth();
        int imgHeight = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(imgWidth, imgHeight);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        // First copy the image
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                writer.setArgb(x, y, reader.getArgb(x, y));
            }
        }

        Set<Coord> explored = new HashSet<>();
        LinkedList<Coord> toExplore = new LinkedList<>();

        for (int i = 0; i < Math.max(imgWidth, imgHeight); i++) {
            if (i < imgWidth) {
                toExplore.add(Coord.of(i, 0));
                toExplore.add(Coord.of(i, imgHeight - 1));
            }
            if (i < imgHeight) {
                toExplore.add(Coord.of(0, i));
                toExplore.add(Coord.of(imgWidth - 1, i));
            }
        }

        // Explore
        while (!toExplore.isEmpty()) {
            Coord coordToExplore = toExplore.pop();
            if (!explored.contains(coordToExplore)) {
                explored.add(coordToExplore);
                // Check color
                int argb = reader.getArgb(coordToExplore.x, coordToExplore.y);
                int o = argb >> 24 & 0xFF;
                int r = argb >> 16 & 0xFF;
                int g = argb >> 8 & 0xFF;
                int b = argb & 0xFF;
                if (isEgalsTo(o, wO, threshold) && isEgalsTo(r, wR, threshold) && isEgalsTo(g, wG, threshold) && isEgalsTo(b, wB, threshold)) {
                    argb = destArgb;
                    coordToExplore.addToExplore(toExplore, imgWidth, imgHeight);
                } else {
                    argb = convertTo32Argb(new Color(r / 255.0, g / 255.0, b / 255.0, 0.3));
                }
                writer.setArgb(coordToExplore.x, coordToExplore.y, argb);
            }
        }
        return outputImage;
    }


}
