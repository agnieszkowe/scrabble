package com.example.scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

public class Menu extends Application {

    static public Pane root;
    static public Pane mainMenu;
    static public Pane options;
    @Override
    public void start(Stage primaryStage) throws Exception {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/mainFrame.fxml")));
        mainMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/menu.fxml")));
        root.getChildren().add(mainMenu);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setResizable(false);
    }
}
