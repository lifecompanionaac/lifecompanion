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

package org.lifecompanion.framework.server.controller;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import spark.Request;
import spark.Spark;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MultipartUtils {

    public static PartHolder getDtoAndFileInputStream(Request request) {
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iter = upload.getItemIterator(request.raw());
            String dto = null;
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                if ("dto".equals(name)) {
                    dto = IOUtils.readStreamLines(item.openStream(), "UTF-8");
                }
                if ("file".equals(name)) {
                    return new PartHolder(dto, item.openStream());
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class PartHolder {
        final String dto;
        final InputStream fileIS;

        public PartHolder(String dto, InputStream fileIS) {
            this.dto = dto;
            this.fileIS = fileIS;
        }
    }

}
