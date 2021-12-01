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

import javafx.beans.property.*;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Tool class to serialize base object field to XML using {@link XMLUtils}.<br>
 * Object properties can be ignored with transient.<br>
 * All possible attribute type can be found by check {@link DataWriters#DATA_TYPES}.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class XMLObjectSerializer {
    private final static Logger LOGGER = LoggerFactory.getLogger(XMLObjectSerializer.class);

    private static final List<Class<?>> VALID_PROPERTY_TYPE = Arrays.asList(DoubleProperty.class, IntegerProperty.class, LongProperty.class,
            FloatProperty.class, StringProperty.class, BooleanProperty.class);

    @SuppressWarnings("rawtypes")
    private static final Map<Class<CustomPropertyConverter>, CustomPropertyConverter> CACHED_CONVERTER = new HashMap<>();

    /**
     * Cache the field to serialize/deserialize for a given type.<br>
     * Done because Reflection cost runtime perf and serialized type are not modified on runtime.
     */
    private static final Map<Class<?>, List<Field>> CACHED_FIELD_FOR_TYPE = new HashMap<>(50);

    /**
     * Serialize the attribute of a given class type into a XML element.<br>
     * Will ignore transient attribute.
     *
     * @param type     type of instance
     * @param instance the instance that contains attribute values
     * @param element  the element we want to serialize instance attributes in
     */
    public static Element serializeInto(final Class<?> type, final Object instance, final Element element) {
        // Get all the valid fields
        List<Field> validFields = XMLObjectSerializer.getAllValidAccessibleField(type);
        for (Field field : validFields) {
            // Property
            if (Property.class.isAssignableFrom(field.getType())) {
                Property<?> prop = XMLObjectSerializer.getFieldValue(instance, field);
                if (prop != null && XMLObjectSerializer.isValidPropertyField(field)) {
                    XMLObjectSerializer.writeFieldValueIn(field, prop.getValue(), element);
                }
            }
            // Primitive
            else if (isDirectlyAssignedField(field)) {
                Object fieldValue = XMLObjectSerializer.getFieldValue(instance, field);
                XMLObjectSerializer.writeFieldValueIn(field, fieldValue, element);
            }
            //Custom converter
            else if (field.isAnnotationPresent(XMLCustomProperty.class)) {
                writeCustomProperty(instance, field, element);
            }
        }
        return element;
    }

    /**
     * Deserialize the class attributes from the given element into the given instance.<br>
     * Ignore transient attributes.
     * If there is no xml attribute corresponding to a type attribute, this will not fail and just ignore it.
     *
     * @param type     type of instance
     * @param instance instance that contains attribute we want to change value
     * @param element  the element that contains attribute values
     */
    public static void deserializeInto(final Class<?> type, final Object instance, final Element element) {
        // Get all the valid fields
        List<Field> validFields = XMLObjectSerializer.getAllValidAccessibleField(type);
        for (Field field : validFields) {
            try {
                // Property
                if (Property.class.isAssignableFrom(field.getType())) {
                    Property<?> prop = XMLObjectSerializer.getFieldValue(instance, field);
                    if (prop != null && XMLObjectSerializer.isValidPropertyField(field)) {
                        // For a property, we determine the type with gen
                        prop.setValue(XMLObjectSerializer.readFieldValue(field, element, XMLObjectSerializer.getPropertyGenericType(prop, field)));
                    }
                }
                // "Primitive"
                else if (isDirectlyAssignedField(field)) {
                    XMLObjectSerializer.setFieldValue(field, instance, XMLObjectSerializer.readFieldValue(field, element, null));
                }
                //Custom converter
                else if (field.isAnnotationPresent(XMLCustomProperty.class)) {
                    readCustomProperty(instance, field, element);
                }
            } catch (Exception e) {
                XMLObjectSerializer.LOGGER.warn("Couldn't read the field {}, value didn't change", field.getName(), e);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void readCustomProperty(final Object instance, Field field, Element element) {
        Object customFieldValue = XMLObjectSerializer.getFieldValue(instance, field);
        XMLCustomProperty customProp = field.getAnnotation(XMLCustomProperty.class);
        CustomPropertyConverter converterFor = getConverterFor(customProp);
        converterFor.setValue(customFieldValue, XMLObjectSerializer.readFieldValue(field, element, customProp.value()));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void writeCustomProperty(final Object instance, Field field, Element element) {
        Object customFieldValue = XMLObjectSerializer.getFieldValue(instance, field);
        XMLCustomProperty customProp = field.getAnnotation(XMLCustomProperty.class);
        CustomPropertyConverter converterFor = getConverterFor(customProp);
        XMLObjectSerializer.writeFieldValueIn(field, converterFor.getValue(customFieldValue), element);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static CustomPropertyConverter getConverterFor(XMLCustomProperty customProp) {
        Class<? extends CustomPropertyConverter> converterType = customProp.converter();
        if (!CACHED_CONVERTER.containsKey(converterType)) {
            try {
                CACHED_CONVERTER.put((Class<CustomPropertyConverter>) converterType, converterType.newInstance());
            } catch (Exception e) {
                LOGGER.error("Couldn't create the custom property converter", e);
            }
        }
        return CACHED_CONVERTER.get((Class<CustomPropertyConverter>) converterType);
    }

    private static boolean isValidPropertyField(final Field field) {
        Class<?> propertyType = field.getType();
        //Check annotation
        if (ObjectProperty.class.isAssignableFrom(propertyType) && field.isAnnotationPresent(XMLGenericProperty.class)) {
            XMLGenericProperty annotation = field.getAnnotation(XMLGenericProperty.class);
            if (annotation.value() == null) {
                XMLObjectSerializer.LOGGER
                        .warn("Found a  XMLGenericProperty annotation without value inside, may that you didn't use it correctly ?");
                return false;
            } else {
                return true;
            }
        }
        for (Class<? extends Object> validType : XMLObjectSerializer.VALID_PROPERTY_TYPE) {
            if (validType.isAssignableFrom(propertyType)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"unchecked"})
    private static <T> Class<T> getPropertyGenericType(final Property<T> prop, final Field field) {
        Class<Property<T>> propertyClass = (Class<Property<T>>) prop.getClass();
        Class<?> result = null;
        // Primitive properties
        if (DoubleProperty.class.isAssignableFrom(propertyClass)) {
            result = Double.class;
        } else if (IntegerProperty.class.isAssignableFrom(propertyClass)) {
            result = Integer.class;
        } else if (FloatProperty.class.isAssignableFrom(propertyClass)) {
            result = Float.class;
        } else if (LongProperty.class.isAssignableFrom(propertyClass)) {
            result = Long.class;
        } else if (StringProperty.class.isAssignableFrom(propertyClass)) {
            result = String.class;
        } else if (BooleanProperty.class.isAssignableFrom(propertyClass)) {
            result = Boolean.class;
        }
        // Custom property for simple serializable types
        else if (ObjectProperty.class.isAssignableFrom(propertyClass) && field.isAnnotationPresent(XMLGenericProperty.class)) {
            XMLGenericProperty annotation = field.getAnnotation(XMLGenericProperty.class);
            result = annotation.value();
        } else {
            XMLObjectSerializer.LOGGER.warn("Didn't find any valid type for property {}, will return a null type", prop.getClass().getSimpleName());
        }
        return (Class<T>) result;
    }

    private static void setFieldValue(final Field field, final Object object, final Object value) {
        try {
            field.set(object, value);
        } catch (Exception e) {
            XMLObjectSerializer.LOGGER.warn("Couldn't set value from {}", field.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T readFieldValue(final Field field, final Element element, final Class<?> valueType) {
        Attribute attribute = element.getAttribute(field.getName());
        if (attribute != null) {
            String elementValue = attribute.getValue();
            Object valueNull = DataWriters.NULL_DATA_TYPE.stringToValue(elementValue);
            // Because  property doesn't know the type "inside"
            Class<?> realFieldType = valueType != null ? valueType : field.getType();
            if (valueNull != null && DataWriters.isValidType(realFieldType)) {
                return (T) DataWriters.getDataType(realFieldType).stringToValue(elementValue);
            } else {
                return null;
            }
        } else if (field.getAnnotation(XMLIgnoreNullValue.class) != null) {
            return null;
        } else if (field.getAnnotation(XMLIgnoreDefaultBooleanValue.class) != null) {
            Object val = field.getAnnotation(XMLIgnoreDefaultBooleanValue.class).value();
            return (T) val;
        } else if (field.getAnnotation(XMLIgnoreDefaultDoubleValue.class) != null) {
            Object val = field.getAnnotation(XMLIgnoreDefaultDoubleValue.class).value();
            return (T) val;
        } else {
            throw new IllegalArgumentException("The xml element doesn't contains any field for the name " + field.getName());
        }
    }

    private static void writeFieldValueIn(final Field field, final Object value, final Element element) {
        if (value == null) {
            if (field.getAnnotation(XMLIgnoreNullValue.class) == null) {
                element.setAttribute(field.getName(), DataWriters.NULL_DATA_TYPE.valueToString(value));
            }
        } else if (checkIfNonNullValueShouldBeWritten(field, value)) {
            element.setAttribute(field.getName(), DataWriters.getDataType(value.getClass()).valueToString(value));
        }
    }

    private static boolean checkIfNonNullValueShouldBeWritten(final Field field, final Object value) {
        if (value instanceof Boolean) {
            final XMLIgnoreDefaultBooleanValue xmlIgnoreDefaultBoolean = field.getAnnotation(XMLIgnoreDefaultBooleanValue.class);
            if (xmlIgnoreDefaultBoolean != null && Objects.equals(value, xmlIgnoreDefaultBoolean.value())) {
                return false;
            }
        }
        if (value instanceof Double) {
            final XMLIgnoreDefaultDoubleValue xmlIgnoreDefaultDoubleValue = field.getAnnotation(XMLIgnoreDefaultDoubleValue.class);
            if (xmlIgnoreDefaultDoubleValue != null && Objects.equals(value, xmlIgnoreDefaultDoubleValue.value())) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getFieldValue(final Object object, final Field field) {
        try {
            return (T) field.get(object);
        } catch (Exception e) {
            XMLObjectSerializer.LOGGER.warn("Couldn't get the value from {}, will return a null value", field.getName(), e);
            return null;
        }
    }

    /**
     * Return all the field of a object that are not ignored, that are primitive
     * type, or property.<br>
     * Will try for each field to set it accessible.
     *
     * @param type the object type
     * @return the list of all valid field.
     */
    private static List<Field> getAllValidAccessibleField(final Class<?> type) {
        if (!CACHED_FIELD_FOR_TYPE.containsKey(type)) {
            ArrayList<Field> validField = new ArrayList<>();
            Field[] declaredFields = type.getDeclaredFields();
            for (Field field : declaredFields) {
                // Is a property or a primitive, and is not ignored
                if (isFieldValidType(field) && isFieldModifiersValid(field)) {
                    try {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        validField.add(field);
                    } catch (Exception e) {
                        XMLObjectSerializer.LOGGER.warn("Couldn't set the field {} accessible", field.getName(), e);
                    }
                }
            }
            CACHED_FIELD_FOR_TYPE.put(type, validField);
        }
        return CACHED_FIELD_FOR_TYPE.get(type);
    }

    private static boolean isFieldValidType(Field field) {
        return Property.class.isAssignableFrom(field.getType()) || isDirectlyAssignedField(field)
                || field.isAnnotationPresent(XMLCustomProperty.class);
    }

    private static boolean isDirectlyAssignedField(Field field) {
        return DataWriters.isValidType(field.getType());
    }

    private static boolean isFieldModifiersValid(Field field) {
        int modifiers = field.getModifiers();
        return (!isDirectlyAssignedField(field) || !Modifier.isFinal(modifiers)) && !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }
}
