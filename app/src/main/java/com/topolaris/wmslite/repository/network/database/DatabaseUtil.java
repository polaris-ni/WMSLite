package com.topolaris.wmslite.repository.network.database;

import com.topolaris.wmslite.model.base.BaseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author toPolaris
 * description 数据库操作工具类
 * @date 2021/5/19 14:32
 */
public class DatabaseUtil {
    private static final String TAG = "DatabaseUtil";
    private static final Connection CONNECTION = MySQLConnector.getConnection();

    /**
     * 解析无返回值的Sql语句
     *
     * @param sqlString 被解析的Sql语句
     */
    public static void executeSqlWithoutResult(String sqlString) {
        try {
            PreparedStatement stmt = CONNECTION.prepareStatement(sqlString);
            // 关闭事务自动提交 ,这一行必须加上
            CONNECTION.setAutoCommit(false);
            stmt.addBatch();
            stmt.executeBatch();
            CONNECTION.commit();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析有返回数据的Sql语句
     *
     * @param sqlString 被解析的Sql语句
     * @param eClass    返回数据对应实体类的Class对象
     * @param <E>       返回数据对应的实体类类型
     * @return 返回对象集合
     */
    public static <E extends BaseEntity> ArrayList<E> executeSqlWithResult(String sqlString, Class<E> eClass) {
        ArrayList<E> result = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>(8);
        try {
            Statement stmt = CONNECTION.createStatement();
            ResultSet res = stmt.executeQuery(sqlString);
            if (res != null) {
                while (res.next()) {
                    try {
                        E e = eClass.newInstance();
                        map.put("ClassType", eClass.getSimpleName());
                        for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
                            String field = res.getMetaData().getColumnName(i);
                            map.put(field, res.getString(field));
                        }
                        e.convertFromMap(map);
                        result.add(e);
                    } catch (IllegalAccessException | InstantiationException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    }
                }
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
