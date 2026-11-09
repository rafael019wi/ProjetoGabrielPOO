package org.example.util;

public class UserUtil {
    private static final String STATUS = "active";

    public static boolean validarStatus(String userStatus) {
        return userStatus.equals(STATUS);
    }

    public static boolean validarPasswords(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
