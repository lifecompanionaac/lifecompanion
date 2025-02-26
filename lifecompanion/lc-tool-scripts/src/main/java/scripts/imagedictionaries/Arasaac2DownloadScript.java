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

package scripts.imagedictionaries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author Mathieu THEBAUD
 */
public class Arasaac2DownloadScript {

    private static final String LANGUAGE_CODE = "fr";
    private static final int LIMIT = 20_000;
    private static final boolean USE_CACHE = true;

    private static final Gson GSON = new GsonBuilder()//
            //.setPrettyPrinting()//
            .create();


    public static void main(String[] args) throws IOException {
        download();
    }

    private static void download() throws IOException {
        File outputDir = new File("C:\\Users\\Mathieu\\Desktop\\TMP\\ARASAAC");
        outputDir.mkdirs();

        File jsonFile = new File(outputDir + "/arasaac-all-pictograms.json");
        OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();

        ArasaacImage[] images;
        if (!USE_CACHE || !jsonFile.exists()) {
            try (okhttp3.Response response = okHttpClient.newCall(new Request.Builder().url("https://api.arasaac.org/v1/pictograms/all/" + LANGUAGE_CODE)
                    .get()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()).execute()) {
                String content = response.body().string();
                IOUtils.writeToFile(jsonFile, content, "UTF-8");
            }
        }
        images = GSON.fromJson(IOUtils.readFileLines(jsonFile, "UTF-8"), ArasaacImage[].class);
        System.out.println(+images.length + " images");

        LoggingProgressIndicator pi = new LoggingProgressIndicator(Math.min(LIMIT, images.length), "Arasaac download");

        int count = 0;
        for (ArasaacImage image : images) {
            // 2340/2340_2500.png
            File destFile = new File(outputDir + "/images/" + image._id + ".png");
            IOUtils.createParentDirectoryIfNeeded(destFile);


            if ((!USE_CACHE || !destFile.exists()) && count < LIMIT) {
                System.out.println("Download " + destFile.getName() + " / keywords " + image.keywords);
                count++;
                try (okhttp3.Response response = okHttpClient.newCall(new Request.Builder().url("https://static.arasaac.org/pictograms/" + image._id + "/" + image._id + "_500.png")
                        .get()
                        .build()).execute()) {
                    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(destFile))) {
                        try (InputStream is = new BufferedInputStream(response.body().byteStream())) {
                            IOUtils.copyStream(is, os);
                        }
                    }
                }
            }

            pi.increment();
        }
    }

    static class ArasaacImage {
        int _id;
        List<ArasaacKeyword> keywords;
        List<String> categories;
        List<String> tags;
    }

    static class ArasaacKeyword {
        String keyword;
        String plural;

        @Override
        public String toString() {
            return "{" +
                    "keyword='" + keyword + '\'' +
                    ", plural='" + plural + '\'' +
                    '}';
        }
    }
}
