// User.java
package org.example.models;



import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( unique = true, nullable = false)
    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            // make join columns unique
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}),
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;


    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isOnline;



    public User() {
        this.friends = new ArrayList<>();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friends = new ArrayList<>();
    }

    public void setActive(boolean active) {
        this.isOnline = active;
    }
    public boolean isActive() {
        return isOnline;
    }








    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    // toString method

    @Override
    public String toString() {
        StringBuilder User = new StringBuilder();
        User.append("User{id=").append(id);
        User.append(", username='").append(username).append('\'');
        User.append(", password='").append(password).append('\'');
        return User.toString();

    }

    public List<User> getFriends() {
        if (friends == null) {
            friends = new ArrayList<>();
        }
        return  friends;

    }
}