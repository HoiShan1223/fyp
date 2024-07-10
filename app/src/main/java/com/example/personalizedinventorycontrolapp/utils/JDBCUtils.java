package com.example.personalizedinventorycontrolapp.utils;

import android.net.wifi.WifiManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtils {

    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String ip = null;


    private static final String DB_NAME = "test";
    private static final String DB_USERNAME = "rachel";
    private static final String DB_PASSWORD = "51131632";

    static {

        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConn() {
        Connection  conn = null;
        String DB_URL = "jdbc:mysql://" + "192.168.0.32:3306/test?autoReconnect=true&useSSL=false";
        try {
            conn= DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);

            if(conn != null){
                System.out.println("Successfully connected.");
            }else{
                System.out.println("Failed to connect.");
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return conn;
    }

    public static void close(Connection conn){
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
