package com.example.scrabble;

import javafx.scene.layout.Pane;

public class Field3xLetterBonus extends Field{

    public Field3xLetterBonus(double x, double y, String pathStatic, Pane layer) {
        super(x, y, pathStatic, layer);
        setLetterBonus(3);
    }
}
