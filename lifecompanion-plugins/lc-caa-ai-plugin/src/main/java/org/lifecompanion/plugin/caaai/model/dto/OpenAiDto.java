package org.lifecompanion.plugin.caaai.model.dto;

import org.lifecompanion.plugin.caaai.model.Suggestion;

import java.util.List;
import java.util.Map;

public class OpenAiDto {
    public record SuggestionsRequest(
            String model,
            ResponseFormat response_format,
            List<Message> messages
    ) {
    }

    public static final class Response {
        public List<Choice> choices;

        public Response(List<Choice> choices) {
            this.choices = choices;
        }
    }

    public record JsonSchema(
            String name,
            boolean strict,
            Map<String, Object> schema
    ) {
    }

    public record ResponseFormat(
            String type,
            JsonSchema json_schema
    ) {
    }

    public static final class Message {
        public String role;
        public String name;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public Message(String role, String name, String content) {
            this(role, content);

            this.name = name;
        }
    }

    public static final class Choice {
        public Integer index;
        public Message message;

        public Choice(Integer index, Message message) {
            this.index = index;
            this.message = message;
        }
    }

    public static final class SuggestionsChoice {
        public List<Suggestion> suggestions;

        public SuggestionsChoice(List<Suggestion> suggestions) {
            this.suggestions = suggestions;
        }
    }
}
