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

import org.lifecompanion.framework.model.server.service.FileStorageServiceI;
import org.lifecompanion.framework.server.LifeCompanionFrameworkServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.lifecompanion.framework.model.server.LifeCompanionFrameworkServerConstant.DEFAULT_OK_RETURN_VALUE;

public enum FileStorageService implements FileStorageServiceI {
    INSTANCE;
    private final FileStorageServiceI IMPLEMENTATION;

    private final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);

    FileStorageService() {
        IMPLEMENTATION = LifeCompanionFrameworkServer.ONLINE ? new AmazonFileStorageService() : new LocalFileStorageService();
        LOGGER.info("File storage initialized to {}", IMPLEMENTATION.getID());
    }

    @Override
    public String getID() {
        return "router";
    }

    @Override
    public String generateFileUrl(String id) throws IOException {
        return IMPLEMENTATION.generateFileUrl(id);
    }

    @Override
    public String getFileIdFromPrefix(String prefix) {
        return IMPLEMENTATION.getFileIdFromPrefix(prefix);
    }

    @Override
    public String downloadFileTo(String id, OutputStream os) throws IOException {
        IMPLEMENTATION.downloadFileTo(id, os);
        return DEFAULT_OK_RETURN_VALUE;
    }

    @Override
    public String saveFile(InputStream inputStream, String name, long length) throws IOException {
        return IMPLEMENTATION.saveFile(inputStream, name, length);
    }

    @Override
    public void removeFile(String id) throws IOException {
        IMPLEMENTATION.removeFile(id);
    }
}
