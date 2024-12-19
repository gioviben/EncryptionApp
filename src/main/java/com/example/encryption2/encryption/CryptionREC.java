package com.example.encryption2.encryption;

import com.example.encryption2.Controller;
import com.example.encryption2.util.PrintLogger;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class CryptionREC {

    TreeCryptographer treeCryptographer;
    Path startPath;
    PrintLogger printLogger;
    ExecutorService executorService;

    public CryptionREC(TreeCryptographer treeCryptographer, Path startPath, ExecutorService executorService, PrintLogger printLogger) throws IOException {
        this.treeCryptographer = treeCryptographer;
        this.startPath = startPath;
        this.executorService = executorService;
        treeCryptographer.getProgressBarController().initialize(startPath);
        this.printLogger = printLogger;
    }

    public void launchCryptography(Controller.MODE mode) {
        executorService.submit(() -> {
            try {
                Files.walkFileTree(startPath, new HashSet<>(), Integer.MAX_VALUE, treeCryptographer);
            } catch (IOException e) {
                e.printStackTrace();
                printLogger.printBackSpace();
                printLogger.println("Something went wrong walking the file tree");
            }
        });
    }
}

