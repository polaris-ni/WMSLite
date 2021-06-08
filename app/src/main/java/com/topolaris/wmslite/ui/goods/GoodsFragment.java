package com.topolaris.wmslite.ui.goods;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.topolaris.wmslite.MainActivity;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.user.UserAuthority;
import com.topolaris.wmslite.repository.local.Cache;
import com.topolaris.wmslite.ui.login.LoginActivity;
import com.topolaris.wmslite.utils.WmsLiteApplication;

/**
 * @author Liangyong Ni
 * description 商品显示界面
 * @date 2021/5/19 15:32
 */
public class GoodsFragment extends Fragment {
//    private static final String TAG = "GoodsFragment";

    RecyclerView allRecyclerView, popularRecyclerView;
    private GoodsViewModel mViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private FloatingActionButton menu;
    private MaterialCardView logout, accountManager;
    // TODO: 2021/6/2 搜索逻辑

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

        // 退出登录
        logout.setOnClickListener(v -> {
            WmsLiteApplication.setAccount(null);
            Cache.clearCache();
            ((MainActivity) requireActivity()).startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        allRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        AllGoodsAdapter allGoodsAdapter = new AllGoodsAdapter(requireView());
        allGoodsAdapter.setGoods(Cache.getGoodsCache());
        allRecyclerView.setAdapter(allGoodsAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        popularRecyclerView.setLayoutManager(linearLayoutManager);
        // TODO: 2021/6/2 注意数据刷新逻辑
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

        menu.setOnClickListener(v -> {
            Cache.updateCacheByAuthority();
            Bundle bundle = new Bundle();
            switch (WmsLiteApplication.getAccount().getAuthority()) {
                case UserAuthority
                        .ADMINISTRATOR:
                case UserAuthority
                        .CHECKER:
                    Navigation.findNavController(requireView()).navigate(R.id.action_nav_goods_to_nav_all_orders);
                    break;
                case UserAuthority
                        .PURCHASER:
                    bundle.putBoolean("TYPE", true);
                    Navigation.findNavController(requireView()).navigate(R.id.action_nav_goods_to_nav_order_single, bundle);
                    break;
                case UserAuthority
                        .SHIPMENT:
                    bundle.putBoolean("TYPE", false);
                    Navigation.findNavController(requireView()).navigate(R.id.action_nav_goods_to_nav_order_single, bundle);
                    break;
                default:
                    Toast.makeText(WmsLiteApplication.context, "账号权限异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        accountManager.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_nav_goods_to_nav_user_management));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initView() {
        allRecyclerView = requireView().findViewById(R.id.goods_rv_all);
        popularRecyclerView = requireView().findViewById(R.id.goods_rv_popular);
        swipeRefreshLayout = requireView().findViewById(R.id.goods_swipe_refresh);
        searchView = requireView().findViewById(R.id.goods_search);
        menu = requireView().findViewById(R.id.fab_menu);
        logout = requireView().findViewById(R.id.goods_mcv_logout);
        accountManager = requireView().findViewById(R.id.goods_mcv_account);
        if (WmsLiteApplication.getAccount().getAuthority() != UserAuthority.ADMINISTRATOR) {
            accountManager.setVisibility(View.GONE);
        }
    }
}