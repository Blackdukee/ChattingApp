package org.example;

import org.example.gui.LoginGUI;

import javax.swing.*;

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
