package com.example.scrabble;

import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Field {

    private int wordBonus = 0;

    private int letterBonus = 0;

    private javafx.geometry.Dimension2D dimension;

    private Pane layer;

    private ImageView imageView;

    private Image imageStatic;

    private double x;

    private double y;

    public Field(double x, double y, String pathStatic, Pane layer) {
        this.layer = layer;
        loadImage(pathStatic);
        setLocation(x, y);
        imageStatic = new Image(String.valueOf(getClass().getResource(pathStatic)));
        dimension = new javafx.geometry.Dimension2D(imageStatic.getWidth(), imageStatic.getHeight());
        //addToLayer();
    }

    public Dimension2D getDimension() {
        return dimension;
    }

    public void setDimension(Dimension2D dimension) {
        this.dimension = dimension;
    }

    public Pane getLayer() {
        return layer;
    }

    public void setLayer(Pane layer) {
        this.layer = layer;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public Image getImageStatic() {
        return imageStatic;
    }

    public void setImageStatic(Image imageStatic) {
        this.imageStatic = imageStatic;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    public int getWordBonus() {
        return wordBonus;
    }

    public void setWordBonus(int wordBonus) {
        this.wordBonus = wordBonus;
    }

    public int getLetterBonus() {
        return letterBonus;
    }

    public void setLetterBonus(int letterBonus) {
        this.letterBonus = letterBonus;
    }

    public void addToLayer() {
        this.layer.getChildren().add(this.imageView);
    }
    public void removeFromLayer() {
        this.layer.getChildren().remove(this.imageView);
    }

    private void loadImage(String path) {
        imageView = new ImageView(String.valueOf(getClass().getResource(path)));
        this.imageView.relocate(this.getX(), this.getY());
        this.addToLayer();
    }

    public void setLocation(double x, double y) {
        setX(x);
        setY(y);
        imageView.relocate(x, y);
    }

}
