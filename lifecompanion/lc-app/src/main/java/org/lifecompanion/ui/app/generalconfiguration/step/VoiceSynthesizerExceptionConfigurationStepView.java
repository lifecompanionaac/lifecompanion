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

package org.lifecompanion.ui.app.generalconfiguration.step;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.voicesynthesizer.PronunciationExceptionI;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.pane.specific.voicesynthesizer.PronunciationExceptionView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.List;

public class VoiceSynthesizerExceptionConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private LCConfigurationI model;

    /**
     * View for pronunciation exceptions
     */
    private PronunciationExceptionView pronunciationExceptionView;

    public VoiceSynthesizerExceptionConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.voice.synthesizer.exceptions.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.VOICE_SYNTHESIZER_EXCEPTIONS.name();
    }

    @Override
    public String getMenuStepToSelect() {
        return GeneralConfigurationStep.VOICE_SYNTHESIZER_MAIN.name();
    }

    @Override
    public String getPreviousStep() {
        return GeneralConfigurationStep.VOICE_SYNTHESIZER_MAIN.name();
    }

    @Override
    public Node getViewNode() {
        return this;
    }


    // UI
    //========================================================================
    @Override
    public void initUI() {
        this.pronunciationExceptionView = new PronunciationExceptionView();
        BorderPane.setMargin(this.pronunciationExceptionView, new Insets(0, 0, 5, 0));
        this.setCenter(this.pronunciationExceptionView);
    }
    //========================================================================


    @Override
    public void saveChanges() {
        List<PronunciationExceptionI> exceptions = this.pronunciationExceptionView.getModifiedPronunciationExceptions();
        model.getVoiceSynthesizerParameter().getPronunciationExceptions().clear();
        model.getVoiceSynthesizerParameter().getPronunciationExceptions().addAll(exceptions);
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.pronunciationExceptionView.setVoiceSynthesizerUser(model);
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
        this.pronunciationExceptionView.setVoiceSynthesizerUser(null);
    }


}
