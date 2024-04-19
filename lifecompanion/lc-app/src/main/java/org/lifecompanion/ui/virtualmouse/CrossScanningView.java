package org.lifecompanion.ui.virtualmouse;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Screen;
import org.lifecompanion.controller.virtualmouse.ScanningMouseController;

import java.util.ArrayList;
import java.util.List;

public class CrossScanningView extends Pane implements ScanningMouseDrawingI {

    private final static double DEFAULT_LINE_SIZE = 5.0, PADDING = 10.0, INTERNAL_GAP_LINE = 75.0;

    public List<Line> lines;
    private final Double windowsWidth, windowsHeight;
    private final Line left, right, top, bottom, accuracyLeft, accuracyRight, accuracyTop, accuracyBottom;

    public CrossScanningView() {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
        this.windowsWidth = primaryScreenBounds.getWidth();
        this.windowsHeight = primaryScreenBounds.getHeight();
        this.lines = new ArrayList<>();
        // Create
        left = new Line();
        right = new Line();
        top = new Line();
        bottom = new Line();
        accuracyLeft = new Line();
        accuracyRight = new Line();
        accuracyTop = new Line();
        accuracyBottom = new Line();

        // Add
        this.lines.addAll(List.of(left, right, top, bottom, accuracyLeft, accuracyRight, accuracyTop, accuracyBottom));
        this.getChildren().addAll(this.lines);
    }

    @Override
    public void bind(final ScanningMouseController mouseController) {
        // Size on scale
        this.lines.forEach((ln) -> {
            ln.fillProperty().bind(mouseController.colorProperty());
            ln.strokeProperty().bind(mouseController.colorProperty());
            ln.strokeWidthProperty().bind(mouseController.sizeScaleProperty().multiply(DEFAULT_LINE_SIZE));
            ln.setStrokeLineCap(StrokeLineCap.BUTT);
        });

        top.startXProperty().bind(mouseController.mouseXProperty());
        top.endXProperty().bind(mouseController.mouseXProperty());
        top.endYProperty().bind(mouseController.mouseYProperty().add(-PADDING));
        top.visibleProperty().bind(mouseController.visibilityMouseYProperty());

        left.startYProperty().bind(mouseController.mouseYProperty());
        left.endYProperty().bind(mouseController.mouseYProperty());
        left.endXProperty().bind(mouseController.mouseXProperty().add(-PADDING));
        left.visibleProperty().bind(mouseController.visibilityMouseXProperty());

        bottom.startXProperty().bind(mouseController.mouseXProperty());
        bottom.endXProperty().bind(mouseController.mouseXProperty());
        bottom.startYProperty().bind(mouseController.mouseYProperty().add(PADDING));
        bottom.setEndY(this.windowsHeight);
        bottom.visibleProperty().bind(mouseController.visibilityMouseYProperty());

        right.startYProperty().bind(mouseController.mouseYProperty());
        right.endYProperty().bind(mouseController.mouseYProperty());
        right.startXProperty().bind(mouseController.mouseXProperty().add(PADDING));
        right.setEndX(this.windowsWidth);
        right.visibleProperty().bind(mouseController.visibilityMouseXProperty());

        accuracyLeft.startXProperty().bind(mouseController.mouseXAccuracyProperty());
        accuracyLeft.endXProperty().bind(mouseController.mouseXAccuracyProperty());
        accuracyLeft.setEndY(this.windowsHeight);
        accuracyLeft.visibleProperty().bind(mouseController.visibilityMouseYAccuracyProperty());

        accuracyRight.startXProperty().bind(mouseController.mouseXAccuracyProperty().add(mouseController.sizeScaleProperty().multiply(DEFAULT_LINE_SIZE+DEFAULT_LINE_SIZE)).add(INTERNAL_GAP_LINE));
        accuracyRight.endXProperty().bind(mouseController.mouseXAccuracyProperty().add(mouseController.sizeScaleProperty().multiply(DEFAULT_LINE_SIZE+DEFAULT_LINE_SIZE)).add(INTERNAL_GAP_LINE));
        accuracyRight.setEndY(this.windowsHeight);
        accuracyRight.visibleProperty().bind(mouseController.visibilityMouseYAccuracyProperty());

        accuracyTop.startYProperty().bind(mouseController.mouseYAccuracyProperty());
        accuracyTop.endYProperty().bind(mouseController.mouseYAccuracyProperty());
        accuracyTop.setEndX(this.windowsWidth);
        accuracyTop.visibleProperty().bind(mouseController.visibilityMouseXAccuracyProperty());

        accuracyBottom.startYProperty().bind(mouseController.mouseYAccuracyProperty().add(mouseController.sizeScaleProperty().multiply(DEFAULT_LINE_SIZE+DEFAULT_LINE_SIZE)).add(INTERNAL_GAP_LINE));
        accuracyBottom.endYProperty().bind(mouseController.mouseYAccuracyProperty().add(mouseController.sizeScaleProperty().multiply(DEFAULT_LINE_SIZE+DEFAULT_LINE_SIZE)).add(INTERNAL_GAP_LINE));
        accuracyBottom.setEndX(this.windowsWidth);
        accuracyBottom.visibleProperty().bind(mouseController.visibilityMouseXAccuracyProperty());
    }

    @Override
    public Node getView()  {
        return this;
    }

    @Override
    public void unbind() {
        this.lines.forEach((ln) -> {
            ln.fillProperty().unbind();
            ln.strokeProperty().unbind();
            ln.strokeWidthProperty().unbind();
        });
    }

    public List<Line> getLines() {
        return lines;
    }
}
