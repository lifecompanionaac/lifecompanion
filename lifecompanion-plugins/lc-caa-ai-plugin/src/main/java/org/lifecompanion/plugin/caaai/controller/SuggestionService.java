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
import org.lifecompanion.plugin.caaai.model.Suggestion;
import org.lifecompanion.plugin.caaai.model.dto.OpenAiDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SuggestionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestionService.class);

    private final Gson gson;

    private final String endpoint;
    private final String token;

    private final List<Suggestion> suggestions;
    private final List<OpenAiDto.Message> messages;

    public SuggestionService(String endpoint, String token) {
        gson = JsonHelper.GSON.newBuilder()
                .registerTypeAdapter(ObservableList.class, new MyCustomJsonAdapter())
                .create();

        this.endpoint = endpoint;
        this.token = token;

        this.messages = new ArrayList<>();
        this.suggestions = new ArrayList<>();
    }

    public void initConversation(Integer wantedCount) {
        // TODO System message.
        // TODO First user message (backstory, etc.).

        String systemMessage = "Tu es un assistant intégré dans un outil de communication alternative et amélioré (CAA). Ton rôle est de me faciliter l'accès à la communication. Il peut y avoir une conversation engagée avec plusieurs utilisateurs différents : me correspond à moi-même (l'utilisateur courant) et other est un intervenant externe. Tu peux aussi simplement compléter les mots ou les phrases que je souhaite écrire. Propose à chaque fois "
                + wantedCount
                + " alternatives dans un tableau JSON. Ces propositions doivent être courtes (3-5 mots), compréhensibles et toujours en français.";

        // Initial context for user.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        List<String> userOriginalContextValues = List.of(
                "je suis chez moi",
                "je suis de bonne humeur",
                "il est " + dtf.format(ZonedDateTime.now(ZoneId.of("Europe/Paris"))));

        String userOriginalMessage =
                "Je suis en situation de handicap et j'ai des difficultés dans la compréhension et/ou la production du langage. "
                        + String.join(", ", userOriginalContextValues) + ".";

        this.messages.add(new OpenAiDto.Message("system", systemMessage));
        this.handleOwnMessage(userOriginalMessage);
    }

    public void stopConversation() {
        this.messages.clear();
        this.suggestions.clear();
    }

    public void handleOwnMessage(String content) {
        this.messages.add(new OpenAiDto.Message("user", "me", content));
    }

    public void handleInterlocutorMessage(String content) {
        this.messages.add(new OpenAiDto.Message("user", "other", content));
    }

    public List<Suggestion> suggestSentences(String text) {
        List<OpenAiDto.Message> requestMessages = new ArrayList<>(this.messages);

        if (!text.isBlank()) {
            requestMessages.add(new OpenAiDto.Message("user", "me", "Propose-moi des alternatives pour compléter ma phrase."));
            requestMessages.add(new OpenAiDto.Message("user", "me", text));
        } else if (this.messages.size() > 2) {
            requestMessages.add(new OpenAiDto.Message("user", "me", "Propose-moi des alternatives pour continuer la discussion."));
        } else {
            requestMessages.add(new OpenAiDto.Message("user", "me", "Propose-moi des alternatives pour engager une conversation ou exprimer un besoin lié à ma situation."));
        }

        for (OpenAiDto.Message message : requestMessages) {
            LOGGER.info("Message from {}: {}", message.name, message.content);
        }

        return this.fetchSuggestions(requestMessages);
    }

    public List<Suggestion> retrySuggestSentences() {
        // TODO
        return null;
    }

    private List<Suggestion> fetchSuggestions(List<OpenAiDto.Message> messages) {
        List<Suggestion> suggested = List.of();

        OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();
        try (Response response = okHttpClient.newCall(new Request.Builder().url(this.endpoint)
                .post(RequestBody.create(this.gson.toJson(this.prepareSuggestionsRequest(messages)), null))
                .addHeader("Authorization", "Bearer " + this.token)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()).execute()) {
            if (response.isSuccessful()) {
                suggested = gson.fromJson(
                        gson.fromJson(response.body().string(), OpenAiDto.Response.class).choices.getFirst().message.content,
                        OpenAiDto.SuggestionsChoice.class).suggestions;
            } else {
                LOGGER.warn("Error when calling : {}", response.body().string());
            }
        } catch (Exception e) {
            LOGGER.warn("Error when calling : {}", e.getMessage());
        }

        for (Suggestion suggestion : suggested) {
            LOGGER.warn("Suggestion : {}", suggestion.content());
        }

        this.suggestions.addAll(suggested);

        return suggested;
    }

    private OpenAiDto.SuggestionsRequest prepareSuggestionsRequest(List<OpenAiDto.Message> messages) {
        Map<String, Object> jsonSchema = Map.of(
                "type", "object",
                "required", List.of("suggestions"),
                "additionalProperties", false,
                "properties", Map.of(
                        "suggestions", Map.of(
                                "type", "array",
                                "items", Map.of(
                                        "type", "object",
                                        "required", List.of("content"),
                                        "additionalProperties", false,
                                        "properties", Map.of(
                                                "content", Map.of(
                                                        "type", "string"
                                                )
                                        )
                                )
                        )
                ));

        OpenAiDto.ResponseFormat responseFormat = new OpenAiDto.ResponseFormat(
                "json_schema",
                new OpenAiDto.JsonSchema("suggestions", true, jsonSchema));

        return new OpenAiDto.SuggestionsRequest("gpt-4o-mini", responseFormat, messages);
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
