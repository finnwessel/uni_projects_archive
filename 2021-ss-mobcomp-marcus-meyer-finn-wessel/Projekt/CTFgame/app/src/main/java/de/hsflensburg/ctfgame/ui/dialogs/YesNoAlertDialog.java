package de.hsflensburg.ctfgame.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class YesNoAlertDialog {
    private AlertDialog dialog;
    private DialogCallback callback;

    public YesNoAlertDialog(Context context, DialogCallback callback) {
        this.callback = callback;
        createDialog(context);
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.hide();
    }

    private void createDialog(Context context) {
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set a title for alert dialog
        builder.setTitle("Warning");

        // Ask the final question
        builder.setMessage("Are you sure to cancel the game?");

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.accept();
            }
        });

        // Set the alert dialog no button click listener
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.decline();
                // Do something when No button clicked
                Toast.makeText(context,
                        "No Button Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        dialog = builder.create();
        // Display the alert dialog on interface
    }
}
