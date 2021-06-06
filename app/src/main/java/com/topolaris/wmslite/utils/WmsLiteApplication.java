package com.topolaris.wmslite.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.topolaris.wmslite.model.user.User;

/**
 * @author toPolaris
 * description 自定义的Application类
 * @date 2021/5/19 14:41
 */
public class WmsLiteApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    private static User account;

    public static User getAccount() {
        return account;
    }

    public static void setAccount(User user) {
        account = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
