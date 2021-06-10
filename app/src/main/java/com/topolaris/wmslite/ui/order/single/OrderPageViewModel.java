package com.topolaris.wmslite.ui.order.single;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.topolaris.wmslite.model.order.Order;
import com.topolaris.wmslite.model.order.OrderType;
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
    private OrderType type;

    public OrderPageViewModel() {
        super();
        orders = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Order>> getOrders() {
        orders.setValue(Cache.getOrdersCache());
        return orders;
    }

    public void refresh() {
        switch (type) {
            case PURCHASE:
                ThreadPool.EXECUTOR.execute(() -> orders.postValue(DatabaseUtil.executeSqlWithResult("select * from purchase", Order.class)));
                break;
            case SHIPMENT:
                ThreadPool.EXECUTOR.execute(() -> orders.postValue(DatabaseUtil.executeSqlWithResult("select * from shipment", Order.class)));
                break;
            case SHORTAGE:
                ThreadPool.EXECUTOR.execute(() -> orders.postValue(DatabaseUtil.executeSqlWithResult("select * from shortage", Order.class)));
                break;
            default:
                break;
        }
    }

    public void setType(OrderType type) {
        this.type = type;
    }
}
