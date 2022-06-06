package de.hsflensburg.ctfgame.ui.game;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.lokibt.bluetooth.BluetoothAdapter;
import com.lokibt.bluetooth.BluetoothDevice;

import java.util.ArrayList;

import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.databinding.FragmentGameBinding;
import de.hsflensburg.ctfgame.dto.GameGpsPoint;
import de.hsflensburg.ctfgame.dto.GameListDialogData;
import de.hsflensburg.ctfgame.dto.Participant;
import de.hsflensburg.ctfgame.repositories.LocationRepository;
import de.hsflensburg.ctfgame.services.bluetooth.BluetoothService;
import de.hsflensburg.ctfgame.services.map.BitmapMap;
import de.hsflensburg.ctfgame.services.sound.SoundManager;
import de.hsflensburg.ctfgame.ui.TouchImageView;
import de.hsflensburg.ctfgame.ui.dialogs.CountdownCallback;
import de.hsflensburg.ctfgame.ui.dialogs.DialogCallback;
import de.hsflensburg.ctfgame.ui.dialogs.GameListDialog;
import de.hsflensburg.ctfgame.ui.dialogs.IngestiblePointDialog;
import de.hsflensburg.ctfgame.ui.dialogs.ScannerDialog;
import de.hsflensburg.ctfgame.ui.dialogs.WarnCamperDialog;
import de.hsflensburg.ctfgame.ui.dialogs.YesNoAlertDialog;
import de.hsflensburg.ctfgame.ui.lobby.LobbyListAdapter;
import de.hsflensburg.ctfgame.ui.lobby.LobbyViewModel;

public class GameFragment extends Fragment implements View.OnClickListener {

    private GameViewModel viewModel;
    private FragmentGameBinding binding;
    private IngestiblePointDialog ingestiblePointDialog;
    private YesNoAlertDialog leaveGameDialog;
    private GameListDialog gameListDialog;
    private ScannerDialog scannerDialog;
    private BitmapMap mapImage;
    private Location lastPlayerLocation;
    private TextView gpsAccuracyValue;
    private ImageView gpsAccuracySatelliteIcon;

    private BluetoothAdapter bt;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        View root = binding.getRoot();

        gpsAccuracyValue = binding.gpsAccuracyContainer.gpsAccuracyValue;
        gpsAccuracySatelliteIcon = binding.gpsAccuracyContainer.gpsAccuracySatelliteIcon;

        Button leaveGameBtn = root.findViewById(R.id.game_button_leave);
        leaveGameBtn.setOnClickListener(this);

        class ScannerDialogCallback implements CountdownCallback {
            @Override
            public void onFinish() {
                scannerDialog.hide();
                viewModel.getRunDiscovery().setValue(false);
            }
        }
        scannerDialog = new ScannerDialog(requireContext(), new ScannerDialogCallback());

        gameListDialog = new GameListDialog(requireContext(), requireActivity());

        viewModel.getGameListDialogLiveData().observe(getViewLifecycleOwner(), new Observer<GameListDialogData>() {
            @Override
            public void onChanged(GameListDialogData data) {
                gameListDialog.setDialogData(data);
            }
        });

        bt = BluetoothAdapter.getDefaultAdapter();
        if(!viewModel.getClient().isHost()) {
            btConsent();
        }

        TouchImageView image = root.findViewById(R.id.game_image_map);
        mapImage = new BitmapMap(requireContext());
        mapImage.drawGameGpsPoints(viewModel.getGameGpsPoints());

        LocationRepository lR = new LocationRepository(requireContext());
        lR.startLocationListener();
        lR.location.observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (location != null) {
                    handleGpsAccuracy(location.getAccuracy());
                    viewModel.addRecordedLocation(location);
                    Log.d("Meters traveled", String.valueOf(viewModel.distanceCovered()));
                    lastPlayerLocation = location;
                    mapImage.resetCanvas();
                    mapImage.drawGameGpsPoints(viewModel.getGameGpsPoints());
                    mapImage.drawPlayerPosition(location);
                    image.setImageDrawable(new BitmapDrawable(getResources(), mapImage.getBitmap()));
                }
                Log.d("GameFragment", "Location changed: Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
            }
        });

        viewModel.getGameGpsPointsLiveData().observe(getViewLifecycleOwner(), gameGpsPoints -> {
            if (gameGpsPoints != null) {
                mapImage.resetCanvas();
                mapImage.drawGameGpsPoints(gameGpsPoints);
                if (lastPlayerLocation != null) {
                    mapImage.drawPlayerPosition(lastPlayerLocation);
                }
                image.setImageDrawable(new BitmapDrawable(getResources(), mapImage.getBitmap()));
            }
        });

        viewModel.getRunDiscovery().observe(getViewLifecycleOwner(), run -> {
            if (run) {
                new Thread(() -> bt.startDiscovery()).start();
            } else {
                bt.cancelDiscovery();
            }
        });

        class LeaveGameDialogCallback implements DialogCallback {
            @Override
            public void accept() {
                viewModel.leaveGame();
                NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.action_nav_game_to_nav_start);
            }

            @Override
            public void decline() {

            }
        }
        leaveGameDialog = new YesNoAlertDialog(getContext(), new LeaveGameDialogCallback());

        class IngestiblePointDialogCallback implements DialogCallback {
            @Override
            public void accept() {
                viewModel.conquerGamePoint();
            }

            @Override
            public void decline() {

            }
        }
        ingestiblePointDialog = new IngestiblePointDialog(getContext(), new IngestiblePointDialogCallback());

        viewModel.getIngestiblePointDialogVisibleLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean show) {
                    if (show) {
                        ingestiblePointDialog.show();
                    } else {
                        ingestiblePointDialog.hide();
                    }
            }
        });

        class WarnCamperDialogCallback implements CountdownCallback {
            @Override
            public void onFinish() {
                viewModel.withdrawPoint();
            }
        }
        WarnCamperDialog warnCamperDialog = new WarnCamperDialog(getContext(), new WarnCamperDialogCallback());
        viewModel.getGuardingWarningVisible().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean show) {
                if (show) {
                    warnCamperDialog.show();
                } else {
                    warnCamperDialog.hide();
                }
            }
        });

        viewModel.getGameEndedLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean ended) {
                if (ended) {
                    NavController navController = Navigation.findNavController(root);
                    navController.navigate(R.id.action_nav_game_to_nav_game_stats);
                }
            }
        });

        image.setImageDrawable(new BitmapDrawable(getResources(), mapImage.getBitmap()));
        
        viewModel.init();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_button_leave:
                leaveGameDialog.show();
                break;
        }
    }

    private void btConsent() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, viewModel.getGameDurationInMinutes() * 60);
        startActivityForResult(discoverableIntent, 1);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d("btreceiver", "Bluetooth will be enabled");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d("btreceiver", "Bluetooth will be disabled");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d("btreceiver", "Bluetooth was enabled");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.d("btreceiver", "Bluetooth was disabled");
                            break;
                        default:
                            Log.e("btreceiver", "Unknown Bluetooth state");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    viewModel.deviceFound(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    viewModel.determinePointWinner();
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    scannerDialog.show();
                default:
                    Log.e("btreceiver", "Unhandled Bluetooth action " + intent.getAction());
                    break;
            }
        }
    };

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