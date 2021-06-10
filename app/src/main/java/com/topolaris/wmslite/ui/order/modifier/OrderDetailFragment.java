package com.topolaris.wmslite.ui.order.modifier;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.goods.Goods;
import com.topolaris.wmslite.model.order.Order;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.model.user.UserAuthority;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;
import com.topolaris.wmslite.utils.ToastUtil;
import com.topolaris.wmslite.utils.WmsLiteApplication;

/**
 * @author Liangyong Ni
 * description 订单详情界面
 * @date 2021/6/9 20:20:32
 */
public class OrderDetailFragment extends Fragment {
    private static final String TAG = "OrderDetailFragment";
    private final User account = WmsLiteApplication.getAccount();
    private Goods goods;
    private Order order;
    private TextView number, state;
    private ImageView modify;
    private Button delete, save, shortage, revoke, ensure;
    private String tableName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            goods = getArguments().getParcelable("goods");
            order = getArguments().getParcelable("order");
            tableName = getArguments().getString("table");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        hideSpecificView();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void hideSpecificView() {
        String shortageTableName = "shortage";
        if (order.isExecuted() || tableName.equals(shortageTableName)) {
            delete.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            shortage.setVisibility(View.GONE);
            revoke.setVisibility(View.GONE);
            ensure.setVisibility(View.GONE);
            modify.setVisibility(View.GONE);
            if (tableName.equals(shortageTableName)) {
                state.setVisibility(View.GONE);
                if (WmsLiteApplication.getAccount().getAuthority() == UserAuthority.PURCHASER) {
                    delete.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(v -> showConfirmDialog("delete from " + tableName + " where id = " + order.getId()));
                }
            }
        } else {
            setClickListener();
            String shipmentTableName = "shipment";
            boolean confirmable = order.getNumber() > goods.getInventory() && tableName.equals(shipmentTableName);
            if (confirmable || WmsLiteApplication.getAccount().getAuthority() != UserAuthority.CHECKER) {
                ensure.setBackground(requireActivity().getDrawable(R.drawable.bg_button_off));
                ensure.setClickable(false);
            }
        }
    }

    private void setClickListener() {
        delete.setOnClickListener(v -> showConfirmDialog("delete from " + tableName + " where id = " + order.getId()));
        save.setOnClickListener(v -> showConfirmDialog("update " + tableName + " set number = " + Long.parseLong(number.getText().toString()) + " where id = " + order.getId()));
        modify.setOnClickListener(v -> setDialogTextToTextView(number, editTextString -> order.setNumber(Long.parseLong(editTextString))));
        shortage.setOnClickListener(v -> makeOrder());
        revoke.setOnClickListener(v -> showConfirmDialog("update " + tableName + " set executed = 1, revoked = 1 where id = " + order.getId()));
        ensure.setOnClickListener(v -> showConfirmDialog("update " + tableName + " set executed = 1 where id = " + order.getId()));
    }

    private void initView() {
        TextView name = requireView().findViewById(R.id.order_detail_goods_name);
        TextView inventory = requireView().findViewById(R.id.order_detail_goods_inventory);
        TextView date = requireView().findViewById(R.id.order_detail_date);
        state = requireView().findViewById(R.id.order_detail_state);
        number = requireView().findViewById(R.id.order_detail_number);
        delete = requireView().findViewById(R.id.order_detail_delete);
        save = requireView().findViewById(R.id.order_detail_save);
        shortage = requireView().findViewById(R.id.order_detail_shortage);
        revoke = requireView().findViewById(R.id.order_detail_revoke);
        ensure = requireView().findViewById(R.id.order_detail_ensure);
        modify = requireView().findViewById(R.id.order_detail_modify);
        name.setText(goods.getName());
        inventory.setText(String.valueOf(goods.getInventory()));
        number.setText(String.valueOf(order.getNumber()));
        date.setText(order.getDate());
        state.setText(order.getStateString());
    }

    private void showConfirmDialog(String sql) {
        Log.e(TAG, "showConfirmDialog: Here");
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input, null, false);
        EditText editText = dialogView.findViewById(R.id.dialog_input_et);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("请输入密码确认")
                .setNegativeButton("取消", (dialog, which) -> ToastUtil.show("修改取消"))
                .setPositiveButton("确认", (dialog, which) -> {
                    if (TextUtils.isEmpty(editText.getText())) {
                        ToastUtil.show("输入不合法");
                    } else {
                        checkPassword(editText.getText().toString(), sql);
                    }
                })
                .create();
        alertDialog.show();
    }

    private void makeOrder() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input, null, false);
        EditText editText = dialogView.findViewById(R.id.dialog_input_et);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("请输入")
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .setPositiveButton("确认", (dialog, which) -> {
                    Editable text = editText.getText();
                    if (TextUtils.isEmpty(text) || !TextUtils.isDigitsOnly(text) || Long.parseLong(text.toString()) <= 0) {
                        ToastUtil.show("输入不合法");
                    } else {
                        String sql = "insert into shortage(goods_id, number, date) values(" + goods.getIndex() + ", " + Long.parseLong(text.toString()) + ", CURDATE())";
                        ThreadPool.EXECUTOR.execute(() -> ToastUtil.showOnUiThread(DatabaseUtil.executeSqlWithoutResult(sql) ? "订单建立成功" : "订单建立失败请重试", requireActivity()));
                    }
                })
                .create();
        alertDialog.show();
    }

    private void setDialogTextToTextView(TextView textView, OnDialogEnsureListener onDialogEnsureListener) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input, null, false);
        EditText editText = dialogView.findViewById(R.id.dialog_input_et);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("请输入")
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .setPositiveButton("确认", (dialog, which) -> {
                    if (TextUtils.isEmpty(editText.getText())) {
                        ToastUtil.show("输入不合法");
                    } else {
                        textView.setText(editText.getText().toString().trim());
                        onDialogEnsureListener.executeTask(editText.getText().toString());
                    }
                })
                .create();
        alertDialog.show();
    }

    private void checkPassword(String password, String sql) {
        if (account.getPassword().equals(password)) {
            ThreadPool.EXECUTOR.execute(() -> {
                if (DatabaseUtil.executeSqlWithoutResult(sql)) {
                    requireActivity().runOnUiThread(() -> {
                        ToastUtil.show("操作成功");
                        Navigation.findNavController(requireView()).popBackStack();
                    });
                } else {
                    ToastUtil.showOnUiThread("操作失败，请重试", requireActivity());
                }
            });
        } else {
            ToastUtil.show("密码输入错误，删除失败");
        }
    }

    private interface OnDialogEnsureListener {
        /**
         * 点击对话框确认按钮后，回调此方法
         *
         * @param editTextString 对话框输入的字符串
         */
        void executeTask(String editTextString);
    }
}