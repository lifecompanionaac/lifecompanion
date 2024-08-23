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

package org.lifecompanion.plugin.caaai.model.keyoption;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.caaai.model.useaction.WriteKeySuggestionAction;
import org.lifecompanion.util.javafx.FXThreadUtils;


public class AiSuggestionKeyOption extends AbstractKeyOption {

    private final StringProperty suggestion;

    private WriteKeySuggestionAction writeSuggestedSentenceAction;

    private final BooleanProperty exampleProperty;


    public AiSuggestionKeyOption() {
        super();
        this.disableTextContent.set(true);
        this.optionNameId = "caa.ai.plugin.keys.ai_suggestion.name";
        this.optionDescriptionId = "caa.ai.plugin.keys.ai_suggestion.description";
        this.iconName = "filler_icon_32px.png";
        this.disableTextContent.set(true);

        this.exampleProperty = new SimpleBooleanProperty();

        this.suggestion = new SimpleStringProperty();

        this.suggestion.addListener((obs, ov, pred) -> {
            final GridPartKeyComponentI key = this.attachedKeyProperty().get();
            if (key != null) {
                stopLoading();
                key.textContentProperty().set(pred);
            }
        });
    }

    public BooleanProperty examplePropertyProperty() {
        return this.exampleProperty;
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        this.writeSuggestedSentenceAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, WriteKeySuggestionAction.class);
        if (this.writeSuggestedSentenceAction == null) {
            this.writeSuggestedSentenceAction = new WriteKeySuggestionAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(0, this.writeSuggestedSentenceAction);
        }
        this.writeSuggestedSentenceAction.attachedToKeyOptionProperty().set(true);

        // TODO Translated "(suggestion IA)".
        key.textContentProperty().set(null);
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
        key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.writeSuggestedSentenceAction);
        key.textContentProperty().set("");
    }

    @Override
    public Element serialize(IOContextI context) {
        final Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(AiSuggestionKeyOption.class, this, node);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(AiSuggestionKeyOption.class, this, node);
    }

    public StringProperty suggestionProperty() {
        return this.suggestion;
    }

    public void startLoading() {
        FXThreadUtils.runOnFXThread(() -> {
            ProgressIndicator progressIndicator = new ProgressIndicator(-1);
            progressIndicator.setStyle("-fx-progress-color: white;");
            progressIndicator.setPrefSize(20, 20);

            AnchorPane loadingPane = new AnchorPane(progressIndicator);
            AnchorPane.setTopAnchor(progressIndicator, 5.0);
            AnchorPane.setRightAnchor(progressIndicator, 5.0);

            this.keyViewAddedNodeProperty().set(loadingPane);
        });
    }

    private void stopLoading() {
        this.keyViewAddedNodeProperty().set(null);
    }
}
