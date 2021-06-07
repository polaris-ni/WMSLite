package com.topolaris.wmslite.ui.order.single;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.topolaris.wmslite.model.order.Order;
import com.topolaris.wmslite.repository.local.Cache;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;

import java.util.ArrayList;

/**
 * @author Liangyong Ni
 * description OrderPage的ViewModel
 * @date 2021/6/2 18:47
 */
public class OrderPageViewModel extends ViewModel {
//    private static final String TAG = "OrderPageViewModel";

    private final MutableLiveData<ArrayList<Order>> orders;
    private boolean type;

    public OrderPageViewModel() {
        super();
        orders = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Order>> getOrders() {
        orders.setValue(Cache.getOrdersCache());
        return orders;
    }

    public void refresh() {
        String sql = type ? "select * from purchase;" : "select * from shipment;";
        ThreadPool.EXECUTOR.execute(() -> orders.postValue(DatabaseUtil.executeSqlWithResult(sql, Order.class)));
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
