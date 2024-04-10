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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.lifecompanion.framework.commons.translation.Translation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LCAutoTranslation {
    private static final Format FORMAT = Format
            .getPrettyFormat()
            .setEncoding(StandardCharsets.UTF_8.name());

    public static void main(String[] args) throws Exception {
        System.out.println(generateAutoTranslation("ceci est une édition spéciale !"));

//        Map<String, String> texts = loadTexts();
//        texts.entrySet().stream().limit(10).forEach(entry -> {
//            System.out.println(entry.getKey() + " = " + entry.getValue());
//        });
//
//        System.out.println(texts.values().stream().mapToInt(String::length).sum());

        //saveResult(texts);
    }

    private static void saveResult(Map<String, String> texts) throws IOException {
        Element textsElement = new Element("texts");
        texts.entrySet().stream().limit(10).forEach(entry -> {
            Element textElement = new Element("text");
            textElement.setText(entry.getValue());
            textElement.setAttribute("key", entry.getKey());
            textsElement.addContent(textElement);
        });
        XMLOutputter xmlOutputter = new XMLOutputter(FORMAT);
        try (OutputStream os = new FileOutputStream("result.xml")) {
            xmlOutputter.output(textsElement, os);
        }
    }

    private static Map<String, String> loadTexts() throws IOException, JDOMException {
        try (InputStreamReader inputStreamForPath = new InputStreamReader(new FileInputStream(new File("..\\lc-app\\src\\main\\resources\\translation\\fr_translations.xml")))) {
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

    // GOOGLE_APPLICATION_CREDENTIALS to credentials json (ex : E:\Desktop\temp\translation-test-36d46f42a839.json)
    static String generateAutoTranslation(String text) {
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        com.google.cloud.translate.Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage("fr"),
                Translate.TranslateOption.targetLanguage("en"),
                Translate.TranslateOption.model("base"));
        return translation.getTranslatedText().replace("&#39;", "'");
    }
}