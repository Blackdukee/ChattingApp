package org.example.models;

import jakarta.persistence.*;

/**
 * FriendRequest represents a friend request between two users.
 * It contains information about the sender, recipient, and the status of the request.
 */
@Entity
@Table(name = "friend_requests")
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

    /**
     * Constructs a new FriendRequest with the specified sender and recipient.
     * The status is set to PENDING by default.
     *
     * @param sender    the user sending the friend request
     * @param recipient the user receiving the friend request
     */
    public FriendRequest(User sender, User recipient) {
        this.sender = sender;
        this.recipient = recipient;
        this.isAccepted = Status.PENDING;
    }

    /**
     * Default constructor for FriendRequest.
     */
    public FriendRequest() {
    }

    /**
     * Constructs a new FriendRequest with the specified sender, recipient, and status.
     *
     * @param sender      the user sending the friend request
     * @param recipient   the user receiving the friend request
     * @param isAccepted  the status of the friend request
     */
    public FriendRequest(User sender, User recipient, Status isAccepted) {
        this.sender = sender;
        this.recipient = recipient;
        this.isAccepted = isAccepted;
    }

    /**
     * Returns the ID of the friend request.
     *
     * @return the ID of the friend request
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the sender of the friend request.
     *
     * @return the sender of the friend request
     */
    public User getSender() {
        return sender;
    }

    /**
     * Sets the sender of the friend request.
     *
     * @param sender the sender of the friend request
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Returns the recipient of the friend request.
     *
     * @return the recipient of the friend request
     */
    public User getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient of the friend request.
     *
     * @param recipient the recipient of the friend request
     */
    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    /**
     * Sets the status of the friend request to REJECTED.
     */
    public void setRejected() {
        isAccepted = Status.REJECTED;
    }

    /**
     * Sets the status of the friend request to PENDING.
     */
    public void setPending() {
        isAccepted = Status.PENDING;
    }

    /**
     * Sets the status of the friend request to ACCEPTED.
     */
    public void setAccepted() {
        isAccepted = Status.ACCEPTED;
    }

    /**
     * Returns the status of the friend request.
     *
     * @return the status of the friend request
     */
    public Status getIsAccepted() {
        return isAccepted;
    }

    /**
     * Returns a string representation of the friend request.
     *
     * @return a string representation of the friend request
     */
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
