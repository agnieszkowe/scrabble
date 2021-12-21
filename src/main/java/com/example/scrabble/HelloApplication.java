package com.example.scrabble;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.example.scrabble.Menu.*;



public class HelloApplication extends Application {

    public ArrayList<Field> fieldArrayList = new ArrayList<>();
    public ArrayList<Letter> letterArrayList = new ArrayList<>();
    public ArrayList<Player> playerArrayList = new ArrayList<>();
    static public ArrayList<String> playerNicknamesArrayList = new ArrayList<>();
    public ArrayList<LetterField> letterFieldArrayList = new ArrayList<>();
    public ArrayList<Field> playerGameFields = new ArrayList<>();
    public Player player;
    private ArrayList<Player> playersOut = new ArrayList<>();
    private Button giveBackWord, passTurn, surrender;
    private Label playerName, information;
    private boolean ifFirstTurn;
    private Label scoreboard;
    static public Label player1,player2,player3,player4,player1Points,player2Points,player3Points,player4Points;
    private Stage stage;
    private Thread thread = null;
    private Timeline timeline;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/hello-view.fxml")));
        stage.setScene(new Scene(root));
        //stage.setTitle("Hello!");
        stage.show();
        setGameboard();
        createScoreboard();
        root.getChildren().addAll(giveBackWord,playerName,scoreboard,player1,player2,player3,player4,player1Points,player2Points,player3Points,player4Points,passTurn,surrender,information);
        Generator generator = new Generator();
        fieldArrayList = generator.mapGenerator(root);
        letterFieldArrayList = generator.LetterFieldsGenerator(root);
        letterArrayList = generator.LetterGenerator();
//        playerArrayList.add(new Player("Aga",generator.PlayerLetterRandom(letterArrayList)));
//        playerArrayList.add(new Player("Maks",generator.PlayerLetterRandom(letterArrayList)));
//        playerArrayList.add(new Player("Dawid",generator.PlayerLetterRandom(letterArrayList)));
        for (String s:playerNicknamesArrayList) {
            playerArrayList.add(new Player(s,generator.PlayerLetterRandom(letterArrayList)));
        }
        setNamesOfPlayers();
        player = playerArrayList.get(0);
        setLettersOfPlayer(player.playersLetters);
        playerName.setText((player.getName()));
        ifFirstTurn = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> gameLoop()));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
                //setTimeline(timeline);
            }
        });
        thread.start();
    }

    private void gameLoop() {
        //System.out.println("siema");
        /*
        if(player.playersLetters.size() == 0){
            if(!playersOut.contains(player)){
                playersOut.add(player);
            }
            if(playersOut.size() == playerArrayList.size()){
                try {
                    root.getChildren().remove(root);
                    results = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/results.fxml")));
                    root.getChildren().add(results);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            passTurn();


        }*/
        if(player.playersLetters.size()==0){
            if(!playersOut.contains(player)){
                playersOut.add(player);
            }
            if(playersOut.size()==playerArrayList.size()){
                //System.out.println("KONIEC");
                try {

                    //ogarnij sobie funkcję
                    endGame();
                    results = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/results.fxml")));
                    stage.setScene(new Scene(results));
                    stage.show();
                    // TU TRZEBA ZATRZYMAC GAMELOOP I OTWORZYC MAIN MENU PO KLIKNIECU PRZYCISKU GO BACK



                    root.getChildren().add(results);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                passTurn();
            }
        }

        if(passTurn.isPressed()){
            passTurn();
        }
        //if(checkIfOutOfLetters()){ // returns true if all of players dont have any letters

       // } else {
            if (this.giveBackWord.isPressed()) {
                nextTurnGenerator();
            }
            setLetterFieldTouch();

            checkIfLetterInput();

            //System.out.println(letterArrayList.size());
    //    }
    }

    public static void main(String[] args) {
        launch();
    }

    private void endGame(){
        timeline.stop();
    }


    public void nextTurnGenerator(){
        if (checkIfWordCorrectAfter()) {
            information.setText("");
            for (Field field : playerGameFields) {
                field.isModified = true;
            }

            playerGameFields.clear();
            ArrayList<Letter> lettersToDelete = new ArrayList<>();

            for (LetterField letterField : letterFieldArrayList) {
                if (letterField.button.isDisable() && !letterField.button.getText().equals("")) {
                    lettersToDelete.add(letterField.letter);
                }
            }

            for (Letter s : lettersToDelete) {
                player.playersLetters.remove(s);
                //letterFieldArrayList.remove(s);
            }

            Random random = new Random();
            Integer NoLeftPlayersLetters = player.playersLetters.size();
            for (int i = 0; i < 7 - NoLeftPlayersLetters; i++) {
                if (letterArrayList.size() >= 1) {
                    int rand = random.nextInt(letterArrayList.size());
                    player.playersLetters.add(letterArrayList.get(rand));
                    letterArrayList.remove(rand);
                }
            }

            playerArrayList.remove(player);
            playerArrayList.add(player);
            this.player = playerArrayList.get(0);
            playerName.setText((player.getName()));
            setLettersOfPlayer(this.player.playersLetters);
            activateLetterFields();

        } else {
            System.out.println("word IS BAD");
            information.setText("Wrong Word! Try again or pass your turn");

            for (Field field : playerGameFields) {
                if (field.isModified == false) {
                    field.button.setText("");
                    field.button.setDisable(false);
                }
            }

            for (LetterField letterField : letterFieldArrayList) {
                letterField.button.setDisable(false);
            }

            playerGameFields.clear();
        }
    }

    private void setLettersOfPlayer(ArrayList<Letter> letters){
        int counter = 0;
        for (Letter letter:letters) {
            letterFieldArrayList.get(counter).button.setText(letter.getLetter());
            letterFieldArrayList.get(counter).setLetterPoints(letter.getValue());
            letterFieldArrayList.get(counter).letter = letter;
            counter++;
        }
        for(int i=counter; i < 7;i++){
            letterFieldArrayList.get(i).button.setText("");
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
    private boolean checkIfWordCorrectAfter(){
        if(playerGameFields.size()==0) {
            return false;
        } else {
            ArrayList<Double> xArray = new ArrayList<>();
            ArrayList<Double> yArray = new ArrayList<>();
            for (Field field : playerGameFields) {
                xArray.add(field.button.getLayoutX());
                yArray.add(field.button.getLayoutY());
            }
            Double maxX = Collections.max(xArray);
            Double minX = Collections.min(xArray);
            Double maxY = Collections.max(yArray);
            Double minY = Collections.min(yArray);
            ArrayList<Field> existingWord = new ArrayList<>();

            boolean toReturnX = false;
            boolean toReturnY = false;
            boolean toReturnVertical = false;
            boolean toReturnHorizontal = false;

            Collections.sort(xArray);
            Collections.sort(yArray);

            // if first turn
            if (ifFirstTurn) {
                for (Field field : playerGameFields) {
                    Double x = field.button.getLayoutX();
                    Double y = field.button.getLayoutY();
                    if (x == 210 && y == 210) {
                        ifFirstTurn = false;
                        break;
                    }
                }
                if (ifFirstTurn) {
                    return false;
                }
            } else { //if not first turn, normal procedure
                // if word crosses other word - condition
                int noCrossCounter = 0;
                for (Field field : playerGameFields) {
                    double x = field.button.getLayoutX();
                    double y = field.button.getLayoutY();
                    if ((getByXY(fieldArrayList, x, y - 30, true) == null) && (getByXY(fieldArrayList, x, y + 30, true) == null) && (getByXY(fieldArrayList, x - 30, y, true) == null) && (getByXY(fieldArrayList, x + 30, y, true) == null)) {
                        noCrossCounter++;
                    }
                }
                if (noCrossCounter == playerGameFields.size()) {
                    return false;
                }
            }
            // Checking for collisions + between playergamefield and fieldarraylist
            boolean collisions = false;
            for (Field field : playerGameFields) {
                double x = field.button.getLayoutX();
                double y = field.button.getLayoutY();
                if ((getByXY(fieldArrayList, x, y - 30, true)) != null && (getByXY(fieldArrayList, x - 30, y, true) != null || getByXY(fieldArrayList, x + 30, y, true) != null)) {
                    collisions = true;
                } else if ((getByXY(fieldArrayList, x, y + 30, true)) != null && (getByXY(fieldArrayList, x - 30, y, true) != null || getByXY(fieldArrayList, x + 30, y, true) != null)) {
                    collisions = true;
                } else if ((getByXY(fieldArrayList, x - 30, y, true)) != null && (getByXY(fieldArrayList, x, y - 30, true) != null || getByXY(fieldArrayList, x, y + 30, true) != null)) {
                    collisions = true;
                } else if ((getByXY(fieldArrayList, x + 30, y, true)) != null && (getByXY(fieldArrayList, x, y - 30, true) != null || getByXY(fieldArrayList, x, y + 30, true) != null)) {
                    collisions = true;
                } else if ((getByXY(fieldArrayList, x + 30, y, true)) != null && (getByXYPlayerGame(playerGameFields, x, y - 30) != null || getByXYPlayerGame(playerGameFields, x, y + 30) != null)) {
                    collisions = true;
                } else if ((getByXY(fieldArrayList, x - 30, y, true)) != null && (getByXYPlayerGame(playerGameFields, x, y - 30) != null || getByXYPlayerGame(playerGameFields, x, y + 30) != null)) {
                    collisions = true;
                } else if ((getByXY(fieldArrayList, x, y - 30, true)) != null && (getByXYPlayerGame(playerGameFields, x - 30, y) != null || getByXYPlayerGame(playerGameFields, x + 30, y) != null)) {
                    collisions = true;
                } else if ((getByXY(fieldArrayList, x, y + 30, true)) != null && (getByXYPlayerGame(playerGameFields, x - 30, y) != null || getByXYPlayerGame(playerGameFields, x + 30, y) != null)) {
                    collisions = true;
                }
            }
            if (collisions) {
                return false;
            }
            // if 1 letter added
            if (playerGameFields.size() == 1) {
                if (getByXY(fieldArrayList, minX, minY - 30, true) != null || (getByXY(fieldArrayList, minX, minY + 30, true)) != null) {
                    toReturnVertical = true;
                } else if (getByXY(fieldArrayList, minX - 30, minY, true) != null || (getByXY(fieldArrayList, minX + 30, minY, true)) != null) {
                    toReturnHorizontal = true;
                }
            } else {
                for (int i = 0; i < xArray.size() - 1; i++) {
                    if (!xArray.get(i).equals(xArray.get(i + 1))) {
                        toReturnVertical = false;
                        break;
                    }
                    toReturnVertical = true;
                }
                for (int i = 0; i < yArray.size() - 1; i++) {
                    if (!yArray.get(i).equals(yArray.get(i + 1))) {
                        toReturnHorizontal = false;
                        break;
                    }
                    toReturnHorizontal = true;
                }
                if (!toReturnHorizontal && !toReturnVertical) {
                    System.out.println("Ani pion ani poziom");
                }
            }

            // horizontal word
            if (toReturnHorizontal) {
                toReturnX = true;
                for (double i = minX - 30; i >= 0; i -= 30) {
                    Field field = getByXY(fieldArrayList, i, minY, true);
                    if (field == null) {
                        break;
                    } else {
                        existingWord.add(0, field);
                    }
                }
                existingWord.add(getByXYPlayerGame(playerGameFields, minX, minY));
                for (double i = minX + 30; i >= 0; i += 30) {
                    Field field = getByXY(fieldArrayList, i, minY, true);
                    if (field == null) {
                        Field gameField = getByXYPlayerGame(playerGameFields, i, minY);
                        if (gameField == null) {
                            break;
                        } else {
                            existingWord.add(gameField);
                        }
                    } else {
                        existingWord.add(field);
                    }
                }
            }
            // vertical word
            if (toReturnVertical) {
                toReturnY = true;
                for (double i = minY - 30; i >= 0; i -= 30) {
                    Field field = getByXY(fieldArrayList, maxX, i, true);
                    if (field == null) {
                        break;
                    } else {
                        existingWord.add(0, field);
                    }
                }
                existingWord.add(getByXYPlayerGame(playerGameFields, minX, minY));
                for (double i = minY + 30; i <= 450; i += 30) {
                    Field field = getByXY(fieldArrayList, minX, i, true);
                    if (field == null) {
                        Field gameField = getByXYPlayerGame(playerGameFields, minX, i);
                        if (gameField == null) {
                            break;
                        } else {
                            existingWord.add(gameField);
                        }
                    } else {
                        existingWord.add(field);
                    }
                }
            }

            StringBuilder word = new StringBuilder();
            for (Field field : existingWord) {
                word.append(field.button.getText());
            }
            ArrayList<String> letterArray = new ArrayList<>();
            for (Field field : existingWord) {
                letterArray.add(field.button.getText());
            }
            for (Field field : playerGameFields) {
                if (!letterArray.contains(field.button.getText())) {
                    return false;
                }
            }

//        if ((checkWord(word.toString()) == false) || (!toReturnY && !toReturnX)){
//            return false;
//        }

            // counting points
            int pointsFinal, points = 0;
            int wordBonus = 1;
            for (Field field : existingWord) {
                if (field.getWordBonus() != 1) {
                    wordBonus = field.getWordBonus();
                }
                points += field.getLetterBonus() * field.getLetterPoints();
            }
            if (wordBonus == 1) {
                pointsFinal = points;
            } else {
                pointsFinal = points * wordBonus;
            }
            // updating scoreboard
            if (player1.getText().equals(player.getName())) {
                int current_points = Integer.parseInt(player1Points.getText());
                current_points += pointsFinal;
                player1Points.setText(Integer.toString(current_points));
            }
            if (player2.getText().equals(player.getName())) {
                int current_points = Integer.parseInt(player2Points.getText());
                current_points += pointsFinal;
                player2Points.setText(Integer.toString(current_points));
            }
            if (player3.getText().equals(player.getName())) {
                int current_points = Integer.parseInt(player3Points.getText());
                current_points += pointsFinal;
                player3Points.setText(Integer.toString(current_points));
            }
            if (player4.getText().equals(player.getName())) {
                int current_points = Integer.parseInt(player4Points.getText());
                current_points += pointsFinal;
                player4Points.setText(Integer.toString(current_points));
            }
            System.out.println(word);
            return true;
        }

    }
    /*public boolean checkIfOutOfLetters(){
        int playersWithoutLetters=0;
        for (Player player : playerArrayList){
            if(player.playersLetters.size() == 0){
                playersWithoutLetters++;
            }
        }
        if (playersWithoutLetters == playerArrayList.size()){
            //end game
            System.out.println("KUNIEC");
            return true;
        }
        return false;
    }
    */

    public void passTurn(){
        playerArrayList.remove(player);
        playerArrayList.add(player);
        this.player = playerArrayList.get(0);
        playerName.setText((player.getName()));
        setLettersOfPlayer(player.playersLetters);
        activateLetterFields();
    }
    public boolean checkWord(String word){
        boolean isWord = false;
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/com/example/scrabble/dictionary.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(word)){
                    isWord = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isWord;
    }

    public void activateLetterFields(){
        for (LetterField letterField:letterFieldArrayList) { // pogubiłem sie w letterfield/field/gamefield xD ale działa
            if(letterField.button.getText().equals("")){
                letterField.button.setDisable(true);
            } else {
                letterField.button.setDisable(false);
            }

        }
    }

    public Field getByXY(ArrayList<Field> list, double x, double y, boolean ismodified){
        for(Field field : list){
            if(field.button.getLayoutY()==y && field.button.getLayoutX()==x && field.isModified == ismodified){
                return field;
            }
        }
        return null;
    }

    public Field getByXYPlayerGame(ArrayList<Field> list, double x, double y){
        for(Field field : list){
            if(field.button.getLayoutY()==y && field.button.getLayoutX()==x){
                return field;
            }
        }
        return null;
    }
    public void createScoreboard(){
        int mine = -188;
        int minex = 6;
        Label scoreboard = new Label("Scoreboard");
        scoreboard.relocate(600+minex,400+mine);
        scoreboard.setStyle("-fx-border-color:grey; -fx-background-color: #babfbb; -fx-alignment: center");
        scoreboard.setMinWidth(300);
        scoreboard.setMinHeight(30);
        Label player1 = new Label("");
        Label player2 = new Label("");
        Label player3 = new Label("");
        Label player4 = new Label("");
        player1.relocate(600+minex,430+mine);
        player1.setMinWidth(75);
        player1.setMinHeight(30);
        player1.setStyle("-fx-border-color:grey; -fx-background-color: #babfbb; -fx-alignment: center");
        player2.relocate(675+minex,430+mine);
        player2.setMinWidth(75);
        player2.setMinHeight(30);
        player2.setStyle("-fx-border-color:grey; -fx-background-color: #babfbb; -fx-alignment: center");
        player3.relocate(750+minex,430+mine);
        player3.setMinWidth(75);
        player3.setMinHeight(30);
        player3.setStyle("-fx-border-color:grey; -fx-background-color: #babfbb; -fx-alignment: center");
        player4.relocate(825+minex,430+mine);
        player4.setMinWidth(75);
        player4.setMinHeight(30);
        player4.setStyle("-fx-border-color:grey; -fx-background-color: #babfbb; -fx-alignment: center");
        Label player1Points = new Label("0");
        Label player2Points = new Label("0");
        Label player3Points = new Label("0");
        Label player4Points = new Label("0");
        player1Points.relocate(600+minex,460+mine);
        player1Points.setMinWidth(75);
        player1Points.setMinHeight(30);
        player1Points.setStyle("-fx-border-color:grey; -fx-background-color: #babfbb; -fx-alignment: center");
        player2Points.relocate(675+minex,460+mine);
        player2Points.setMinWidth(75);
        player2Points.setMinHeight(30);
        player2Points.setStyle("-fx-border-color:grey; -fx-background-color: #babfbb; -fx-alignment: center");
        player3Points.relocate(750+minex,460+mine);
        player3Points.setMinWidth(75);
        player3Points.setMinHeight(30);
        player3Points.setStyle("-fx-border-color:grey; -fx-background-color: #babfbb; -fx-alignment: center");
        player4Points.relocate(825+minex,460+mine);
        player4Points.setMinWidth(75);
        player4Points.setMinHeight(30);
        player4Points.setStyle("-fx-border-color:grey; -fx-background-color: #babfbb; -fx-alignment: center");
        this.scoreboard = scoreboard;
        this.player1=player1;
        this.player1Points = player1Points;
        this.player2 = player2;
        this.player2Points = player2Points;
        this.player3 = player3;
        this.player3Points = player3Points;
        this.player4 = player4;
        this.player4Points = player4Points;
    }

    public void setNamesOfPlayers(){
        int length = playerArrayList.size();
        if(length == 2) {
            player1.setText(playerArrayList.get(0).getName());
            player2.setText(playerArrayList.get(1).getName());
        } else if (length == 3){
            player1.setText(playerArrayList.get(0).getName());
            player2.setText(playerArrayList.get(1).getName());
            player3.setText(playerArrayList.get(2).getName());
        } else if (length == 4){
            player1.setText(playerArrayList.get(0).getName());
            player2.setText(playerArrayList.get(1).getName());
            player3.setText(playerArrayList.get(2).getName());
            player4.setText(playerArrayList.get(3).getName());
        }
    }
    public void setGameboard(){
        information = new Label("");
        information.relocate(100,570);
        information.setTextFill(Color.color(1,0,0));
        giveBackWord = new Button("Give back the word");
        giveBackWord.relocate(535,340);
        giveBackWord.setMinHeight(50);
        giveBackWord.setMinWidth(150);
        passTurn = new Button("Pass my turn");
        passTurn.relocate(700,340);
        passTurn.setMinWidth(150);
        passTurn.setMinHeight(50);
        surrender = new Button("Give Up");
        surrender.relocate(865,340);
        surrender.setMinHeight(50);
        surrender.setMinWidth(100);
        playerName = new Label();
        playerName.setText("");
        playerName.relocate(630, 138);
        playerName.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
    }
}