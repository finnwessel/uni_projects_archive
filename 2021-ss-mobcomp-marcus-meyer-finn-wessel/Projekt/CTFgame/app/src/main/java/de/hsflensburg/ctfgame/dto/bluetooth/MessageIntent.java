package de.hsflensburg.ctfgame.dto.bluetooth;

public interface MessageIntent {
    int SELF = -2;
    int ERROR = -1;
    int RECEIVED = 0;
    int AUTHENTICATION = 1;
    int GAME_DATA = 2;
    int READY = 3;
}
