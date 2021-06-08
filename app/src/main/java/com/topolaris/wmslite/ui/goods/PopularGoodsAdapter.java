package com.topolaris.wmslite.ui.goods;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.goods.Goods;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Liangyong Ni
 * description 受欢迎商品RecyclerView适配器
 * @date 2021/5/25 20:00
 */
public class PopularGoodsAdapter extends RecyclerView.Adapter<PopularGoodsAdapter.PopularAdapterViewHolder> {
    private final View fromView;
    private ArrayList<Goods> goods;

    public PopularGoodsAdapter(View fromView) {
        this.fromView = fromView;
    }

    public void setGoods(ArrayList<Goods> goods) {
        this.goods = goods;
    }

    @NonNull
    @NotNull
    @Override
    public PopularAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new PopularAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_goods_popular, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PopularAdapterViewHolder holder, int position) {
        Goods g = this.goods.get(position);
        // TODO: 2021/6/1
        g.setSold(0);
        holder.name.setText(g.getName());
        holder.inventory.setText(String.valueOf(g.getInventory()));
        holder.sold.setText(String.valueOf(g.getSold()));
        holder.location.setText(g.getLocation());
        holder.materialCardView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("GOODS", g);
            Navigation.findNavController(fromView).navigate(R.id.action_nav_goods_to_nav_details, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    static final class PopularAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, inventory, sold, location;
        private final MaterialCardView materialCardView;

        public PopularAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_popular_name);
            inventory = itemView.findViewById(R.id.item_popular_inventory);
            sold = itemView.findViewById(R.id.item_popular_sold);
            location = itemView.findViewById(R.id.item_popular_location);
            materialCardView = itemView.findViewById(R.id.item_rv_popular_mcv);
        }
    }
}
