package org.example.gui;

public interface MessageListener {
    void onMessageReceived(String from, String message);
    void onActiveUsersChanged(String activeUsers ,boolean isActive);
}
