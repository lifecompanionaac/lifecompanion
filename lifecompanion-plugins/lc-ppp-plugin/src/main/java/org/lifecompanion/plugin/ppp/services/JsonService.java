package org.lifecompanion.plugin.ppp.services;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class JsonService {
    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(ZonedDateTime.class, new JsonService.ZonedDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate.class, new JsonService.LocalDateTypeAdapter())
            .setPrettyPrinting()
            .create();

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
