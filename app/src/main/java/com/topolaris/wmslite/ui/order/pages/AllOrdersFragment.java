package com.topolaris.wmslite.ui.order.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.model.order.OrderType;
import com.topolaris.wmslite.model.user.UserAuthority;
import com.topolaris.wmslite.ui.order.page.OrderPageFragment;
import com.topolaris.wmslite.utils.WmsLiteApplication;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Liangyong Ni
 * description 全订单显示界面
 * @date 2021/6/7 14:32
 */
public class AllOrdersFragment extends Fragment {

    List<String> titles;
    List<Fragment> fragments;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        mViewPager.setAdapter(new PageAdapter(getChildFragmentManager(), titles, fragments));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initView() {
        mTabLayout = requireView().findViewById(R.id.orders_tab_layout);
        mViewPager = requireView().findViewById(R.id.orders_viewpager);
    }

    private void initData() {
        int authority = WmsLiteApplication.getAccount().getAuthority();
        titles = new ArrayList<>();
        fragments = new ArrayList<>();
        if (authority == UserAuthority.SHIPMENT) {
            titles.add("销售");
            fragments.add(new OrderPageFragment(OrderType.SHIPMENT));
        } else if (authority == UserAuthority.PURCHASER) {
            titles.add("采购");
            fragments.add(new OrderPageFragment(OrderType.PURCHASE));
        } else {
            titles.add("销售");
            titles.add("采购");
            fragments.add(new OrderPageFragment(OrderType.SHIPMENT));
            fragments.add(new OrderPageFragment(OrderType.PURCHASE));
        }
        titles.add("缺货");
        fragments.add(new OrderPageFragment(OrderType.SHORTAGE));
    }
}