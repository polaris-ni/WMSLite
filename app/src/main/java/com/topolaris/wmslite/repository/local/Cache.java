package com.topolaris.wmslite.repository.local;

import com.topolaris.wmslite.model.goods.Goods;
import com.topolaris.wmslite.model.order.Order;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;

import java.util.ArrayList;

/**
 * @author toPolaris
 * description 数据缓存类
 * @date 2021/5/19 14:43
 */
public class Cache {
    private static final String TAG = "Cache";
    private static ArrayList<Goods> goodsCache = new ArrayList<>();
    private static ArrayList<Order> ordersCache = new ArrayList<>();
    private static ArrayList<Order> shipmentsCache = new ArrayList<>();

    public static Goods searchGoodsById(long id) {
//        Log.e(TAG, "searchGoodsById: " + id);
        for (Goods goods : goodsCache) {
//            Log.e(TAG, "searchGoodsById: " + goods);
            if (goods.getIndex() == id) {
                return goods;
            }
        }
        // TODO: 2021/6/2 刷新缓存重试
        return null;
    }

    public static ArrayList<Order> getSelectedOrdersCache(boolean type) {
        return type ? getOrdersCache() : getShipmentsCache();
    }


    public static ArrayList<Order> getOrdersCache() {
        return ordersCache;
    }

    public static ArrayList<Goods> getGoodsCache() {
        return goodsCache;
    }

    public static ArrayList<Order> getShipmentsCache() {
        return shipmentsCache;
    }

    public static void updateAllCache() {
        updateGoodsCache();
        updateOrdersCache();
        updateShipmentsCache();
    }

    public static void updateGoodsCache() {
        ThreadPool.executor.execute(() -> {
            String sql = "select * from goodsinfo;";
            goodsCache = DatabaseUtil.executeSqlWithResult(sql, Goods.class);
        });
    }

    public static void updateOrdersCache() {
        ThreadPool.executor.execute(() -> {
            String sql = "select * from purchase;";
            ordersCache = DatabaseUtil.executeSqlWithResult(sql, Order.class);
        });
    }

    public static void updateShipmentsCache() {
        ThreadPool.executor.execute(() -> {
            String sql = "select * from shipment;";
            shipmentsCache = DatabaseUtil.executeSqlWithResult(sql, Order.class);
        });
    }
    //    public static ArrayList<User> usersCache;
//    public static ArrayList<String> usernameCache = new ArrayList<>();

//    public static void updateCache() {
//        ThreadPool.executor.execute(() -> {
//            String sql = "select * from wmsusers;";
//            usersCache = DatabaseUtil.executeSqlWithResult(sql, User.class);
//            for (User user : usersCache) {
//                usernameCache.add(user.getName());
//            }
//        });
//    }
}
