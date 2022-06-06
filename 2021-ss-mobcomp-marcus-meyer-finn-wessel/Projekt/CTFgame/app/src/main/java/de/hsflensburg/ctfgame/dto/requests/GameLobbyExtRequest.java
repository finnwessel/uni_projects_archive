package de.hsflensburg.ctfgame.dto.requests;

import de.hsflensburg.ctfgame.dto.Auth;

public class GameLobbyExtRequest extends GameLobbyRequest {

    public Auth auth;

    public GameLobbyExtRequest(int game, Auth auth) {
        super(game);
        this.auth = auth;
    }
}
