package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class SmartShipClient {

    public static void main(String[] args) {

        final String SERVER_IP = "127.0.0.1";
        final int SERVER_PORT = 5000;

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in  = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Connected to SmartShip Server!");

            while (true) {
                System.out.println("\n===== CLIENT MENU =====");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Exit");
                System.out.print("Choose: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1 -> {
                        out.writeObject("register");

                        System.out.print("Name: ");
                        out.writeObject(sc.nextLine());

                        System.out.print("Email: ");
                        out.writeObject(sc.nextLine());

                        System.out.print("Password: ");
                        out.writeObject(sc.nextLine());

                        System.out.println((String) in.readObject());
                    }

                    case 2 -> {
                        out.writeObject("login");

                        System.out.print("Email: ");
                        out.writeObject(sc.nextLine());

                        System.out.print("Password: ");
                        out.writeObject(sc.nextLine());

                        String response = (String) in.readObject();

                        if (response.equals("LOGIN_SUCCESS")) {
                            String name = (String) in.readObject();
                            System.out.println("Welcome " + name + "!");
                        } else {
                            System.out.println("Invalid login!");
                        }
                    }

                    case 0 -> {
                        out.writeObject("exit");
                        System.out.println("Goodbye!");
                        return;
                    }

                    default -> System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            System.out.println("Connection lost!");
        }
    }
}
