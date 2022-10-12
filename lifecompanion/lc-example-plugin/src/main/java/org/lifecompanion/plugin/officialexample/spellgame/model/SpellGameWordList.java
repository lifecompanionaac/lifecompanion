package org.lifecompanion.plugin.officialexample.spellgame.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

public class SpellGameWordList implements XMLSerializable<IOContextI> {
    private final StringProperty name;
    private final ObservableList<String> words;

    public SpellGameWordList() {
        name = new SimpleStringProperty();
        words = FXCollections.observableArrayList();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObservableList<String> getWords() {
        return words;
    }

    private static final String NODE_WORDLIST = "SpellGameWordList", NODE_WORDS = "Words", NODE_WORD = "Word";

    @Override
    public Element serialize(IOContextI context) {
        Element wordListElement = XMLObjectSerializer.serializeInto(SpellGameWordList.class, this, new Element(NODE_WORDLIST));
        Element wordsElement = new Element(NODE_WORDS);
        for (String word : words) {
            Element wordElement = new Element(NODE_WORD);
            wordElement.setText(word);
            wordsElement.addContent(wordElement);
        }
        wordListElement.addContent(wordsElement);
        return wordListElement;
    }

    @Override
    public void deserialize(Element element, IOContextI context) {
        XMLObjectSerializer.deserializeInto(SpellGameWordList.class, this, element);
        final Element wordsElement = element.getChild(NODE_WORDS);
        if (wordsElement != null && !wordsElement.getChildren().isEmpty()) {
            for (Element child : wordsElement.getChildren()) {
                words.add(child.getText());
            }
        }
    }
}
