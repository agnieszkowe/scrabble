package com.example.scrabble;

import javafx.scene.layout.Pane;

public class Field3xWordBonus extends Field{

    public Field3xWordBonus(double x, double y, String pathStatic, Pane layer) {
        super(x, y, pathStatic, layer);
        setWordBonus(3);
    }
}
