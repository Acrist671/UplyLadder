package com.otsos.userservice.exceptions;

public class IncorrectPasswordException extends ApiException {
    public IncorrectPasswordException(String id) {
        super("Incorrect password, id: " + id, 401);
    }
}
