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

package org.lifecompanion.framework.server.service.model;

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.app.VersionUtils.VersionInfo;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.client.UpdateFileProgress;
import org.lifecompanion.framework.model.client.UpdateFileProgressType;
import org.lifecompanion.framework.model.server.dto.*;
import org.lifecompanion.framework.model.server.error.BusinessLogicError;
import org.lifecompanion.framework.model.server.update.*;
import org.lifecompanion.framework.server.controller.handler.BusinessLogicException;
import org.lifecompanion.framework.server.data.dao.ApplicationUpdateDao;
import org.lifecompanion.framework.server.data.dao.DataSource;
import org.lifecompanion.framework.server.service.FileStorageService;
import org.lifecompanion.framework.server.service.SoftwareStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import spark.Request;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.lifecompanion.framework.model.server.LifeCompanionFrameworkServerConstant.DEFAULT_OK_RETURN_VALUE;

public enum ApplicationUpdateService {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationUpdateService.class);

    public String finishApplicationUpdate(FinishApplicationUpdateDto dto) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
            ApplicationUpdateDao.INSTANCE.updateApplicationUpdateVisibilityAndDate(connection, dto.getUpdateId(), dto.getTargetVisibility(), new Date());
            connection.commit();
            return DEFAULT_OK_RETURN_VALUE;
        }
    }

    public String uploadApplicationUpdateFile(UploadUpdateFileDto dto, InputStream file) throws IOException {
        try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
            String fileId = FileStorageService.INSTANCE.saveFile(file, generateFileName(dto), dto.getFileSize());
            ApplicationUpdateDao.INSTANCE.updateApplicationUpdateFileFileStorageId(connection, dto.getApplicationUpdateFileIdInDb(), fileId);
            connection.commit();
            return DEFAULT_OK_RETURN_VALUE;
        }
    }

    private String generateFileName(UploadUpdateFileDto dto) {
        return "updates/" + dto.getApplicationUpdateIdInDb() + "/" + (dto.getSystem() != null ? dto.getSystem().getCode() : "ALL") + "/" + dto.getFilePath() + "_" + System.currentTimeMillis();
    }


    // Backward compatibility : prior version should get the application diff without "launcher" file types
    public List<UpdateFileProgress> getLastApplicationUpdateDiffOld(String applicationId, SystemType system, String fromVersion, boolean preview) {
        final List<UpdateFileProgress> lastApplicationUpdateDiff = getLastApplicationUpdateDiff(applicationId, system, fromVersion, preview);
        return lastApplicationUpdateDiff.stream().filter(f -> f.getTargetType() != TargetType.LAUNCHER).collect(Collectors.toList());
    }

    public List<UpdateFileProgress> getLastApplicationUpdateDiff(String applicationId, SystemType system, String fromVersion, boolean preview) {
        VersionInfo fromVersionInfo = VersionInfo.parse(fromVersion);
        List<ApplicationUpdate> allUpdateAbove = ApplicationUpdateDao.INSTANCE.getAllUpdateAboveOrderByVersion(applicationId,
                fromVersionInfo.getMajor(), fromVersionInfo.getMinor(), fromVersionInfo.getPatch(), preview);

        // Keep only the last version of each file
        Map<String, ApplicationUpdateFile> files = new HashMap<>();
        for (ApplicationUpdate applicationUpdate : allUpdateAbove) {
            List<ApplicationUpdateFile> filesForUpdate = ApplicationUpdateDao.INSTANCE.getFilesForUpdate(applicationUpdate.getId(), system);
            for (ApplicationUpdateFile fileForUpdate : filesForUpdate) {
                if (fileForUpdate.getFileState() != FileState.SAME) {
                    files.put(fileForUpdate.getTargetPath(), fileForUpdate);
                }
            }
        }

        // Create update file list
        return files.values()//
                .stream()//
                .map(f -> new UpdateFileProgress(f.getId(), f.getTargetPath(), convertToInitialProgressState(f.getFileState()), f.getTargetType(), f.getFileHash(), f.getFileSize(), f.isToUnzip()))//
                .collect(Collectors.toList());
    }

    public String getApplicationFileDownloadUrl(String fileId) throws IOException {
        ApplicationUpdateFile fileById = ApplicationUpdateDao.INSTANCE.getFileById(fileId);
        return FileStorageService.INSTANCE.generateFileUrl(fileById.getFileStorageId(), UUID.randomUUID().toString());
    }

    private UpdateFileProgressType convertToInitialProgressState(FileState fileState) {
        switch (fileState) {
            case SAME:
                return UpdateFileProgressType.TO_COPY;
            case CREATED:
            case CHANGED:
                return UpdateFileProgressType.TO_DOWNLOAD;
            case REMOVED:
                return UpdateFileProgressType.TO_REMOVE;
        }
        return null;
    }

    public ApplicationUpdateInitializedDto initializeApplicationUpdate(InitializeApplicationUpdateDto dto) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
            // Check that there is no existing file for this update and system : count if there is file for this system on this update
            if (ApplicationUpdateDao.INSTANCE.countByApplicationAndVersionAndSystem(connection, dto.getApplicationId(), dto.getVersion(), dto.getSystem()) > 0) {
                throw new BusinessLogicException(BusinessLogicError.EXISTING_UPDATE_SAME_VERSION_SYSTEM);
            }

            // Get latest update (include preview updates > update should always be based on the last ones)
            ApplicationUpdate lastUpdate = ApplicationUpdateDao.INSTANCE.getLastestUpdateFor(connection, dto.getApplicationId(), true);

            // Get the existing update for same version > will update it
            ApplicationUpdate applicationUpdate = ApplicationUpdateDao.INSTANCE.getUpdateByApplicationAndVersion(connection, dto.getApplicationId(), dto.getVersion());
            VersionInfo versionInfo = VersionInfo.parse(dto.getVersion());
            if (applicationUpdate != null) {
                ApplicationUpdateDao.INSTANCE.updateApplicationUpdateVisibilityAndDate(connection, applicationUpdate.getId(), UpdateVisibility.UPLOADING, new Date());
                LOGGER.info("Update {} updated", applicationUpdate.getId());
            } else {
                applicationUpdate = new ApplicationUpdate();
                applicationUpdate.setId(UUID.randomUUID().toString());
                applicationUpdate.setApplicationId(dto.getApplicationId());
                applicationUpdate.setDescription(dto.getDescription());
                applicationUpdate.setVersion(dto.getVersion());
                applicationUpdate.setVersionMajor(versionInfo.getMajor());
                applicationUpdate.setVersionMinor(versionInfo.getMinor());
                applicationUpdate.setVersionPatch(versionInfo.getPatch());
                applicationUpdate.setVisibility(UpdateVisibility.UPLOADING);
                applicationUpdate.setUpdateDate(new Date());
                ApplicationUpdateDao.INSTANCE.insertApplicationUpdate(connection, applicationUpdate);
                LOGGER.info("Update {} inserted", applicationUpdate.getId());
            }

            // No update -> all file should be created
            if (lastUpdate == null) {
                List<ApplicationUpdateFile> createdFiles = new ArrayList<>();
                for (ApplicationUpdateFileDto file : dto.getFiles()) {
                    ApplicationUpdateFile updateFile = createApplicatioUpdateFile(applicationUpdate, file, dto.getSystem());
                    updateFile.setFileState(FileState.CREATED);
                    file.setApplicationUpdateFileIdInDb(updateFile.getId());
                    createdFiles.add(updateFile);
                }
                ApplicationUpdateDao.INSTANCE.insertApplicationUpdateFiles(connection, createdFiles);
                LOGGER.info("No previous update found, update inserted to database");
                connection.commit();
                return new ApplicationUpdateInitializedDto(applicationUpdate.getId(), dto.getFiles());
            }
            // Update -> compare files
            else {
                List<ApplicationUpdateFile> newUpdateFiles = new ArrayList<>();
                List<ApplicationUpdateFileDto> filesToUpload = new ArrayList<>();
                Map<String, ApplicationUpdateFileDto> newFilesMap = new HashMap<>();

                // Get all previous file from DB
                List<String> newFilePaths = dto.getFiles().stream().map(ApplicationUpdateFileDto::getTargetPath).collect(Collectors.toList());
                Map<String, ApplicationUpdateFile> existingFileLastChangedByPath = findLastModifiedFileForAndGroupbyPath(connection, dto, versionInfo, newFilePaths);

                LOGGER.info("Got {} existing previous changed file from database, will now check changed/created files", existingFileLastChangedByPath.size());

                // For each file in update, find the last modified file
                for (ApplicationUpdateFileDto newFile : dto.getFiles()) {
                    ApplicationUpdateFile existingFileLastChanged = existingFileLastChangedByPath.get(newFile.getTargetPath());
                    // Create the file (if not added to newUpdateFiles it will not be inserted in DB)
                    ApplicationUpdateFile file = createApplicatioUpdateFile(applicationUpdate, newFile, dto.getSystem());
                    newFile.setApplicationUpdateFileIdInDb(file.getId());
                    newFilesMap.put(file.getTargetPath(), newFile);
                    // LOGGER.info("Check for existing last file {} vs {}", existingFileLastChanged, newFile);
                    // Insert only if created or changed
                    if (existingFileLastChanged != null && !existingFileLastChanged.getFileState().isRemoved()) {
                        if (!newFile.getFileHash().equals(existingFileLastChanged.getFileHash())) {
                            LOGGER.info("Detected a changed file : {}", newFile);
                            file.setFileState(FileState.CHANGED);
                            filesToUpload.add(newFile);
                            newUpdateFiles.add(file);
                        }
                    }
                    // Find didn't exist in previous update : created
                    else {
                        LOGGER.info("Detected a created file : {}", newFile);
                        file.setFileState(FileState.CREATED);
                        filesToUpload.add(newFile);
                        newUpdateFiles.add(file);
                    }
                }

                // Check with previous if some of them were deleted
                Set<String> previousUpdatesFilesPaths = new HashSet<>(ApplicationUpdateDao.INSTANCE.getAllUniqueUpdateFilePathBellow(connection, dto.getApplicationId(), dto.getSystem(), versionInfo.getMajor(), versionInfo.getMinor(), versionInfo.getPatch()));
                Map<String, ApplicationUpdateFile> lastUpdatesFilesLastModified = findLastModifiedFileForAndGroupbyPath(connection, dto, versionInfo, previousUpdatesFilesPaths);
                LOGGER.info("Got {} previous update files from database, will check for removed files", lastUpdatesFilesLastModified.size());

                for (String previousUpdatesFilesPath : previousUpdatesFilesPaths) {
                    ApplicationUpdateFile lastUpdatesFileLastModified = lastUpdatesFilesLastModified.get(previousUpdatesFilesPath);
                    if (!newFilesMap.containsKey(previousUpdatesFilesPath) && lastUpdatesFileLastModified != null && !lastUpdatesFileLastModified.getFileState().isRemoved()) {
                        ApplicationUpdateFile newDeletedFile = lastUpdatesFileLastModified.copy();
                        newDeletedFile.setId(UUID.randomUUID().toString());
                        newDeletedFile.setApplicationUpdateId(applicationUpdate.getId());
                        newDeletedFile.setFileState(FileState.REMOVED);
                        newUpdateFiles.add(newDeletedFile);
                    }
                }

                LOGGER.info("Every changed detected, insert {} file to database", newUpdateFiles.size());
                ApplicationUpdateDao.INSTANCE.insertApplicationUpdateFiles(connection, newUpdateFiles);
                connection.commit();
                return new ApplicationUpdateInitializedDto(applicationUpdate.getId(), filesToUpload);
            }
        }
    }

    // (note : existingFileLastChanged is sorted, so the first in the list is the last changed file)
    private Map<String, ApplicationUpdateFile> findLastModifiedFileForAndGroupbyPath(Connection connection, InitializeApplicationUpdateDto dto, VersionInfo versionInfo, Collection<String> newFilePaths) {
        Map<String, ApplicationUpdateFile> existingFileLastChangedByPath = new HashMap<>();
        List<ApplicationUpdateFile> existingFilesLastChanged = ApplicationUpdateDao.INSTANCE.getLastModifiedUpdateFile(connection, dto.getApplicationId(), dto.getSystem(), versionInfo.getMajor(), versionInfo.getMinor(), versionInfo.getPatch(), newFilePaths);
        for (ApplicationUpdateFile existingFile : existingFilesLastChanged) {
            existingFileLastChangedByPath.putIfAbsent(existingFile.getTargetPath(), existingFile);
        }
        return existingFileLastChangedByPath;
    }

    private ApplicationUpdateFile createApplicatioUpdateFile(ApplicationUpdate applicationUpdate, ApplicationUpdateFileDto file, SystemType system) {
        ApplicationUpdateFile updateFile = new ApplicationUpdateFile();
        updateFile.setId(UUID.randomUUID().toString());
        updateFile.setApplicationUpdateId(applicationUpdate.getId());
        updateFile.setTargetPath(file.getTargetPath());
        updateFile.setFileHash(file.getFileHash());
        updateFile.setFileSize(file.getFileSize());
        updateFile.setTargetType(file.getTargetType());
        updateFile.setSystem(file.getTargetType().isSystemTypeDependant() ? system : null);
        updateFile.setFileState(FileState.SAME);
        updateFile.setToUnzip(file.isToUnzip());
        updateFile.setFileStorageId(file.getPresetFileStorageId());
        return updateFile;
    }

    public ApplicationUpdate getLastApplicationUpdate(String application, boolean preview) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return ApplicationUpdateDao.INSTANCE.getLastestUpdateFor(connection, application, preview);
        }
    }

    public List<String> cleanPreviousUpdates(String applicationId) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
            List<String> modifications = new ArrayList<>();
            SystemType[] systemTypes = SystemType.allExpectMobile();
            for (SystemType systemType : systemTypes) {
                // Get all files for all update of this system
                executeCleanForSystem(applicationId, connection, modifications, systemType);
            }
            // Get all files for all update of no system (to get SOFTWARE_RESOURCES)
            executeCleanForSystem(applicationId, connection, modifications, null);
            connection.commit();
            return modifications;
        }
    }

    private void executeCleanForSystem(String applicationId, Connection connection, List<String> modifications, SystemType systemType) {
        Set<String> allUniqueUpdateFilePath = new HashSet<>(ApplicationUpdateDao.INSTANCE.getAllUniqueUpdateFilePath(connection, applicationId, systemType));
        for (String uniqueFilePath : allUniqueUpdateFilePath) {
            List<ApplicationUpdateFile> lastUpdatesFileLastModified = ApplicationUpdateDao.INSTANCE.getAllFileForTargetOrderByVersionDesc(connection, applicationId, systemType, uniqueFilePath);
            // Start at 1 > should keep the last version of the file
            for (int i = 1; i < lastUpdatesFileLastModified.size(); i++) {
                ApplicationUpdateFile updateFile = lastUpdatesFileLastModified.get(i);
                String fileStorageId = updateFile.getFileStorageId();
                if (StringUtils.isNotBlank(fileStorageId)) {
                    try {
                        FileStorageService.INSTANCE.removeFile(fileStorageId);
                        ApplicationUpdateDao.INSTANCE.updateApplicationUpdateFileFileStorageId(connection, updateFile.getId(), null);
                        LOGGER.info("Removed {} with storage ID {} from storage to only keep the last version", updateFile.getTargetPath(), fileStorageId);
                        modifications.add("Removed " + updateFile.getTargetPath() + "(" + updateFile.getSystem() + ") - " + updateFile.getFileState() + " / update " + updateFile.getApplicationUpdateId());
                    } catch (IOException e) {
                        LOGGER.error("Couldn't remove file {} with storage ID {} from storage", updateFile.getId(), fileStorageId, e);
                    }
                }
            }
        }
    }

    public void addUpdateDoneStat(Request request, AddApplicationUpdateStatDto addApplicationUpdateStatDto) {
        SoftwareStatService.INSTANCE.pushStat(request, SoftwareStatService.StatEvent.APP_UPDATE_DONE, addApplicationUpdateStatDto.getVersion(), addApplicationUpdateStatDto.getSystemType(), addApplicationUpdateStatDto.getRecordedDate());
    }

    public List<String> deleteUpdate(String applicationUpdateId) throws IOException {
        List<String> logs = new ArrayList<>();
        final List<ApplicationUpdateFile> filesForUpdates = ApplicationUpdateDao.INSTANCE.getFilesForUpdate(applicationUpdateId, null);
        try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {

            final ApplicationUpdate applicationUpdate = ApplicationUpdateDao.INSTANCE.getById(applicationUpdateId);
            if (applicationUpdate != null) {
                logs.add("Deleted update will be " + applicationUpdate.getVersion() + " / " + applicationUpdate.getApplicationId() + " / " + applicationUpdate.getUpdateDate());

                // Get all files linked to this application update and remove then from storage (then from DB)
                for (ApplicationUpdateFile updateFile : filesForUpdates) {
                    String fileStorageId = updateFile.getFileStorageId();
                    if (StringUtils.isNotBlank(fileStorageId)) {
                        try {
                            FileStorageService.INSTANCE.removeFile(fileStorageId);
                        } catch (IOException e) {
                            throw new IOException("Couldn't remove file " + updateFile.getId() + " with storage ID " + fileStorageId + " from storage, abort update deletion", e);
                        }
                        ApplicationUpdateDao.INSTANCE.deleteApplicationUpdateFile(connection, updateFile.getId());
                        logs.add("Deleted application update file : " + updateFile.getTargetPath() + "(" + updateFile.getSystem() + ") - " + updateFile.getFileState());
                    }
                }
                // Once files are all delete, delete the application update itself
                ApplicationUpdateDao.INSTANCE.deleteApplicationUpdate(connection, applicationUpdateId);
                logs.add("Deleted application update " + applicationUpdateId);

                connection.commit();
            } else {
                logs.add("No update found for ID " + applicationUpdateId);
            }
        }
        return logs;
    }
}
