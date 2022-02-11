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
package org.lifecompanion.model.api.textcomponent;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import org.lifecompanion.model.api.configurationcomponent.WriterEntryI;
import org.lifecompanion.model.api.configurationcomponent.WritingDeviceI;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface WritingStateControllerI extends WritingDeviceI {

    // STATE
    //========================================================================
    ObservableList<WriterEntryI> getWriterEntries();

    /**
     * @return the current total text contained in this writer.<br>
     * It's the result of appending all the entries.
     */
    ReadOnlyStringProperty currentTextProperty();

    /**
     * @return the text just before the caret in this writer (substring based on {@link #currentTextProperty()} and {@link #caretPosition()})
     */
    ReadOnlyStringProperty textBeforeCaretProperty();

    /**
     * @return the text just after the caret in this writer (substring based on {@link #currentTextProperty()} and {@link #caretPosition()})
     */
    ReadOnlyStringProperty textAfterCaretProperty();

    /**
     * The caret position in the writer.<br>
     * The caret can be between 0 - text length (inclusive)
     *
     * @return the caret position
     */
    ReadOnlyIntegerProperty caretPosition();

    /**
     * @return the last entry at the end of the writer
     */
    WriterEntryI getLastEntry();

    /**
     * @return the last word before the caret.</br>
     * Example : "the last is " -> "is"
     */
    String getLastWord();

    /**
     * @return the last word before the caret, but only if it's not the current word<br>
     * Example : "the last is" -> "last"<br>
     * "the last is " -> "is"
     */
    String getLastCompleteWord();

    /**
     * @return the last sentence before the caret.</br>
     * Example : "In this. the last is " -> " the last is "
     */
    String getLastSentence();

    ReadOnlyStringProperty currentCharProperty();

    ReadOnlyStringProperty currentWordProperty();

    ReadOnlyStringProperty lastCompleteWordProperty();
    //========================================================================

    // CASE
    //========================================================================
    ReadOnlyBooleanProperty upperCaseProperty();

    ReadOnlyBooleanProperty capitalizeNextProperty();

    void switchUpperCase(WritingEventSource src);

    void switchCapitalizeNext(WritingEventSource src);
    //========================================================================


    //
    //========================================================================

    /**
     * Get the entry located at the given caret position.<br>
     * This method should return null if the caret position doesn't intersect any entry.<br>
     * For exemple with two entry "bonjour" and "test", that make a total text of "bonjourtest" :<br>
     * <ul>
     * <li>0 returns null</li>
     * <li>1 returns "bonjour"</li>
     * <li>6 returns "bonjour"</li>
     * <li>7 returns null</li>
     * <li>8 returns "test"</li>
     * </ul>
     *
     * @param position the position of the caret
     * @return the entry where caret is located on
     */
    WriterEntryI getEntryAtCaretPosition(int position);

    /**
     * Get the caret entry index if the caret would be inserted as a writer entry.<br>
     * For example, for two entry "bonjour" and "test", that makes a total text of "bonjourtest":<br>
     * <ul>
     * <li>0 returns 0</li>
     * <li>1 returns 0</li>
     * <li>6 returns 0"</li>
     * <li>7 returns 1</li>
     * <li>8 returns 1</li>
     * </ul>
     *
     * @param caretPosition
     * @return
     */
    int getCaretEntryIndex(int caretPosition);

    /**
     * Get the text length before the given entry.<br>
     * The text length doesn't include the entry length.
     *
     * @param targetEntry the entry
     * @return the text's before entry length
     */
    int getTextLengthBefore(WriterEntryI targetEntry);

    /**
     * To get the entry just before the caret
     *
     * @param caretPosition the caret position
     * @return the entry before the caret (or null if there is no entry)
     */
    WriterEntryI getEntryBeforeCaretPosition(int caretPosition);

    /**
     * To get the entry just after the caret
     *
     * @param caretPosition the caret position
     * @return the entry after the caret (or null if there is no entry)
     */
    WriterEntryI getEntryAfterCaretPosition(int caretPosition);
    //========================================================================
}
