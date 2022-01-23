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

package org.lifecompanion.base.data.component.simple;

import org.jdom2.Element;

import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.component.definition.TreeDisplayableComponentI;
import org.lifecompanion.api.component.definition.TreeDisplayableType;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.component.baseimpl.GridPartComponentBaseImpl;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;

/**
 * The grid part component that represent a component span part.<br>
 * It's a dummy component to fill the grid empty space when a component span on multiple grid part.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ComponentSpan extends GridPartComponentBaseImpl {
	private GridPartComponentI expanded;
	private String expandedID;//Only use while loading XML

	public ComponentSpan() {}

	public void setExpanded(final GridPartComponentI expandedP) {
		this.expanded = expandedP;
		this.expandedID = this.expanded.getID();
	}

	public GridPartComponentI getExpanded() {
		return this.expanded;
	}

	public String getExpandedId() {
		return this.expandedID;
	}

	@Override
	public String getDisplayableTypeName() {
		return null;
	}

	@Override
	public ObservableList<TreeDisplayableComponentI> getChildrenNode() {
		return null;
	}

	@Override
	public boolean isNodeLeaf() {
		return false;
	}

	@Override
	public TreeDisplayableType getNodeType() {
		return null;
	}

	// Class part : "XML"
	//========================================================================
	private static final String ATB_EXPANDED_ID = "expandedID";

	/*
	 * Caution : the expanded ID should be always got on runtime on serialization because
	 * if expanded ID changes before serialization, the expandedID attribute will not change, and an incorrect value
	 * will be saved
	 */
	@Override
	public Element serialize(final IOContextI contextP) {
		Element element = super.serialize(contextP);
		XMLUtils.write(this.expanded.getID(), ComponentSpan.ATB_EXPANDED_ID, element);
		return element;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		this.expandedID = XMLUtils.readString(ComponentSpan.ATB_EXPANDED_ID, nodeP);
	}

	@Override
	public String toString() {
		return super.toString() + " => " + this.expanded;
	}
	//========================================================================

}
