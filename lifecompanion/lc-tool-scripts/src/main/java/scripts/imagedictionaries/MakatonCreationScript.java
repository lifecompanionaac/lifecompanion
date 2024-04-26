/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.lifecompanion.framework.commons.utils.io.IOUtils.fileSha256HexToString;

/**
 * Process
 * 1) get whole DB
 * 2) clean each cat sheet in Excel : remove first columns/rows, keep only the three data columns + rows
 * 3) clean sheet names if needed (remove spaces before/after)
 * 4) manually add missing CAT if needed
 */
public class MakatonCreationScript {
    private final static Logger LOGGER = LoggerFactory.getLogger(MakatonCreationScript.class);

    private static final String[] CATEGORIES = Arrays.stream(
                    new String[]{"INF", "ATT", "SAN", "VET", "EPS", "ADM", "PRO", "ENV", "VDB", "BEB", "OUQ", "MUS", "DIV", "FAM", "MAI", "GRA", "SCI", "VEH", "ANI", "REP", "ALI", "SCO"})
            .sorted()
            .toArray(String[]::new);

    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>() {{
        put("ADM", List.of("administration", "banque", "justice", "commerce"));
        put("ALI", List.of("alimentation"));
        put("ANI", List.of("animaux"));
        put("ATT", List.of("attitudes", "comportements", "émotions"));
        put("BEB", List.of("petite enfance"));
        put("DIV", List.of("divertissement", "culture"));
        put("ENV", List.of("environnement", "nature", "météo", "géographie", "espace"));
        put("EPS", List.of("sports", "activités motrices"));
        put("FAM", List.of("relations", "famille"));
        put("GRAM", List.of("grammaire", "lexique généraliste"));
        put("INF", List.of("informatique", "numérique", "communication"));
        put("MAI", List.of("maison", "bâtiments"));
        put("MUS", List.of("musique", "danse"));
        put("OUQ", List.of("lieux", "temps"));
        put("PRO", List.of("professions", "métiers"));
        put("SAN", List.of("santé"));
        put("SCI", List.of("sciences", "mathématiques", "maths"));
        put("SCO", List.of("vie scolaire", "école", "jeux", "loisirs créatifs"));
        put("VDB", List.of("vocabulaire de base"));
        put("VEH", List.of("véhicules", "sécurité routière"));
        put("VET", List.of("vêtements", "accessoires", "linge"));
    }};

    private static final boolean DISPLAY_MISSING_FILES = true;

    public static void main(String[] args) throws Exception {
        File outputDir = new File("C:\\Users\\Mathieu\\Desktop\\temp\\makaton\\out");
        File inputDir = new File("C:\\Users\\Mathieu\\Desktop\\temp\\makaton\\in");

        List<MakatonPic> makatonPicList = new ArrayList<>();

        // Read datas
        try (FileInputStream fis = new FileInputStream(inputDir + "/6900 pictogrammes Makaton - Index.xlsx")) {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            for (String category : CATEGORIES) {
                XSSFSheet sheet = wb.getSheet(category);
                if (sheet != null) {
                    for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                        XSSFRow row = sheet.getRow(rowIndex);
                        if (row != null) {
                            XSSFCell firstCell = row.getCell(0);
                            if (firstCell != null) {
                                String name = getStringCellValue(firstCell);
                                Set<String> categories = Arrays.stream(getStringCellValue(row.getCell(1)).split("\\s+")).collect(Collectors.toSet());
                                String fileName = getStringCellValue(row.getCell(2));
                                MakatonPic pic = new MakatonPic(category, name, categories, fileName);
                                makatonPicList.add(pic);
                            }
                        }
                    }
                } else {
                    LOGGER.warn("Can't find category sheet for {}", category);
                }
            }
        }

        // Check datas
        long diffFiles = makatonPicList.stream().map(MakatonPic::filename).distinct().count();
        long diffCats = makatonPicList.stream().flatMap(p -> p.categories.stream()).distinct().count();
        LOGGER.info("Found\n\t{} pict data\n\t{} different files\n\t{} different categories", makatonPicList.size(), diffFiles, diffCats);

        // Try to associate files
        Map<File, List<MakatonPic>> picPerFile = new HashMap<>();
        int missingFilesCount = 0;
        for (MakatonPic makatonPic : makatonPicList) {
            List<File> foundFiles = makatonPic.findFiles(inputDir);
            if (foundFiles.isEmpty()) {
                missingFilesCount++;
                if (DISPLAY_MISSING_FILES)
                    LOGGER.error("No file for {}", makatonPic);
            } else {
                for (File foundFile : foundFiles) {
                    picPerFile.computeIfAbsent(foundFile, f -> new ArrayList<>()).add(makatonPic);
                }
            }
        }
        LOGGER.info("{} missing files", missingFilesCount);

        // Associate each file with its keywords
        Map<File, Set<String>> keywords = new HashMap<>();
        picPerFile.forEach((file, pictListForFile) -> {
            if (keywords.size() < 500_000)//LIMIT
            {
                Set<String> keywordForPics = pictListForFile.stream().map(MakatonPic::name).map(StringUtils::trimToEmpty).collect(Collectors.toSet());
                //                pictListForFile.stream().map(MakatonPic::category).distinct().forEach(cat -> {
                //                    List<String> otherKeywords = CATEGORY_KEYWORDS.get(cat);
                //                    if (otherKeywords != null) keywordForPics.addAll(otherKeywords);
                //                });
                keywords.put(file, keywordForPics);
            }
        });

        // Group file keywords per sha256
        Map<String, List<Map.Entry<File, Set<String>>>> perHash = new HashMap<>();
        for (Map.Entry<File, Set<String>> fileSetEntry : keywords.entrySet()) {
            String sha256File = fileSha256HexToString(fileSetEntry.getKey());
            perHash.computeIfAbsent(sha256File, f -> new ArrayList<>()).add(fileSetEntry);
        }

        // Copy in a unique directory + keep all the associated keywords
        File uniqueDir = new File(outputDir + "/ALL");
        uniqueDir.mkdirs();
        Map<File, Set<String>> fileAndKeywords = new HashMap<>();
        for (Map.Entry<String, List<Map.Entry<File, Set<String>>>> hashAndFiles : perHash.entrySet()) {
            // Copy the first file in the list
            File key = hashAndFiles.getValue().get(0).getKey();
            File targetFile = new File(uniqueDir + "/" + hashAndFiles.getKey() + ".jpg");
            if (!targetFile.exists()) {
                try (FileInputStream fis = new FileInputStream(key)) {
                    try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                        IOUtils.copy(fis, fos, 4096);
                    }
                }
            }
            // Merge keywords
            fileAndKeywords.put(targetFile, hashAndFiles.getValue().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toSet()));
        }

        // Unique file count
        LOGGER.info("Found {} unique file (over {} rows)", perHash.size(), keywords.size());

        DicInfo dicInfo = new DicInfo(
                new ImageDictionary("Makaton", "image.dictionary.description.makaton", "image.dictionary.author.makaton",
                        "png", "https://www.makaton.fr/", false),
                uniqueDir, "makaton", "jpg", false, true, true, true, false, false, fileAndKeywords);
        ImageDictionariesCreationScript.generateImageDictionary(dicInfo);
    }

    private static String getStringCellValue(XSSFCell cell) {
        try {
            return cell.getStringCellValue();
        } catch (Exception e) {
            return cell != null ? cell.getRawValue() : null;
        }
    }

    private record MakatonPic(String category, String name, Set<String> categories, String filename) {
        public List<File> findFiles(File directory) {
            List<File> foundFiles = new ArrayList<>();
            List<String> cats = new ArrayList<>(List.of(category));
            cats.addAll(categories);
            for (String cat : cats) {
                File imageFile = new File(directory + "/" + cat + " pictos/" + filename);
                if (imageFile.isFile() && imageFile.exists()) {
                    foundFiles.add(imageFile);
                }
            }
            return foundFiles;
        }

        @Override
        public String toString() {
            return "MakatonPic{" +
                    "category='" + category + '\'' +
                    ", name='" + name + '\'' +
                    ", categories=" + categories +
                    ", filename='" + filename + '\'' +
                    '}';
        }
    }


}
