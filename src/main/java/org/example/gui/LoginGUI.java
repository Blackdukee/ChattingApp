package org.example.gui;

import org.example.controller.UserController;
import org.example.models.User;

import javax.swing.*;
import java.util.Arrays;

public class LoginGUI extends JFrame {
    private JPanel contentPane;
    private JTextField userName;
    private JButton loginButton;
    private JButton registerButton;

    public LoginGUI() {
        super("Login");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addGuiComponents();
    }

    private void addGuiComponents(){
        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(Utilities.addPadding(10, 10, 10, 10));
        contentPane.setBackground(Utilities.PRIMARYP_COLOR);

        JLabel userNameLabel = new JLabel("Enter your username");
        userNameLabel.setForeground(Utilities.Text_COLOR);
        userNameLabel.setFont(Utilities.FONT);
        contentPane.add(userNameLabel);



        userName = new JTextField();
        userName.setFont(Utilities.FONT);
        contentPane.add(userName);

        JLabel passwordLabel = new JLabel("Enter your password");
        passwordLabel.setForeground(Utilities.Text_COLOR);
        passwordLabel.setFont(Utilities.FONT);
        contentPane.add(passwordLabel);

        JPasswordField password = new JPasswordField();
        password.setFont(Utilities.FONT);
        contentPane.add(password);

        password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
                    Login(password);
                }
            }
        });

        loginButton = new JButton("Login");
        loginButton.setFont(Utilities.FONT);
        contentPane.add(loginButton);

        loginButton.addActionListener(e -> {
            // check if the user exists

            Login(password);

        });
        registerButton = new JButton("Register");
        registerButton.setFont(Utilities.FONT);
        contentPane.add(registerButton);



        add(contentPane);
    }

    private void Login(JPasswordField password) {
        String userName = this.userName.getText().trim();
        String passwordString = new String(password.getPassword());
        User user = UserController.Loing(userName, passwordString);


        if (user != null) {
            ClientChatGUI clientChatGUI = new ClientChatGUI(user);
            clientChatGUI.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(LoginGUI.this,
                    "Invalid username or password",
                    "Login error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // if the user does not exist, show an error message
    }


}
