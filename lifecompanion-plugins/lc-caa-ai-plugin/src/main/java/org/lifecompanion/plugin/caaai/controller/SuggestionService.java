/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.caaai.controller;

import com.google.gson.*;
import javafx.collections.ObservableList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.plugin.caaai.model.dto.RequestPredictionContextDto;
import org.lifecompanion.plugin.caaai.model.dto.SuggestionResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.List;

public enum SuggestionService {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestionService.class);

    private int predictionCount = 0;
    private final Gson gson;

    SuggestionService() {
        gson = JsonHelper.GSON.newBuilder()
                .registerTypeAdapter(ObservableList.class, new MyCustomJsonAdapter())
                .create();
    }

    public List<String> getSuggestions(String textBeforeCaret, int wantedCount) {
        List<String> suggested = List.of((predictionCount++) + " ça va et toi ?", "Je commence à avoir faim", "Tout roule");

        OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();
        try (Response response = okHttpClient.newCall(new Request.Builder().url("http://localhost:80/api/prediction")
                .post(RequestBody.create(gson.toJson(new RequestPredictionContextDto(textBeforeCaret, "todo")), null))
                .addHeader("Content-Type", "application/vnd.api+json")
                .build()).execute()) {
            if (response.isSuccessful()) {
                suggested = gson.fromJson(response.body().string(), SuggestionResponseDto.class).suggestions();
            }
        } catch (Exception e) {
            LOGGER.warn("Error when calling : {}", e.getMessage());
        }

        return suggested;
    }

    private static class MyCustomJsonAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
        @Override
        public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return src != null ? new JsonPrimitive(src.toString()) : JsonNull.INSTANCE;
        }

        @Override
        public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json == JsonNull.INSTANCE ? null : ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString());
        }
    }
}
