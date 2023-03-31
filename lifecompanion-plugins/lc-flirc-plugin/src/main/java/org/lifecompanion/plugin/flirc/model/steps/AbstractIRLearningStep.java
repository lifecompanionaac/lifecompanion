package org.lifecompanion.plugin.flirc.model.steps;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

public abstract class AbstractIRLearningStep implements IRLearningStep {

    protected final String translation, imageName;

    protected AbstractIRLearningStep(String translation, String imageName) {
        this.translation = translation;
        this.imageName = imageName;
    }

    @Override
    public String getImage() {
        return "steps/" + imageName;
    }

    @Override
    public String getName() {
        return Translation.getText(translation + ".name");
    }

    @Override
    public String getDescription() {
        return Translation.getText(translation + ".description");
    }

    @Override
    public String getManualStepButtonName() {
        return null;
    }

    @Override
    public boolean isManualStep() {
        return StringUtils.isNotBlank(getManualStepButtonName());
    }

    @Override
    public boolean generateCodes() {
        return false;
    }

    @Override
    public String getNotificationOnShown() {
        return null;
    }
}
