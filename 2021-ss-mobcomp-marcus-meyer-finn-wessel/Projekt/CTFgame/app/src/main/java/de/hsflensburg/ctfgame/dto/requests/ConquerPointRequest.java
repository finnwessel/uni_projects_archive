package de.hsflensburg.ctfgame.dto.requests;

public class ConquerPointRequest {
    public final int point;
    public final int team;

    public ConquerPointRequest(int point, int team) {
        this.point = point;
        this.team = team;
    }
}
