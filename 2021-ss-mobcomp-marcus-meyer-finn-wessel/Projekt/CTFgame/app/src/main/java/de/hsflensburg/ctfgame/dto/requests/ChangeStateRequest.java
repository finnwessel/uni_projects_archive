package de.hsflensburg.ctfgame.dto.requests;

import de.hsflensburg.ctfgame.dto.Auth;

public class ChangeStateRequest {
    public final int game;
    public final String name;
    public final int state;
    public final Auth auth;

    public ChangeStateRequest(int game, String name, int state, Auth auth) {
        this.game = game;
        this.name = name;
        this.state = state;
        this.auth = auth;
    }
}
