package org.lifecompanion.plugin.homeassistant;

import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.LifeCompanionFrameworkServerConstant;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.homeassistant.model.*;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum HomeAssistantPluginService {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeAssistantPluginService.class);

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final MediaType TEXT_PLAIN = MediaType.get("text/plain; charset=utf-8");

    private String serverUrl;
    private String authToken;
    private OkHttpClient client;

    private Timer timerStateUpdater;
    private final Map<String, StringProperty> states;
    private final Map<Consumer<String>, ChangeListener<String>> statesListeners;

    HomeAssistantPluginService() {
        statesListeners = new HashMap<>();
        states = new HashMap<>();
    }

    private OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()//
                    .connectTimeout(Duration.ofSeconds(10))//
                    .readTimeout(Duration.ofSeconds(10))//
                    .retryOnConnectionFailure(false)//
                    .addInterceptor((chain) -> handleAuth(chain))
                    .build();
        }
        return client;
    }

    @NotNull
    private Response handleAuth(Interceptor.Chain chain) throws IOException {
        return chain.proceed(StringUtils.isNotBlank(authToken)
                ? chain.request()
                .newBuilder()
                .addHeader(LifeCompanionFrameworkServerConstant.AUTHORIZATION_HEADER, LifeCompanionFrameworkServerConstant.AUTHORIZATION_HEADER_VALUE_PREFIX + " " + authToken)
                .build()
                : chain.request());
    }

    // GET INFO
    //========================================================================
    public List<HAService> getServices() throws IOException {
        List<HAService> services = new ArrayList<>();
        try (Response response = getClient().newCall(new Request.Builder()
                .url(createUrlFor("services"))
                .get()
                .build()).execute()) {
            final HARawDomain[] domains = JsonServiceHA.GSON.fromJson(response.body().string(), HARawDomain[].class);
            for (HARawDomain domain : domains) {
                domain.getServices().forEach((id, service) -> {
                    service.setDomainId(domain.getDomain());
                    service.setServiceId(id);
                    services.add(service);
                });
            }
        }
        return services;
    }

    public List<HAEntityState> getStates() throws IOException {
        List<HAEntityState> states = new ArrayList<>();
        try (Response response = getClient().newCall(new Request.Builder()
                .url(createUrlFor("states"))
                .get()
                .build()).execute()) {
            return JsonServiceHA.GSON.fromJson(response.body().string(), new TypeToken<ArrayList<HAEntityState>>() {
            }.getType());
        }
    }

    public GetEntitiesTask getEntities() {
        return new GetEntitiesTask();
    }

    public class GetEntitiesTask extends LCTask<List<HAEntity>> {

        protected GetEntitiesTask() {
            super("ha.plugin.task.get.entities");
        }

        @Override
        protected List<HAEntity> call() throws Exception {
            return getStates().stream()
                    .map(s -> Pair.of(s.getEntityId(), s))
                    .sorted(Comparator.comparing(Pair::getLeft))
                    .map(s -> new HAEntity(s.getLeft(), s.getRight().getAttributes().getFriendlyName()))
                    .collect(Collectors.toList());
        }
    }
    //========================================================================


    // CALL SERVICE
    //========================================================================
    public void executeService(String service, String entityId) throws IOException {
        RequestBody body = RequestBody.create(JsonServiceHA.GSON.toJson(new HAServiceCallPayload(entityId)), JSON);
        final String domain = HAEntity.getDomainFromEntityId(entityId);
        try (Response response = getClient().newCall(new Request.Builder()
                .url(createUrlFor("services/" + domain + "/" + service))
                .post(body)
                .build()).execute()) {
            LOGGER.info("executeService(...) to {} / {} on {} = {}", domain, service, entityId, response.code());
        }
    }
    //========================================================================


    // START/STOP
    //========================================================================
    public void start(LCConfigurationI configuration) {
        HomeAssistantPluginProperties properties = configuration.getPluginConfigProperties(HomeAssistantPlugin.PLUGIN_ID, HomeAssistantPluginProperties.class);
        this.serverUrl = properties.serverUrlProperty().get();
        this.authToken = properties.authTokenProperty().get();
        timerStateUpdater = new Timer();
        timerStateUpdater.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    final List<HAEntityState> states = getStates();
                    for (Map.Entry<String, StringProperty> stateRequest : HomeAssistantPluginService.this.states.entrySet()) {
                        final HAEntityState entityState = states.stream().filter(s -> s.getEntityId().equals(stateRequest.getKey())).findAny().orElse(null);
                        if (entityState != null) {
                            stateRequest.getValue().set(entityState.getState());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Can't get states", e);
                }
            }
        }, 1000, 100);
    }

    public void stop(LCConfigurationI configuration) {
        this.client = null;
        timerStateUpdater.cancel();
        timerStateUpdater = null;
        this.states.clear();
        this.statesListeners.clear();
    }
    //========================================================================


    // STATE
    //========================================================================
    public void registerStateListener(String itemName, Consumer<String> valueUpdatedCallback) {
        ChangeListener<String> changeListenerStateValue = (obs, ov, nv) -> valueUpdatedCallback.accept(nv);
        statesListeners.put(valueUpdatedCallback, changeListenerStateValue);
        this.states.computeIfAbsent(itemName, i -> new SimpleStringProperty()).addListener(changeListenerStateValue);
    }

    public void removeStateListener(String itemName, Consumer<String> valueUpdatedCallback) {
        ChangeListener<String> removed = this.statesListeners.remove(valueUpdatedCallback);
        if (removed != null) {
            StringProperty stateProp = this.states.get(itemName);
            if (stateProp != null) {
                stateProp.removeListener(removed);
            }
        }
    }
    //========================================================================


    // HELPER
    //========================================================================
    private String createUrlFor(String serverUrl, String path) {
        if (!serverUrl.endsWith("/") && !path.startsWith("/")) {
            serverUrl = serverUrl + "/";
        }
        return serverUrl + path;// FIXME : check http-https, check /api, check ending /
    }

    private String createUrlFor(String path) {
        return createUrlFor(serverUrl, path);
    }

    public Pair<Boolean, String> checkConnection(String url, String token) {
        String savedAuth = this.authToken;
        this.authToken = token;
        try (Response response = getClient().newCall(new Request.Builder()
                .url(createUrlFor(url, ""))
                .get()
                .build()).execute()) {
            return Pair.of(response.isSuccessful(), response.body().string());
        } catch (Exception e) {
            LOGGER.error("Can't connect to openHAB server", e);
            return Pair.of(false, e.getClass().getSimpleName() + " " + e.getMessage());
        } finally {
            this.authToken = savedAuth;
        }
    }
    //========================================================================
}
