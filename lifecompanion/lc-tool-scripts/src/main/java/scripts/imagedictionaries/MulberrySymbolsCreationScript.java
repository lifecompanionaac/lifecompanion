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

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.lifecompanion.framework.commons.utils.io.IOUtils.fileSha256HexToString;

/**
 * @author Mathieu THEBAUD
 */
public class MulberrySymbolsCreationScript {

    private static final int WIDTH = 400;
    private static final String LANGUAGE_CODE = "fr";

    private static final Gson GSON = new GsonBuilder()//
            //.setPrettyPrinting()//
            .create();


    public static void main(String[] args) throws IOException {
        DicInfo mulberrySymbols = new DicInfo(
                new ImageDictionary("Mulberry Symbols", "image.dictionary.description.mulberry.symbols", "image.dictionary.author.mulberry.symbols",
                        "png", "https://mulberrysymbols.org/", false),
                new File("D:\\ARASAAC\\mulberry-symbols\\EN-symbols"), "mulberry-symbols", true, true, true, false, false, false);
        generateImageDictionary(mulberrySymbols);
    }

    private static void generateImageDictionary(DicInfo dictionaryInformation) throws IOException {
        File outputDir = new File("D:\\ARASAAC\\OUT\\" + dictionaryInformation.dicId);
        outputDir.mkdirs();

        List<MlSymbol> symbols = new ArrayList<>();

        // Read from CSV
        Reader in = new FileReader(dictionaryInformation.inputdir + "/../symbol-info.csv");
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
        for (CSVRecord record : records) {
            File imageFile = new File(dictionaryInformation.inputdir + "/" + record.get(1) + ".svg");
            if (imageFile.exists()) {
                symbols.add(new MlSymbol(imageFile, record.get(1), StringUtils.stripToEmpty(record.get(5)).split(" ")));
            } else {
                System.out.println("NO - FILE / symbol = " + record.get(1) + " / tags = " + record.get(5));
            }
        }
        System.out.println("Found " + symbols.size());

        // Clean input names
        symbols.parallelStream().forEach(symbol -> {
            symbol.tags = Arrays.asList(symbol.tags).stream().map(MulberrySymbolsCreationScript::cleanText).toArray(String[]::new);
            symbol.name = cleanText(symbol.name);
            if (symbol.name.endsWith(" , to")) {
                symbol.name = "to " + symbol.name.substring(0, symbol.name.lastIndexOf(" , to"));
            }
        });

        // Get / generate translations
        // generateAutoTranslation(symbols);
        final Map<String, String> translation = MulberryTranslations.getTranslation();

        LoggingProgressIndicator pi2 = new LoggingProgressIndicator(symbols.size(), "Image conversion");

        final File tempDir = LCUtils.getTempDir("export-mulberry");
        final List<ImageElement> images = symbols.parallelStream().map(symbol -> {
            final File tempSvgFile = new File(tempDir + "/" + UUID.randomUUID().toString() + ".png");
            IOUtils.createParentDirectoryIfNeeded(tempSvgFile);
            try {
                // Convert SVG > PNG with Inkscape4
                final Process inkscape = new ProcessBuilder()
                        .command("C:\\Program Files\\Inkscape\\inkscape.exe", symbol.file.getAbsolutePath(), "--export-png=" + tempSvgFile.getAbsolutePath(),
                                "--export-width=" + WIDTH)
                        .redirectError(ProcessBuilder.Redirect.INHERIT).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
                inkscape.waitFor();

                // Move file to dest
                String sha256 = fileSha256HexToString(tempSvgFile);
                IOUtils.copyFiles(tempSvgFile, new File(outputDir.getPath() + File.separator + sha256 + "." + dictionaryInformation.dictionary.imageExtension));

                // Create associated element
                ImageElement imageElement = new ImageElement();
                imageElement.id = sha256;
                final List<String> tagsAndName = new ArrayList<>(Arrays.asList(symbol.tags));
                tagsAndName.add(0, symbol.name);
                String[] translatedKeywords = tagsAndName.stream().map(t -> translation.getOrDefault(t, t)).toArray(String[]::new);
                imageElement.keywords = FluentHashMap.map(LANGUAGE_CODE, translatedKeywords).with("en", tagsAndName.toArray(String[]::new));
                imageElement.name = imageElement.keywords.get(LANGUAGE_CODE)[0];

                pi2.increment();

                return imageElement;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).filter(e -> e != null).collect(Collectors.toList());

        // Save dictionary
        dictionaryInformation.dictionary.images = images;
        try (PrintWriter pw = new PrintWriter(new File(outputDir.getParentFile() + File.separator + dictionaryInformation.dicId + ".json"), "UTF-8")) {
            GSON.toJson(dictionaryInformation.dictionary, pw);
        }
    }

    @NotNull
    private static String cleanText(String s) {
        return s.replaceAll("_\\d+", " ").replace('_', ' ').replace('-', ' ').toLowerCase().replaceAll("\\s{2,}", " ").trim();
    }

    static class MlSymbol {
        private File file;
        private String name;
        private String[] tags;

        public MlSymbol(File file, String name, String[] tags) {
            this.file = file;
            this.name = name;
            this.tags = tags;
        }
    }

    // CONFIGURATION : GOOGLE_APPLICATION_CREDENTIALS to credentials json (ex : E:\Desktop\temp\translation-test-36d46f42a839.json)
    static void generateAutoTranslation(List<MlSymbol> symbols) {
        Set<String> toTranslate = new HashSet<>();
        symbols.stream().forEach(s -> {
            toTranslate.add(s.name);
            toTranslate.addAll(Arrays.asList(s.tags));
        });
        System.out.println(toTranslate.size() + " translation todo (vs " + symbols.size() + " symbols)");

        Translate translate = TranslateOptions.getDefaultInstance().getService();
        try (PrintWriter pw = new PrintWriter("code.txt", "UTF-8")) {
            for (String inputText : toTranslate) {
                Translation translation = translate.translate(
                        inputText,
                        Translate.TranslateOption.sourceLanguage("en"),
                        Translate.TranslateOption.targetLanguage("fr"),
                        Translate.TranslateOption.model("base"));
                pw.println("translations.put(\"" + inputText + "\", \"" + translation.getTranslatedText().replace("&#39;", "'") + "\");");
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
