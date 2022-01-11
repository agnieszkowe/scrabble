package com.example.scrabble;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.print.DocFlavor;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.example.scrabble.Menu.*;



public class HelloApplication extends Application {

    public ArrayList<Field> fieldArrayList = new ArrayList<>();
    public ArrayList<Letter> letterArrayList = new ArrayList<>();
    public ArrayList<Player> playerArrayList = new ArrayList<>();
    static public ArrayList<String> playerNicknamesArrayList = new ArrayList<>();
    public ArrayList<LetterField> letterFieldArrayList = new ArrayList<>();
    public ArrayList<Field> playerGameFields = new ArrayList<>();
    public ArrayList<String> botsArrayList = new ArrayList<>();
    public Player player;
    private ArrayList<Player> playersOut = new ArrayList<>();
    private Button giveBackWord, passTurn, surrender;
    private Label playerName, information;
    private boolean ifFirstTurn;
    private Label scoreboard;
    static public Label player1,player2,player3,player4,player1Points,player2Points,player3Points,player4Points,timeLabel;
    private Stage stage;
    private Thread thread = null;
    private Timeline timeline;
    static public Integer TimePerMove;
    private AnimationTimer animationTimer;
    private Integer snapCounter;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/hello-view.fxml")));
        stage.setScene(new Scene(root));
        //stage.setTitle("Hello!");
        stage.show();
        setGameboard();
        createScoreboard();
        root.getChildren().addAll(giveBackWord,playerName,scoreboard,player1,player2,player3,player4,player1Points,player2Points,player3Points,player4Points,passTurn,surrender,information,timeLabel);
        Generator generator = new Generator();
        fieldArrayList = generator.mapGenerator(root);
        letterFieldArrayList = generator.LetterFieldsGenerator(root);
        letterArrayList = generator.LetterGenerator();
//        playerArrayList.add(new Player("Aga",generator.PlayerLetterRandom(letterArrayList)));
//        playerArrayList.add(new Player("Maks",generator.PlayerLetterRandom(letterArrayList)));
//        playerArrayList.add(new Player("Dawid",generator.PlayerLetterRandom(letterArrayList)));
        for (String s:playerNicknamesArrayList) {
            playerArrayList.add(new Player(s,generator.PlayerLetterRandom(letterArrayList)));
            if(s.startsWith("Bot ")){
                botsArrayList.add(s);
            }
        }
        setNamesOfPlayers();
        player = playerArrayList.get(0);
        setLettersOfPlayer(player.playersLetters);
        playerName.setText((player.getName()));

        snapCounter = 1;
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
        calculateTime();
    }

    private void gameLoop() {
        if (player.playersLetters.size() == 0) {
            if (!playersOut.contains(player)) {
                playersOut.add(player);
            }
            if (playersOut.size() == playerArrayList.size()) {
                try {

                    //ogarnij sobie funkcję
                    endGame();
                    results = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/scrabble/results.fxml")));
                    stage.setScene(new Scene(results));
                    stage.show();
                    //root.getChildren().add(results);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                passTurn();
            }
        }

        if (botsArrayList.contains(this.player.getName())) {
            if (ifFirstTurn) {

            } else {
                if (!insertWord()) {
                    passTurn();
                } else {
                    animationTimer.stop();
                    animationTimer.start();
                }
            }

        } else {

            if (passTurn.isPressed()) {
                passTurn();
            }

            if (this.giveBackWord.isPressed()) {
                try {
                    nextTurnGenerator();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setLetterFieldTouch();
            checkIfLetterInput();
        }
    }


    public static void main(String[] args) {
        launch();
    }

    public boolean insertWord(){
        ArrayList<String> currentLetters = new ArrayList<>();
        for(LetterField letterField : letterFieldArrayList){
            currentLetters.add(letterField.letter.getLetter());
        }

        for(Field field : fieldArrayList){
            if(field.isModified){
                if(hasColissions(field) == 0){
                    if(createWord(field,currentLetters,5,0)){
                        return true;
                    } else {

                    }
                } else if(hasColissions(field) == 1){
                    if(createWord(field,currentLetters,5,1)){
                        return true;
                    } else {

                    }
                } else if(hasColissions(field) == 2){
                    if(createWord(field,currentLetters,5,2)){
                        return true;
                    } else {

                    }
                } else if(hasColissions(field) == 3){
                    System.out.println("LOL");
                }
            }
        }

        return true;
    }

    public int hasColissions(Field field){
        if(((getByXY(fieldArrayList, field.getX(), field.getY()-30, true) != null) || (getByXY(fieldArrayList,field.getX(), field.getY()+30, true)) != null) &&
        (getByXY(fieldArrayList, field.getX()-30, field.getY(), true) != null) || (getByXY(fieldArrayList,field.getX()+30, field.getY(), true)) != null){
            return 2;
        } else if((getByXY(fieldArrayList, field.getX()-30, field.getY(), true) != null) || (getByXY(fieldArrayList,field.getX()+30, field.getY(), true)) != null){
            return 0;//vertical
        } else if((getByXY(fieldArrayList, field.getX(), field.getY()-30, true) != null) || (getByXY(fieldArrayList,field.getX(), field.getY()+30, true)) != null){
            return 1; //horizontal;
        }
        return 3;
    }

    public boolean createWord(Field field, ArrayList<String> letters, int limit, int collisions) {
        double mainX = field.getX();
        double mainY = field.getY();
        ArrayList<String> currWordArray = letters;
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/com/example/scrabble/dictionary.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() <= limit) {
                    ArrayList<String> dictWord = new ArrayList<>();
                    for (int i = 0; i < line.length(); i++) {
                        dictWord.add(String.valueOf(line.charAt(i)));
                    }
                    int index = 0;
                    if (dictWord.contains(field.button.getText())) {
                        index = dictWord.indexOf(field.button.getText());
                        dictWord.remove(field.button.getText());
                    }
                    if (currWordArray.containsAll(dictWord)) {
                        dictWord.add(index, field.button.getText());
                        int insertedLetters = 0;
                        ArrayList<Field> insertedFields = new ArrayList<>();
                        if(collisions==0){
                            double localX = mainX;
                            double localY = mainY;
                            if(index > 0 && index < dictWord.size()-1){
                                for(int i=index-1; i>=0; i--){
                                    String letter = dictWord.get(i);
                                    Field toInsert = getByXY(fieldArrayList, localX, localY-30,false);
                                    if(toInsert == null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    if(getByXY(fieldArrayList, toInsert.getX()-30, toInsert.getY(),true ) != null || getByXY(fieldArrayList, toInsert.getX()+30, toInsert.getY(),true ) != null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    insertedFields.add(toInsert);
                                    toInsert.button.setText(letter);
                                    toInsert.isModified=true;
                                    toInsert.button.setDisable(true);
                                    localY-=30;
                                    insertedLetters++;
                                }
                                for(int i=index+1; i<dictWord.size()-1; i++){
                                    String letter = dictWord.get(i);
                                    Field toInsert = getByXY(fieldArrayList, localX, localY+30,false);
                                    if(toInsert == null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    if(getByXY(fieldArrayList, toInsert.getX()-30, toInsert.getY(),true ) != null || getByXY(fieldArrayList, toInsert.getX()+30, toInsert.getY(),true ) != null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    insertedFields.add(toInsert);
                                    toInsert.button.setText(letter);
                                    toInsert.isModified=true;
                                    toInsert.button.setDisable(true);
                                    localY+=30;
                                    insertedLetters++;
                                }
                            } else if(index == 0){
                                for(int i=index+1; i<=dictWord.size()-1; i++){
                                    String letter = dictWord.get(i);
                                    Field toInsert = getByXY(fieldArrayList, localX, localY+30,false);
                                    if(toInsert == null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    if(getByXY(fieldArrayList, toInsert.getX()-30, toInsert.getY(),true ) != null || getByXY(fieldArrayList, toInsert.getX()+30, toInsert.getY(),true ) != null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    insertedFields.add(toInsert);
                                    toInsert.button.setText(letter);
                                    toInsert.isModified=true;
                                    toInsert.button.setDisable(true);
                                    localY+=30;
                                    insertedLetters++;
                                }
                            } else if(index == dictWord.size()-1){
                                for(int i=index-1; i>=0; i--){
                                    String letter = dictWord.get(i);
                                    Field toInsert = getByXY(fieldArrayList, localX, localY-30,false);
                                    if(toInsert == null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    if(getByXY(fieldArrayList, toInsert.getX()-30, toInsert.getY(),true ) != null || getByXY(fieldArrayList, toInsert.getX()+30, toInsert.getY(),true ) != null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    insertedFields.add(toInsert);
                                    toInsert.button.setText(letter);
                                    toInsert.isModified=true;
                                    toInsert.button.setDisable(true);
                                    localY-=30;
                                    insertedLetters++;
                                }
                            }
                        } else if(collisions==1){
                            double localX = mainX;
                            double localY = mainY;
                            if(index > 0 && index < dictWord.size()-1){
                                for(int i=index-1; i>=0; i--){
                                    String letter = dictWord.get(i);
                                    Field toInsert = getByXY(fieldArrayList, localX-30, localY,false);
                                    if(toInsert == null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    if(getByXY(fieldArrayList, toInsert.getX(), toInsert.getY()+30,true ) != null || getByXY(fieldArrayList, toInsert.getX(), toInsert.getY()-30,true ) != null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    insertedFields.add(toInsert);
                                    toInsert.button.setText(letter);
                                    toInsert.isModified=true;
                                    toInsert.button.setDisable(true);
                                    localX-=30;
                                    insertedLetters++;
                                }
                                for(int i=index+1; i<dictWord.size()-1; i++){
                                    String letter = dictWord.get(i);
                                    Field toInsert = getByXY(fieldArrayList, localX+30, localY,false);
                                    if(toInsert == null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    if(getByXY(fieldArrayList, toInsert.getX(), toInsert.getY()+30,true ) != null || getByXY(fieldArrayList, toInsert.getX(), toInsert.getY()-30,true ) != null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    insertedFields.add(toInsert);
                                    toInsert.button.setText(letter);
                                    toInsert.isModified=true;
                                    toInsert.button.setDisable(true);
                                    localX+=30;
                                    insertedLetters++;
                                }
                            } else if(index ==0){
                                for(int i=index+1; i<=dictWord.size()-1; i++){
                                    String letter = dictWord.get(i);
                                    Field toInsert = getByXY(fieldArrayList, localX+30, localY,false);
                                    if(toInsert == null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    if(getByXY(fieldArrayList, toInsert.getX(), toInsert.getY()+30,true ) != null || getByXY(fieldArrayList, toInsert.getX(), toInsert.getY()-30,true ) != null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    insertedFields.add(toInsert);
                                    toInsert.button.setText(letter);
                                    toInsert.isModified=true;
                                    toInsert.button.setDisable(true);
                                    localX+=30;
                                    insertedLetters++;
                                }
                            } else if(index == dictWord.size()-1){
                                for(int i=index-1; i>=0; i--){
                                    String letter = dictWord.get(i);
                                    Field toInsert = getByXY(fieldArrayList, localX-30, localY,false);
                                    if(toInsert == null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    if(getByXY(fieldArrayList, toInsert.getX(), toInsert.getY()+30,true ) != null || getByXY(fieldArrayList, toInsert.getX(), toInsert.getY()-30,true ) != null){
                                        deleteFields(insertedFields);
                                        break;
                                    }
                                    insertedFields.add(toInsert);
                                    toInsert.button.setText(letter);
                                    toInsert.isModified=true;
                                    toInsert.button.setDisable(true);
                                    localX-=30;
                                    insertedLetters++;
                                }
                            }
                        } else if(collisions == 2){
                            continue;
                        }
                        if(insertedLetters == dictWord.size()-1){
                            boolean horizontal;
                            Field fieldToDetermineDirection = insertedFields.get(0);
                            if((getByXY(fieldArrayList,fieldToDetermineDirection.getX()-30,fieldToDetermineDirection.getY(),true) != null) || (getByXY(fieldArrayList,fieldToDetermineDirection.getX()+30,fieldToDetermineDirection.getY(),true) != null)) {
                                horizontal = true;
                            } else {
                                horizontal = false;
                            }

                            ArrayList<Double> coords = new ArrayList<>();
                            if(horizontal){
                                for(Field field1: insertedFields){
                                    coords.add(field1.getX());
                                }
                            } else {
                                for(Field field1: insertedFields){
                                    coords.add(field1.getY());
                                }
                            }

                            Double minVal = Collections.min(coords);
                            ArrayList<Field> existingWord = new ArrayList<>();
                            if(horizontal){
                                for(Double i = minVal; i <= 450.0; i+=30.0){
                                    if(getByXY(fieldArrayList,i, field.getY(), true) != null){
                                        existingWord.add(getByXY(fieldArrayList,i, field.getY(), true));
                                    }
                                }
                            } else {
                                for(Double i = minVal; i <= 450.0; i+=30.0){
                                    if(getByXY(fieldArrayList,field.getX(), i, true) != null){
                                        existingWord.add(getByXY(fieldArrayList,field.getX(), i, true));
                                    }
                                }
                            }
                            if(minVal > field.getX() && horizontal){
                                existingWord.add(0,field);
                            } else if(minVal > field.getY() && !horizontal){
                                existingWord.add(0,field);
                            }
                            StringBuilder word = new StringBuilder();
                            for(Field field1: existingWord){
                                word.append(field1.button.getText());
                            }
                            System.out.println(word);

                            // JAK JEST MIEJESCE TO WRZUCA LITERKE ALE NIE SPRAWDZA ZE DALEJ SA INNE I POROWNUJE DICT WORD JAKO TO KROTSZE, A SLOWO KTORE POWSTAJE JEST DLUZSZE :(
                            // DODAC USUWANIE UZYTYCH LITER PRZEZ BOTA, LOSOWANIE NASTEPNYCH DLA NIEGO
                            // DODAC PUNKTACJE I SAVE ROUND
                            /*int pointsFinal, points = 0;
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
                            */
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return false;
    }

    public void deleteFields(ArrayList<Field> fieldsToDelete){
        for(Field field : fieldsToDelete){
            field.button.setText("");
            field.isModified = false;
            field.button.setDisable(false);
        }
    }

    private void endGame(){
        timeline.stop();
    }
    /*
    public boolean insertWord(int limit){
        StringBuilder currentWord = new StringBuilder();
        for(LetterField letterField : letterFieldArrayList){
            currentWord.append(letterField.letter.getLetter());
        }

        ArrayList<String> currWordArray = new ArrayList<>();
        for(int i=0;i<currentWord.length();i++){
            currWordArray.add(String.valueOf(currentWord.charAt(i)));
        }

        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/com/example/scrabble/dictionary.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() <= limit){
                    ArrayList<String> dictWord = new ArrayList<>();
                    for(int i=0;i<line.length();i++){
                        dictWord.add(String.valueOf(line.charAt(i)));
                    }
                    if(currWordArray.containsAll(dictWord)){
                        if(findPlace(currentWord.toString(),line)){
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean findPlace(String currentWord, String dictWord){
        boolean success = false;
        ArrayList<String> dictWordArray = new ArrayList<>();
        for(int i=0;i<dictWord.length();i++){
            dictWordArray.add(String.valueOf(dictWord.charAt(i)));
        }
        ArrayList<String> curWordArray = new ArrayList<>();
        for(int i=0;i<currentWord.length();i++){
            curWordArray.add(String.valueOf(currentWord.charAt(i)));
        }
        for(Field field : fieldArrayList){
            if(curWordArray.contains(field.button.getText())){
                double mainX = field.getX();
                double mainY = field.getY();

                int counter = 1;
                for(int i=0; i < dictWord.length(); i++){
                    if(String.valueOf(dictWord.charAt(i)).equals(field.button.getText())){
                        counter++;
                    }
                }
                int index = dictWord.indexOf(field.button.getText());
                for(int i = 0; i < counter; i++){
                    if(index > 0 && index < dictWord.length()-1){
                        double localX = mainX;
                        double localY = mainY;
                        for(int j = index-1; j >=0; j--){
                            String letter = String.valueOf(dictWord.charAt(j));
                            for(Field field2 : fieldArrayList){
                                if(field2.getX() == localX-30 && field2.getY() == mainY && !field2.isModified and check collision){
                                    field2.button.setText(letter);
                                    localX-=30;
                                    success = true;
                                } else if (field2.getY() == localY-30 && field2.getX() == mainX && !field2.isModified){
                                    field2.button.setText(letter);
                                    localY-=30;
                                    success = true;
                                }
                            }
                        }
                        double localX2 = mainX;
                        double localY2 = mainY;
                        for(int j = index + 1; j <= dictWord.length(); j++){
                            String letter = String.valueOf(dictWord.charAt(j));
                            for(Field field2 : fieldArrayList){
                                if(field2.getX() == localX2+30 && field2.getY() == mainY && !field2.isModified and check collision){
                                    field2.button.setText(letter);
                                    localX2 += 30;
                                    success = true;
                                } else if (field2.getY() == localY2+30 && field2.getX() == mainX && !field2.isModified){
                                    field2.button.setText(letter);
                                    localY+=30;
                                    success = true;
                                }
                            }
                        }

                    } else if(index == 0){
                        double localX3 = mainX;
                        double localY3 = mainY;
                        for(int j = index+1; j <= dictWord.length();j++){
                            String letter = String.valueOf(dictWord.charAt(j));
                            for(Field field2 : fieldArrayList){
                                if(field2.getX() == localX3+30 && field2.getY() == mainY && !field2.isModified and check collision){
                                    field2.button.setText(letter);
                                    localX3 += 30;
                                    success = true;
                                } else if (field2.getY() == localY3+30 && field2.getX() == mainX && !field2.isModified){
                                    field2.button.setText(letter);
                                    localY3+=30;
                                    success = true;
                                }
                            }
                        }
                    } else if(index == dictWord.length()-1){
                        double localX4 = mainX;
                        double localY4 = mainY;
                        for(int j = index-1; j >=0; j--) {
                            String letter = String.valueOf(dictWord.charAt(j));
                            for (Field field2 : fieldArrayList) {
                                if (field2.getX() == localX4 - 30 && field2.getY()==mainY && !field2.isModified and check collision) {
                                    field2.button.setText(letter);
                                    localX4 -= 30;
                                    success = true;
                                } else if (field2.getY() == localY4 - 30 && field2.getX() == mainX && !field2.isModified) {
                                    field2.button.setText(letter);
                                    localY4 -= 30;
                                    success = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return success;
    }

    public boolean isAnagram(String firstWord, String secondWord) {
        char[] word1 = firstWord.replaceAll("[\\s]", "").toCharArray();
        char[] word2 = secondWord.replaceAll("[\\s]", "").toCharArray();
        Arrays.sort(word1);
        Arrays.sort(word2);
        return Arrays.equals(word1, word2);
    }
    */
    public void nextTurnGenerator() throws IOException {
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

            //playerArrayList.remove(player);
            //playerArrayList.add(player);
            //this.player = playerArrayList.get(0);
            //playerName.setText((player.getName()));
            //setLettersOfPlayer(this.player.playersLetters);
            //activateLetterFields();
            //calculateTime();
            animationTimer.stop();
            animationTimer.start();
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
    private boolean checkIfWordCorrectAfter() throws IOException {
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
            saveRound(pointsFinal);
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
        //Do zmiany wygląd i pozycja!! maks
        Label timeLabel = new Label(TimePerMove.toString());
        timeLabel.relocate(862,138);
        timeLabel.setMinWidth(75);
        timeLabel.setMinHeight(30);
        timeLabel.setTextFill(Color.color(1, 1, 1));
        timeLabel.setStyle( "-fx-font-size: 20;");
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
        this.timeLabel = timeLabel;

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

    public void calculateTime(){
        AnimationTimer animationTimer = new AnimationTimer(){
            long lastUpdate = 0;
            long convert = TimePerMove;
            @Override
            public void handle(long now) {
                if ((now - lastUpdate) >= 1000_000_000) {
                    lastUpdate = now;
                    convert = convert - 1;
                    timeLabel.setText(String.valueOf(convert));
                }
                if (convert <= 0) {
                    timeLabel.setText(String.valueOf(TimePerMove));
                    this.stop();
                }

            }
            @Override
            public void stop() {
                timeLabel.setText(TimePerMove.toString());
                convert = TimePerMove;
                passTurn();
            }
        };
        this.animationTimer = animationTimer;
        animationTimer.stop();
        animationTimer.start();
    }

    public void saveRound(Integer pointsFinal) throws IOException {

        BufferedWriter writer;
        writer = new BufferedWriter((new OutputStreamWriter(
                new FileOutputStream("src/main/resources/com/example/scrabble/gameHistory.txt", true), "UTF-8")));
        //writer.newLine();
        writer.write(player.getName()+";");
        for (Field field:playerGameFields) {
            writer.write(field.getX()+";"+field.getY()+";"+String.valueOf(field.getButton().getText())+";");
        }
        writer.write(String.valueOf(pointsFinal)+";");
        writer.newLine();
        writer.close();

    }

}