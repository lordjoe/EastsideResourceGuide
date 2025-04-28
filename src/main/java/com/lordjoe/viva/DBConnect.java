package com.lordjoe.viva;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * com.lordjoe.viva.DBConnect
 * User: Steve
 * Date: 4/22/25
 */
public class DBConnect {

    private static String USED = null;
    private static Properties props = new Properties();

    public static void loadEnv() {
        try {
            props.load(new FileInputStream(".env"));
            props.forEach((k, v) -> System.setProperty(k.toString(), v.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    public static String getUsed() {
        if(USED == null) {
            loadEnv();
            USED = props.getProperty("LOCAL");
        }
        return USED;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(getUsed());
        }
        catch(Exception e ){
                     throw  new SQLException(e);
        }
     }
}
