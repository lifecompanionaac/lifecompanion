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
package org.lifecompanion.base.data.component.baseimpl;

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.*;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.style2.definition.GridStyleUserI;
import org.lifecompanion.api.style2.definition.KeyCompStyleI;
import org.lifecompanion.api.style2.definition.ShapeCompStyleI;
import org.lifecompanion.api.style2.definition.TextCompStyleI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.style2.impl.GridShapeCompStyle;
import org.lifecompanion.base.data.style2.impl.KeyCompStyle;
import org.lifecompanion.base.data.style2.impl.KeyTextCompStyle;
import org.lifecompanion.base.data.style2.impl.StyleSerialializer;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

import java.util.function.Consumer;

/**
 * Base implementation for {@link GridPartComponentI} to provide default properties.<br>
 * This component is the component that can go inside grid, and also on the top of a stack of grid.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class GridPartComponentBaseImpl extends DisplayableComponentBaseImpl implements GridPartComponentI, GridStyleUserI {

    /**
     * Base properties
     */
    protected IntegerProperty row, column, columnSpan, rowSpan;

    /**
     * Screen location properties<br>
     * These properties are determined from the
     */
    protected transient DoubleProperty layoutX, layoutY, layoutWidth, layoutHeight;

    /**
     * The parent component (the component that keep this parent, can be null)
     */
    protected ObjectProperty<GridComponentI> gridParent;

    /**
     * Expand disabled properties
     */
    protected transient BooleanProperty expandRight, collapseRight, expandLeft, collapseLeft, expandTop, collapseTop, expandBottom, collapseBottom;

    /**
     * Parent stack of this node
     */
    protected ObjectProperty<StackComponentI> stackParent;

    /**
     * If this component is the last child in the stack
     */
    protected transient BooleanProperty lastStackChild;

    /**
     * Root parent for this node
     */
    protected ObjectProperty<RootGraphicComponentI> rootParent;

    private final ShapeCompStyleI gridShapeStyle;
    private final KeyCompStyleI keyStyle;
    private final TextCompStyleI keyTextStyle;

    /**
     * Initialize the properties for this grid part component.
     */
    protected GridPartComponentBaseImpl() {
        super();
        this.row = new SimpleIntegerProperty(this, "row");
        this.column = new SimpleIntegerProperty(this, "column");
        this.columnSpan = new SimpleIntegerProperty(this, "columnSpan");
        this.rowSpan = new SimpleIntegerProperty(this, "rowSpan");
        this.layoutX = new SimpleDoubleProperty(this, "layoutX");
        this.layoutY = new SimpleDoubleProperty(this, "layoutY");
        this.layoutWidth = new SimpleDoubleProperty(this, "layoutWidth");
        this.layoutHeight = new SimpleDoubleProperty(this, "layoutHeight");
        this.gridParent = new SimpleObjectProperty<>(this, "gridParent");
        this.expandRight = new SimpleBooleanProperty(this, "expandRight");
        this.collapseRight = new SimpleBooleanProperty(this, "collapseRight");
        this.expandLeft = new SimpleBooleanProperty(this, "expandLeft");
        this.collapseLeft = new SimpleBooleanProperty(this, "collapseLeft");
        this.expandTop = new SimpleBooleanProperty(this, "expandTop");
        this.collapseTop = new SimpleBooleanProperty(this, "collapseTop");
        this.expandBottom = new SimpleBooleanProperty(this, "expandBottom");
        this.collapseBottom = new SimpleBooleanProperty(this, "collapseBottom");
        this.stackParent = new SimpleObjectProperty<>(this, "stackParent");
        this.rootParent = new SimpleObjectProperty<>(this, "rootParent");
        this.lastStackChild = new SimpleBooleanProperty(this, "lastStackChild");
        this.gridShapeStyle = new GridShapeCompStyle();
        this.keyStyle = new KeyCompStyle();
        this.keyTextStyle = new KeyTextCompStyle();
        //Listener on parent : bind it
        this.gridParent.addListener((obs, ov, nv) -> {
            this.bindParent(ov, nv);
        });
    }

    // Class part : "Expand/collapse"
    //========================================================================
    @Override
    public BooleanProperty expandRightDisabledProperty() {
        return this.expandRight;
    }

    @Override
    public BooleanProperty expandLeftDisabledProperty() {
        return this.expandLeft;
    }

    @Override
    public BooleanProperty expandTopDisabledProperty() {
        return this.expandTop;
    }

    @Override
    public BooleanProperty expandBottomDisabledProperty() {
        return this.expandBottom;
    }

    @Override
    public BooleanProperty collapseRightDisabledProperty() {
        return this.collapseRight;
    }

    @Override
    public BooleanProperty collapseLeftDisabledProperty() {
        return this.collapseLeft;
    }

    @Override
    public BooleanProperty collapseTopDisabledProperty() {
        return this.collapseTop;
    }

    @Override
    public BooleanProperty collapseBottomDisabledProperty() {
        return this.collapseBottom;
    }

    @Override
    public BooleanProperty lastStackChildProperty() {
        return this.lastStackChild;
    }

    @Override
    public ShapeCompStyleI getGridShapeStyle() {
        return this.gridShapeStyle;
    }

    @Override
    public KeyCompStyleI getKeyStyle() {
        return this.keyStyle;
    }

    @Override
    public TextCompStyleI getKeyTextStyle() {
        return this.keyTextStyle;
    }
    //========================================================================

    // Class part : "Layout properties"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty layoutXProperty() {
        return this.layoutX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty layoutYProperty() {
        return this.layoutY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty layoutWidthProperty() {
        return this.layoutWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty layoutHeightProperty() {
        return this.layoutHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<GridComponentI> gridParentProperty() {
        return this.gridParent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<RootGraphicComponentI> rootParentProperty() {
        return this.rootParent;
    }

    //========================================================================

    // Class part : "Parent"
    //========================================================================

    /**
     * Bind all the part properties relative to the parent properties.<br>
     * Also set the parent attribute to be able to compute the level.
     *
     * @param parent the parent that must be binded
     */
    private void bindParent(final GridComponentI oldValue, final GridComponentI parent) {
        //Unbind
        if (oldValue != null) {
            LCUtils.unbindAndSetNull(this.stackParent);
            LCUtils.unbindAndSetNull(this.rootParent);
            LCUtils.unbindAndSetNull(this.configurationParent);
            if (!(this instanceof StackComponentI)) {
                LCUtils.unbindAndSetNull(this.detailName);
            }
        }
        //Bind to parent
        if (parent != null) {
            this.gridShapeStyle.parentComponentStyleProperty().set(parent.getGridShapeStyle());
            this.keyStyle.parentComponentStyleProperty().set(parent.getKeyStyle());
            this.keyTextStyle.parentComponentStyleProperty().set(parent.getKeyTextStyle());
            //Style
            if (!(this instanceof StackComponentI)) {
                this.detailName.bind(parent.nameProperty()); //Bind detail name only if it's not a stack
            }
            this.stackParent.bind(parent.stackParentProperty());
            this.rootParent.bind(parent.rootParentProperty());
            this.configurationParent.bind(parent.configurationParentProperty());
            //Bind location
            this.layoutX.bind(this.gridParent.get().hGapProperty().multiply(this.column.add(1.0))
                    .add(this.column.multiply(this.gridParent.get().caseWidthProperty())));
            this.layoutY.bind(this.gridParent.get().vGapProperty().multiply(this.row.add(1.0))
                    .add(this.row.multiply(this.gridParent.get().caseHeightProperty())));
            //Bind size
            this.layoutWidth.bind(this.columnSpan.multiply(this.gridParent.get().caseWidthProperty())
                    .add(this.columnSpan.add(-1).multiply(this.gridParent.get().hGapProperty())));
            this.layoutHeight.bind(this.rowSpan.multiply(this.gridParent.get().caseHeightProperty())
                    .add(this.rowSpan.add(-1).multiply(this.gridParent.get().vGapProperty())));
            //Bind expand possible
            this.expandRight.bind(this.column.add(this.columnSpan).greaterThanOrEqualTo(this.gridParent.get().columnCountProperty()));
            this.expandLeft.bind(this.column.add(-1).lessThan(0));
            this.expandTop.bind(this.row.add(-1).lessThan(0));
            this.expandBottom.bind(this.row.add(this.rowSpan).greaterThanOrEqualTo(this.gridParent.get().rowCountProperty()));
            //Bind collapse possible
            this.collapseBottom.bind(this.rowSpan.isEqualTo(1));
            this.collapseTop.bind(this.rowSpan.isEqualTo(1));
            this.collapseLeft.bind(this.columnSpan.isEqualTo(1));
            this.collapseRight.bind(this.columnSpan.isEqualTo(1));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        if (this.gridParent.get() == null) {
            return 0;
        } else {
            return 1 + this.gridParent.get().getLevel();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isParentSelected() {
        if (this.gridParent.get() != null && this.gridParent.get().selectedProperty().get()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isParentExist() {
        return this.gridParent.get() != null;
    }

    @Override
    public ObjectProperty<StackComponentI> stackParentProperty() {
        return this.stackParent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public void showToFront() {
        super.showToFront();
        //Root
        if (this.gridParent.get() != null) {
            this.gridParent.get().showToFront();
        }
        //Stack
        if (this.stackParent.get() != null) {
            this.stackParent.get().showToFront();
        }
    }

    @Override
    public void forEachKeys(final Consumer<GridPartKeyComponentI> action) {
    }
    //========================================================================

    // Class part : "Grid"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegerProperty rowProperty() {
        return this.row;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegerProperty columnProperty() {
        return this.column;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegerProperty columnSpanProperty() {
        return this.columnSpan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegerProperty rowSpanProperty() {
        return this.rowSpan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expandRight() {
        if (this.isParentExist()) {
            this.gridParent.get().getGrid().expandSpanRight(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collapseRight() {
        if (this.isParentExist()) {
            this.gridParent.get().getGrid().collapseSpanRight(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expandLeft() {
        if (this.isParentExist()) {
            this.gridParent.get().getGrid().expandSpanLeft(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collapseLeft() {
        if (this.isParentExist()) {
            this.gridParent.get().getGrid().collapseSpanLeft(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expandTop() {
        if (this.isParentExist()) {
            this.gridParent.get().getGrid().expandSpanTop(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collapseTop() {
        if (this.isParentExist()) {
            this.gridParent.get().getGrid().collapseSpanTop(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expandBottom() {
        if (this.isParentExist()) {
            this.gridParent.get().getGrid().expandSpanBottom(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collapseBottom() {
        if (this.isParentExist()) {
            this.gridParent.get().getGrid().collapseSpanBottom(this);
        }
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element content = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(GridPartComponentBaseImpl.class, this, content);
        StyleSerialializer.serializeGridStyle(this, content, contextP);
        StyleSerialializer.serializeKeyStyle(this, content, contextP);
        return content;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(GridPartComponentBaseImpl.class, this, nodeP);
        StyleSerialializer.deserializeGridStyle(this, nodeP, contextP);
        StyleSerialializer.deserializeKeyStyle(this, nodeP, contextP);
    }
    //========================================================================

}
