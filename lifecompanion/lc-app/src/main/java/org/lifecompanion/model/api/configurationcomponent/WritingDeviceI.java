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
package org.lifecompanion.model.api.configurationcomponent;

import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.textprediction.WordPredictionI;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface WritingDeviceI {

    // ACTION THAT NEEDS STATE (should not be implemented by virtual keyboard)
    //========================================================================
    /**
     * Remove the last entered entry
     */
    void removeLastEntry(WritingEventSource src);

    /**
     * To remove the last word behind the caret.<br>
     * Should remove the beginning of the word if the word is started, or the last word if a new word is started.
     */
    void removeLastWord(WritingEventSource src);

    /**
     * To remove the last selected word prediction (if existing)<br>
     * @param src event source
     */
    void removeLastWordPrediction(WritingEventSource src);

    /**
     * To remove every entries from this writer
     */
    void removeAll(WritingEventSource src);
    //========================================================================

    boolean isExternalWritingDevice();

    /**
     * Insert the given entry at caret position
     *
     * @param entry       the entry to insert in current writer
     * @param specialChar to indicate that the entry is special, null if the entry is not a special char
     */
    void insert(WritingEventSource src, WriterEntryI entry, WriteSpecialChar specialChar);

    /**
     * @param text the text to insert
     * @return true if the text was inserted to existing entry
     */
    void insertText(WritingEventSource src, String text);

    void insertWordPrediction(WritingEventSource src, String toInsert, WordPredictionI originalPrediction);

    void insertCharPrediction(WritingEventSource src, String toInsert);


    /**
     * To remove the last char (char just before the caret position)
     */
    void removeLastChar(WritingEventSource src);

    /**
     * To remove a count of char before the caret
     *
     * @param n the number of char to remove
     */
    void removeLastChars(WritingEventSource src, int n);

    /**
     * To remove the next char (char just after the caret position)
     */
    void removeNextChar(WritingEventSource src);

    /**
     * To remove a count of char after the caret
     *
     * @param n the number of char to remove
     */
    void removeNextChars(WritingEventSource src, int n);

    /**
     * To move the caret forward (safe caret position method)
     */
    void moveCaretForward(WritingEventSource src);

    /**
     * To move the caret backward (safe caret position method)
     */
    void moveCaretBackward(WritingEventSource src);

    /**
     * To move the caret at the first possible position
     */
    void moveCaretToStart(WritingEventSource src);

    /**
     * To move the caret at the last possible position
     */
    void moveCaretToEnd(WritingEventSource src);

    /**
     * To move the caret to the upper line
     */
    void moveCaretUp(WritingEventSource src);

    /**
     * To move the caret to lower line
     */
    void moveCaretDown(WritingEventSource src);

    void moveCaretToPosition(WritingEventSource src, WriterDisplayerI displayer, double xInEditor, double yInEditor);

    /**
     * To insert a new line in this writer
     */
    void newLine(WritingEventSource src);

    /**
     * To insert a space in this writer
     */
    void space(WritingEventSource src);

    /**
     * To insert a tab in this writer
     */
    void tab(WritingEventSource src);
}
