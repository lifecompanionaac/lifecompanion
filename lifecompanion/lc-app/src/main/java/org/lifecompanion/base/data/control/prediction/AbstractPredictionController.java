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
package org.lifecompanion.base.data.control.prediction;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.PredictionParameterI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.textprediction.BasePredictorI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.controller.plugin.PluginImplementationLoadingHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstract prediction controller.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractPredictionController<T extends BasePredictorI, K, V extends KeyOptionI> implements LCStateListener, ModeListenerI {
    private final Logger LOGGER = LoggerFactory.getLogger(AbstractPredictionController.class);

    private static final long PREDICTION_DELAY_MS = 100;

    /**
     * The list of all current used char prediction
     */
    protected Map<GridComponentI, List<V>> predictionOptions;

    /**
     * List of current char max count in the configuration
     */
    private int wantedPredictionCount;

    /**
     * Listener for typed text changes
     */
    private final InvalidationListener textChangedListener;

    /**
     * Type of prediction option
     */
    private final Class<V> keyOptionType;

    /**
     * Currently selected predictor
     */
    protected T currentPredictor;

    /**
     * Available predictor
     */
    protected final ObservableList<T> availablePredictor;

    /**
     * Contains the predictors ID that comes from plugins
     */
    protected final Map<String, String> predictorFromPluginIds;

    /**
     * Prediction parameters
     */
    protected PredictionParameterI parameter;

    /**
     * Executor to avoid over loading predictors (delay each call, prediction will update only when type end for {@link #PREDICTION_DELAY_MS})
     */
    private final ScheduledExecutorService scheduledExecutor;

    /**
     * Current prediction task (running or cancelled)
     */
    private final AtomicReference<PredictionTask> predictionTask;

    /**
     * To disable the model training on this current session
     */
    private final BooleanProperty disableTrainingOnThisSession;

    private String lastPredictionRequestId;
    private String lastTextBeforeCaret;

    protected AbstractPredictionController(final Class<V> keyOptionTypeP) {
        this.keyOptionType = keyOptionTypeP;
        this.availablePredictor = FXCollections.observableArrayList();
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.predictionOptions = new HashMap<>();
        this.predictorFromPluginIds = new HashMap<>();
        this.predictionTask = new AtomicReference<>();
        this.disableTrainingOnThisSession = new SimpleBooleanProperty();
        this.textChangedListener = (inv) -> {
            this.launchPrediction();
        };
    }

    protected void initializePredictorListener(final PluginImplementationLoadingHelper<Class<? extends T>> pluginImplementationLoadingHelper) {
        pluginImplementationLoadingHelper.registerListenerAndDrainCache((pluginId, predictorType) -> {
                    try {
                        T predictorInstance = predictorType.getConstructor().newInstance();
                        availablePredictor.add(predictorInstance);
                        predictorFromPluginIds.put(predictorInstance.getId(), pluginId);
                    } catch (Exception e) {
                        LOGGER.error("Impossible to create the plugin predictor from {}", predictorType, e);
                    }
                }
        );
    }

    public String getPluginIdForPredictor(String predictorId) {
        return predictorFromPluginIds.get(predictorId);
    }

    // Class part : "Prediction"
    //========================================================================
    private void launchPrediction() {
        if (this.wantedPredictionCount > 0) {
            final String textBeforeCaret = WritingStateController.INSTANCE.textBeforeCaretProperty().get();
            if (StringUtils.isDifferent(lastTextBeforeCaret, textBeforeCaret)) {
                // Create the new task
                final String id = UUID.randomUUID().toString();
                this.lastPredictionRequestId = id;
                this.lastTextBeforeCaret = textBeforeCaret;
                final String textAfterCaret = WritingStateController.INSTANCE.textAfterCaretProperty().get();
                PredictionTask predTask = new PredictionTask(id, textBeforeCaret, textAfterCaret);

                // Cancel previous task if running
                AbstractPredictionController<T, K, V>.PredictionTask previousTask = predictionTask.getAndSet(predTask);
                if (previousTask != null && !previousTask.isDone()) {
                    previousTask.cancel(false);
                }

                // Launch new task
                this.scheduledExecutor.schedule(predTask, PREDICTION_DELAY_MS, TimeUnit.MILLISECONDS);
            }
        }
    }

    private class PredictionTask extends Task<Void> {
        private final String predictionId, textBeforeCaret, textAfterCaret;

        public PredictionTask(String predictionId, String textBeforeCaret, String textAfterCaret) {
            super();
            this.predictionId = predictionId;
            this.textBeforeCaret = textBeforeCaret;
            this.textAfterCaret = textAfterCaret;
        }

        @Override
        protected Void call() throws Exception {
            if (AbstractPredictionController.this.currentPredictor.isInitialized()) {
                if (!isCancelled()) {
                    AbstractPredictionController.this.dispatchPredictionResult(Collections.nCopies(
                            AbstractPredictionController.this.wantedPredictionCount, AbstractPredictionController.this.getWaitingElement()), true);
                    if (!isCancelled()) {
                        List<K> predictions = AbstractPredictionController.this.predict(textBeforeCaret, textAfterCaret, wantedPredictionCount);
                        if (predictionId.equals(lastPredictionRequestId)) {
                            AbstractPredictionController.this.dispatchPredictionResult(predictions, false);
                        } else {
                            LOGGER.warn("Got a prediction result but wasn't the last prediction request.");
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void failed() {
            AbstractPredictionController.this.LOGGER.error("Prediction task for prediction type {} failed",
                    AbstractPredictionController.class.getSimpleName(), this.getException());
        }

    }
    //========================================================================

    // Class part : "Utils"
    //========================================================================
    public T getPredictorForId(final String id) {
        for (T predictor : this.availablePredictor) {
            if (StringUtils.isEquals(predictor.getId(), id)) {
                return predictor;
            }
        }
        return this.getDefaultPredictor();
    }

    public ObservableList<T> getAvailablePredictor() {
        return this.availablePredictor;
    }

    public int getPredictionCount() {
        return this.wantedPredictionCount;
    }

    public ReadOnlyBooleanProperty disableTrainingOnThisSessionProperty() {
        return disableTrainingOnThisSession;
    }

    public void disableTrainingOnThisSession() {
        this.disableTrainingOnThisSession.set(true);
    }

    //========================================================================

    // Class part : "Initialization"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.wantedPredictionCount = 0;
        this.lastPredictionRequestId = null;
        this.lastTextBeforeCaret = null;
        //Select predictor from configuration
        this.parameter = configuration.getPredictionParameters();
        this.currentPredictor = this.getPredictorFor(configuration.getPredictionParameters());
        //Prepare
        this.preparePredictionKeys(configuration);
        this.keyPrepared();
        this.LOGGER.info("Wanted prediction count for the current configuration {} (predictor {})", this.wantedPredictionCount,
                this.getClass().getSimpleName());

        //Initialize if needed
        if (!this.currentPredictor.isInitialized() && this.wantedPredictionCount > 0) {
            try {
                this.currentPredictor.initialize();
                this.LOGGER.info("Prediction {} initialized : {}", this.currentPredictor.getClass().getSimpleName(),
                        this.currentPredictor.isInitialized());
            } catch (Exception e) {
                this.LOGGER.error("Couldn't initialize the word predictor", e);
            }
        }
        //Mode start if needed
        if (this.currentPredictor.isInitialized()) {
            this.currentPredictor.modeStart(configuration);
        }
        //First prediction
        this.launchPrediction();
        //Bind current text
        WritingStateController.INSTANCE.textBeforeCaretProperty().addListener(this.textChangedListener);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        if (this.currentPredictor.isInitialized()) {
            this.currentPredictor.modeStop(configuration);
        }
        this.predictionOptions.clear();
        WritingStateController.INSTANCE.textBeforeCaretProperty().removeListener(this.textChangedListener);
    }

    @Override
    public void lcStart() {
    }

    @Override
    public void lcExit() {
        //Dispose and train all predictor
        String trainText = WritingStateController.INSTANCE.getTotalTextFromStart();
        LOGGER.info("Will train predictor {} with\n{}", this.getClass().getSimpleName(), trainText);
        for (T t : this.availablePredictor) {
            if (!disableTrainingOnThisSession.get()) {
                t.trainDynamicModel(trainText);
            }
            if (t.isInitialized()) {
                this.disposePredictor(t);
            }
        }
        this.scheduledExecutor.shutdownNow();
    }

    private void disposePredictor(final T t) {
        try {
            if (t.isInitialized()) {
                t.dispose();
                this.LOGGER.info("Predictor {} disposed", t.getId());
            }
        } catch (Exception e) {
            this.LOGGER.info("Can't dispose predictor {}", t.getId(), e);
        }
    }
    //========================================================================

    // Class part : "Prediction keys preparation"
    //========================================================================
    private void preparePredictionKeys(final LCConfigurationI configuration) {
        this.wantedPredictionCount = LCUtils.findKeyOptionsByGrid(this.keyOptionType, configuration, this.predictionOptions, this::isValidKey);
    }
    //========================================================================

    // Class part : "Subclass implementation"
    //========================================================================
    protected abstract List<K> predict(String textBeforeCaret, String textAfterCaret, int count);

    protected abstract K getWaitingElement();

    public abstract T getDefaultPredictor();

    protected abstract T getPredictorFor(PredictionParameterI parameter);

    protected abstract void dispatchPredictionResult(List<K> result, boolean waitingDispatch);

    /**
     * Subclass can implements this method to add a control on keys used (default implementation always return true)
     *
     * @param key the key to check
     * @return true only the key is valid to be used with this prediction.
     */
    protected boolean isValidKey(final GridPartKeyComponentI key) {
        return true;
    }

    /**
     * Method called once all the valid keys are listed on use mode starts.<br>
     * Subclass can implements this method to create custom behavior with the keys.
     */
    protected void keyPrepared() {
    }
    //========================================================================

    // Class part : "Serialize if needed"
    //========================================================================
    public void serializeCustomInformation(final Element node, final PredictionParameterI parameter, final IOContextI context) {
        T predictor = this.getPredictorFor(parameter);
        if (predictor != null) {
            Element element = predictor.serialize(context);
            if (element != null) {
                node.addContent(element);
            }
        }
    }

    public void deserializeCustomInformation(final Element element, final PredictionParameterI parameter, final IOContextI context)
            throws LCException {
        T predictor = this.getPredictorFor(parameter);
        if (predictor != null && !element.getChildren().isEmpty()) {
            Element predictorElem = element.getChildren().get(0);
            predictor.deserialize(predictorElem, context);
        }
    }
    //========================================================================

}
