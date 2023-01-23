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

package org.lifecompanion.framework.commons.utils.io;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.LongConsumer;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class IOUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    private static final int BUFFER_SIZE = 2048;

    private IOUtils() {
    }

    /**
     * Zip the full directory and sub directory into a zip file
     *
     * @param zipPath   the path to the wanted zip file
     * @param directory the directory to zip inside the created zip
     * @param comment   the comment to put for the zip
     * @throws IOException if a problem happen in zip creation
     */
    public static void zipInto(final File zipPath, final File directory, final String comment) throws IOException {
        String directoryPath = directory.getPath();
        List<File> files = new ArrayList<>();
        listFile(directory, files);
        HashMap<String, InputStream> streams = new HashMap<>(files.size());
        for (File file : files) {
            String filePath = file.getPath();
            String relativized = getRelativePath(filePath, directoryPath);
            streams.put(relativized.toString(), new FileInputStream(file));
        }
        zipInto(streams, zipPath, comment);
    }

    /**
     * Zip all given input stream into a single zip file
     *
     * @param inputs  each input stream to add in the zip file (path<->inputstream)
     * @param zipPath the zip file path
     * @param comment zip file comment
     * @throws IOException if zip fail
     */
    public static void zipInto(final Map<String, InputStream> inputs, final File zipPath, final String comment) throws IOException {
        IOUtils.createParentDirectoryIfNeeded(zipPath);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            zos.setComment(comment);
            Set<String> paths = inputs.keySet();
            for (String path : paths) {
                ZipEntry entry = new ZipEntry(path);
                zos.putNextEntry(entry);
                try (InputStream is = inputs.get(path)) {
                    copyStream(is, zos);
                }
                zos.closeEntry();
            }
        }
    }

    /**
     * @param zipPath the path to the zip file
     * @return the comment of the zip, or null if the zip doesn't have any comment
     * @throws IOException if a problem happen
     */
    public static String getZipComment(final File zipPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipPath)) {
            return zipFile.getComment();
        }
    }

    /**
     * Get the relative path from one file to another, specifying the directory separator.
     * If one of the provided resources does not exist, it is assumed to be a file unless it ends with '/' or
     * '\'.<br>
     * Copied from <a href="http://stackoverflow.com/questions/204784/how-to-construct-a-relative-path-in-java-from-two-absolute-paths-or-urls">StackOverflow</a> and modified.<br>
     * This is use to avoid nio use and to provide a full Android compatibility.
     *
     * @param targetPath targetPath is calculated to this file
     * @param basePath   basePath is calculated from this file
     * @return the relative path
     */
    public static String getRelativePath(final String targetPath, final String basePath) {
        final String pathSeparator = "/";

        // Normalize the paths
        String normalizedTargetPath = FilenameUtils.separatorsToUnix(FilenameUtils.normalizeNoEndSeparator(targetPath));
        String normalizedBasePath = FilenameUtils.separatorsToUnix(FilenameUtils.normalizeNoEndSeparator(basePath));

        String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
        String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

        // First get all the common elements. Store them as a string,
        // and also count how many of them there are.
        StringBuffer common = new StringBuffer();

        int commonIndex = 0;
        while (commonIndex < target.length && commonIndex < base.length && target[commonIndex].equals(base[commonIndex])) {
            common.append(target[commonIndex] + pathSeparator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            // No single common path element. This most
            // likely indicates differing drive letters, like C: and D:.
            // These paths cannot be relativized.
            throw new PathResolutionException("No common path element found for '" + normalizedTargetPath + "' and '" + normalizedBasePath + "'");
        }

        // The number of directories we have to backtrack depends on whether the base is a file or a dir
        // For example, the relative path from
        //
        // /foo/bar/baz/gg/ff to /foo/bar/baz
        //
        // ".." if ff is a file
        // "../.." if ff is a directory
        //
        // The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
        // the resource referred to by this path may not actually exist, but it's the best I can do
        boolean baseIsFile = true;

        File baseResource = new File(normalizedBasePath);

        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();
        } else if (basePath.endsWith(pathSeparator)) {
            baseIsFile = false;
        }

        StringBuffer relative = new StringBuffer();
        if (base.length != commonIndex) {
            int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;
            for (int i = 0; i < numDirsUp; i++) {
                relative.append(".." + pathSeparator);
            }
        }
        relative.append(normalizedTargetPath.substring(common.length()));
        return relative.toString();
    }

    static class PathResolutionException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        PathResolutionException(final String msg) {
            super(msg);
        }
    }

    /**
     * Unzip the full content of a zip into a given directory
     *
     * @param zipPath      the path to the zip file
     * @param directory    the directory where unzipped file must be placed
     * @param excludeRegex a regex defined to exclude zip entry, can be null to unzip all
     * @throws IOException if a problem happen in unzip
     */
    public static void unzipIntoCounting(final File zipPath, final File directory, final String excludeRegex, LongConsumer counter) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry nextEntry = zis.getNextEntry();
            while (nextEntry != null) {
                String name = nextEntry.getName();
                //Check valid
                if (excludeRegex == null || !name.matches(excludeRegex)) {
                    //Create the path and the parent directory
                    File filePath = new File(directory.getPath() + File.separator + name);
                    File parent = filePath.getParentFile();
                    parent.mkdirs();
                    if (nextEntry.isDirectory()) {
                        filePath.mkdir();
                    } else {
                        //Extract
                        try (FileOutputStream fos = new FileOutputStream(filePath)) {
                            copyStreamCounting(zis, fos, counter);
                        }
                    }
                }
                //Next
                zis.closeEntry();
                nextEntry = zis.getNextEntry();
            }
        }
    }

    public static void unzipInto(final File zipPath, final File directory, final String excludeRegex) throws IOException {
        unzipIntoCounting(zipPath, directory, excludeRegex, null);
    }

    /**
     * Explore the directory and its subdirectory.<br>
     * Add all the file to the given list
     *
     * @param directory the directory to explore
     * @param files     list where file are added
     */
    public static void listFile(final File directory, final List<File> files) {
        File[] children = directory.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    listFile(child, files);
                } else {
                    files.add(child);
                }
            }
        }
    }

    /**
     * Copy a directory and its children to another directory.<br>
     * <strong>This replace file in destination if a file has the same path</strong>
     *
     * @param source the directory to copy
     * @param target target directory
     * @return the list of copied files
     * @throws Exception if copy fails
     */
    public static List<File> copyDirectory(final File source, final File target) throws Exception {
        List<File> fileList = new ArrayList<>();
        listFile(source, fileList);
        //For each file, copy
        for (File sourceFile : fileList) {
            //Create file path
            String relativePath = getRelativePath(sourceFile.getAbsolutePath(), source.getAbsolutePath());
            File targetFile = new File(target.getAbsolutePath() + File.separator + relativePath);
            targetFile.getParentFile().mkdirs();
            //Copy
            copyFiles(sourceFile, targetFile);
        }
        return fileList;
    }

    /**
     * Copy a file to another
     *
     * @param sourceFile the source file
     * @param targetFile target file
     * @throws IOException if copy fail
     */
    public static void copyFiles(final File sourceFile, final File targetFile) throws IOException {
        if (targetFile.getParentFile() != null) {
            targetFile.getParentFile().mkdirs();
        }
        try (FileInputStream fis = new FileInputStream(sourceFile)) {
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                copyStream(fis, fos);
            }
        }
    }

    /**
     * Make a full copy of the input stream into the output stream
     *
     * @param in stream to copy
     * @param os stream to write in
     * @throws IOException if a problem happen in copy
     */
    public static void copyStream(final InputStream in, final OutputStream os) throws IOException {
        copyStreamCounting(in, os, null);
    }

    public static void copyStreamCounting(final InputStream in, final OutputStream os, LongConsumer counter) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        long counterUpdates = 0, addedSinceLastUpdate = 0;
        while ((len = in.read(buffer)) > -1) {
            os.write(buffer, 0, len);
            addedSinceLastUpdate += len;
            if (counter != null && counterUpdates++ % 1000 == 0) {
                counter.accept(addedSinceLastUpdate);
                addedSinceLastUpdate = 0;
            }
        }
        if (counter != null) {
            counter.accept(addedSinceLastUpdate);
        }
    }

    /**
     * Delete a directory and its children.<br>
     * It recursively delete the given directory, its sub directories, and its files
     *
     * @param target the directory or file where a recursive delete is needed
     */
    public static void deleteDirectoryAndChildren(final File target) {
        if (target.isDirectory()) {
            File[] children = target.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectoryAndChildren(child);
                }
            }
            target.delete();
        } else if (target.isFile()) {
            target.delete();
        }
    }

    /**
     * @param file file that contains string content in UTF-8
     * @return the full file content, whith \n as new line char
     */
    public static String getFileContent(final File file) {
        try (Scanner scan = new Scanner(file, "UTF-8")) {
            StringBuilder sb = new StringBuilder();
            while (scan.hasNextLine()) {
                sb.append(scan.nextLine());
                if (scan.hasNextLine()) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        } catch (IOException e) {
            LOGGER.warn("Full content of the given file " + file + " can't be read ", e);
            return "";
        }
    }

    public static String readStreamLines(final InputStream is, final String encoding) {
        try (Scanner scan = new Scanner(is, encoding)) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            while (scan.hasNextLine()) {
                sb.append(first ? "\n" : "");
                sb.append(scan.nextLine());
                first = false;
            }
            return sb.toString();
        }
    }

    public static String readFileLines(final File file, final String encoding) throws FileNotFoundException, IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return readStreamLines(fis, encoding);
        }
    }

    public static void writeToFile(final File file, final String content) {
        writeToFile(file, content, StandardCharsets.UTF_8.name());
    }

    /**
     * Write a string to a file
     *
     * @param file    the file where string is written
     * @param content the content to write in the file
     */
    public static void writeToFile(final File file, final String content, String encoding) {
        try (PrintWriter pw = new PrintWriter(file, encoding)) {
            pw.print(content);
        } catch (IOException e) {
            LOGGER.warn("Couldn't write the given content to {}", file, e);
        }
    }

    public static String fileSha256HexToString(File path) throws IOException {
        try (InputStream fis = new BufferedInputStream(new FileInputStream(path))) {
            return DigestUtils.sha256Hex(fis);
        }
    }

    public static void createParentDirectoryIfNeeded(File path) {
        if (path != null) {
            File parentFile = path.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
        }
    }
}
