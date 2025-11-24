package server;

import java.io.*;
import java.net.*;
import java.sql.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            // Correct stream order (must ALWAYS be output FIRST)
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            while (true) {

                String command;

                // Safe read — prevents instant disconnect crash
                try {
                    command = (String) in.readObject();
                } catch (EOFException | SocketException e) {
                    System.out.println("Client closed connection.");
                    return;
                }

                if (command == null) {
                    System.out.println("Client sent null, closing.");
                    return;
                }

                switch (command) {

                    case "register" -> registerUser();
                    case "login" -> loginUser();
                    case "exit" -> {
                        socket.close();
                        return;
                    }

                    default -> out.writeObject("INVALID_COMMAND");
                }
            }

        } catch (Exception e) {
            System.out.println("Client disconnected.");
        }
    }

    private void registerUser() throws Exception {
        String name = (String) in.readObject();
        String email = (String) in.readObject();
        String password = (String) in.readObject();

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO users(name, email, password) VALUES(?,?,?)"
        );

        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, password);
        ps.executeUpdate();

        out.writeObject("REGISTER_SUCCESS");
    }

    private void loginUser() throws Exception {
        String email = (String) in.readObject();
        String password = (String) in.readObject();

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT name FROM users WHERE email=? AND password=?"
        );

        ps.setString(1, email);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            out.writeObject("LOGIN_SUCCESS");
            out.writeObject(rs.getString("name"));
        } else {
            out.writeObject("LOGIN_FAIL");
        }
    }
}
