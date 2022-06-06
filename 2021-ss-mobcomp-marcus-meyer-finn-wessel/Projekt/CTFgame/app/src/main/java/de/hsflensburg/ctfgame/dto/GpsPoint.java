package de.hsflensburg.ctfgame.dto;

import com.google.gson.annotations.SerializedName;

public class GpsPoint {
        @SerializedName("lat")
        public double latitude;
        @SerializedName("long")
        public double longitude;

        public GpsPoint(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
}
