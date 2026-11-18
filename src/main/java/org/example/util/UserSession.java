package org.example.util;

import org.example.entity.User;

public class UserSession {
    private static User loggedUser;

    public static void setLoggedUser(User user){
        loggedUser = user;
    }
    public static User getLoggedUser(){
        return loggedUser;
    }
    public static void clearSession(){
        loggedUser = null;
    }
}
