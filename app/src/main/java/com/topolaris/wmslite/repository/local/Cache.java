package com.topolaris.wmslite.repository.local;

import com.topolaris.wmslite.model.goods.Goods;
import com.topolaris.wmslite.model.order.Order;
import com.topolaris.wmslite.model.order.OrderType;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.model.user.UserAuthority;
import com.topolaris.wmslite.repository.network.database.DatabaseUtil;
import com.topolaris.wmslite.utils.ThreadPool;

import java.util.ArrayList;

/**
 * @author Liangyong Ni
 * description 数据缓存类
 * @date 2021/5/19 14:43
 */
public class Cache {
    private static ArrayList<Goods> goodsCache = new ArrayList<>();
    private static ArrayList<Goods> popularGoodsCache = new ArrayList<>();
    private static ArrayList<Order> ordersCache = new ArrayList<>();
    private static ArrayList<Order> shortageCache = new ArrayList<>();
    private static ArrayList<Order> shipmentsCache = new ArrayList<>();
    private static ArrayList<User> usersCache = new ArrayList<>();
    private static int authority = UserAuthority.COMMON;

    public static ArrayList<User> getUsersCache() {
        return usersCache;
    }

    public static ArrayList<Goods> getPopularGoodsCache() {
        return popularGoodsCache;
    }

    /**
     * 设置Cache的权限等级
     *
     * @param authority 权限
     */
    public static void setAuthority(int authority) {
        Cache.authority = authority;
    }

    public static Goods searchGoodsById(long id) {
        for (Goods goods : goodsCache) {
            if (goods.getIndex() == id) {
                return goods;
            }
        }
        return null;
    }

    public static ArrayList<Order> getSelectedOrdersCache(OrderType type) {
        switch (type) {
            case SHIPMENT:
                return getShipmentsCache();
            case PURCHASE:
                return getOrdersCache();
            case SHORTAGE:
                return getShortageCache();
            default:
                return new ArrayList<>();
        }
    }

    private static ArrayList<Order> getShortageCache() {
        return shortageCache;
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

    public static void updateCacheByAuthority() {
        updateGoodsCache();
        switch (authority) {
            case UserAuthority.ADMINISTRATOR:
                updateUsersCache();
            case UserAuthority.CHECKER:
                updateOrdersCache();
                updateShipmentsCache();
                break;
            case UserAuthority.PURCHASER:
                updateOrdersCache();
                break;
            case UserAuthority.SHIPMENT:
                updateShipmentsCache();
                break;
            default:
                break;
        }
    }

    private static void updateGoodsCache() {
        ThreadPool.EXECUTOR.execute(() -> {
            goodsCache = DatabaseUtil.executeSqlWithResult("select * from goodsinfo", Goods.class);
            popularGoodsCache = DatabaseUtil.executeSqlWithResult("select * from popular_goods", Goods.class);
        });
    }

    private static void updateOrdersCache() {
        ThreadPool.EXECUTOR.execute(() -> ordersCache = DatabaseUtil.executeSqlWithResult("select * from purchase;", Order.class));
        ThreadPool.EXECUTOR.execute(() -> shortageCache = DatabaseUtil.executeSqlWithResult("select * from shortage;", Order.class));
    }

    private static void updateShipmentsCache() {
        ThreadPool.EXECUTOR.execute(() -> shipmentsCache = DatabaseUtil.executeSqlWithResult("select * from shipment;", Order.class));
        ThreadPool.EXECUTOR.execute(() -> shortageCache = DatabaseUtil.executeSqlWithResult("select * from shortage;", Order.class));
    }

    private static void updateUsersCache() {
        ThreadPool.EXECUTOR.execute(() -> usersCache = DatabaseUtil.executeSqlWithResult("select * from wmsusers;", User.class));
    }
}
