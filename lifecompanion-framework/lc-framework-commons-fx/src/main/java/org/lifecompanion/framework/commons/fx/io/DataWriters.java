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

import javafx.scene.paint.Color;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class that hold data writers for each existing data type.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DataWriters {

    // Class part : "Initialization"
    // ========================================================================
    public final static NullDataType NULL_DATA_TYPE = new NullDataType();

    @SuppressWarnings("rawtypes")
    public final static Map<Class, DataTypeI<?>> DATA_TYPES = new HashMap<>();

    static {
        DataWriters.DATA_TYPES.put(Integer.class, new IntegerDataType());
        DataWriters.DATA_TYPES.put(int.class, new IntegerDataType());
        DataWriters.DATA_TYPES.put(Double.class, new DoubleDataType());
        DataWriters.DATA_TYPES.put(double.class, new DoubleDataType());
        DataWriters.DATA_TYPES.put(Long.class, new LongDataType());
        DataWriters.DATA_TYPES.put(long.class, new LongDataType());
        DataWriters.DATA_TYPES.put(Boolean.class, new BooleanDataType());
        DataWriters.DATA_TYPES.put(boolean.class, new BooleanDataType());
        DataWriters.DATA_TYPES.put(Float.class, new FloatDataType());
        DataWriters.DATA_TYPES.put(float.class, new FloatDataType());
        DataWriters.DATA_TYPES.put(String.class, new StringDataType());
        DataWriters.DATA_TYPES.put(Color.class, new ColorDataType());
        DataWriters.DATA_TYPES.put(Date.class, new DateDataType());
        DataWriters.DATA_TYPES.put(File.class, new FileDataType());
    }
    // ========================================================================

    // Class part : "Interface"
    // ========================================================================

    /**
     * Represent a type that can be written/read from/to string
     *
     * @param <T> the data type
     */
    public static interface DataTypeI<T> {
        public String valueToString(T value);

        public T stringToValue(String string);
    }

    public static boolean isValidType(final Class<?> type) {
        return DataWriters.DATA_TYPES.containsKey(type) || type.isEnum();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> DataTypeI<T> getDataType(final Class<?> type) {
        if (type.isEnum()) {
            return new EnumDataType(type);
        }
        return (DataTypeI<T>) DataWriters.DATA_TYPES.get(type);
    }
    // ========================================================================

    // Class part : "Implementations"
    // ========================================================================
    static class NullDataType implements DataTypeI<Object> {

        @Override
        public String valueToString(final Object valueP) {
            return "null";
        }

        @Override
        public Object stringToValue(final String stringP) {
            if ("null".equalsIgnoreCase(stringP)) {
                return null;
            } else {
                // Return something != null if the data is not null
                return true;
            }
        }
    }

    public static abstract class PrintValueDataType<T> implements DataTypeI<T> {
        @Override
        public String valueToString(final T valueP) {
            return valueP != null ? valueToStringIfNotNUll(valueP) : "null";
        }

        protected abstract String valueToStringIfNotNUll(final T valueP);
    }

    private static class IntegerDataType extends PrintValueDataType<Integer> {

        @Override
        public Integer stringToValue(final String stringP) {
            return Integer.parseInt(stringP);
        }

        @Override
        protected String valueToStringIfNotNUll(final Integer valueP) {
            return Integer.toString(valueP);
        }

    }

    private static class DoubleDataType extends PrintValueDataType<Double> {

        @Override
        public Double stringToValue(final String stringP) {
            return Double.parseDouble(stringP);
        }

        @Override
        protected String valueToStringIfNotNUll(final Double valueP) {
            return Double.toString(valueP);
        }

    }

    private static class FloatDataType extends PrintValueDataType<Float> {

        @Override
        public Float stringToValue(final String stringP) {
            return Float.parseFloat(stringP);
        }

        @Override
        protected String valueToStringIfNotNUll(Float valueP) {
            return Float.toString(valueP);
        }

    }

    private static class LongDataType extends PrintValueDataType<Long> {

        @Override
        public Long stringToValue(final String stringP) {
            return Long.parseLong(stringP);
        }

        @Override
        protected String valueToStringIfNotNUll(Long valueP) {
            return Long.toString(valueP);
        }

    }

    // Just to be able to call the datatype
    private static class StringDataType extends PrintValueDataType<String> {

        @Override
        public String stringToValue(final String stringP) {
            return stringP;
        }

        @Override
        protected String valueToStringIfNotNUll(String valueP) {
            return valueP;
        }

    }

    private static class BooleanDataType extends PrintValueDataType<Boolean> {

        @Override
        public Boolean stringToValue(final String stringP) {
            return Boolean.parseBoolean(stringP);
        }

        @Override
        protected String valueToStringIfNotNUll(Boolean valueP) {
            return Boolean.toString(valueP);
        }

    }

    private static class ColorDataType implements DataTypeI<Color> {

        @Override
        public String valueToString(final Color c) {
            return String.format((Locale) null, "#%02x%02x%02x%02x",
                    Math.round(c.getRed() * 255.0),
                    Math.round(c.getGreen() * 255.0),
                    Math.round(c.getBlue() * 255.0),
                    Math.round(c.getOpacity() * 255.0)
            );
        }

        @Override
        public Color stringToValue(final String stringP) {
            // Backward compatibility
            if (!stringP.startsWith("#") || stringP.contains(";")) {
                String[] split = stringP.split(";");
                return Color.rgb(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Double.parseDouble(split[3]));
            } else {
                return Color.rgb(
                        Integer.valueOf(stringP.substring(1, 3), 16),
                        Integer.valueOf(stringP.substring(3, 5), 16),
                        Integer.valueOf(stringP.substring(5, 7), 16),
                        Integer.valueOf(stringP.substring(7, 9), 16) / 255.0
                );
            }
        }
    }

    private static class DateDataType implements DataTypeI<Date> {

        @Override
        public String valueToString(final Date valueP) {
            return Long.toString(valueP.getTime());
        }

        @Override
        public Date stringToValue(final String stringP) {
            return new Date(Long.parseLong(stringP));
        }

    }

    private static class EnumDataType<T extends Enum<T>> implements DataTypeI<Enum<T>> {
        private Class<T> enumType;

        EnumDataType(final Class<T> enumTypeP) {
            this.enumType = enumTypeP;
        }

        @Override
        public String valueToString(final Enum<T> valueP) {
            return valueP.name();
        }

        @Override
        public Enum<T> stringToValue(final String stringP) {
            return Enum.valueOf(this.enumType, stringP);
        }
    }

    private static class FileDataType implements DataTypeI<File> {

        @Override
        public String valueToString(final File valueP) {
            return valueP.getAbsolutePath();
        }

        @Override
        public File stringToValue(final String stringP) {
            return new File(stringP);
        }
    }
    // ========================================================================

}
