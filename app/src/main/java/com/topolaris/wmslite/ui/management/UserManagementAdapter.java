package com.topolaris.wmslite.ui.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.user.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Liangyong Ni
 * description 用户管理界面RecyclerView的适配器
 * @date 2021/6/8 14:36
 */
public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.MyViewHolder> {

    private List<User> users;
    private Context context;

    public UserManagementAdapter(Context context) {
        this.context = context;
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
        holder.name.setText(user.getName());
        holder.uid.setText(user.getUid());
        holder.authority.setText(user.getAuthorityString());
        holder.password.setText(user.getPassword());
        holder.item.setOnClickListener(v -> {
            // TODO: 2021/6/8 编辑用户逻辑
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
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
