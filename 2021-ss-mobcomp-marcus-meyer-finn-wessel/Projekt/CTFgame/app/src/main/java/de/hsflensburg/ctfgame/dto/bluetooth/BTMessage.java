package de.hsflensburg.ctfgame.dto.bluetooth;

public class BTMessage<T> {
    public T body;
    public int intent;
    public com.lokibt.bluetooth.BluetoothDevice device;

    public BTMessage(int intent, T body, com.lokibt.bluetooth.BluetoothDevice device) {
        this.body = body;
        this.intent = intent;
        this.device = device;
    }

}
