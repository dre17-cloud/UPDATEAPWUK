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
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Connected to SmartShip Server\n");

            while (true) {
                System.out.println("\n===== CLIENT MENU =====");
                System.out.println("1. Register User");
                System.out.println("2. Login");
                System.out.println("0. Exit");
                System.out.print("Choose: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> {
                        out.writeObject("register");
                        System.out.print("Enter name: ");
                        out.writeObject(sc.nextLine());
                        System.out.print("Enter email: ");
                        out.writeObject(sc.nextLine());
                        System.out.print("Enter password: ");
                        out.writeObject(sc.nextLine());
                        System.out.print("Enter role (Customer/Clerk/Driver/Manager): ");
                        out.writeObject(sc.nextLine());
                        System.out.println(" Waiting for server...");
                        System.out.println(in.readObject());
                    }

                    case 2 -> {
                        out.writeObject("login");
                        System.out.print("Enter email: ");
                        out.writeObject(sc.nextLine());
                        System.out.print("Enter password: ");
                        out.writeObject(sc.nextLine());
                        System.out.println(" Waiting for server...");
                        System.out.println(in.readObject());
                    }

                    case 0 -> {
                        out.writeObject("exit");
                        System.out.println(" Disconnected from server.");
                        return;
                    }

                    default -> System.out.println(" Invalid option.");
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println(" Connection error: " + e.getMessage());
        }
    }
}
