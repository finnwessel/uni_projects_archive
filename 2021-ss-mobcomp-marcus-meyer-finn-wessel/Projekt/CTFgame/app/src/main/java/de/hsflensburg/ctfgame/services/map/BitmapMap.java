package de.hsflensburg.ctfgame.services.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.dto.GameGpsPoint;
import de.hsflensburg.ctfgame.dto.GpsPoint;

public class BitmapMap {

    private double bmWidth = 2048.00;
    private double bmHeight = 1536.00;
    private final Canvas tempCanvas;
    private final Bitmap tempBitmap;
    private final Bitmap mapBitmap;
    private final Bitmap playerBitmap;
    private final Drawable gpsPoint;
    private final Drawable emptyCircle;
    private Drawable playerPosition;

    private boolean currentColor = false;

    private final int playerColor;
    private final int blackColor;
    private final int redColor;
    private final int blueColor;
    private final Context context;

    final double leftUpperCornerLatitude = 54.778514; // Y
    final double leftUpperCornerLongitude = 9.442749; // X

    final double rightBottomCornerLatitude = 54.769009; // Y 54.76910424417393, 9.464620334246185
    final double rightBottomCornerLongitude = 9.464722; // X

    public BitmapMap(Context context){

        this.context = context;

        gpsPoint = ResourcesCompat.getDrawable(context.getResources(), R.drawable.location_icon, null);
        emptyCircle = ResourcesCompat.getDrawable(context.getResources(), R.drawable.empty_circle, null);
        playerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.navigation_arrow);
        playerColor = ContextCompat.getColor(context, R.color.player);
        blackColor = ContextCompat.getColor(context, R.color.black);
        redColor = ContextCompat.getColor(context, R.color.red_team);
        blueColor = ContextCompat.getColor(context, R.color.blue_team);

        mapBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.campuskarte);
        tempBitmap = Bitmap.createBitmap(mapBitmap.getWidth(), mapBitmap.getHeight(), Bitmap.Config.ARGB_8888); //Bitmap.Config.RGB_565
        bmWidth = tempBitmap.getWidth();
        bmHeight = tempBitmap.getHeight();
        tempCanvas = new Canvas(tempBitmap);
        //Draw the image bitmap into the canvas
        tempCanvas.drawBitmap(mapBitmap, 0, 0, null);
    }

    private static Bitmap rotateImage(Bitmap src, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    public void resetCanvas() {
        tempCanvas.drawBitmap(mapBitmap, 0, 0, null);
    }

    public void drawGameGpsPoints(ArrayList<GameGpsPoint> points) {
        currentColor = !currentColor;
        points.forEach(point -> {
            switch(point.team) {
                case -1: // fight in progress
                    drawGpsPosition(point.latitude, point.longitude, currentColor ? blueColor : redColor);
                    break;
                case 0: // neutral
                    drawGpsPosition(point.latitude, point.longitude, blackColor);
                    break;
                case 1: // team red
                    drawGpsPosition(point.latitude, point.longitude, redColor);
                    break;
                case 2: //  team blue
                    drawGpsPosition(point.latitude, point.longitude, blueColor);
                    break;
            }
        });
    }

    public void drawGpsPoints(ArrayList<GpsPoint> points) {
        points.forEach(point -> {
            drawGpsPosition(point.latitude, point.longitude, blackColor);
        });
    }

    public void drawGpsPosition(double latitude, double longitude, int color) {
        double latitude_factor = (latitude - leftUpperCornerLatitude)  / (rightBottomCornerLatitude - leftUpperCornerLatitude);
        double longitude_factor = (longitude - leftUpperCornerLongitude) / (rightBottomCornerLongitude - leftUpperCornerLongitude);
        double x = longitude_factor * bmWidth;
        double y = latitude_factor * bmHeight;

        float radius = Math.min((float) bmWidth,(float) bmHeight / 20) / 2;
        gpsPoint.setBounds((int) (x-radius), (int) (y-radius), (int) (x+radius), (int) (y+radius));
        gpsPoint.setTint(color);
        gpsPoint.draw(tempCanvas);
    }

    public void drawPlayerPosition(Location location) {
        double latitude_factor = (location.getLatitude() - leftUpperCornerLatitude)  / (rightBottomCornerLatitude - leftUpperCornerLatitude);
        double longitude_factor = (location.getLongitude() - leftUpperCornerLongitude) / (rightBottomCornerLongitude - leftUpperCornerLongitude);
        double x = longitude_factor * bmWidth;
        double y = latitude_factor * bmHeight;

        float radius = Math.min((float) bmWidth,(float) bmHeight) / 20 / 2;
        playerPosition = new BitmapDrawable(context.getResources(), rotateImage(playerBitmap, location.getBearing()));
        Log.d("BitmapMap", "Bearing: " + location.getBearing());
        Log.d("BitmapMap", "Accuracy: " + location.getAccuracy());
        playerPosition.setBounds((int) (x-radius), (int) (y-radius), (int) (x+radius), (int) (y+radius));
        playerPosition.setTint(playerColor);
        playerPosition.draw(tempCanvas);

        emptyCircle.setBounds((int) (x-radius), (int) (y-radius), (int) (x+radius), (int) (y+radius));
        emptyCircle.setTint(blackColor);
        emptyCircle.draw(tempCanvas);
    }

    public GpsPoint calculateGpsPoint(double x, double y, double viewWidth, double viewHeight) {

        double widthPercentage = x / viewWidth;
        double heightPercentage = y / viewHeight;

        //Log.d("Width and Height", "Width: " + widthPercentage + " Height: " + heightPercentage);

        double latitude = heightPercentage * (rightBottomCornerLatitude - leftUpperCornerLatitude) + leftUpperCornerLatitude;
        double longitude = widthPercentage * (rightBottomCornerLongitude - leftUpperCornerLongitude) + leftUpperCornerLongitude;

        return new GpsPoint(latitude, longitude);
    }

    public Bitmap getBitmap() {
        return tempBitmap;
    }
}
