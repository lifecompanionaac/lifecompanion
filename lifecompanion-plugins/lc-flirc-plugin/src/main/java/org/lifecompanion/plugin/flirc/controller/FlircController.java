package org.lifecompanion.plugin.flirc.controller;

import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.flirc.model.IRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum FlircController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(FlircController.class);

    private final Object callSynchronizeLock = new Object();

    private static final Map<SystemType, Pair<String, String>> FLIRC_UTIL = FluentHashMap.map(SystemType.WINDOWS,
            Pair.of("flirc_util.exe", "145ae3b3d42d03f8643b49f6d4a8ef2d4381a212c109ea6932250614046cee29"));

    private String getFlircPath() throws LCException {
        Pair<String, String> utilInfo = FLIRC_UTIL.get(SystemType.current());
        if (utilInfo != null) {
            File destFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "lc-flirc-plugin" + File.separator + utilInfo.getLeft());
            try {
                String existingHash = IOUtils.fileSha256HexToString(destFile);
                if (!StringUtils.isEquals(existingHash, utilInfo.getRight())) {
                    throw new IOException("Destination file hash doesn't match expected hash : value = " + existingHash + " vs expected = " + utilInfo.getRight());
                }
            } catch (IOException e) {
                LOGGER.warn("Could not check existing flirc_util file, will copy source file to destination", e);
                destFile.getParentFile().mkdirs();
                try (InputStream is = ResourceHelper.getInputStreamForPath("/flirc_util/flirc_util.exe")) {
                    try (FileOutputStream fos = new FileOutputStream(destFile)) {
                        IOUtils.copyStream(is, fos);
                    }
                } catch (IOException e2) {
                    throw LCException.newException().withMessage("flirc.plugin.error.cant.copy.flirc.application").withCause(e2).build();
                }
            }
            return destFile.getPath();

        } else {
            throw LCException.newException().withMessage("flirc.plugin.error.not.available.system").build();
        }
    }

    public void waitForDevice() throws LCException {
        waitFor(true, new ProcessBuilder().command(getFlircPath(), "wait").redirectError(ProcessBuilder.Redirect.DISCARD).redirectOutput(ProcessBuilder.Redirect.DISCARD));
    }


    public void clearLogsAndDisableRecording() throws LCException {
        waitFor(false, new ProcessBuilder().command(getFlircPath(), "device_log").redirectError(ProcessBuilder.Redirect.DISCARD).redirectOutput(ProcessBuilder.Redirect.DISCARD));
    }

    public void clearLogsAndEnableRecording() throws LCException {
        analyzeOutputAndFail(waitFor(true, new ProcessBuilder().command(getFlircPath(), "device_log", "-i")));
    }


    public List<String> getRecordedCodes() throws Exception {
        List<String> lines = analyzeOutputAndFail(waitFor(true, new ProcessBuilder().command(getFlircPath(), "device_log", "-i")));
        // Keep only data lines
        List<String> dataAsString = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (StringUtils.startWithIgnoreCase(StringUtils.trimToEmpty(line), ":e:") && i + 1 < lines.size()) {
                dataAsString.add(lines.get(i + 1));
            }
        }
        return dataAsString;
    }

    public void sendIr(IRCode irCode) throws Exception {
        LOGGER.info("Sending ... {}", irCode);
        List<String> cmds = new ArrayList<>(List.of(getFlircPath(), "sendir", "--pattern=" + irCode.getPattern()));
        if (irCode.isLongPress()) {
            cmds.add("--repeat=" + irCode.getComputedSendCount());
        }
        analyzeOutputAndFail(waitFor(true, new ProcessBuilder().command(cmds)));
    }

    public String getSettings() throws Exception {
        return analyzeOutputAndFail(waitFor(true, new ProcessBuilder().command(getFlircPath(), "settings"))).stream().filter(StringUtils::isNotBlank).collect(Collectors.joining("\n"));
    }

    private Process waitFor(boolean failForNonZero, ProcessBuilder processBuilder) throws LCException {
        synchronized (callSynchronizeLock) {
            LOGGER.info("Will try to run {}", processBuilder.command());
            try {
                Process process = processBuilder.start();
                try {
                    int returnCode = process.waitFor();
                    if (failForNonZero && returnCode != 0) {
                        LCException.newException().withMessage("flirc.plugin.error.unknown.return.code").buildAndThrow();
                    }

                } catch (InterruptedException e) {
                    LOGGER.warn("Could not wait for process to finish", e);
                    if (process.isAlive()) {
                        LOGGER.info("Process is still alive, try to destroy it...");
                        process.destroy();
                    }
                }
                return process;
            } catch (IOException e) {
                throw LCException.newException().withMessage("flirc.plugin.error.cant.start.flirc.process").withCause(e).build();
            }
        }
    }

    private List<String> analyzeOutputAndFail(Process process) throws LCException {
        List<String> lines = convertStreamToLinesRemovingBlank(process.getInputStream());
        if (lines.stream().anyMatch(l -> StringUtils.containsIgnoreCase(l, "disconnected"))) {
            LCException.newException().withMessage("flirc.plugin.error.disconnected.device").buildAndThrow();
        }
        return lines;
    }

    private List<String> convertStreamToLinesRemovingBlank(InputStream inputStream) throws LCException {
        List<String> lines = new ArrayList<>();
        try {
            try (BufferedReader is = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = is.readLine()) != null) {
                    if (StringUtils.isNotBlank(line)) {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            LCException.newException().withMessage("flirc.plugin.error.communication.error").withCause(e).buildAndThrow();
        }
        return lines;
    }
}
