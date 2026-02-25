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

package org.lifecompanion.plugin.translate.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.plugin.translate.controller.TranslateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ArgoTranslateService implements TranslationServiceI {
    private final Logger LOGGER = LoggerFactory.getLogger(ArgoTranslateService.class);
    private static final String MEDIA_TYPE_JSON = "application/json";
    private static final int PORT = 8000;
    private static final String URL = "http://localhost:" + PORT + "/";
    private OkHttpClient httpClient;

    @Override
    public String translate(String sourceLanguageCode, String targetLanguageCode, String textToTranslate) throws Exception {
        //LOGGER.info("Will request translation of\n\t\"{}\"\n\t{} > {}", textToTranslate, sourceLanguageCode, targetLanguageCode);
        try (Response response = httpClient.newCall(new Request.Builder().url(URL + "translate")
                .post(RequestBody.create(JsonHelper.GSON.toJson(new TranslateRequest(sourceLanguageCode, targetLanguageCode, textToTranslate)), null))
                .addHeader("Accept", MEDIA_TYPE_JSON)
                .addHeader("Content-Type", MEDIA_TYPE_JSON)
                .build()).execute()) {
            if (response.isSuccessful()) {
                return JsonHelper.GSON.fromJson(response.body().string(), TranslateResponse.class).translated_text;
            } else {
                throw new Exception("Can't translate, code " + response.code() + "\nMessage : " + response.body().string());
            }
        }
    }

    @Override
    public void initialize() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .callTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public boolean isInitialized() {
        return httpClient != null;
    }

    @Override
    public void dispose() {
        this.httpClient = null;
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
