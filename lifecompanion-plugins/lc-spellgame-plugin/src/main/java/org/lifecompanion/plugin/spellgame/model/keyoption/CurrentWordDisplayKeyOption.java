/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.spellgame.model.keyoption;

import javafx.animation.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.jdom2.Element;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.ui.common.pane.generic.ImageViewPane;
import org.lifecompanion.util.javafx.FXThreadUtils;


public class CurrentWordDisplayKeyOption extends AbstractKeyOption {

    public CurrentWordDisplayKeyOption() {
        super();
        this.disableTextContent.set(true);
        this.optionNameId = "spellgame.plugin.current.word.key.option.name";
        this.optionDescriptionId = "spellgame.plugin.current.word.key.option.description";
        this.iconName = "current_word.png";
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().set(null);
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
    }

    @Override
    public Element serialize(IOContextI context) {
        final Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(CurrentWordDisplayKeyOption.class, this, node);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(CurrentWordDisplayKeyOption.class, this, node);
    }

    public void showWord(String word) {
        FXThreadUtils.runOnFXThread(() -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (key != null) {
                key.textContentProperty().set(word);
            }
        });
    }

    public void hideWord() {
        FXThreadUtils.runOnFXThread(() -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (key != null) {
                key.textContentProperty().set(null);
            }
        });
    }

    public void answerDone(boolean correct) {
        ImageView imageView = new ImageView(IconHelper.get("answers/" + (correct ? "answer_good.png" : "answer_bad.png")));
        imageView.setPreserveRatio(true);
        ImageViewPane imageViewPane = new ImageViewPane(imageView);

        int durationMs = 1000;

        Transition toPlay;
        if (correct) {
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(durationMs), imageView);
            rotateTransition.setAxis(Rotate.Y_AXIS);
            rotateTransition.setToAngle(360 * 4);
            rotateTransition.setInterpolator(Interpolator.EASE_IN);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationMs), imageView);
            fadeTransition.setToValue(0.0);
            fadeTransition.setInterpolator(Interpolator.EASE_IN);
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(durationMs), imageView);
            scaleTransition.setToX(0.0);
            scaleTransition.setToY(0.0);
            scaleTransition.setInterpolator(Interpolator.EASE_IN);
            toPlay = new ParallelTransition(rotateTransition, fadeTransition, scaleTransition);
        } else {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(durationMs / 6), imageView);
            scaleTransition.setToX(1.3);
            scaleTransition.setToY(1.3);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(6);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationMs / 2), imageView);
            fadeTransition.setToValue(0.0);

            toPlay = new SequentialTransition(scaleTransition, fadeTransition);
        }

        toPlay.setOnFinished(e -> this.keyViewAddedNodeProperty().set(null));

        FXThreadUtils.runOnFXThread(() -> {
            this.keyViewAddedNodeProperty().set(new BorderPane(imageViewPane));
            toPlay.play();
        });
    }
}
