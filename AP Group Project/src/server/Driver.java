package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class Driver {
	public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3307/";
        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;

        try {
            // 1. Get Connection to localhost/xampp server
            myConn = DriverManager.getConnection(url, "root", "usbw");

            if (myConn != null) { // If connection was made to the xampp server
                JOptionPane.showMessageDialog(null, 
                        "Connected to Local Server", 
                        "JDBC Connection Status", 
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // 2. Create a statement
            myStmt = myConn.createStatement();

            // 3. Execute SQL Query (example: show all databases)
            String sql = "SHOW DATABASES;";
            myRs = myStmt.executeQuery(sql);

            // 4. Process the result set
            System.out.println("=== DATABASES FOUND ===");
            while (myRs.next()) {
                System.out.println(myRs.getString(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close everything
                if (myRs != null) myRs.close();
                if (myStmt != null) myStmt.close();
                if (myConn != null) myConn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
