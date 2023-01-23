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

package org.lifecompanion.framework.client.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import okhttp3.*;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.LifeCompanionFrameworkServerConstant;
import org.lifecompanion.framework.model.server.dto.ErrorDto;
import org.lifecompanion.framework.model.server.dto.UserLoginRequestDto;
import org.lifecompanion.framework.model.server.dto.UserLoginResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.LongConsumer;

public class AppServerClient implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppServerClient.class);

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_APPLICATION_OCTET_STREAM = MediaType.parse("application/octet-stream");

    public static final String[] CONNECT_TEST_URL = {"http://google.com", "http://facebook.com"};

    /**
     * Contains the current authentication token returned from server after login
     */
    private String authenticationToken;

    /**
     * Default client that will be use to execute http calls.</br>
     * Can be used to derive a new client when specific configuration is needed.
     */
    private OkHttpClient defaultClient;

    /**
     * JSON mapper
     */
    private final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    /**
     * Contains all the currently running http calls, including download call (which are not included in OkHttp)
     */
    private final Set<Call> runningCalls;

    private final String url;

    private boolean closed;

    private String installationIdForHeader;

    public AppServerClient(String apiUrl) {
        this.url = apiUrl;
        runningCalls = Collections.synchronizedSet(new HashSet<>());
        initializeClient();
        LOGGER.info("App server client initialized, api url = {}", apiUrl);
    }

    private void initializeClient() {
        defaultClient = initializeClientForExternalCalls()
                .addInterceptor((chain) -> chain.proceed(StringUtils.isNotBlank(authenticationToken)
                        ? chain.request()
                        .newBuilder()
                        .addHeader(LifeCompanionFrameworkServerConstant.AUTHORIZATION_HEADER, LifeCompanionFrameworkServerConstant.AUTHORIZATION_HEADER_VALUE_PREFIX + " " + authenticationToken)
                        .build()
                        : chain.request()))
                .addInterceptor((chain) -> chain.proceed(StringUtils.isNotBlank(installationIdForHeader)
                        ? chain.request().newBuilder().addHeader(LifeCompanionFrameworkServerConstant.HEADER_INSTALLATION_ID, installationIdForHeader)
                        .build()
                        : chain.request()))
                .build();
    }

    public static OkHttpClient.Builder initializeClientForExternalCalls() {
        /*
         * Implementation note : this is working well on Windows 10 to detect proxy configuration if the parameter "auto detect proxy configuration" is disabled.
         * > this means that contrary to Chrome and other browsers, default proxy selector explicitly needs to know that you're not using an auto configuration.
         * > this works for both pac files and manual configurations
         */
        LOGGER.info("Will check system proxies, java.net.useSystemProxies = {}", System.getProperty("java.net.useSystemProxies"));
        ProxySelector proxySelector = ProxySelector.getDefault();
        if (proxySelector != null) {
            LOGGER.info("Detected a proxy selector on system, selector : {}", proxySelector);
            List<Proxy> proxies = proxySelector.select(URI.create(CONNECT_TEST_URL[0]));
            if (LangUtils.isNotEmpty(proxies)) {
                for (Proxy proxy : proxies) {
                    LOGGER.info("Detected proxy : {} - {}", proxy.type(), proxy.address());
                }
            }
        }

        return new OkHttpClient.Builder()//
                .connectTimeout(Duration.ofSeconds(30))//
                .readTimeout(Duration.ofSeconds(20))//
                .retryOnConnectionFailure(false)//
                .proxy(null);// To use default proxy selector
    }

    public Gson gson() {
        return GSON;
    }

    // TESTING CONNECTION
    //========================================================================
    public boolean isConnectedToInternet() {
        for (String url : CONNECT_TEST_URL) {
            if (this.canReach(url)) {
                return true;
            }
        }
        return false;
    }

    public boolean canReach(final String url) {
        try {
            this.executeAndGetResponse(new Request.Builder()//
                    .url(url).get(), null);//
            return true;
        } catch (Exception e) {
            LOGGER.warn("Couldn't reach {}", url, e);
            return false;
        }
    }
    //========================================================================

    // Class part : "AUTH"
    // ========================================================================
    public boolean login(String login, String password) {
        try {
            UserLoginResponseDto response = this.executeAndGetResponse(//
                    createBaseRequest("/public/login")//
                            .post(createJsonBody(new UserLoginRequestDto(login, password))),
                    UserLoginResponseDto.class);
            this.authenticationToken = response.getToken();
            return true;
        } catch (Exception e) {
            LOGGER.error("User login failed for {}", login, e);
            return false;
        }
    }

    public void setInstallationIdForHeader(String installationIdForHeader) {
        this.installationIdForHeader = installationIdForHeader;
    }
    // ========================================================================

    // METHODS
    // ========================================================================
    public <T> T get(String url, Class<T> returnType) throws ApiException {
        return this.executeAndGetResponse(createBaseRequest(url).get(), returnType);
    }

    public void download(String fullUrl, File destPath) throws ApiException {
        download(fullUrl, destPath, null);
    }

    public void download(String fullUrl, File destPath, LongConsumer counter) throws ApiException {
        File parentDir = destPath.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }
        final String fileStart = "file:";
        if (StringUtils.startWithIgnoreCase(fullUrl, fileStart)) {
            try (FileOutputStream fos = new FileOutputStream(destPath)) {
                try (FileInputStream fis = new FileInputStream(fullUrl.substring(fileStart.length()))) {
                    IOUtils.copyStreamCounting(fis, fos, counter);
                }
            } catch (Exception e) {
                throw new ApiException("Can't download local file", e);
            }
        } else {
            Call call = this.defaultClient.newCall(new Request.Builder()
                    .url(fullUrl)
                    .addHeader("Connection", "close")
                    .build());
            this.runningCalls.add(call);
            try (Response response = call.execute()) {
                if (response.isSuccessful()) {
                    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(destPath))) {
                        try (InputStream is = new BufferedInputStream(response.body().byteStream())) {
                            IOUtils.copyStreamCounting(is, os, counter);
                        }
                    }
                } else {
                    throw new ApiException("Server returned error : " + response.code());
                }
            } catch (IOException e) {
                throw new ApiException("Can't execute the request", e);
            } finally {
                this.runningCalls.remove(call);
            }
        }
    }

    public <T> T post(String url, Object payload, Class<T> returnType) throws ApiException {
        return this.executeAndGetResponse(createBaseRequest(url).post(createJsonBody(payload)), returnType);
    }

    public <T> T post(String url, Object payload) throws ApiException {
        return this.executeAndGetResponse(createBaseRequest(url).post(createJsonBody(payload)), null);
    }

    public <T> T postWithFile(String url, Object payload, File file, Class<T> returnType) throws ApiException {
        RequestBody requestBody = new MultipartBody.Builder()//
                .setType(MultipartBody.FORM)//
                .addFormDataPart("dto", null, //
                        createJsonBody(payload))//
                .addFormDataPart("file", null, //
                        RequestBody.create(file, MEDIA_APPLICATION_OCTET_STREAM))//
                .build();
        /*
         * For uploading file, we delete the read timeout, because long upload can cause
         * a read timeout. This is a problem in the OkHttp lib : issues #2122 and #2443
         */
        return this.executeAndGetResponse(//
                this.defaultClient.newBuilder().readTimeout(0, TimeUnit.SECONDS).build(), //
                createBaseRequest(url)//
                        .post(requestBody)//
                , returnType);
    }

    public <T> T postWithFile(String url, Object payload, File file) throws ApiException {
        return postWithFile(url, payload, file, null);
    }

    // ========================================================================

    // HELPERS
    // ========================================================================
    private <T> T executeAndGetResponse(Request.Builder requestBuilder, Class<T> returnTypeClass) throws ApiException {
        return this.executeAndGetResponse(defaultClient, requestBuilder, returnTypeClass);
    }

    private <T> T executeAndGetResponse(OkHttpClient httpClient, Request.Builder requestBuilder, Class<T> returnTypeClass) throws ApiException {
        Call call = httpClient.newCall(requestBuilder.build());
        this.runningCalls.add(call);
        try (Response response = call.execute()) {
            String bodyAsString = response.body().string();
            if (!response.isSuccessful()) {
                try {
                    throw new ServerBusinessException(GSON.fromJson(bodyAsString, ErrorDto.class));
                } catch (JsonSyntaxException jse) {
                    throw new ApiException("Unknown error on the server, body :\n" + bodyAsString);
                }
            }
            return createObjectFromJson(bodyAsString, returnTypeClass);
        } catch (IOException e) {
            LOGGER.error("Api request failed", e);
            throw new ApiException("Can't execute the request", e);
        } finally {
            this.runningCalls.remove(call);
        }
    }

    private Request.Builder createBaseRequest(String path) {
        return new Request.Builder()//
                .url(this.url + path);//
    }

    private RequestBody createJsonBody(Object object) throws ApiException {
        try {
            return RequestBody.create(GSON.toJson(object), MEDIA_TYPE_JSON);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Converting object to json failed", e);
            throw new ApiException("Couldn't convert object to json", e);
        }
    }

    @Override
    public void close() {
        this.closed = true;
        defaultClient.dispatcher().cancelAll();
        Call[] runningCallsArray = this.runningCalls.toArray(new Call[this.runningCalls.size()]);
        int closedCount = 0;
        for (Call call : runningCallsArray) {
            try {
                call.cancel();
                closedCount++;
            } catch (Exception e) {
                LOGGER.warn("Cancel running call failed", e);
            }
        }
        LOGGER.info("Closed {} left running calls", closedCount);
    }

    public boolean isClosed() {
        return this.closed;
    }
    // ========================================================================

    // JSON
    // ========================================================================
    private <T> T createObjectFromJson(String json, Class<T> typeClass) throws IOException, ServerBusinessException {
        try {
            //TODO : add other type
            if (typeClass == String.class)
                return (T) json;
            if (typeClass != null)
                return StringUtils.isEquals(json, LifeCompanionFrameworkServerConstant.EMPTY_JSON_OBJECT) ? null : GSON.fromJson(json, typeClass);
            else return null;
        } catch (JsonSyntaxException jsonException) {
            LOGGER.warn("Couldn't deserialize original json from server", jsonException);
            throw new ServerBusinessException(GSON.fromJson(json, ErrorDto.class));
        }
    }
    // ========================================================================

}
