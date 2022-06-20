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

import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.model.server.service.FileStorageServiceI;

import java.io.*;

import static org.lifecompanion.framework.model.server.LifeCompanionFrameworkServerConstant.DEFAULT_OK_RETURN_VALUE;

public class LocalFileStorageService implements FileStorageServiceI {
    @Override
    public String getID() {
        return "localfilestorage";
    }

    @Override
    public String getFileIdFromPrefix(String prefix) {
        File[] files = new File("./data/").listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getAbsolutePath().startsWith(prefix)) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    @Override
    public String saveFile(InputStream inputStream, String name, long length) throws IOException {
        File file = new File("./data/" + name);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            IOUtils.copyStream(inputStream, fos);
        }
        return file.getAbsolutePath();
    }

    @Override
    public String generateFileUrl(String id) throws IOException {
        return "http://localhost:1234/public/download-file/" + id;
    }

    @Override
    public String downloadFileTo(String id, OutputStream os) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(id))) {
            IOUtils.copyStream(fis, os);
            return DEFAULT_OK_RETURN_VALUE;
        }
    }

    @Override
    public void removeFile(String id) throws IOException {
        new File(id).delete();
    }

}
