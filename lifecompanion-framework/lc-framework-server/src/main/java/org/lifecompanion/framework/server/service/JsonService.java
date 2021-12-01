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

package org.lifecompanion.framework.server.service;

import com.google.gson.*;
import org.lifecompanion.framework.model.server.LifeCompanionFrameworkServerConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JsonService {

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    private static final Gson GSON_UTC = new GsonBuilder().registerTypeAdapter(Date.class, new UTCDateAdapter()).create();

    public static Gson json() {
        return GSON;
    }

    public static Gson jsonUTC() {
        return GSON_UTC;
    }

    public static String toJson(Object src) {
        return src == null ? LifeCompanionFrameworkServerConstant.EMPTY_JSON_OBJECT : json().toJson(src);
    }

    public static <T> T fromJson(String str, Class<T> type) {
        return json().fromJson(str, type);
    }

    public static <T> T fromJson(InputStream is, Class<T> type) {
        try {
            return json().fromJson(new InputStreamReader(is), type);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    public static class UTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
        private final DateFormat DATE_FORMAT_UTC;

        public UTCDateAdapter() {
            DATE_FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            DATE_FORMAT_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(DATE_FORMAT_UTC.format(date));
        }

        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            try {
                return DATE_FORMAT_UTC.parse(jsonElement.getAsString());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }
    }
}
