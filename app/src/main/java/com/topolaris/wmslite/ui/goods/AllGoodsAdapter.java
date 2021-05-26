package com.topolaris.wmslite.ui.goods;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.goods.Goods;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author toPolaris
 * description TODO
 * @date 2021/5/22 17:43
 */
public class AllGoodsAdapter extends RecyclerView.Adapter<AllGoodsAdapter.MyViewHolder> {
    private static final String TAG = "GoodsAdapter";
    //    private final GoodsFragment fragment;
    private ArrayList<Goods> goods;

    public AllGoodsAdapter(ArrayList<Goods> goods, GoodsFragment fragment) {
        this.goods = goods;
//        this.fragment = fragment;
//        fragment.setHeadInfo(goods.get(0));
    }

    public void setGoods(ArrayList<Goods> goods) {
        this.goods = goods;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_goods_all, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.index.setText(String.valueOf(goods.get(position).getIndex()));
        holder.name.setText(goods.get(position).getName());
        holder.inventory.setText(String.valueOf(goods.get(position).getInventory()));
        holder.manufacturer.setText(goods.get(position).getManufacturer());
        holder.materialCardView.setOnClickListener(v -> {
            // TODO: 2021/5/25 跳转到详情界面
//            fragment.setHeadInfo(goods.get(position));
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("GOODS", goods.get(position));
//                Log.e(TAG, "onClick: here!");
//                EventBus.getDefault().postSticky(new MessageEvent.Builder(MessageType.GOODS_ITEM_CLICK).setBundle(bundle));
        });
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView materialCardView;
        private final TextView index, name, inventory, manufacturer;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.item_mcv);
            index = itemView.findViewById(R.id.item_all_index);
            name = itemView.findViewById(R.id.item_all_name);
            inventory = itemView.findViewById(R.id.item_all_inventory);
            manufacturer = itemView.findViewById(R.id.item_all_manufacturer);
        }
    }
}