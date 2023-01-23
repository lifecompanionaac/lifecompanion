/**
 * Copyright (c) 2014, 2021, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lifecompanion.ui.controlsfx.control.textfield;

import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.Collection;

/**
 *  <strong><a href="https://github.com/controlsfx/controlsfx">COPIED FROM CONTROLSFX</a></strong>
 * A class containing useful customizations for the JavaFX {@link TextField}.
 * Note that this class is experimental and the API may change in future 
 * releases. Note also that this class makes use of the {@link CustomTextField}
 * class.
 * 
 * @see CustomTextField
 */
public class TextFields {
    private static final Duration FADE_DURATION = Duration.millis(350);

    private TextFields() {
        // no-op
    }

    /***************************************************************************
     *                                                                         *
     * Search fields                                                           *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a TextField that shows a clear button inside the TextField (on
     * the right hand side of it) when text is entered by the user.
     */
    public static TextField createClearableTextField() {
        CustomTextField inputField = new CustomTextField();
        setupClearButtonField(inputField, inputField.rightProperty());
        return inputField;
    }
    
    private static void setupClearButtonField(TextField inputField, ObjectProperty<Node> rightProperty) {
        inputField.getStyleClass().add("clearable-field"); //$NON-NLS-1$

        Region clearButton = new Region();
        clearButton.getStyleClass().addAll("graphic"); //$NON-NLS-1$
        StackPane clearButtonPane = new StackPane(clearButton);
        clearButtonPane.getStyleClass().addAll("clear-button"); //$NON-NLS-1$
        clearButtonPane.setOpacity(0.0);
        clearButtonPane.setCursor(Cursor.DEFAULT);
        clearButtonPane.setOnMouseReleased(e -> inputField.clear());
        clearButtonPane.managedProperty().bind(inputField.editableProperty());
        clearButtonPane.visibleProperty().bind(inputField.editableProperty());

        rightProperty.set(clearButtonPane);

        final FadeTransition fader = new FadeTransition(FADE_DURATION, clearButtonPane);
        fader.setCycleCount(1);

        inputField.textProperty().addListener(new InvalidationListener() {

            private boolean isButtonVisible = false;

            @Override public void invalidated(Observable arg0) {
                String text = inputField.getText();
                boolean isTextEmpty = text == null || text.isEmpty();

                if (isTextEmpty == isButtonVisible) {
                    isButtonVisible = !isTextEmpty;
                    fadeTo(isButtonVisible);
                }
            }

            private void fadeTo(boolean visible) {
                fader.stop();
                fader.setFromValue(visible? 0.0: 1.0);
                fader.setToValue(visible? 1.0: 0.0);
                fader.play();
            }
        });
    }
}

