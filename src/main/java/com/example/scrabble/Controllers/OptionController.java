package com.example.scrabble.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import static com.example.scrabble.HelloApplication.TimePerMove;
import static com.example.scrabble.HelloApplication.playerNicknamesArrayList;
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

    @FXML
    private Button setOptionButton;

    @FXML
    private ComboBox<Integer> timeComboBox;

    public ObservableList<Integer> observableTimeList;


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

        setOptionButton.setOnAction(event -> {
            if (!playersNoComboBox1.getValue().equals("")) {
                playerNicknamesArrayList.add(playersNoComboBox1.getValue());
            }
            if (!playersNoComboBox2.getValue().equals("")) {
                playerNicknamesArrayList.add(playersNoComboBox2.getValue());
            }
            if (!playersNoComboBox3.getValue().equals("")) {
                playerNicknamesArrayList.add(playersNoComboBox3.getValue());
            }
            if (!playersNoComboBox4.getValue().equals("")) {
                playerNicknamesArrayList.add(playersNoComboBox4.getValue());
            }
            TimePerMove = timeComboBox.getValue();
        });

        playersNoComboBox1.setItems(observablePlayerList);
        playersNoComboBox1.setValue(observablePlayerList.get(0));
        playersNoComboBox2.setItems(observablePlayerList);
        playersNoComboBox2.setValue(observablePlayerList.get(1));
        playersNoComboBox3.setItems(observablePlayerList);
        playersNoComboBox3.setValue(observablePlayerList.get(observablePlayerList.size()-1));
        playersNoComboBox4.setItems(observablePlayerList);
        playersNoComboBox4.setValue(observablePlayerList.get(observablePlayerList.size()-1));

        //time
        observableTimeList = FXCollections.observableArrayList();
        observableTimeList.addAll(15,30,45,60,90,120,150,180);
        timeComboBox.setItems(observableTimeList);
        timeComboBox.setValue(observableTimeList.get(0));
    }

}

