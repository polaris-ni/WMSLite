package com.topolaris.wmslite.ui.main.goods;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.repository.local.Cache;

/**
 * @author Liangyong Ni
 * description 商品显示界面
 * @date 2021/5/19 15:32
 */
public class GoodsFragment extends Fragment {

    RecyclerView allRecyclerView, popularRecyclerView;
    private GoodsViewModel mViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialCardView addGoods, search;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goods, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GoodsViewModel.class);
        initView();

        allRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        AllGoodsAdapter allGoodsAdapter = new AllGoodsAdapter(requireView());
        allGoodsAdapter.setGoods(Cache.getGoodsCache());
        allRecyclerView.setAdapter(allGoodsAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        popularRecyclerView.setLayoutManager(linearLayoutManager);
        PopularGoodsAdapter popularGoodsAdapter = new PopularGoodsAdapter(requireView());
        popularGoodsAdapter.setGoods(Cache.getPopularGoodsCache());
        popularRecyclerView.setAdapter(popularGoodsAdapter);

        mViewModel.getGoods().observe(getViewLifecycleOwner(), goods -> {
            allGoodsAdapter.setGoods(goods);
            allGoodsAdapter.notifyDataSetChanged();
        });
        mViewModel.getPopular().observe(getViewLifecycleOwner(), goods -> {
            popularGoodsAdapter.setGoods(goods);
            popularGoodsAdapter.notifyDataSetChanged();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.update();
            swipeRefreshLayout.setRefreshing(false);
        });
        setAddGoodsListener(addGoods);
        search.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_fragment_goods_to_fragment_search));

    }

    private void setAddGoodsListener(MaterialCardView addGoods) {
        addGoods.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_nav_goods_to_nav_details));
    }

    private void initView() {
        allRecyclerView = requireView().findViewById(R.id.goods_rv_all);
        popularRecyclerView = requireView().findViewById(R.id.goods_rv_popular);
        swipeRefreshLayout = requireView().findViewById(R.id.goods_swipe_refresh);
        search = requireView().findViewById(R.id.goods_mcv_search);
        addGoods = requireView().findViewById(R.id.goods_mcv_add);
    }
}