package org.lifecompanion.plugin.flirc.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.lifecompanion.plugin.flirc.ui.control.IRRecorderField.PERCENT_DECIMAL_FORMAT;

public class FlircUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlircUtils.class);

    public static int[] toIntArray(String pattern) {
        return Arrays.stream(pattern.split(",")).map(StringUtils::trimToEmpty).filter(StringUtils::isNotBlank).mapToInt(Integer::parseInt).toArray();
    }

    public static double correlationBetween(int[] a, int[] b) {
        if (a.length != b.length) {
            LOGGER.warn("IR signal doesn't have the same length : {} vs {}", a.length, b.length);
        }
        int length = Math.min(a.length, b.length);

        double meanA = (Arrays.stream(a).limit(length).sum() * 1.0) / length;
        double meanB = (Arrays.stream(b).limit(length).sum() * 1.0) / length;

        double aPowSum = 0.0;
        double bPowSum = 0.0;
        double abSum = 0.0;

        for (int i = 0; i < length; i++) {
            double valAMinMean = a[i] - meanA;
            double valBMinMean = b[i] - meanB;
            abSum += valAMinMean * valBMinMean;
            aPowSum += Math.pow(valAMinMean, 2.0);
            bPowSum += Math.pow(valBMinMean, 2.0);
        }

        return abSum / Math.sqrt(aPowSum * bPowSum);
    }

    public static void debugComparisons(List<Pair<Double, Pair<String, String>>> comparisons) {
        if (StringUtils.isNotBlank(System.getProperty("org.lifecompanion.debug.dev.env"))) {
            try {
                File destDir = new File(IOUtils.getTempDir("ir-debug") + File.separator + IOHelper.DATE_FORMAT_FILENAME_WITH_TIME_SECOND.format(new Date()));
                destDir.mkdirs();
                LOGGER.info("Debug directory :\n{}", destDir.getAbsolutePath());
                for (int i = 0; i < comparisons.size(); i++) {
                    Pair<Double, Pair<String, String>> comparison = comparisons.get(i);
                    NumberAxis xAxis = new NumberAxis();
                    NumberAxis yAxis = new NumberAxis();
                    LineChart<Number, Number> linechart = new LineChart<>(xAxis, yAxis);
                    linechart.setTitle(PERCENT_DECIMAL_FORMAT.format(100.0 * comparison.getLeft()));
                    linechart.getData().add(getSeriesFor(comparison.getRight().getLeft()));
                    linechart.getData().add(getSeriesFor(comparison.getRight().getRight()));
                    linechart.setCreateSymbols(false);
                    linechart.setLegendVisible(false);
                    SnapshotParameters snapParams = new SnapshotParameters();
                    Group group = new Group(linechart);
                    Scene scene = new Scene(group);
                    ImageIO.write(SwingFXUtils.fromFXImage(group.snapshot(snapParams, null), null), "png", new File(destDir.getPath() + File.separator + i + ".png"));
                }
                DesktopUtils.openFile(destDir);
            } catch (Throwable t) {
                LOGGER.error("Cant debug IR code compare", t);
            }
        }
    }

    private static XYChart.Series<Number, Number> getSeriesFor(String pattern) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        int[] ints = FlircUtils.toIntArray(pattern);
        for (int i = 0; i < ints.length; i++) {
            series.getData().add(new XYChart.Data<>(i, ints[i]));
        }
        return series;
    }
}
