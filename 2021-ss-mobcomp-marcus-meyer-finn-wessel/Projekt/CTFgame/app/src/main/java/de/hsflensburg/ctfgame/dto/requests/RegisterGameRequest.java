package de.hsflensburg.ctfgame.dto.requests;

import java.util.ArrayList;
import de.hsflensburg.ctfgame.dto.GpsPoint;

public class RegisterGameRequest {
    public final String name;
    public final ArrayList<GpsPoint> points;

    public RegisterGameRequest(String name, ArrayList<GpsPoint> points) {
        this.name = name;
        this.points = points;
    }
}
