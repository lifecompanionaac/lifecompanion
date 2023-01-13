package scripts;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class IconUniqueFileGenerator extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static final File DEST_SVG = new File("E:\\dev-lifecompanion\\icon-wip\\extraction-manuelle-svg");
    private static final File DEST_PNG = new File("E:\\dev-lifecompanion\\icon-wip\\extraction-manuelle-png");
    private static final File REF_DIR = new File("E:\\dev-lifecompanion\\workspace\\lifecompanion\\lifecompanion\\lc-app\\src\\main\\resources\\icons\\");
    private static final File SVG_DIR = new File("E:\\dev-lifecompanion\\workspace\\lifecompanion\\res\\icons\\lc-app\\");

    private File pngFile;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // exportAll(SVG_DIR, new File("E:\\dev-lifecompanion\\icon-wip\\export"), SVG_DIR);


        Set<String> fromRefDir = new HashSet<>();
        getRelativePathsFrom(fromRefDir, REF_DIR, REF_DIR);
        Set<String> fromExportDir = new HashSet<>();
        getRelativePathsFrom(fromExportDir, new File("E:\\dev-lifecompanion\\icon-wip\\export"), new File("E:\\dev-lifecompanion\\icon-wip\\export"));

        fromRefDir.stream().filter(p -> !fromExportDir.contains(p)).forEach(f -> {
            System.out.println("Missing icon : " + f);
        });

        fromExportDir.stream().filter(p -> !fromRefDir.contains(p)).forEach(f -> {
            System.out.println("Invalid icon : " + f);
        });

        System.exit(0);

        primaryStage.setWidth(400);
        primaryStage.setHeight(200);
        primaryStage.setAlwaysOnTop(true);

        Label labelPng = new Label();
        labelPng.setWrapText(true);

        Label labelResult = new Label();


        Button buttonPng = new Button("1 - PNG");
        buttonPng.setOnAction(e -> {
            pngFile = Clipboard.getSystemClipboard().getFiles().get(0);
            labelPng.setText(pngFile.getName());
        });

        Button buttonSvg = new Button("2 - SVG");
        buttonSvg.setOnAction(e -> {
            generate(labelResult);
        });

        VBox box = new VBox(10.0, labelPng, buttonPng, buttonSvg, labelResult);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10.0));
        primaryStage.setScene(new Scene(box));
        primaryStage.show();
    }

    private void getRelativePathsFrom(Set<String> paths, File root, File file) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                getRelativePathsFrom(paths, root, listFile);
            }
        } else {
            paths.add(IOUtils.getRelativePath(file.getAbsolutePath(), root.getAbsolutePath()));
        }
    }

    private void exportAll(File rootSrc, File rootDest, File file) throws Exception {
        System.out.println(file + "\n\tisDirectory() = " + file.isDirectory());
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                exportAll(rootSrc, rootDest, listFile);
            }
        } else if (file.getName().endsWith("svg")) {
            System.out.println(file + "\n\t" + rootSrc);
            String relativePathSrc = IOUtils.getRelativePath(file.getAbsolutePath(), rootSrc.getAbsolutePath());
            File out = new File(new File(rootDest.getAbsolutePath() + "/" + relativePathSrc).getParentFile().getAbsolutePath() + "/" + FileNameUtils.getNameWithoutExtension(file) + ".png");
            out.getParentFile().mkdirs();
            final Process inkscape = new ProcessBuilder()
                    .command("D:\\Programmes\\Inkscape\\bin\\inkscape.exe",
                            file.getAbsolutePath(),
                            "--export-filename=" + out,
                            "--export-width=32")
                    .redirectError(ProcessBuilder.Redirect.INHERIT).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
            inkscape.waitFor();
        }
    }


    private void generate(Label labelResult) {
        try {
            Set<DataFormat> contentTypes = Clipboard.getSystemClipboard().getContentTypes();
            for (DataFormat contentType : contentTypes) {
                if (contentType.getIdentifiers().contains("image/x-inkscape-svg")) {
                    String relativePathWithoutName = IOUtils.getRelativePath(pngFile.getParentFile().getAbsolutePath(), REF_DIR.getAbsolutePath());

                    ByteBuffer byteBuffer = (ByteBuffer) Clipboard.getSystemClipboard().getContent(contentType);
                    File destFile = new File(DEST_SVG.getPath() + "/" + relativePathWithoutName + "/" + FileNameUtils.getNameWithoutExtension(pngFile) + ".svg");
                    destFile.getParentFile().mkdirs();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(destFile)) {
                        fileOutputStream.write(byteBuffer.array());
                    }
                    File destPng = new File(DEST_PNG.getPath() + "/" + relativePathWithoutName + "/" + FileNameUtils.getNameWithoutExtension(pngFile) + ".png");
                    destPng.getParentFile().mkdirs();
                    final Process inkscape = new ProcessBuilder()
                            .command("D:\\Programmes\\Inkscape\\bin\\inkscape.exe",
                                    destFile.getAbsolutePath(),
                                    "--export-filename=" + destPng.getPath(),
                                    "--export-width=128")
                            .redirectError(ProcessBuilder.Redirect.INHERIT).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
                    inkscape.waitFor();
                    labelResult.setText(destFile.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
