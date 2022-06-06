package de.hsflensburg.ctfgame.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

public class WarnCamperDialog {

    private CountdownCallback countdownCallback;
    private AlertDialog alertDialog;
    private CountDownTimer countDownTimer;

    public WarnCamperDialog(Context context, CountdownCallback countdownCallback) {
        this.countdownCallback = countdownCallback;
        createDialog(context);
    }

    private void createDialog(Context context) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Guarding the point is not allowed. \n" +
                "Please move away or your team will lose this point.");
    }

    public void show() {
        startTimer();
        alertDialog.show();
    }

    public void hide() {
        stopTimer();
        alertDialog.hide();
    }

    private void startTimer() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(90000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    alertDialog.setTitle("Alert Losing point in "+ (millisUntilFinished/1000) + "s.");
                }

                @Override
                public void onFinish() {
                    countdownCallback.onFinish();
                }
            };
            countDownTimer.start();
        }
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = null;
    }

}
