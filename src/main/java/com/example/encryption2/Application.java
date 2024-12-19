package com.example.encryption2;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("terminal-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("Encription");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        /*
        if (LocalDate.now().isBefore(LocalDate.of(2023, 8, 1))) {
            launch();
        } else {
            System.out.println("time scaduto");
        }*/
    }
}