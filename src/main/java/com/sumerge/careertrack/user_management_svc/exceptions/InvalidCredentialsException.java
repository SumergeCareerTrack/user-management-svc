package com.sumerge.careertrack.user_management_svc.exceptions;

public class InvalidCredentialsException extends UserManagementException {
    public static final String DEFAULT = "Invalid username or password.";

    public InvalidCredentialsException() {
        super(DEFAULT);
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Object... args) {
        super(String.format(message, args));
    }
}
