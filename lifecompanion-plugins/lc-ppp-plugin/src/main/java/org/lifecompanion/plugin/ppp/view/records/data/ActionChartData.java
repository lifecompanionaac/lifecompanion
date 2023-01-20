package org.lifecompanion.plugin.ppp.view.records.data;

import javafx.beans.binding.Bindings;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.ppp.model.ActionRecord;
import org.lifecompanion.plugin.ppp.view.records.RecordsChart;

import java.util.List;
import java.util.stream.Collectors;

public class ActionChartData extends AbstractChartData<ActionRecord> {
    public ActionChartData(RecordsChart chart, long date, List<ActionRecord> records, boolean isDetailed) {
        super(chart, date, records, isDetailed);
    }

    @Override
    public String getTitle() {
        if (this.recordsSize < 2) {
            return Translation.getText("ppp.plugin.view.records.details.actions.single.title");
        }

        return Translation.getText("ppp.plugin.view.records.details.actions.multiple.title");
    }

    @Override
    protected XYChart.Data<Number, Number> computeData() {
        // We set the data Y value to the middle score (low is 0, max is 60).
        // This way it will center it on layoutPlotChildren.
        XYChart.Data<Number, Number> data = new XYChart.Data<>(this.date, 30);

        Rectangle internalRectangle = this.createDataRectangle(2.0);
        Rectangle externalRectangle = this.createDataRectangle(15.0);
        externalRectangle.opacityProperty().bind(Bindings.createDoubleBinding(() -> {
            if (this.chart.selectedDataProperty().get() == this) {
                return 0.70;
            }

            return 0.25;
        }, this.chart.selectedDataProperty()));

        StackPane rectanglesContainer = new StackPane(externalRectangle, internalRectangle);
        rectanglesContainer.setOnMouseReleased(e -> this.chart.selectedDataProperty().set(this));
        data.setNode(rectanglesContainer);

        return data;
    }

    @Override
    protected String computeDetailsName() {
        return this.recordsSize < 2
                ? Translation.getText("ppp.plugin.view.records.details.actions.single.details")
                : Translation.getText("ppp.plugin.view.records.details.actions.multiple.details", this.recordsSize);
    }

    @Override
    protected String computeDetailsValue() {
        if (this.recordsSize == 0) {
            return "-";
        }

        return this.recordsSize == 1
                ? this.records.get(0).getAction().getName()
                : this.records.stream()
                .map(aR -> aR.getAction().getName())
                .collect(Collectors.joining(", "));
    }

    @Override
    protected List<ChartData> getAllData() {
        return this.chart.getActionsData();
    }

    private Rectangle createDataRectangle(double width) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(width);
        rectangle.heightProperty().bind(this.chart.heightProperty());
        rectangle.setFill(LCGraphicStyle.SECOND_PRIMARY);

        return rectangle;
    }
}
