/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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
package org.lifecompanion.model.impl.style;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.lifecompanion.model.api.style.GridCompStyleI;
import org.lifecompanion.model.api.style.ShapeCompStyleI;

public class StyleTest {

    private static final Color DEFAULT_BACKGROUND_COLOR = Color.BEIGE;
    private GridCompStyleI defaultShapeStyle;

    @BeforeEach
    public void setUp() {
        this.defaultShapeStyle = new GridCompStyle();
        this.defaultShapeStyle.backgroundColorProperty().selected().setValue(StyleTest.DEFAULT_BACKGROUND_COLOR);
    }

//    @Test
//    public void testBinding() {
//        GridShapeCompStyle stackStyle = new GridShapeCompStyle();
//        Assert.assertEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, stackStyle.backgroundColorProperty().value().getValue());
//        //Create another child
//        ShapeCompStyle gridStyle = new ShapeCompStyle(null);
//        gridStyle.nameProperty().set("GridStyle");
//        gridStyle.styleProperty().parent().bind(stackStyle.styleProperty().value());
//        gridStyle.parentComponentStyleProperty().set(stackStyle);
//        Assert.assertEquals(this.defaultShapeStyle, gridStyle.styleProperty().value().getValue());
//        Assert.assertEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, gridStyle.backgroundColorProperty().value().getValue());
//        //Change color on parent
//        Color selectColor = Color.ANTIQUEWHITE;
//        stackStyle.backgroundColorProperty().selected().setValue(selectColor);
//        Assert.assertNotEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, stackStyle.backgroundColorProperty().value().getValue());
//        Assert.assertEquals(selectColor, stackStyle.backgroundColorProperty().value().getValue());
//        //Check on children
//        Assert.assertEquals(selectColor, gridStyle.backgroundColorProperty().parent().getValue());
//        Assert.assertNotEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, gridStyle.backgroundColorProperty().value().getValue());
//        Assert.assertEquals(selectColor, gridStyle.backgroundColorProperty().value().getValue());
//    }
//
//	@Test
//	public void testParentRestore() {
//		ShapeCompStyle stackStyle = new ShapeCompStyle(null);
//		stackStyle.nameProperty().set("StackStyle");
//		stackStyle.styleProperty().parent().setValue(this.defaultShapeStyle);
//		Assert.assertEquals(this.defaultShapeStyle, stackStyle.styleProperty().value().getValue());
//		Assert.assertEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, stackStyle.backgroundColorProperty().value().getValue());
//		//Create another child
//		ShapeCompStyle gridStyle = new ShapeCompStyle(null);
//		gridStyle.nameProperty().set("GridStyle");
//		gridStyle.styleProperty().parent().bind(stackStyle.styleProperty().value());
//		gridStyle.parentComponentStyleProperty().set(stackStyle);
//		Assert.assertEquals(this.defaultShapeStyle, gridStyle.styleProperty().value().getValue());
//		Assert.assertEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, gridStyle.backgroundColorProperty().value().getValue());
//		//Change color on children
//		Color selectColor = Color.ANTIQUEWHITE;
//		gridStyle.backgroundColorProperty().selected().setValue(selectColor);
//		Assert.assertEquals(true, gridStyle.backgroundColorProperty().isSelectedNotNull().get());
//		Assert.assertEquals(selectColor, gridStyle.backgroundColorProperty().value().getValue());
//		//Change to parent
//		stackStyle.backgroundColorProperty().selected().setValue(selectColor);
//		Assert.assertEquals(selectColor, stackStyle.backgroundColorProperty().value().getValue());
//		Assert.assertEquals(selectColor, gridStyle.backgroundColorProperty().value().getValue());
//		//TODO : if possible, but hard to code without a big memory usage
//		//Assert.assertEquals(false, gridStyle.backgroundColorProperty().isSelectedNotNull().get());
//	}
//
//	@Test
//	public void testBinding3Level() {
//		ShapeCompStyle stackStyle = new ShapeCompStyle(null);
//		stackStyle.nameProperty().set("StackStyle");
//		stackStyle.styleProperty().parent().setValue(this.defaultShapeStyle);
//		Assert.assertEquals(this.defaultShapeStyle, stackStyle.styleProperty().value().getValue());
//		Assert.assertEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, stackStyle.backgroundColorProperty().value().getValue());
//		//Create another child
//		ShapeCompStyle gridStyle = new ShapeCompStyle(null);
//		gridStyle.nameProperty().set("GridStyle");
//		gridStyle.styleProperty().parent().bind(stackStyle.styleProperty().value());
//		gridStyle.parentComponentStyleProperty().set(stackStyle);
//		Assert.assertEquals(this.defaultShapeStyle, gridStyle.styleProperty().value().getValue());
//		Assert.assertEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, gridStyle.backgroundColorProperty().value().getValue());
//		//Change color on parent
//		Color selectColor = Color.ANTIQUEWHITE;
//		stackStyle.backgroundColorProperty().selected().setValue(selectColor);
//		Assert.assertNotEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, stackStyle.backgroundColorProperty().value().getValue());
//		Assert.assertEquals(selectColor, stackStyle.backgroundColorProperty().value().getValue());
//		//Check on children
//		Assert.assertEquals(selectColor, gridStyle.backgroundColorProperty().parent().getValue());
//		Assert.assertNotEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, gridStyle.backgroundColorProperty().value().getValue());
//		Assert.assertEquals(selectColor, gridStyle.backgroundColorProperty().value().getValue());
//		//Key style
//		ShapeCompStyle keyStyle = new ShapeCompStyle(null);
//		keyStyle.nameProperty().set("KeyStyle");
//		keyStyle.styleProperty().parent().bind(gridStyle.styleProperty().value());
//		keyStyle.parentComponentStyleProperty().set(gridStyle);
//		//Check values on key
//		Assert.assertNotEquals(StyleTest.DEFAULT_BACKGROUND_COLOR, keyStyle.backgroundColorProperty().value().getValue());
//		Assert.assertEquals(selectColor, keyStyle.backgroundColorProperty().value().getValue());
//		//Override on key
//		Color onKeyColor = Color.AZURE;
//		keyStyle.backgroundColorProperty().selected().setValue(onKeyColor);
//		Assert.assertEquals(onKeyColor, keyStyle.backgroundColorProperty().value().getValue());
//		//Go back
//		keyStyle.backgroundColorProperty().selected().setValue(null);
//		//Override on grid
//		Color onGridColor = Color.AQUA;
//		gridStyle.backgroundColorProperty().selected().setValue(onGridColor);
//		Assert.assertEquals(onGridColor, keyStyle.backgroundColorProperty().value().getValue());
//	}
//
//	@Test
//	public void testXmlSerialize() {
//		//Test write
//		ShapeCompStyle stackStyle = new ShapeCompStyle(null);
//		stackStyle.shapeRadiusProperty().selected().setValue(50);
//		Element element = new Element("Test");
//		XMLObjectSerializer.serializeInto(AbstractShapeCompStyle.class, stackStyle, element);
//		Assert.assertEquals("50", element.getAttributeValue("shapeRadius"));
//		Assert.assertEquals("null", element.getAttributeValue("strokeSize"));
//		//Test read
//		ShapeCompStyle stackToRead = new ShapeCompStyle(null);
//		stackToRead.strokeSizeProperty().selected().setValue(15);
//		XMLObjectSerializer.deserializeInto(AbstractShapeCompStyle.class, stackToRead, element);
//		Assert.assertEquals(50, stackToRead.shapeRadiusProperty().selected().getValue());
//		Assert.assertNull(stackToRead.strokeSizeProperty().selected().getValue());
//	}
}
