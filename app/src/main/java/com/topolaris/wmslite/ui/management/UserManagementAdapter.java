package com.topolaris.wmslite.ui.management;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.model.user.UserAuthority;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Liangyong Ni
 * description 用户管理界面RecyclerView的适配器
 * @date 2021/6/8 14:36
 */
public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.MyViewHolder> {

    private final UserManagementFragment fragment;
    private final Context context;
    private List<User> users;

    public UserManagementAdapter(UserManagementFragment fragment) {
        this.fragment = fragment;
        context = fragment.requireContext();
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_um, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        User user = users.get(position);
        setHolder(holder, user);

        holder.item.setOnClickListener(v -> {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null, false);

            initDataByUser(view, user);

            AlertDialog registerUser = new AlertDialog.Builder(context)
                    .setTitle("用户信息修改")
                    .setView(view)
                    .setNegativeButton("取消", (dialog, which) -> Toast.makeText(context, "修改取消", Toast.LENGTH_SHORT).show())
                    .setPositiveButton("修改", (dialog, which) -> {
                        user.setName(((TextView) view.findViewById(R.id.dialog_ac_name)).getText().toString());
                        user.setPassword(((TextView) view.findViewById(R.id.dialog_ac_password)).getText().toString());
                        ThreadPool.EXECUTOR.execute(() -> {
                            String sql = "update wmsusers set uName = " + "\"" + user.getName() + "\"," + "uPassword = " + "\"" + user.getPassword() + "\"," + "authority = " + user.getAuthority() + " where uid = " + "\"" + user.getUid() + "\";";
                            String message = DatabaseUtil.executeSqlWithoutResult(sql) ? "修改成功" : "修改失败，请重试";
                            fragment.requireActivity().runOnUiThread(() -> {
                                setHolder(holder, user);
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            });
                        });
                    })
                    .setNeutralButton("删除", (dialog, which) -> {
                        user.setName(((TextView) view.findViewById(R.id.dialog_ac_name)).getText().toString());
                        user.setPassword(((TextView) view.findViewById(R.id.dialog_ac_password)).getText().toString());
                        ThreadPool.EXECUTOR.execute(() -> {
                            String sql = "delete from wmsusers where uid = " + "\"" + user.getUid() + "\";";
                            String message = DatabaseUtil.executeSqlWithoutResult(sql) ? "删除成功" : "删除失败，请重试";
                            fragment.requireActivity().runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
                            fragment.getViewModel().refresh();
                        });
                    })
                    .create();
            registerUser.show();
        });
    }

    private void setHolder(MyViewHolder holder, User user) {
        holder.name.setText(user.getName());
        holder.uid.setText(user.getUid());
        holder.authority.setText(user.getAuthorityString());
        holder.password.setText(user.getPassword());
    }

    private void initDataByUser(View view, User user) {
        ((RadioButton) view.findViewById(getResourceIdByAuthority(user.getAuthority()))).setChecked(true);
        ((TextView) view.findViewById(R.id.dialog_ac_uid_title)).setText(R.string.dialog_ac_uid_title2);
        ((TextView) view.findViewById(R.id.dialog_ac_uid)).setText(user.getUid());
        ((EditText) view.findViewById(R.id.dialog_ac_name)).setText(user.getName());
        ((EditText) view.findViewById(R.id.dialog_ac_password)).setText(user.getPassword());
        ((RadioGroup) view.findViewById(R.id.dialog_ac_rg)).setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.dialog_ac_rb_a) {
                user.setAuthority(UserAuthority.ADMINISTRATOR);
            } else if (checkedId == R.id.dialog_ac_rb_p) {
                user.setAuthority(UserAuthority.PURCHASER);
            } else if (checkedId == R.id.dialog_ac_rb_s) {
                user.setAuthority(UserAuthority.SHIPMENT);
            } else if (checkedId == R.id.dialog_ac_rb_c) {
                user.setAuthority(UserAuthority.CHECKER);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private int getResourceIdByAuthority(int authority) {
        if (authority == UserAuthority.ADMINISTRATOR) {
            return R.id.dialog_ac_rb_a;
        } else if (authority == UserAuthority.PURCHASER) {
            return R.id.dialog_ac_rb_p;
        } else if (authority == UserAuthority.SHIPMENT) {
            return R.id.dialog_ac_rb_s;
        } else {
            return R.id.dialog_ac_rb_c;
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView item;
        private final TextView name, authority, uid, password;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_rv_um_mcv);
            name = itemView.findViewById(R.id.item_um_name);
            authority = itemView.findViewById(R.id.item_um_authority);
            uid = itemView.findViewById(R.id.item_um_uid);
            password = itemView.findViewById(R.id.item_um_password);
        }
    }
}
