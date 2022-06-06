package de.hsflensburg.ctfgame.services.http;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import de.hsflensburg.ctfgame.dto.responses.ChangeTeamResponse;
import de.hsflensburg.ctfgame.dto.responses.Response;
import de.hsflensburg.ctfgame.services.http.methods.HttpGet;
import de.hsflensburg.ctfgame.services.http.methods.HttpPost;

public class HttpService<T extends Response> extends AsyncTask<HttpCall, T, T> {

    /* Charset Settings */
    private static final String UTF_8 = "UTF-8";
    /* Timeouts in milliseconds */
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    private Class<T> responseClass;
    public HttpService(Class<T> c){
        responseClass = c;
    }

    @Override
    protected T doInBackground(HttpCall... params) {
        HttpURLConnection con = null;
        int responseCode = 500;
        StringBuilder response = new StringBuilder();

        try {
            if (params[0] instanceof HttpGet) {
                HttpGet httpGet = (HttpGet) params[0];

                URL url = new URL(httpGet.getUrl() + getQueryString(httpGet.getParams()));
                con = createHttpConnection(url, "GET");

            } else if (params[0] instanceof HttpPost) {
                HttpPost httpPost = (HttpPost) params[0];

                URL url = new URL(httpPost.getUrl());
                con = createHttpConnection(url, "POST");

                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(httpPost.getBody());
                writer.flush();
                writer.close();
                os.close();
            }

            responseCode = con.getResponseCode();
            //Log.d("HttpRequest_61", "Response Code: " + responseCode);
            if (HttpURLConnection.HTTP_OK == responseCode || responseCode == HttpURLConnection.HTTP_CREATED) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        //Log.d("HttpRequest_74", response.toString());
        //T httpResponse = new HttpResponse(responseCode, response.toString());
        Gson gson = new Gson();
        T httpResponse = gson.fromJson(response.toString(), responseClass);
        if(httpResponse != null) {
            httpResponse.statusCode = responseCode;
        }

        return httpResponse;
    }

    @Override
    protected void onPostExecute(T s) {
        super.onPostExecute(s);
        onResponse(s);
    }

    public T onResponse(T response) {
        return response;
    }

    private HttpURLConnection createHttpConnection(URL url, String methodType) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(methodType);
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setReadTimeout(READ_TIMEOUT);
        con.setConnectTimeout(CONNECT_TIMEOUT);
        return con;
    }

    private String getQueryString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        if (params.isEmpty()) {
            return "";
        } else {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (isFirst) {
                    isFirst = false;
                    result.append("?");
                } else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(entry.getKey(), UTF_8));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), UTF_8));
            }
            return result.toString();
        }
    }
}