package com.example.encryption2.util;

import javafx.scene.control.TextArea;

public class PrintLogger {

    public static final String RESET = "#FFFFFF";
    public static final String BLACK = "#FFFFFF";
    public static final String RED = "FF0000";
    TextArea textArea;

    public PrintLogger(TextArea textArea) {
        this.textArea = textArea;
    }

    public void println(String string) {
        textArea.appendText("EncrypterLogger>>" + string + "\n");
    }

    public void print(String string) {
        textArea.appendText("EncrypterLogger>>" + string);
    }

    public void printBackSpace(){
        textArea.appendText("\n");
    }

    public void printlnClean(String string){
        textArea.appendText(string + "\n");
    }

    public void printClean(String string){
        textArea.appendText(string);
    }
}
