package com.example.scrabble.Controllers;

import com.example.scrabble.HelloApplication;
import com.example.scrabble.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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



    public void initialize() {
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
                observablePlayerList.add("Bot Marcin");
                observablePlayerList.add("Bot Andrzej");
                observablePlayerList.add("Bot Krzysztof");
                observablePlayerList.add("Bot Duszan");
                observablePlayerList.add("");
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


    }
}
