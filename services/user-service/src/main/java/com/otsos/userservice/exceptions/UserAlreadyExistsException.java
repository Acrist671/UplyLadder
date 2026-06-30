package com.otsos.userservice.exceptions;

public class UserAlreadyExistsException extends ApiException {
    public UserAlreadyExistsException(String email) {
        super("User with email: " + email + " already exists", 409);
    }
}
