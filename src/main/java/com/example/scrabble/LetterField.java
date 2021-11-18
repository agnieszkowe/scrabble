package com.example.scrabble;

import javafx.scene.layout.Pane;

public class LetterField extends Field{
    public LetterField(double x, double y, String color, Pane layer,String letter) {
        super(x, y, color, layer);
        this.button.setText(letter);
        this.button.setPrefSize(40,40);
    }
}
