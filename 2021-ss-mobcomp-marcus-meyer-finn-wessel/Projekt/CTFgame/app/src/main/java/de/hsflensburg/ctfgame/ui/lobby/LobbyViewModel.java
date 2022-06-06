package de.hsflensburg.ctfgame.ui.lobby;

import android.transition.Visibility;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.lokibt.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hsflensburg.ctfgame.Game;
import de.hsflensburg.ctfgame.dto.Participant;
import de.hsflensburg.ctfgame.dto.bluetooth.MessageIntent;
import de.hsflensburg.ctfgame.dto.responses.GameLobbyResponse;
import de.hsflensburg.ctfgame.repositories.GameRepository;
import de.hsflensburg.ctfgame.services.sound.SoundManager;

public class LobbyViewModel extends ViewModel {

    private final GameRepository gameRepository;
    private Timer timer;
    private int gameDurationInMinutes;
    private final MutableLiveData<String> lobbyTitle;
    private final HashMap<Participant, BluetoothDevice> lobbyDevices;
    private final ArrayList<Participant> participants;
    private final MutableLiveData<ArrayList<Participant>> participantsLiveData;
    private final MutableLiveData<Boolean> allParticipantsConnected;
    private final MutableLiveData<Boolean> isParticipantOfLobby;


    public LobbyViewModel() {
        gameRepository = GameRepository.getInstance();
        gameDurationInMinutes = 30;
        lobbyTitle = new MutableLiveData<>();
        lobbyDevices = new HashMap<>();
        participants = new ArrayList<>();
        participantsLiveData = new MutableLiveData<>();
        allParticipantsConnected = new MutableLiveData<>();
        isParticipantOfLobby = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Participant>> getPlayerNamesLiveData() {
        return participantsLiveData;
    }

    public MutableLiveData<Boolean> getAllParticipantsConnected() {
        return allParticipantsConnected;
    }

    public LiveData<String> getLobbyTitle() {
        return lobbyTitle;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void startTimer() {
        lobbyTitle.postValue("Lobby: " + Game.getInstance().getGameId());
        // Check if client is host
        if (getClient().isHost()) {
            refreshLobbyExtParticipants();
        } else {
            refreshLobbyParticipants();
        }
    }

    private void refreshLobbyParticipants() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            int lastListSize = 0;
            @Override
            public void run() {
                GameLobbyResponse res = gameRepository.getGameLobby();
                if (res != null) {
                    participants.clear();
                    participants.addAll(res.participants);
                    sortParticipantList(participants);
                    participantsLiveData.postValue(participants);
                    for (int i = 0; i < participants.size(); i++) {
                        if (participants.get(i).name.equals(Game.getInstance().getClient().name)) {
                            Game.getInstance().getClient().team = participants.get(i).team;
                        }
                    }
                    if (lastListSize != participants.size()) {
                        lastListSize = participants.size();
                        checkIfParticipantIsInLobby(participants, Game.getInstance().getClient());
                    }
                }
                //Log.d("LobbyViewModel", "Fetching Game Lobby");
            }
        }, 500L, 2500L);
    }

    private void checkIfParticipantIsInLobby(ArrayList<Participant> list, Participant participant){
        boolean isPartOfLobby = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).name.equals(participant.name)) {
                i = list.size();
                // Found participant
                isPartOfLobby = true;
            }
        }
        isParticipantOfLobby.postValue(isPartOfLobby);
    }

    private void sortParticipantList(ArrayList<Participant> participants) {
        Collections.sort(participants, new Comparator<Participant>() {
            @Override
            public int compare(Participant p1, Participant p2)
            {
                return Integer.compare(p1.team, p2.team);
            }
        });
    }

    private void refreshLobbyExtParticipants() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                GameLobbyResponse res = gameRepository.getExtendedGameLobby();
                if (res != null) {
                    participants.clear();
                    int participantsReadyCount = 0;
                    for (Participant p : res.participants) {
                        participants.add(p);
                        if (p.state == 1 && p.team != 0) {
                            participantsReadyCount++;
                        }
                    }
                    sortParticipantList(participants);
                    allParticipantsConnected.postValue(participantsReadyCount == participants.size());
                    participantsLiveData.postValue(participants);
                }
                //Log.d("LobbyViewModel", "Fetching Extended Game Lobby");
            }
        }, 500L, 500L);
    }

    public void clearTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void leaveLobby(){
        gameRepository.leaveLobby();
        if (getClient().isHost()) {
            gameRepository.endGame();
        }
    }

    public void kickPlayer(String playerName) {
        boolean alreadyKicked = false;
        Iterator<Map.Entry<Participant, BluetoothDevice>> iterator = getLobbyDevices().entrySet().iterator();
        while (iterator.hasNext() && !alreadyKicked) {
            if(iterator.next().getKey().name.equals(playerName)) {
                try{
                    getLobbyDevices().remove(iterator.next().getKey());
                } catch (Exception e) {
                    Log.d("KICK_PLAYER", ""+ e);
                }
                alreadyKicked = true;
            }
        }
        gameRepository.kickPlayer(playerName);
    }

    public void changeTeam(int teamId, String playerName) {
        boolean alreadySet = false;
        if (getLobbyDevices().entrySet().size() > 0) {
            Iterator<Map.Entry<Participant, BluetoothDevice>> iterator = getLobbyDevices().entrySet().iterator();
            while (iterator.hasNext() && !alreadySet) {
                Map.Entry<Participant, BluetoothDevice> entry = iterator.next();
                Participant p = entry.getKey();
                if(p.name.equals(playerName)) {
                    alreadySet = true;
                    getLobbyDevices().put(new Participant(p.name, teamId, p.address), entry.getValue());
                    getLobbyDevices().remove(p);
                }
            }
        }
        gameRepository.changeTeam(teamId, playerName);
    }

    public void changeState(int state, String playerName) {
        gameRepository.changeState(state, playerName);
    }

    public HashMap<Participant, BluetoothDevice> getLobbyDevices() {
        return lobbyDevices;
    }

    
    public void addLobbyDevice(final Participant participant, final BluetoothDevice device) {
        boolean alreadyInSet = false;
        if (getLobbyDevices().entrySet().size() > 0) {
            Iterator<Map.Entry<Participant, BluetoothDevice>> iterator = getLobbyDevices().entrySet().iterator();
            while (iterator.hasNext() && !alreadyInSet) {
                if(iterator.next().getValue().getAddress().equals(device.getAddress())) {
                    alreadyInSet = true;
                }
            }
        }
        if(!alreadyInSet) {
            lobbyDevices.put(participant, device);
        }
    }

    private boolean isPartOfLobby(Participant participant) {
        for (Participant p : participants) {
            if (p.token.equals(participant.token)) {
                Log.d("LobbyCheck", participant.name + " is part of this lobby");
                return true;
            }
        }
        Log.d("LobbyCheck", participant.name + " is NOT part of this lobby. Bye");
        return false;
    }

    public boolean initParticipant(Participant participant, BluetoothDevice device) {
        if (isPartOfLobby(participant)) {
            addLobbyDevice(participant, device);
            changeState(1, participant.name);
            return true;
        }
        return false;
    }

    public void setGameParticipants(ArrayList<Participant> participants) {
        Game.getInstance().setGameParticipants(participants);
    }

    public int getGameId() {
        return Game.getInstance().getGameId();
    }

    public void setGameState(int state) {
        Game.getInstance().setGameState(state);
    }

    public int getGameState() {
        return Game.getInstance().getGameState();
    }

    public Participant getClient() {
        return Game.getInstance().getClient();
    }

    public LiveData<Boolean> getIsParticipantOfLobby() {
        return isParticipantOfLobby;
    }

    public int getSettingsButtonVisibility(){
        if (Game.getInstance().getClient().isHost()) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public void setGameDurationInMinutes(int minutes) {
        gameDurationInMinutes = minutes;
        Game.getInstance().setGameDurationInMinutes(minutes);
    }

    public int getGameDurationInMinutes() {
        return gameDurationInMinutes;
    }

}