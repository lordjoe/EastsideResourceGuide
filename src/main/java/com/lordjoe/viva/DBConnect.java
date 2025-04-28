package com.lordjoe.viva;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * com.lordjoe.viva.DBConnect
 * User: Steve
 * Date: 4/22/25
 */
public class DBConnect {

    private static String USED = "LOCAL";



    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(USED);
        }
        catch(Exception e ){
                     throw  new SQLException(e);
        }
     }
}
