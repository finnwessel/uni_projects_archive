package de.hsflensburg.ctfgame.services.http.methods;

import de.hsflensburg.ctfgame.services.http.HttpCall;

public class HttpPost extends HttpCall {

    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
