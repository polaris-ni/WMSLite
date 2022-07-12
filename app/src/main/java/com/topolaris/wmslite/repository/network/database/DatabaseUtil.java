package com.topolaris.wmslite.repository.network.database;

import android.util.Log;

import com.topolaris.wmslite.model.base.BaseEntity;
import com.topolaris.wmslite.model.user.User;
import com.topolaris.wmslite.model.user.UserAuthority;

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
    private static final String TAG = "DatabaseUtil";

    /**
     * 解析无返回值的Sql语句
     *
     * @param sqlString 被解析的Sql语句
     * @return 语句是否解析成功
     */
    public static boolean executeSqlWithoutResult(String sqlString) {
        Connection connection = MySqlConnector.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sqlString);
            connection.setAutoCommit(false);
            stmt.addBatch();
            stmt.executeBatch();
            connection.commit();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                return false;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return true;
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
    Connection connection = MySqlConnector.getConnection();
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
            res.close();
        }
        stmt.close();
        connection.close();
    } catch (SQLException e) {
        Log.e(TAG, "executeSqlWithResult: " + sqlString);
        e.printStackTrace();
    }
    return result;
}

    /**
     * 使用管理员权限的连接解析有返回数据的Sql语句
     *
     * @param sqlString 被解析的Sql语句
     * @param eClass    返回数据对应实体类的Class对象
     * @param <E>       返回数据对应的实体类类型
     * @return 返回对象集合
     */
    public static <E extends BaseEntity> ArrayList<E> executeAdminSqlWithResult(String sqlString, Class<E> eClass) {
        // 数据请求失败返回null，空数据返回空集合
        Connection connection = MySqlConnector.getAdminConnection();
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
                res.close();
            }
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            Log.e(TAG, "executeSqlWithResult: " + sqlString);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 使用有管理员权限的连接解析无返回值的Sql语句
     *
     * @param sqlString 被解析的Sql语句
     * @return 语句是否解析成功
     */
    public static boolean executeAdminSqlWithoutResult(String sqlString) {
        try {
            Connection connection = MySqlConnector.getAdminConnection();
            PreparedStatement stmt = connection.prepareStatement(sqlString);
            // 关闭事务自动提交 ,这一行必须加上
            connection.setAutoCommit(false);
            stmt.addBatch();
            stmt.executeBatch();
            connection.commit();
            stmt.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "executeSqlWithoutResult: " + sqlString);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateUser(User oldUser, User newUser) {
        return deleteUser(oldUser) && createUser(newUser);
    }

    public static boolean createUser(User newUser) {
        ArrayList<String> sqlList = new ArrayList<>();
        sqlList.add(String.format("create user '%s'@'%%' identified by '%s';", newUser.getName(), newUser.getPassword()));
        switch (newUser.getAuthority()) {
            case UserAuthority.ADMINISTRATOR:
                sqlList.add("grant all privileges on wmsdatabase.* to " + newUser.getName() + ";");
                break;
            case UserAuthority.PURCHASER:
                sqlList.add("grant all privileges on wmsdatabase.goodsinfo to " + newUser.getName() + ";");
                sqlList.add("grant all privileges on wmsdatabase.purchase to " + newUser.getName() + ";");
                sqlList.add("grant all privileges on wmsdatabase.shortage to " + newUser.getName() + ";");
                sqlList.add("grant select on wmsdatabase.popular_goods to " + newUser.getName() + ";");
                break;
            case UserAuthority.SHIPMENT:
                sqlList.add("grant all privileges on wmsdatabase.goodsinfo to " + newUser.getName() + ";");
                sqlList.add("grant all privileges on wmsdatabase.shipment to " + newUser.getName() + ";");
                sqlList.add("grant all privileges on wmsdatabase.shortage to " + newUser.getName() + ";");
                sqlList.add("grant select on wmsdatabase.popular_goods to " + newUser.getName() + ";");
                break;
            case UserAuthority.CHECKER:
                sqlList.add("grant all privileges on wmsdatabase.goodsinfo to " + newUser.getName() + ";");
                sqlList.add("grant all privileges on wmsdatabase.shipment to " + newUser.getName() + ";");
                sqlList.add("grant all privileges on wmsdatabase.purchase to " + newUser.getName() + ";");
                sqlList.add("grant all privileges on wmsdatabase.shortage to " + newUser.getName() + ";");
                sqlList.add("grant select on wmsdatabase.popular_goods to " + newUser.getName() + ";");
                break;
            default:
                break;
        }
        sqlList.add("insert into wmsusers values" + newUser.getSql() + ";");
        for (String sql : sqlList) {
            if (!executeAdminSqlWithoutResult(sql)) {
                return false;
            }
        }
        return true;
    }

    public static boolean deleteUser(User user) {
        return executeAdminSqlWithoutResult(String.format("drop user '%s'@'%%';", user.getName()))
                && executeAdminSqlWithoutResult(String.format("delete from wmsusers where uid = '%s'", user.getUid()));
    }

    public static void setConnectorUser(User user) {
        MySqlConnector.setUser(user);
    }

    public static void setConnectorUserWithAdmin() {
        MySqlConnector.setConnectorUserWithAdmin();
    }
}
