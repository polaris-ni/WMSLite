package com.topolaris.wmslite.ui.order.single;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.topolaris.wmslite.R;
import com.topolaris.wmslite.repository.local.Cache;

/**
 * @author Protein
 */
public class OrderPageFragment extends Fragment {
    private static final String TAG = "OrderPageFragment";

    private OrderPageViewModel mViewModel;
    private boolean type;
    private SearchView search;
    private RecyclerView orderRecyclerView;
    private SwipeRefreshLayout refresh;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OrderPageViewModel.class);

        if (getArguments() != null) {
            type = getArguments().getBoolean("TYPE");
        }
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
        Log.e(TAG, "onActivityCreated: " + Cache.getOrdersCache());
        OrderPageAdapter orderPageAdapter = new OrderPageAdapter(this, Cache.getSelectedOrdersCache(type), type);
        orderRecyclerView.setAdapter(orderPageAdapter);

        mViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            orderPageAdapter.setOrders(orders);
            orderPageAdapter.notifyDataSetChanged();
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO: 2021/6/2 搜索逻辑
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        refresh.setColorSchemeResources(R.color.design_default_color_primary);
        refresh.setOnRefreshListener(() -> {
            mViewModel.refresh();
            refresh.setRefreshing(false);
        });
    }

    private void initView() {
        TextView title = requireView().findViewById(R.id.order_title);
        if (type) {
            title.setText(getString(R.string.order_title_purchase));
        } else {
            title.setText(getString(R.string.order_title_shipment));
        }
        search = requireView().findViewById(R.id.order_search);
        orderRecyclerView = requireView().findViewById(R.id.order_rv);
        refresh = requireView().findViewById(R.id.order_swipe_refresh);
    }
}