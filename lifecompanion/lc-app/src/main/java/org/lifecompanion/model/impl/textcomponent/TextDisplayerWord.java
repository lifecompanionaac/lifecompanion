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

package org.lifecompanion.model.impl.textcomponent;

import org.lifecompanion.model.api.textcomponent.TextBoundsProviderI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerWordI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerWordPartI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.predict4all.nlp.Separator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a word into a {@link TextDisplayerLine}.</br>
 * Each word is divided into {@link TextDisplayerWordPart} because we need to known from witch {@link WriterEntryI} a word part is from.</br>
 * E.G. If {@link TextDisplayerWord} is contained into the same {@link WriterEntryI}, {@link #getParts()} will only contains one part that contains the whole word.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TextDisplayerWord implements TextDisplayerWordI {
    private final List<TextDisplayerWordPartI> parts;
    private Character wordSeparatorChar;
    private boolean previousLineSplittedOnThisWord;
    private int caretStart, caretEnd;

    private double width, height, wordSeparatorCharWidth;

    public TextDisplayerWord() {
        super();
        this.parts = new ArrayList<>(2);
    }

    @Override
    public boolean isPreviousLineSplittedOnThisWord() {
        return previousLineSplittedOnThisWord;
    }

    @Override
    public void setPreviousLineSplittedOnThisWord(boolean previousLineSplitOnThisWord) {
        this.previousLineSplittedOnThisWord = previousLineSplitOnThisWord;
    }

    @Override
    public Character getWordSeparatorChar() {
        return this.wordSeparatorChar;
    }

    @Override
    public void setWordSeparatorChar(Character c) {
        this.wordSeparatorChar = c;
    }

    @Override
    public double getWordSeparatorCharWidth() {
        return this.wordSeparatorCharWidth;
    }

    @Override
    public List<TextDisplayerWordPartI> getParts() {
        return parts;
    }

    @Override
    public int getCaretStart() {
        return caretStart;
    }

    @Override
    public void setCaretStart(int caretStart) {
        this.caretStart = caretStart;
    }

    @Override
    public int getCaretEnd() {
        return caretEnd;
    }

    @Override
    public void setCaretEnd(int caretEnd) {
        this.caretEnd = caretEnd;
    }

    @Override
    public String toString() {
        return "(parts=" + parts + ", end=" + wordSeparatorChar + ", start=" + caretStart + ", end=" + caretEnd + ")";
    }

    /**
     * Compute the width of this word using the given bounds provider.</br>
     * Use the {@link WriterEntryI} of each part as style information, or the default style if needed.</br>
     * The given width includes the end stop char when present.
     *
     * @param provider         bounds provider to compute text size
     * @param defaultTextStyle text style to use if entry has not its own style
     * @return the width of this word, including end stop char
     */
    @Override
    public double getWidth(TextBoundsProviderI provider, TextCompStyleI defaultTextStyle) {
        // TODO use entry style
        double width = getStopCharWidth(provider, defaultTextStyle);
        for (TextDisplayerWordPartI part : parts) {
            width += provider.getBounds(part.getPart(), defaultTextStyle).getWidth();
        }
        return width;
    }

    private double getStopCharWidth(TextBoundsProviderI provider, TextCompStyleI defaultTextStyle) {
        return wordSeparatorChar != null ? provider.getBounds(String.valueOf(wordSeparatorChar), defaultTextStyle).getWidth() : 0.0;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void computeSize(TextBoundsProviderI provider, TextCompStyleI defaultTextStyle) {
        for (TextDisplayerWordPartI part : parts) {
            part.computeSize(provider, defaultTextStyle);
            this.width += part.getWidth();
            this.height = Math.max(height, part.getHeight());
        }
        if (wordSeparatorChar != null && Separator.getSeparatorFor(wordSeparatorChar) != Separator.NEWLINE) {
            this.wordSeparatorCharWidth = getStopCharWidth(provider, defaultTextStyle);
            this.width += wordSeparatorCharWidth;
            // this.height = Math.max(height, caretWidth);
        }
    }

}
