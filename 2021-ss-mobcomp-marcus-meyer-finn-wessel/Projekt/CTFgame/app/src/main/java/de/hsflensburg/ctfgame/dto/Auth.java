package de.hsflensburg.ctfgame.dto;

public class Auth {
    public String name;
    public String token;

    public Auth(String name, String token) {
        this.name = name;
        this.token = token;
    }
}
