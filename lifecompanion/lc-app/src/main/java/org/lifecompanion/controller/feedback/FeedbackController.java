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
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.configurationcomponent.ComponentGridI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.configurationcomponent.GridPartKeyComponent;
import org.lifecompanion.model.impl.useapi.dto.ShowFeedbackDto;
import org.lifecompanion.ui.feedback.FeedbackView;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.ColorUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller that manage feedbacks that can be displayed to user in use mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum FeedbackController implements ModeListenerI {
    INSTANCE;
    private static final Color DEFAULT_STROKE_COLOR = Color.RED;
    private static final double DEFAULT_STROKE_SIZE = 5;
    private final Logger LOGGER = LoggerFactory.getLogger(FeedbackController.class);
    private FeedbackView feedbackView;


    FeedbackController() {
    }

    private LCConfigurationI configuration;

    public void showFeedback(ShowFeedbackDto showFeedbackDto) {
        if (this.feedbackView != null) {

            LCConfigurationI configuration = AppModeController.INSTANCE.getUseModeContext().getConfiguration();

            // TODO : define the target grid with a specific method
            ComponentGridI grid = configuration.firstSelectionPartProperty().get().getGrid();

            GridPartComponentI component = grid.getComponent(showFeedbackDto.getRow(), showFeedbackDto.getColumn());
            if (component instanceof GridPartKeyComponentI) {
                FXThreadUtils.runOnFXThread(() -> {
                    this.feedbackView.showFeedback((GridPartKeyComponentI) component,
                            showFeedbackDto.getColor() != null ? ColorUtils.fromWebColor(showFeedbackDto.getColor()) : DEFAULT_STROKE_COLOR,
                            showFeedbackDto.getStrokeSize() != null ? showFeedbackDto.getStrokeSize() : DEFAULT_STROKE_SIZE);
                });
            } else {
                throw new IllegalArgumentException("Targeted component in the grid is not a key, the component is an instance of " + component.getClass().getSimpleName());
            }
        }

    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        ThreadUtils.runAfter(1000, () -> {
            showFeedback(new ShowFeedbackDto(ColorUtils.toWebColorWithAlpha(Color.GREEN), 10.0, 2, 2));
        });
    }


    @Override
    public void modeStop(final LCConfigurationI configuration) {
        // TODO
    }

    public void setFeedbackView(FeedbackView feedbackView) {
        this.feedbackView = feedbackView;
    }
}