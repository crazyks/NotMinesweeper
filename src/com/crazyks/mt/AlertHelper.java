package com.crazyks.mt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertHelper {
    public static AlertDialog createAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener listener) { 
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setInverseBackgroundForced(true);
        alert.setCancelable(false);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.close), listener);
        return alert;
    }

    public static AlertDialog createWarningDialog(Context context, String title, String message, DialogInterface.OnClickListener listener) { 
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setInverseBackgroundForced(true);
        alert.setCancelable(false);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok), listener);
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel), listener);
        return alert;
    }
}
