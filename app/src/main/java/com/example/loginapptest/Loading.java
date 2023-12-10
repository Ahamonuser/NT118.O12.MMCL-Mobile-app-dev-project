package com.example.loginapptest;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class Loading {
    private Activity activity;
    private AlertDialog dialog;
    Loading(Activity myactivity)
    {
        activity=myactivity;
    }
    void startLoading()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setCancelable(false);
        dialog= builder.create();
        dialog.show();
    }
    void DismissLoading()
    {
        dialog.dismiss();
    }
}
