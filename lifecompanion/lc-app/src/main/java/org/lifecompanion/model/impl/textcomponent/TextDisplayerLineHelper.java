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

import javafx.scene.text.Font;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.WriterDisplayerI;
import org.lifecompanion.model.api.configurationcomponent.WriterEntryI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.api.textcomponent.*;
import org.lifecompanion.ui.configurationcomponent.base.TextDisplayer3;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.predict4all.nlp.Separator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This utils class is useful to generate lines for {@link TextDisplayer3}
 * Generating lines is a complex operation because line contains multiple entries, and word span over this multiple entries, so it's hard to find a good word wrap.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TextDisplayerLineHelper {

    public static final TextBoundsProviderI BOUNDS_PROVIDER = (text, textStyle) -> {
        Font originalFont = textStyle.fontProperty().get();
        return FXUtils.getTextBounds(text, originalFont);
    };

    public static List<TextDisplayerLineI> generateLines(WritingStateControllerI writingStateController, WriterDisplayerI component, TextCompStyleI defaultTextStyle, double maxWidth) {
        return generateLines(writingStateController.getWriterEntries(), defaultTextStyle, maxWidth, component.enableWordWrapProperty().get());
    }

    public static List<TextDisplayerLineI> generateLines(List<WriterEntryI> originalEntriesList, TextCompStyleI defaultTextStyle, double maxWidth, boolean enableWordWrap) {
        // long splitStart = System.currentTimeMillis();
        // Copy entries (thread-safe)
        List<WriterEntryI> entries = new ArrayList<>(originalEntriesList);

        // Create lines (without wrap)
        List<TextDisplayerLineI> lines = createSimpleLines(entries, defaultTextStyle);

        // Split on multiples lines
        splitLineForWidth(BOUNDS_PROVIDER, defaultTextStyle, maxWidth, enableWordWrap, lines);

        // Split can create empty lines (when width is too small, so we clear thoses empty lines)
        lines.removeIf(textDisplayerLineI -> textDisplayerLineI.getWords().isEmpty());
        addCaretBoundsAndComputeSizeAndImage(lines, BOUNDS_PROVIDER, defaultTextStyle);

        return lines;
    }

    private static void addCaretBoundsAndComputeSizeAndImage(List<TextDisplayerLineI> lines, TextBoundsProviderI boundsProvider,
                                                             TextCompStyleI defaultTextStyle) {
        int caret = 0;

        // final HashMap<WriterEntryI, WordPart> imageAddedForEntries = new HashMap<>();

        for (TextDisplayerLineI line : lines) {
            // Compute size
            line.computeSize(boundsProvider, defaultTextStyle);

            List<TextDisplayerWordI> words = line.getWords();
            TextDisplayerWordPartI lastPartWithEntry = null;
            double currentImageWidth = 0.0;

            // set caret bounds and compute image size
            for (TextDisplayerWordI word : words) {
                word.setCaretStart(caret);
                List<TextDisplayerWordPartI> parts = word.getParts();
                for (TextDisplayerWordPartI part : parts) {
                    part.setCaretStart(caret);
                    caret += part.getPart().length();
                    part.setCaretEnd(caret);

                    // Entry become different : set on previous entry that the image started, and the image with
                    if (lastPartWithEntry == null || lastPartWithEntry.getEntry() != part.getEntry()) {
                        imageStartFoundForEntry(line, lastPartWithEntry, currentImageWidth);
                        lastPartWithEntry = part;
                        currentImageWidth = 0.0;
                    }
                    currentImageWidth += word.getWidth();
                }
                if (word.getWordSeparatorChar() != null) {
                    caret++;
                    currentImageWidth += word.getWordSeparatorCharWidth();
                }
                // Caret end for word, caret should be displayed on next line if there is a line change at the end of the word
                word.setCaretEnd(word.getWordSeparatorChar() != null && Separator.getSeparatorFor(word.getWordSeparatorChar()) == Separator.NEWLINE ? caret - 1 : caret);
            }
            imageStartFoundForEntry(line, lastPartWithEntry, currentImageWidth);
        }
    }

    private static void imageStartFoundForEntry(TextDisplayerLineI line, TextDisplayerWordPartI lastPartWithEntry, double currentImageWidth) {
        if (lastPartWithEntry != null && lastPartWithEntry.getEntry().imageProperty().get() != null) {
            lastPartWithEntry.setImageStart(true);
            lastPartWithEntry.setImageWidth(currentImageWidth);
            line.setImageOnLine(true);
        }
    }

    private static void splitLineForWidth(TextBoundsProviderI boundsProvider, TextCompStyleI defaultTextStyle, double maxWidth, boolean wordWrap,
                                          List<TextDisplayerLineI> lines) {
        for (int i = 0; i < lines.size(); i++) {
            double width = 0.0;
            TextDisplayerLineI line = lines.get(i);
            for (int j = 0; j < line.getWords().size(); j++) {
                TextDisplayerWordI word = line.getWords().get(j);
                double wordWidth = word.getWidth(boundsProvider, defaultTextStyle);
                // Next word go out of the bounds
                if (width + wordWidth >= maxWidth) {
                    if (word.isPreviousLineSplittedOnThisWord() || !wordWrap) {
                        splitLineOnChar(boundsProvider, defaultTextStyle, maxWidth, lines, i, width, line, j, word);
                    } else {
                        splitLineOnWord(lines, i, line, j, word);
                    }
                    break;
                } else {
                    width += wordWidth;
                }
            }
        }
    }

    /**
     * To split the current on a char contained in the line.</br>
     * Useful when required width is too small to contains a full word, or when we don't want a word wrap.
     *
     * @param boundsProvider   text bounds provider
     * @param defaultTextStyle default text style to use to compute text bounds
     * @param maxWidth         max wanted width for a line
     * @param lines            line list
     * @param lineIndex        current line index
     * @param width            current computed width for the current line
     * @param line             current line
     * @param wordIndex        word where is split is executed (index)
     * @param word             word where is split is executed
     */
    // TODO style
    private static void splitLineOnChar(TextBoundsProviderI boundsProvider, TextCompStyleI defaultTextStyle, double maxWidth,
                                        List<TextDisplayerLineI> lines, int lineIndex, double width, TextDisplayerLineI line, int wordIndex, TextDisplayerWordI word) {

        // Go thought each word part
        List<TextDisplayerWordPartI> wordParts = word.getParts();
        for (int w = 0; w < wordParts.size(); w++) {
            TextDisplayerWordPartI wordContent = wordParts.get(w);

            // For each part, compute the next char width, to know where the word should be splitted
            for (int c = 0; c < wordContent.getPart().length(); c++) {
                double charWith = boundsProvider.getBounds(String.valueOf(wordContent.getPart().charAt(c)), defaultTextStyle).getWidth();

                // When width become longer than max width : char to split on is found
                if (width + charWith >= maxWidth) {

                    // It become too large on a next char : the word part should be split in two part
                    if (c > 0 || w > 0) {
                        // Next line will start with this word entry that contains all the remaining word parts
                        TextDisplayerWord nextLineFirstWord = new TextDisplayerWord();

                        // Split word part into two word part (left : stay on current line, right : go on next line)
                        // Split the word only if needed (sometime this we just need to split word parts)
                        if (c > 0) {
                            String rightPart = wordContent.splitOnIndex(c);
                            TextDisplayerWordPart nextWordPart = new TextDisplayerWordPart(wordContent.getEntry(), rightPart);
                            nextLineFirstWord.getParts().add(nextWordPart);
                        }

                        // Word on next line will finish with the current word end char
                        nextLineFirstWord.setWordSeparatorChar(word.getWordSeparatorChar());
                        word.setWordSeparatorChar(null);

                        // Move remaining part of the word to the word on next line
                        splitList(wordParts, w + (c > 0 ? 1 : 0), nextLineFirstWord.getParts(), -1);

                        // Create/get next line (because it should shift line content)
                        TextDisplayerLineI nextLine = getOrCreateNextLine(lines, lineIndex);
                        nextLine.getWords().add(0, nextLineFirstWord);

                        // Move remaining words to the next line (after the inserted word)
                        splitList(line.getWords(), wordIndex + 1, nextLine.getWords(), 1);
                        return;
                    }
                }
                width += charWith;
            }
        }
        // If at the end of all the part, the width is still correct, it's that the split should happen on end stop char
        if (width < maxWidth) {
            // Create the word with the stop char for next line
            TextDisplayerWord nextLineFirstWord = new TextDisplayerWord();
            nextLineFirstWord.setWordSeparatorChar(word.getWordSeparatorChar());
            word.setWordSeparatorChar(null);

            // Add the word as the first word in the next line
            TextDisplayerLineI nextLine = getOrCreateNextLine(lines, lineIndex);
            nextLine.getWords().add(0, nextLineFirstWord);

            // Move remaining words to the next line (after the inserted word)
            splitList(line.getWords(), wordIndex + 1, nextLine.getWords(), 1);
        }
    }

    private static TextDisplayerLineI getOrCreateNextLine(List<TextDisplayerLineI> lines, int lineIndex) {
        TextDisplayerLineI nextLine;
        if (lineIndex + 1 < lines.size()) {
            nextLine = lines.get(lineIndex + 1);
        } else {
            nextLine = new TextDisplayerLine();
            lines.add(lineIndex + 1, nextLine);
        }
        return nextLine;
    }

    /**
     * To split a line on a specific word.</br>
     * This is useful to execute a word wrap.
     *
     * @param lines     the lines list
     * @param lineIndex the current line index (line to split)
     * @param line      the current line (line to split)
     * @param wordIndex word index, the word where split is executed (inclusive, the splitted word will goes on the next line)
     * @param word      the word to split on
     */
    private static void splitLineOnWord(List<TextDisplayerLineI> lines, int lineIndex, TextDisplayerLineI line, int wordIndex,
                                        TextDisplayerWordI word) {
        // Avoid infinite loop : we just try once to execute a word wrap
        word.setPreviousLineSplittedOnThisWord(true);

        // New line is created after the current one (don't need to use the existing next : this algo goes forward, so next line only contains wanted line break)
        TextDisplayerLineI nextLine = new TextDisplayerLine();
        lines.add(lineIndex + 1, nextLine);

        // Move the end of the current line to the next line
        splitList(line.getWords(), wordIndex, nextLine.getWords(), -1);// Insert to end of line
    }

    /**
     * This method create simple lines from given entries.</br>
     * Each {@link TextDisplayerLine} contains {@link TextDisplayerWord}, that can be divided in {@link TextDisplayerWordPart}.</br>
     * This method split the given entries by their {@link Separator} to produce words.</br>
     * This method doesn't take into account line width, but can create a multiple lines if entry contains the {@link Separator#NEWLINE}.
     *
     * @param entries          writer's entries
     * @param defaultTextStyle
     * @return line for the given entries, each line is divided by its words and separator ( {@link Separator} )
     */
    protected static List<TextDisplayerLineI> createSimpleLines(List<WriterEntryI> entries, TextCompStyleI defaultTextStyle) {
        List<TextDisplayerLineI> lines = new ArrayList<>();

        // Initialize first line and word
        TextDisplayerLineI currentLine = new TextDisplayerLine();
        lines.add(currentLine);
        TextDisplayerWordI currentWordEntry = new TextDisplayerWord();
        currentLine.getWords().add(currentWordEntry);

        for (WriterEntryI entry : entries) {

            // At least a different word part by entry (because word part is linked to entry)
            StringBuilder currentWordPart = new StringBuilder();

            // Go thought each entry char
            final String entryTextBefore = entry.entryTextProperty().get();
            final String entryText = LangUtils
                    .nullToEmpty(LangUtils.isTrue(defaultTextStyle != null ? defaultTextStyle.upperCaseProperty().value().getValue() : false)
                            ? StringUtils.toUpperCase(entryTextBefore)
                            : entryTextBefore);
            for (int c = 0; c < entryText.length(); c++) {

                final char chartAtC = entryText.charAt(c);
                Separator stopChar = Separator.getSeparatorFor(chartAtC);
                // Found a stop char : finish current word part
                if (stopChar != null) {

                    // If a word was already started : add it
                    if (currentWordPart.length() > 0) {
                        currentWordEntry.getParts().add(new TextDisplayerWordPart(entry, currentWordPart.toString()));
                        currentWordPart = new StringBuilder();
                    }

                    // Save the stop char to the finished word
                    currentWordEntry.setWordSeparatorChar(chartAtC);
                    // Create new line if needed (on new line separator)
                    if (stopChar == Separator.NEWLINE) {
                        currentLine = new TextDisplayerLine();
                        lines.add(currentLine);
                    }
                    // Prepare the next word
                    currentWordEntry = new TextDisplayerWord();
                    currentLine.getWords().add(currentWordEntry);
                }

                // No stop char : continue current word
                else {
                    currentWordPart.append(chartAtC);
                }
            }

            // This entry is finished : the word part should be finished
            if (currentWordPart.length() > 0) {
                currentWordEntry.getParts().add(new TextDisplayerWordPart(entry, currentWordPart.toString()));
            }
        }
        return lines;
    }

    /**
     * Move the end of a given source list to to a target list.
     *
     * @param source             source list, element with index >= fromIndexInclusive will be removed from it
     * @param fromIndexInclusive the starting (inclusive) index of moving operation
     * @param target             target list where all elements taken from source will be added. Insert index is shifted while inserting.
     * @param insertFrom         the insert index in the target list. If specified (insertFrom >= 0), add in the target list will start on insertFrom and finish on insertFrom + moved element count
     */
    private static <T> void splitList(List<T> source, int fromIndexInclusive, List<T> target, int insertFrom) {
        int currentIndex = 0;
        Iterator<T> sourceCollectionIterator = source.iterator();
        while (sourceCollectionIterator.hasNext()) {
            T value = sourceCollectionIterator.next();
            if (currentIndex >= fromIndexInclusive) {
                sourceCollectionIterator.remove();
                if (insertFrom >= 0)
                    target.add(insertFrom++, value);
                else target.add(value);
            }
            currentIndex++;
        }
    }
}
