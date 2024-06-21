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

package org.lifecompanion.ui.selectionmode;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.lifecompanion.model.impl.selectionmode.VirtualDirectionalCursorSelectionMode;

import java.util.List;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VirtualDirectionalCursorSelectionModeView extends AbstractAutoActivationSelectionModeView<VirtualDirectionalCursorSelectionMode> {

    private final Circle virtualCursorView;
    private final ObjectProperty<Node> currentOverNode;

    public VirtualDirectionalCursorSelectionModeView(final VirtualDirectionalCursorSelectionMode selectionMode) {
        super(selectionMode);
        this.currentOverNode = new SimpleObjectProperty<>();
        this.virtualCursorView = new Circle();
        this.virtualCursorView.radiusProperty().bind(selectionMode.virtualCursorSizeProperty());
        this.virtualCursorView.fillProperty().bind(selectionMode.virtualCursorColorProperty());
        this.virtualCursorView.visibleProperty().bind(selectionMode.showVirtualCursorProperty());
        this.getChildren().add(virtualCursorView);
        initCursorBinding();
    }

    private void initCursorBinding() {
        // Position bound on model value
        virtualCursorView.layoutXProperty().bind(selectionMode.cursorXProperty());
        virtualCursorView.layoutYProperty().bind(selectionMode.cursorYProperty());

        // FIXME : as the final target component is not the same, the entered can be fired multiple times on the same component, should be fixed
        // Detect enter/exit on current node
        currentOverNode.addListener((obs, ov, nv) -> {
            if (ov != null) {
                simulateMouseEvent(ov, MouseEvent.MOUSE_EXITED);
            }
            if (nv != null) {
                simulateMouseEvent(nv, MouseEvent.MOUSE_ENTERED);
            }
        });

        // Fire mouse moved and change current node
        selectionMode.cursorXProperty().addListener(inv -> this.cursorMoved());
        selectionMode.cursorYProperty().addListener(inv -> this.cursorMoved());
    }

    public void pressed() {
        simulateMouseEvent(MouseEvent.MOUSE_PRESSED);
    }

    public void released() {
        simulateMouseEvent(MouseEvent.MOUSE_RELEASED);
    }

    private void cursorMoved() {
        Node node = simulateMouseEvent(MouseEvent.MOUSE_MOVED);
        currentOverNode.set(node);
    }

    private Node simulateMouseEvent(EventType<MouseEvent> event) {
        return simulateMouseEvent(virtualCursorView, event);
    }

    private Node simulateMouseEvent(Node target, EventType<MouseEvent> event) {
        Bounds boundsInLocal = target.getBoundsInLocal();
        Point2D point = target.localToScene(boundsInLocal.getCenterX(), boundsInLocal.getCenterY());
        Node pick = pick(this.getParent(), point.getX(), point.getY());
        simulateMouseEvent(pick, point, event);
        return pick;
    }

    private void simulateMouseEvent(Node target, Point2D point, EventType<MouseEvent> event) {
        if (target != null) {
            Event.fireEvent(target,
                    new MouseEvent(event, point.getX(), point.getY(), point.getX(), point.getY(), MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, true, true, true, null));
        }
    }

    // Copied from https://stackoverflow.com/questions/40041625/how-to-fire-mouse-event-programmatically-in-javafx
    public Node pick(Node node, double sceneX, double sceneY) {
        if (node != null) {
            Point2D p = node.sceneToLocal(sceneX, sceneY, true /* rootScene */);

            // check if the given node has the point inside it, or else we drop out
            if (!node.contains(p)) return null;

            // at this point we know that _at least_ the given node is a valid
            // answer to the given point, so we will return that if we don't find
            // a better child option
            if (node instanceof Parent) {
                // we iterate through all children in reverse order, and stop when we find a match.
                // We do this as we know the elements at the end of the list have a higher
                // z-order, and are therefore the better match, compared to children that
                // might also intersect (but that would be underneath the element).
                Node bestMatchingChild = null;
                List<Node> children = ((Parent) node).getChildrenUnmodifiable();
                for (int i = children.size() - 1; i >= 0; i--) {
                    Node child = children.get(i);
                    p = child.sceneToLocal(sceneX, sceneY, true /* rootScene */);
                    if (child.isVisible() && !child.isMouseTransparent() && child.contains(p)) {
                        bestMatchingChild = child;
                        break;
                    }
                }

                if (bestMatchingChild != null) {
                    return pick(bestMatchingChild, sceneX, sceneY);
                }
            }
        }

        return node;
    }


}
