package de.hsflensburg.ctfgame.dto.responses;

public abstract class Response {
    public int statusCode;

    public boolean isSuccess() {
        return this.statusCode == 200;
    }
}
