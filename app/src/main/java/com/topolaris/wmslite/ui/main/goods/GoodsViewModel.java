package com.topolaris.wmslite.ui.main.goods;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.topolaris.wmslite.model.goods.Goods;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;

import java.util.ArrayList;


/**
 * @author Liangyong Ni
 * description 商品显示配套ViewModel
 * @date 2021/5/19 15:32
 */
public class GoodsViewModel extends ViewModel {
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
        ThreadPool.EXECUTOR.execute(() -> goods.postValue(DatabaseUtil.executeSqlWithResult("select * from goodsinfo", Goods.class)));
        ThreadPool.EXECUTOR.execute(() -> popular.postValue(DatabaseUtil.executeSqlWithResult("select * from popular_goods", Goods.class)));
    }
}
