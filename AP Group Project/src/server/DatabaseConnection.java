package server;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static Connection myConn = null;

    private static final String URL = "jdbc:mysql://localhost:3307/smartship_db";
    private static final String USER = "root";
    private static final String PASSWORD = "usbw";

    /**
     * Returns a single shared connection instance.
     */
    public static Connection getConnection() {
        if (myConn == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // load driver
                myConn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println(" Database Connected!");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(" Database Connection Failed!");
            }
        }
        return myConn;
    }
}
