package com.example.scrabble.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import static com.example.scrabble.Menu.*;

public class OptionController {


    @FXML
    private Button goBackButton;

    @FXML
    private ComboBox<String> playersNoComboBox1;

    @FXML
    private ComboBox<String> playersNoComboBox2;

    @FXML
    private ComboBox<String> playersNoComboBox3;

    @FXML
    private ComboBox<String> playersNoComboBox4;

    public void initialize() {
        goBackButton.setOnAction(event -> {
            try {
                root.getChildren().remove(options);
                //options = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/options.fxml")));
                root.getChildren().add(mainMenu);
                observablePlayerList.clear();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        playersNoComboBox1.setItems(observablePlayerList);
        playersNoComboBox1.setValue(observablePlayerList.get(0));
        playersNoComboBox2.setItems(observablePlayerList);
        playersNoComboBox2.setValue(observablePlayerList.get(1));
        playersNoComboBox3.setItems(observablePlayerList);
        playersNoComboBox3.setValue(observablePlayerList.get(observablePlayerList.size()-1));
        playersNoComboBox4.setItems(observablePlayerList);
        playersNoComboBox4.setValue(observablePlayerList.get(observablePlayerList.size()-1));
    }

}

