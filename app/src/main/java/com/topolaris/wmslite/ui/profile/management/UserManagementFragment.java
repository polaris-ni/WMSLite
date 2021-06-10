package com.topolaris.wmslite.ui.profile.management;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.model.user.UserAuthority;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;
import com.topolaris.wmslite.utils.WmsLiteApplication;

import java.util.UUID;

/**
 * @author Liangyong Ni
 * description 用户管理界面
 * @date 2021/6/8 14:36
 */

public class UserManagementFragment extends Fragment {

    private UserManagementViewModel mViewModel;
    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private AlertDialog registerUser;

    public UserManagementViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_management, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(UserManagementViewModel.class);
        initView();

        UserManagementAdapter adapter = new UserManagementAdapter(this);
        adapter.setUsers(mViewModel.getUsers().getValue());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        mViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            adapter.setUsers(users);
            adapter.notifyDataSetChanged();
        });

        refresh.setOnRefreshListener(() -> {
            mViewModel.refresh();
            refresh.setRefreshing(false);
        });


    }

    @SuppressLint("NonConstantResourceId")
    private void initView() {
        refresh = requireView().findViewById(R.id.um_swipe_refresh);
        recyclerView = requireView().findViewById(R.id.um_rv);
        if (WmsLiteApplication.getAccount().getAuthority() == UserAuthority.ADMINISTRATOR) {
            MaterialCardView addUser = requireView().findViewById(R.id.um_mcv_add);
            addUser.setOnClickListener(v -> {
                View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_account, null, false);
                User newUser = new User();
                newUser.setAuthority(UserAuthority.CHECKER);
                newUser.setUid(UUID.randomUUID().toString());
                ((TextView) view.findViewById(R.id.dialog_aa_tv_uid)).setText(newUser.getUid());
                RadioGroup radioGroup = view.findViewById(R.id.dialog_aa_rg);
                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId == R.id.dialog_aa_rb_a) {
                        newUser.setAuthority(UserAuthority.ADMINISTRATOR);
                    } else if (checkedId == R.id.dialog_aa_rb_p) {
                        newUser.setAuthority(UserAuthority.PURCHASER);
                    } else if (checkedId == R.id.dialog_aa_rb_s) {
                        newUser.setAuthority(UserAuthority.SHIPMENT);
                    } else {
                        newUser.setAuthority(UserAuthority.CHECKER);
                    }
                });
                registerUser = new AlertDialog.Builder(requireContext())
                        .setTitle("用户注册")
                        .setView(view)
                        .setNegativeButton("取消", (dialog, which) -> Toast.makeText(requireContext(), "注册取消", Toast.LENGTH_SHORT).show())
                        .setPositiveButton("确定", (dialog, which) -> {
                            String userName = ((TextView) view.findViewById(R.id.dialog_aa_et_name)).getText().toString();
                            String userPassword = ((TextView) view.findViewById(R.id.dialog_aa_et_password)).getText().toString();
                            newUser.setName(userName);
                            newUser.setPassword(userPassword);
                            ThreadPool.EXECUTOR.execute(() -> {
                                String sql = "insert into wmsusers values" + newUser.getSql();
                                String message = DatabaseUtil.executeSqlWithoutResult(sql) ? "注册成功" : "注册失败";
                                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                            });
                        })
                        .create();
                registerUser.show();
            });
        }
    }
}