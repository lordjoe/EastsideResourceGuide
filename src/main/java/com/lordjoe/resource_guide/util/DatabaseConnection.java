package com.lordjoe.resource_guide.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.lordjoe.resource_guide.util.SecretFetcher.getJdbcUrlFromSecret;
/**
 * com.lordjoe.viva.DBConnect
 * User: Steve
 * Date: 4/22/25
 */
public class DatabaseConnection {

    private static String USED = null;
    private static Properties props = new Properties();
    private static boolean TEST = false;
    private static boolean REMOTE = false;

    public static void setTEST() {
        TEST = true;
    }
    public static void setREMOTE() {
        try {
            SecretFetcher.getPasswordfromSecret(); // make sure this works
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        REMOTE = true;
    }

    public static boolean isRemote() {
        return REMOTE;
    }

    public static void loadEnv() {
        try {
            props.load(new FileInputStream(".env"));
            props.forEach((k, v) -> System.setProperty(k.toString(), v.toString()));
            USED = props.getProperty("LOCAL") ;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    public static String getUsed() {
        try {
            if(TEST)
                return getTest();

            if(USED == null) {
                if(REMOTE) {
                    USED = getJdbcUrlFromSecret();
                }
                 else {
                      loadEnv();  // will set USED 
                   }
             }
            return USED;
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
    // should be alternate DB
    public static String getTest() {
        if(USED == null) {
            loadEnv();
            USED = props.getProperty("TEST");
        }
        return USED;
    }

    private static Connection  theConnection = null;

    public static Connection getConnectionX() throws SQLException {
           if(theConnection != null) {
               if( theConnection.isClosed()) {
                     theConnection = getConnection();
                }
           }
           else {
               theConnection = getConnection();
           }
           return theConnection;
    }


        public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            if(!REMOTE)
              return DriverManager.getConnection(getUsed());
            else {
                String passwordfromSecret = SecretFetcher.getPasswordfromSecret();
                return DriverManager.getConnection(
                        "jdbc:postgresql://pg-2737f92c-vivavolunteers.b.aivencloud.com:26951/defaultdb?sslmode=require",
                        "avnadmin", passwordfromSecret
                  );
            }
        }
        catch(Exception e ){
                     throw  new SQLException(e);
        }
     }

    public static Connection getTestConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(getTest());
        }
        catch(Exception e ){
            throw  new SQLException(e);
        }
    }

    public static void clearConnection() {
        theConnection = null;
    }
}
