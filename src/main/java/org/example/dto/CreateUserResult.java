package org.example.dto;

import org.example.entity.User;

public class CreateUserResult {
    private boolean sucess;
    private int status;
    private String message;
    private User user;

    public CreateUserResult(boolean sucess, int status, String message, User user) {
        this.sucess = sucess;
        this.status= status;
        this.message= message;
        this.user = user;
    }

    public boolean isSucess() {
        return sucess;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
