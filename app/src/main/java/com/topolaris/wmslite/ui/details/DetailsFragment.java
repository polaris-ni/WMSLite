package com.topolaris.wmslite.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.event.MessageEvent;
import com.topolaris.wmslite.model.event.MessageType;
import com.topolaris.wmslite.model.goods.Goods;

import org.greenrobot.eventbus.EventBus;

public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";
    private Goods goods;
    private TextView id, name, inventory, sold, manufacturer, location;
    private Button purchaseButton, shipmentButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            goods = getArguments().getParcelable("GOODS");
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
        initView();
        if (goods != null) {
            id.setText(String.valueOf(goods.getIndex()));
            name.setText(goods.getName());
            inventory.setText(String.valueOf(goods.getInventory()));
            sold.setText(String.valueOf(goods.getSold()));
            location.setText(goods.getLocation());
            manufacturer.setText(goods.getManufacturer());
            purchaseButton.setOnClickListener(v -> {
                // TODO: 2021/6/1
            });

            shipmentButton.setOnClickListener(v -> {
                // TODO: 2021/6/1
            });
        } else {
            EventBus.getDefault().post(new MessageEvent.Builder(MessageType.MAIN_TOAST_MAKER).setMessage("Something Wrong!"));
            Navigation.findNavController(requireView()).popBackStack();
        }
    }

    private void initView() {
        id = requireView().findViewById(R.id.details_goods_id);
        name = requireView().findViewById(R.id.details_goods_name);
        inventory = requireView().findViewById(R.id.details_goods_inventory);
        sold = requireView().findViewById(R.id.details_goods_sold);
        manufacturer = requireView().findViewById(R.id.details_goods_manufacturer);
        location = requireView().findViewById(R.id.details_goods_location);
        purchaseButton = requireView().findViewById(R.id.details_purchase);
        shipmentButton = requireView().findViewById(R.id.details_shipment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}