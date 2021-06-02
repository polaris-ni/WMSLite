package com.topolaris.wmslite.ui.order.single;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.topolaris.wmslite.R;
import com.topolaris.wmslite.repository.local.Cache;

public class OrderPageFragment extends Fragment {

    private OrderPageViewModel mViewModel;
    private boolean type;
    private TextView title;
    // TODO: 2021/6/2 搜索逻辑
    private EditText search;
    private RecyclerView orderRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OrderPageViewModel.class);
        if (getArguments() != null) {
            type = getArguments().getBoolean("TYPE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

        // TODO: 2021/6/2 数据刷新逻辑
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        OrderPageAdapter orderPageAdapter = new OrderPageAdapter(this, Cache.getOrdersCache(), type);
        orderRecyclerView.setAdapter(orderPageAdapter);

        mViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            orderPageAdapter.setOrders(orders);
            orderPageAdapter.notifyDataSetChanged();
        });

    }

    private void initView() {
        title = requireView().findViewById(R.id.order_title);
        if (type) {
            title.setText(getString(R.string.order_title_purchase));
        } else {
            title.setText(getString(R.string.order_title_shipment));
        }
        search = requireView().findViewById(R.id.order_et_search);
        orderRecyclerView = requireView().findViewById(R.id.order_rv);
    }
}