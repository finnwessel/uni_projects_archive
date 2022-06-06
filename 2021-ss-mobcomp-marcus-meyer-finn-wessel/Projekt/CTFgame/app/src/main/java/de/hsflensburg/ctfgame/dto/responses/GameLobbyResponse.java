package de.hsflensburg.ctfgame.dto.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import de.hsflensburg.ctfgame.dto.Participant;

public class GameLobbyResponse extends Response{
    @SerializedName("game")
    public final int game;
    @SerializedName("participants")
    public final ArrayList<Participant> participants;

    public GameLobbyResponse(int game, ArrayList<Participant> participants) {
        this.game = game;
        this.participants = participants;
    }
}
