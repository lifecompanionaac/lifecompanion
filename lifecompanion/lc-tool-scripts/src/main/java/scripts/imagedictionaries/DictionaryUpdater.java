package scripts.imagedictionaries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DictionaryUpdater {
    private final static Logger LOGGER = LoggerFactory.getLogger(DictionaryUpdater.class);

    private static final Gson GSON = new GsonBuilder()//
            .create();

    public static void main(String[] args) throws IOException {
        File dir = new File("C:\\Users\\Mathieu\\Desktop\\TMP\\makaton\\update");
        File imgDir = new File("C:\\Users\\Mathieu\\Desktop\\TMP\\makaton\\2024-08-02-makaton-v1\\makaton");

        File previousFile = new File(dir + "/previous.json");
        File newFile = new File(dir + "/new.json");
        File patchedFile = new File(dir + "/patched.json");

        int total = 0, diffID = 0, sameID = 0;
        AtomicInteger noMatch = new AtomicInteger(), noMatchButFound = new AtomicInteger();

        Map<String, String> mapping = new HashMap<>();

        try (FileReader frp = new FileReader(previousFile, StandardCharsets.UTF_8)) {
            ImageDictionary previousDic = GSON.fromJson(frp, ImageDictionary.class);
            try (FileReader frn = new FileReader(newFile, StandardCharsets.UTF_8)) {
                ImageDictionary newDic = GSON.fromJson(frn, ImageDictionary.class);

                List<ImageElement> toAdd = new ArrayList<>();

                // For each previous image, try to find closest new image
                for (ImageElement previousImage : previousDic.images) {
                    total++;
                    // If new dic contains the same ID : ignore
                    if (newDic.images.stream().noneMatch(img -> StringUtils.isEquals(img.id, previousImage.id))) {
                        diffID++;
                        // System.out.println("FOR " + previousImage.name);
                        // In new dic, try to find the best matching keyword
                        newDic.images.stream()
                                .map(img -> {
                                    return Pair.of(img, similarity(img.keywords.get("fr"), previousImage.keywords.get("fr")));
                                })
                                .sorted((e1, e2) -> Integer.compare(e2.getRight(), e1.getRight()))
                                .filter(e -> e.getRight() > 0)
                                .peek(e -> {
                                    //System.out.println("\t" + e.getLeft().name + " = " + e.getRight());
                                })
                                .findFirst().ifPresentOrElse(e -> {
                                    mapping.put(previousImage.id, e.getLeft().id);
                                }, () -> {
                                    File prevFile = new File(imgDir + File.separator + previousImage.id + "." + previousDic.imageExtension);
                                    if (prevFile.exists()) {
                                        noMatchButFound.incrementAndGet();
                                        try {
                                            IOUtils.copyFiles(prevFile, new File(patchedFile.getParentFile() + "/recovered/" + prevFile.getName()));
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        toAdd.add(previousImage);
                                    } else {
                                        noMatch.incrementAndGet();
                                        LOGGER.info("Could not find any matching image for previous : {}", previousImage.name);
                                    }
                                });
                    } else {
                        sameID++;
                    }
                }

                // Update previous
                newDic.patched = mapping;
                newDic.images.addAll(toAdd);

                try (PrintWriter pw = new PrintWriter(patchedFile, StandardCharsets.UTF_8)) {
                    GSON.toJson(newDic, pw);
                }
            }
        }
        LOGGER.info("Result\n\tPrevious images={}\n\tSame ID = {}\n\tDifferent ID = {}\n\tNo match but recovered = {}\n\tNo match = {}", total, sameID, diffID, noMatchButFound, noMatch);
    }

    private static int similarity(String[] a1, String[] a2) {
        Set<String> container = new HashSet<>(List.of(a1));
        container.retainAll(List.of(a2));
        return container.size();
    }
}
