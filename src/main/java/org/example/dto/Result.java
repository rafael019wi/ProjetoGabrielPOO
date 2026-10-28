package org.example.dto;

public class Result<Type> {
    private boolean success;
    private int status;
    private String message;
    private Type data;

    public Result(boolean success, int status, String message, Type data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Type getData() {
        return data;
    }
}