package de.hsflensburg.ctfgame.dto;

public class GameGpsPoint extends GpsPoint {

    public int id;
    public int team;

    public GameGpsPoint(int id, int team, double latitude, double longitude) {
        super(latitude, longitude);
        this.id = id;
        this.team = team;
    }
}
