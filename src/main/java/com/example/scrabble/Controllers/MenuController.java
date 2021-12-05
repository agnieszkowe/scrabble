package com.example.scrabble.Controllers;

import com.example.scrabble.HelloApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Button newGameButton;

    @FXML
    private Button optionsButton;

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

    }
}
