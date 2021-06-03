package com.topolaris.wmslite.ui.goods;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.topolaris.wmslite.R;
import com.topolaris.wmslite.repository.local.Cache;
import com.topolaris.wmslite.utils.Test;
import com.topolaris.wmslite.utils.WMSLiteApplication;

/**
 * @author toPolaris
 */
public class GoodsFragment extends Fragment {
    private static final String TAG = "GoodsFragment";
    RecyclerView allRecyclerView, popularRecyclerView;
    private GoodsViewModel mViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    // TODO: 2021/6/2 搜索逻辑
    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_goods, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GoodsViewModel.class);

        initView();

        if (WMSLiteApplication.getAccount() == null) {
            Navigation.findNavController(requireView()).navigate(R.id.nav_login);
        }

        allRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        // TODO: 2021/6/2 注意数据刷新逻辑
        AllGoodsAdapter allGoodsAdapter = new AllGoodsAdapter(Cache.getGoodsCache(), this);
        allRecyclerView.setAdapter(allGoodsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        popularRecyclerView.setLayoutManager(linearLayoutManager);
        PopularGoodsAdapter popularGoodsAdapter = new PopularGoodsAdapter(Test.goods, this);
        popularRecyclerView.setAdapter(popularGoodsAdapter);

        mViewModel.getGoods().observe(getViewLifecycleOwner(), goods -> {
            allGoodsAdapter.setGoods(goods);
            allGoodsAdapter.notifyDataSetChanged();
        });
        mViewModel.getPopular().observe(getViewLifecycleOwner(), goods -> {
            popularGoodsAdapter.setGoods(goods);
            popularGoodsAdapter.notifyDataSetChanged();
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.update();
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    private void initView() {
        allRecyclerView = requireView().findViewById(R.id.goods_rv_all);
        popularRecyclerView = requireView().findViewById(R.id.goods_rv_popular);
        swipeRefreshLayout = requireView().findViewById(R.id.goods_swipe_refresh);
        searchView = requireView().findViewById(R.id.goods_search);
    }
}