package de.hsflensburg.ctfgame.dto.responses;

import java.util.ArrayList;
import de.hsflensburg.ctfgame.dto.Participant;

public class GameLobbyExtResponse extends GameLobbyResponse {
    public GameLobbyExtResponse(int game, ArrayList<Participant> participants) {
        super(game, participants);
    }
}
