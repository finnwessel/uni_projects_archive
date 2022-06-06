package de.hsflensburg.ctfgame.ui.lobby;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.dto.Participant;

import static android.view.View.GONE;

public class LobbyListAdapter extends ArrayAdapter<Participant> {

    private Context mContext;
    private ArrayList<Participant> participantsList = new ArrayList<>();

    //@LayoutRes
    public LobbyListAdapter(@NonNull Context context, ArrayList<Participant> list) {
        super(context, 0, list);
        mContext = context;
        participantsList = list;
    }

    public void setParticipantsList(ArrayList<Participant> list) {
        this.participantsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.lobby_list_item,parent,false);

        Participant currentParticipant = participantsList.get(position);

        TextView name = listItem.findViewById(R.id.lobby_participant_name);
        name.setText(currentParticipant.name);
        name.setTextColor(Color.WHITE);

        ImageView bt_icon = listItem.findViewById(R.id.lobby_participant_bluetooth);
        ImageView host_icon = listItem.findViewById(R.id.lobby_participant_host);

        host_icon.setVisibility(currentParticipant.isHost() ? View.VISIBLE : View.INVISIBLE);
        bt_icon.setVisibility(currentParticipant.state == 0 ? View.INVISIBLE : View.VISIBLE);

        switch (currentParticipant.team) {
            case 0: // Team 0 -> no selection
                listItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.no_team));
                break;
            case 1: // Team 1 -> red
                listItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red_team));
                break;
            case 2: // Team 2 -> blue
                listItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_team));
                break;
            default:
                listItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.no_team));
        }
        return listItem;
    }

    @Override
    public int getCount() {
        return participantsList.size();
    }
}
