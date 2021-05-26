package com.topolaris.wmslite.repository.local;

import com.topolaris.wmslite.model.user.User;
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
