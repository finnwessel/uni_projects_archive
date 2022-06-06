package de.hsflensburg.ctfgame.ui.select;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.hsflensburg.ctfgame.repositories.GameRepository;

public class SelectViewModel extends ViewModel {

    public static final int MIN_LOBBY_ID_LENGTH = 1;
    public static final int MIN_PLAYER_NAME_LENGTH = 3;
    public static final int MAX_LOBBY_ID_LENGTH = 8;
    public static final int MAX_PLAYER_NAME_LENGTH = 15;

    private final GameRepository gameRepository;
    private String playerName;
    private int teamId;
    private int gameId;

    private final MutableLiveData<Boolean> joinButtonEnabled;
    private final MutableLiveData<Integer> gameIdLiveData;
    private final MutableLiveData<String> playerNameLiveData;

    public SelectViewModel() {
        gameRepository = GameRepository.getInstance();
        playerName = "";
        teamId = 0;
        gameId = 0;
        joinButtonEnabled = new MutableLiveData<>();
        gameIdLiveData = new MutableLiveData<>();
        playerNameLiveData = new MutableLiveData<>();
    }

    public void joinGameLobby() {
        gameRepository.joinGameLobby(playerName, gameId, teamId);
    }

    public void setTeamId(int id) {
        teamId = id;
    }

    public void setGameId(int id) {
        gameId = id;
        gameIdLiveData.postValue(id);
        checkIfEnableJoinButton();
    }

    public void setPlayerName(String name) {
        playerName = name;
        playerNameLiveData.postValue(name);
        checkIfEnableJoinButton();
    }

    private void checkIfEnableJoinButton() {
        joinButtonEnabled.postValue(String.valueOf(gameId).length() >= SelectViewModel.MIN_LOBBY_ID_LENGTH && playerName.length() >= SelectViewModel.MIN_PLAYER_NAME_LENGTH);
    }
}