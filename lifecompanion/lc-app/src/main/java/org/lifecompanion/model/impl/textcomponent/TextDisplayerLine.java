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

import javafx.geometry.Bounds;
import org.lifecompanion.model.api.configurationcomponent.WriterDisplayerI;
import org.lifecompanion.model.api.textcomponent.TextBoundsProviderI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerLineI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerWordI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerWordPartI;
import org.lifecompanion.model.api.style.TextCompStyleI;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a line in text editor.</br>
 * Each line contains words (each word finish with a stop char)
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TextDisplayerLine implements TextDisplayerLineI {
    private double width, textHeight;
    private final List<TextDisplayerWordI> words;

    public TextDisplayerLine() {
        this.words = new ArrayList<>();
    }

    @Override
    public List<TextDisplayerWordI> getWords() {
        return words;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getTextHeight() {
        return textHeight;
    }

    @Override
    public double getImageHeight(WriterDisplayerI textDisplayerComponent) {
        return this.imageOnLine && textDisplayerComponent.enableImageProperty().get() ? textDisplayerComponent.imageHeightProperty().get() : 0.0;
    }

    @Override
    public void computeSize(TextBoundsProviderI provider, TextCompStyleI defaultTextStyle) {
        for (TextDisplayerWordI word : words) {
            word.computeSize(provider, defaultTextStyle);
            this.width += word.getWidth();
            this.textHeight = Math.max(textHeight, word.getHeight());
        }
        // If empty text (just new line), line should have a height
        if (textHeight <= 0.0) {
            Bounds emptyBounds = provider.getBounds("", defaultTextStyle);
            this.textHeight = emptyBounds.getHeight();
        }
    }

    private boolean imageOnLine;

    @Override
    public void setImageOnLine(boolean imageOnLineP) {
        this.imageOnLine = imageOnLineP;
    }

    @Override
    public boolean isImageOnLine() {
        return imageOnLine;
    }

    @Override
    public boolean isCaretOnLine(int caretPosition) {
        for (TextDisplayerWordI word : this.words) {
            if (caretPosition >= word.getCaretStart() && caretPosition <= word.getCaretEnd()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getCaretXFromPosition(int caretPosition, TextBoundsProviderI provider, TextCompStyleI defaultTextStyle) {
        if (!this.words.isEmpty()) {
            double xTotal = 0;
            // Try to find the caret in words
            for (TextDisplayerWordI word : this.words) {
                double xInWord = xTotal;
                if (caretPosition >= word.getCaretStart() && caretPosition <= word.getCaretEnd()) {
                    for (TextDisplayerWordPartI part : word.getParts()) {
                        if (caretPosition >= part.getCaretStart() && caretPosition <= part.getCaretEnd()) {
                            return xInWord + part.getCaretPosition(caretPosition, provider, defaultTextStyle);
                        }
                        xInWord += part.getWidth();
                    }
                    // Not found in the part, is at the word end (after sep char)
                    return xInWord;
                }
                xTotal += word.getWidth();
            }
        }
        return -1;
    }

    @Override
    public int getCaretPositionFromX(double caretX, TextBoundsProviderI provider, TextCompStyleI defaultTextStyle) {
        if (!this.words.isEmpty()) {
            double xTotal = 0;
            // Try to find the caret in words
            for (TextDisplayerWordI word : this.words) {
                double xInWord = xTotal;
                if (caretX >= xInWord && caretX <= xInWord + word.getWidth()) {
                    for (TextDisplayerWordPartI part : word.getParts()) {
                        if (caretX >= xInWord && caretX <= xInWord + part.getWidth()) {
                            // Find the right caret in word part
                            String partStr = part.getPart();
                            for (int c = 0; c < partStr.length(); c++) {
                                double cw = provider.getBounds(String.valueOf(partStr.charAt(c)), defaultTextStyle).getWidth();
                                if (xInWord + cw < caretX) {
                                    xInWord += cw;
                                } else {
                                    return part.getCaretStart() + c + (xInWord + cw - caretX < caretX - xInWord ? 1 : 0);
                                }
                            }
                            return part.getCaretEnd();
                        }
                        xInWord += part.getWidth();
                    }
                    // Not found in the part, is at the word end (after sep char)
                    return word.getCaretEnd();
                }
                xTotal += word.getWidth();
            }
            return words.get(words.size() - 1).getCaretEnd();
        }
        return -1;
    }

    @Override
    public String toString() {
        return "TextDisplayerLine{" +
                "width=" + width +
                ", textHeight=" + textHeight +
                ", words=" + words +
                ", imageOnLine=" + imageOnLine +
                '}';
    }
}
