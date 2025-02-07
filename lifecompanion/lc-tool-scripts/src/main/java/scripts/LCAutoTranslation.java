/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import org.apache.xmlbeans.SchemaStringEnumEntry;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.ThreadUtils;
import scripts.imagedictionaries.LoggingProgressIndicator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LCAutoTranslation {
    private static final Format FORMAT = Format
            .getPrettyFormat()
            .setEncoding(StandardCharsets.UTF_8.name());

    static Translate translate;

    public static void main(String[] args) throws Exception {
        translate = TranslateOptions.getDefaultInstance().getService();

        // Load source translations
        Map<String, String> texts = loadTexts("fr_translations.xml");
        LoggingProgressIndicator pi = new LoggingProgressIndicator(texts.size(), "Translation");

        // Load already translated
        Map<String, String> alreadyTranslated = loadTexts("en_translations.xml");
        System.out.println("To translate : " +( texts.size() - alreadyTranslated.size()));
        ThreadUtils.safeSleep(5_000);
        //System.out.println(alreadyTranslated);

        Map<String, String> translated = new HashMap<>();

        for (Map.Entry<String, String> idAndText : texts.entrySet()) {
            try {
                String originalText = idAndText.getValue();
                String id = idAndText.getKey();
                if (!alreadyTranslated.containsKey(id)) {
                    // System.out.println("Missing  " + id);
                    String translation = generateAutoTranslation(originalText);
                    translated.put(id, translation);
                    System.out.println("\n" + id + "\n===== FR =====\n" + originalText);
                    System.out.println("\n===== EN =====\n" + translation + "\n==============");
                } else {
                    translated.put(id, alreadyTranslated.get(id));
                }
                pi.increment();
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }
        }
        saveResult(translated);
    }

    private static void saveResult(Map<String, String> texts) throws IOException {
        Element textsElement = new Element("texts");
        texts.entrySet().stream().forEach(entry -> {
            Element textElement = new Element("text");
            textElement.setText(entry.getValue().replace("\n", "\\n"));
            textElement.setAttribute("key", entry.getKey());
            textsElement.addContent(textElement);
        });
        XMLOutputter xmlOutputter = new XMLOutputter(FORMAT);
        try (OutputStream os = new FileOutputStream("..\\lc-app\\src\\main\\resources\\translation\\en_translations.xml")) {
            xmlOutputter.output(textsElement, os);
        }
    }

    private static Map<String, String> loadTexts(String name) throws IOException, JDOMException {
        try (InputStreamReader inputStreamForPath = new InputStreamReader(new FileInputStream(new File("..\\lc-app\\src\\main\\resources\\translation\\" + name)))) {
            SAXBuilder sxb = new SAXBuilder();
            Document doc = sxb.build(inputStreamForPath);
            Element root = doc.getRootElement();
            List<Element> children = root.getChildren("text");
            Map<String, String> texts = new HashMap<>();
            for (Element child : children) {
                String id = child.getAttribute("key").getValue();
                String text = Translation.cleanText(child.getText());
                texts.put(id, text);
            }
            return texts;
        }
    }

    // GOOGLE_APPLICATION_CREDENTIALS to credentials json (ex : "C:\Users\John\Desktop\cred.json")
    static String generateAutoTranslation(String text) {
        com.google.cloud.translate.Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage("fr"),
                Translate.TranslateOption.targetLanguage("en"),
                Translate.TranslateOption.format("text"),
                Translate.TranslateOption.model("base"));
        return translation.getTranslatedText();//.replace("&#39;", "'").replace("&quot;", "\"")
    }
}