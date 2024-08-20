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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public enum SuggestionService {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestionService.class);

    private final Gson gson;

    SuggestionService() {
        gson = JsonHelper.GSON.newBuilder()
                .registerTypeAdapter(ObservableList.class, new MyCustomJsonAdapter())
                .create();
    }

    public List<String> getSuggestions(String endpoint, String token, String textBeforeCaret, int wantedCount) {
        List<String> suggested = List.of();

        OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();
        try (Response response = okHttpClient.newCall(new Request.Builder().url(endpoint)
                .post(RequestBody.create(gson.toJson(this.makeOpenAiRequest(textBeforeCaret, wantedCount)), null))
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()).execute()) {
            if (response.isSuccessful()) {
                suggested = gson.fromJson(
                        gson.fromJson(response.body().string(), OpenAiResponseDto.class).choices.getFirst().message.content,
                        OpenAiSuggestions.class).suggestions;
            }
        } catch (Exception e) {
            LOGGER.warn("Error when calling : {}", e.getMessage());
        }

        return suggested;
    }

    private OpenAiRequestDto makeOpenAiRequest(String textBeforeCaret, int wantedCount) {
        JsonSchemaDto schema = new JsonSchemaDto(
                "object",
                new JsonSchemaPropertiesDto(new JsonSchemaArrayPropDto("array", new JsonSchemaArrayItemsDto("string"))),
                List.of("suggestions"),
                false);
        OpenAiResponseFormatDto responseFormat = new OpenAiResponseFormatDto(
                "json_schema",
                new OpenAiJsonSchemaDto("suggestions", true, schema));

        StringBuilder systemMessage = new StringBuilder();

        systemMessage.append("Tu m'aides à finir mes phrases.\n");
        systemMessage.append("Propose ");
        systemMessage.append(wantedCount);
        systemMessage.append(" suggestions dans un tableau JSON.\n");
        systemMessage.append("Les suggestions doivent être courtes et simples.\n");

        // Context.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        List<String> contextValues = List.of(
                "je suis chez moi",
                "je discute avec un ami",
                "il est " + dtf.format(ZonedDateTime.now(ZoneId.of("Europe/Paris"))));

        systemMessage.append("Voici le contexte : ");
        systemMessage.append(String.join(", ", contextValues));
        systemMessage.append("\n");

        // Conversation.
        /*
        List<String> contextConversation = List.of(
                "salut ça va ?",
                "ça va bien et toi ?",
                "ça va merci.");

        systemMessage.append("Voici l'historique de discussion :\n");
        systemMessage.append(String.join("\n", contextConversation));
         */

        LOGGER.info("System prompt: {}", systemMessage);

        return new OpenAiRequestDto(
                "gpt-4o-mini",
                responseFormat,
                List.of(
                        new OpenAiMessageDto("system", systemMessage.toString()),
                        new OpenAiMessageDto("user", textBeforeCaret)));
    }

    private record JsonSchemaDto(
            String type,
            JsonSchemaPropertiesDto properties,
            List<String> required,
            boolean additionalProperties
    ) {
    }

    private record JsonSchemaPropertiesDto(
            JsonSchemaArrayPropDto suggestions
    ) {
    }

    private record JsonSchemaArrayPropDto(
            String type,
            JsonSchemaArrayItemsDto items
    ) {
    }

    private record JsonSchemaArrayItemsDto(
            String type
    ) {
    }

    private record OpenAiJsonSchemaDto(
            String name,
            boolean strict,
            JsonSchemaDto schema
    ) {
    }

    private record OpenAiResponseFormatDto(
            String type,
            OpenAiJsonSchemaDto json_schema
    ) {
    }

    private record OpenAiRequestDto(
            String model,
            OpenAiResponseFormatDto response_format,
            List<OpenAiMessageDto> messages
    ) {
    }

    private static final class OpenAiMessageDto {
        private String role;
        private String content;

        public OpenAiMessageDto(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private static final class OpenAiChoiceDto {
        private Integer index;
        private OpenAiMessageDto message;

        public OpenAiChoiceDto(Integer index, OpenAiMessageDto message) {
            this.index = index;
            this.message = message;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public OpenAiMessageDto getMessage() {
            return message;
        }

        public void setMessage(OpenAiMessageDto message) {
            this.message = message;
        }
    }

    private static final class OpenAiResponseDto {
        private List<OpenAiChoiceDto> choices;

        public OpenAiResponseDto(List<OpenAiChoiceDto> choices) {
            this.choices = choices;
        }

        public List<OpenAiChoiceDto> getChoices() {
            return choices;
        }

        public void setChoices(List<OpenAiChoiceDto> choices) {
            this.choices = choices;
        }
    }

    private static final class OpenAiSuggestions {
        private List<String> suggestions;

        public OpenAiSuggestions(List<String> suggestions) {
            this.suggestions = suggestions;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        public void setSuggestions(List<String> suggestions) {
            this.suggestions = suggestions;
        }
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
