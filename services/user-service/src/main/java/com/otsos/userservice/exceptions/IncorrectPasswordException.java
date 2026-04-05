package com.otsos.userservice.exceptions;

public class IncorrectPasswordException extends ApiException {
    public IncorrectPasswordException(String idOrEmail) {
        super("Incorrect password, id/email: " + idOrEmail, 400);
    }
}
