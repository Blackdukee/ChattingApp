// Message.java
package org.example.models;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Message represents a chat message between two users.
 * It contains information about the sender, recipient, content, timestamp, and read status of the message.
 */
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    private boolean isRead;

    /**
     * Constructs a new Message with the specified sender, recipient, and content.
     *
     * @param sender    the user sending the message
     * @param recipient the user receiving the message
     * @param content   the content of the message
     */
    public Message(User sender, User recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = new Date();
        this.isRead = false;
    }

    /**
     * Default constructor for Message.
     */
    public Message() {
    }

    /**
     * Returns the recipient of the message.
     *
     * @return the recipient of the message
     */
    public User getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient of the message.
     *
     * @param recipient the recipient of the message
     */
    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    /**
     * Returns the sender of the message.
     *
     * @return the sender of the message
     */
    public User getSender() {
        return sender;
    }

    /**
     * Sets the sender of the message.
     *
     * @param sender the sender of the message
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Returns the content of the message.
     *
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the message.
     *
     * @param content the content of the message
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the ID of the message.
     *
     * @return the ID of the message
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the timestamp of the message.
     *
     * @return the timestamp of the message
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the message.
     *
     * @param timestamp the timestamp of the message
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns whether the message has been read.
     *
     * @return true if the message has been read, false otherwise
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the read status of the message.
     *
     * @param isRead the read status of the message
     */
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * Returns a string representation of the message.
     *
     * @return a string representation of the message
     */
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                '}';
    }
}