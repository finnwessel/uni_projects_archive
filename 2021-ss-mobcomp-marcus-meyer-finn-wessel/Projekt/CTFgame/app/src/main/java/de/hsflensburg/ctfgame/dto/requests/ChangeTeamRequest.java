package de.hsflensburg.ctfgame.dto.requests;

import de.hsflensburg.ctfgame.dto.Auth;

public class ChangeTeamRequest {
    public final int game;
    public final String name;
    public final int team;
    public final Auth auth;

    public ChangeTeamRequest(int game, String name, int team, Auth auth) {
        this.game = game;
        this.name = name;
        this.team = team;
        this.auth = auth;
    }
}
