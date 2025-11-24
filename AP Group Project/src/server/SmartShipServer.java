package server;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class SmartShipServer {

    private ServerSocket serverSocket;
    private static int clientCount = 0;

    public SmartShipServer() {
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("SmartShip Server started: " + new Date());

            // Ensure DB is connected at startup
            DatabaseConnection.getConnection();
            System.out.println("Database connection established.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Connected: " + clientSocket.getInetAddress());
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
                System.out.println(clientCount + " client threads running...");
            }

        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }

    /**
     * Handles a single client
     */
    class ClientHandler extends Thread {

        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            clientCount++;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                while (true) {

                    String command = (String) in.readObject();

                    if (command.equalsIgnoreCase("EXIT")) {
                        break;
                    }

                    // Command example:  GET_STATUS|TRK002
                    String[] parts = command.split("\\|");

                    if (parts[0].equals("GET_STATUS")) {
                        String trackingNo = parts[1];
                        String response = getShipmentStatus(trackingNo);
                        out.writeObject(response);
                    }

                    // Additional commands may be added here...
                }

                in.close();
                out.close();
                clientSocket.close();

                clientCount--;
                System.out.println("Client disconnected. " + clientCount + " client threads running.");

            } catch (Exception e) {
                System.err.println("Client handler error: " + e.getMessage());
            }
        }

        /**
         * Query shipment status from database
         */
        private String getShipmentStatus(String trackingNumber) {
            try {
                Connection conn = DatabaseConnection.getConnection();

                String sql = "SELECT `Status` FROM baseshipment WHERE `Tracking Number` = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, trackingNumber);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return "STATUS|" + rs.getString("Status");
                } else {
                    return "STATUS|NOT_FOUND";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "STATUS|ERROR";
            }
        }
    }

    public static void main(String[] args) {
        new SmartShipServer();
    }
}
