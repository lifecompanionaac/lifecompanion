package scripts.imagedictionaries;

import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MakatonV2CreationScript {
    private final static String EXTENSION = "tiff";
    private static final int EXPORT_W = 2000, EXPORT_H = 2000, EXPORT_DPI = 300;
    private static final boolean TO_SQUARE = false;

    public static void main(String[] args) throws Exception {
        File inkscape = new File("C:\\Program Files\\Inkscape\\bin\\inkscape.com");

        File root = new File("C:\\Users\\Mathieu\\Desktop\\TMP\\2026-02-voc-makaton");

        File srcDir = new File(root + "/src");
        File outDir = new File(root + "/vocabulaire-makaton-" + EXTENSION + "-" + (TO_SQUARE ? "carre" : "libre") + "-" + EXPORT_W + "x" + EXPORT_H + "-" + EXPORT_DPI + "dpi");

        List<File> files = new ArrayList<>();
        explore(srcDir, files);

        for (File file : files) {
            File logFile = org.lifecompanion.util.IOUtils.getTempFile("makaton", ".txt");

            // Create img from AI
            String relativePath = IOUtils.getRelativePath(file.getParentFile().getAbsolutePath(), srcDir.getAbsolutePath());
            File destFile = new File(outDir.getPath() + "/" + relativePath + "/" + FileNameUtils.getNameWithoutExtension(file) + "." + EXTENSION);
            destFile.getParentFile().mkdirs();
            Process inkscapeProcess = new ProcessBuilder()
                    .command(
                            inkscape.getAbsolutePath(),
                            "--export-type=\"" + EXTENSION + "\"",
                            "--export-area-drawing",
                            "--export-dpi=" + EXPORT_DPI,
                            "--pdf-poppler",
                            "--export-filename=" + destFile.getAbsolutePath(),
                            file.getAbsolutePath()
                    )
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.to(logFile))
                    .start();
            int exitValue = inkscapeProcess.waitFor();
            System.out.println("\tinkscape : " + exitValue);

            // Center
            if (TO_SQUARE) {
                Process magickProcess = new ProcessBuilder()
                        .command(
                                "magick",
                                destFile.getAbsolutePath(),
                                "-resize",
                                EXPORT_W + "x" + EXPORT_H,
                                "-gravity",
                                "center",
                                "-background",
                                "none",
                                "-extent",
                                EXPORT_W + "x" + EXPORT_H,
                                destFile.getAbsolutePath()
                        )
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start();
                exitValue = magickProcess.waitFor();
                System.out.println("\tmagick : " + exitValue);
            }

            // magick Aller.png -resize 1000x1000 -gravity center -background none -extent 1000x1000 carre.png

            break;


        }
    }

    private static void explore(File file, List<File> files) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    explore(child, files);
                }
            }
        } else {
            String extension = FileNameUtils.getExtension(file);
            if ("ai".equals(extension))
                files.add(file);
        }
    }
}
