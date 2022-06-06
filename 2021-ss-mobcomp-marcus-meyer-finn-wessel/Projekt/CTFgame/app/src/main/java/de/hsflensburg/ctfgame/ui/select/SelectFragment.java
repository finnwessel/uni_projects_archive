package de.hsflensburg.ctfgame.ui.select;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.databinding.FragmentSelectBinding;
import de.hsflensburg.ctfgame.services.sound.SoundManager;

public class SelectFragment extends Fragment implements View.OnClickListener{

    private SelectViewModel viewModel;
    private FragmentSelectBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(SelectViewModel.class);

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_select, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        View root = binding.getRoot();

        Button joinLobbyBtn = root.findViewById(R.id.select_button_join);
        joinLobbyBtn.setEnabled(false);
        joinLobbyBtn.setOnClickListener(this);


        final EditText inputPlayerName = root.findViewById(R.id.select_input_player_name);
        final EditText inputGameId = root.findViewById(R.id.select_input_game_id);

        inputPlayerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= SelectViewModel.MIN_PLAYER_NAME_LENGTH && inputGameId.getText().length() >= SelectViewModel.MIN_LOBBY_ID_LENGTH) {
                    joinLobbyBtn.setEnabled(true);
                } else {
                    joinLobbyBtn.setEnabled(false);
                }
                viewModel.setPlayerName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputGameId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    //viewModel.gameId = Integer.parseInt(s.toString());
                    viewModel.setGameId(Integer.parseInt(s.toString()));
                }
                if (s.length() >= SelectViewModel.MIN_LOBBY_ID_LENGTH && inputPlayerName.getText().length() >= SelectViewModel.MIN_PLAYER_NAME_LENGTH) {
                    joinLobbyBtn.setEnabled(true);
                } else {
                    joinLobbyBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return root;
    }

    @Override
    public void onClick(View v) {
        NavController navController = Navigation.findNavController(v);
        switch (v.getId()) {
            case R.id.select_button_join:
                // Team select dialog
                showTeamSelectAlert(navController, v);
                break;
        }
    }

    private void showTeamSelectAlert(NavController navController, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Wähle ein Team");


        String[] teams = {"Egal", "Rot", "Blau"};
        int checkedItem = 0;
        builder.setSingleChoiceItems(teams, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int teamId) {
                viewModel.setTeamId(teamId);
            }
        });

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int teamId) {
                viewModel.joinGameLobby();
                SoundManager.getInstance().playSound("lobby_enter");
                navController.navigate(R.id.action_nav_select_to_nav_lobby);
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}