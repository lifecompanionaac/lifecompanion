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

package org.lifecompanion.framework.commons.translation;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;

/**
 * Class that load language resources and provide it to user.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum Translation {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(Translation.class);

    private static final String ATB_KEY = "key", NODE_ELEM = "text";

    /**
     * Contains all the text
     */
    private final HashMap<String, String> texts;

    /**
     * Contains all the loaded resources ids
     */
    private final Set<String> loadedResourcesIds;

    /**
     * Predicate from external provider to indicate that the string could be converted from this provider
     */
    private Predicate<Object> stringConverterPredicate;

    /**
     * Function to convert an object to string from external provider
     */
    private Function<Object, Object> stringConverterValueGetter;

    /**
     * Create the Translation object.<br>
     * Load the default text for the framework.
     */
    private Translation() {
        this.texts = new HashMap<>(200);
        this.loadedResourcesIds = new HashSet<>(10);
    }

    /**
     * Get the text from the current resource.<br>
     * Return the key if the text wasn't found
     *
     * @param key  the key
     * @param args the value to put in argument
     * @return the text, or the key
     */
    public String getIText(final String key, final Object... args) {
        if (this.texts.containsKey(key)) {
            String txt = this.texts.get(key);
            for (int i = 0; i < args.length; i++) {
                txt = txt.replaceFirst("\\{\\}", this.toString(args[i]));
            }
            return txt;
        } else {
            return key;
        }
    }

    /**
     * Convert an object to a string representation
     *
     * @param arg the object to convert
     * @return the string that represent the given argument
     */
    private String toString(final Object arg) {
        String strValue = "";
        if (arg == null) {
            strValue = "null";
        } else if (this.stringConverterPredicate != null && this.stringConverterPredicate.test(arg)) {
            strValue = this.toString(this.stringConverterValueGetter.apply(arg));
        } else {
            strValue = arg.toString();
        }
        return Matcher.quoteReplacement(strValue);
    }


    public void setSuppStringConverter(Predicate<Object> stringConverterPredicate, Function<Object, Object> stringConverterValueGetter) {
        this.stringConverterPredicate = stringConverterPredicate;
        this.stringConverterValueGetter = stringConverterValueGetter;
    }

    /**
     * This method is equivalent to {@link #getIText(String, Object...)}.<br>
     *
     * <pre>
     * //Equivalent
     * INSTANCE.getIText(...)
     * </pre>
     */
    public static String getText(final String key, final Object... args) {
        return Translation.INSTANCE.getIText(key, args);
    }

    public static boolean isTranslationExit(final String key) {
        return Translation.INSTANCE.texts.containsKey(key);
    }


    /**
     * Load the given XML and put key/value in map
     *
     * @param file the file to load
     */
    public void load(final String id, final InputStream file) throws Exception {
        if (this.loadedResourcesIds.contains(id)) {
            LOGGER.info("Didn't load {} because it was already loaded", id);
        } else {
            SAXBuilder sxb = new SAXBuilder();
            Document doc = sxb.build(file);
            Element root = doc.getRootElement();
            List<Element> children = root.getChildren(Translation.NODE_ELEM);
            for (Element child : children) {
                String key = child.getAttribute(Translation.ATB_KEY).getValue();
                String value = cleanText(child.getText());
                if (this.texts.containsKey(key)) {
                    LOGGER.warn("Found a duplicated translation entry : {} - {}", id, key);
                }
                this.texts.put(key, value);
            }
            this.loadedResourcesIds.add(id);
            LOGGER.info("Translation loaded from {}, {} elements found", id, children.size());
        }
    }

    /**
     * Clean a given text from XML.<br>
     * Remove tab and new line, and replace \n and \t
     *
     * @param text the text to clean
     * @return the cleaned text
     */
    public static String cleanText(final String text) {
        return text.replace("\t", "").replace("\n", " ").replace("\r", "").replace("\\t", "\t").replace("\\n", "\n");
    }
}
