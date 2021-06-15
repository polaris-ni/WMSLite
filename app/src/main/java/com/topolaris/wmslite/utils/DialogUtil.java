package com.topolaris.wmslite.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.topolaris.wmslite.R;

/**
 * @author Liangyong Ni
 * description TODO
 * @date 2021/6/10 21:14
 */
public class DialogUtil {

    @SuppressLint("InflateParams")
    public static AlertDialog getWaitingDialog(Context context) {
            return new AlertDialog.Builder(context)
                    .setTitle("Waiting")
                    .setView(LayoutInflater.from(context).inflate(R.layout.dialog_waiting, null, false))
                    .create();
    }
}
