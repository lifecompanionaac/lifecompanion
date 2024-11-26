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

package org.lifecompanion.build;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.io.*;
import java.util.function.Function;

public abstract class DownloadJdkAndJfxTask extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(DownloadJdkAndJfxTask.class);


    @TaskAction
    void downloadJdkAndJfx() throws Exception {
        for (DestPlatform platform : DestPlatform.values()) {
            downloadForPlatform(platform, DlType.JDK);
            if (platform.hasJfx()) {
                downloadForPlatform(platform, DlType.JFX);
            }
        }
    }

    private void downloadForPlatform(DestPlatform platform, DlType type) throws IOException {
        File jdkFile = new File(type.fileGetter.apply(platform));
        // Check download
        if (!jdkFile.exists()) {
            LOGGER.lifecycle("{} will be downloaded to {}", type, jdkFile);
            IOUtils.createParentDirectoryIfNeeded(jdkFile);
            OkHttpClient client = new OkHttpClient.Builder().build();
            try (Response response = client.newCall(
                    new Request.Builder()
                            .url(type.urlGetter.apply(platform))
                            .build()).execute()) {
                if (response.isSuccessful()) {
                    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(jdkFile))) {
                        try (InputStream is = new BufferedInputStream(response.body().byteStream())) {
                            IOUtils.copyStream(is, os);
                        }
                    }
                    LOGGER.lifecycle("{} downloaded", type);
                } else {
                    throw new IllegalArgumentException("Can't download " + type + " from " + type.urlGetter.apply(platform));
                }
            }
        }
        // Check extract
        File jdkPath = new File(type.pathGetter.apply(platform));
        if (!jdkPath.exists()) {
            IOUtils.createParentDirectoryIfNeeded(jdkPath);
            LOGGER.lifecycle("Will extract {} to {}", type, jdkPath);
            if (type == DlType.JFX || "zip".equals(platform.jdkExt)) {
                IOUtils.unzipInto(jdkFile, jdkPath, null);
            } else {
                try (GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(new FileInputStream(jdkFile))) {
                    try (TarArchiveInputStream i = new TarArchiveInputStream(gzipIn)) {
                        for (ArchiveEntry nextEntry = i.getNextEntry(); nextEntry != null; nextEntry = i.getNextEntry()) {
                            String name = nextEntry.getName();
                            String dirPath = jdkPath.getPath();
                            File filePath = new File(dirPath + File.separator + name);
                            File parent = filePath.getParentFile();
                            parent.mkdirs();
                            if (nextEntry.isDirectory()) {
                                filePath.mkdir();
                            } else {
                                try (OutputStream o = new FileOutputStream(filePath)) {
                                    IOUtils.copyStream(i, o);
                                }
                            }
                        }
                    }
                }
                LOGGER.lifecycle("{} extracted", type);
            }
        }
    }

    enum DlType {
        JDK(p -> p.jdkUrl, DestPlatform::getJdkFilePath, DestPlatform::getJdkPath),
        JFX(p -> p.jfxUrl, DestPlatform::getJfxFilePath, DestPlatform::getJfxPath);

        private final Function<DestPlatform, String> urlGetter, fileGetter, pathGetter;

        DlType(Function<DestPlatform, String> urlGetter, Function<DestPlatform, String> fileGetter, Function<DestPlatform, String> pathGetter) {
            this.urlGetter = urlGetter;
            this.fileGetter = fileGetter;
            this.pathGetter = pathGetter;
        }
    }

    enum DestPlatform {
        WIN_X64(
                "zip",
                "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jdk_x64_windows_hotspot_21.0.2_13.zip",
                "https://download2.gluonhq.com/openjfx/22/openjfx-22_windows-x64_bin-jmods.zip",
                "jdk-21.0.2+13",
                "javafx-jmods-22"
        ),
        LINUX_X64(
                "tar.gz",
                "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jdk_x64_linux_hotspot_21.0.2_13.tar.gz",
                "https://download2.gluonhq.com/openjfx/22/openjfx-22_linux-x64_bin-jmods.zip",
                "jdk-21.0.2+13",
                "javafx-jmods-22"
        ),
        MAC_X64("tar.gz",
                "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jdk_x64_mac_hotspot_21.0.2_13.tar.gz",
                "https://download2.gluonhq.com/openjfx/22/openjfx-22_osx-x64_bin-jmods.zip",
                "jdk-21.0.2+13/Contents/Home",
                "javafx-jmods-22"
        ),
        WIN_AARCH64(
                "zip",
                "https://download.bell-sw.com/java/21.0.5+11/bellsoft-jdk21.0.5+11-windows-aarch64-full.zip",
                null,
                "jdk-21.0.5-full",
                null
        ),
        ;

        private final String jdkUrl, jdkExt, jfxUrl, internalJdkPath, internalJfxPath;

        DestPlatform(String jdkExt, String jdkUrl, String jfxUrl, String internalJdkPath, String internalJfxPath) {
            this.jdkExt = jdkExt;
            this.jdkUrl = jdkUrl;
            this.jfxUrl = jfxUrl;
            this.internalJdkPath = internalJdkPath;
            this.internalJfxPath = internalJfxPath;
        }

        public boolean hasJfx() {
            return jfxUrl != null;
        }

        public String getId() {
            return name().toLowerCase();
        }

        private String getJdkPath() {
            return BuildToolUtils.getEnvVarOrDefault("LIFECOMPANION_JDK_JFX_PATH", getDefaultJdkJfx()) + "/jdk/" + getId();
        }

        private String getJdkFilePath() {
            return BuildToolUtils.getEnvVarOrDefault("LIFECOMPANION_JDK_JFX_PATH", getDefaultJdkJfx()) + "/jdk/" + getId() + "." + jdkExt;
        }

        private String getJfxPath() {
            return BuildToolUtils.getEnvVarOrDefault("LIFECOMPANION_JDK_JFX_PATH", getDefaultJdkJfx()) + "/jfx/" + getId();
        }

        private String getJfxFilePath() {
            return BuildToolUtils.getEnvVarOrDefault("LIFECOMPANION_JDK_JFX_PATH", getDefaultJdkJfx()) + "/jfx/" + getId() + ".zip";
        }

        public String getJdkPathToInject() {
            return getJdkPath() + "/" + internalJdkPath;
        }

        public String getJfxPathToInject() {
            return getJfxPath() + "/" + internalJfxPath;
        }

        private String getDefaultJdkJfx() {
            return System.getProperty("user.home") + "/.lifecompanion-jdk-jfx";
        }
    }
}
