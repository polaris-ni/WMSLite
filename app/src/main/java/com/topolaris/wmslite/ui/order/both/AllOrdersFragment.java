package com.topolaris.wmslite.ui.order.both;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.topolaris.wmslite.R;
import com.topolaris.wmslite.ui.order.single.OrderPageFragment;

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
        return inflater.inflate(R.layout.fragment_all_orders, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mViewPager.setAdapter(new PageAdapter(requireActivity().getSupportFragmentManager(), titles, fragments));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initView() {
        mTabLayout = requireView().findViewById(R.id.both_tab_layout);
        mViewPager = requireView().findViewById(R.id.both_viewpager);
    }

    private void initData() {
        titles = new ArrayList<>();
        titles.add("Purchase");
        titles.add("Shipment");
        fragments = new ArrayList<>();
        fragments.add(new OrderPageFragment(true, true));
        fragments.add(new OrderPageFragment(false, true));
    }
}