package org.example.gui;
import org.example.controller.FriendRequestController;
import org.example.controller.MessageController;
import org.example.controller.UserController;
import org.example.models.FriendRequest;
import org.example.models.Message;
import org.example.models.User;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class ClientChatGUI extends JFrame implements MessageListener {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static  java.util.List<String> friends ;

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    @Override
    public void onMessageReceived(String from, String message) {

            if (selectedUser == null || !selectedUser.equals(from)) {
                JButton friendButton = friendButtons.get(from);
                if (friendButton != null) {
                    String text = friendButton.getText();
                    int unreadMessages = Integer.parseInt(text.split(" ")[1]);
                    unreadMessages++;
                    friendButton.setText(from + " " + unreadMessages);
                }
            }

            messagePanel.add(createChatMessageComponent(from, message));
            messagePanel.revalidate();
            messagePanel.repaint();


            SwingUtilities.invokeLater(() -> messageScrollPane.getVerticalScrollBar().setValue(messageScrollPane.getVerticalScrollBar().getMaximum()));
    }


    @Override
    public void onActiveUsersChanged(String[] activeUsers) {


    }


    private final User user;
    private JPanel connectedUsersPanel;
    private JPanel messagePanel;
    JScrollPane messageScrollPane;
    private String selectedUser;
    private HashMap<String,JButton> friendButtons;

    public ClientChatGUI(User user) {
        super("User: " + user.getUsername());
        this.user = user;
        setSize(1218,685);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                   int option = JOptionPane.showConfirmDialog(ClientChatGUI.this,
                            "Are you sure you want to exit the chat?",
                            "Exit chat",
                            JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        closeConnection();
                       ClientChatGUI.this.dispose();
                    }

            }
        });


        startConnection();
        addGuiComponents();


    }

    private List<String> activeFriend;
    private void startConnection() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                output.println(user.getUsername());

                Thread listenerThread = new Thread(() -> {
                    try {
                        String serverMessage;
                        friends = new java.util.ArrayList<>(List.of());
                        output.println("/friends");

                        while ((serverMessage = input.readLine()) != null) {
                            if (serverMessage.startsWith("/friends")) {
                                friends.add(serverMessage.split(" ")[1]);
                                for (String friend : friends) {
                                    System.out.println(friend);
                                }
                            } else if (serverMessage.startsWith("/active")) {
                                    onActiveUsersChanged(serverMessage.split(":"));

                            } else if (serverMessage.startsWith("/exit")) {
                                    onActiveUsersChanged(serverMessage.split(":"));

                            } else if(serverMessage.startsWith("/private")) {
                                String[] parts = serverMessage.split(" ", 3);
                                if (parts.length >= 3) {
                                    String from = parts[1];
                                    String message = parts[2];
                                    onMessageReceived(from, message);
                                }

                            }else if (serverMessage.startsWith("/friendAccepted")) {
                                String[] parts = serverMessage.split(" ", 2);
                                if (parts.length >= 2) {
                                    String friend = parts[1];
                                    friends.add(friend);
                                    JButton userButton = new JButton(friend);
                                    userButton.setFocusable(false);
                                    userButton.setFont(new Font("Inter", Font.PLAIN, 16));
                                    userButton.setBorder(Utilities.addPadding(10, 10, 10, 10));
                                    userButton.setBackground(Utilities.SECONDARY_COLOR);
                                    userButton.setForeground(Utilities.Text_COLOR);
                                    userButton.addActionListener(e -> {
                                        openFriendChat(friend, ActiveUsersPane);
                                    });
                                    friendButtons.put(friend, userButton);
                                    ActiveUsersPane.add(userButton);
                                    ActiveUsersPane.revalidate();
                                    ActiveUsersPane.repaint();

                                }
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Connection closed.");
                    }
                });
                listenerThread.start();

                // Send messages to the server
//                try (Scanner scanner = new Scanner(System.in)) {
//                    while (true) {
//                        String userMessage = scanner.nextLine();
//                        output.println(userMessage);
//
//                        if (userMessage.equalsIgnoreCase("exit")) {
//                            System.out.println("Disconnected from the server.");
//                            break;
//                        }
//                    }
//                }
            } catch (IOException e) {
                System.err.println("Error connecting to server: " + e.getMessage());
            }
        }).start();

    }

    private void closeConnection() {
        try {
            if (output != null) {
                output.println("exit");
            }
            if (socket != null) {
                socket.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    private void sendPrivateMessage(String to , String message) {


        if (output != null) {
            output.println("/msg " + to + " " + message);
        }

    }



    private void addGuiComponents() {
        addConnectedUsersComponents();
        addChatComponents();

    }


    private JList<String> friendRequestsList;

    private JFrame frame;
    private JPanel friendRequestsPanel;
    private List<String> friendRequestsFromController;
    private JButton acceptButton ;
    private JButton rejectButton ;
    private JPanel ActiveUsersPane;
    private void addConnectedUsersComponents() {
        connectedUsersPanel = new JPanel();
        connectedUsersPanel.setBorder(Utilities.addPadding(10, 10, 10, 10));
        connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel, BoxLayout.Y_AXIS));
        connectedUsersPanel.setBackground(Utilities.PRIMARYP_COLOR);
        connectedUsersPanel.setPreferredSize(new Dimension(200, getHeight()));


        JButton addFriendButton = addFriendButton();
        JButton friendRequestsButton = new JButton("Friend Requests");
        friendRequestsButton.setFocusable(false);
        friendRequestsButton.setFont(new Font("Inter", Font.PLAIN, 16));
        friendRequestsButton.setBorder(Utilities.addPadding(10, 10, 10, 10));


        friendRequestsButton.addActionListener(e -> {

            if (frame == null) {
                frame = new JFrame("Friend Requests");
                frame.setSize(400, 400);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                frame.setVisible(true);
                // on dispose set the frame to null
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        frame = null;

                    }
                });

                friendRequestsPanel = new JPanel();
                friendRequestsPanel.setLayout(new BoxLayout(friendRequestsPanel, BoxLayout.Y_AXIS));
                friendRequestsPanel.setBackground(Utilities.SECONDARY_COLOR);
                friendRequestsPanel.setBorder(Utilities.addPadding(10, 10, 10, 10));


                friendRequestsFromController = FriendRequestController.getFriendRequests(user);

                friendRequestsList = new JList<>(friendRequestsFromController.toArray(new String[0]));

                friendRequestsList.addListSelectionListener(e1 -> {
                    String selectedFriendRequest = friendRequestsList.getSelectedValue();
                    System.out.println("Selected friend request: " + selectedFriendRequest);
                });

                friendRequestsPanel.add(friendRequestsList);

                acceptButton = new JButton("Accept");
                rejectButton = new JButton("Reject");


                // TODO: implement the accept and reject buttons
                acceptButton.addActionListener(e1 -> {
                    String selectedFriendRequest = friendRequestsList.getSelectedValue();
                    if (selectedFriendRequest != null) {
                        User friend = UserController.getUserByUsername(selectedFriendRequest);
                        FriendRequest friendRequest = FriendRequestController.getFriendRequest(friend, user);
                        if (friendRequest != null) {
                            FriendRequestController.acceptFriendRequest(friendRequest);
                            JOptionPane.showMessageDialog(frame, "Friend request accepted");
                            output.println("/friendAccepted "+ ":" + friend.getUsername());

                            // add the friend to list of friends
                                friends.add(friend.getUsername());
                                JButton userButton = new JButton(friend.getUsername());
                                userButton.setFocusable(false);
                                userButton.setFont(new Font("Inter", Font.PLAIN, 16));
                                userButton.setBorder(Utilities.addPadding(10, 10, 10, 10));
                                userButton.setBackground(Utilities.SECONDARY_COLOR);
                                userButton.setForeground(Utilities.Text_COLOR);
                            userButton.addActionListener(e2 -> {
                                openFriendChat(friend.getUsername(), ActiveUsersPane);
                            });
                            friendButtons.put(friend.getUsername(), userButton);
                            ActiveUsersPane.add(userButton);
                            ActiveUsersPane.revalidate();
                            ActiveUsersPane.repaint();

                            friendRequestsFromController.remove(selectedFriendRequest);

                            friendRequestsList.setListData(friendRequestsFromController.toArray(new String[0]));




                        }
                    }
                });

                rejectButton.addActionListener(e1 -> {
                    String selectedFriendRequest = friendRequestsList.getSelectedValue();
                    if (selectedFriendRequest != null) {
                        User friend = UserController.getUserByUsername(selectedFriendRequest);
                        FriendRequest friendRequest = FriendRequestController.getFriendRequest(friend, user);
                        if (friendRequest != null) {
                            FriendRequestController.deleteFriendRequest(friendRequest);
                            JOptionPane.showMessageDialog(frame, "Friend request rejected");
                            DefaultListModel<String> model = (DefaultListModel<String>) friendRequestsList.getModel();
                            int selectedIndex = friendRequestsList.getSelectedIndex();
                            if (selectedIndex != -1) {
                                model.remove(selectedIndex);
                            }
                        }
                    }
                });

                // put them horizontally
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout());
                buttonPanel.setBackground(Utilities.SECONDARY_COLOR);
                buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
                buttonPanel.setPreferredSize(new Dimension(400, 50));
                buttonPanel.add(acceptButton);
                buttonPanel.add(rejectButton);

                friendRequestsPanel.add(buttonPanel);


                frame.add(friendRequestsPanel);


            }



        });



        friendRequestsButton.setBackground(Utilities.SECONDARY_COLOR);
        friendRequestsButton.setForeground(Utilities.Text_COLOR);

        connectedUsersPanel.add(friendRequestsButton);
        connectedUsersPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        connectedUsersPanel.add(addFriendButton);


        JLabel connectedUsersLabel = new JLabel("Connected Users");
        connectedUsersLabel.setFont(new Font("Inter", Font.BOLD, 18));
        connectedUsersLabel.setForeground(Utilities.Text_COLOR);
        connectedUsersPanel.add(connectedUsersLabel);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }




        ActiveUsersPane = new JPanel();
        ActiveUsersPane.setLayout(new BoxLayout(ActiveUsersPane, BoxLayout.Y_AXIS));
        ActiveUsersPane.setBackground(Utilities.SECONDARY_COLOR);


        friendButtons = new HashMap<>();
        for (String user : friends) {

            int unreadMessages = MessageController.getUnreadMessagesCount(this.user.getUsername(), user);

            JButton userButton = getFriendButtons(user, unreadMessages, ActiveUsersPane);
            friendButtons.put(user, userButton);
            ActiveUsersPane.add(userButton);
        }

        JScrollPane connectedUsersScrollPane = new JScrollPane(ActiveUsersPane);
        connectedUsersScrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        connectedUsersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        connectedUsersScrollPane.setPreferredSize(new Dimension(200, getHeight()));

        connectedUsersPanel.add(connectedUsersScrollPane);
        add(connectedUsersPanel, BorderLayout.WEST);

    }

    private JButton getFriendButtons(String user, int unreadMessages, JPanel ActiveUsersPane) {
        JButton userButton = new JButton(user + " " + unreadMessages);
        // add the number of unread messages

        userButton.setFocusable(false);
        userButton.setFont(new Font("Inter", Font.PLAIN, 16));
        userButton.setBorder(Utilities.addPadding(10, 10, 10, 10));
        userButton.setBackground(Utilities.SECONDARY_COLOR);
        userButton.setForeground(Utilities.Text_COLOR);
        userButton.addActionListener(e -> {
            openFriendChat(user, ActiveUsersPane);

        });
        return userButton;
    }

    private void openFriendChat(String user, JPanel ActiveUsersPane) {
        selectedUser = user;
        System.out.println("Selected user: " + selectedUser);
        // make the selected user button look different
        for (Component component : ActiveUsersPane.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals(selectedUser)) {
                    button.setBackground(Utilities.PRIMARYP_COLOR);
                } else {
                    button.setBackground(Utilities.SECONDARY_COLOR);
                }
            }
        }
        // when a user is selected, display the chat messages between the current user and the selected user
        messagePanel.removeAll();
        messagePanel.revalidate();
        messagePanel.repaint();
        messagePanel.repaint();

        getSelectedUserMessages(selectedUser);
    }

    private void getSelectedUserMessages(String selectedUser) {
        // get the messages between the current user and the selected user
        long selectedFriendID = UserController.getUserByUsername(selectedUser) == null ? -1 : UserController.getUserByUsername(selectedUser).getId();
        // display the messages in the messagePanel
        List<Message> messages = MessageController.getMessages(selectedFriendID , user.getId());
        if (messages == null || messages.isEmpty()) {
            JOptionPane.showMessageDialog(ClientChatGUI.this, "No messages between you and " + selectedUser);
        }else {
        for (Message message : messages) {
            messagePanel.add(createChatMessageComponent(message));
        }
            SwingUtilities.invokeLater(() -> messageScrollPane.getVerticalScrollBar().setValue(messageScrollPane.getVerticalScrollBar().getMaximum()));
        }

    }

    private JButton addFriendButton() {

        JButton addFriendButton = new JButton("Add Friend");
        addFriendButton.setFocusable(false);
        addFriendButton.setFont(new Font("Inter", Font.PLAIN, 16));
        addFriendButton.setBorder(Utilities.addPadding(10, 10, 10, 10));
        addFriendButton.setBackground(Utilities.SECONDARY_COLOR);
        addFriendButton.setForeground(Utilities.Text_COLOR);
        addFriendButton.addActionListener(e -> {
            String friendName = JOptionPane.showInputDialog(ClientChatGUI.this, "Enter the username of the friend you want to add");
            if (Objects.equals(friendName, user.getUsername()))
            {
                JOptionPane.showMessageDialog(ClientChatGUI.this, "You cannot add yourself as a friend");
            }
            else
            if (friendName != null && !friendName.isEmpty()) {
                if (friends.contains(friendName)) {
                    JOptionPane.showMessageDialog(ClientChatGUI.this, "You are already friends with " + friendName);
                    return;
                }
                User friend = UserController.getUserByUsername(friendName);
                if (friend == null) {
                    JOptionPane.showMessageDialog(ClientChatGUI.this, "User not found");
                }
                else {
                    // add the friend to the current user's list of friends
                    try {
                        FriendRequestController.sendFriendRequest(new FriendRequest(user, friend));
                        JOptionPane.showMessageDialog(ClientChatGUI.this, "Friend request sent to " + friendName);
                    }catch (Exception ex) {
                        if (ex.getMessage().equals("Friend request already sent")) {
                            JOptionPane.showMessageDialog(ClientChatGUI.this, "Friend request already sent to " + friendName);
                        }
                    }
                }
            }
        });
        return addFriendButton;
    }

    private void addChatComponents() {

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Utilities.SECONDARY_COLOR);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Utilities.SECONDARY_COLOR);


        messageScrollPane = new JScrollPane(messagePanel);
        messageScrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        messageScrollPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                revalidate();
                repaint();
            }
        });
        chatPanel.add(messageScrollPane, BorderLayout.CENTER);



        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Utilities.PRIMARYP_COLOR);
        inputPanel.setBorder(Utilities.addPadding(10, 10, 10, 10));

        JTextField inputField = massageField(inputPanel);
        inputPanel.add(inputField, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.CENTER);
    }

    private JTextField massageField(JPanel inputPanel) {
        JTextField inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if( e.getKeyChar() == KeyEvent.VK_ENTER){

                    String message = inputField.getText();
                    if (selectedUser != null && !selectedUser.isEmpty() && !message.isEmpty()) {
                        inputField.setText("");
                        sendPrivateMessage(selectedUser, message);
                        messagePanel.add(createChatMessageComponent(user.getUsername(), message));
                        messagePanel.revalidate();
                        messagePanel.repaint();
                        // scroll to the bottom of the message panel
                        SwingUtilities.invokeLater(() -> messageScrollPane.getVerticalScrollBar().setValue(messageScrollPane.getVerticalScrollBar().getMaximum()));

                    }else {
                        JOptionPane.showMessageDialog(ClientChatGUI.this, "Please select a user to chat with");
                        inputField.setText("");
                        return;
                    }

                }
            }
        });
        inputField.setBackground(Utilities.SECONDARY_COLOR);
        inputField.setForeground(Utilities.Text_COLOR);
        inputField.setBorder(Utilities.addPadding(0, 10, 0 , 10));
        inputField.setFont(new Font("Inter", Font.PLAIN, 16));
        inputField.setPreferredSize(new Dimension(inputPanel.getWidth(), 50));
        return inputField;
    }


    private JPanel createChatMessageComponent(String from , String msgContent){
        JPanel messageComponent = new JPanel();
        messageComponent.setLayout(new BoxLayout(messageComponent, BoxLayout.Y_AXIS));
        messageComponent.setBackground(Utilities.SECONDARY_COLOR);
        messageComponent.setBorder(Utilities.addPadding(20, 20, 10, 20));

        JLabel sender = new JLabel(from);
        sender.setFont(new Font("Inter", Font.BOLD, 18));
        sender.setForeground(Utilities.Text_COLOR);
        messageComponent.add(sender);

        JLabel messageText = new JLabel();
        messageText.setText("<html>" +
                "<body style='width:" +(0.60 * getWidth()) + "'px>" +
                    msgContent +
                "</body>" +

                "</html>");

        messageText.setFont(new Font("Inter", Font.PLAIN, 18));
        messageText.setForeground(Utilities.Text_COLOR);
        // make the line split if it is too long
        messageText.setPreferredSize(new Dimension(1000, 50));
        messageText.setMaximumSize(new Dimension(1000, 50));

        JLabel timeLabel = new JLabel(new Date().toString());
        timeLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        timeLabel.setForeground(Utilities.Text_COLOR);
        messageComponent.add(messageText);
        messageComponent.add(timeLabel);


        return messageComponent;
    }


    private JPanel createChatMessageComponent(Message message) {
        JPanel messageComponent = new JPanel();
        messageComponent.setLayout(new BoxLayout(messageComponent, BoxLayout.Y_AXIS));
        messageComponent.setBackground(Utilities.SECONDARY_COLOR);
        messageComponent.setBorder(Utilities.addPadding(20, 20, 10, 20));

        JLabel sender = new JLabel(message.getSender().getUsername());
        sender.setFont(new Font("Inter", Font.BOLD, 18));
        sender.setForeground(Utilities.Text_COLOR);
        messageComponent.add(sender);

        JLabel messageText = new JLabel();
        messageText.setText("<html>" +
                "<body style='width:" + (0.60 * getWidth()) + "'px>" +
                message.getContent() +
                "</body>" +
                "</html>");

        messageText.setFont(new Font("Inter", Font.PLAIN, 18));
        messageText.setForeground(Utilities.Text_COLOR);
        // make the line split if it is too long
        messageText.setPreferredSize(new Dimension(1000, 50));
        messageText.setMaximumSize(new Dimension(1000, 50));

        JLabel timeLabel = new JLabel(message.getTimestamp().toString());
        timeLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        timeLabel.setForeground(Utilities.Text_COLOR);





        messageComponent.add(messageText);
        messageComponent.add(timeLabel);

        return messageComponent;

    }

}
