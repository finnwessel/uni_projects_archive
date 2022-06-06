package de.hsflensburg.ctfgame.services.http;

public class HttpResponse {
    public int statusCode;

    public String data;

    public HttpResponse(int statusCode, String data) {
        this.statusCode = statusCode;
        this.data = data;
    }
}
