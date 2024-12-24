package org.example.gui;

import org.example.controller.UserController;
import org.example.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * LoginGUI is the main GUI class for the login screen of the chat application.
 * It handles the user interface for logging in and navigating to the signup screen.
 */
public class LoginGUI extends JFrame {
    private JTextField userName;
    private JPasswordField password;

    /**
     * Constructs a new LoginGUI.
     */
    public LoginGUI() {
        super("Login");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
        applyTheme();
    }

    /**
     * Initializes the GUI components for the login screen.
     */
    private void initComponents() {
        // Initialize fields
        userName = new JTextField(15);
        password = new JPasswordField(15);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(userName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(password, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        signupButton.addActionListener(e -> {
            dispose();
            new SingupGUI().setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);
    }

    /**
     * Applies the theme to the login screen.
     */
    private void applyTheme() {
        Utilities.applyTheme(getContentPane());
    }

    /**
     * Handles the login process by validating the username and password.
     */
    private void handleLogin() {
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
    }

}