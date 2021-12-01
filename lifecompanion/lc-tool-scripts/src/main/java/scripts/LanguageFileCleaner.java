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

package scripts;

import javafx.util.Pair;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LanguageFileCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageFileCleaner.class);

    public static void main(String[] args) throws Exception {
        File sourceDirectory = new File("../lifecompanion/");
        File inputDirectory = new File("../lifecompanion/lc-app/src/main/resources/translation");
        File outputCleanedFile = new File("../lifecompanion/lc-app/src/main/resources/translation/fr_merged_translations_v1.xml");

        LOGGER.info("Current path {}", new File(".").getAbsolutePath());

        // Read every translation
        Map<String, List<Pair<String, String>>> allTranslations = new HashMap<>();
        File[] files = inputDirectory.listFiles();
        for (File file : files) {
            if (!file.getName().equals(outputCleanedFile.getName())) {
                Map<String, String> translationMap = getTranslationMap(file);
                translationMap.forEach((key, value) -> {
                    List<Pair<String, String>> valueList = allTranslations.computeIfAbsent(key, k -> new ArrayList<>());
                    if (!valueList.isEmpty()) {
                        LOGGER.info("Found a duplicated translation in {} : {}\n\tVALUE = {}", file.getName(), key, value);
                        valueList.forEach(previous -> System.out.println("\tPREVIOUS = " + previous.getKey() + " / " + StringUtils.safeSubstring(previous.getValue(), 0, 30)));
                    }
                    valueList.add(new Pair<>(file.getName(), value));
                });
            }
        }

        // Explore every source file
        ConcurrentHashMap<String, Set<File>> resultMap = new ConcurrentHashMap<>();
        allTranslations.forEach((k, v) -> resultMap.put(k, ConcurrentHashMap.newKeySet()));

        exploreSources(sourceDirectory, allTranslations.entrySet(), resultMap);
        LOGGER.info("Finished, {} file/dir explored", count.get());

        // TODO : COMPLETE THESE MANUAL FIXES
        //        resultMap.forEach((key, value) -> {
        //            if (key.startsWith("config.tips.")) {
        //                value.add("AvailableConfigTipsEnum");
        //            }
        //        });

        AtomicInteger notUsedCount = new AtomicInteger(0);
        resultMap.forEach((key, fileList) -> {
            if (fileList.isEmpty()) {
                notUsedCount.incrementAndGet();
                LOGGER.info("Key {} is never used !", key);
            }
        });
        LOGGER.info("Not used count {} / {}", notUsedCount.get(), resultMap.size());

        // Clean map : Name > translations
        Map<String, List<Pair<String, String>>> cleanMap = new HashMap<>();
        resultMap.forEach((key, fileList) -> {
            String fileNames = fileList.isEmpty() ? "~ NOT USED ~" : fileList.stream().map(f -> generateKeyMapFrom(f)).distinct().collect(Collectors.joining(", "));
            cleanMap.computeIfAbsent(fileNames, ik -> new ArrayList<>()).add(new Pair<>(key, allTranslations.get(key).get(0).getValue()));
        });

        // Create cleaned translation
        Element root = new Element("texts");
        cleanMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry -> {
                    root.addContent(new Comment(entry.getKey()));
                    List<Pair<String, String>> translationForKey = entry.getValue();
                    translationForKey.stream().sorted(Comparator.comparing(Pair::getKey)).forEach(tp -> {
                        Element text = new Element("text");
                        text.setAttribute("key", tp.getKey());
                        text.setText(tp.getValue());
                        root.addContent(text);
                    });
                });


        // Write it
        try (FileOutputStream fos = new FileOutputStream(outputCleanedFile)) {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(root, fos);
        }
    }

    private static String generateKeyMapFrom(File file) {
        final String filePath = file.getPath();
        final String javaSrcKey = "src\\main\\java\\";
        final String filePathWithPackageOnly = filePath.substring(filePath.indexOf(javaSrcKey) + javaSrcKey.length());
        return filePathWithPackageOnly.substring(0, filePathWithPackageOnly.lastIndexOf("\\")).replace('\\', '.' );
    }

    private static AtomicInteger count = new AtomicInteger();

    private static void exploreSources(File current, Set<Map.Entry<String, List<Pair<String, String>>>> allTranslations, ConcurrentHashMap<String, Set<File>> resultMap) {
        count.incrementAndGet();
        if (current.isDirectory() && current.listFiles() != null) {
            Arrays.stream(current.listFiles())
                    .parallel()
                    .forEach(f -> exploreSources(f, allTranslations, resultMap));
        } else if (current.getName().toLowerCase().endsWith(".java")) {
            try {
                try (BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(current), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = is.readLine()) != null) {
                        final String lineF = line;
                        allTranslations.stream().forEach(k -> {
                            if (lineF.contains(k.getKey())) {
                                resultMap.get(k.getKey()).add(current);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Can't read {}", current, e);
            }
        }
    }

    private static Map<String, String> getTranslationMap(File file) throws JDOMException, IOException {
        Map<String, String> translation = new HashMap<>();
        SAXBuilder sxb = new SAXBuilder();
        Document doc = sxb.build(file);
        Element root = doc.getRootElement();
        List<Element> children = root.getChildren("text");
        for (Element child : children) {
            String key = child.getAttribute("key").getValue();
            translation.put(key, child.getText());
        }
        return translation;
    }
}
