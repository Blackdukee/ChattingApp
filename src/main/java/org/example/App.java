package org.example;

import org.example.controller.UserController;
import org.example.gui.ClientChatGUI;
import org.example.gui.LoginGUI;
import org.example.models.Message;
import org.example.models.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                LoginGUI loginGUI = new LoginGUI();
                loginGUI.setVisible(true);
            }


        });






    }


}
