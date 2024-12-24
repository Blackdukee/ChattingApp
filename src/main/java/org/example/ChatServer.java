package org.example;

import org.example.controller.MessageController;
import org.example.controller.UserController;
import org.example.models.Message;
import org.example.models.User;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChatServer is the main server class for the chat application.
 * It handles client connections, broadcasting messages, and managing active clients.
 */
public class ChatServer {
    private static final int PORT = 12345;
    protected static ConcurrentHashMap<String, ClientHandler> activeClients = new ConcurrentHashMap<>();

    /**
     * The main method to start the chat server.
     *
     * @param args command-line arguments
     */
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

    /**
     * Broadcasts a message to all active clients except the sender.
     *
     * @param message the message to be broadcasted
     * @param sender  the sender of the message
     */
    static void broadcastMessage(String message, String sender) {
        for (ClientHandler client : activeClients.values()) {
            if (!client.getClientName().equals(sender)) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * Adds a client to the active clients list and sets their status to active.
     *
     * @param name          the name of the client
     * @param clientHandler the client handler for the client
     */
    static void addClient(String name, ClientHandler clientHandler) {
        activeClients.put(name, clientHandler);
        UserController.setUserStatus(name, true);
    }

    /**
     * Removes a client from the active clients list and sets their status to inactive.
     *
     * @param name the name of the client
     */
    static void removeClient(String name) {
        activeClients.remove(name);
        UserController.setUserStatus(name, false);
    }

    /**
     * Sends a private message to a specific recipient.
     *
     * @param recipient the recipient of the message
     * @param message   the message to be sent
     * @param sender    the sender of the message
     */
    static void sendPrivateMessage(String recipient, String message, String sender) {
        ClientHandler recipientHandler = activeClients.get(recipient);
        if (recipientHandler != null) {
            recipientHandler.sendMessage("/private " + sender + " " + message);
        }
    }
}

/**
 * ClientHandler handles the communication with a single client.
 */
class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String clientName;

    /**
     * Constructs a new ClientHandler for the specified socket.
     *
     * @param socket the socket for the client connection
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Returns the name of the client.
     *
     * @return the name of the client
     */
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (friends != null) {
                for (String friend : friends.keySet()) {
                    ClientHandler friendHandler = ChatServer.activeClients.get(friend);
                    if (friendHandler != null) {
                        friendHandler.sendMessage("/active" + " : " + clientName);
                    }
                }
            }

            if (clientName != null) {
                ChatServer.addClient(clientName, this);
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
                    } else if (message.startsWith("/friends")) {
                        // Update the friend list when a new friend is added
                        ConcurrentHashMap<String, Boolean> friendUser = null;
                        try {
                            friendUser = UserController.getFriends(clientName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (friendUser != null) {
                            for (String friend : friendUser.keySet()) {
                                output.println("/friends" + " " + friend + " " + friendUser.get(friend));
                            }
                        }
                    } else if (message.startsWith("/friendAccepted")) {
                        String[] parts = message.split(":", 2);
                        if (parts.length >= 2) {
                            String friend = parts[1];
                            ChatServer.activeClients.get(friend).sendMessage("/friendAccepted" + " " + clientName);
                        }
                    } else if (message.startsWith("/deleteFriend")) {
                        String[] parts = message.split(":", 2);
                        if (parts.length >= 2) {
                            String friend = parts[1];
                            ChatServer.activeClients.get(friend).sendMessage("/deleteFriend" + ":" + clientName);
                        }
                    } else {
                        // Broadcast to all clients
                        ChatServer.broadcastMessage(clientName + ": " + message, clientName);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                ConcurrentHashMap<String, Boolean> friends = UserController.getFriends(clientName);
                assert friends != null;
                for (String friend : friends.keySet()) {
                    ClientHandler friendHandler = ChatServer.activeClients.get(friend);
                    if (friendHandler != null) {
                        friendHandler.sendMessage("/exit" + " : " + clientName);
                    }
                }
                ChatServer.removeClient(clientName);
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(clientName + " disconnected.");
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param message the message to be sent
     */
    public void sendMessage(String message) {
        output.println(message);
    }
}
