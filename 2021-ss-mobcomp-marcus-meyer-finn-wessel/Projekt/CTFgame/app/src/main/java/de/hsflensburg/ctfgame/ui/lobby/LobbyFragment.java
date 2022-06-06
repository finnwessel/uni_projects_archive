package de.hsflensburg.ctfgame.ui.lobby;

import android.app.AlertDialog;
//import android.bluetooth.BluetoothAdapter;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lokibt.bluetooth.BluetoothAdapter;
import com.lokibt.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hsflensburg.ctfgame.GameState;
import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.databinding.FragmentLobbyBinding;
import de.hsflensburg.ctfgame.dto.Participant;
import de.hsflensburg.ctfgame.dto.bluetooth.BTMessage;
import de.hsflensburg.ctfgame.dto.bluetooth.GameData;
import de.hsflensburg.ctfgame.dto.bluetooth.MessageIntent;
import de.hsflensburg.ctfgame.services.bluetooth.BluetoothService;
import de.hsflensburg.ctfgame.services.sound.SoundManager;

public class LobbyFragment extends Fragment implements View.OnClickListener {

    private View root;
    private LobbyViewModel viewModel;
    private FragmentLobbyBinding binding;
    private ListView listView;

    private BluetoothService btService;
    private BluetoothAdapter bt;
    private final Gson gson = new Gson();

    private Timer dataExChangeTimer;
    private Timer readyTimer;
    private final long hostUpdateDuration = 5000;

    private BluetoothDevice host = null;
    private final ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

    private int count = 0;
    ArrayList<String> readyMAC = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(LobbyViewModel.class);

        viewModel.startTimer();

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_lobby, container, false);
        binding.setLifecycleOwner(this);
        binding.setLobbyViewModel(viewModel);

        root = binding.getRoot();

        binding.lobbyButtonStart.setEnabled(false);
        binding.lobbyButtonStart.setOnClickListener(this);
        binding.lobbyButtonCancel.setOnClickListener(this);

        bt = BluetoothAdapter.getDefaultAdapter();

        BluetoothInit();

        viewModel.getIsParticipantOfLobby().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPartOfLobby) {
                if(!isPartOfLobby) {
                    Navigation.findNavController(root).navigate(R.id.action_nav_lobby_to_nav_start);
                }
            }
        });

        viewModel.getAllParticipantsConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                binding.lobbyButtonStart.setEnabled(enabled);
            }
        });

        listView = binding.lobbyList;

        viewModel.getPlayerNamesLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Participant>>() {
            LobbyListAdapter adapter = null;

            @Override
            public void onChanged(ArrayList<Participant> participants) {
                if (listView.getAdapter() == null) {
                    adapter = new LobbyListAdapter(requireContext(), participants);
                    listView.setAdapter(adapter);
                } else {
                    adapter.setParticipantsList(participants);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Participant participant = (Participant) listView.getItemAtPosition(position);

                if (viewModel.getClient().isHost() || viewModel.getClient().name.equals(participant.name)) { // ToDO: Rework
                    listViewItemClicked(participant);
                }
            }
        });

        binding.lobbySettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLobbySettingsDialog();
            }
        });

        return root;
    }

    private void showLobbySettingsDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view = getActivity().getLayoutInflater().inflate(R.layout.lobby_settings_dialog, null);
        dialog.setContentView(view);
        EditText timeText= view.findViewById(R.id.settings_dialog_time);
        int hour = (viewModel.getGameDurationInMinutes() / 60);//cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = (viewModel.getGameDurationInMinutes() % 60); //cldr.get(Calendar.MINUTE);
        viewModel.setGameDurationInMinutes((hour * 60) + minutes);
        timeText.setText(String.format("%d : %dh", hour, minutes));
        timeText.setInputType(InputType.TYPE_NULL);
        timeText.setOnClickListener(v -> {
            // time picker dialog
            TimePickerDialog picker = new TimePickerDialog(requireContext(), R.style.TimePickerTheme,
                    (tp, sHour, sMinute) -> {
                        timeText.setText(String.format("%d : %dh", sHour, sMinute));
                        viewModel.setGameDurationInMinutes((sHour * 60) + sMinute);
                    }, hour, minutes, true);
            picker.show();

        });
        dialog.show();
    }

    private void listViewItemClicked(Participant p) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view = getActivity().getLayoutInflater().inflate(R.layout.lobby_list_dialog, null);
        dialog.setContentView(view);

        Button changeTeam = (Button) view.findViewById(R.id.lobby_list_dialog_btn_change_team);
        Button kickPlayer = (Button) view.findViewById(R.id.lobby_list_dialog_btn_kick_player);
        kickPlayer.setText(String.format("Kick %s", p.name));

        changeTeam.setOnClickListener(view12 -> {
            Toast.makeText(getContext(),
                    "Change Team for Player" + p.name, Toast.LENGTH_LONG)
                    .show();
            showTeamSelectAlert(view12, p);
        });
        kickPlayer.setOnClickListener(view1 -> {
            Toast.makeText(getContext(),
                    "Kick Player" + p.name, Toast.LENGTH_LONG)
                    .show();
            viewModel.kickPlayer(p.name);
        });
        dialog.show();
    }

    private void showTeamSelectAlert(View v, Participant p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Wähle ein Team");

        String[] teams = {"Egal", "Rot", "Blau"};
        int checkedItem = 0;
        builder.setSingleChoiceItems(teams, checkedItem, (dialog, teamId) -> p.team = teamId);

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", (dialog, teamId) -> viewModel.changeTeam(p.team, p.name));
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
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
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.clearTimer();
    }

    @Override
    public void onClick(View v) {
        NavController navController = Navigation.findNavController(v);
        switch (v.getId()) {
            case R.id.lobby_button_start:
                viewModel.setGameState(GameState.RUNNING);
                break;
            case R.id.lobby_button_cancel:
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getRootView().getContext());
                alert.setTitle("Warning");
                alert.setMessage("Are you sure to cancel the game?");
                alert.setPositiveButton("YES", (dialog, which) -> {
                    viewModel.leaveLobby();
                    SoundManager.getInstance().playSound("lobby_leave");
                    navController.navigate(R.id.action_nav_lobby_to_nav_start);
                    dialog.dismiss();
                });
                alert.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                break;
        }
    }

    public void navigateToGame() {
        if(root.isShown()) {
            SoundManager.getInstance().playSound("game_started");
            Navigation.findNavController(root).navigate(R.id.action_nav_lobby_to_nav_game);
        }
    }

    Handler handler = new Handler(msg -> {
        if (msg.obj != null) {
            String rawMessage;
            BTMessage btMessage;
            switch (msg.what) {
                case MessageIntent.RECEIVED:
                    Log.d("HANDLER", "Default...");
                    rawMessage =  new String((byte[]) msg.obj, msg.arg1, msg.arg2);
                    Log.d("HANDLER_RAW", rawMessage);
                    try {
                        btMessage = gson.fromJson(rawMessage, BTMessage.class);
                        switch (btMessage.intent) {
                            case MessageIntent.AUTHENTICATION: // HOST
                                Log.d("HANDLER", "authenticate...");
                                authenticate(btMessage, btMessage.device);
                                break;
                            case MessageIntent.GAME_DATA: // CLIENT
                                Log.d("GAME DATA", btMessage.body.toString());
                                host = btMessage.device;
                                GameData gameData = gson.fromJson(gson.toJson(btMessage.body), GameData.class);
                                handleGameData(gameData);
                                Toast.makeText(getContext(), "Got game data " + count++, Toast.LENGTH_SHORT).show();
                                break;
                            case MessageIntent.READY: // HOST
                                if(viewModel.getClient().isHost()) {
                                    answer(new BTMessage<>(MessageIntent.READY, "ready", bt.getRemoteDevice(bt.getAddress())));
                                    countReadyParticipant(btMessage.device.getAddress());
                                }
                                break;
                            case MessageIntent.ERROR:
                                Log.d("ERROR", btMessage.body.toString());
                                break;
                            default:
                                Log.d("HANDLER", "NOT a valid intent");
                        }
                    } catch (JsonSyntaxException e) {
                        Log.d("HANDLER", "Could not decode json message");
                    }
                    break;
                    case MessageIntent.SELF:
                        rawMessage =  new String((byte[]) msg.obj);
                        btMessage = gson.fromJson(rawMessage, BTMessage.class);
                        Log.d("HANDLER_SELF", "Intent: " + btMessage.intent);
                        switch (btMessage.intent) {
                            case MessageIntent.READY: // CLIENT
                                btService.SenderThread.cancel();
                                readyTimer.cancel();
                                navigateToGame();
                                break;
                            default:
                                Log.d("HANDLER_SELF", "No action for this intent defined.");
                                break;
                        }
            }
        }
        return true;
    });

    private void countReadyParticipant(String address) {
        boolean alreadyReady = false;
        for(int i = 0; i < readyMAC.size() && !alreadyReady; i++) {
            if(readyMAC.get(i).equals(address)) {
                alreadyReady = true;
            }
        }
        if(!alreadyReady) {
            readyMAC.add(address);
        }
        Log.d("PART_COUNT", "READY: " + readyMAC.size() + " Devices: " + viewModel.getLobbyDevices().size());
        if (readyMAC.size() >= viewModel.getLobbyDevices().size()) {
            btService.server.cancel();
            navigateToGame();
        }
    }

    boolean routeChangeHandled = false;
    private void handleGameData(GameData gameData) {
        if(gameData.state == GameState.RUNNING) {
            viewModel.setGameDurationInMinutes(gameData.gameDurationInMinutes);
            viewModel.setGameParticipants(gameData.participants);
            if (!routeChangeHandled) {
                routeChangeHandled = true;
                try {
                    dataExChangeTimer.cancel();
                } catch (Exception e) {
                    Log.d("Stop_Exchange", "Not able to stop the exchange " + e);
                }
            }
            readyTimer = new Timer();
            readyTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(btService != null) {
                        btService.sendMessageToDevice(gson.toJson(new BTMessage<>(MessageIntent.READY, gameData, bt.getRemoteDevice(bt.getAddress()))), host);
                    }
                }
            }, 0L, 1000L);
        }
    }

    private void authenticate(BTMessage btMessage, BluetoothDevice device) {
        BTMessage message;
        Participant participant = gson.fromJson(btMessage.body.toString(), Participant.class);
        if (viewModel.initParticipant(participant, device)) {
            GameData gameData = new GameData(viewModel.getGameId(), viewModel.getGameState(), viewModel.getGameDurationInMinutes());
            gameData.participants.add(new Participant(viewModel.getClient().name, viewModel.getClient().team, bt.getAddress()));
            for(Map.Entry<Participant, BluetoothDevice> entry : viewModel.getLobbyDevices().entrySet()) {
                gameData.participants.add(new Participant(entry.getKey().name, entry.getKey().team, entry.getValue().getAddress()));
            }
            viewModel.setGameParticipants(gameData.participants);
            message = new BTMessage<>(MessageIntent.GAME_DATA, gameData, bt.getRemoteDevice(bt.getAddress()));
        } else {
            message = new BTMessage<>(MessageIntent.ERROR, participant.name + " is not part of this Lobby", bt.getRemoteDevice(bt.getAddress()));
        }
        answer(message);
    }

    private void answer(BTMessage message) {
        new Thread(() -> {
            if(btService != null && btService.server != null) {
                Log.d("LobbyFragment", "Answer Message");
                btService.server.write(gson.toJson(message));
            }
        }).start();
    }

    private void startConnection() {
        for (BluetoothDevice device : discoveredDevices) {
            refreshHost(device);
        }
    }

    public void informHost() {
        Log.d("INFORM", "Inform started...");
        dataExChangeTimer = new Timer();
        dataExChangeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshHost(host);
            }
        }, 0L, hostUpdateDuration);
    }

    private void refreshHost(BluetoothDevice device) {
        BTMessage<Participant> clientData = new BTMessage(MessageIntent.AUTHENTICATION, viewModel.getClient(), bt.getRemoteDevice(bt.getAddress()));
        String message = gson.toJson(clientData);
        btService.sendMessageToDevice(message, device);
    }

    private void BluetoothInit() {
        btService = new BluetoothService(handler);
        if (viewModel.getClient().isHost()) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
            startActivityForResult(discoverableIntent, 1);
            new Thread(() -> btService.startServer()).start();
        } else {
            discoverHost();
        }
    }

    private void discoverHost() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("DicoveryTimer", "is still running...");
                if(host != null) {
                    bt.cancelDiscovery();
                    timer.cancel();
                    Log.d("DISCOVERY", "Discover timer canceled");
                } else {
                    startConnection();
                    if(!bt.isDiscovering()) {
                        Log.d("DISCOVERY", "Starting Discovery...");
                        new Thread(() -> bt.startDiscovery()).start();
                    }
                }
            }
        }, 0L, 3000L);
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
                    discoveredDevices.add(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                    startConnection();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    informHost();
                    break;
                default:
                    Log.e("btreceiver", "Unhandled Bluetooth action " + intent.getAction());
                    break;
            }
        }
    };
}