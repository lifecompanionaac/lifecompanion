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

package org.lifecompanion.base.data.prediction.predict4all;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;


public class P4AUserModelUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(P4AUserModelUtils.class);

    private static final String PSWD_UNSAFE = "Predict4All-training";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");

    public static void saveUserModel(final File baseDir, final boolean encrypted, final String txt) {
        baseDir.mkdirs();
        File path = new File(baseDir.getPath() + File.separator + P4AUserModelUtils.FORMAT.format(new Date()) + ".txt");
        try (PrintStream ps = new PrintStream(path, "UTF-8")) {
            ps.println(encrypted ? P4AUserModelUtils.xorEncrypt(txt, P4AUserModelUtils.PSWD_UNSAFE) : txt);
        } catch (Exception e) {
            P4AUserModelUtils.LOGGER.error("Couldn't save user model text to {}", path, e);
        }
    }

    public static String readUserModel(final File path, final boolean encrypted) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            String word;
            while ((word = bufferedReader.readLine()) != null) {
                sb.append(word);
            }
        } catch (Exception e) {
            P4AUserModelUtils.LOGGER.error("Couldn't read user model text from {}", path, e);
        }
        return encrypted ? P4AUserModelUtils.xorDecrypt(sb.toString(), P4AUserModelUtils.PSWD_UNSAFE) : sb.toString();
    }

    public static String xorEncrypt(final String message, final String key) {
        try {
            if (message == null || key == null) {
                return null;
            }

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
            }
            mesg = null;
            keys = null;
            String temp = new String(newmsg);
            return new String(Base64.getEncoder().encode(temp.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    public static String xorDecrypt(String message, final String key) {
        try {
            if (StringUtils.isBlank(message) || key == null) {
                return null;
            }
            char[] keys = key.toCharArray();
            message = new String(Base64.getDecoder().decode(message));
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
            }
            mesg = null;
            keys = null;
            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }
}
