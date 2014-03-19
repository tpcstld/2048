package com.tpcstld.twozerogame;

/**
 * Created by tpcstld on 3/18/14.
 */
public class TextBox {
    private int width;
    private int length;
    private int textFormat;
    private int padding = 5;
    private float textSize;
    private String title;
    private String text;


    public TextBox(String text, float textSize) {
        this.text = text;
        this.textSize = textSize;
    }

    public TextBox(String title, String text, float textSize) {
        this.title = title;
        this.text = text;
        this.textSize = textSize;
    }

    private void calculateDimensions() {

    }
}

