package com.otsos.userservice.exceptions;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String idOrEmail) {
        super("user with id/email: " + idOrEmail + " not found", 400);
    }
}
