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

package org.lifecompanion.framework.server.service;

import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.LifeCompanionFrameworkServerConstant;
import org.lifecompanion.framework.model.server.stats.PushStatus;
import org.lifecompanion.framework.model.server.stats.SoftwareStat;
import org.lifecompanion.framework.server.data.dao.DataSource;
import org.lifecompanion.framework.server.data.dao.SoftwareStatDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import spark.Request;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

public enum SoftwareStatService {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(SoftwareStatService.class);

    private final String platformApiToken;
    private final String platformUrl;
    private final OkHttpClient httpClient;
    private final String appTmpPassword;

    SoftwareStatService() {
        platformApiToken = System.getenv("LC_PLATFORM_API_TOKEN");
        platformUrl = System.getenv("LC_PLATFORM_URL");

        LOGGER.info("Got platform URL from env : {}", platformUrl);

        appTmpPassword = System.getenv("LC_PLATFORM_URL_PASSWORD");

        httpClient = new OkHttpClient.Builder()//
                .connectTimeout(Duration.ofSeconds(30))//
                .readTimeout(Duration.ofSeconds(20))//
                .retryOnConnectionFailure(false)//
                .build();
    }

    public void startPushStatsBackgroundThread() {
        new PushStatBackgroundThread().start();
    }

    public void pushStat(Request request, StatEvent event, String version, SystemType systemType) {
        pushStat(request, event, version, systemType, new Date());
    }

    public void pushStat(Request request, String event, String version, SystemType systemType) {
        pushStat(request, event, version, systemType, new Date());
    }

    public void pushStat(Request request, StatEvent event, String version, SystemType systemType, Date recordedAt) {
        pushStat(request, event.code, version, systemType, recordedAt);
    }

    public void pushStat(Request request, String event, String version, SystemType systemType, Date recordedAt) {
        SoftwareStat stat = new SoftwareStat();
        stat.setId(UUID.randomUUID().toString());
        stat.setEvent(event);
        stat.setRecordedAt(recordedAt);
        stat.setVersion(version);
        stat.setSystemId(systemType);
        stat.setInstallationId(request.headers(LifeCompanionFrameworkServerConstant.HEADER_INSTALLATION_ID));
        stat.setPushStatus(PushStatus.WAITING);
        try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
            SoftwareStatDao.INSTANCE.insertSoftwareStat(connection, stat);
            connection.commit();
        }
    }


    // PUSH THREAD
    //========================================================================
    private class PushStatBackgroundThread extends Thread {
        PushStatBackgroundThread() {
            setName("Background-stats-uploader");
            setDaemon(true);
            setPriority(Thread.MIN_PRIORITY);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10_000);
                    SoftwareStat statToPush = SoftwareStatDao.INSTANCE.getFirstStatToPush();
                    LOGGER.info("Background thread will try to push stats, found a stat to push : {}", statToPush != null);
                    while (statToPush != null) {
                        final JsonObject dataObject = JsonService.jsonUTC().toJsonTree(statToPush).getAsJsonObject();
                        dataObject.remove("id");
                        dataObject.remove("pushStatus");
                        dataObject.remove("pushError");
                        dataObject.remove("pushTries");
                        try {
                            try (Response response = httpClient.newCall(new okhttp3.Request.Builder()
                                    .url(platformUrl + "/api/v1/services/software-stats" + StringUtils.trimToEmpty(appTmpPassword))
                                    .post(RequestBody.create(dataObject.toString(), null))
                                    .addHeader("Content-Type", "application/vnd.api+json")
                                    .addHeader("Authorization", "Bearer " + platformApiToken)
                                    .build()).execute()) {
                                if (!response.isSuccessful()) {
                                    throw new Exception("Bad response from app server : " + response.code());
                                } else {
                                    try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
                                        // TODO : in a next update, we should delete the entries
                                        SoftwareStatDao.INSTANCE.updatePushStatusAndTries(connection, statToPush.getId(), PushStatus.DONE, statToPush.getPushTries() + 1, null);
                                        connection.commit();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.warn("Failed to push stats : {}", statToPush, e);
                            try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
                                SoftwareStatDao.INSTANCE.updatePushStatusAndTries(connection, statToPush.getId(), PushStatus.FAILED, statToPush.getPushTries() + 1, e.toString());
                                connection.commit();
                            }
                        }
                        // Get next one (might return the same if call failed)
                        Thread.sleep(2_000);
                        statToPush = SoftwareStatDao.INSTANCE.getFirstStatToPush();
                    }
                } catch (Throwable t) {
                    LOGGER.error("Error in push stats background thread", t);
                }
            }
        }
    }
    //========================================================================

    // STAT TYPE
    //========================================================================
    public enum StatEvent {
        APP_UPDATE_DONE("app.update.done");

        public final String code;

        StatEvent(String code) {
            this.code = code;
        }
    }
    //========================================================================
}
