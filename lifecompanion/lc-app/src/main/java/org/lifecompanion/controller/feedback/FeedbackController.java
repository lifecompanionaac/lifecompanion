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
package org.lifecompanion.controller.feedback;

import javafx.scene.paint.Color;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.useapi.dto.ShowFeedbackActivationDto;
import org.lifecompanion.model.impl.useapi.dto.ShowFeedbackTargetDto;
import org.lifecompanion.ui.feedback.FeedbackView;
import org.lifecompanion.util.javafx.ColorUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Objects;

/**
 * Controller that manage feedbacks that can be displayed to user in use mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum FeedbackController implements ModeListenerI {
    INSTANCE;
    private static final Color DEFAULT_STROKE_COLOR = Color.RED;
    private static final double DEFAULT_STROKE_SIZE = 5;
    public static final long TRANSITION_TIME_MS = 500;

    private static final Color DEFAULT_ACTIVATION_COLOR = ColorUtils.fromWebColor("#2517c263");
    private final Logger LOGGER = LoggerFactory.getLogger(FeedbackController.class);
    private FeedbackView feedbackView;


    FeedbackController() {
    }

    private LCConfigurationI configuration;

    public void showTarget(ShowFeedbackTargetDto showFeedbackTargetDto) {
        if (this.feedbackView != null && configuration != null) {
            // Find the main currently displayed grid
            GridComponentI targetedGrid = getTargetedGrid();
            if (targetedGrid != null) {
                GridPartComponentI component = targetedGrid.getGrid().getComponent(showFeedbackTargetDto.getRow(), showFeedbackTargetDto.getColumn());
                if (component instanceof GridPartKeyComponentI) {
                    Color strokeColor = showFeedbackTargetDto.getColor() != null ? ColorUtils.fromWebColor(showFeedbackTargetDto.getColor()) : DEFAULT_STROKE_COLOR;
                    double strokeSize = showFeedbackTargetDto.getStrokeSize() != null ? showFeedbackTargetDto.getStrokeSize() : DEFAULT_STROKE_SIZE;
                    FXThreadUtils.runOnFXThread(() -> this.feedbackView.showFeedback((GridPartKeyComponentI) component, strokeColor, strokeSize));
                } else {
                    throw new IllegalArgumentException("Targeted component in the grid is not a key, the component is an instance of " + component.getClass().getSimpleName());
                }
            } else {
                throw new IllegalArgumentException("Can't find any target grid");
            }
        }

    }

    public GridComponentI getTargetedGrid() {
        if (configuration != null) {
            return configuration.getChildren()
                    .stream()
                    .filter(c -> c instanceof StackComponentI)
                    .map(c -> (StackComponentI) c)
                    .map(c -> c.displayedComponentProperty().get())
                    .filter(Objects::nonNull)
                    .max(Comparator.comparingInt(g -> g.getGrid().getGridContent().size()))
                    .orElse(null);
        } else return null;
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.configuration = configuration;
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.configuration = null;
        this.feedbackView = null;
    }

    public void setFeedbackView(FeedbackView feedbackView) {
        this.feedbackView = feedbackView;
    }

    public void hideTarget() {
        if (this.feedbackView != null) {
            FXThreadUtils.runOnFXThread(this.feedbackView::hideFeedback);
        }
    }

    public void showActivation(ShowFeedbackActivationDto showFeedbackActivationDto) {
        Color activationColor = showFeedbackActivationDto.getColor() != null ? ColorUtils.fromWebColor(showFeedbackActivationDto.getColor()) : DEFAULT_ACTIVATION_COLOR;
        SelectionModeController.INSTANCE.showActivationRequest(activationColor);
    }

    public void hideActivation() {
        SelectionModeController.INSTANCE.hideActivationRequest();
    }
}