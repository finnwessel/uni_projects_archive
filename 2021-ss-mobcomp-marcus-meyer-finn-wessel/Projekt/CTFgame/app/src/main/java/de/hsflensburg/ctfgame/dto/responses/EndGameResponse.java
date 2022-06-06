package de.hsflensburg.ctfgame.dto.responses;

public class EndGameResponse extends Response {
    public final int game;

    public EndGameResponse(int game) {
        this.game = game;
    }
}
