package de.hsflensburg.ctfgame.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.dto.GameListDialogData;
import de.hsflensburg.ctfgame.dto.Participant;
import de.hsflensburg.ctfgame.ui.lobby.LobbyListAdapter;

public class GameListDialog {
    private Dialog dialog;
    private Context context;

    TextView whoWonText;
    TextView ownTeamText;
    TextView enemyTeamText;
    ListView playerList;

    private final ArrayList<Participant> nearbyPlayers;

    public GameListDialog(Context context, FragmentActivity fragmentActivity) {
        dialog = new Dialog(context);
        dialog.setCancelable(true);
        this.context = context;

        View view = fragmentActivity.getLayoutInflater().inflate(R.layout.game_list_dialog, null);
        dialog.setContentView(view);

        Button closeDialog = view.findViewById(R.id.game_list_dialog_close);

        whoWonText = view.findViewById(R.id.game_list_title);
        ownTeamText = view.findViewById(R.id.game_list_players_own_text);
        enemyTeamText = view.findViewById(R.id.game_list_players_enemy_text);
        playerList = view.findViewById(R.id.game_list);


        LobbyListAdapter adapter = null;
        nearbyPlayers = new ArrayList<>();

        if (playerList.getAdapter() == null) {
            adapter = new LobbyListAdapter(context, nearbyPlayers);
            playerList.setAdapter(adapter);
        } else {
            adapter.setParticipantsList(nearbyPlayers);
            adapter.notifyDataSetChanged();
        }

        closeDialog.setOnClickListener(view1 -> hide());

    }

    public void setWhoWon(int team) {
        if (team == 1) {
            whoWonText.setText("Red got the point!");
            whoWonText.setTextColor(ContextCompat.getColor(context, R.color.red_team));
        } else if (team == 2) {
            whoWonText.setText("Blue got the point!");
            whoWonText.setTextColor(ContextCompat.getColor(context, R.color.blue_team));
        }
        else {
            whoWonText.setText("It's a draw!");
            whoWonText.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    private void setTeamCounts(int ownTeamID, int ownTeamCount, int enemyTeamCount) {
        ownTeamText.setText(Integer.toString(ownTeamCount));
        enemyTeamText.setText(Integer.toString(enemyTeamCount));
        if(ownTeamID == 1) {
            ownTeamText.setTextColor(ContextCompat.getColor(context, R.color.red_team));
            enemyTeamText.setTextColor(ContextCompat.getColor(context, R.color.blue_team));
        } else {
            ownTeamText.setTextColor(ContextCompat.getColor(context, R.color.blue_team));
            enemyTeamText.setTextColor(ContextCompat.getColor(context, R.color.red_team));
        }
    }

    public void setDialogData(GameListDialogData data) {
        if (data.isDialogVisible()) {
            setWhoWon(data.winnerTeamID);
            setTeamCounts(data.ownTeamID, data.ownTeamCount, data.enemyTeamCount);
            nearbyPlayers.clear();
            nearbyPlayers.addAll(data.nearbyPlayers);
            show();
        } else {
            hide();
        }
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.hide();
    }
}
