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

package org.lifecompanion.base.data.io.json;

import com.google.gson.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.api.image2.ImageElementI;
import org.lifecompanion.base.data.image2.ImageElement;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class JsonHelper {
    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(StringProperty.class, new StringPropertyTypeAdapter())
            .registerTypeAdapter(BooleanProperty.class, new BooleanPropertyTypeAdapter())
            .registerTypeAdapter(ImageElementI.class, new ImageElementCreator())
            .registerTypeAdapter(ObservableList.class, new ObservableListCreator())
            .registerTypeAdapter(File.class, new FileTypeAdapter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .setPrettyPrinting()
            .create();

    private static class StringPropertyTypeAdapter implements JsonSerializer<StringProperty>, JsonDeserializer<StringProperty> {
        @Override
        public JsonElement serialize(StringProperty src, Type typeOfSrc, JsonSerializationContext context) {
            return src != null && src.get() != null ? new JsonPrimitive(src.get()) : JsonNull.INSTANCE;
        }

        @Override
        public StringProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json == JsonNull.INSTANCE ? new SimpleStringProperty(null) : new SimpleStringProperty(json.getAsJsonPrimitive().getAsString());
        }
    }

    private static class FileTypeAdapter implements JsonSerializer<File>, JsonDeserializer<File> {
        @Override
        public JsonElement serialize(File src, Type typeOfSrc, JsonSerializationContext context) {
            return src != null ? new JsonPrimitive(src.getPath()) : JsonNull.INSTANCE;
        }

        @Override
        public File deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json == JsonNull.INSTANCE ? null : new File(json.getAsJsonPrimitive().getAsString());
        }
    }

    private static class BooleanPropertyTypeAdapter implements JsonSerializer<BooleanProperty>, JsonDeserializer<BooleanProperty> {
        @Override
        public BooleanProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json == JsonNull.INSTANCE ? new SimpleBooleanProperty(false) : new SimpleBooleanProperty(json.getAsJsonPrimitive().getAsBoolean());
        }

        @Override
        public JsonElement serialize(BooleanProperty src, Type typeOfSrc, JsonSerializationContext context) {
            return src != null ? new JsonPrimitive(src.get()) : JsonNull.INSTANCE;
        }
    }

    private static class ImageElementCreator implements InstanceCreator<ImageElementI>, JsonDeserializer<ImageElementI>, JsonSerializer<ImageElementI> {
        @Override
        public ImageElementI createInstance(Type type) {
            return new ImageElement();
        }

        @Override
        public ImageElementI deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return context.deserialize(json, ImageElement.class);
        }

        @Override
        public JsonElement serialize(ImageElementI src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src, ImageElement.class);
        }
    }

    private static class ObservableListCreator implements InstanceCreator<ObservableList<?>> {
        @Override
        public ObservableList<?> createInstance(Type type) {
            return FXCollections.observableArrayList();
        }
    }

    private static class ZonedDateTimeTypeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
        @Override
        public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return src != null ? new JsonPrimitive(src.toString()) : JsonNull.INSTANCE;
        }

        @Override
        public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json == JsonNull.INSTANCE ? null : ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString());
        }
    }

    private static class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return src != null ? new JsonPrimitive(src.toString()) : JsonNull.INSTANCE;
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json == JsonNull.INSTANCE ? null : LocalDate.parse(json.getAsJsonPrimitive().getAsString());
        }
    }
}
