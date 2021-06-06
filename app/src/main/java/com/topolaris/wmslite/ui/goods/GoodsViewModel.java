package com.topolaris.wmslite.ui.goods;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.topolaris.wmslite.model.goods.Goods;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;

import java.util.ArrayList;


/**
 * @author Protein
 */
public class GoodsViewModel extends ViewModel {
    private static final String TAG = "GoodsViewModel";
    private final MutableLiveData<ArrayList<Goods>> goods;
    private final MutableLiveData<ArrayList<Goods>> popular;

    public GoodsViewModel() {
        super();
        goods = new MutableLiveData<>();
        popular = new MutableLiveData<>();
        update();
    }

    public LiveData<ArrayList<Goods>> getGoods() {
        return goods;
    }

    public LiveData<ArrayList<Goods>> getPopular() {
        return popular;
    }

    protected void update() {
        ThreadPool.executor.execute(() -> {
            String goodsQuerySql = "select * from goodsinfo";
            goods.postValue(DatabaseUtil.executeSqlWithResult(goodsQuerySql, Goods.class));
        });
        // TODO: 2021/5/25 重写查询语句
        ThreadPool.executor.execute(() -> {
            String goodsQuerySql = "select * from goodsinfo";
            popular.postValue(DatabaseUtil.executeSqlWithResult(goodsQuerySql, Goods.class));
        });
    }
}
