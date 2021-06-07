package com.topolaris.wmslite.repository.network.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Liangyong Ni
 * description 用来连接远程数据库
 * @date 2021/5/19 14:32
 */
public class MySQLConnector {
    private static final String TAG = "MySQLConnector";
    private static MySQLConnector connector = null;
    private static Connection conn = null;
    private final String driver;
    private final String dbURL;
    private final String user;
    private final String password;

    private MySQLConnector() {
        driver = "com.mysql.jdbc.Driver";
        dbURL = "jdbc:mysql://rm-bp1e2ibqfu5p96kqc2o.mysql.rds.aliyuncs.com:3306/wmsdatabase";
        user = "polarisni";
        password = "941113aliyun_";
    }

    public static Connection getConnection() {
        if (conn == null) {
            if (connector == null) {
                connector = new MySQLConnector();
            }
            try {

                Class.forName(connector.driver);
                while (true){
                    conn = DriverManager.getConnection(connector.dbURL, connector.user, connector.password);
                    if (conn != null) {
                        return conn;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}
