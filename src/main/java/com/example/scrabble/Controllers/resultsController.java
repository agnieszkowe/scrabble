package com.example.scrabble.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.example.scrabble.HelloApplication;

import java.io.*;
import java.security.Key;
import java.util.*;

public class resultsController {
    @FXML
    private Label number4;
    @FXML
    private Label firstPlayer;

    @FXML
    private Label firstPoints;

    @FXML
    private Label fourthPlayer;

    @FXML
    private Label fourthPoints;

    @FXML
    private Label secondPlayer;

    @FXML
    private Label secondPoints;

    @FXML
    private Label thirdPlayer;

    @FXML
    private Label thirdPoints;

    @FXML
    private Button goBackButton;

    public void initialize() {


        HashMap<String,Integer> results = new HashMap<>();
        results.put(HelloApplication.player1.getText(),Integer.parseInt(HelloApplication.player1Points.getText()));
        results.put(HelloApplication.player2.getText(),Integer.parseInt(HelloApplication.player2Points.getText()));

        if (!HelloApplication.player3.getText().isEmpty()) {
            results.put(HelloApplication.player3.getText(),Integer.parseInt(HelloApplication.player3Points.getText()));
        }
        if (!HelloApplication.player4.getText().isEmpty()) {
            results.put(HelloApplication.player4.getText(),Integer.parseInt(HelloApplication.player4Points.getText()));
        }
        String winner = new String();
        int winnerPlayerPoints=0;
        String second = new String();
        int secondPlayerPoints=0;
        String third = new String();
        int thirdPlayerPoints=0;
        String fourth = new String();
        int fourthPlayerPoints=0;
        ArrayList<Integer> scores = new ArrayList<>(results.values());
        Collections.sort(scores);
        for (String key : results.keySet()){
            if(results.get(key) == scores.get(scores.size()-1)){
                winner = key;
                winnerPlayerPoints = scores.get(scores.size()-1);
            }
        }
        for (String key : results.keySet()){
            if(results.get(key) == scores.get(scores.size()-2)){
                second = key;
                secondPlayerPoints = scores.get(scores.size()-2);
            }
        }
        if(scores.size() == 3) {
            for (String key : results.keySet()) {
                if (results.get(key) == scores.get(scores.size() - 3)) {
                    third = key;
                    thirdPlayerPoints = scores.get(scores.size()-3);
                }
            }
        }
        if(scores.size()==4) {
            for (String key : results.keySet()) {
                if (results.get(key) == scores.get(scores.size() - 3)) {
                    third = key;
                    thirdPlayerPoints = scores.get(scores.size()-3);
                }
            }
            for (String key : results.keySet()) {
                if (results.get(key) == scores.get(scores.size()) - 4) {
                    fourth = key;
                    fourthPlayerPoints = scores.get(scores.size()-4);
                }
            }
        }

        firstPlayer.setText(winner);
        firstPoints.setText(String.valueOf(winnerPlayerPoints) + " pkt");
        secondPlayer.setText(second);
        secondPoints.setText(String.valueOf(secondPlayerPoints) + " pkt");
        if(scores.size()==2){
            thirdPoints.setText("");
            fourthPoints.setText("");
            number4.setText("");
        } else if (scores.size()==3){
            thirdPlayer.setText(third);
            thirdPoints.setText(String.valueOf(thirdPlayerPoints) + " pkt");
            fourthPoints.setText("");
            number4.setText("");
        } else {
            fourthPlayer.setText(fourth);
            fourthPoints.setText(String.valueOf(fourthPlayerPoints) + " pkt");
        }
        getFromDatabase();


    }
    public void updateStatistics(){

        BufferedWriter writer;
        try {
            writer = new BufferedWriter((new OutputStreamWriter(
                    new FileOutputStream("src/main/resources/com/example/scrabble/database.txt", true), "UTF-8")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private String getFromDatabase() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("src/main/resources/com/example/scrabble/statistics.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] parts;
        String stringToReturn = "";
        while (true) {
            assert scanner != null;
            if (!scanner.hasNextLine()) break;
            parts = scanner.nextLine().split(";");
            System.out.println(parts[0]);
        }
        scanner.close();
        return stringToReturn;
    }
}
