package scripts.imagedictionaries;

import org.imgscalr.Scalr;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.predict4all.nlp.utils.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ArasaacUpdater {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("000,000");
    private static final DecimalFormat MATCH_FORMAT = new DecimalFormat("000000");

    static final boolean CLEAN_PREPARED = true;

    static final boolean SAVE_ALL = false;

    static final double K1 = 0.01;
    static final double K2 = 0.03;
    static final int L = 255; // 8-bit images
    static final double C1 = (K1 * L) * (K1 * L);
    static final double C2 = (K2 * L) * (K2 * L);
    static final int IMAGE_SIZE = 50;

    static final String v1 = "v1.png";
    static final String dir = "C:\\Users\\Mathieu\\Desktop\\TMP\\ARASAAC-update\\";


    public static void main(String[] args) throws IOException {

        File outputDir = createAndClean(dir + "/output");

        AtomicInteger count = new AtomicInteger();

        File[] v1Images = new File(dir + "v1/").listFiles();
        File[] v2Images = new File(dir + "v2/").listFiles();

        File v1Prepared = createAndClean(dir + "v1-prepared/", CLEAN_PREPARED);
        File v2Prepared = createAndClean(dir + "v2-prepared/", CLEAN_PREPARED);

        ConcurrentHashMap<File, File> v1Map = new ConcurrentHashMap<>();
        ConcurrentHashMap<File, File> v2Map = new ConcurrentHashMap<>();

        // Resize + grayscale to cache
        LoggingProgressIndicator lpiPrepare = new LoggingProgressIndicator(v1Images.length + v2Images.length, "Image prepare");
        Arrays.stream(v1Images).parallel().forEach(image -> {
            v1Map.put(prepare(image, v1Prepared), image);
            lpiPrepare.increment();
        });
        Arrays.stream(v2Images).parallel().forEach(image -> {
            v2Map.put(prepare(image, v2Prepared), image);
            lpiPrepare.increment();
        });

        System.out.println("TOTAL COMPUTE " + (v1Images.length * v2Images.length));
        long start = System.currentTimeMillis();

        LoggingProgressIndicator lpiCompute = new LoggingProgressIndicator(v1Images.length * v2Images.length, "Image compare");

        Arrays.stream(v1Prepared.listFiles()).parallel().forEach(v1Image -> {
            // Score to each image
            CopyOnWriteArrayList<Pair<File, Double>> results = new CopyOnWriteArrayList<>();
            try {
                BufferedImage originalImage = ImageIO.read(v1Image);
                Arrays.stream(v2Prepared.listFiles()).parallel().forEach(image -> {
                    try {
                        BufferedImage image2 = ImageIO.read(image);
                        double score = computeSSIM(originalImage, image2);
                        results.add(Pair.of(image, score));
                        lpiCompute.increment();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Find best matching
            try {
                int i = count.incrementAndGet();
                IOUtils.copyFiles(v1Map.get(v1Image), new File(outputDir + "/" + MATCH_FORMAT.format(i) + "_V1.png"));
                // TODO : percent > 60/65
                if (SAVE_ALL) {
                    results.stream().parallel().forEach(sndImage -> {
                        try {
                            IOUtils.copyFiles(v2Map.get(sndImage.getLeft()), new File(outputDir + "/" + MATCH_FORMAT.format(i) + "_V2_" + DECIMAL_FORMAT.format(sndImage.getRight() * 100_000) + ".png"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    Pair<File, Double> bestMatching = results.stream().parallel().max(Comparator.comparingDouble(Pair::getRight)).get();
                    IOUtils.copyFiles(v2Map.get(bestMatching.getLeft()), new File(outputDir + "/" + MATCH_FORMAT.format(i) + "_V2_" + DECIMAL_FORMAT.format(bestMatching.getRight() * 100_000) + ".png"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("TOTAL TIME " + ((System.currentTimeMillis() - start) / 1000.0) + " s");
    }

    private static File createAndClean(String path) {
        return createAndClean(path, true);
    }

    private static File createAndClean(String path, boolean clean) {
        File dir = new File(path);
        dir.mkdirs();
        if (clean) Arrays.stream(dir.listFiles()).forEach(File::delete);
        return dir;
    }

    private static File prepare(File image, File dir) {
        try {
            File destFile = new File(dir + "/" + image.getName());
            if (!destFile.exists()) {
                ImageIO.write(toGrayscale(Scalr.resize(ImageIO.read(image), IMAGE_SIZE)), "png", destFile);
            }
            return destFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void findMatching() throws IOException {
        // Read original image
        BufferedImage originalImage = toGrayscale(Scalr.resize(ImageIO.read(new File(dir + v1)), 300));

        // Read all file in folder
        File[] images = new File(dir + "v2/").listFiles();
        LoggingProgressIndicator loggingProgressIndicator = new LoggingProgressIndicator(images.length, "Image compare");
        CopyOnWriteArrayList<Pair<File, Double>> results = new CopyOnWriteArrayList<>();
        Arrays.stream(images).parallel().forEach(image -> {
            try {
                BufferedImage image2 = toGrayscale(Scalr.resize(ImageIO.read(image), 200));
                double score = computeSSIM(originalImage, image2);
                results.add(Pair.of(image, score));
                loggingProgressIndicator.increment();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Scores output
        File outputDir = createAndClean(dir + "/output");

        LoggingProgressIndicator lpiCopy = new LoggingProgressIndicator(results.size(), "Image copy");
        results.forEach(p -> {
            File output = new File(outputDir.getPath() + "/" + DECIMAL_FORMAT.format(p.getRight() * 100_000.0) + ".png");
            try {
                IOUtils.copyFiles(p.getLeft(), output);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            lpiCopy.increment();
        });
    }

    public static BufferedImage toGrayscaleOld(BufferedImage img) {
        BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        gray.getGraphics().drawImage(img, 0, 0, null);
        return gray;
    }

    public static BufferedImage toGrayscale(BufferedImage bi) {
        int[] pixels = bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), null, 0, bi.getWidth());
        for (int i = 0; i < pixels.length; i++) {
            int color = pixels[i];
            int a = (color >> 24) & 255;
            int r = (color >> 16) & 255;
            int g = (color >> 8) & 255;
            int b = (color) & 255;
            if (a <= 100) {
                a = 255;
                r = 255;
                g = 255;
                b = 255;
            }
            pixels[i] = (a << 24) | (r << 16) | (g << 8) | (b);
        }
        BufferedImage biOut = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
        biOut.setRGB(0, 0, bi.getWidth(), bi.getHeight(), pixels, 0, bi.getWidth());
        return toGrayscaleOld(biOut);
    }


    public static double computeSSIM(BufferedImage img1, BufferedImage img2) {
        final int width = img1.getWidth();
        final int height = img1.getHeight();

        Raster r1 = img1.getRaster();
        Raster r2 = img2.getRaster();

        double sumX = 0;
        double sumY = 0;
        double sumX2 = 0;
        double sumY2 = 0;
        double sumXY = 0;

        int N = width * height;

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double px = r1.getSample(i, j, 0);
                double py = r2.getSample(i, j, 0);

                sumX += px;
                sumY += py;
                sumX2 += px * px;
                sumY2 += py * py;
                sumXY += px * py;
            }
        }

        double meanX = sumX / N;
        double meanY = sumY / N;
        double varianceX = (sumX2 - (sumX * sumX) / N) / (N - 1);
        double varianceY = (sumY2 - (sumY * sumY) / N) / (N - 1);
        double covarianceXY = (sumXY - (sumX * sumY) / N) / (N - 1);

        double numerator = (2 * meanX * meanY + C1) * (2 * covarianceXY + C2);
        double denominator = (meanX * meanX + meanY * meanY + C1) * (varianceX + varianceY + C2);

        return numerator / denominator;
    }
}
