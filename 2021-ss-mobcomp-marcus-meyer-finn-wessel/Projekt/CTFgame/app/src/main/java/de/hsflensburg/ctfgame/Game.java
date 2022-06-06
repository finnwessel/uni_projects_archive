package de.hsflensburg.ctfgame;

import android.location.Location;
import android.util.Log;
import android.util.TimeUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hsflensburg.ctfgame.dto.Participant;

public class Game {

    private static Game instance;
    private int gameId;
    private int gameState;
    private int conquestsCount;
    private final ArrayList<Location> recordedLocations = new ArrayList<Location>();
    private long gameStartTimeInMillis;
    private long expectedGameEndTimeInMillis;
    private long gameEndTimeInMillis;

    private int gameDuration = 3600;

    private ArrayList<Participant> gameParticipants = new ArrayList<>();

    private Participant client;

    private Game() {
        gameState = GameState.NOT_RUNNING;
        gameId = -1;
        conquestsCount = 0;
        client = new Participant(null, -1, 0, false);
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void resetInstance() {
        instance = new Game();
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int id) {
        gameId = id;
    }

    public void setGameState(int state) { gameState = state; }
    public int getGameState() {
        return gameState;
    }

    public void startGame() {
        gameStartTimeInMillis = System.currentTimeMillis();
        expectedGameEndTimeInMillis = gameStartTimeInMillis + (gameDuration * 60000);
    }

    public void endGame() {
        gameEndTimeInMillis = System.currentTimeMillis();
    }

    public long getRemainingTimeInMinutes() {
        return (expectedGameEndTimeInMillis - System.currentTimeMillis()) / 60000;
    }

    public long getRemainingTimeInSeconds() {
        return (expectedGameEndTimeInMillis - System.currentTimeMillis()) / 1000;
    }

    public boolean isGameTimeOver() {
        return System.currentTimeMillis() > expectedGameEndTimeInMillis;
    }

    public long getPlayedTimeInMinutes() {
        long timeDiff = gameEndTimeInMillis - gameStartTimeInMillis;
        return TimeUnit.MILLISECONDS.toMinutes(timeDiff);
    }

    public void increaseConquestsCount(){
        conquestsCount++;
    }

    public int getConquestsCount() {
        return conquestsCount;
    }

    public void addRecordedLocation(Location location) {
        recordedLocations.add(location);
    }

    public float distanceCovered() { // ToDo: Make sure Array does not change when calculating
        float distance = 0;
        for (int i = 0; i < recordedLocations.size(); i++) {
            if (i + 1 < recordedLocations.size()) {
                //Log.d("Distance Between: ", "" + i + " " + (i + 1) + " Meter: " + recordedLocations.get(i).distanceTo(recordedLocations.get(i+1)));
                distance += recordedLocations.get(i).distanceTo(recordedLocations.get(i+1));
            }
        }
        return distance;
    }

    public ArrayList<Participant> getGameParticipants() {
        return gameParticipants;
    }

    public void setGameParticipants(ArrayList<Participant> gameParticipants) {
        this.gameParticipants = gameParticipants;
    }

    public Participant getClient() {
        return client;
    }

    public void setClient(Participant p) {
        client = p;
    }

    public int getGameDurationInMinutes() {
        return gameDuration;
    }

    public void setGameDurationInMinutes(int gameDuration) {
        this.gameDuration = gameDuration;
    }

}
