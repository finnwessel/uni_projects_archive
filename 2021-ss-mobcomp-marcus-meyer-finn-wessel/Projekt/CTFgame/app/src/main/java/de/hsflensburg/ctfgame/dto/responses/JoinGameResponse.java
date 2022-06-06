package de.hsflensburg.ctfgame.dto.responses;

public class JoinGameResponse extends Response {
    public final String token;

    public JoinGameResponse(String token){
        this.token = token;
    }

    @Override
    public boolean isSuccess() {
        return this.statusCode == 200;
    }
}
