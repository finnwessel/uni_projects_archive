package de.hsflensburg.ctfgame.ui.gameStats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.databinding.FragmentGameStatsBinding;

public class GameStatsFragment extends Fragment {

    private GameStatsViewModel viewModel;
    private FragmentGameStatsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(GameStatsViewModel.class);


        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_stats, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        View root = binding.getRoot();

        binding.gameStatsBtnBackToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_nav_game_stats_to_nav_start);
            }
        });

        return root;
    }
}
