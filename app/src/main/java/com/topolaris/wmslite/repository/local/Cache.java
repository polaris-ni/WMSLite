package com.topolaris.wmslite.repository.local;

import com.topolaris.wmslite.model.goods.Goods;
import com.topolaris.wmslite.model.order.Order;

import java.util.ArrayList;

/**
 * @author toPolaris
 * description 数据缓存类
 * @date 2021/5/19 14:43
 */
public class Cache {
    private static final String TAG = "Cache";
    private static final ArrayList<Goods> goodsCache = new ArrayList<>();
    private static final ArrayList<Order> ordersCache = new ArrayList<>();

    public static Goods searchGoodsById(long id) {
        for (Goods goods : goodsCache) {
            if (goods.getIndex() == id) {
                return goods;
            }
        }
        // TODO: 2021/6/2 刷新缓存重试
        return null;
    }

    public static ArrayList<Order> getOrdersCache() {
        return ordersCache;
    }

    public static ArrayList<Goods> getGoodsCache() {
        return goodsCache;
    }

    public static void update() {
        // TODO: 2021/6/2 数据刷新策略
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
