package com.example.scrabble;

import javafx.scene.layout.Pane;

public class Field2xLetterBonus extends Field{

    public Field2xLetterBonus(double x, double y, String pathStatic, Pane layer) {
        super(x, y, pathStatic, layer);
        setLetterBonus(2);
    }
}
