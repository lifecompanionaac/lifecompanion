package org.lifecompanion.controller.gaming;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;

public enum GamingFrameworkController implements ModeListenerI {
    INSTANCE;

    private final IntegerProperty maxScore;
    private final IntegerProperty currentScore;

    GamingFrameworkController() {
        this.maxScore = new SimpleIntegerProperty(1);
        this.currentScore = new SimpleIntegerProperty(0);
    }

    public void addToCurrentScore(int value) {
        this.currentScore.set(this.currentScore.get() + value);
    }

    public void setCurrentScore(int value) {
        this.currentScore.set(value);
    }

    public void setMaxScore(int value) {
        this.maxScore.set(value);
    }

    public ReadOnlyIntegerProperty maxScoreProperty() {
        return maxScore;
    }

    public ReadOnlyIntegerProperty currentScoreProperty() {
        return currentScore;
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.maxScore.set(1);
        this.currentScore.set(0);
    }
}
