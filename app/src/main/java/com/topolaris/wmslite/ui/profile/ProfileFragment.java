package com.topolaris.wmslite.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.model.user.UserAuthority;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.ui.login.LoginActivity;
import com.topolaris.wmslite.utils.ThreadPool;
import com.topolaris.wmslite.utils.ToastUtil;
import com.topolaris.wmslite.utils.WmsLiteApplication;

import java.util.ArrayList;

/**
 * @author Liangyong Ni
 * description 用户信息界面
 * @date 2021/6/9 16:00:09
 */
public class ProfileFragment extends Fragment {
    private final String space = " ";
    private TextView username;
    private MaterialCardView userManagement;
    private MaterialCardView mName, mPassword, logout, logoff;
    private User account;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        account = WmsLiteApplication.getAccount();
        initView();
        setClickListener();
    }

    private void setClickListener() {
        if (account.getAuthority() != UserAuthority.ADMINISTRATOR) {
            userManagement.setVisibility(View.GONE);
        } else {
            userManagement.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_fragment_profile_to_fragment_user_management));
        }
        logout.setOnClickListener(v -> returnToLogin());
        mName.setOnClickListener(v -> showModifyDialog(0));
        mPassword.setOnClickListener(v -> showModifyDialog(1));
        logoff.setOnClickListener(v -> showModifyDialog(2));
    }

    private void returnToLogin() {
        WmsLiteApplication.setAccount(null);
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    private void initView() {
        User account = WmsLiteApplication.getAccount();
        username = requireView().findViewById(R.id.profile_tv_username);
        TextView authority = requireView().findViewById(R.id.profile_tv_authority);
        TextView uid = requireView().findViewById(R.id.profile_tv_uid);
        username.setText(account.getName());
        authority.setText(account.getAuthorityString());
        uid.setText(account.getUid());

        userManagement = requireView().findViewById(R.id.profile_mcv_um);
        mName = requireView().findViewById(R.id.profile_mcv_name);
        mPassword = requireView().findViewById(R.id.profile_mcv_pw);
        logoff = requireView().findViewById(R.id.profile_mcv_log_off);
        logout = requireView().findViewById(R.id.profile_mcv_log_out);
    }

    private void showModifyDialog(int type) {
        String[] titles = {"请输入新用户名", "请输入新密码", "请输入密码"};
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input, null, false);
        EditText editText = dialogView.findViewById(R.id.dialog_input_et);
        if (type == 0) {
            editText.setText(WmsLiteApplication.getAccount().getName());
        }
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle(titles[type])
                .setNegativeButton("取消", (dialog, which) -> ToastUtil.show("修改取消"))
                .setPositiveButton("确认", (dialog, which) -> {
                    if (TextUtils.isEmpty(editText.getText())) {
                        ToastUtil.show("输入不合法");
                    } else {
                        if (type == 0) {
                            modifyUsername(editText.getText().toString());
                        } else if (type == 1) {
                            modifyPassword(editText.getText().toString());
                        } else {
                            deleteUser(editText.getText().toString());
                        }
                    }
                })
                .create();
        alertDialog.show();
    }

    private void deleteUser(String password) {
        if (account.getPassword().equals(password)) {
            ThreadPool.EXECUTOR.execute(() -> {
                String querySql = "select * from wmsusers where uid = \"" + account.getUid() + "\";";
                ArrayList<User> result = DatabaseUtil.executeSqlWithResult(querySql, User.class);
                if (result == null) {
                    ToastUtil.showOnUiThread("网络错误，修改失败",requireActivity());
                } else if (result.isEmpty()) {
                    ToastUtil.showOnUiThread("用户已删除",requireActivity());
                    returnToLogin();
                } else {
                    String sql = "delete from wmsusers where uid = \"" + account.getUid() + "\";";
                    if (DatabaseUtil.executeSqlWithoutResult(sql)) {
                        ToastUtil.showOnUiThread("删除成功",requireActivity());
                        returnToLogin();
                    } else {
                        ToastUtil.showOnUiThread("删除失败，请重试",requireActivity());
                    }
                }
            });
        } else {
            ToastUtil.show("密码输入错误，删除失败");
        }
    }

    private void modifyUsername(String newUsername) {
        if (newUsername.equals(account.getName())) {
            ToastUtil.show("新用户名不能与原用户名相同");
        } else if (newUsername.contains(space)) {
            ToastUtil.show("用户名中不能含有空格");
        } else {
            ThreadPool.EXECUTOR.execute(() -> {
                String querySql = "select * from wmsusers where uName = \"" + username + "\";";
                ArrayList<User> result = DatabaseUtil.executeSqlWithResult(querySql, User.class);
                if (result == null) {
                    ToastUtil.showOnUiThread("网络错误，修改失败", requireActivity());
                } else if (!result.isEmpty()) {
                    ToastUtil.showOnUiThread("用户名重复，修改失败", requireActivity());
                } else {
                    String sql = "update wmsusers set uName = \"" + newUsername + "\" where uid = \"" + account.getUid() + "\";";
                    if (DatabaseUtil.executeSqlWithoutResult(sql)) {
                        requireActivity().runOnUiThread(() -> {
                            username.setText(newUsername);
                            ToastUtil.show("修改成功");
                        });
                        WmsLiteApplication.getAccount().setName(newUsername);
                    } else {
                        ToastUtil.showOnUiThread("修改失败，请重试", requireActivity());
                    }
                }
            });
        }
    }

    private void modifyPassword(String newPassword) {
        if (newPassword.equals(account.getPassword())) {
            ToastUtil.show("新密码不能与原密码相同");
        } else if (newPassword.contains(space)) {
            ToastUtil.show("密码中不能含有空格");
        } else {
            ThreadPool.EXECUTOR.execute(() -> {
                String sql = "update wmsusers set uPassword = \"" + newPassword + "\" where uid = \"" + account.getUid() + "\";";
                if (DatabaseUtil.executeSqlWithoutResult(sql)) {
                    account.setPassword(newPassword);
                    ToastUtil.showOnUiThread("修改成功", requireActivity());
                } else {
                    ToastUtil.showOnUiThread("修改失败，请重试", requireActivity());
                }
            });
        }
    }
}