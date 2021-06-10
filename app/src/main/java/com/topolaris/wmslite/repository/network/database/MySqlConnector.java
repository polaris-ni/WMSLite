package com.topolaris.wmslite.repository.network.database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Liangyong Ni
 * description 用来连接远程数据库
 * @date 2021/5/19 14:32
 */
public class MySqlConnector {
    private static final String TAG = "MySQLConnector";
    private static String driver = "com.mysql.jdbc.Driver";
    private static String dbURL = "jdbc:mysql://rm-bp1e2ibqfu5p96kqc2o.mysql.rds.aliyuncs.com:3306/wmsdatabase?erverTimezone=GMT";
    private static String user = "polarisni";
    private static String password = "941113aliyun_";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(dbURL, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
