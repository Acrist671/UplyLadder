package com.otsos.userservice.exceptions;

public class UserAlreadyExistsException extends ApiException {
    public UserAlreadyExistsException(String idOrEmail) {
        super("User with id/email: " + idOrEmail + " already exists", 400);
    }
}
