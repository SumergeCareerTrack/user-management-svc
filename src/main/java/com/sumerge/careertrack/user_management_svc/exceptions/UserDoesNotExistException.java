package com.sumerge.careertrack.user_management_svc.exceptions;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
    public UserDoesNotExistException(String message, Object... args) {
        super(String.format(message, args));
    }
}
