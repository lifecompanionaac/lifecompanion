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

package org.lifecompanion.model.impl.configurationcomponent.keyoption;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import org.jdom2.Element;
import org.lifecompanion.controller.configurationcomponent.UseModeWhiteboardController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.imagedictionary.StaticImageElement;
import org.lifecompanion.model.impl.style.ShapeStyleBinder;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.binding.Unbindable;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WhiteboardKeyOption extends AbstractKeyOption {
    private Canvas drawingCanvas;

    public WhiteboardKeyOption() {
        super();
        this.optionNameId = "key.option.whiteboard.name";
        this.optionDescriptionId = "key.option.whiteboard.description";
        this.iconName = "icon_type_whiteboard.png";
        this.disableTextContent.set(true);
        this.considerKeyEmpty.set(true);
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        keyViewAddedNode.set(createWhiteboard());
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
        keyViewAddedNode.set(null);
        this.drawingCanvas = null;
    }

    private Region createWhiteboard() {
        drawingCanvas = new Canvas();
        Pane container = new Pane(drawingCanvas);
        drawingCanvas.widthProperty().bind(container.widthProperty());
        drawingCanvas.heightProperty().bind(container.heightProperty());

        GraphicsContext graphicsContext = drawingCanvas.getGraphicsContext2D();
        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
        graphicsContext.setLineCap(StrokeLineCap.ROUND);

        drawingCanvas.setOnMousePressed(mouseEvent -> drawOrErase(mouseEvent, graphicsContext, true));
        drawingCanvas.setOnMouseDragged(mouseEvent -> drawOrErase(mouseEvent, graphicsContext, false));
        return container;
    }

    private void drawOrErase(MouseEvent mouseEvent, GraphicsContext graphicsContext, boolean begin) {
        if (UseModeWhiteboardController.INSTANCE.currentToolProperty().get() == UseModeWhiteboardController.WhiteboardTool.ERASER) {
            double eraserSize = UseModeWhiteboardController.INSTANCE.eraserSizeProperty().get();
            graphicsContext.clearRect(mouseEvent.getX() - eraserSize / 2.0, mouseEvent.getY() - eraserSize / 2.0, eraserSize, eraserSize);
        } else {
            graphicsContext.setStroke(UseModeWhiteboardController.INSTANCE.drawingColorProperty().get());
            graphicsContext.setLineWidth(UseModeWhiteboardController.INSTANCE.pencilSizeProperty().get());
            if (begin) graphicsContext.beginPath();
            graphicsContext.lineTo(mouseEvent.getX(), mouseEvent.getY());
            graphicsContext.stroke();
        }
    }

    public void clearWhiteboard() {
        if (this.drawingCanvas != null) {
            this.drawingCanvas.getGraphicsContext2D().clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
        }
    }

    public Canvas getDrawingCanvas() {
        return drawingCanvas;
    }

    @Override
    public Element serialize(IOContextI context) {
        final Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(WhiteboardKeyOption.class, this, node);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(WhiteboardKeyOption.class, this, node);
    }

    public void bindAndShowProgress(ReadOnlyDoubleProperty progressProperty) {
        //keyViewAddedNode.set(null);
    }

}
