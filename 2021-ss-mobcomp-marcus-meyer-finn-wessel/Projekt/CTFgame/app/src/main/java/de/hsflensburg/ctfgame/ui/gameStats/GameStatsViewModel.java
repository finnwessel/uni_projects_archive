package de.hsflensburg.ctfgame.ui.gameStats;

import androidx.lifecycle.ViewModel;
import de.hsflensburg.ctfgame.Game;
import de.hsflensburg.ctfgame.dto.Participant;
import de.hsflensburg.ctfgame.dto.responses.GamePointsResponse;
import de.hsflensburg.ctfgame.repositories.GameRepository;
import de.hsflensburg.ctfgame.services.sound.SoundManager;


public class GameStatsViewModel extends ViewModel {

    public String getWinnerTeam() {
        if (isWinnerTeam()) {
            SoundManager.getInstance().playSound("game_won");
            return "You won!";
        } else {
            SoundManager.getInstance().playSound("game_lose");
            return "You lost!";
        }
    }

    public String getTimePlayed() {
        return Game.getInstance().getPlayedTimeInMinutes() + " min";
    }

    public String getDistanceCovered() {
        return Game.getInstance().distanceCovered() + " m";
    }

    public String getConquestsCount() {
        return Game.getInstance().getConquestsCount() + " times";
    }

    public Participant getClient() {
        return Game.getInstance().getClient();
    }


    private boolean isWinnerTeam() {
        boolean isWinner = false;
        GamePointsResponse res = GameRepository.getInstance().getGamePoints();
        if (res != null) {
            int teamId = getClient().team;
            int pointsCount = res.points.size();
            int teamPointsCount = 0;
            for (int i = 0; i < pointsCount; i++) {
                if(res.points.get(i).team == teamId) {
                    teamPointsCount++;
                }
            }
            if(teamPointsCount == pointsCount) {
                isWinner = true;
            }
        }
        return isWinner;
    }
}

