package com.sumerge.careertrack.user_management_svc.exceptions;

public class UserManagementException extends RuntimeException {
    public UserManagementException(String message) {
        super(message);
    }

    public UserManagementException(String message, Object... args) {
        super(String.format(message, args));
    }
}
