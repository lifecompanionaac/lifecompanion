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

package org.lifecompanion.base.data.control.stats;

import com.google.gson.*;
import okhttp3.*;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.base.data.control.update.InstallationController;
import org.lifecompanion.base.data.io.json.JsonHelper;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.props.ApplicationBuildProperties;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SendPendingSessionStatsRunnable implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendPendingSessionStatsRunnable.class);

    @Override
    public void run() {
        LCUtils.safeSleep(20_000);
        LOGGER.info("Send pending session stats thread started");
        try {
            if (UserBaseConfiguration.INSTANCE.recordAndSendSessionStatsProperty().get()) {
                // List potential session IDs to send
                Set<String> sessionIds = new HashSet<>();
                File[] sessionDirs = new File(LCConstant.PATH_SESSION_STATS_CACHE + File.separator).listFiles();
                if (sessionDirs != null) {
                    for (File sessionDir : sessionDirs) {
                        String id = sessionDir.getName();
                        if (StringUtils.isDifferent(id, SessionStatsController.INSTANCE.getCurrentSessionId())) {
                            sessionIds.add(id);
                        }
                    }
                }

                // Check which session are not already sent
                if (!sessionIds.isEmpty()) {
                    LOGGER.info("Found {} local session stats to send, will check if they should be sent", sessionIds.size());
                    OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();
                    final ApplicationBuildProperties buildProperties = InstallationController.INSTANCE.getBuildProperties();
                    Request checkIdsRequest = new Request.Builder().url(buildProperties.getAppServerUrl() + "/api/v1/services/software-sessions/exists" + StringUtils.trimToEmpty(buildProperties.getAppServerQueryParameters()))
                            .post(RequestBody.create(APP_SERVER_GSON.toJson(new CheckSessionRequestDto(sessionIds)), null))
                            .addHeader("Content-Type", "application/vnd.api+json")
                            .build();
                    // Remove each already existing session
                    try (Response response = okHttpClient.newCall(checkIdsRequest).execute()) {
                        final String body = responseBodyAsStringSafe(response);
                        if (response.isSuccessful()) {
                            final CheckSessionResponseDto checkSessionResponseDto = JsonHelper.GSON.fromJson(body, CheckSessionResponseDto.class);
                            checkSessionResponseDto.data.forEach((sessionId, exist) -> {
                                if (exist) {
                                    sessionIds.remove(sessionId);
                                    removeSessionDirectory(sessionId);
                                }
                            });
                        } else {
                            LOGGER.warn("Couldn't check if session exists on server, will try to upload all detected sessions, response : {}", body);
                        }
                    }
                    // Now prepare the content request
                    if (!sessionIds.isEmpty()) {
                        LOGGER.info("{} sessions are to upload, will read them from local cache and validate", sessionIds.size());
                        List<SoftwareSession> sessions = new ArrayList<>();
                        for (String sessionId : sessionIds) {
                            File sessionDir = new File(LCConstant.PATH_SESSION_STATS_CACHE + File.separator + sessionId);
                            final SoftwareSession softwareSession = readSoftwareSession(sessionDir);
                            if (softwareSession != null) {
                                sessions.add(softwareSession);
                            }
                        }
                        LOGGER.info("Found {} valid sessions to upload to stats server", sessions.size());
                        if (!sessions.isEmpty()) {
                            try (Response response = okHttpClient.newCall(createSessionUploadRequest(sessions)).execute()) {
                                LOGGER.info("Session upload request done, returned : {}", response.code());
                                if (response.isSuccessful()) {
                                    sessionIds.forEach(this::removeSessionDirectory);
                                } else {
                                    LOGGER.warn("Mass session upload error, response body is : {}, will try to upload session individually", responseBodyAsStringSafe(response));
                                    for (SoftwareSession session : sessions) {
                                        try (Response responseInd = okHttpClient.newCall(createSessionUploadRequest(Collections.singletonList(session))).execute()) {
                                            if (responseInd.isSuccessful()) {
                                                LOGGER.info("Session {} uploaded request successful", session.getId());
                                                this.removeSessionDirectory(session.getId());
                                            } else {
                                                LOGGER.warn("Session {} uploaded request failed (response is {}), session ignored", session.getId(), responseBodyAsStringSafe(responseInd));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        LOGGER.info("Every local session were already uploaded, didn't send!");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Could not send pending session stats", e);
        }
    }

    private Request createSessionUploadRequest(List<SoftwareSession> sessions) {
        final ApplicationBuildProperties buildProperties = InstallationController.INSTANCE.getBuildProperties();
        Request uploadSessionRequest = new Request.Builder().url(buildProperties.getAppServerUrl() + "/api/v1/services/software-sessions" + StringUtils.trimToEmpty(buildProperties.getAppServerQueryParameters()))
                .post(RequestBody.create(APP_SERVER_GSON.toJson(new CreateSessionRequestDto(sessions)), null))
                .addHeader("Content-Type", "application/vnd.api+json")
                .build();
        return uploadSessionRequest;
    }

    private String responseBodyAsStringSafe(Response response) throws IOException {
        final ResponseBody body = response.body();
        return body != null ? body.string() : "{}";
    }

    private void removeSessionDirectory(String sessionId) {
        IOUtils.deleteDirectoryAndChildren(new File(LCConstant.PATH_SESSION_STATS_CACHE + File.separator + sessionId));
    }

    // DTO
    //========================================================================
    private static class CheckSessionRequestDto {
        private final Set<String> ids;

        public CheckSessionRequestDto(Set<String> ids) {
            this.ids = ids;
        }
    }

    private static class CheckSessionResponseDto {
        private Map<String, Boolean> data;
    }

    private static class CreateSessionRequestDto {
        private final List<SoftwareSession> sessions;

        public CreateSessionRequestDto(List<SoftwareSession> sessions) {
            this.sessions = sessions;
        }
    }
    //========================================================================


    // CREATE SESSION FROM LOCAL CACHE
    //========================================================================
    private SoftwareSession readSoftwareSession(File sessionDirectory) {
        // Read all sub part from directory
        List<SessionPart> sessionParts = new ArrayList<>();
        File[] partDirs = new File(sessionDirectory + File.separator + SessionStatsController.SESSION_PART_DIRNAME).listFiles();
        if (partDirs != null) {
            for (File partDir : partDirs) {
                // Read the part data
                try (Reader isPart = new BufferedReader(new InputStreamReader(new FileInputStream(partDir + File.separator + LCConstant.SESSION_DATA_FILENAME), StandardCharsets.UTF_8))) {
                    final SessionPart sessionPart = JsonHelper.GSON.fromJson(isPart, SessionPart.class);
                    sessionPart.setEvents(new ArrayList<>());
                    // Then read the part events
                    File[] eventDirs = new File(partDir + File.separator + SessionStatsController.EVENT_DIRNAME).listFiles();
                    if (eventDirs != null) {
                        for (File eventDir : eventDirs) {
                            try (Reader isEvent = new BufferedReader(new InputStreamReader(new FileInputStream(eventDir + File.separator + LCConstant.SESSION_DATA_FILENAME), StandardCharsets.UTF_8))) {
                                sessionPart.getEvents().add(JsonHelper.GSON.fromJson(isEvent, SessionEvent.class));
                            } catch (Exception e2) {
                                LOGGER.warn("Couldn't read the event in dir {}", eventDir, e2);
                            }
                        }
                    }
                    if (!sessionPart.getEvents().isEmpty() && StringUtils.isNotBlank(sessionPart.getProfileId())) {
                        sessionParts.add(sessionPart);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Couldn't read the part in dir {}", partDir, e);
                }
            }
        }
        if (!sessionParts.isEmpty()) {
            // Now set the start/end date for each element (session, part) from their contents (find last event or next part)
            sessionParts.sort(Comparator.comparing(SessionPart::getStartedAt));
            for (SessionPart sessionPart : sessionParts) {
                sessionPart.getEvents().sort(Comparator.comparing(SessionEvent::getRecordedAt));
                sessionPart.setEndedAt(sessionPart.getEvents().get(sessionPart.getEvents().size() - 1).getRecordedAt());
                sessionPart.getEvents().removeIf(e -> StringUtils.isEquals(e.getEventType(), SessionEventType.PART_START.getId()) || StringUtils.isEquals(e.getEventType(), SessionEventType.PART_STOP.getId()));

                // Clearing interaction events
                final List<SessionEvent> allUserInteractionCountEvents = sessionPart.getEvents().stream().filter(sessionEvent -> sessionEvent.getEventType().equalsIgnoreCase(SessionEventType.USER_INTERACTION_COUNT_LAST_MINUTE.getId())).collect(Collectors.toList());
                final long startAtTT = sessionPart.getStartedAt().getTime();
                final long endAtTT = sessionPart.getEndedAt().getTime();
                if (startAtTT <= endAtTT) {
                    List<SessionEvent> cleanCountEventsToInsert = new ArrayList<>();
                    // Sum every count per minute in the elapsed time and delete source event from session part events
                    for (long time = startAtTT; time <= endAtTT; time += 60_000) {
                        long minuteStartedAt = time;
                        final double interactionSum = allUserInteractionCountEvents.stream()
                                .filter(event -> event.getRecordedAt().getTime() >= minuteStartedAt && event.getRecordedAt().getTime() < minuteStartedAt + 60_000)
                                .peek(event -> sessionPart.getEvents().remove(event))
                                .mapToDouble(event -> (double) event.getData().get("value"))
                                .sum();
                        // Create the event to sum
                        if (interactionSum > 0) {
                            cleanCountEventsToInsert.add(new SessionEvent(SessionEventType.USER_INTERACTION_COUNT_LAST_MINUTE.getId(), new Date(minuteStartedAt), FluentHashMap.map("value", interactionSum)));
                        }
                    }
                    // Add every sum events and sort again
                    sessionPart.getEvents().addAll(cleanCountEventsToInsert);
                    sessionPart.getEvents().sort(Comparator.comparing(SessionEvent::getRecordedAt));
                } else {
                    LOGGER.error("Session stats are corrupted, end date before start date for session part : {} vs {}", sessionPart.getStartedAt(), sessionPart.getEndedAt());
                }
            }
            // Filter out session part less than 5 seconds
            sessionParts.removeIf(part -> part.getEndedAt().getTime() - part.getStartedAt().getTime() < 5 * 1000);
            if (!sessionParts.isEmpty())
                return new SoftwareSession(sessionDirectory.getName(), InstallationController.INSTANCE.getInstallationRegistrationInformation().getInstallationId(), sessionParts.get(0).getStartedAt(), sessionParts.get(sessionParts.size() - 1).getEndedAt(), sessionParts);
        }
        return null;
    }
    //========================================================================

    // GSON
    //========================================================================
    private static final Gson APP_SERVER_GSON = new GsonBuilder().registerTypeAdapter(Date.class, new UTCDateAdapter()).setPrettyPrinting().create();

    public static class UTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
        private final DateFormat DATE_FORMAT_UTC;

        public UTCDateAdapter() {
            DATE_FORMAT_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            DATE_FORMAT_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(DATE_FORMAT_UTC.format(date));
        }

        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            try {
                return DATE_FORMAT_UTC.parse(jsonElement.getAsString());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }
    }
    //========================================================================
}
