package de.hsflensburg.ctfgame.ui.prep;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

import de.hsflensburg.ctfgame.Game;
import de.hsflensburg.ctfgame.dto.GpsPoint;
import de.hsflensburg.ctfgame.dto.Participant;
import de.hsflensburg.ctfgame.dto.TextMessage;
import de.hsflensburg.ctfgame.dto.responses.RegisterGameResponse;
import de.hsflensburg.ctfgame.repositories.GameRepository;

public class PrepViewModel extends ViewModel {

    private static final int MIN_HOST_NAME_LENGTH = 3;

    private final GameRepository gameRepository;
    private String gameName;
    private ArrayList<GpsPoint> gpsPoints;
    private final MutableLiveData<ArrayList<GpsPoint>> gpsPointsLiveData;
    public MutableLiveData<TextMessage> message;
    public final MutableLiveData<Boolean> createGameBtnEnabled;


    public PrepViewModel() {
        gameRepository = GameRepository.getInstance();
        gpsPoints = new ArrayList<>();
        gpsPointsLiveData = new MutableLiveData<>();
        message = new MutableLiveData<>();
        createGameBtnEnabled = new MutableLiveData<>();
    }

    public void createGameLobby(String name) {
        Game.getInstance().setClient(new Participant(name, 1, 1, true));
        RegisterGameResponse res = gameRepository.createGameLobby(name, gpsPoints);
        if (res == null || !res.isSuccess()) {
            message.postValue(new TextMessage(false, "Failed to create lobby!"));
        } else {
            message.postValue(new TextMessage(true, "New lobby created!"));
        }
    }

    public ArrayList<GpsPoint> getGpsPoints() {
        return gpsPoints;
    }

    public MutableLiveData<ArrayList<GpsPoint>> getGpsPointsLiveData(){
        return gpsPointsLiveData;
    }

    public void addOrRemoveGpsPoint(GpsPoint clickedPoint) {
        boolean pointRemoved = false;
        ArrayList<GpsPoint> points = new ArrayList<>();
        for (GpsPoint gpsPoint: gpsPoints) {
            if (Math.abs(gpsPoint.longitude - clickedPoint.longitude) < 0.001 && Math.abs(gpsPoint.latitude - clickedPoint.latitude) < 0.001) {
                pointRemoved = true;
            } else {
                points.add(gpsPoint);
            }
        }
        if (!pointRemoved) {
           points.add(clickedPoint);
        }
        gpsPoints = points;
        gpsPointsLiveData.postValue(gpsPoints);
        checkIfEnableCreateGameButton();
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
        checkIfEnableCreateGameButton();
    }

    private void checkIfEnableCreateGameButton() {
        createGameBtnEnabled.setValue(this.gameName != null && this.gameName.length() >= MIN_HOST_NAME_LENGTH && gpsPoints.size() > 1);
    }
}