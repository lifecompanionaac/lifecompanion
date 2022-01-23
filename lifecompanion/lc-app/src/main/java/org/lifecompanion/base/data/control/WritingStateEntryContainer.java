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
package org.lifecompanion.base.data.control;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.WriteSpecialChar;
import org.lifecompanion.api.component.definition.WriterDisplayerI;
import org.lifecompanion.api.component.definition.WriterEntryI;
import org.lifecompanion.api.component.definition.text.TextDisplayerLineI;
import org.lifecompanion.api.control.events.WritingEventSource;
import org.lifecompanion.api.control.events.WritingStateControllerI;
import org.lifecompanion.api.prediction.WordPredictionI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.component.simple.WriterEntry;
import org.lifecompanion.base.data.control.prediction.WordPredictionController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.predict4all.nlp.Separator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WritingStateEntryContainer implements WritingStateControllerI {

    // ATTRIBUTES
    //========================================================================
    private final ObservableList<WriterEntryI> entries;

    private final StringProperty currentText;
    private final StringProperty textBeforeCaret;
    private final StringProperty textAfterCaret;
    private final IntegerProperty caretPosition;

    private final StringProperty currentWord, currentChar, lastCompleteWord;
    private final BooleanProperty upperCase;
    private final BooleanProperty capitalizeNext;
    private boolean nextCapitalizedAutoEnabled = false;

    private final ChangeListener<String> changeListenerTextBeforeCaret;


    /**
     * Represent the writer displayer used to compute lines calculation for caret position.<br>
     * Every other writer display will be based on this one.
     */
    private WriterDisplayerI currentDisplayer;
    //========================================================================

    WritingStateEntryContainer() {

        this.entries = FXCollections.observableArrayList();
        this.currentText = new SimpleStringProperty(this, "currentText", "");
        this.textBeforeCaret = new SimpleStringProperty(this, "textBeforeCaret", "");
        this.textAfterCaret = new SimpleStringProperty(this, "textAfterCaret", "");
        this.caretPosition = new SimpleIntegerProperty(this, "caretPosition", 0);

        this.currentWord = new SimpleStringProperty("");
        this.currentChar = new SimpleStringProperty("");
        this.lastCompleteWord = new SimpleStringProperty("");
        this.upperCase = new SimpleBooleanProperty();
        this.capitalizeNext = new SimpleBooleanProperty();

        changeListenerTextBeforeCaret = (obs, ov, nv) -> {
            evaluateAutoUpperCase();
            evaluateWriterProperties(nv);
        };
        initEntriesBindings();
    }

    public void setCurrentDisplayerAndBindChangeListenerCaret(WriterDisplayerI currentDisplayer) {
        this.currentDisplayer = currentDisplayer;
        this.textBeforeCaret.addListener(changeListenerTextBeforeCaret);
    }

    public void clearCurrentDisplayerAndUnbindCaret() {
        this.currentDisplayer = null;
        this.textBeforeCaret.removeListener(changeListenerTextBeforeCaret);
        // Disable uppercase if needed
        LCUtils.runOnFXThread(() -> {
            this.nextCapitalizedAutoEnabled = false;
            this.capitalizeNext.set(false);
            this.upperCase.set(false);
        });
    }

    public WriterDisplayerI getCurrentDisplayer() {
        return currentDisplayer;
    }

    public void setWriterEntries(List<WriterEntryI> entries) {
        this.removeAll(WritingEventSource.SYSTEM);
        this.entries.addAll(entries);
        this.moveCaretToEnd(WritingEventSource.SYSTEM);
    }

    // BINDINGS/STATE
    //========================================================================
    private void initEntriesBindings() {
        this.entries.addListener((ListChangeListener<WriterEntryI>) (change) -> {
            this.currentText.unbind();
            ObservableList<WriterEntryI> entryList = this.getWriterEntries();
            // List every string properties
            List<StringProperty> entryTextProperty = new ArrayList<>(entryList.size());
            for (WriterEntryI entry : entryList) {
                entryTextProperty.add(entry.entryTextProperty());
            }
            // Create bindings
            this.currentText.bind(Bindings.createStringBinding(() -> {
                StringBuilder sb = new StringBuilder();
                for (StringProperty strProp : entryTextProperty) {
                    sb.append(strProp.get());
                }
                return sb.toString();
            }, entryTextProperty.toArray(new StringProperty[0])));
        });
        // Bind the text before/after caret on the the text and caret position
        this.textBeforeCaret.bind(Bindings.createStringBinding(() -> {
            String text = this.currentText.get();
            int caret = caretPosition.get();
            boolean textEmpty = StringUtils.isEmpty(text);
            if (textEmpty || caret == 0) {
                return "";
            } else {
                return StringUtils.safeSubstring(text, 0, caret);
            }
        }, this.currentText, this.caretPosition));
        this.textAfterCaret.bind(Bindings.createStringBinding(() -> {
            String text = this.currentText.get();
            int caret = caretPosition.get();
            if (StringUtils.isEmpty(text) || caret == text.length()) {
                return "";
            } else {
                return StringUtils.safeSubstring(text, caret, text.length());
            }
        }, this.currentText, this.caretPosition));
    }

    private void evaluateWriterProperties(String nv) {
        this.currentWord.set(this.getLastWord());
        this.lastCompleteWord.set(this.getLastCompleteWord());
        this.currentChar.set(nv != null && nv.length() > 0 ? "" + nv.charAt(nv.length() - 1) : "");
    }

    private void evaluateAutoUpperCase() {
        if (WordPredictionController.INSTANCE.isSentenceStarted()) {
            nextCapitalizedAutoEnabled = true;
            this.enableCapitalizeNext();
        } else if (nextCapitalizedAutoEnabled) {
            this.disableCapitalizeNext();
        }
    }
    //========================================================================

    // PROPERTIES
    //========================================================================
    @Override
    public ReadOnlyBooleanProperty upperCaseProperty() {
        return this.upperCase;
    }

    @Override
    public ReadOnlyBooleanProperty capitalizeNextProperty() {
        return this.capitalizeNext;
    }

    @Override
    public ReadOnlyStringProperty currentCharProperty() {
        return currentChar;
    }

    @Override
    public ReadOnlyStringProperty currentWordProperty() {
        return currentWord;
    }

    @Override
    public ReadOnlyStringProperty lastCompleteWordProperty() {
        return lastCompleteWord;
    }

    @Override
    public ObservableList<WriterEntryI> getWriterEntries() {
        return entries;
    }

    @Override
    public ReadOnlyStringProperty currentTextProperty() {
        return currentText;
    }

    @Override
    public ReadOnlyStringProperty textBeforeCaretProperty() {
        return textBeforeCaret;
    }

    @Override
    public ReadOnlyStringProperty textAfterCaretProperty() {
        return textAfterCaret;
    }

    @Override
    public ReadOnlyIntegerProperty caretPosition() {
        return caretPosition;
    }
    //========================================================================


    // GETTERS
    //========================================================================
    @Override
    public WriterEntryI getEntryBeforeCaretPosition(final int caretPosition) {
        int totalTextLength = 0;
        for (WriterEntryI entry : this.getWriterEntries()) {
            totalTextLength += entry.entryTextProperty().get().length();
            if (totalTextLength >= caretPosition) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public WriterEntryI getEntryAfterCaretPosition(final int caretPosition) {
        int totalTextLength = 0;
        for (WriterEntryI entry : this.getWriterEntries()) {
            totalTextLength += entry.entryTextProperty().get().length();
            if (totalTextLength > caretPosition) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public WriterEntryI getLastEntry() {
        return this.getWriterEntries().isEmpty() ? null : this.getWriterEntries().get(this.getWriterEntries().size() - 1);
    }

    @Override
    public String getLastWord() {
        // Get text before caret
        StringBuilder lastWord = new StringBuilder();
        String textBefore = this.textBeforeCaretProperty().get();
        // Search for the last word
        boolean found = false;
        boolean notStopCharFound = false;
        for (int i = textBefore.length() - 1; !found && i >= 0; i--) {
            char charAt = textBefore.charAt(i);
            boolean stopChar = Separator.getSeparatorFor(charAt) != null;
            if (stopChar && notStopCharFound) {
                found = true;
            } else if (!stopChar) {
                lastWord.insert(0, charAt);
                notStopCharFound = true;
            }
        }
        return lastWord.toString();
    }

    @Override
    public String getLastCompleteWord() {
        // Get text before caret
        StringBuilder lastWord = new StringBuilder();
        String textBefore = this.textBeforeCaretProperty().get();
        // Search for the last word
        boolean found = false;
        boolean firstStopCharFound = false;
        boolean noStopCharFound = false;
        for (int i = textBefore.length() - 1; !found && i >= 0; i--) {
            char charAt = textBefore.charAt(i);
            boolean stopChar = Separator.getSeparatorFor(charAt) != null;
            if (stopChar) {
                if (firstStopCharFound && noStopCharFound)
                    found = true;
                else firstStopCharFound = true;
            } else if (firstStopCharFound) {
                lastWord.insert(0, charAt);
                noStopCharFound = true;
            }
        }
        return lastWord.toString();
    }

    @Override
    public String getLastSentence() {
        // Get text before caret
        StringBuilder lastSentence = new StringBuilder();
        String textBefore = this.textBeforeCaretProperty().get();
        // Search for the last word
        boolean found = false;
        boolean notStopCharFound = false;
        for (int i = textBefore.length() - 1; !found && i >= 0; i--) {
            char charAt = textBefore.charAt(i);
            final Separator stopCharFor = Separator.getSeparatorFor(charAt);
            boolean sentenceStartChar = stopCharFor != null && stopCharFor.isSentenceSeparator();
            if (sentenceStartChar && notStopCharFound) {
                found = true;
            } else if (!sentenceStartChar) {
                lastSentence.insert(0, charAt);
                notStopCharFound = true;
            }
        }
        return lastSentence.toString();
    }


    @Override
    public int getTextLengthBefore(final WriterEntryI targetEntry) {
        return getTextLengthBeforeImpl(targetEntry, false);
    }

    @Override
    public WriterEntryI getEntryAtCaretPosition(final int position) {
        ObservableList<WriterEntryI> entries = this.getWriterEntries();
        int lastEntryStart = 0;
        for (WriterEntryI entry : entries) {
            int entryLength = StringUtils.safeLength(entry.entryTextProperty().get());
            if (position > lastEntryStart && position < lastEntryStart + entryLength) {
                return entry;
            }
            lastEntryStart += entryLength;
        }
        return null;
    }

    @Override
    public int getCaretEntryIndex(final int caretPosition) {
        ObservableList<WriterEntryI> entries = this.getWriterEntries();
        int lastEntryStart = 0;
        int caretTextflowIndex = 0;
        for (WriterEntryI entry : entries) {
            int entryLength = entry.entryTextProperty().get().length();
            lastEntryStart += entryLength;
            if (lastEntryStart > caretPosition) {
                return caretTextflowIndex;
            } else {
                caretTextflowIndex++;
            }
        }
        return caretTextflowIndex;
    }
    //========================================================================

    // BASICS ACTIONS
    //========================================================================
    @Override
    public void newLine(WritingEventSource src) {
        this.disableCapitalizeNext();
        this.insert(src, new WriterEntry("\n", false), WriteSpecialChar.ENTER);
    }

    @Override
    public void tab(WritingEventSource src) {
        this.disableCapitalizeNext();
        this.insert(src, new WriterEntry("\t", false), WriteSpecialChar.TAB);
    }

    @Override
    public void space(WritingEventSource src) {
        this.disableCapitalizeNext();
        this.insert(src, new WriterEntry(" ", false), WriteSpecialChar.SPACE);
    }

    @Override
    public void moveCaretForward(WritingEventSource src) {
        if (this.caretPosition.get() < this.currentText.get().length()) {
            this.caretPosition.set(this.caretPosition.get() + 1);
        }
    }

    @Override
    public void moveCaretToStart(WritingEventSource src) {
        this.caretPosition.set(0);
    }

    @Override
    public void moveCaretToEnd(WritingEventSource src) {
        this.caretPosition.set(this.currentText.get().length());
    }

    @Override
    public void moveCaretBackward(WritingEventSource src) {
        if (this.caretPosition.get() > 0) {
            this.caretPosition.set(this.caretPosition().get() - 1);
        }
    }

    @Override
    public void switchUpperCase(WritingEventSource src) {
        LCUtils.runOnFXThread(() -> {
            if (this.upperCase.get()) {
                this.disableUpperCase();
            } else {
                this.enableUpperCase();
            }
        });
    }

    @Override
    public void switchCapitalizeNext(WritingEventSource src) {
        LCUtils.runOnFXThread(() -> {
            if (this.capitalizeNext.get()) {
                this.disableCapitalizeNext();
            } else {
                this.enableCapitalizeNext();
            }
        });
    }

    @Override
    public void moveCaretUp(WritingEventSource src) {
        moveCaretOnLine(1);
    }

    @Override
    public void moveCaretDown(WritingEventSource src) {
        moveCaretOnLine(-1);
    }


    // TODO : move to parent interface
    public void moveCaretToPosition(WritingEventSource src, WriterDisplayerI displayer, double xInEditor, double yInEditor) {
        //TODO
        List<TextDisplayerLineI> lines = displayer.getLastCachedLines();
        if (lines != null) {
            double y = 0.0;
            for (TextDisplayerLineI line : lines) {
                double lineImageHeight = line.getImageHeight(displayer);
                double lineTextHeight = line.getTextHeight();
                double toAddOnLine = lineImageHeight + lineTextHeight + displayer.lineSpacingProperty().get();
                if (y + toAddOnLine < yInEditor) {
                    y += lineImageHeight + lineTextHeight + displayer.lineSpacingProperty().get();
                } else {
                    int nCaret = line.getCaretPositionFromX(xInEditor, displayer.getCachedLineUpdateListener().getTextBoundsProvider(),
                            displayer.getTextDisplayerTextStyle());
                    if (nCaret >= 0) {
                        caretPosition.set(Math.min(nCaret, this.currentTextProperty().get().length()));
                        return;
                    }
                }
            }
        }
    }


    @Override
    public void insertWordPrediction(WritingEventSource src, String toInsert, WordPredictionI originalPrediction) {
        this.insertText(src, toInsert);
    }

    @Override
    public void insertCharPrediction(WritingEventSource src, String toInsert) {
        this.insertText(src, toInsert);
    }

    @Override
    public void removeLastChar(WritingEventSource src) {
        int caret = this.caretPosition().get();
        WriterEntryI entryBeofre = this.getEntryBeforeCaretPosition(caret);
        if (caret != 0) {
            removeChar(entryBeofre, -1, (entry) -> {
                int textLengthBefore = this.getTextLengthBefore(entry);
                String textBefore = entry.entryTextProperty().get().substring(0, caret - textLengthBefore);
                String textAfter = entry.entryTextProperty().get().substring(caret - textLengthBefore);
                return textBefore.substring(0, textBefore.length() - 1) + textAfter;
            });
        }
    }

    @Override
    public void removeNextChars(WritingEventSource src, final int n) {
        for (int i = 0; i < n; i++) {
            this.removeNextChar(src);
        }
    }

    @Override
    public void removeLastChars(WritingEventSource src, final int n) {
        for (int i = 0; i < n; i++) {
            this.removeLastChar(src);
        }
    }

    @Override
    public void removeNextChar(WritingEventSource src) {
        int caret = this.caretPosition().get();
        WriterEntryI entryAfter = this.getEntryAfterCaretPosition(caret);
        removeChar(entryAfter, 0, (entry) -> {
            int textLengthBefore = getTextLengthBefore(entry);
            String textBefore = entry.entryTextProperty().get().substring(0, caret - textLengthBefore);
            String textAfter = entry.entryTextProperty().get().substring(caret - textLengthBefore);
            return textBefore + textAfter.substring(1);
        });
    }

    public void insert(WritingEventSource src, final WriterEntryI entryP) {
        insert(src, entryP, null);
    }

    @Override
    public void insert(WritingEventSource src, final WriterEntryI entryP, final WriteSpecialChar specialChar) {
        if (this.capitalizeNext.get()) {
            entryP.capitalize();
            this.disableCapitalizeNext();
        }
        if (this.upperCase.get()) {
            entryP.toUpperCase();
        }
        int caret = this.caretPosition().get();
        WriterEntryI entryAtCaret = this.getEntryAtCaretPosition(caret);
        // Simple insert
        if (entryAtCaret == null) {
            // TODO check text entry text null
            this.entries.add(this.getCaretEntryIndex(caret), entryP);
            this.caretPosition.set(caret + entryP.entryTextProperty().get().length());
        }
        // Split
        else {
            // Determine text split
            int textBeforeCurrent = this.getTextLengthBefore(entryAtCaret);
            String textBefore = entryAtCaret.entryTextProperty().get().substring(0, caret - textBeforeCurrent);
            String textAfter = entryAtCaret.entryTextProperty().get().substring(caret - textBeforeCurrent);
            // Create entries
            int lastIndex = this.getWriterEntries().indexOf(entryAtCaret);
            WriterEntry leftEntry = new WriterEntry(textBefore, false);
            WriterEntry rightEntry = new WriterEntry(textAfter, entryAtCaret.disableInsertProperty().get());
            // TODO : replace with the new style system
            // leftEntry.textStyleProperty().set(entryAtCaret.textStyleProperty().get());
            // rightEntry.textStyleProperty().set(entryAtCaret.textStyleProperty().get());
            // entryP.textStyleProperty().set(entryAtCaret.textStyleProperty().get());
            this.getWriterEntries().set(lastIndex++, leftEntry);
            this.getWriterEntries().add(lastIndex++, entryP);
            this.getWriterEntries().add(lastIndex, rightEntry);
            // Update caret
            this.caretPosition.set(textBeforeCurrent + textBefore.length() + entryP.entryTextProperty().get().length());
        }
    }

    @Override
    public void insertText(WritingEventSource src, String text) {
        if (this.capitalizeNext.get()) {
            text = StringUtils.capitalize(text);
            this.disableCapitalizeNext();
        }
        if (this.upperCase.get()) {
            text = StringUtils.toUpperCase(text);
        }
        int caret = this.caretPosition().get();
        // Get the closest entry to the caret
        // If there is not, it's that the entry list is empty
        int entryIndex = this.getCaretEntryIndex(caret);
        boolean insertAsEntry = false;
        if (this.getWriterEntries().size() > 0) {
            WriterEntryI entryAtCaret;
            if (entryIndex >= 0 && entryIndex < this.getWriterEntries().size()) {
                entryAtCaret = this.getWriterEntries().get(entryIndex);
            } else {
                entryAtCaret = this.getWriterEntries().get(this.getWriterEntries().size() - 1);
            }
            // If we can't append to the entry, just
            if (entryAtCaret.disableInsertProperty().get()) {
                insertAsEntry = true;
            } else {
                // TODO : fix a rare StringIndexOutOfBoundsException on substring, threading problem ?
                // Change the entry text by inserting
                String entryText = entryAtCaret.entryTextProperty().get();
                int textBeforeCurrent = this.getTextLengthBefore(entryAtCaret);
                int splitIndexInclusive = caret - textBeforeCurrent;
                String textOnRigth = entryText.substring(splitIndexInclusive);
                String textOnLeft = entryText.substring(0, splitIndexInclusive);
                entryAtCaret.entryTextProperty().set(textOnLeft + text + textOnRigth);
                // Move the caret at the end
                this.caretPosition.set(caret + text.length());
            }
        } else {
            insertAsEntry = true;
        }
        if (insertAsEntry) {
            // No current text or disable append on entry
            this.insert(src, new WriterEntry(text, false), null);
        }
    }

    @Override
    public void removeLastEntry(WritingEventSource src) {
        if (!this.getWriterEntries().isEmpty()) {
            this.getWriterEntries().remove(this.getLastEntry());
            // Place the caret at the end
            this.moveCaretToEnd(src);
        }
    }


    @Override
    public void removeLastWord(WritingEventSource src) {
        this.removeLastChars(src, getLastWordAndStopCharCount());
    }

    int getLastWordAndStopCharCount() {
        // Get text before caret
        String textBefore = this.textBeforeCaretProperty().get();
        // Search for the last word
        // Don't use "getLastWord()" because we need the count that include stop char
        boolean found = false;
        boolean notStopCharFound = false;
        int charToRemove = 0;
        for (int i = textBefore.length() - 1; !found && i >= 0; i--) {
            boolean stopChar = Separator.getSeparatorFor(textBefore.charAt(i)) != null;
            if (stopChar && notStopCharFound) {
                found = true;
            } else if (!stopChar) {
                notStopCharFound = true;
            }
            if (!found) {
                charToRemove++;
            }
        }
        return charToRemove;
    }

    //========================================================================

    // PRIVATE ACTIONS
    //========================================================================
    @Override
    public boolean isExternalWritingDevice() {
        return false;
    }

    @Override
    public void removeAll(WritingEventSource src) {
        this.entries.clear();
        this.caretPosition.set(0);
    }

    private void enableUpperCase() {
        LCUtils.runOnFXThread(() -> {
            this.nextCapitalizedAutoEnabled = false;
            this.capitalizeNext.set(false);
            this.upperCase.set(true);
        });
    }

    private void enableCapitalizeNext() {
        LCUtils.runOnFXThread(() -> {
            this.upperCase.set(false);
            this.capitalizeNext.set(true);
        });
    }

    private void disableUpperCase() {
        LCUtils.runOnFXThread(() -> this.upperCase.set(false));
    }

    private void disableCapitalizeNext() {
        LCUtils.runOnFXThread(() -> {
            this.nextCapitalizedAutoEnabled = false;
            this.capitalizeNext.set(false);
        });
    }

    private void removeChar(final WriterEntryI entry, final int caretAdd, final Function<WriterEntryI, String> entryChanging) {
        if (entry != null) {
            // Move the caret
            if (this.caretPosition.get() != 0) {
                this.caretPosition.set(this.caretPosition().get() + caretAdd);
            }
            // Remove the entry when entry is a unique character entry
            if (entry.entryTextProperty().get().length() <= 1) {
                this.entries.remove(entry);
            }
            // Remove a part of the text in the entry
            else {
                entry.entryTextProperty().set(entryChanging.apply(entry));
            }
        }
    }

    private int getTextLengthBeforeImpl(final WriterEntryI targetEntry, final boolean includeEntry) {
        int textLength = 0;
        ObservableList<WriterEntryI> entries = this.getWriterEntries();
        for (WriterEntryI entry : entries) {
            if (entry == targetEntry) {
                return includeEntry ? textLength + entry.entryTextProperty().get().length() : textLength;
            } else {
                textLength += entry.entryTextProperty().get().length();
            }
        }
        return textLength;
    }

    private void moveCaretOnLine(int direction) {
        if (this.currentDisplayer != null) {
            List<TextDisplayerLineI> lines = currentDisplayer.getLastCachedLines();
            if (lines != null) {
                int caret = this.caretPosition().get();
                TextDisplayerLineI previous = null;
                for (int i = direction > 0 ? 0 : lines.size() - 1; i < lines.size() && i >= 0; i += direction > 0 ? 1 : -1) {
                    TextDisplayerLineI line = lines.get(i);
                    if (previous != null) {
                        double caretXPos = line.getCaretXFromPosition(caret, currentDisplayer.getCachedLineUpdateListener().getTextBoundsProvider(),
                                currentDisplayer.getTextDisplayerTextStyle());
                        if (caretXPos >= 0.0) {
                            int nCaret = previous.getCaretPositionFromX(caretXPos, currentDisplayer.getCachedLineUpdateListener().getTextBoundsProvider(),
                                    currentDisplayer.getTextDisplayerTextStyle());
                            if (nCaret >= 0) {
                                caretPosition.set(Math.min(nCaret, this.currentTextProperty().get().length()));
                            }
                        }
                    }
                    previous = line;
                }
            }
        }
    }


    //========================================================================
}
