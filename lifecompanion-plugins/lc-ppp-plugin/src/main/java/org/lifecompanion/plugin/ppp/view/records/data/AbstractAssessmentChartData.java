package org.lifecompanion.plugin.ppp.view.records.data;

import javafx.beans.binding.Bindings;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.plugin.ppp.model.AssessmentRecord;
import org.lifecompanion.plugin.ppp.view.records.RecordsChart;

import java.util.List;

public abstract class AbstractAssessmentChartData extends AbstractChartData<AssessmentRecord> {
    protected Number score;

    public AbstractAssessmentChartData(RecordsChart chart, long date, List<AssessmentRecord> records, boolean isDetailed) {
        super(chart, date, records, isDetailed);
    }

    abstract protected Color getColor();

    protected Number computeScore() {
        return this.recordsSize == 1
                ? this.records.get(0).getScore()
                : this.records.stream()
                .mapToDouble(a -> a.getScore() * 1.0)
                .average()
                .orElse(0.0);
    }

    protected Number computeScoreForAxis() {
        return this.score;
    }

    protected String computeScoreForDetails() {
        return String.format("%.2f", this.score.doubleValue());
    }

    @Override
    protected XYChart.Data<Number, Number> computeData() {
        this.score = this.computeScore();

        XYChart.Data<Number, Number> data = new XYChart.Data<>(this.date, this.computeScoreForAxis());

        Circle internalCircle = new Circle(5, this.getColor());
        Circle externalCircle = new Circle(15, this.getColor());
        externalCircle.opacityProperty().bind(Bindings.createDoubleBinding(() -> {
            if (this.chart.selectedDataProperty().get() == this) {
                return 0.70;
            }

            return 0.25;
        }, this.chart.selectedDataProperty()));

        StackPane circlesContainer = new StackPane(externalCircle, internalCircle);
        circlesContainer.setOnMouseReleased(e -> this.chart.selectedDataProperty().set(this));
        data.setNode(circlesContainer);

        return data;
    }

    @Override
    protected String computeDetailsName() {
        return this.recordsSize < 2
                ? Translation.getText("ppp.plugin.view.records.details.assessments.single.details")
                : Translation.getText("ppp.plugin.view.records.details.assessments.multiple.details", this.recordsSize);
    }

    @Override
    protected String computeDetailsValue() {
        return this.recordsSize == 0 ? "-" : this.computeScoreForDetails();
    }
}
