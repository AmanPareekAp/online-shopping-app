package com.online_shopping.token;

import com.online_shopping.entity.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ConfirmationToken {
    private static final int EXPIRATION_TIME = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true)
    private String token;
    private LocalDateTime createdDate;
    private LocalDateTime expiryDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public ConfirmationToken(){}

    public ConfirmationToken(User user) {
        this.user = user;
        this.createdDate = LocalDateTime.now();
        this.token = UUID.randomUUID().toString();
        expiryDate=createdDate.plusMinutes(15);
    }

    public static int getEXPIRATION() {
        return EXPIRATION_TIME;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static int getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
