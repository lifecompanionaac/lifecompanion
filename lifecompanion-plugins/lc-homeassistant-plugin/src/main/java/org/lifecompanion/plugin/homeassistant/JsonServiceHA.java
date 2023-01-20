package org.lifecompanion.plugin.homeassistant;

import com.google.gson.*;

import java.lang.reflect.Type;

public class JsonServiceHA {
    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
//            .registerTypeAdapter(OHABItemType.class, new JsonServiceHA.OHABItemTypeAdapter())
            .setPrettyPrinting()
            .create();

    //    private static class OHABItemTypeAdapter implements JsonSerializer<OHABItemType>, JsonDeserializer<OHABItemType> {
    //        @Override
    //        public OHABItemType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    //            return json == JsonNull.INSTANCE ? OHABItemType.UNKNOWN : OHABItemType.fromJson(json.getAsJsonPrimitive().getAsString());
    //        }
    //
    //        @Override
    //        public JsonElement serialize(OHABItemType src, Type typeOfSrc, JsonSerializationContext context) {
    //            return src != null ? new JsonPrimitive(src.name()) : JsonNull.INSTANCE;
    //        }
    //    }
}
