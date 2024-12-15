package org.example;

import org.example.controller.MessageController;
import org.example.controller.UserController;
import org.example.models.Message;
import org.example.models.User;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final int PORT = 12345;
    protected static ConcurrentHashMap<String, ClientHandler> activeClients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Handle the client in a new thread

                ClientHandler clientHandler = new ClientHandler(clientSocket);

                new Thread(clientHandler).start();

            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    static void broadcastMessage(String message, String sender) {
        for (ClientHandler client : activeClients.values()) {
            if (!client.getClientName().equals(sender)) {
                client.sendMessage(message);
            }
        }
    }

    static void addClient(String name, ClientHandler clientHandler) {
        activeClients.put(name, clientHandler);
        UserController.setUserStatus(name, true);
    }

    static void removeClient(String name) {
        activeClients.remove(name);
        UserController.setUserStatus(name, false);
    }

    static void sendPrivateMessage(String recipient, String message, String sender) {
        ClientHandler recipientHandler = activeClients.get(recipient);
        if (recipientHandler != null) {
            recipientHandler.sendMessage("/private " + sender + " " + message);
        }

    }

}

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);


            clientName = input.readLine();
            ConcurrentHashMap<String, Boolean> friends = null;
            try {
                     friends = UserController.getFriends(clientName);
            }catch (Exception e){
                e.printStackTrace();
            }
            if ( friends != null) {
                for (String friend : friends.keySet()) {

                    ClientHandler friendHandler = ChatServer.activeClients.get(friend);
                    if (friendHandler != null) {
                        friendHandler.sendMessage("/active" + " : " + clientName);
                    }

                }
            }

            if (clientName != null) {

                ChatServer.addClient(clientName, this);
//                ChatServer.broadcastMessage(clientName + " has joined the chat!", clientName);
                System.out.println(clientName + " joined the chat.");


                // Read messages from this client and process them
                String message;
                while ((message = input.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }

                    if (message.startsWith("/msg ")) {
                        // Parse private message
                        String[] parts = message.split(" ", 3);
                        if (parts.length >= 3) {
                            String recipient = parts[1];
                            String privateMessage = parts[2];
                            ChatServer.sendPrivateMessage(recipient, privateMessage, clientName);
                            Message message1 = new Message();
                            message1.setContent(privateMessage);
                            User sender = UserController.getUserByUsername(clientName);
                            User recipientUser = UserController.getUserByUsername(recipient);
                            message1.setSender(sender);
                            message1.setRecipient(recipientUser);
                            MessageController.saveMessage(message1);

                        } else {
                            output.println("Invalid command. Use /msg <recipient> <message>");
                        }
                    }else if (message.startsWith("/friends")){
                    // todo: update the friend list when a new friend is added
                        ConcurrentHashMap<String, Boolean> friendUser = null;
                        try {
                             friendUser = UserController.getFriends(clientName);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (friendUser != null) {
                            for (String friend : friendUser.keySet()) {
                                output.println("/friends" + " " + friend + " " + friendUser.get(friend));
                            }
                        }

                    } else if(message.startsWith("/friendAccepted")){
                            String[] parts = message.split(":", 2);
                            if (parts.length >= 2) {
                                String friend = parts[1];
                                ChatServer.activeClients.get(friend).sendMessage("/friendAccepted" + " " + clientName);
                            }
                    }
                    else {
                        // Broadcast to all clients

                        ChatServer.broadcastMessage(clientName + ": " + message, clientName);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {

                ConcurrentHashMap<String,Boolean> friends = UserController.getFriends(clientName);
                assert friends != null;
                for (String friend : friends.keySet()) {
                    ClientHandler friendHandler = ChatServer.activeClients.get(friend);
                    if (friendHandler != null) {
                        friendHandler.sendMessage("/exit" + " : " + clientName );
                    }
                }
                ChatServer.removeClient(clientName);
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println(clientName + " disconnected.");
        }

    }

//    public void sendActiveFriends() {
//        ConcurrentHashMap<String, Boolean> friends = UserController.getFriends(clientName);
//        for (String friend : friends.keySet()) {
//            output.println("/friends" + " " + friend);
//        }
//    }

    public void sendMessage(String message) {
        output.println(message);
    }
}
