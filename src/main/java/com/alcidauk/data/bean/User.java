package com.alcidauk.data.bean;


import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by alcidauk on 20/08/16.
 */
@Entity
public class User {

    @Id
    private String username;

    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
