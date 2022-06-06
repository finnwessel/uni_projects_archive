package de.hsflensburg.ctfgame.services.bluetooth;

import android.os.Handler;
import android.util.Log;

import com.lokibt.bluetooth.BluetoothDevice;
import com.lokibt.bluetooth.BluetoothServerSocket;
import com.lokibt.bluetooth.BluetoothSocket;
import com.lokibt.bluetooth.BluetoothAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.hsflensburg.ctfgame.dto.bluetooth.MessageIntent;


public class BluetoothService {

    private final String APP_NAME = "CTF";
    private final UUID APP_UUID = UUID.fromString("97e86912-bd7c-11eb-8529-0242ac130003");
    static final String TAG1 = "BT_SERVER";
    static final String TAG2 = "BT_SENDER";

    Handler handler;
    BluetoothAdapter bt;

    public BTServer server;

    public BTSender SenderThread = null;

    public BluetoothService(Handler handler) {
        bt = BluetoothAdapter.getDefaultAdapter();
        this.handler = handler;
    }

    public class BTServer extends Thread {
        private boolean isRunning = true;
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket = null;
        private InputStream inStream = null;
        private OutputStream outStream = null;

        public BTServer() {
            try {
                serverSocket = bt.listenUsingRfcommWithServiceRecord(APP_NAME, APP_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (isRunning) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    inStream = socket.getInputStream();
                    outStream = socket.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytes;

                    bytes = inStream.read(buffer);
                    if(bytes > 0) {
                        handler.obtainMessage(MessageIntent.RECEIVED, 0, bytes, buffer).sendToTarget();
                    }
                } catch (Exception e) {
                    Log.d(TAG1, "Exception during data exchange", e);
                }
            }
            try {
                Log.d(TAG1, "Closing listen socket");
                if (serverSocket != null) {
                    serverSocket.close();
                }
                Log.d(TAG1, "Server stopped");
            } catch (Exception e2) {
                Log.d(TAG1, "Exception when stopping server " + e2);
            }
        }

        public void write(String message) {
            try {
                Log.d("bt", "Server answering...");
                byte[] bytes = message.getBytes();
                outStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG1, "Exception during server write", e);
            }
        }
        public void cancel() {
            isRunning = false;
        }
    }

    public void startServer() {
        server = new BTServer();
        server.start();
    }

    public class BTSender extends Thread {

        private BluetoothSocket socket = null;
        private InputStream inStream = null;
        private OutputStream outStream = null;
        private boolean isWaiting = true;
        private boolean connBlocked = true;
        private String message;
        private BluetoothDevice device;

        private final long reconnectTime = 1000;
        private int reconnectAttempts = 0;

        byte[] buffer = new byte[1024];
        int bytes;

        public BTSender(String message, BluetoothDevice device) {
            this.message = message;
            this.device = device;
        }

        public void run() {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                if(!connBlocked || reconnectAttempts >= 2) {
                    t.cancel();
                }
                try {
                    reconnectAttempts++;
                    socket = device.createRfcommSocketToServiceRecord(APP_UUID);
                    socket.connect();
                    connBlocked = false;
                    inStream = socket.getInputStream();
                    outStream = socket.getOutputStream();

                    byte[] mbytes = message.getBytes();
                    outStream.write(mbytes);
                    handler.obtainMessage(MessageIntent.SELF, mbytes).sendToTarget();

                    while (isWaiting) {
                        try {
                            bytes = inStream.read(buffer);
                            if (bytes > 0) {
                                handler.obtainMessage(MessageIntent.RECEIVED, 0, bytes, buffer).sendToTarget();
                                isWaiting = false;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("Sender", "Not able to read an answer");
                            isWaiting = false;
                        }
                    }
                } catch (IOException e) {
                    Log.d(TAG2, "Exception in sender connecting...", e);
                } finally {
                    if (socket != null) {
                        try {
                            Log.d(TAG2, "Closing sender socket...");
                            socket.close();
                        } catch (IOException e) {
                            Log.d(TAG2, "Exception when stopping sender " + e);
                        }
                    }
                }
                }
            }, 0L, reconnectTime);
        }
        public void cancel() {
            isWaiting = false;
        }
    }

    public void sendMessageToDevice(String message, final BluetoothDevice device) {
        if(SenderThread != null) {
            SenderThread = null;
        }
        SenderThread = new BTSender(message, device);
        SenderThread.start();
    }
}
