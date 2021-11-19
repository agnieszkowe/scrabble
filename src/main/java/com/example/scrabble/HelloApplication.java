package com.example.scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class HelloApplication extends Application {

    public ArrayList<Field> fieldArrayList = new ArrayList<>();
    public ArrayList<Letter> letterArrayList = new ArrayList<>();
    @Override
    public void start(Stage stage) throws IOException {
        Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/hello-view.fxml")));
        stage.setScene(new Scene(root));
        stage.setTitle("Hello!");
        stage.show();
        Generator generator = new Generator();
        fieldArrayList = generator.mapGenerator(root);
        generator.LetterFieldsGenerator(root);
        letterArrayList = generator.LetterGenerator();
        ArrayList<Letter> player1LetterArrayList = generator.PlayerLetterRandom(letterArrayList);
        ArrayList<Letter> player2LetterArrayList = generator.PlayerLetterRandom(letterArrayList);
    }

    public static void main(String[] args) {
        launch();
    }
}