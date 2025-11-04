package org.lifecompanion.plugin.translate.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.util.javafx.FXThreadUtils;

import java.io.IOException;

public enum TranslateController {
    INSTANCE;

    TranslateController() {
    }

    public void testTranslate() {
        LCConfigurationI configuration = AppModeController.INSTANCE.getUseModeContext().getConfiguration();
        if (configuration != null) {
            configuration.getAllComponent().values()
                    .stream()
                    .filter(c -> c instanceof GridPartKeyComponentI)
                    .map(c -> (GridPartKeyComponentI) c)
                    .forEach(key -> {
                        String original = key.textContentProperty().get();
                        if (StringUtils.isNotBlank(original)) {
                            String translated = translate(original);
                            FXThreadUtils.runOnFXThread(() -> {
                                key.textContentProperty().set(translated);
                            });
                        }
                    });
        }
    }

    private String translate(String original) {
        OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();
        try (Response response = okHttpClient.newCall(new Request.Builder().url("http://localhost:8000/translate")
                .post(RequestBody.create(JsonHelper.GSON.toJson(new TranslateRequest("fr", "en", original)), null))
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()).execute()) {
            if (response.isSuccessful()) {
                return JsonHelper.GSON.fromJson(response.body().string(), TranslateResponse.class).translated_text;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static final class TranslateRequest {
        private final String from_code;
        private final String to_code;
        private final String text;

        private TranslateRequest(String from_code, String to_code, String text) {
            this.from_code = from_code;
            this.to_code = to_code;
            this.text = text;
        }

        public String from_code() {
            return from_code;
        }

        public String to_code() {
            return to_code;
        }

        public String text() {
            return text;
        }

    }

    private static final class TranslateResponse {
        private final String translated_text;

        private TranslateResponse(String translated_text) {
            this.translated_text = translated_text;
        }

        public String translated_text() {
            return translated_text;
        }

    }

}
