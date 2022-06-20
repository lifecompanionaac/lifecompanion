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
package org.lifecompanion.framework.commons.fx.io;

import java.util.Date;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 * Class to easily read/write information in XML.<br>
 * This class is usefull to silently log exception/error and to retrieve values in properties.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class XMLUtils {
	private final static Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);

	private XMLUtils() {}

	// Class part : "Read"
	//========================================================================
	public static void read(final DoubleProperty prop, final String name, final Element node) {
		try {
			prop.set(node.getAttribute(name).getDoubleValue());
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The double property with the name \"" + name + "\" can't be read from " + node, e);
		}
	}

	public static void read(final BooleanProperty prop, final String name, final Element node) {
		try {
			prop.set(node.getAttribute(name).getBooleanValue());
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The boolean property with the name \"" + name + "\" can't be read from " + node, e);
		}
	}

	public static void read(final IntegerProperty prop, final String name, final Element node) {
		try {
			prop.set(node.getAttribute(name).getIntValue());
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The integer property with the name \"" + name + "\" can't be read from " + node, e);
		}
	}

	public static void read(final StringProperty prop, final String name, final Element node) {
		try {
			String value = node.getAttributeValue(name);
			if ("null".equals(value)) {
				value = null;
			}
			prop.set(value);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The String property with the name \"" + name + "\" can't be read from " + node, e);
		}
	}

	public static String readString(final String name, final Element node) {
		try {
			String value = node.getAttributeValue(name);
			if ("null".equals(value)) {
				value = null;
			}
			return value;
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The String with the name \"" + name + "\" can't be read from " + node, e);
			return null;
		}
	}

	public static int readInt(final String name, final Element node) {
		try {
			String value = node.getAttributeValue(name);
			return Integer.parseInt(value);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Integer with the name \"" + name + "\" can't be read from " + node, e);
			return 0;
		}
	}

	public static double readDouble(final String name, final Element node) {
		try {
			String value = node.getAttributeValue(name);
			return Double.parseDouble(value);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Double with the name \"" + name + "\" can't be read from " + node, e);
			return 0.0;
		}
	}

	public static boolean readBool(final String name, final Element node) {
		try {
			String value = node.getAttributeValue(name);
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Boolean with the name \"" + name + "\" can't be read from " + node, e);
			return false;
		}
	}

	public static <T extends Enum<T>> Enum<T> readEnum(final Class<T> enumType, final String name, final Element node) {
		try {
			String value = node.getAttributeValue(name);
			if ("null".equals(value)) {
				return null;
			} else {
				return Enum.valueOf(enumType, value);
			}
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Enum with the name \"" + name + "\" can't be read from " + node, e);
			return null;
		}
	}

	public static Color readColor(final String name, final Element node) {
		try {
			String value = node.getAttributeValue(name);
			if ("null".equals(value)) {
				return null;
			} else {
				String[] split = value.split(";");
				return Color.rgb(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Double.parseDouble(split[3]));
			}
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Color with the name \"" + name + "\" can't be read from " + node, e);
			return null;
		}
	}

	public static void readColor(final ObjectProperty<Color> prop, final String name, final Element node) {
		prop.set(XMLUtils.readColor(name, node));
	}

	public static void readDate(final ObjectProperty<Date> prop, final String name, final Element node) {
		prop.set(XMLUtils.readDate(name, node));
	}

	public static Date readDate(final String name, final Element node) {
		try {
			String value = node.getAttributeValue(name);
			if ("null".equals(value)) {
				return null;
			} else {
				return new Date(Long.parseLong(value));
			}
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Date with the name \"" + name + "\" can't be read from " + node, e);
			return null;
		}
	}
	//========================================================================

	// Class part : "Write"
	//========================================================================
	public static void write(final DoubleProperty prop, final String name, final Element node) {
		try {
			node.setAttribute(name, "" + prop.get());
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The double property with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void write(final IntegerProperty prop, final String name, final Element node) {
		try {
			node.setAttribute(name, "" + prop.get());
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The integer property with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void write(final BooleanProperty prop, final String name, final Element node) {
		try {
			node.setAttribute(name, "" + prop.get());
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The boolean property with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void write(final boolean prop, final String name, final Element node) {
		try {
			node.setAttribute(name, "" + prop);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The boolean with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void write(final String str, final String name, final Element node) {
		try {
			node.setAttribute(name, "" + str);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The String with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void write(final StringProperty str, final String name, final Element node) {
		try {
			node.setAttribute(name, "" + str.get());
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The StringProperty with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void write(final Integer val, final String name, final Element node) {
		try {
			node.setAttribute(name, "" + val);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Integer with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void write(final Enum<?> val, final String name, final Element node) {
		try {
			String strVal = val != null ? val.name() : "null";
			node.setAttribute(name, strVal);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Enum with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void write(final Color val, final String name, final Element node) {
		try {
			String strVal = val != null ? "" + (int) (val.getRed() * 255) + ";" + (int) (val.getGreen() * 255) + ";" + (int) (val.getBlue() * 255)
					+ ";" + val.getOpacity() : "null";
			node.setAttribute(name, strVal);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Color with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void write(final Date date, final String name, final Element node) {
		try {
			String strVal = date != null ? "" + date.getTime() : "null";
			node.setAttribute(name, strVal);
		} catch (Exception e) {
			XMLUtils.LOGGER.warn("The Date with the name \"" + name + "\" can't be writen into " + node, e);
		}
	}

	public static void writeColor(final ObjectProperty<Color> prop, final String name, final Element node) {
		write(prop.get(), name, node);
	}

	public static void writeDate(final ObjectProperty<Date> prop, final String name, final Element node) {
		write(prop.get(), name, node);
	}
	//========================================================================

}
