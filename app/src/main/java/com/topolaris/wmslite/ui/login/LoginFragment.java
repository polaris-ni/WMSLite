package com.topolaris.wmslite.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.event.MessageEvent;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;
import com.topolaris.wmslite.utils.WMSLiteApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.topolaris.wmslite.model.event.MessageType.LOGIN_NAVIGATE_BACK;
import static com.topolaris.wmslite.model.event.MessageType.MAIN_TOAST_MAKER;

/**
 * @author toPolaris
 */

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    SharedPreferences.Editor editor;
    SwitchMaterial switchMaterial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //todo:考虑将数据库远程查询功能提前，减少UI卡顿
        super.onActivityCreated(savedInstanceState);

        EventBus.getDefault().register(this);
        TextView forgot = requireView().findViewById(R.id.login_tv_forgot);
        EditText usernameEditText = requireView().findViewById(R.id.login_et_username);
        EditText passwordEditText = requireView().findViewById(R.id.login_et_password);
        Button loginButton = requireView().findViewById(R.id.login_btn_login);
        switchMaterial = requireView().findViewById(R.id.login_sm_rem_pw);

        SharedPreferences sharedPreferences = WMSLiteApplication.context.getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String isRemembered = "isRemembered";
        if (sharedPreferences.getBoolean(isRemembered, false)) {
            switchMaterial.setChecked(true);
            String lastUsername = sharedPreferences.getString("username", "");
            String lastPassword = sharedPreferences.getString("password", "");
            usernameEditText.setText(lastUsername);
            passwordEditText.setText(lastPassword);
        }

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            checkAccount(username, password);
        });

        forgot.setOnClickListener(v -> {
            Toast.makeText(WMSLiteApplication.context, "请使用Uid登录或者向管理员查询密码", Toast.LENGTH_LONG).show();
        });
    }

    private void checkAccount(String username, String password) {
        ThreadPool.executor.execute(() -> {
            User user = new User(username, password);
            String querySql = "select * from wmsusers where uName = \"" + username + "\" and uPassword = \"" + password + "\";";
            ThreadPool.executor.execute(() -> {
                ArrayList<User> result = DatabaseUtil.executeSqlWithResult(querySql, User.class);
                User u = result.get(0);
                if (u.equals(user)) {
                    if (u.isOnline()) {
                        MessageEvent messageEvent = new MessageEvent.Builder(MAIN_TOAST_MAKER).setMessage("账号已在别处登录").build();
                        EventBus.getDefault().post(messageEvent);
                    } else {
                        u.setOnline(true);
                        WMSLiteApplication.setAccount(u);
                        EventBus.getDefault().postSticky(new MessageEvent.Builder(LOGIN_NAVIGATE_BACK).build());
                        MessageEvent messageEvent = new MessageEvent.Builder(MAIN_TOAST_MAKER).setMessage(u.getAuthorityString()).build();
                        EventBus.getDefault().post(messageEvent);
                        // 状态设置为上线，需要更新本地账户缓存
                        String sql = "update wmsusers set online = 1 where uName = \"" + u.getName() + "\" and uPassword = \"" + u.getPassword() + "\";";
                        DatabaseUtil.executeSqlWithoutResult(sql);
                    }
                } else {
                    MessageEvent messageEvent = new MessageEvent.Builder(MAIN_TOAST_MAKER).setMessage("账户不存在或密码错误").build();
                    EventBus.getDefault().post(messageEvent);
                }
                saveNameAndPassword(username, password, editor, switchMaterial);
            });
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void saveNameAndPassword(String username, String password, SharedPreferences.Editor editor, SwitchMaterial switchMaterial) {
        if (switchMaterial.isChecked()) {
            editor.putBoolean("isRemembered", true);
            editor.putString("username", username);
            editor.putString("password", password);
        } else {
            editor.putBoolean("isRemembered", false);
        }
        editor.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loginMessageHandler(MessageEvent messageEvent) {
        switch (messageEvent.getMessageType()) {
            case LOGIN_NAVIGATE_BACK:
                Navigation.findNavController(requireView()).popBackStack();
                break;
            default:
                break;
        }
    }
}