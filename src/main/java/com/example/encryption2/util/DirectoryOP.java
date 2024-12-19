package com.example.encryption2.util;

import com.example.encryption2.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.random.RandomGenerator;

public class DirectoryOP {

    public static Path createDestinationDirectory(Path directoryPath, Controller.MODE mode) throws IOException {
        String destinationFolderPath = "";
        RandomGenerator randomGenerator = RandomGenerator.getDefault();
        Path saveDirectoryPath = Path.of(directoryPath.toString());
        while (Files.exists(directoryPath, LinkOption.NOFOLLOW_LINKS)) {
            if (mode.equals(Controller.MODE.CRYPTION)) {
                destinationFolderPath = saveDirectoryPath + System.getProperty("file.separator") + "CryptedFiles" + randomGenerator.nextInt(100);
            } else if (mode.equals(Controller.MODE.DECRYPTION)) {
                destinationFolderPath = saveDirectoryPath + System.getProperty("file.separator") + "DecryptedFiles" + randomGenerator.nextInt(100);
            }
            directoryPath = Paths.get(destinationFolderPath);
        }

        Files.createDirectory(directoryPath);
        return Path.of(destinationFolderPath);
    }

    public static Path createDirectoryCopy(String directoryName, Path directoryPath) throws IOException {
        String destinationFolderPath = directoryPath + System.getProperty("file.separator") + directoryName;
        Path dir = Paths.get(destinationFolderPath);
        Files.createDirectory(dir);
        return dir;
    }

    public static Path createFileName(File file, Path directoryPath, Controller.MODE mode) {
        StringBuilder newFileName = new StringBuilder();
        int pointIndex, len, sum;
        if (mode.equals(Controller.MODE.CRYPTION)) {
            newFileName = new StringBuilder(directoryPath.toString() + System.getProperty("file.separator") + file.getName());
            StringBuilder newFileNameBuilder = new StringBuilder(newFileName);
            len = newFileNameBuilder.substring(0, newFileNameBuilder.lastIndexOf("\\")).toString().length();
            pointIndex = file.getName().lastIndexOf(".");
            if (pointIndex >= 0) {
                sum = len + pointIndex;
                newFileName.replace(sum + 1, sum + 2, "_CRYPTED_");
            } else {
                newFileName.append("_CRYPTED");
            }
        } else if (mode.equals(Controller.MODE.DECRYPTION)) {
            newFileName = new StringBuilder(directoryPath.toString() + System.getProperty("file.separator") + file.getName());
            pointIndex = newFileName.lastIndexOf("_CRYPTED_");
            if (pointIndex > 0) {
                newFileName.replace(pointIndex, pointIndex + 9, ".");
            } else if (pointIndex < 0) {
                pointIndex = newFileName.lastIndexOf("_CRYPTED");
                newFileName.replace(pointIndex, pointIndex + 8, "");
            } else {
                throw new IllegalArgumentException();
            }
        }
        return Path.of(newFileName.toString());
    }

    public static boolean isDirectoryNotEmpty(File currentFile) {
        File[] fileArray = currentFile.listFiles();
        return Optional.ofNullable(fileArray).isPresent();
    }

    public static Path buildRightDirPath_File(String file, Path rootDestinationFolderPath, Path inputDirectoryName, Path inputDirectoryPath) {
        StringBuilder fileStringBuilder = new StringBuilder(file);

        int endIndex = inputDirectoryPath.toString().lastIndexOf("\\" + inputDirectoryName.toString());
        fileStringBuilder.replace(0, endIndex, rootDestinationFolderPath.toString());
        return Path.of(fileStringBuilder.substring(0, fileStringBuilder.lastIndexOf("\\")));
    }

    public static Path buildRightDirPath_Dir(String file, Path rootDestinationFolderPath, Path inputDirectoryName, Path inputDirectoryPath) {
        StringBuilder fileStringBuilder = new StringBuilder(file);

        int endIndex = inputDirectoryPath.toString().lastIndexOf("\\" + inputDirectoryName.toString());
        fileStringBuilder.replace(0, endIndex, rootDestinationFolderPath.toString());
        return Path.of(fileStringBuilder.toString());
    }

    public static int countTOTfileTree(Path startPath) throws IOException {
        FileCounter fileCounter = new FileCounter();
        Files.walkFileTree(startPath, new HashSet<>(), Integer.MAX_VALUE, fileCounter);
        return fileCounter.getFileCount();
    }

    public static Path autoFill(String input) {
        StringBuilder inputBuilder = new StringBuilder(input);
        if (inputBuilder.indexOf("Desktop") == 0) {
            String DesktopPath = System.getProperty("user.home") + System.getProperty("file.separator");
            inputBuilder.insert(0, DesktopPath);
        }
        return Path.of(inputBuilder.toString());
    }

    static class FileCounter extends SimpleFileVisitor<Path> {
        private int fileCount = 0;
        private int directoryCount = 0;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            fileCount++;
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            directoryCount++;
            return FileVisitResult.CONTINUE;
        }

        public int getFileCount() {
            return fileCount;
        }

        public int getDirectoryCount() {
            return directoryCount;
        }
    }
}
