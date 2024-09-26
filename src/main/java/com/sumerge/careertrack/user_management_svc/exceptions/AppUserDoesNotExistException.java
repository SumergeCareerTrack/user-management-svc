package com.sumerge.careertrack.user_management_svc.exceptions;

public class AppUserDoesNotExistException extends UserManagementException {
    public AppUserDoesNotExistException(String message) {
        super(message);
    }

    public AppUserDoesNotExistException(String message, Object... args) {
        super(String.format(message, args));
    }
}
