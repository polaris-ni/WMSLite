package com.topolaris.wmslite.repository.network.database;

import com.topolaris.wmslite.model.user.User;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Liangyong Ni
 * description 用来连接远程数据库
 * @date 2021/5/19 14:32
 */
public class MySqlConnector {
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://rm-bp1e2ibqfu5p96kqc2o.mysql.rds.aliyuncs.com:3306/wmsdatabase?erverTimezone=GMT";
    private static String username = "polaris";
    private static String password = "941113aliyun_";

    protected static void setUser(User user) {
        MySqlConnector.username = user.getName();
        MySqlConnector.password = user.getPassword();
    }

    protected static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(DRIVER).newInstance();
            conn = DriverManager.getConnection(URL, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    protected static Connection getAdminConnection() {
        Connection conn = null;
        try {
            Class.forName(DRIVER).newInstance();
            conn = DriverManager.getConnection(URL, "polaris", "941113aliyun_");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void setConnectorUserWithAdmin() {
        username = "polaris";
        password = "941113aliyun_";
    }
}
