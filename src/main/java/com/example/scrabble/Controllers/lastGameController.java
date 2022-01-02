package com.example.scrabble.Controllers;

import com.example.scrabble.Field;
import com.example.scrabble.Generator;
import com.example.scrabble.Letter;
import com.example.scrabble.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static com.example.scrabble.Menu.*;

public class lastGameController {
    ArrayList<ArrayList<String>> file = new ArrayList<>();
    private int moveCounter = 0;
    public ArrayList<Field> fieldArrayList = new ArrayList<>();
    private int fileLength;
    @FXML
    private Pane mainPane;
    @FXML
    private Button nextMoveButton;
    @FXML
    private Label playerNameLabel;
    @FXML
    private Label pointsLabel;
    @FXML
    private Button goBackButton;

    public void initialize() throws IOException {
        Generator generator = new Generator();

        fieldArrayList = generator.mapGenerator(mainPane);


        /*BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/com/example/scrabble/statistics.txt"));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        this.fileLength = lines;
        */

        readFile();
        nextMoveButton.setOnAction(event -> {

            nextMove();

        });
        goBackButton.setOnAction(event -> {

            try {
                root.getChildren().remove(lastGame);
                //options = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/options.fxml")));
                root.getChildren().add(mainMenu);
            }catch (Exception e){
                e.printStackTrace();
            }

        });

    }


    public void readFile(){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("src/main/resources/com/example/scrabble/gameHistory.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<String>> file = new ArrayList<>();

        String[] parts = new String[0];

        while (true) {
            assert scanner != null;
            if (!scanner.hasNextLine()) break;
            parts = scanner.nextLine().split(";");
            ArrayList<String> line = new ArrayList<>();
            for(int i=0;i<parts.length;i++){
                line.add(parts[i]);
            }
            file.add(line);

        }
        this.file = file;
        this.fileLength = file.size();
        for(int i = 0; i<file.size(); i++){
            for(int j = 0 ; j< file.get(i).size(); j++){
                System.out.println(file.get(i).get(j));
            }
        }

    }
    public void nextMove(){
        ArrayList<String> line = file.get(moveCounter);
        String name = line.get(0);
        playerNameLabel.setText(name);
        String points = line.get(line.size()-1);
        pointsLabel.setText(points);
        ArrayList<String> currentCoord = new ArrayList<String>();
        for(int i = 1; i < line.size()-1; i++){
            String data = line.get(i);
            if(data.length()>1){
                currentCoord.add(data);
            } else {
                currentCoord.add(data);
            }
            if(currentCoord.size()==3){
                for(Field field : fieldArrayList){
                    if((field.getX()==Double.parseDouble(currentCoord.get(0))) && (field.getY()==Double.parseDouble(currentCoord.get(1)))) {
                        field.getButton().setText(currentCoord.get(2));
                    }
                }
                currentCoord.clear();
            }

        }
        moveCounter++;
        if(moveCounter == fileLength){
            nextMoveButton.setDisable(true);
        }
    }
    //dodać previous move
}
