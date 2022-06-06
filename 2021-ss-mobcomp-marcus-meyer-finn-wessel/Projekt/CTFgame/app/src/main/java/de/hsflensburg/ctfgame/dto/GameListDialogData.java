package de.hsflensburg.ctfgame.dto;

import java.util.ArrayList;

public class GameListDialogData {

    private final boolean visible;

    public int winnerTeamID;
    public int ownTeamID;
    public int enemyTeamCount;
    public int ownTeamCount;
    public ArrayList<Participant> nearbyPlayers;

    public GameListDialogData(boolean visible, int winnerTeamID, int ownTeamID, int enemyTeamCount, int ownTeamCount, ArrayList<Participant> nearbyPlayers) {
        this.visible = visible;
        this.winnerTeamID = winnerTeamID;
        this.ownTeamID = ownTeamID;
        this.enemyTeamCount = enemyTeamCount;
        this.ownTeamCount = ownTeamCount;
        this.nearbyPlayers = nearbyPlayers;
    }

    public boolean isDialogVisible() {
        return visible;
    }
}
