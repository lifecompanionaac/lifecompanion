package org.lifecompanion.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

public class SoundUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoundUtils.class);

    public static File trimSilences(final File wavFile, final double silenceThreshold) throws IOException {
        long start = System.currentTimeMillis();
        int WAV_FILEFORMAT_DATA_START = 44;
        File modifiedWavFile = org.lifecompanion.util.IOUtils.getTempFile("silence-trimmed-wav", ".wav");
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(wavFile))) {
            byte[] data = bufferedInputStream.readAllBytes();
            byte[] wavFFData = Arrays.copyOfRange(data, 0, WAV_FILEFORMAT_DATA_START);
            short[] dataAsShort = new short[(data.length - wavFFData.length) / 2];
            short maxVal = 0;
            // Find max to compute threshold (and convert values)
            for (int i = WAV_FILEFORMAT_DATA_START; i < data.length; i += 2) {
                short val = (short) (((data[i + 1] & 0xff) << 8) + (data[i] & 0xff));
                dataAsShort[(i - WAV_FILEFORMAT_DATA_START) / 2] = val;
                maxVal = (short) Math.max(Math.abs(val), maxVal);
            }

            short threshold = (short) (silenceThreshold * maxVal);
            // Find start index
            int startI = 0, endI = 0;
            for (int i = 0; i < dataAsShort.length; i++) {
                if (Math.abs(dataAsShort[i]) > threshold) {
                    startI = i;
                    break;
                }
            }
            // Find end index
            for (int i = dataAsShort.length - 1; i >= 0; i--) {
                if (Math.abs(dataAsShort[i]) > threshold) {
                    endI = i;
                    break;
                }
            }
            // Write output wav file
            try (DataOutputStream bos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(modifiedWavFile)))) {
                bos.write(wavFFData);
                for (int i = startI; i < endI; i++) {
                    bos.writeShort(Short.reverseBytes(dataAsShort[i]));
                }
            }
        }
        LOGGER.debug("Took {} ms to remove silences from file", System.currentTimeMillis() - start);
        return modifiedWavFile;
    }
}
