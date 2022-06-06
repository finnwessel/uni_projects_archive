package de.hsflensburg.ctfgame.dto.responses;

public class ChangeTeamResponse extends Response {
    public final String name;
    public final int team;
    public final int state;

    public ChangeTeamResponse(String name, int team, int state){
        this.name = name;
        this.team = team;
        this.state = state;
    }
}
