package de.hsflensburg.ctfgame.dto.requests;

public class JoinGameRequest {
    public final int game;
    public final String name;
    public final int team;

    public JoinGameRequest(int game, String name, int team){
        this.game = game;
        this.name = name;
        this.team = team;
    }
}
