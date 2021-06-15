package com.topolaris.wmslite.ui.main.goods.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.order.OrderType;
import com.topolaris.wmslite.ui.main.order.page.OrderAdapter;

/**
 * @author Liangyong Ni
 * description 搜索界面
 * @date 2021/6/10 14:13:39
 */
public class SearchFragment extends Fragment {

    private SearchViewModel mViewModel;
    private SearchView searchView;
    private RadioGroup radioGroup;
    private RadioButton purchaseRadioButton, shipmentRadioButton, shortageRadioButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private OrderType type = OrderType.SHORTAGE;
    private String goodsName = "";

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        mViewModel.setType(type);
        initView();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        OrderAdapter orderAdapter = new OrderAdapter(this, mViewModel.getOrders().getValue(), type);
        recyclerView.setAdapter(orderAdapter);

        mViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            orderAdapter.setOrders(orders);
            orderAdapter.notifyDataSetChanged();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.refresh(goodsName);
            swipeRefreshLayout.setRefreshing(false);
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.search_rb_purchase:
                    type = OrderType.PURCHASE;
                    orderAdapter.setType(type);
                    mViewModel.setType(type);
                    mViewModel.refresh(goodsName);
                    break;
                case R.id.search_rb_shipment:
                    type = OrderType.SHIPMENT;
                    orderAdapter.setType(type);
                    mViewModel.setType(type);
                    mViewModel.refresh(goodsName);
                    break;
                case R.id.search_rb_shortage:
                    type = OrderType.SHORTAGE;
                    orderAdapter.setType(type);
                    mViewModel.setType(type);
                    mViewModel.refresh(goodsName);
                    break;
                default:
                    break;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                goodsName = query.trim();
                mViewModel.refresh(goodsName);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initView() {
        searchView = requireView().findViewById(R.id.search_search);
        radioGroup = requireView().findViewById(R.id.search_rg);
        purchaseRadioButton = requireView().findViewById(R.id.search_rb_purchase);
        shipmentRadioButton = requireView().findViewById(R.id.search_rb_shipment);
        shortageRadioButton = requireView().findViewById(R.id.search_rb_shortage);
        swipeRefreshLayout = requireView().findViewById(R.id.search_swipe_refresh);
        recyclerView = requireView().findViewById(R.id.search_rv);
    }
}