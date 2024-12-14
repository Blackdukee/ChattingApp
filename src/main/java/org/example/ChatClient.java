package org.example;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class ChatClient {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static final List<String> friends = new java.util.ArrayList<>(List.of());

    public static void main(String[] args) {

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            output.println("user2");
            output.println("user2");


            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    output.println("/friends");

                    while ((serverMessage = input.readLine()) != null) {

                        if (serverMessage.startsWith("/friends")){
                            friends.add(serverMessage.split(" ")[1]);

                        }else if (serverMessage.startsWith("/active")){
                            String[] parts = serverMessage.split(":");
                            System.out.println(parts[1] + " is online");
                        } else if ( serverMessage.startsWith("/exit")){
                            System.out.println(serverMessage.split(":")[1] + " has left the chat");
                        }else {

                            System.out.println(serverMessage);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Connection closed.");
                }
            });
            listenerThread.start();


            // how to wait for the friends list to be populated
            while (friends.isEmpty()){
                Thread.sleep(1000);
            }



            // Send messages to the server
            System.out.println("Type your messages below:");
            System.out.println("To send a private message: /msg <recipient_name> <message>");
            while (true) {
                String userMessage = scanner.nextLine();
                output.println(userMessage);

                if (userMessage.equalsIgnoreCase("exit")) {
                    System.out.println("Disconnected from the server.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
