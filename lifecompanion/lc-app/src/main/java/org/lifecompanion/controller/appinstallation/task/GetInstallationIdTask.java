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

package org.lifecompanion.controller.appinstallation.task;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.props.ApplicationBuildProperties;
import org.lifecompanion.framework.commons.ApplicationConstant;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.lifecompanion.framework.commons.ApplicationConstant.DIR_NAME_APPLICATION_DATA;

public class GetInstallationIdTask extends LCTask<InstallationController.InstallationRegistrationInformation> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetInstallationIdTask.class);

    private final ApplicationBuildProperties applicationBuildProperties;

    public GetInstallationIdTask(ApplicationBuildProperties applicationBuildProperties) {
        super("task.get.installation.id");
        this.applicationBuildProperties = applicationBuildProperties;
    }

    @Override
    protected InstallationController.InstallationRegistrationInformation call() throws Exception {
        // Get the computer ID
        final String deviceId = getDeviceId();
        LOGGER.info("Got the computer ID : {}", deviceId);

        // Read public key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(applicationBuildProperties.getInstallationPublicKey())));
        Cipher cipher = Cipher.getInstance("RSA");

        // No installation key file, try to request one from server
        final File installationKeyFile = new File(DIR_NAME_APPLICATION_DATA + File.separator + ApplicationConstant.INSTALLATION_KEY_FILENAME);
        if (!installationKeyFile.exists()) {
            // One more barrier to hacking, even if can be easily bypassed...
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();

            Request request = new Request.Builder()
                    .url(applicationBuildProperties.getAppServerUrl() + "/api/v1/services/software-installations" + StringUtils.trimToEmpty(applicationBuildProperties.getAppServerQueryParameters()))
                    .post(RequestBody.create(JsonHelper.GSON.toJson(
                            new CreateInstallationRequestDto(
                                    SystemType.current().getCode(),
                                    Base64.getEncoder().encodeToString(cipher.doFinal(deviceId.getBytes(StandardCharsets.UTF_8))),
                                    InstallationController.INSTANCE.getBuildProperties().getVersionLabel())), null)
                    )
                    .addHeader("Content-Type", "application/vnd.api+json")
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    LOGGER.info("Fresh installation token got from server, will saved it to {}", installationKeyFile.getAbsolutePath());
                    IOUtils.writeToFile(installationKeyFile, JsonHelper.GSON.fromJson(response.body().string(), CreateInstallationResponseDto.class).data);
                } else {
                    throw new Exception("App server returned " + response.code() + " when request installation key");
                }
            }
        }

        // Read installation file (if not present, will fail)
        final InstallationController.InstallationRegistrationInformation installationRegistrationInformation;
        try {
            final String installationKeyBase64Encrypted = IOUtils.readFileLines(installationKeyFile, StandardCharsets.UTF_8.name());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            final String decodedInstallationInfo = new String(cipher.doFinal(Base64.getDecoder().decode(StringUtils.stripToEmpty(installationKeyBase64Encrypted))), StandardCharsets.UTF_8);
            installationRegistrationInformation = JsonHelper.GSON.fromJson(decodedInstallationInfo, InstallationController.InstallationRegistrationInformation.class);
            LOGGER.info("Read installation information from stored installation key : {}", installationRegistrationInformation);
        } catch (Exception e) {
            installationKeyFile.delete();
            LOGGER.warn("Reading installation information from file failed : {} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }

        // Check that device ID in stored key and current device ID are the same
        if (!StringUtils.isEquals(installationRegistrationInformation.getDeviceId(), deviceId)) {
            LOGGER.warn("Read device ID from key file is {} while current device ID is {}, bad key detected, will delete the key file", installationRegistrationInformation.getDeviceId(), deviceId);
            installationKeyFile.delete();
            throw new Exception("Device ID from key file and current device ID don't match, invalid installation ID");
        }

        return installationRegistrationInformation;
    }


    // DTO FOR API
    //========================================================================
    private static class CreateInstallationRequestDto {
        private final String systemId;
        private final String deviceId;
        private final String appVersion;

        private CreateInstallationRequestDto(String systemId, String deviceId, String appVersion) {
            this.systemId = systemId;
            this.deviceId = deviceId;
            this.appVersion = appVersion;
        }
    }

    private static class CreateInstallationResponseDto {
        private String data;

        public CreateInstallationResponseDto() {
        }
    }
    //========================================================================

    // COMPUTER ID
    //========================================================================
    private static final String MAC_OS_HARDWARE_ID_LINE = "Hardware UUID: ";

    private static String getDeviceId() throws Exception {
        final SystemType current = SystemType.current();
        if (current == SystemType.WINDOWS) {
            // If wmic is available
            try {
                return readCmdResult(Arrays.asList("wmic", "csproduct", "get", "UUID")).split("\n")[2];
            } catch (IOException e) {
                LOGGER.warn("Could not read Windows ID from WMIC, will try powershell", e);
                return readCmdResult(Arrays.asList("powershell", "Get-WmiObject -Class \"Win32_ComputerSystemProduct\" | Select-Object -Property UUID")).split("\n")[3];
            }
        } else if (current == SystemType.UNIX) {
            // Don't use readCmdResult(Arrays.asList("cat", "/sys/class/dmi/id/product_uuid")) as product uuid is not given if not root
            // instead, read the command result that was stored in device_id when installed
            File deviceIdFile = new File(LCConstant.EXT_PATH_DATA + File.separator + "device_id.txt");
            if (deviceIdFile.exists()) {
                return StringUtils.trimToEmpty(IOUtils.readFileLines(deviceIdFile, StandardCharsets.UTF_8.name()));
            } else {
                throw new FileNotFoundException("The device_id.txt was not found on this UNIX installation");
            }
        } else if (current == SystemType.MAC) {
            final String hardwareUUIDLine = Arrays
                    .stream(readCmdResult(Arrays.asList("system_profiler", "SPHardwareDataType"))
                            .split("\n"))
                    .filter(line -> StringUtils.startWithIgnoreCase(line, MAC_OS_HARDWARE_ID_LINE))
                    .findFirst()
                    .orElse("");
            return StringUtils.safeSubstring(hardwareUUIDLine, MAC_OS_HARDWARE_ID_LINE.length(), hardwareUUIDLine.length());
        }
        throw new IllegalStateException("No getDeviceId() implementation for system : " + SystemType.current());
    }

    private static String readCmdResult(List<String> cmds) throws Exception {
        File errorLog = new File(System.getProperty("java.io.tmpdir") + "/LifeCompanion/logs/computer-id-cmd/" + System.currentTimeMillis() + "/err.txt");
        IOUtils.createParentDirectoryIfNeeded(errorLog);
        final Process computerIdCmdProcess = new ProcessBuilder()
                .command(cmds)
                .redirectError(errorLog)
                .start();
        StringBuilder result = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(computerIdCmdProcess.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.append(StringUtils.stripToEmpty(line)).append("\n");
            }
        }
        computerIdCmdProcess.waitFor();
        return result.toString();
    }
    //========================================================================
}
