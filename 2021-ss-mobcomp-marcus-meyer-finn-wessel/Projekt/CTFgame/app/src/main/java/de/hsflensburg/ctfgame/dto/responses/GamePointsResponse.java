package de.hsflensburg.ctfgame.dto.responses;

import java.util.ArrayList;

import de.hsflensburg.ctfgame.dto.GameGpsPoint;

public class GamePointsResponse extends Response{
    public final int game;
    public final ArrayList<GameGpsPoint> points;
    public final boolean ended;

    public GamePointsResponse(int game, ArrayList<GameGpsPoint> points, boolean ended) {
        this.game = game;
        this.points = points;
        this.ended = ended;
    }
}
