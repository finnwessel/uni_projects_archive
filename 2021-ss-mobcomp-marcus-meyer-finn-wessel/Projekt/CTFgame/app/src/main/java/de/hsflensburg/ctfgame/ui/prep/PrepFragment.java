package de.hsflensburg.ctfgame.ui.prep;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.databinding.FragmentPrepBinding;
import de.hsflensburg.ctfgame.dto.GpsPoint;
import de.hsflensburg.ctfgame.dto.TextMessage;
import de.hsflensburg.ctfgame.repositories.LocationRepository;
import de.hsflensburg.ctfgame.services.map.BitmapMap;
import de.hsflensburg.ctfgame.services.sound.SoundManager;

public class PrepFragment extends Fragment implements View.OnClickListener {

    private ListView listView ;
    private ImageView mapImageView;
    private FragmentPrepBinding binding;
    private PrepViewModel viewModel;
    private EditText gameName;
    private BitmapMap bitmapMap;
    private Location lastPlayerLocation;
    private TextView gpsAccuracyValue;
    private ImageView gpsAccuracySatelliteIcon;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel =
                new ViewModelProvider(this).get(PrepViewModel.class);

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_prep, container, false);
        binding.setLifecycleOwner(this);
        binding.setPrepViewModel(viewModel);
        View root = binding.getRoot();

        gpsAccuracyValue = binding.gpsAccuracyContainer.gpsAccuracyValue;
        gpsAccuracySatelliteIcon = binding.gpsAccuracyContainer.gpsAccuracySatelliteIcon;

        Button safePlayerLocation = binding.prepButtonSafeLocation;
        safePlayerLocation.setOnClickListener(this);

        Button createGameBtn = binding.prepButtonCreate;
        createGameBtn.setEnabled(false);
        createGameBtn.setOnClickListener(this);

        bitmapMap = new BitmapMap(requireContext());

        mapImageView = binding.imageMap;

        mapImageView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("", "Event x: " + event.getX() + " Event y: " + event.getY());
                viewModel.addOrRemoveGpsPoint(bitmapMap.calculateGpsPoint(event.getX(), event.getY(), mapImageView.getWidth(), mapImageView.getHeight()));
                return false;
            }
        });

        gameName = binding.prepEditTextHostName;

        gameName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setGameName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewModel.createGameBtnEnabled.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean bool) {
                createGameBtn.setEnabled(bool);
            }
        });

        viewModel.message.observe(getViewLifecycleOwner(), new Observer<TextMessage>() {
            @Override
            public void onChanged(TextMessage res) {
                Toast.makeText(getContext(), res.text, Toast.LENGTH_LONG).show();
            }
        });


        listView = (ListView) binding.prepPointListView;


        LocationRepository lR = new LocationRepository(getContext());
        lR.startLocationListener();
        lR.location.observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (location != null) {
                    handleGpsAccuracy(location.getAccuracy());
                    lastPlayerLocation = location;
                    bitmapMap.resetCanvas();
                    bitmapMap.drawGpsPoints(viewModel.getGpsPoints());
                    bitmapMap.drawPlayerPosition(location);
                    mapImageView.setImageDrawable(new BitmapDrawable(getResources(), bitmapMap.getBitmap()));
                }
                Log.d("GameFragment", "Location changed: Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
            }
        });


        viewModel.getGpsPointsLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<GpsPoint>>() {
            PrepListAdapter adapter = null;
            @Override
            public void onChanged(ArrayList<GpsPoint> gpsPoints) {
                if(listView.getAdapter() == null) {
                    adapter = new PrepListAdapter(requireContext(), gpsPoints);
                    listView.setAdapter(adapter);
                } else{
                    Log.d("Update GpsPoint List", "New GpsPoint");
                    adapter.setGpsPointsList(gpsPoints);
                    adapter.notifyDataSetChanged();
                }
                bitmapMap.resetCanvas();
                if (lastPlayerLocation != null) {
                    bitmapMap.drawPlayerPosition(lastPlayerLocation);
                }
                bitmapMap.drawGpsPoints(gpsPoints);
                mapImageView.setImageDrawable(new BitmapDrawable(getResources(), bitmapMap.getBitmap()));
            }
        });

        return root;
    }

    @Override
    public void onClick(View v) {
        NavController navController = Navigation.findNavController(v);
        switch (v.getId()) {
            case R.id.prep_button_create:
                // Opens game lobby
                SoundManager.getInstance().playSound("lobby_enter");
                viewModel.createGameLobby(gameName.getText().toString());
                navController.navigate(R.id.action_nav_prep_to_nav_lobby);
                break;
            case R.id.prep_button_safe_location:
                viewModel.addOrRemoveGpsPoint(new GpsPoint(lastPlayerLocation.getLatitude(), lastPlayerLocation.getLongitude()));
        }
    }

    int redColor = Color.argb(255, 255,0,0);
    int yellowColor = Color.argb(255, 255,255,0);
    int greenColor = Color.argb(255, 0,255,0);
    private void handleGpsAccuracy(float accuracy) {
        gpsAccuracyValue.setText(String.format("%s", accuracy));
        if (accuracy <= 5.0) {
            gpsAccuracySatelliteIcon.setColorFilter(greenColor);
        } else if (accuracy <= 10.0) {
            gpsAccuracySatelliteIcon.setColorFilter(yellowColor);
        } else {
            gpsAccuracySatelliteIcon.setColorFilter(redColor);
        }
    }
}