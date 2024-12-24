package org.example.gui;

import javax.swing.*;

import org.example.controller.UserController;
import org.example.models.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

/**
 * SingupGUI is the main GUI class for the signup screen of the chat application.
 * It handles the user interface for creating a new user account.
 */
public class SingupGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signupButton;
    private JButton goBackButton;
    JPasswordField confirmPasswordField;

    /**
     * Constructs a new SingupGUI.
     */
    public SingupGUI() {
        setTitle("Signup");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        applyTheme();
    }

    /**
     * Initializes the GUI components for the signup screen.
     */
    private void initComponents() {
        // Initialize fields
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        confirmPasswordField = new JPasswordField(15);
        panel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;

        goBackButton = new JButton("Go Back");
        signupButton = new JButton("Signup");

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });

        goBackButton.addActionListener(e -> {
            dispose();
            new LoginGUI().setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(goBackButton);
        buttonPanel.add(signupButton);
        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);

    }

    /**
     * Applies the theme to the signup screen.
     */
    private void applyTheme() {
        Utilities.applyTheme(getContentPane());
    }

    /**
     * Handles the signup process by validating the input fields and creating a new user.
     */
    private void handleSignup() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        boolean usernameValid = Pattern.matches("^[a-zA-Z0-9]{5,}$", username);
        if (usernameValid == false) {
            usernameField.setText("");
            JOptionPane.showMessageDialog(this,
                    "Username must be at least 5 characters long and contain only letters and numbers", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!new String(password).equals(new String(confirmPassword))) {
            passwordField.setText("");
            confirmPasswordField.setText("");
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean passwordValid = Pattern.matches("^[a-zA-Z0-9!@#$%^&*()_]{5,}$", new String(password));
        if (passwordValid == false) {
            passwordField.setText("");
            confirmPasswordField.setText("");
            JOptionPane.showMessageDialog(this, "Password must be at least 5 characters long", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Username: " + username);
        System.out.println("Password: " + new String(password));
        User user = new User();
        user.setUsername(username);
        user.setPassword(new String(password));
        UserController.signup(user);
        JOptionPane.showMessageDialog(this, "User created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginGUI().setVisible(true);
    }
}
