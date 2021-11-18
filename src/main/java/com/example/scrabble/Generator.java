package com.example.scrabble;

import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Generator {
    public int getTripleWord() {
        return tripleWord;
    }

    public int getDoubleWord() {
        return doubleWord;
    }

    public int getDoubleLetter() {
        return doubleLetter;
    }

    public int getTripleLetter() {
        return tripleLetter;
    }

    public int getMiddle() {
        return middle;
    }

    public int getCasual() {
        return casual;
    }

    public final int tripleWord = new Color(233,30,99).getRGB();
    public final int doubleWord = new Color(0,255,255).getRGB();
    final int doubleLetter = new Color(244,67,54).getRGB();
    final int tripleLetter = new Color(0,128,0).getRGB();
    final int middle = new Color(0,0,128).getRGB();
    final int casual = new Color(192,192,192).getRGB();


    public ArrayList<Field> mapGenerator(Pane layer){
        int x = 0;
        int y = 0;
        ArrayList<Field> buttonArrayList = new ArrayList<Field>();
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("src/main/resources/com/example/scrabble/pixil-frame-0.png"));
        } catch (IOException e) {
        }
        for (int h = 0; h<img.getHeight(); h++) {
            for (int w = 0; w < img.getWidth(); w++) {
                int currentPixel = img.getRGB(h, w);

                if (currentPixel == tripleWord){
                    buttonArrayList.add(new Field3xWordBonus(h*30,w*30,"#E91E63",layer));
                }
                if (currentPixel == doubleWord){
                    buttonArrayList.add(new Field2xWordBonus(h*30,w*30,"#00FFFF",layer));
                }
                if (currentPixel == doubleLetter){
                    buttonArrayList.add(new Field2xLetterBonus(h*30,w*30,"#F44336",layer));
                }
                if (currentPixel == tripleLetter){
                    buttonArrayList.add(new Field3xLetterBonus(h*30,w*30,"#008000",layer));
                }
                if (currentPixel == middle){
                    buttonArrayList.add(new Field(h*30,w*30,"#000080",layer));
                }
                if (currentPixel == casual){
                    buttonArrayList.add(new Field(h*30,w*30,"#c0c0c0",layer));
                }

            }
        }
        return buttonArrayList;
    };

    public ArrayList<LetterField> LetterFieldsGenerator(Pane layer){
        ArrayList<LetterField> buttonArrayList = new ArrayList<LetterField>();
        int x = 0;
        int y = 475;
        for (int i = 0; i < 7; i++) {
            x+=45;
            buttonArrayList.add(new LetterField(x,y,"#FFFF00",layer,"A"));
        }
        return buttonArrayList;

    }

    public ArrayList<Letter> LetterGenerator(){
        for (int i = 0; i < 26; i++) {

        }
    }



}
