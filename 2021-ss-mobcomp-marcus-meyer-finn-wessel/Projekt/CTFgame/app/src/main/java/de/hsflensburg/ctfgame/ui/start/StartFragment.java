package de.hsflensburg.ctfgame.ui.start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.services.sound.SoundManager;

public class StartFragment extends Fragment implements View.OnClickListener {

    private StartViewModel startViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        startViewModel =
                new ViewModelProvider(this).get(StartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_start, container, false);

        Button hostGameBtn = root.findViewById(R.id.button_host);
        hostGameBtn.setOnClickListener(this);
        Button joinGameBtn = root.findViewById(R.id.button_join);
        joinGameBtn.setOnClickListener(this);
        return root;
    }
    @Override
    public void onClick(View v) {
        NavController navController = Navigation.findNavController(v);
        switch (v.getId()) {
            case R.id.button_host:
                // Opens game prep
                navController.navigate(R.id.action_nav_start_to_nav_prep);
                break;
            case R.id.button_join:
                // Opens lobby screen
                navController.navigate(R.id.action_nav_start_to_nav_select);
                break;
        }
    }
}