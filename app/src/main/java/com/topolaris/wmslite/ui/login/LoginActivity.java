package com.topolaris.wmslite.ui.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.topolaris.wmslite.MainActivity;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.repository.local.Cache;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
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

        initView();
        restoreData();

        loginButton.setOnClickListener(v -> {
            waitingDialog.show();
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            checkAccount(username, password);
        });
        forgot.setOnClickListener(v -> Toast.makeText(WmsLiteApplication.context, "请使用Uid登录或者向管理员查询密码", Toast.LENGTH_LONG).show());
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
        waitingDialog = new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Waiting")
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null, false))
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void checkAccount(String username, String password) {
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
                // TODO: 2021/6/7 修改数据库登录账号
                user = currentUser;
                // 根据登录用户权限请求相应数据到本地
                Cache.setAuthority(user.getAuthority());
                Cache.updateCacheByAuthority();
                WmsLiteApplication.setAccount(currentUser);
                runOnUiThread(() -> Toast.makeText(WmsLiteApplication.context, currentUser.getAuthorityString(), Toast.LENGTH_SHORT).show());
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