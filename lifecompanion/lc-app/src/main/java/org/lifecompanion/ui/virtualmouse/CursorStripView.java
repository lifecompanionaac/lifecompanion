package org.lifecompanion.ui.virtualmouse;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Screen;
import org.lifecompanion.controller.virtualmouse.ScanningMouseController;

import java.util.ArrayList;
import java.util.List;

public class CursorStripView extends Pane implements ScanningMouseDrawingI {

    private final static double RECTANGLE_STROKE = 5.0;

    public List<Line> lines;
    private final Double windowsWidth;
    private final Double windowsHeight;
    private static Line left;
    private static Line right;
    private final Line top;
    private final Line bottom;
    private final Line  accuracyLeft;
    private final Line accuracyRight;
    private final Line accuracyTop;
    private final Line accuracyBottom;

    public CursorStripView() {
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
            ln.strokeWidthProperty().bind(mouseController.sizeScaleProperty().multiply(RECTANGLE_STROKE));
            ln.setStrokeLineCap(StrokeLineCap.BUTT);
        });

        top.startXProperty().bind(mouseController.mouseXProperty());
        top.endXProperty().bind(mouseController.mouseXProperty());
        top.endYProperty().bind(mouseController.mouseYProperty().add(-10.0));
        top.visibleProperty().bind(mouseController.visibilityMouseYProperty());

        left.startYProperty().bind(mouseController.mouseYProperty());
        left.endYProperty().bind(mouseController.mouseYProperty());
        left.endXProperty().bind(mouseController.mouseXProperty().add(-10.0));
        left.visibleProperty().bind(mouseController.visibilityMouseXProperty());

        bottom.startXProperty().bind(mouseController.mouseXProperty());
        bottom.endXProperty().bind(mouseController.mouseXProperty());
        bottom.startYProperty().bind(mouseController.mouseYProperty().add(10));
        bottom.setEndY(this.windowsHeight);
        bottom.visibleProperty().bind(mouseController.visibilityMouseYProperty());

        right.startYProperty().bind(mouseController.mouseYProperty());
        right.endYProperty().bind(mouseController.mouseYProperty());
        right.startXProperty().bind(mouseController.mouseXProperty().add(10));
        right.setEndX(this.windowsWidth);
        right.visibleProperty().bind(mouseController.visibilityMouseXProperty());

        accuracyLeft.startXProperty().bind(mouseController.mouseXAccuracyProperty());
        accuracyLeft.endXProperty().bind(mouseController.mouseXAccuracyProperty());
        accuracyLeft.setEndY(this.windowsHeight);
        accuracyLeft.visibleProperty().bind(mouseController.visibilityMouseXAccuracyProperty());

        accuracyRight.startXProperty().bind(mouseController.mouseXAccuracyProperty().add(ScanningMouseController.INSTANCE.sizeScaleProperty().multiply(RECTANGLE_STROKE*1.9).add(75)));
        accuracyRight.endXProperty().bind(mouseController.mouseXAccuracyProperty().add(ScanningMouseController.INSTANCE.sizeScaleProperty().multiply(RECTANGLE_STROKE*1.9).add(75)));
        accuracyRight.setEndY(this.windowsHeight);
        accuracyRight.visibleProperty().bind(mouseController.visibilityMouseXAccuracyProperty());

        accuracyTop.startYProperty().bind(mouseController.mouseYAccuracyProperty());
        accuracyTop.endYProperty().bind(mouseController.mouseYAccuracyProperty());
        accuracyTop.setEndX(this.windowsWidth);
        accuracyTop.visibleProperty().bind(mouseController.visibilityMouseYAccuracyProperty());

        accuracyBottom.startYProperty().bind(mouseController.mouseYAccuracyProperty().add(ScanningMouseController.INSTANCE.sizeScaleProperty().multiply(RECTANGLE_STROKE*1.9).add(75)));
        accuracyBottom.endYProperty().bind(mouseController.mouseYAccuracyProperty().add(ScanningMouseController.INSTANCE.sizeScaleProperty().multiply(RECTANGLE_STROKE*1.9).add(75)));
        accuracyBottom.setEndX(this.windowsWidth);
        accuracyBottom.visibleProperty().bind(mouseController.visibilityMouseYAccuracyProperty());
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
