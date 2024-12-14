package org.example.models;

import jakarta.persistence.*;

enum Status {
    ACCEPTED, REJECTED, PENDING
}
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User recipient;
    @Enumerated(EnumType.STRING)
    private Status isAccepted;

    public FriendRequest(User sender, User recipient) {
        this.sender = sender;
        this.recipient = recipient;
        this.isAccepted = Status.PENDING;
    }

    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public void setRejected() {
        isAccepted = Status.REJECTED;
    }
    public void setPending() {
        isAccepted = Status.PENDING;
    }
    public void setAccepted() {
        isAccepted = Status.ACCEPTED;
    }
    public Status getIsAccepted() {
        return isAccepted;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "id=" + id +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", isAccepted=" + isAccepted +
                '}';
    }
}
