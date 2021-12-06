package com.example.scrabble.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

import static com.example.scrabble.Menu.*;

public class OptionController {

    @FXML
    private ComboBox<?> playersNoComboBox;

    @FXML
    private Button goBackButton;

    public void initialize() {
        goBackButton.setOnAction(event -> {
            try {
                root.getChildren().remove(options);
                //options = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/options.fxml")));
                root.getChildren().add(mainMenu);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

}

