package de.hsflensburg.ctfgame.repositories;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hsflensburg.ctfgame.Game;
import de.hsflensburg.ctfgame.dto.Auth;
import de.hsflensburg.ctfgame.dto.GpsPoint;
import de.hsflensburg.ctfgame.dto.Participant;
import de.hsflensburg.ctfgame.dto.requests.ChangeStateRequest;
import de.hsflensburg.ctfgame.dto.requests.ChangeTeamRequest;
import de.hsflensburg.ctfgame.dto.requests.ConquerPointRequest;
import de.hsflensburg.ctfgame.dto.requests.EndGameRequest;
import de.hsflensburg.ctfgame.dto.requests.GameLobbyExtRequest;
import de.hsflensburg.ctfgame.dto.requests.GameLobbyRequest;
import de.hsflensburg.ctfgame.dto.requests.GamePointsRequest;
import de.hsflensburg.ctfgame.dto.requests.JoinGameRequest;
import de.hsflensburg.ctfgame.dto.requests.RegisterGameRequest;
import de.hsflensburg.ctfgame.dto.responses.ChangeStateResponse;
import de.hsflensburg.ctfgame.dto.responses.ChangeTeamResponse;
import de.hsflensburg.ctfgame.dto.responses.ConquerPointResponse;
import de.hsflensburg.ctfgame.dto.responses.EndGameResponse;
import de.hsflensburg.ctfgame.dto.responses.GameLobbyExtResponse;
import de.hsflensburg.ctfgame.dto.responses.GameLobbyResponse;
import de.hsflensburg.ctfgame.dto.responses.GamePointsResponse;
import de.hsflensburg.ctfgame.dto.responses.JoinGameResponse;
import de.hsflensburg.ctfgame.dto.responses.RegisterGameResponse;
import de.hsflensburg.ctfgame.services.http.HttpResponse;
import de.hsflensburg.ctfgame.services.http.HttpService;
import de.hsflensburg.ctfgame.services.http.methods.HttpPost;

public class GameRepository {
    private boolean debug = false;
    private static GameRepository instance;
    private final Game game;
    private final Gson gson;

    private GameRepository() {
        game = Game.getInstance();
        gson = new Gson();
    }

    public static GameRepository getInstance() {
        if (instance == null) {
            instance = new GameRepository();
        }
        return instance;
    }

    public RegisterGameResponse createGameLobby(String playerName, ArrayList<GpsPoint> gpsPoints) {
        RegisterGameRequest body = new RegisterGameRequest(playerName, gpsPoints);

        HttpPost httpCallPost = new HttpPost();
        httpCallPost.setUrl("https://ctf.letorbi.de/register");
        httpCallPost.setBody(gson.toJson(body));

        RegisterGameResponse res = null;
        try {
            res = new HttpService<RegisterGameResponse>(RegisterGameResponse.class).execute(httpCallPost).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        game.setGameId(res.game);
        game.setClient(new Participant(playerName, 1, 1, true, res.token));
        if (debug) Log.d("GameRepository: ", "Created new Lobby with params | Name: " + playerName + " ID: " + res.game + " Token: " + res.token);
        return res;
    }

    public JoinGameResponse joinGameLobby(String playerName, int gameId, int teamId) {
        JoinGameRequest body = new JoinGameRequest(gameId, playerName, teamId);

        HttpPost httpCallPost = new HttpPost();
        httpCallPost.setUrl("https://ctf.letorbi.de/join");
        httpCallPost.setBody(gson.toJson(body));
        if (debug) Log.d("GameRepository: ", gson.toJson(body));

        JoinGameResponse res = null;
        try {
            res = new HttpService<JoinGameResponse>(JoinGameResponse.class).execute(httpCallPost).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            if (debug) Log.d("GameRepository: ", "Failed to join lobby! Could not get a valid response");
        }

        if (res != null && res.token != null) {
            game.setGameId(gameId);
            game.setClient(new Participant(playerName, teamId, 0, false, res.token));
            if (debug) Log.d("GameRepository: ", "Joined Lobby with params | Name: " + playerName + " Token: " + res.token);
        } else {
            if (debug) Log.d("GameRepository: ", "Failed to join lobby! Could not decode Json object");
        }

        return res;
    }

    public void leaveLobby() {
        ChangeTeamResponse res = changeTeam(-1, game.getClient().name);
        game.resetInstance();
    }

    public void kickPlayer(String playerName) {
        ChangeTeamResponse res = changeTeam(-1, playerName);
    }

    public ChangeTeamResponse changeTeam(int teamId, String playerName) {
        Auth auth = new Auth(game.getClient().name, game.getClient().token);
        ChangeTeamRequest body = new ChangeTeamRequest(game.getGameId(), playerName, teamId, auth);

        HttpPost httpCallPost = new HttpPost();
        httpCallPost.setUrl("https://ctf.letorbi.de/change");
        httpCallPost.setBody(gson.toJson(body));

        ChangeTeamResponse res = null;
        try {
            res = new HttpService<ChangeTeamResponse>(ChangeTeamResponse.class).execute(httpCallPost).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (game.getClient().name.equals(res.name)) {
            game.getClient().team = res.team;
        }
        return res;
    }

    public ChangeStateResponse changeState(int state, String playerName) {
        Auth auth = new Auth(game.getClient().name, game.getClient().token);
        ChangeStateRequest body = new ChangeStateRequest(game.getGameId(), playerName, state, auth);

        HttpPost httpCallPost = new HttpPost();
        httpCallPost.setUrl("https://ctf.letorbi.de/change");
        httpCallPost.setBody(gson.toJson(body));

        ChangeStateResponse res = null;
        try {
            res = new HttpService<ChangeStateResponse>(ChangeStateResponse.class).execute(httpCallPost).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public GameLobbyResponse getGameLobby() {
        GameLobbyRequest body = new GameLobbyRequest(game.getGameId());

        HttpPost httpCallPost = new HttpPost();
        httpCallPost.setUrl("https://ctf.letorbi.de/lobby");
        httpCallPost.setBody(gson.toJson(body));
        if (debug) Log.d("GameRepository: ", gson.toJson(body));

        GameLobbyResponse res = null;
        try {
            res = new HttpService<GameLobbyResponse>(GameLobbyResponse.class).execute(httpCallPost).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public GameLobbyExtResponse getExtendedGameLobby() {
        GameLobbyExtRequest body = new GameLobbyExtRequest(game.getGameId(), new Auth(game.getClient().name, game.getClient().token));

        HttpPost httpCallPost = new HttpPost();
        httpCallPost.setUrl("https://ctf.letorbi.de/lobby-ext");
        httpCallPost.setBody(gson.toJson(body));
        if (debug) Log.d("GameRepository: ", gson.toJson(body));

        GameLobbyExtResponse res = null;
        try {
            res = new HttpService<GameLobbyExtResponse>(GameLobbyExtResponse.class).execute(httpCallPost).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public GamePointsResponse getGamePoints() {
        GamePointsRequest body = new GamePointsRequest(game.getGameId());

        HttpPost httpCallPost = new HttpPost();
        httpCallPost.setUrl("https://ctf.letorbi.de/points");
        httpCallPost.setBody(gson.toJson(body));
        if (debug) Log.d("GameRepository: ", gson.toJson(body));

        GamePointsResponse res = null;
        try {
            res = new HttpService<GamePointsResponse>(GamePointsResponse.class).execute(httpCallPost).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public ConquerPointResponse conquerPoint(int point, int team) {
        ConquerPointRequest body = new ConquerPointRequest(point, team);

        HttpPost httpCallPost = new HttpPost();
        httpCallPost.setUrl("https://ctf.letorbi.de/conquer");
        httpCallPost.setBody(gson.toJson(body));
        if (debug) Log.d("GameRepository: ", gson.toJson(body));

        ConquerPointResponse res = null;
        try {
            res = new HttpService<ConquerPointResponse>(ConquerPointResponse.class).execute(httpCallPost).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public EndGameResponse endGame() {
        EndGameRequest body = new EndGameRequest(game.getGameId());

        HttpPost httpCallPost = new HttpPost();
        httpCallPost.setUrl("https://ctf.letorbi.de/end");
        httpCallPost.setBody(gson.toJson(body));
        if (debug) Log.d("GameRepository: ", gson.toJson(body));

        EndGameResponse res = null;
        try {
            res = new HttpService<EndGameResponse>(EndGameResponse.class).execute(httpCallPost).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }
}
