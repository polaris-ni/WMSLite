package com.topolaris.wmslite.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * @author Liangyong Ni
 * description Toast工具类
 * @date 2021/6/9 19:20
 */
public class ToastUtil {
    public static void show(String message) {
        Toast.makeText(WmsLiteApplication.context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showOnUiThread(String message, Activity activity) {
        activity.runOnUiThread(() -> show(message));
    }
}
