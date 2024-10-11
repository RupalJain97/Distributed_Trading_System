package com.trading.model;

import jakarta.persistence.*;

@Entity
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated id

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "userid", nullable = false, unique = true)
    private String userid;

    @Column(name = "password", nullable = false)
    private String password;

    // No-argument constructor (required by JPA)
    public UserModel() {
    }

    public UserModel(String username, String userid, String password) {
        this.username = username;
        this.userid = userid;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
