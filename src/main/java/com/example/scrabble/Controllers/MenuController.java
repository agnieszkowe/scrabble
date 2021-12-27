package com.example.scrabble.Controllers;

import com.example.scrabble.HelloApplication;
import com.example.scrabble.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.example.scrabble.Menu.*;

public class MenuController {


    @FXML
    private Button newGameButton;

    @FXML
    private Button optionsButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button createAccButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button statisticsButton;



    public void initialize() throws IOException {
        newGameButton.setOnAction(event -> {
            HelloApplication helloApplication = new HelloApplication();
            Stage stage = (Stage) newGameButton.getScene().getWindow();
            try {
                helloApplication.start(stage);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        optionsButton.setOnAction(event -> {
            try {
                root.getChildren().remove(mainMenu);
                options = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/options.fxml")));
                root.getChildren().add(options);
            }catch (Exception e){
                e.printStackTrace();
            }

        });

        createAccButton.setOnAction(event -> {
            try {
                root.getChildren().remove(mainMenu);
                createAcc = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/createAcc.fxml")));
                root.getChildren().add(createAcc);

            }catch (Exception e){
                e.printStackTrace();
            }

        });

        loginButton.setOnAction(event -> {
            try {
                root.getChildren().remove(mainMenu);
                login = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/login.fxml")));
                root.getChildren().add(login);

            }catch (Exception e){
                e.printStackTrace();
            }

        });

        statisticsButton.setOnAction(event -> {
            try {
                root.getChildren().remove(mainMenu);
                statistics = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/statistics.fxml")));
                root.getChildren().add(statistics);

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        exitButton.setOnAction(event -> {
            try {
                Platform.exit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }
}
