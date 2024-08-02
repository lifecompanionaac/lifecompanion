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

package org.lifecompanion.model.impl.imagedictionary;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.util.Pair;
import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.CryptUtils;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.ImageUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum ImageDictionaries implements LCStateListener, ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDictionaries.class);

    public static final int THUMBNAIL_WIDTH = 100, THUMBNAIL_HEIGHT = 100;
    public static final int SEARCH_PAGE_SIZE = 12;
    public static final int ALL_PAGE_SIZE = 18;
    private static final String CHECKING_ID_PASS_UNSAFE = "CheckingImage123456!";

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
     * A temp dictionary which is not saved or displayed in research and just used to create temp image from files
     */
    private final ImageDictionary hiddenImageDictionary;

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
        this.hiddenImageDictionary = new ImageDictionary();
        this.hiddenImageDictionary.setCustomDictionary(true);
    }

    public List<ImageDictionary> getDictionaries() {
        return dictionaries;
    }

    // ADD/GET IMAGE
    //========================================================================
    public ImageElementI getOrAddToUserImagesDictionary(File imagePath) {
        return getOrAdd(imagePath, userImagesDictionary, null);
    }

    public ImageElementI getOrAddToConfigurationImageDictionary(File imagePath) {
        return getOrAdd(imagePath, configurationImageDictionary, null);
    }

    public ImageElementI getOrAddForVideoThumbnail(File imagePath, String name) {
        return getOrAdd(imagePath, hiddenImageDictionary, name);
    }

    private ImageElementI getOrAdd(File imagePath, ImageDictionaryI dictionary, String forceName) {
        try {
            String originalFileName = FileNameUtils.getNameWithoutExtension(imagePath);

            // Hash image to find its ID
            final String id = IOUtils.fileSha256HexToString(imagePath);
            final ImageElementI previousImage = allImages.get(id);

            // Check for imported images (will be used only for custom dictionaries)
            final File copiedImageTargetForCustomDir = new File(InstallationConfigurationController.INSTANCE.getUserDirectory()
                    .getPath() + LCConstant.IMPORTED_IMAGE_DIR_NAME + File.separator + id + "." + FileNameUtils.getExtension(imagePath));

            // Add or replace with newer element if previous don't exist / is not present anymore / wasn't already copied
            if (previousImage == null || !previousImage.isImageFileExist() || (dictionary.isCustomDictionary() && !copiedImageTargetForCustomDir.exists())) {
                // Copy the source image if imported (will not depend anymore on the real image source)
                if (dictionary.isCustomDictionary()) {
                    IOUtils.copyFiles(imagePath, copiedImageTargetForCustomDir);
                    imagePath = copiedImageTargetForCustomDir;
                }
                // Create the updated/new image
                final String imageName = forceName != null ? forceName : originalFileName;
                ImageElement newerImage = new ImageElement(id,
                        imageName,
                        FluentHashMap.map(UserConfigurationController.INSTANCE.userLanguageProperty().get(), new String[]{imageName}),
                        imagePath);
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
                    CachedThumbnailInformation info = new CachedThumbnailInformation(loadedImage, ImageUtils.computeFullImageViewPort(loadedImage));
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
                thumbnailGenerationTask.setOnFailed(e -> LOGGER.warn("Could not generate thumbnail {}", thumbnailPath, e.getSource().getException()));
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
    private static final Comparator<Pair<ImageElementI, Double>> ALPHABETICAL_MAP_COMPARATOR = Comparator.comparing(e -> StringUtils.trimToEmpty(e.getKey().getName()));

    public List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchImage(String rawSearchString, boolean displayAll, double minScore) {
        return searchImage(rawSearchString, displayAll, minScore, null);
    }

    public List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchImage(String rawSearchString) {
        return searchImage(rawSearchString, StringUtils.isBlank(rawSearchString), ConfigurationComponentUtils.SIMILARITY_CONTAINS / 2.0);
    }

    public List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchImage(String rawSearchString, boolean displayAll, double minScore, Predicate<? super ImageDictionary> imageDictionaryFilter) {
        long start = System.currentTimeMillis();
        List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> result = new ArrayList<>();
        String searchFull = StringUtils.stripToEmpty(rawSearchString).toLowerCase();
        AtomicInteger totalResultCount = new AtomicInteger(0);
        // TODO : convert to full stream implementation with map ?
        this.dictionaries
                .stream()
                .filter(d -> imageDictionaryFilter == null || imageDictionaryFilter.test(d))
                .sorted((d1, d2) ->
                        Boolean.compare(LCStateController.INSTANCE.getFavoriteImageDictionaries().contains(d2.getName()),
                                LCStateController.INSTANCE.getFavoriteImageDictionaries().contains(d1.getName()))
                ).forEach(imageDictionary -> {
                    List<ImageElementI> resultList = imageDictionary.getImages()
                            .parallelStream()
                            .map(e -> new Pair<>(e, displayAll ? 0.0 : getSimilarityScore(e.getKeywords(), searchFull)))
                            .sorted(displayAll ? ALPHABETICAL_MAP_COMPARATOR : SCORE_MAP_COMPARATOR)
                            .filter(e -> displayAll || e.getValue() >= minScore)
                            .map(Pair::getKey)
                            .collect(Collectors.toList());
                    if (LangUtils.isNotEmpty(resultList)) {
                        int pageSize = displayAll ? ALL_PAGE_SIZE : SEARCH_PAGE_SIZE;
                        final List<List<ImageElementI>> resultPages = IntStream.range(0, resultList.size())
                                .filter(i -> i % pageSize == 0)
                                .mapToObj(i -> resultList.subList(i, Math.min(i + pageSize, resultList.size())))
                                .collect(Collectors.toList());
                        result.add(new Pair<>(imageDictionary, resultPages));
                        LOGGER.info("Found {} result in {} dictionaries, result is {} pages", resultList.size(), imageDictionary.getName(), resultPages.size());
                        totalResultCount.addAndGet(resultList.size());
                    }
                });
        LOGGER.info("Search executed in image dictionaries in {} ms - found {} elements (in {} dictionaries, for \"{}\")",
                System.currentTimeMillis() - start,
                totalResultCount,
                result.size(),
                rawSearchString);
        return result;
    }

    public double getSimilarityScore(String[] keywords, String searchFull) {
        double score = 0.0;
        for (String source : keywords) {
            score += ConfigurationComponentUtils.getSimilarityScoreFor(searchFull, source, s -> org.lifecompanion.framework.utils.Pair.of(s, 1.0));
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
        // TODO : parallel loading for dictionaries ?
        long start = System.currentTimeMillis();
        File imageDictionariesRoot = new File(LCConstant.DEFAULT_IMAGE_DICTIONARIES);
        File[] potentialDictionaries = imageDictionariesRoot.listFiles();
        if (potentialDictionaries != null) {
            for (File potentialDictionary : potentialDictionaries) {
                if (potentialDictionary.isFile() && StringUtils.isEqualsIgnoreCase("json", FileNameUtils.getExtension(potentialDictionary))) {
                    loadImageDictionary(potentialDictionary);
                }
            }
        }
        userImagesDictionary = this.loadImageDictionary(new File(InstallationConfigurationController.INSTANCE.getUserDirectory()
                .getPath() + LCConstant.IMAGE_RESOURCES_DIR_NAME + LCConstant.DICTIONARY_NAME_USER_IMAGES));
        if (userImagesDictionary == null) {
            userImagesDictionary = new ImageDictionary();
            this.userImagesDictionary.setId(LCConstant.DICTIONARY_ID_USER_IMAGES);
            this.userImagesDictionary.setName(Translation.getText("image.dictionary.default.custom.dic"));
            this.userImagesDictionary.setCustomDictionary(true);
            this.dictionaries.add(userImagesDictionary);
        }
        configurationImageDictionary = this.loadImageDictionary(new File(InstallationConfigurationController.INSTANCE.getUserDirectory()
                .getPath() + LCConstant.IMAGE_RESOURCES_DIR_NAME + LCConstant.DICTIONARY_NAME_CONFIGURATION_IMAGES));
        if (configurationImageDictionary == null) {
            configurationImageDictionary = new ImageDictionary();
            this.userImagesDictionary.setId(LCConstant.DICTIONARY_ID_CONFIGURATION_IMAGES);
            this.configurationImageDictionary.setName(Translation.getText("image.dictionary.default.imported.dic"));
            this.configurationImageDictionary.setCustomDictionary(true);
            this.dictionaries.add(configurationImageDictionary);
        }
        LOGGER.info("Image dictionaries loading done in {} s", (System.currentTimeMillis() - start) / 1000.0);
    }

    public void removeDictionary(String id) {
        this.dictionaries.stream().filter(dic -> id.equals(dic.getId())).findAny().ifPresent(dictionaryToRemove -> {
            dictionaryToRemove.getImages().forEach(imageElement -> {
                allImages.remove(imageElement.getId());
            });
            this.dictionaries.remove(dictionaryToRemove);
        });
    }

    public ImageDictionary loadImageDictionary(File dictionaryFile) {
        if (dictionaryFile.exists()) {
            try (Reader is = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFile), StandardCharsets.UTF_8))) {
                ImageDictionary imageDictionary = JsonHelper.GSON.fromJson(is, ImageDictionary.class);
                imageDictionary.setId(FileNameUtils.getNameWithoutExtension(dictionaryFile));
                // If id check is present, check that the dictionary has the right ID / simple protection to avoid unauthorized dictionary copy
                if (imageDictionary.isEncodedDictionary()) {
                    if (!StringUtils.isEquals(imageDictionary.getId(), CryptUtils.xorDecrypt(imageDictionary.getIdCheck(), CHECKING_ID_PASS_UNSAFE))) {
                        LOGGER.warn("Could not load image dictionary from {} as the id check didn't match, dictionary will not be loaded", dictionaryFile);
                        return null;
                    }
                }
                imageDictionary.setImageDirectory(new File(dictionaryFile.getParentFile() + File.separator + FileNameUtils.getNameWithoutExtension(dictionaryFile)));
                imageDictionary.loaded(this.allImages);
                this.dictionaries.add(imageDictionary);
                LOGGER.info("Image dictionary {} loaded from {} ({} images)", imageDictionary.getName(), dictionaryFile, imageDictionary.getImages().size());
                return imageDictionary;
            } catch (Exception e) {
                LOGGER.error("Couldn't load dictionary from {}", dictionaryFile, e);
            }
        } else {
            LOGGER.warn("Found a folder in image dictionary folder, but didn't find its description file {}", dictionaryFile);
        }
        return null;
    }


    @Override
    public void lcStart() {
        this.loadDictionaries();
        startImageLoadingDebug();
    }

    private void startImageLoadingDebug() {
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.PROP_DEBUG_LOADED_IMAGE)) {
            LOGGER.info("Loaded images debug enabled");
            LCNamedThreadFactory.daemonThreadFactory("ImageLoadingDebug").newThread(() -> {
                while (true) {
                    Set<ImageElement> imageLoaded = new ArrayList<>(this.allImages.values()).stream()
                            .map(img -> (ImageElement) img)
                            .filter(img -> img.loadedImageProperty().get() != null)
                            .collect(Collectors.toSet());
                    LOGGER.info("Loaded image count : {}", imageLoaded.size());
                    if (AppModeController.INSTANCE.getEditModeContext().getConfiguration() == null && AppModeController.INSTANCE.getUseModeContext().getConfiguration() == null) {
                        imageLoaded
                                .forEach(img ->
                                        LOGGER.info("\t" + img.getName() + " = " + (img.getLoadingRequest()
                                                .entrySet()
                                                .stream()
                                                .filter(Map.Entry::getValue)
                                                .map(Map.Entry::getKey)
                                                .collect(Collectors.joining(" "))))
                                );
                    }
                    ThreadUtils.safeSleep(5_000);
                }
            }).start();
        }
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
