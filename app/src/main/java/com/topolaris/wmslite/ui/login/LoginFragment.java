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
import com.topolaris.wmslite.model.user.UserAuthority;
import com.topolaris.wmslite.repository.local.Cache;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;
import com.topolaris.wmslite.utils.WmsLiteApplication;

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
    SwitchMaterial switchMaterial;
    SharedPreferences sharedPreferences;

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

        sharedPreferences = WmsLiteApplication.context.getSharedPreferences("config", Context.MODE_PRIVATE);

        String isRemembered = "isRemembered";
        // 做数据恢复工作
        if (sharedPreferences.getBoolean(isRemembered, false)) {
            // 上一次记住密码，根据权限请求数据
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

        forgot.setOnClickListener(v -> Toast.makeText(WmsLiteApplication.context, "请使用Uid登录或者向管理员查询密码", Toast.LENGTH_LONG).show());
    }

    private void checkAccount(String username, String password) {
        ThreadPool.executor.execute(() -> {
            String querySql = "select * from wmsusers where uName = \"" + username + "\";";
            ThreadPool.executor.execute(() -> {
                User user = new User(username, password);
                User u;
                ArrayList<User> result = DatabaseUtil.executeSqlWithResult(querySql, User.class);
                if (result == null) {
                    EventBus.getDefault().post(new MessageEvent.Builder(MAIN_TOAST_MAKER).setMessage("账号列表获取失败").build());
                    return;
                } else if (result.isEmpty()) {
                    // 账户列表为空
                    EventBus.getDefault().post(new MessageEvent.Builder(MAIN_TOAST_MAKER).setMessage("账户不存在").build());
                    return;
                } else {
                    u = result.get(0);
                }
                if (!user.equals(u)) {
                    // 如果密码或UID与账号不匹配，则弹出提示
                    EventBus.getDefault().post(new MessageEvent.Builder(MAIN_TOAST_MAKER).setMessage("密码或UID错误").build());
                } else {
                    // 账号密码正确，接下来判断账号是否已经登录
                    if (u.isOnline()) {
                        // 账号在别处登录
                        MessageEvent messageEvent = new MessageEvent.Builder(MAIN_TOAST_MAKER).setMessage("账号已在别处登录").build();
                        EventBus.getDefault().post(messageEvent);
                    } else {
                        // 账号正常登录，设置在线状态为true，并设置当前登录账户
                        u.setOnline(true);
                        WmsLiteApplication.setAccount(u);
                        EventBus.getDefault().postSticky(new MessageEvent.Builder(LOGIN_NAVIGATE_BACK).build());
                        MessageEvent messageEvent = new MessageEvent.Builder(MAIN_TOAST_MAKER).setMessage(u.getAuthorityString()).build();
                        EventBus.getDefault().post(messageEvent);
                        // 状态设置为上线
                        String sql = "update wmsusers set online = 1 where uName = \"" + u.getName() + "\" and uPassword = \"" + u.getPassword() + "\";";
                        DatabaseUtil.executeSqlWithoutResult(sql);
                        user = u;
                        // 根据登录用户权限请求相应数据到本地
                        if (user.getAuthority() == UserAuthority.CHECKER || user.getAuthority() == UserAuthority.ADMINISTRATOR) {
                            Cache.updateAllCache();
                        } else if (user.getAuthority() == UserAuthority.PURCHASER) {
                            Cache.updateOrdersCache();
                        } else if (user.getAuthority() == UserAuthority.SHIPMENT) {
                            Cache.updateShipmentsCache();
                        }
                    }
                }
                saveNameAndPassword(user, switchMaterial);
            });
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loginMessageHandler(MessageEvent messageEvent) {
        if (messageEvent.getMessageType() == LOGIN_NAVIGATE_BACK) {
            Navigation.findNavController(requireView()).popBackStack();
        }
    }
}