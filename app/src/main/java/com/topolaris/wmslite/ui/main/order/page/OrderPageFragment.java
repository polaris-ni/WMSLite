package com.topolaris.wmslite.ui.main.order.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.order.OrderType;
import com.topolaris.wmslite.repository.local.Cache;

/**
 * @author Liangyong Ni
 * description 订单界面
 * @date 2021/6/2 17:12
 */
public class OrderPageFragment extends Fragment {
    private final OrderType type;
    private OrderPageViewModel mViewModel;
    private RecyclerView orderRecyclerView;
    private SwipeRefreshLayout refresh;

    public OrderPageFragment(OrderType type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OrderPageViewModel.class);
        mViewModel.setType(type);
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

        orderRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        OrderAdapter orderAdapter = new OrderAdapter(this, Cache.getSelectedOrdersCache(type), type);
        orderRecyclerView.setAdapter(orderAdapter);

        mViewModel.refresh();

        mViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            orderAdapter.setOrders(orders);
            orderAdapter.notifyDataSetChanged();
        });

        refresh.setColorSchemeResources(R.color.design_default_color_primary);
        refresh.setOnRefreshListener(() -> {
            mViewModel.refresh();
            refresh.setRefreshing(false);
        });
    }

    private void initView() {
        orderRecyclerView = requireView().findViewById(R.id.order_rv);
        refresh = requireView().findViewById(R.id.order_swipe_refresh);
    }
}