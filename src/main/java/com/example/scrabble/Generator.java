package com.example.scrabble;

import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    public Generator(Pane layer){
        int x = 0;
        int y = 0;
//        for (int i = 0; i < 15; i++) {
//            for (int j = 0; j < 15; j++) {
//                if (((j == 14 || j ==0) && (i == 14 || i ==0)) || ((i == 14 || i ==0) && (j == 7)) || ((j == 14 || j ==0) && (i == 7))) {
//                    Field field = new Field3xWordBonus(x, y, "red.jpg", layer);
//                }else if ((j==i || i + j == 14) && ){
//                    Field field = new Field2xWordBonus(x, y, "test.jpg", layer);
//                } else {
//                    Field field = new Field(x,y, "greyoyellow.jpg",layer);
//                }
//                x += 32;
//            }
//            x = 0;
//            y += 32;
//        }
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("src/main/resources/com/example/scrabble/pixil-frame-0.png"));
        } catch (IOException e) {
        }
        for (int h = 0; h<img.getHeight(); h++) {
            for (int w = 0; w < img.getWidth(); w++) {
                int currentPixel = img.getRGB(h, w);

                if (currentPixel == tripleWord){
                    new Field3xWordBonus(h*30,w*30,"red.jpg",layer);
                }
                if (currentPixel == doubleWord){
                    new Field2xWordBonus(h*30,w*30,"blue.png",layer);
                }
                if (currentPixel == doubleLetter){
                    new Field3xWordBonus(h*30,w*30,"purple.png",layer);
                }
                if (currentPixel == tripleLetter){
                    new Field3xWordBonus(h*30,w*30,"test.jpg",layer);
                }
                if (currentPixel == middle){
                    new Field3xWordBonus(h*30,w*30,"green.png",layer);
                }
                if (currentPixel == casual){
                    new Field3xWordBonus(h*30,w*30,"greyoyellow.png",layer);
                }

            }
        }
    }
}
