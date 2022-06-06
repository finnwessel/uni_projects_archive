package de.hsflensburg.ctfgame;

import org.junit.Test;

import de.hsflensburg.ctfgame.dto.responses.ChangeTeamResponse;
import de.hsflensburg.ctfgame.dto.responses.GameLobbyExtResponse;
import de.hsflensburg.ctfgame.dto.responses.GameLobbyResponse;
import de.hsflensburg.ctfgame.dto.responses.GamePointsResponse;
import de.hsflensburg.ctfgame.dto.responses.JoinGameResponse;
import de.hsflensburg.ctfgame.dto.responses.RegisterGameResponse;
import de.hsflensburg.ctfgame.repositories.GameRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ApiUnitTest {
    private int gameId;
    private int team = 0;


    @Test
    public void createGameLobbyTest(){
        RegisterGameResponse res = GameRepository.getInstance().createGameLobby("Unit Test");
        assertTrue(res.isSuccess());
        gameId = res.game;
    }

    @Test
    public void joinGameLobbyTest(){
        JoinGameResponse res = GameRepository.getInstance().joinGameLobby("Unit Test Joined", gameId, team);
        assertTrue(res.isSuccess());
        assertNotNull(res.token);
    }

    @Test
    public void changeTeamTest(){
        ChangeTeamResponse res = GameRepository.getInstance().changeTeam(team+1);
        assertTrue(res.isSuccess());
        assertEquals(team+1, res.team);
        assertEquals("Unit Test Joined" ,res.name);
    }

    @Test
    public void getGameLobbyTest(){
        GameLobbyResponse res = GameRepository.getInstance().getGameLobby();
        assertTrue(res.isSuccess());
    }

    @Test
    public void getExtendedGameLobbyTest(){
        GameLobbyExtResponse res = GameRepository.getInstance().getExtendedGameLobby();
        assertTrue(res.isSuccess());
    }

    @Test
    public void getGamePointsTest(){
        GamePointsResponse res = GameRepository.getInstance().getGamePoints();
        assertTrue(res.isSuccess());
    }
}
