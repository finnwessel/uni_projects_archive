package de.hsflensburg.ctfgame.ui.game;

import android.location.Location;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lokibt.bluetooth.BluetoothDevice;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import de.hsflensburg.ctfgame.Game;
import de.hsflensburg.ctfgame.dto.GameGpsPoint;
import de.hsflensburg.ctfgame.dto.GameListDialogData;
import de.hsflensburg.ctfgame.dto.Participant;
import de.hsflensburg.ctfgame.dto.responses.GamePointsResponse;
import de.hsflensburg.ctfgame.repositories.GameRepository;

public class GameViewModel extends ViewModel {

    private final GameRepository gameRepository;

    private Timer timer;
    private int lastGamePointIndex;
    private Location playerLocation;
    private final ArrayList<Location> pointsLocation;
    private ArrayList<GameGpsPoint> points;
    private final MutableLiveData<ArrayList<GameGpsPoint>> pointsLiveData;
    private final ArrayList<Participant> nearbyPlayers;
    private final MutableLiveData<ArrayList<Participant>> nearbyPlayersLiveData;
    public final ArrayList<String> nearbyDevicesMAC;
    private final MutableLiveData<Boolean> runDiscovery;
    private final MutableLiveData<Boolean> guardingWarningVisible;
    private final MutableLiveData<Boolean> ingestiblePointDialogVisibleLiveData;
    private final MutableLiveData<Boolean> gameEndedLiveData;
    private final MutableLiveData<GameListDialogData> gameListDialogLiveData;
    private final MutableLiveData<String> gameTimerStringLiveData;

    public GameViewModel() {
        gameRepository = GameRepository.getInstance();
        pointsLocation = new ArrayList<>();
        points = new ArrayList<>();
        pointsLiveData = new MutableLiveData<>();
        nearbyPlayers = new ArrayList<>();
        nearbyPlayersLiveData = new MutableLiveData<>();
        nearbyDevicesMAC = new ArrayList<>();
        runDiscovery = new MutableLiveData<>();
        guardingWarningVisible = new MutableLiveData<>();
        ingestiblePointDialogVisibleLiveData = new MutableLiveData<>();
        gameEndedLiveData = new MutableLiveData<>();
        gameListDialogLiveData = new MutableLiveData<>();
        gameTimerStringLiveData = new MutableLiveData<>();
    }

    public void init(){
        Game.getInstance().startGame();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                calculateAndDrawRemainingGameTime();
                fetchGamePoints();
                checkPlayerPosition();
                gameOverCheck();
            }
        }, 500L, 1000L);
    }

    public void clear() {
        Game.getInstance().endGame();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void calculateAndDrawRemainingGameTime() {
        int remainingMinutes = (int) Game.getInstance().getRemainingTimeInMinutes();
        int remainingSeconds = (int) Game.getInstance().getRemainingTimeInSeconds();
        if (remainingMinutes > 60) {
            gameTimerStringLiveData.postValue(String.format("%d:%d:%d", remainingMinutes / 60, remainingMinutes % 60, remainingSeconds % 60));
        } else {
            gameTimerStringLiveData.postValue(String.format("0:%d:%d", remainingMinutes, remainingSeconds % 60));
        }
    }


    private void initPointsLocation() {
        pointsLocation.clear();
        for(GameGpsPoint point : points) {
            Location pointLocation = new Location("gamePointLocation");
            pointLocation.setLatitude(point.latitude);
            pointLocation.setLongitude(point.longitude);
            pointsLocation.add(pointLocation);
        }
    }

    private void fetchGamePoints() {
        GamePointsResponse res = gameRepository.getGamePoints();
        if (res != null) {
            points = res.points;
            pointsLiveData.postValue(res.points);
            initPointsLocation();
            checkConqueredPoints();
            if (res.ended) {
                Game.getInstance().endGame();
                gameEndedLiveData.postValue(true);
            }
        }
    }

    private void checkPlayerPosition() {
        float distance = 0f;
        boolean foundNearby = false;
        if(playerLocation != null) {
            int pointsLocationSize = pointsLocation.size();
            for (int i = 0; i < pointsLocationSize; i++) {
                distance = playerLocation.distanceTo(pointsLocation.get(i));
                Log.d("GameViewModel", "Distance: " + distance);
                if (distance <= 10) {
                    foundNearby = true;
                    gamePointNearby(i);
                    i = pointsLocationSize;
                }
            }
            if(!foundNearby) {
                lastGamePointIndex = -1;
                ingestiblePointDialogVisibleLiveData.postValue(false);
                guardingWarningVisible.postValue(false);
            }
        }
    }
    int campCounter = 0;
    private void gamePointNearby(int i) {
        if (lastGamePointIndex != i && points.get(i).team != getClient().team) {
            lastGamePointIndex = i;
            campCounter = 0;
            ingestiblePointDialogVisibleLiveData.postValue(true);
        } else {
            if (campCounter >= 20) {
                guardingWarningVisible.postValue(points.get(i).team == getClient().team);
                Log.d("GameViewModel", "Chillst aber ganz schön lange hier!");
            } else {
                campCounter++;
                Log.d("GameViewModel", "CampCounter: " + campCounter);
            }
        }
    }

    public void withdrawPoint() {
        if (lastGamePointIndex >= 0) {
            gameRepository.conquerPoint(points.get(lastGamePointIndex).id, 0);
        }
    }

    public void conquerGamePoint() {
        GameGpsPoint point = points.get(lastGamePointIndex);
        int teamId = getClient().team;
        // Game Point is not conquered yet
        if (point.team == 0) {
            gameRepository.conquerPoint(point.id, teamId);
            Game.getInstance().increaseConquestsCount();
        } else if (point.team != teamId) { // Other team holds game point
            runDiscovery.postValue(true);
            Log.d("GameViewModel", "Scan for nearby players");
        }
    }

    // Check if team conquered all game points and end game if so
    private void checkConqueredPoints() {
        int teamId = getClient().team;
        int pointsCount = points.size();
        int teamPointsCount = 0;
        if(teamId != 0) {
            for (int i = 0; i < pointsCount; i++) {
                if (points.get(i).team == teamId) {
                    teamPointsCount++;
                }
            }
        }
        if(teamPointsCount == pointsCount) {
            gameRepository.endGame();
        }
        Log.d("GameViewModel", "TPC: " + teamPointsCount + " PC: " + pointsCount + " PS: " + points.size());
    }

    private void gameOverCheck() {
        if(Game.getInstance().isGameTimeOver()){
            gameRepository.endGame();
            //clear();
        }
    }

    public ArrayList<GameGpsPoint> getGameGpsPoints(){
        return points;
    }

    public LiveData<Boolean> getIngestiblePointDialogVisibleLiveData(){
        return ingestiblePointDialogVisibleLiveData;
    }

    public LiveData<Boolean> getGameEndedLiveData(){
        return gameEndedLiveData;
    }

    public LiveData<ArrayList<GameGpsPoint>> getGameGpsPointsLiveData(){
        return pointsLiveData;
    }

    public void addRecordedLocation(Location location) {
        playerLocation = location;
        Game.getInstance().addRecordedLocation(location);
    }

    public float distanceCovered() {
        return Game.getInstance().distanceCovered();
    }

    public void leaveGame() {
        gameRepository.leaveLobby();
    }

    public LiveData<Boolean> getGuardingWarningVisible() {
        return guardingWarningVisible;
    }

    public MutableLiveData<Boolean> getRunDiscovery() {
        return runDiscovery;
    }

    public LiveData<GameListDialogData> getGameListDialogLiveData() {
        return gameListDialogLiveData;
    }

    public ArrayList<Participant> getParticipants() {
        return Game.getInstance().getGameParticipants();
    }

    public Participant getClient() {
        return Game.getInstance().getClient();
    }

    public void deviceFound(BluetoothDevice device) {
        nearbyDevicesMAC.add(device.getAddress());
    }

    public void determinePointWinner() {
        int winnerTeam = -1;
        int enemyCount = 0, teamCount = 1;
        nearbyPlayers.clear();
        nearbyPlayers.add(Game.getInstance().getClient());
        for (Participant participant : getParticipants()) {
            for (String macAddress : nearbyDevicesMAC) {
                if (participant.address.equals(macAddress)) {
                    nearbyPlayers.add(participant);
                    if (participant.team == Game.getInstance().getClient().team) {
                        teamCount++;
                    } else {
                        enemyCount++;
                    }
                }
            }
        }
        if (lastGamePointIndex != -1) {
            if (enemyCount < teamCount) {
                gameRepository.conquerPoint(points.get(lastGamePointIndex).id, Game.getInstance().getClient().team);
                winnerTeam = Game.getInstance().getClient().team;
            } else if (enemyCount == teamCount) {
                gameRepository.conquerPoint(points.get(lastGamePointIndex).id, -1);
            } else {
                if(Game.getInstance().getClient().team == 1) {
                    winnerTeam = 2;
                } else {
                    winnerTeam = 1;
                }
            }
        }
        nearbyDevicesMAC.clear();
        gameListDialogLiveData.postValue(new GameListDialogData(true, winnerTeam, Game.getInstance().getClient().team, enemyCount, teamCount, nearbyPlayers));
    }

    public int getGameDurationInMinutes() {
        return Game.getInstance().getGameDurationInMinutes();
    }

    public LiveData<ArrayList<Participant>> getNearbyPlayerLiveData() {
        return nearbyPlayersLiveData;
    }

    public LiveData<String> getGameTimerStringLiveData() {
        return gameTimerStringLiveData;
    }
}