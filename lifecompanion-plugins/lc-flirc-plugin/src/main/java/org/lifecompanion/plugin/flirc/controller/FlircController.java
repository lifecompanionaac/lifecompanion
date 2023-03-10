package org.lifecompanion.plugin.flirc.controller;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.impl.exception.LCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public enum FlircController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(FlircController.class);


    // FIXME: integrate EXE into plugin files

    private final Object callSynchronizeLock = new Object();

    private static final String EXE_PATH = System.getProperty("java.io.tmpdir") + "/flirc_util.exe";

    public void waitForDevice() throws LCException {
        waitFor(true, new ProcessBuilder().command(EXE_PATH, "wait")
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
        );
    }


    public void clearLogsAndDisableRecording() throws LCException {
        waitFor(false, new ProcessBuilder()
                .command(EXE_PATH, "device_log")
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
        );
    }

    public void clearLogsAndEnableRecording() throws LCException {
        analyzeOutputAndFail(
                waitFor(true, new ProcessBuilder().command(EXE_PATH, "device_log", "-i"))
        );
    }


    public List<String> getRecordedCodes() throws Exception {
        List<String> lines = analyzeOutputAndFail(waitFor(true, new ProcessBuilder()
                .command(EXE_PATH, "device_log", "-i")
        ));
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

    public void sendIr(String pattern) throws Exception {
        analyzeOutputAndFail(waitFor(true, new ProcessBuilder().command(
                EXE_PATH, "sendir"
                , "--pattern=" + pattern)
        ));
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
