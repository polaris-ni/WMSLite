package com.topolaris.wmslite.ui.order.single;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.goods.Goods;
import com.topolaris.wmslite.model.order.Order;
import com.topolaris.wmslite.repository.local.Cache;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author toPolaris
 * description 订单界面RecyclerView的适配器
 * @date 2021/6/2 17:12
 */
public class OrderPageAdapter extends RecyclerView.Adapter<OrderPageAdapter.OrderPageAdapterViewHolder> {
    private static final String TAG = "OrderPageAdapter";
    private final Fragment fragment;
    /**
     * type表示当前显示内容类型：0是出货单，1是进货单
     */
    private final boolean type;
    private ArrayList<Order> orders;

    public OrderPageAdapter(Fragment fragment, ArrayList<Order> orders, boolean type) {
        this.fragment = fragment;
        this.orders = orders;
        this.type = type;
    }

    @NonNull
    @NotNull
    @Override
    public OrderPageAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new OrderPageAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderPageAdapterViewHolder holder, int position) {
        Order order = orders.get(position);
        Log.e(TAG, "onBindViewHolder: " + order.toString());
        Goods goods = Cache.searchGoodsById(order.getGoodsId());
        if (goods == null) {
            return;
        }
        holder.name.setText(goods.getName());
        holder.inventory.setText(String.valueOf(goods.getInventory()));
        holder.number.setText(String.valueOf(order.getNumber()));
        holder.goodsId.setText(String.valueOf(order.getGoodsId()));
        if (order.isExecuted()) {
            // 订单已经被审核员处理
            if (order.isRevoked()) {
                // 订单被审核员撤销——撤销状态
                holder.goodsStatusImage.setImageDrawable(ContextCompat.getDrawable(fragment.requireContext(),R.drawable.ic_order_status_canceled));
            } else {
                // 订单被审核员确认——完成状态
                holder.goodsStatusImage.setImageDrawable(ContextCompat.getDrawable(fragment.requireContext(),R.drawable.ic_order_status_ok));
            }
        } else {
            // 订单未被审核员处理
            if (!type) {
                // 出货单
                if (order.getNumber() > goods.getInventory()) {
                    // 出货数量大于库存——缺货状态
                    holder.goodsStatusImage.setImageDrawable(ContextCompat.getDrawable(fragment.requireContext(),R.drawable.ic_order_status_error));
                } else {
                    // 出货数量不大于库存——等待状态
                    holder.goodsStatusImage.setImageDrawable(ContextCompat.getDrawable(fragment.requireContext(),R.drawable.ic_order_status_waiting));
                }
            } else {
                // 出货单——等待状态
                holder.goodsStatusImage.setImageDrawable(ContextCompat.getDrawable(fragment.requireContext(),R.drawable.ic_order_status_waiting));
            }
        }
        holder.materialCardView.setOnClickListener(v -> {
            // TODO: 2021/6/2 跳转到订单处理界面
        });
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    static class OrderPageAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView name, goodsId, number, inventory;
        private ImageView goodsStatusImage;
        private MaterialCardView materialCardView;

        public OrderPageAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_order_name);
            goodsId = itemView.findViewById(R.id.item_order_id);
            number = itemView.findViewById(R.id.item_order_number);
            inventory = itemView.findViewById(R.id.item_order_inventory);
            goodsStatusImage = itemView.findViewById(R.id.item_order_status);
            materialCardView = itemView.findViewById(R.id.item_rv_order_mcv);
        }
    }
}
