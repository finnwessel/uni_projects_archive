package de.hsflensburg.ctfgame.dto;

public class Participant {
    public String name;
    public int team;
    public int state;
    private boolean host;
    public String token;
    public String address;

    public Participant(String name, int team, int state, boolean host) {
        this.name = name;
        this.team = team;
        this.state = state;
        this.host = host;
        this.token = null;
    }

    public Participant(String name, int team, int state, boolean host, String token) {
        this.name = name;
        this.team = team;
        this.state = state;
        this.host = host;
        this.token = token;
    }

    public Participant(String name, int team, String address) {
        this.name = name;
        this.team = team;
        this.address = address;
    }

    public boolean isHost() {
        return host;
    }
}
