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
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.model.server.service.FileStorageServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;

import static org.lifecompanion.framework.model.server.LifeCompanionFrameworkServerConstant.DEFAULT_OK_RETURN_VALUE;

public class AmazonFileStorageService implements FileStorageServiceI {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonFileStorageService.class);


    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucket;

    public AmazonFileStorageService() {
        this.bucket = System.getenv("AMAZON_S3_BUCKET");
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                System.getenv("AMAZON_S3_ACCES_KEY"),
                System.getenv("AMAZON_S3_SECRET")
        );
        Region region = Region.of(System.getenv("AMAZON_S3_REGION"));
        s3 = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(region)
                .build();
        presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(region)
                .build();
    }

    @Override
    public String getID() {
        return "amazons3";
    }

    @Override
    public String getFileIdFromPrefix(String prefix) {
        LOGGER.info("Request prefix is : {}", prefix);
        ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();
        ListObjectsResponse response = s3.listObjects(listObjects);
        List<S3Object> objectsForPrefix = response.contents();
        LOGGER.info("Found objects : {}", objectsForPrefix);
        if (CollectionUtils.isEmpty(objectsForPrefix)) return null;
        else
            return objectsForPrefix.get(0).key();
    }

    @Override
    public String saveFile(InputStream inputStream, String name, long length) throws IOException {
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(name)
                        .build()
                , RequestBody.fromInputStream(new BufferedInputStream(inputStream), length));
        return name;
    }

    @Override
    public String generateFileUrl(String id) throws IOException {
        final PresignedGetObjectRequest urlGen = presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofHours(4))
                        .getObjectRequest(
                                GetObjectRequest.builder()
                                        .bucket(bucket)
                                        .key(id)
                                        .build())
                        .build());
        return urlGen.url().toExternalForm();
    }

    @Override
    public String downloadFileTo(String id, OutputStream os) throws IOException {
        try (InputStream result = s3.getObject(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(id)
                        .build()
        )) {
            try (result) {
                IOUtils.copyStream(result, os);
                return DEFAULT_OK_RETURN_VALUE;
            }
        }
    }

    @Override
    public void removeFile(String id) {
        s3.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(id)
                        .build()
        );
    }
}
