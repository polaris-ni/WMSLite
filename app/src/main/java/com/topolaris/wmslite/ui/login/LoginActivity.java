package com.topolaris.wmslite.ui.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.repository.local.Cache;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.ui.main.MainActivity;
import com.topolaris.wmslite.utils.DialogUtil;
import com.topolaris.wmslite.utils.ThreadPool;
import com.topolaris.wmslite.utils.ToastUtil;
import com.topolaris.wmslite.utils.WmsLiteApplication;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Liangyong Ni
 * description 登录界面
 * @date 2021/6/7 14:32
 */
public class LoginActivity extends AppCompatActivity {
    TextView forgot;
    EditText usernameEditText, passwordEditText;
    Button loginButton;
    AlertDialog waitingDialog;
    SwitchMaterial switchMaterial;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(Color.GRAY);
        initView();
        restoreData();
        loginButton.setOnClickListener(v -> {
            waitingDialog.show();
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            checkAccount(username, password);
        });
        forgot.setOnClickListener(v -> ToastUtil.show("请使用Uid登录或者向管理员查询密码"));
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    /**
     * 数据恢复
     */
    private void restoreData() {
        sharedPreferences = WmsLiteApplication.context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String isRemembered = "isRemembered";
        if (sharedPreferences.getBoolean(isRemembered, false)) {
            // 上一次记住密码，根据权限请求数据
            switchMaterial.setChecked(true);
            String lastUsername = sharedPreferences.getString("username", "");
            String lastPassword = sharedPreferences.getString("password", "");
            usernameEditText.setText(lastUsername);
            passwordEditText.setText(lastPassword);
        }
    }

    @SuppressLint("InflateParams")
    private void initView() {
        forgot = findViewById(R.id.login_tv_forgot);
        usernameEditText = findViewById(R.id.login_et_username);
        passwordEditText = findViewById(R.id.login_et_password);
        loginButton = findViewById(R.id.login_btn_login);
        switchMaterial = findViewById(R.id.login_sm_rem_pw);
        waitingDialog = DialogUtil.getWaitingDialog(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void checkAccount(String username, String password) {
        // 设置连接为管理员权限连接
        DatabaseUtil.setConnectorUserWithAdmin();
        String querySql = "select * from wmsusers where uName = \"" + username + "\";";
        ThreadPool.EXECUTOR.execute(() -> {
            User user = new User(username, password);
            User currentUser;
            ArrayList<User> result = DatabaseUtil.executeSqlWithResult(querySql, User.class);
            if (result == null) {
                runOnUiThread(() -> {
                    waitingDialog.dismiss();
                    ToastUtil.show("账号列表获取失败");
                });
                return;
            } else if (result.isEmpty()) {
                // 账户列表为空
                runOnUiThread(() -> {
                    waitingDialog.dismiss();
                    ToastUtil.show("账户不存在");
                });
                return;
            } else {
                currentUser = result.get(0);
            }
            if (!user.match(currentUser)) {
                // 如果密码或UID与账号不匹配，则弹出提示
                runOnUiThread(() -> {
                    waitingDialog.dismiss();
                    ToastUtil.show("密码或UID错误");
                });
                saveNameAndPassword(user, switchMaterial);
            } else {
                // 账号密码正确
                user = currentUser;
                DatabaseUtil.setConnectorUser(user);
                // 根据登录用户权限请求相应数据到本地
                Cache.setAuthority(user.getAuthority());
                Cache.updateCacheByAuthority();
                WmsLiteApplication.setAccount(currentUser);
                ToastUtil.showOnUiThread(currentUser.getAuthorityString() + "，你好！", this);
                saveNameAndPassword(user, switchMaterial);
                runOnUiThread(() -> waitingDialog.dismiss());
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void saveNameAndPassword(User user, SwitchMaterial switchMaterial) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (switchMaterial.isChecked()) {
            editor.putBoolean("isRemembered", true);
            editor.putString("username", user.getName());
            editor.putString("password", user.getPassword());
        } else {
            editor.putBoolean("isRemembered", false);
        }
        editor.apply();
    }
}