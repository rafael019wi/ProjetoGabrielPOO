package org.example.dto;

import org.example.entity.User;

public class LoginResult {
    private boolean sucess;
    private int status;
    private String message;
    private User user;

    public LoginResult (boolean sucess, int status, String message, User user) {
        this.sucess = sucess;
        this.status = status;
        this.message = message;
        this.user = user;
    }

    public boolean isSucess() {
        return sucess;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
