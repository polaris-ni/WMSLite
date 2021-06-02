package com.topolaris.wmslite.ui.order.single;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.topolaris.wmslite.model.order.Order;
import com.topolaris.wmslite.repository.local.Cache;

import java.util.ArrayList;

/**
 * @author toPolaris
 * description OrderPage的ViewModel
 * @date 2021/6/2 18:47
 */
public class OrderPageViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Order>> orders;

    public OrderPageViewModel() {
        super();
        orders = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Order>> getOrders() {
        orders.setValue(Cache.getOrdersCache());
        return orders;
    }
}
