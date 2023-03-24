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

package org.lifecompanion.util.javafx;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.transform.Scale;
import org.lifecompanion.model.impl.constant.LCConstant;

public class SnapshotUtils {
    private static final double MAX_SCALE_RATIO = 5.0;

    public static Image executeSnapshot(final Node node, double wantedWidth, double wantedHeight, boolean canScaleUp, double minScale) {
        Bounds nodeBounds = node.getBoundsInParent();
        SnapshotParameters snapParams = null;
        // Fix only width or height ? (if needed)
        if (wantedHeight > 0 || wantedWidth > 0) {
            wantedWidth = wantedWidth <= 0 ? nodeBounds.getWidth() : wantedWidth;
            wantedHeight = wantedHeight <= 0 ? nodeBounds.getHeight() : wantedHeight;
            // Compute scale to keep ratio
            double originalRatio = nodeBounds.getWidth() / nodeBounds.getHeight();
            double scale = Math.min(MAX_SCALE_RATIO, Math.max(minScale, wantedWidth / wantedHeight > originalRatio ? wantedHeight / wantedWidth : wantedWidth / wantedHeight));
            // Only scale down if wanted (keep lowest memory footprint)
            if (scale < 1 || canScaleUp) {
                snapParams = new SnapshotParameters();
                snapParams.setTransform(new Scale(scale, scale));
            }
        }
        return node.snapshot(snapParams, null);
    }

    /**
     * Take a snapshot of the given node, even if the node is not currently display.<br>
     * The snapshot ratio is kept, with a resulting size depending on wantedWidth and wantedHeight.<br>
     * The resulting size can be scaled down from original, but never scaled up.
     *
     * @param wantedWidth  the result snapshot width (-1 to keep original, or to compute from height)
     * @param wantedHeight the result snapshot height (-1 to keep original, or to compute from width)
     * @param node         the node we should take a snapshot
     * @return the snapshot for the given node
     */
    public static Image takeNodeSnapshot(final Node node, final double wantedWidth, final double wantedHeight, boolean canScaleUp, double minScale) {
        Image snapshot;
        //Check if node has a parent
        if (node.getParent() != null) {
            snapshot = executeSnapshot(node, wantedWidth, wantedHeight, canScaleUp, minScale);
        } else {
            //Init group if needed
            Group group = new Group();
            new Scene(group);

            group.getStylesheets().addAll(LCConstant.CSS_USE_MODE);

            //Take snapshot
            group.getChildren().add(node);
            snapshot = executeSnapshot(node, wantedWidth, wantedHeight, canScaleUp, minScale);
            group.getChildren().remove(node);
        }
        return snapshot;
    }

    public static Image takeNodeSnapshot(final Node node, final double wantedWidth, final double wantedHeight) {
        return takeNodeSnapshot(node, wantedWidth, wantedHeight, false, 0.5);
    }
}
