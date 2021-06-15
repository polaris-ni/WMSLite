package com.topolaris.wmslite.ui.main.goods.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.topolaris.wmslite.model.order.Order;
import com.topolaris.wmslite.model.order.OrderType;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;

import java.util.ArrayList;

/**
 * @author Liangyong Ni
 * description 搜索界面ViewModel
 * @date 2021/6/10 14:13:39
 */
public class SearchViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Order>> orders;
    private OrderType type;

    public SearchViewModel() {
        super();
        orders = new MutableLiveData<>();
        orders.setValue(new ArrayList<>());
    }

    public LiveData<ArrayList<Order>> getOrders() {
        return orders;
    }

    public void refresh(String name) {
        switch (type) {
            case PURCHASE:
                ThreadPool.EXECUTOR.execute(() -> orders.postValue(DatabaseUtil.executeSqlWithResult("select * from purchase where goods_id = (select goodsinfo.index from goodsinfo where name = \"" + name + "\") order by id", Order.class)));
                break;
            case SHIPMENT:
                ThreadPool.EXECUTOR.execute(() -> orders.postValue(DatabaseUtil.executeSqlWithResult("select * from shipment where goods_id = (select goodsinfo.index from goodsinfo where name = \"" + name + "\") order by id", Order.class)));
                break;
            case SHORTAGE:
                ThreadPool.EXECUTOR.execute(() -> orders.postValue(DatabaseUtil.executeSqlWithResult("select * from shortage where goods_id = (select goodsinfo.index from goodsinfo where name = \"" + name + "\") order by id", Order.class)));
                break;
            default:
                break;
        }
    }

    public void setType(OrderType type) {
        this.type = type;
    }
}