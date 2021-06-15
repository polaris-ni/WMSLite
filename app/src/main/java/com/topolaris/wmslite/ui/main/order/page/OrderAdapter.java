package com.topolaris.wmslite.ui.main.order.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.goods.Goods;
import com.topolaris.wmslite.model.order.Order;
import com.topolaris.wmslite.model.order.OrderType;
import com.topolaris.wmslite.repository.local.Cache;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Liangyong Ni
 * description 订单界面RecyclerView的适配器
 * @date 2021/6/2 17:12
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderPageAdapterViewHolder> {
    private final Fragment fragment;
    private OrderType type;
    private ArrayList<Order> orders;

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderAdapter(Fragment fragment, ArrayList<Order> orders, OrderType type) {
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

        Goods goods = Cache.searchGoodsById(order.getGoodsId());
        if (goods == null) {
            return;
        }
        order.setState(type, goods);
        holder.name.setText(goods.getName());
        holder.inventory.setText(String.valueOf(goods.getInventory()));
        holder.number.setText(String.valueOf(order.getNumber()));
        holder.goodsId.setText(String.valueOf(order.getGoodsId()));
        if (type == OrderType.SHORTAGE) {
            holder.goodsStatusImage.setVisibility(View.GONE);
        } else {
            switch (order.getState()) {
                case 0:
                    holder.goodsStatusImage.setImageDrawable(ContextCompat.getDrawable(fragment.requireContext(), R.drawable.ic_order_status_excuted_28));
                    break;
                case 1:
                    holder.goodsStatusImage.setImageDrawable(ContextCompat.getDrawable(fragment.requireContext(), R.drawable.ic_order_status_waiting_28));
                    break;
                case 2:
                    holder.goodsStatusImage.setImageDrawable(ContextCompat.getDrawable(fragment.requireContext(), R.drawable.ic_order_status_revoked_28));
                    break;
                case 3:
                    holder.goodsStatusImage.setImageDrawable(ContextCompat.getDrawable(fragment.requireContext(), R.drawable.ic_order_status_error_28));
                    break;
                default:
                    break;
            }
        }
        holder.materialCardView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("goods", goods);
            bundle.putParcelable("order", order);
            // TODO: 2021/6/9  
            if (type == OrderType.PURCHASE) {
                bundle.putString("table", "purchase");
            } else if (type == OrderType.SHIPMENT) {
                bundle.putString("table", "shipment");
            } else {
                bundle.putString("table", "shortage");
            }
            Navigation.findNavController(fragment.requireView()).navigate(R.id.fragment_order_detail, bundle);
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
        private final TextView name, goodsId, number, inventory;
        private final ImageView goodsStatusImage;
        private final MaterialCardView materialCardView;

        public OrderPageAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_order_tv_name);
            goodsId = itemView.findViewById(R.id.item_order_tv_id);
            number = itemView.findViewById(R.id.item_order_tv_number);
            inventory = itemView.findViewById(R.id.item_order_tv_inventory);
            goodsStatusImage = itemView.findViewById(R.id.item_order_iv_status);
            materialCardView = itemView.findViewById(R.id.item_rv_order_mcv);
        }
    }



}
