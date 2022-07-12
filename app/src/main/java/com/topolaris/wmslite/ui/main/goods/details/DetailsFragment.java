package com.topolaris.wmslite.ui.main.goods.details;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
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

import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.goods.Goods;
import com.topolaris.wmslite.model.user.UserAuthority;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;
import com.topolaris.wmslite.utils.ToastUtil;
import com.topolaris.wmslite.utils.WmsLiteApplication;

import java.util.ArrayList;

/**
 * @author Liangyong Ni
 * description 显示商品详细信息
 * @date 2021/5/19 14:32
 */
public class DetailsFragment extends Fragment {
    private Goods goods;
    private TextView id, name, inventory, sold, manufacturer, location;
    private Button purchaseButton, shipmentButton, saveButton, deleteButton, shortageButton;
    private boolean isAdd;
    private int authority;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            goods = getArguments().getParcelable("GOODS");
            isAdd = false;
        } else {
            goods = new Goods();
            isAdd = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        authority = WmsLiteApplication.getAccount().getAuthority();
        initView();
        initData();
        setTextViewClickListener();
        setButtonClickListener();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setButtonClickListener() {
        if (authority == UserAuthority.PURCHASER) {
            purchaseButton.setClickable(true);
            purchaseButton.setBackground(requireActivity().getDrawable(R.drawable.bg_button_on));
            purchaseButton.setOnClickListener(v -> makeOrder("purchase"));
        } else if (authority == UserAuthority.SHIPMENT) {
            shipmentButton.setClickable(true);
            shipmentButton.setBackground(requireActivity().getDrawable(R.drawable.bg_button_on));
            shipmentButton.setOnClickListener(v -> makeOrder("shipment"));
        }
        saveButton.setOnClickListener(v -> {
            goods.setName(name.getText().toString());
            goods.setLocation(location.getText().toString());
            goods.setManufacturer(manufacturer.getText().toString());
            String sql;
            if (isAdd) {
                sql = "insert into goodsinfo(name, location, manufacturer) values(\""
                        + goods.getName() + "\", \"" + goods.getLocation() + "\", \""
                        + goods.getManufacturer() + "\");";
            } else {
                sql = "update goodsinfo set name = " + "\"" + goods.getName() + "\", "
                        + "location = " + "\"" + goods.getLocation() + "\", "
                        + "manufacturer = \"" + goods.getManufacturer()
                        + "\" where goodsinfo.index = " + goods.getIndex();
            }
            ThreadPool.EXECUTOR.execute(() -> {
                if (isAdd) {
                    ArrayList<Goods> result = DatabaseUtil.executeSqlWithResult("select * from goodsinfo where name = \"" + goods.getName() + "\"", Goods.class);
                    if (result == null) {
                        ToastUtil.showOnUiThread("网络错误", requireActivity());
                    } else if (!result.isEmpty()) {
                        ToastUtil.showOnUiThread("商品名称重复，添加失败", requireActivity());
                    } else {
                        String message = DatabaseUtil.executeSqlWithoutResult(sql) ? "保存成功" : "保存失败，请重试";
                        ToastUtil.showOnUiThread(message, requireActivity());
                    }
                } else {
                    String message = DatabaseUtil.executeSqlWithoutResult(sql) ? "保存成功" : "保存失败，请重试";
                    ToastUtil.showOnUiThread(message, requireActivity());
                }
            });
        });
        deleteButton.setOnClickListener(v -> {
            String sql = "delete from goodsinfo where goodsinfo.index = " + goods.getIndex();
            ThreadPool.EXECUTOR.execute(() -> {
                String message = DatabaseUtil.executeSqlWithoutResult(sql) ? "删除成功" : "删除失败，有关于此商品的订单";
                requireActivity().runOnUiThread(() -> {
                    ToastUtil.show(message);
                    Navigation.findNavController(requireView()).popBackStack(R.id.fragment_goods, false);
                });
            });
        });
        shortageButton.setOnClickListener(v -> makeOrder("shortage"));
    }

    private void setTextViewClickListener() {
        id.setOnClickListener(v -> ToastUtil.show("商品id不可修改"));
        inventory.setOnClickListener(v -> ToastUtil.show("商品库存不可修改"));
        sold.setOnClickListener(v -> ToastUtil.show("商品售出量不可修改"));
        name.setOnClickListener(v -> getDialogText(name));
        manufacturer.setOnClickListener(v -> getDialogText(manufacturer));
        location.setOnClickListener(v -> getDialogText(location));
        if (authority != UserAuthority.SHIPMENT) {
            shortageButton.setVisibility(View.GONE);
        }
    }


    private void initData() {
        id.setText(String.valueOf(goods.getIndex()));
        name.setText(goods.getName());
        inventory.setText(String.valueOf(goods.getInventory()));
        sold.setText(String.valueOf(goods.getSold()));
        location.setText(goods.getLocation());
        manufacturer.setText(goods.getManufacturer());
    }

    private void getDialogText(TextView textView) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input, null, false);
        EditText editText = dialogView.findViewById(R.id.dialog_input_et);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("请输入")
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .setPositiveButton("确认", (dialog, which) -> {
                    if (TextUtils.isEmpty(editText.getText())) {
                        Toast.makeText(requireContext(), "输入不合法", Toast.LENGTH_SHORT).show();
                    } else {
                        textView.setText(editText.getText().toString().trim());
                    }
                })
                .create();
        alertDialog.show();
    }

    private void makeOrder(String tableName) {
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
                        String sql = "insert into " + tableName + "(goods_id, number, date) values(" + goods.getIndex() + ", " + Long.parseLong(text.toString()) + ", CURDATE())";
                        ThreadPool.EXECUTOR.execute(() -> ToastUtil.showOnUiThread(DatabaseUtil.executeSqlWithoutResult(sql) ? "订单建立成功" : "订单建立失败请重试", requireActivity()));
                    }
                })
                .create();
        alertDialog.show();
    }


    private void initView() {
        id = requireView().findViewById(R.id.details_tv_id);
        name = requireView().findViewById(R.id.details_tv_name);
        inventory = requireView().findViewById(R.id.details_tv_inventory);
        sold = requireView().findViewById(R.id.details_tv_sold);
        manufacturer = requireView().findViewById(R.id.details_tv_manufacturer);
        location = requireView().findViewById(R.id.details_tv_location);
        purchaseButton = requireView().findViewById(R.id.details_btn_purchase);
        shipmentButton = requireView().findViewById(R.id.details_btn_shipment);
        saveButton = requireView().findViewById(R.id.details_btn_save);
        deleteButton = requireView().findViewById(R.id.details_btn_delete);
        shortageButton = requireView().findViewById(R.id.details_btn_shortage);
        if (isAdd) {
            id.setVisibility(View.GONE);
            inventory.setVisibility(View.GONE);
            sold.setVisibility(View.GONE);
        }
    }
}