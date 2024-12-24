package org.example.gui;

/**
 * MessageListener is an interface for handling message-related events
 * such as receiving new messages and changes in active users.
 */
public interface MessageListener {

    /**
     * Called when a new message is received.
     *
     * @param from    the sender of the message
     * @param message the content of the message
     */
    void onMessageReceived(String from, String message);

    /**
     * Called when the active status of a user changes.
     *
     * @param activeUsers the user whose status changed
     * @param isActive    the new active status
     */
    void onActiveUsersChanged(String activeUsers, boolean isActive);
}
