package com.example.piglatin;

import com.example.piglatin.ui.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        MainController mainController = new MainController();
        Scene scene = new Scene(mainController.getView(), 1200, 700);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/piglatin/ui/styles/styles.css").toExternalForm()
        );

        primaryStage.setTitle("PigLatin Compiler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}