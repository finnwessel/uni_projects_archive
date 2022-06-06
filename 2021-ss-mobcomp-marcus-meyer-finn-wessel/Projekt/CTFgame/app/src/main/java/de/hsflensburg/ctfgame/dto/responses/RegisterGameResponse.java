package de.hsflensburg.ctfgame.dto.responses;

public class RegisterGameResponse extends Response{
    public final int game;
    public final String token;

    public RegisterGameResponse(int game, String token){
        this.game = game;
        this.token = token;
    }

    @Override
    public boolean isSuccess() {
        return this.statusCode == 200 || this.statusCode == 201;
    }
}
