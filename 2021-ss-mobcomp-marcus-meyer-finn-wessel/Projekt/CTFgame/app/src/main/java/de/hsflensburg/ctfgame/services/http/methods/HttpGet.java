package de.hsflensburg.ctfgame.services.http.methods;

import java.util.HashMap;

import de.hsflensburg.ctfgame.services.http.HttpCall;

public class HttpGet extends HttpCall {
    private static final String METHOD = "GET";

    private HashMap<String,String> params;

    public HashMap<String, String> getParams() {
        return (params == null) ? new HashMap<String, String>() : params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }
}
