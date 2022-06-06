package de.hsflensburg.ctfgame.dto.bluetooth;

import java.util.ArrayList;
import de.hsflensburg.ctfgame.dto.Participant;

public class GameData {
    public int gameID;
    public int state;
    public int gameDurationInMinutes;
    public ArrayList<Participant> participants;

    public GameData(int gameID, int state, int durationInMinutes) {
        this.gameID = gameID;
        this.state = state;
        participants = new ArrayList<>();
        this.gameDurationInMinutes = durationInMinutes;
    }
}
