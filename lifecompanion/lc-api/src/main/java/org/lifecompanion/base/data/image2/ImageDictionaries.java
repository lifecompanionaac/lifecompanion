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

package org.lifecompanion.base.data.image2;

import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.util.Pair;
import org.lifecompanion.api.component.definition.ImageUseComponentI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.image2.ImageDictionaryI;
import org.lifecompanion.api.image2.ImageElementI;
import org.lifecompanion.api.mode.LCStateListener;
import org.lifecompanion.api.mode.ModeListenerI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.control.InstallationConfigurationController;
import org.lifecompanion.base.data.io.json.JsonHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.predict4all.nlp.utils.DaemonThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public enum ImageDictionaries implements LCStateListener, ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDictionaries.class);

    public static final int THUMBNAIL_WIDTH = 100, THUMBNAIL_HEIGHT = 100;
    public static final int SEARCH_PAGE_SIZE = 12;

    /**
     * Available dictionaries
     */
    private final List<ImageDictionary> dictionaries;

    /**
     * Default user dictionary : when the user add an image from its computer
     */
    private ImageDictionary userImagesDictionary;

    /**
     * Default user dictionary : when the user import a configuration with custom images
     */
    private ImageDictionary configurationImageDictionary;

    /**
     * Contains every images
     */
    private final Map<String, ImageElementI> allImages;

    /**
     * Executor to load images
     */
    private final ExecutorService loadingService;

    /**
     * Executor to create thumbnail
     */
    private final ExecutorService thumbnailService;

    /**
     * Currently running loading tasks
     */
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<ImageLoadingTask>> runningLoadingTasks;

    /**
     * Thumbnail for images
     */
    private final Map<String, CachedThumbnailInformation> loadedThumbnails;

    ImageDictionaries() {
        this.dictionaries = new ArrayList<>();
        this.loadedThumbnails = new HashMap<>();
        this.allImages = new HashMap<>();
        this.loadingService = Executors.newSingleThreadExecutor();
        this.thumbnailService = Executors.newSingleThreadExecutor();
        this.runningLoadingTasks = new ConcurrentHashMap<>();
    }

    // ADD/GET IMAGE
    //========================================================================
    public ImageElementI getOrAddToUserImagesDictionary(File imagePath) {
        return getOrAdd(imagePath, userImagesDictionary);
    }

    public ImageElementI getOrAddToConfigurationImageDictionary(File imagePath) {
        return getOrAdd(imagePath, configurationImageDictionary);
    }

    private ImageElementI getOrAdd(File imagePath, ImageDictionaryI dictionary) {
        try {
            final String originalFilenameWithoutExtension = FileNameUtils.getNameWithoutExtension(imagePath);

            // Hash image to find its ID
            final String id = IOUtils.fileSha256HexToString(imagePath);
            final ImageElementI previousImage = allImages.get(id);

            // Check for imported images (will be used only for custom dictionaries)
            final File copiedImageTargetForCustomDir = new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + LCConstant.IMPORTED_IMAGE_DIR_NAME + File.separator + id + "." + FileNameUtils.getExtension(imagePath));

            // Add or replace with newer element if previous don't exist / is not present anymore / wasn't already copied
            if (previousImage == null || !previousImage.isImageFileExist() || (dictionary.isCustomDictionary() && !copiedImageTargetForCustomDir.exists())) {
                // Copy the source image if imported (will not depend anymore on the real image source)
                if (dictionary.isCustomDictionary()) {
                    IOUtils.copyFiles(imagePath, copiedImageTargetForCustomDir);
                    imagePath = copiedImageTargetForCustomDir;
                }
                // Create the updated/new image
                ImageElement newerImage = new ImageElement(id, originalFilenameWithoutExtension, FluentHashMap.map(UserBaseConfiguration.INSTANCE.userLanguageProperty().get(), new String[]{originalFilenameWithoutExtension}), imagePath);
                newerImage.setDictionary(dictionary);
                allImages.put(id, newerImage);
                // If previous image existed (and don't exist anymore)
                if (previousImage != null) {
                    previousImage.getDictionary().getImages().remove(previousImage);
                }
                dictionary.getImages().add(newerImage);
                return newerImage;
            } else {
                return previousImage;
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't get or add image {}", imagePath, e);
        }
        return null;
    }

    public ImageElementI getById(String imageId) {
        return allImages.get(imageId);
    }
    //========================================================================

    // THUMBNAIL
    //========================================================================
    public void requestLoadThumbnail(ImageElementI imageElementI, Consumer<CachedThumbnailInformation> callback) {
        File thumbnailRoot = new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + File.separator + LCConstant.THUMBNAIL_DIR_NAME + File.separator);
        CachedThumbnailInformation cachedThumbnailInformation = this.loadedThumbnails.get(imageElementI.getId());
        if (cachedThumbnailInformation != null) {
            callback.accept(cachedThumbnailInformation);
        } else {
            File thumbnailPath = new File(thumbnailRoot.getPath() + File.separator + imageElementI.getId() + "." + imageElementI.getExtension());
            // Prepare loading task
            ImageLoadingTask imageLoadingTask = new ImageLoadingTask(thumbnailPath.getPath(), null, thumbnailPath, THUMBNAIL_WIDTH, -1, true, true, null);
            imageLoadingTask.setOnSucceeded(e -> {
                try {
                    Image loadedImage = imageLoadingTask.get();
                    CachedThumbnailInformation info = new CachedThumbnailInformation(loadedImage, UIUtils.computeFullImageViewPort(loadedImage));
                    loadedThumbnails.put(imageElementI.getId(), info);
                    callback.accept(info);
                } catch (Exception ex) {
                    LOGGER.warn("Loading thumbnail failed", ex);
                }
            });
            // Create thumbnail if needed
            if (thumbnailPath.exists()) {
                this.loadingService.submit(imageLoadingTask);
            } else {
                ThumbnailGenerationTask thumbnailGenerationTask = new ThumbnailGenerationTask(imageElementI, thumbnailPath);
                thumbnailGenerationTask.setOnSucceeded(e -> this.loadingService.submit(imageLoadingTask));
                thumbnailService.submit(thumbnailGenerationTask);
            }
        }
    }

    public void clearThumbnailCache() {
        this.loadedThumbnails.clear();
    }
    //========================================================================


    // SEARCH
    //========================================================================
    private static final Comparator<Pair<ImageElementI, Double>> SCORE_MAP_COMPARATOR = (e1, e2) -> Double.compare(e2.getValue(), e1.getValue());

    public List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchImage(String rawSearchString) {
        long start = System.currentTimeMillis();
        List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> result = new ArrayList<>();
        String searchFull = StringUtils.stripToEmpty(rawSearchString).toLowerCase();
        int totalResultCount = 0;
        if (searchFull.length() > 2) {
            for (ImageDictionary imageDictionary : this.dictionaries) {
                List<ImageElementI> resultList = imageDictionary.getImages()
                        .parallelStream()
                        .map(e -> new Pair<>(e, getSimilarityScore(e.getKeywords(), searchFull)))
                        .sorted(SCORE_MAP_COMPARATOR)
                        .filter(e -> e.getValue() > LCUtils.SIMILARITY_CONTAINS)
                        .map(Pair::getKey)
                        .collect(Collectors.toList());
                if (LangUtils.isNotEmpty(resultList)) {
                    final List<List<ImageElementI>> resultPages = IntStream.range(0, resultList.size())
                            .filter(i -> i % SEARCH_PAGE_SIZE == 0)
                            .mapToObj(i -> resultList.subList(i, Math.min(i + SEARCH_PAGE_SIZE, resultList.size())))
                            .collect(Collectors.toList());
                    result.add(new Pair<>(imageDictionary, resultPages));
                    LOGGER.info("Found {} result in {} dictionaries, result is {} pages", resultList.size(), imageDictionary.getName(), resultPages.size());
                    totalResultCount += resultList.size();
                }
            }
        }
        LOGGER.info("Search executed in image dictionaries in {} ms - found {} elements (in {} dictionaries, for \"{}\")", System.currentTimeMillis() - start, totalResultCount, result.size(), rawSearchString);
        return result;
    }

    public double getSimilarityScore(String[] keywords, String searchFull) {
        double score = 0.0;
        for (String source : keywords) {
            score += LCUtils.getSimilarityScoreFor(searchFull, source, s -> org.lifecompanion.framework.utils.Pair.of(s, 1.0));
        }
        return score;
    }

    //========================================================================

    // IMAGE LOADING
    //========================================================================
    public void requestImageLoading(ImageLoadingTask loadingTask) {
        cancelLoadImage(loadingTask.getImageId());
        this.runningLoadingTasks.computeIfAbsent(loadingTask.getImageId(), id -> new CopyOnWriteArrayList<>()).add(loadingTask);
        EventHandler<WorkerStateEvent> eventHandlerTaskFinished = e -> {
            CopyOnWriteArrayList<ImageLoadingTask> loadingTasks = runningLoadingTasks.get(loadingTask.getImageId());
            if (loadingTasks != null) {
                loadingTasks.remove(loadingTask);
            }
        };
        loadingTask.setOnCancelled(eventHandlerTaskFinished);
        loadingTask.setOnFailed(eventHandlerTaskFinished);
        loadingTask.setOnSucceeded(eventHandlerTaskFinished);
        if (!this.loadingService.isShutdown()) {
            this.loadingService.submit(loadingTask);
        }
    }

    public void cancelLoadImage(String imageId) {
        CopyOnWriteArrayList<ImageLoadingTask> previousTasks = runningLoadingTasks.remove(imageId);
        if (previousTasks != null) {
            List<ImageLoadingTask> clearTasks = new ArrayList<>(previousTasks);
            previousTasks.removeAll(clearTasks);
            clearTasks.forEach(ImageLoadingTask::cancel);
        }
    }

    public boolean isRunningImageLoadingTask() {
        return this.runningLoadingTasks.values().stream().anyMatch(c -> !c.isEmpty());
    }
    //========================================================================

    // IO
    //========================================================================
    public void loadDictionaries() {
        File imageDictionariesRoot = new File(LCConstant.DEFAULT_IMAGE_DICTIONARIES);
        File[] potentialDictionaries = imageDictionariesRoot.listFiles();
        if (potentialDictionaries != null) {
            for (File potentialDictionary : potentialDictionaries) {
                if (potentialDictionary.isDirectory()) {
                    loadImageDictionary(potentialDictionary);
                }
            }
        }
        userImagesDictionary = this.loadImageDictionary(new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + LCConstant.IMAGE_RESOURCES_DIR_NAME + LCConstant.DICTIONARY_NAME_USER_IMAGES));
        if (userImagesDictionary == null) {
            userImagesDictionary = new ImageDictionary();
            this.userImagesDictionary.setName(Translation.getText("image.dictionary.default.custom.dic"));
            this.userImagesDictionary.setCustomDictionary(true);
            this.dictionaries.add(userImagesDictionary);
        }
        configurationImageDictionary = this.loadImageDictionary(new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + LCConstant.IMAGE_RESOURCES_DIR_NAME + LCConstant.DICTIONARY_NAME_CONFIGURATION_IMAGES));
        if (configurationImageDictionary == null) {
            configurationImageDictionary = new ImageDictionary();
            this.configurationImageDictionary.setName(Translation.getText("image.dictionary.default.imported.dic"));
            this.configurationImageDictionary.setCustomDictionary(true);
            this.dictionaries.add(configurationImageDictionary);
        }
    }

    private ImageDictionary loadImageDictionary(File potentialDictionaryDirectory) {
        File dictionaryFile = new File(potentialDictionaryDirectory.getParentFile().getPath() + File.separator + FileNameUtils.getNameWithoutExtension(potentialDictionaryDirectory) + ".json");
        if (dictionaryFile.exists()) {
            try (Reader is = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFile), StandardCharsets.UTF_8))) {
                ImageDictionary imageDictionary = JsonHelper.GSON.fromJson(is, ImageDictionary.class);
                imageDictionary.setImageDirectory(potentialDictionaryDirectory);
                imageDictionary.loaded(this.allImages);
                this.dictionaries.add(imageDictionary);
                LOGGER.info("Image dictionary {} loaded from {} ({} images)", imageDictionary.getName(), dictionaryFile, imageDictionary.getImages().size());
                return imageDictionary;
            } catch (Exception e) {
                LOGGER.error("Couldn't load dictionary from {}", potentialDictionaryDirectory, e);
            }
        } else {
            LOGGER.warn("Found a folder in image dictionary folder, but didn't find its description file {}", dictionaryFile);
        }
        return null;
    }


    @Override
    public void lcStart() {
        this.loadDictionaries();
//        LCNamedThreadFactory.daemonThreadFactory("Image-Watcher").newThread(() -> {
//            while (true) {
//                Set<ImageElement> imageLoaded = new ArrayList<>(this.allImages.values()).stream().map(img -> (ImageElement) img).filter(img -> img.loadedImageProperty().get() != null).collect(Collectors.toSet());
//                System.err.println("Loaded image : " + imageLoaded.size());
//                //imageLoaded.stream().forEach(img -> System.err.println("\t" + img.getName() + " = " + (img.getLoadingRequest().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.joining(" ")))));
//                LCUtils.safeSleep(5_000);
//            }
//        }).start();
    }

    @Override
    public void lcExit() {
        // Save default user dictionary
        saveDictionary(userImagesDictionary, LCConstant.DICTIONARY_NAME_USER_IMAGES);
        saveDictionary(configurationImageDictionary, LCConstant.DICTIONARY_NAME_CONFIGURATION_IMAGES);
        this.loadingService.shutdownNow();
        this.thumbnailService.shutdownNow();
        this.runningLoadingTasks.clear();
        clearThumbnailCache();
        LOGGER.info("Image dictionaries closed");
    }

    private void saveDictionary(ImageDictionaryI dictionaryI, String fileName) {
        // TODO : move to helper
        File dicFile = new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + LCConstant.IMAGE_RESOURCES_DIR_NAME + fileName);
        IOUtils.createParentDirectoryIfNeeded(dicFile);
        try (PrintWriter pw = new PrintWriter(dicFile, StandardCharsets.UTF_8)) {
            JsonHelper.GSON.toJson(dictionaryI, pw);
            LOGGER.info("Image dictionary {} saved ({} images)", dictionaryI.getName(), dictionaryI.getImages().size());
        } catch (Exception e) {
            LOGGER.error("Couldn't save JSON file", e);
        }
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        //Use mode start : empty thumbnail cache
        clearThumbnailCache();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
    }
    //========================================================================
}
