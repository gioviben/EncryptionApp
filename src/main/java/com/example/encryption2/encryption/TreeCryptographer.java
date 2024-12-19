package com.example.encryption2.encryption;

import com.example.encryption2.Controller;
import com.example.encryption2.progressbar.ProgressBarController;
import com.example.encryption2.util.DirectoryOP;
import com.example.encryption2.util.PrintLogger;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class TreeCryptographer extends SimpleFileVisitor<Path> {

    Controller.MODE mode;
    Cryption cryption;
    PrintLogger printLogger;
    boolean partialCryptography;
    List<String> fileNotCryptographed;
    File currentFile;
    File currentDir;
    ExecutorService executorService;
    Path currentDirectoryPath;
    ProgressBarController progressBarController;
    Stage dialogStage;
    Path rootDestinationFolderPath;
    Path inputDirectoryName;
    Path inputDirectoryPath;

    public TreeCryptographer(Controller.MODE mode, Cryption cryption, PrintLogger printLogger, ProgressBarController progressBarController, Stage dialogStage, ExecutorService executorService, Path rootDestinationFolderPath) {
        this.mode = mode;
        this.cryption = cryption;
        this.printLogger = printLogger;
        this.currentDirectoryPath = rootDestinationFolderPath;
        this.progressBarController = progressBarController;
        this.dialogStage = dialogStage;
        this.executorService = executorService;
        fileNotCryptographed = new ArrayList<>();
        partialCryptography = false;
        this.rootDestinationFolderPath = rootDestinationFolderPath;
        this.inputDirectoryName = cryption.getInput().getFileName();
        this.inputDirectoryPath = cryption.getInput().toAbsolutePath();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        currentFile = file.toFile();
        cryption.setInput(file);
        currentDirectoryPath = DirectoryOP.buildRightDirPath_File(file.toString(), rootDestinationFolderPath, inputDirectoryName, inputDirectoryPath);
        try {
            Path newFileNamePath = DirectoryOP.createFileName(currentFile, currentDirectoryPath, mode);
            cryption.setOutput(newFileNamePath);
            if (mode.equals(Controller.MODE.CRYPTION)) {
                Platform.runLater(() -> printLogger.printlnClean("Starting Encryption of the file " + "\"" + currentFile.getName() + "\""));
                cryption.encrypt();
                //Platform.runLater(() -> printLogger.printlnClean("\t-->Encryption of the file " + "\"" +
                // currentFile.getName() + "\" " + "finished " + "successful"));
            } else if (mode.equals(Controller.MODE.DECRYPTION)) {
                Platform.runLater(() ->printLogger.printlnClean("Starting Decryption of the file " + "\"" + currentFile.getName() + "\""));
                cryption.decrypt();
                //Platform.runLater(() ->printLogger.printlnClean("\t-->Decryption of the file " + "\"" + currentFile
                // .getName() + "\" " + "finished " + "successful"));
            }
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            Platform.runLater(() -> {
                printLogger.printBackSpace();
                printLogger.println("\tERROR-->Something went wrong in the encryption of the file: " + currentFile.getName() + " " + e);
            });
            partialCryptography = true;
            fileNotCryptographed.add(currentFile.getAbsolutePath());
            printLogger.printBackSpace();
        } catch (OutOfMemoryError e) {
            Platform.runLater(() -> printLogger.println("\tWARNING-->The file " + "\"" + currentFile.getName() + "\"" + " is too big for the encryption"));
            partialCryptography = true;
            fileNotCryptographed.add(currentFile.getAbsolutePath());
        } catch (IllegalArgumentException e) { //todo modificare system out
            Platform.runLater(() -> printLogger.println("The file " + currentFile + " in " + currentDirectoryPath + " doesn't end " + "with " + "\"_Crypted\""));
            partialCryptography = true;
            fileNotCryptographed.add(currentFile.getAbsolutePath());
        } finally {

            Platform.runLater(() -> {
                progressBarController.increaseProgressBar();
                progressBarController.refreshCount();
                if (progressBarController.getCurrentStatus() == progressBarController.getFinalStatus()) {
                    dialogStage.close();
                    if (partialCryptography) {
                        printLogger.printBackSpace();
                        printLogger.println("Partial success. The following file/s could not be crypted: ");
                        for (String files : fileNotCryptographed) {
                            printLogger.printlnClean(files);
                        }
                        printLogger.printBackSpace();
                    } else {
                        printLogger.printBackSpace();
                        printLogger.println("Encryption finished successful");
                        printLogger.printBackSpace();
                    }
                    printLogger.print("Insert command: ");
                }
            });
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        currentDir = dir.toFile();
        if (dir.compareTo(inputDirectoryPath) != 0) {
            StringBuilder dirBuilder = new StringBuilder(dir.toString());
            String subPath = dirBuilder.substring(0, dirBuilder.lastIndexOf("\\"));
            currentDirectoryPath = DirectoryOP.buildRightDirPath_Dir(subPath + "\\", rootDestinationFolderPath, inputDirectoryName, inputDirectoryPath);
        }
        DirectoryOP.createDirectoryCopy(currentDir.getName(), currentDirectoryPath);
        return FileVisitResult.CONTINUE;
    }

    public ProgressBarController getProgressBarController() {
        return progressBarController;
    }
}
