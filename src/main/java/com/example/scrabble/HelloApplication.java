package com.example.scrabble;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;


public class HelloApplication extends Application {

    public ArrayList<Field> fieldArrayList = new ArrayList<>();
    public ArrayList<Letter> letterArrayList = new ArrayList<>();
    public ArrayList<Player> playerArrayList = new ArrayList<>();
    public ArrayList<LetterField> letterFieldArrayList = new ArrayList<>();
    public ArrayList<Field> playerGameFields = new ArrayList<>();
    public Player player;
    private Button giveBackWord;
    private boolean ifFirstTurn;
    @Override
    public void start(Stage stage) throws IOException {
        Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/hello-view.fxml")));
        stage.setScene(new Scene(root));
        stage.setTitle("Hello!");
        stage.show();
        giveBackWord = new Button("Give bACK THE WORD");
        giveBackWord.relocate(600,600);
        root.getChildren().add(giveBackWord);
        Generator generator = new Generator();
        fieldArrayList = generator.mapGenerator(root);
        letterFieldArrayList = generator.LetterFieldsGenerator(root);
        letterArrayList = generator.LetterGenerator();
        playerArrayList.add(new Player("Aga",generator.PlayerLetterRandom(letterArrayList)));
        playerArrayList.add(new Player("Maks",generator.PlayerLetterRandom(letterArrayList)));
        player = playerArrayList.get(0);
        setLettersOfPlayer(player.playersLetters);
        ifFirstTurn = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(80), e -> gameLoop()));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
                //setTimeline(timeline);
            }
        }).start();
    }

    private void gameLoop() {
        //System.out.println("siema");
        if (this.giveBackWord.isPressed()){
            nextTurnGenerator();
        }
        setLetterFieldTouch();
        checkIfLetterInput();
    }

    public static void main(String[] args) {
        launch();
    }


    private void nextTurnGenerator(){
        System.out.println(checkIfWordCorrect());
        playerArrayList.remove(player);
        playerArrayList.add(player);
        this.player = playerArrayList.get(0);
        setLettersOfPlayer(player.playersLetters);


    }

    private void setLettersOfPlayer(ArrayList<Letter> letters){
        int counter = 0;
        for (Letter letter:letters) {
            letterFieldArrayList.get(counter).button.setText(letter.getLetter());
            letterFieldArrayList.get(counter).setLetterPoints(letter.getValue());
            counter++;
        }

    }

    private void checkIfLetterInput(){
        for (LetterField letterField:letterFieldArrayList) {
            if (letterField.isTouched()){
                for (Field field:fieldArrayList) {
                    if (field.button.isPressed()){
                        System.out.println("hello");
                        field.button.setText(letterField.button.getText());
                        field.button.setDisable(true);
                        letterField.button.setDisable(true);
                        letterField.setTouched(false);
                        field.setLetterPoints(letterField.getLetterPoints());
                        playerGameFields.add(field);
                    }
                }
            }
        }
    }

    private void setLetterFieldTouch(){
        for (LetterField letterField:letterFieldArrayList) {
            if (letterField.button.isPressed()){
                letterField.setTouched(true);
                for (LetterField letterToFalse:letterFieldArrayList) {
                    if(letterToFalse != letterField){
                        letterToFalse.setTouched(false);
                    }
                }
            }
        }
    }

    private boolean checkIfWordCorrect(){
        ArrayList<Double> xArray = new ArrayList<>();
        ArrayList<Double> yArray = new ArrayList<>();
        for (Field field:playerGameFields) {
            xArray.add(field.button.getLayoutX());
            yArray.add(field.button.getLayoutY());
        }
        boolean toReturnX = false;
        boolean toReturnY = false;
        boolean toReturnVertical = false;
        boolean toReturnHorizontal = false;

        Collections.sort(xArray);
        Collections.sort(yArray);

        for (int i=0; i<xArray.size()-1; i++) {
            if (!xArray.get(i).equals(xArray.get(i + 1))) {
                toReturnVertical = false;
                break;
            }
            toReturnVertical = true;
        }
        for (int i=0; i<yArray.size()-1; i++) {
            if (!yArray.get(i).equals(yArray.get(i + 1))) {
                toReturnHorizontal = false;
                break;
            }
            toReturnHorizontal = true;
        }
        if (!toReturnHorizontal && ! toReturnVertical) {
            System.out.println("Ani pion ani poziom");
        }
        if (toReturnHorizontal) {
            System.out.println("poziom");
            if (30*(xArray.size()-1)+xArray.get(0)== xArray.get(xArray.size()-1)){
                toReturnX = true;
                System.out.println("slowo poziome ok");
            } else {
                System.out.println("slowo poziome zle");
            }
        }
        if (toReturnVertical) {
            System.out.println("pion");
            if (30*(yArray.size()-1)+yArray.get(0)== yArray.get(yArray.size()-1)){
                toReturnY = true;
                System.out.println("slowo pionowe ok");
            } else {
                System.out.println("slowo pionowe zle");
            }
        }
        if (!toReturnY && !toReturnX) {
            return false;
        }

        double counter = 0;
        Field[] fields = new Field[playerGameFields.size()];
        if (!toReturnX){
            for (Field field:playerGameFields) {
                if (field.button.getLayoutY() == yArray.get(0)+30*counter){
                    fields[(int) (counter)] = field;
                }
                counter += 1;
            }

        }else {
            for (Field field:playerGameFields) {
                if (field.button.getLayoutX() == xArray.get(0)+30*counter){
                    fields[(int) (counter)] = field;
                }
                counter += 1;
            }

        }
        playerGameFields.clear();
        playerGameFields = new ArrayList<Field>(List.of(fields));


        for (Field field:playerGameFields) {
            Double x = field.button.getLayoutX();
            Double y = field.button.getLayoutY();
            if (ifFirstTurn){
                if (x==210 && y==210){
                    ifFirstTurn=false;
                }
            }
        }
        if (ifFirstTurn){
            return false;
        }else {
            return true;
        }
    }

}