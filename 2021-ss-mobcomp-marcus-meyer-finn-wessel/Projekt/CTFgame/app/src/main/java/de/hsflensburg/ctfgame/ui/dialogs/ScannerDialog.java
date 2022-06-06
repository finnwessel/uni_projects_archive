package de.hsflensburg.ctfgame.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;

public class ScannerDialog {

    private CountdownCallback countdownCallback;
    private AlertDialog alertDialog;
    private CountDownTimer countDownTimer;

    public ScannerDialog(Context context, CountdownCallback countdownCallback) {
        this.countdownCallback = countdownCallback;
        createDialog(context);
    }

    private void createDialog(Context context) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Scanning");
        alertDialog.setMessage("Scanning devices nearby");
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
            countDownTimer = new CountDownTimer(6000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    alertDialog.setTitle("Scanning for "+ (millisUntilFinished/1000) + "s.");
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
