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
 * @author Liangyong Ni
 * description 数据库操作工具类
 * @date 2021/5/19 14:32
 */
public class DatabaseUtil {
    private static Connection connection = MySQLConnector.getConnection();

    /**
     * 解析无返回值的Sql语句
     *
     * @param sqlString 被解析的Sql语句
     * @return 语句是否解析成功
     */
    public static boolean executeSqlWithoutResult(String sqlString) {
        try {
            if (connection == null) {
                connection = MySQLConnector.getConnection();
                if (connection == null) {
                    return false;
                }
            }
            PreparedStatement stmt = connection.prepareStatement(sqlString);
            // 关闭事务自动提交 ,这一行必须加上
            connection.setAutoCommit(false);
            stmt.addBatch();
            stmt.executeBatch();
            connection.commit();
            stmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
        // 数据请求失败返回null，空数据返回空集合
        if (connection == null) {
            connection = MySQLConnector.getConnection();
            if (connection == null) {
                return null;
            }
        }
        ArrayList<E> result = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>(8);
        try {
            result = new ArrayList<>();
            Statement stmt = connection.createStatement();
            ResultSet res = stmt.executeQuery(sqlString);
            if (res != null) {
                while (res.next()) {
                    try {
                        E e = eClass.newInstance();
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
