package org.lifecompanion.plugin.spellgame;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;

import java.util.Map;

public class SpellGamePluginProperties extends AbstractPluginConfigProperties {
    private final IntegerProperty wordDisplayTimeInMs;
    private final BooleanProperty validateWithEnter;
    private final BooleanProperty enableFeedbackSound;
    private final ObservableList<SpellGameWordList> wordLists;

    protected SpellGamePluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
        this.wordDisplayTimeInMs = new SimpleIntegerProperty(2000);
        this.validateWithEnter = new SimpleBooleanProperty(true);
        this.enableFeedbackSound = new SimpleBooleanProperty(true);
        this.wordLists = FXCollections.observableArrayList();
    }

    public IntegerProperty wordDisplayTimeInMsProperty() {
        return wordDisplayTimeInMs;
    }

    public BooleanProperty validateWithEnterProperty() {
        return validateWithEnter;
    }

    public BooleanProperty enableFeedbackSoundProperty() {
        return enableFeedbackSound;
    }

    public ObservableList<SpellGameWordList> getWordLists() {
        return wordLists;
    }

    private static final String NODE_WORDLISTS = "SpellGameWordLists";

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element("SpellGamePluginProperties");
        XMLObjectSerializer.serializeInto(SpellGamePluginProperties.class, this, element);
        Element wordListsElement = new Element(NODE_WORDLISTS);
        for (SpellGameWordList wordList : wordLists) {
            wordListsElement.addContent(wordList.serialize(context));
        }
        element.addContent(wordListsElement);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(SpellGamePluginProperties.class, this, node);
        Element wordListsElement = node.getChild(NODE_WORDLISTS);
        if (wordListsElement != null) {
            for (Element spellGameWordListElement : wordListsElement.getChildren()) {
                SpellGameWordList spellGameWordList = new SpellGameWordList();
                spellGameWordList.deserialize(spellGameWordListElement, context);
                wordLists.add(spellGameWordList);
            }
        }
    }

    public SpellGameWordList getWordListById(String id) {
        return wordLists.stream().filter(w -> StringUtils.isEquals(w.getId(), id)).findFirst().orElse(null);
    }

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {

    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
    }
}
