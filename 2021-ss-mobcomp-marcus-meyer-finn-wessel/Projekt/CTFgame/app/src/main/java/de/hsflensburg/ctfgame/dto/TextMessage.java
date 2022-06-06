package de.hsflensburg.ctfgame.dto;

public class TextMessage {

    public boolean success;
    public String text;

    public TextMessage(boolean success, String text) {
        this.success = success;
        this.text = text;
    }
}
