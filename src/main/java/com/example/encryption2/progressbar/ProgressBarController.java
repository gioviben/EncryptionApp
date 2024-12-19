package com.example.encryption2.progressbar;

import com.example.encryption2.util.DirectoryOP;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.nio.file.Path;

public class ProgressBarController {

    int currentStatus;
    int finalStatus;
    @FXML
    private Label currentStatusLabel;
    @FXML
    private Label finalStatusLabel;
    @FXML
    private ProgressBar progressBar;


    public void initialize(Path startPath) throws IOException {
        currentStatus = 0;
        finalStatus = DirectoryOP.countTOTfileTree(startPath);
        progressBar.setProgress(currentStatus);
        currentStatusLabel.setText(Integer.valueOf(currentStatus).toString());
        finalStatusLabel.setText(Integer.valueOf(finalStatus).toString());
    }

    public void increaseProgressBar() {
        ++currentStatus;
        progressBar.setProgress((double) currentStatus/finalStatus);
    }

    public void refreshCount(){
        currentStatusLabel.setText(Integer.valueOf(currentStatus).toString());
    }

    public double getCurrentStatus() {
        return currentStatus;
    }

    public double getFinalStatus() {
        return finalStatus;
    }
}
