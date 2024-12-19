package com.example.encryption2;

import com.example.encryption2.encryption.Cryption;
import com.example.encryption2.encryption.CryptionREC;
import com.example.encryption2.encryption.TreeCryptographer;
import com.example.encryption2.progressbar.ProgressBarController;
import com.example.encryption2.util.DirectoryOP;
import com.example.encryption2.util.PrintLogger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;

public class Controller {

    @FXML
    public TextArea textArea;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField textField;
    @FXML
    private TextField textFieldLogger;
    static Stage dialogStage;
    Cryption cryption = new Cryption();
    PrintLogger printLogger;
    ExecutorService executorService;

    public enum PHASE {
        INPUTFILEPATH, OUTPUTFILEPATH, KEY
    }

    public enum MODE {
        COMMAND, CRYPTION, DECRYPTION
    }

    public enum COMMAND {
        man, manD, auto, autoD
    }

    PHASE phase = PHASE.INPUTFILEPATH;
    MODE mode = MODE.COMMAND;
    COMMAND command;

    public void initialize() {
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleEnter();
            }
        });
        printLogger = new PrintLogger(textArea);
        textArea.setStyle("-fx-control-inner-background: #000000;");
        borderPane.setStyle("-fx-control-inner-background: #000000;");
        printLogger.println("Encryption Application v:1.0");
        printLogger.print("Insert command: ");
        textFieldLogger.setText(System.getProperty("user.name") + ">>");
    }

    @FXML
    void handleEnter() {
        String input = textField.getText();
        Path ofInput;
        if (mode.equals(MODE.COMMAND)) {
            printLogger.printlnClean(input);
            command(input);
        } else if (mode.equals(MODE.CRYPTION)) {
            if (phase.equals(PHASE.INPUTFILEPATH)) {
                ofInput = DirectoryOP.autoFill(input);
                printLogger.printlnClean(ofInput.toString());
                cryption.setInput(ofInput);
                phase = PHASE.OUTPUTFILEPATH;
                printLogger.print("Insert output folder: ");
            } else if (phase.equals(PHASE.OUTPUTFILEPATH)) {
                ofInput = DirectoryOP.autoFill(input);
                printLogger.printlnClean(ofInput.toString());
                cryption.setOutput(ofInput);
                phase = PHASE.KEY;
                printLogger.print("Insert the key: ");
            } else if (phase.equals(PHASE.KEY)) {
                printLogger.printlnClean(input);
                cryption.setEncryptionKey(input);
                if (command.equals(COMMAND.man)) {
                    manMode();
                } else if (command.equals(COMMAND.auto)) {
                    try {
                        autoMode();
                    } catch (IOException e) {
                        printLogger.printBackSpace();
                        printLogger.println("Unable to create the destination directory");
                        printLogger.printlnClean("------------------------");
                    }
                }
            }
        } else if (mode.equals(MODE.DECRYPTION)) {
            if (phase.equals(PHASE.INPUTFILEPATH)) {
                ofInput = DirectoryOP.autoFill(input);
                printLogger.printlnClean(ofInput.toString());
                cryption.setInput(ofInput);
                phase = PHASE.OUTPUTFILEPATH;
                printLogger.print("Insert output folder: ");
            } else if (phase.equals(PHASE.OUTPUTFILEPATH)) {
                ofInput = DirectoryOP.autoFill(input);
                printLogger.printlnClean(ofInput.toString());
                cryption.setOutput(ofInput);
                phase = PHASE.KEY;
                printLogger.print("Insert the key for the decryption: ");
            } else if (phase.equals(PHASE.KEY)) {
                printLogger.printlnClean(input);
                cryption.setEncryptionKey(input);
                if (command.equals(COMMAND.manD)) {
                    manDMode();
                } else if (command.equals(COMMAND.autoD)) {
                    try {
                        autoDMode();
                    } catch (IOException e) {
                        printLogger.printBackSpace();
                        printLogger.println("Unable to create the destination directory");
                        printLogger.printlnClean("------------------------");
                    }
                }
            }
        }
        textField.clear();
    }

    private void command(String command) {
        switch (command) {
            case "auto" -> autoModeSetup();
            case "autoD" -> autoDModeSetup();
            case "man" -> manModeSetup();
            case "manD" -> manDModeSetup();
            case "clear" -> clear();
            case "exit" -> exit();
            default -> {
                printLogger.println("Command nonexistent");
                printLogger.print("Insert command: ");
            }
        }
    }

    private void autoMode() throws IOException {
        textArea.clear();

        int poolSize = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(poolSize);

        ProgressBarController progressBarController = showProgressBar();

        if (DirectoryOP.isDirectoryNotEmpty(cryption.getInput().toFile())) {
            Path destinationFolderPath = DirectoryOP.createDestinationDirectory(cryption.getOutput(), MODE.CRYPTION); //todo da modificare
            TreeCryptographer treeCryptographer = new TreeCryptographer(mode, cryption, printLogger, progressBarController, dialogStage, executorService, destinationFolderPath);
            CryptionREC cryptionREC = new CryptionREC(treeCryptographer, cryption.getInput().toAbsolutePath(), executorService, printLogger);
            cryptionREC.launchCryptography(MODE.CRYPTION);
        } else {
            printLogger.printBackSpace();
            printLogger.println("The directory provided is empty");
        }
        mode = MODE.COMMAND;
        phase = PHASE.INPUTFILEPATH;
    }

    private ProgressBarController showProgressBar() {
        ProgressBarController controller = new ProgressBarController();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Controller.class.getResource("progress-bar-view.fxml"));
            AnchorPane view = loader.load();
            controller = loader.getController();

            dialogStage = new Stage();
            dialogStage.setTitle("Progress Bar");
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            dialogStage.show();
        } catch (IOException e) {
            printLogger.println("Something went wrong regards the Progress Bar");
        }
        return controller;
    }

    private void autoModeSetup() {
        mode = MODE.CRYPTION;
        phase = PHASE.INPUTFILEPATH;
        command = COMMAND.auto;
        printLogger.print("Insert input folder: ");
    }

    private void autoDMode() throws IOException {
        textArea.clear();

        int poolSize = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(poolSize);

        ProgressBarController progressBarController = showProgressBar();

        if (DirectoryOP.isDirectoryNotEmpty(cryption.getInput().toFile())) {
            Path destinationFolderPath = DirectoryOP.createDestinationDirectory(cryption.getOutput(), MODE.DECRYPTION); //todo da modificare
            TreeCryptographer treeCryptographer = new TreeCryptographer(mode, cryption, printLogger, progressBarController, dialogStage, executorService, destinationFolderPath);
            CryptionREC cryptionREC = new CryptionREC(treeCryptographer, cryption.getInput().toAbsolutePath(), executorService, printLogger);
            cryptionREC.launchCryptography(MODE.CRYPTION);
        } else {
            printLogger.printBackSpace();
            printLogger.println("The directory provided is empty");
        }
        mode = MODE.COMMAND;
        phase = PHASE.INPUTFILEPATH;
    }

    private void autoDModeSetup() {
        mode = MODE.DECRYPTION;
        phase = PHASE.INPUTFILEPATH;
        command = COMMAND.autoD;
        printLogger.print("Insert input folder: ");
    }

    private void manDMode() {
        textArea.clear();
        printLogger.println("InputFilePath: " + cryption.getInput());
        printLogger.println("DecryptedFilePath: " + cryption.getOutput());
        printLogger.printBackSpace();
        try {
            printLogger.println("Starting Decryption");
            cryption.decrypt();
            printLogger.println("Decryption finished successful");
            printLogger.printBackSpace();
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            printLogger.printBackSpace();
            printLogger.println("Something went wrong in the Decryption: " + e);
            printLogger.printlnClean("------------------------");
        }
        mode = MODE.COMMAND;
        phase = PHASE.INPUTFILEPATH;
        printLogger.print("Insert command: ");
    }

    private void manDModeSetup() {
        mode = MODE.DECRYPTION;
        command = COMMAND.manD;
        printLogger.print("Insert Path of the file to be decrypted: ");
    }

    private void manModeSetup() {
        mode = MODE.CRYPTION;
        command = COMMAND.man;
        printLogger.print("Insert Path of the file to be crypted: ");
    }

    private void manMode() {
        textArea.clear();
        printLogger.println("InputFilePath: " + cryption.getInput());
        printLogger.println("EncryptedFilePath: " + cryption.getOutput());
        printLogger.printBackSpace();
        try {
            printLogger.println("Starting Encryption");
            cryption.encrypt();
            printLogger.println("Encryption finished successful");
            printLogger.printBackSpace();
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            printLogger.printBackSpace();
            printLogger.println("Something went wrong in the encryption: " + e);
            printLogger.printlnClean("------------------------");
        }
        mode = MODE.COMMAND;
        phase = PHASE.INPUTFILEPATH;
        printLogger.print("Insert command: ");
    }

    private void clear() {
        textArea.clear();
        printLogger.print("Insert command: ");
    }

    private void exit() {
        Platform.exit();
    }

}