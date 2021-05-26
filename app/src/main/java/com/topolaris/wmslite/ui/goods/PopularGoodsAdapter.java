package com.topolaris.wmslite.ui.goods;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.goods.Goods;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author toPolaris
 * description 适配器
 * @date 2021/5/25 20:00
 */
public class PopularGoodsAdapter extends RecyclerView.Adapter<PopularGoodsAdapter.PopularAdapterViewHolder> {
    private ArrayList<Goods> goods;

    public void setGoods(ArrayList<Goods> goods) {
        this.goods = goods;
    }

    public PopularGoodsAdapter(ArrayList<Goods> goods) {
        this.goods = goods;
    }

    @NonNull
    @NotNull
    @Override
    public PopularAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new PopularAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_goods_popular, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PopularAdapterViewHolder holder, int position) {
        holder.name.setText(goods.get(position).getName());
        holder.inventory.setText(String.valueOf(goods.get(position).getPrice()));
        // TODO: 2021/5/25 待修改
        holder.sold.setText("1000");
        holder.location.setText("Wuhan, hubei");
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    static final class PopularAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView inventory;
        private final TextView sold;
        private final TextView location;

        public PopularAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_popular_name);
            inventory = itemView.findViewById(R.id.item_popular_inventory);
            sold = itemView.findViewById(R.id.item_popular_sold);
            location = itemView.findViewById(R.id.item_popular_location);
        }
    }
}
