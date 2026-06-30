package com.otsos.userservice.exceptions;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String id) {
        super("user with id: " + id + " not found", 404);
    }
}
